package julianwi.javainstaller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class InstallActivity extends Activity implements Runnable {
	
	private int pkgs;
	private Handler handler = new Handler();
	//public static String[] tmp = new String[]{"terminal.apk", "busybox", "libc.tar.gz", "zlib.tar.gz", "libffi.tar.gz", "jamvm.tar.gz", "classpath.tar.gz", "freetype.tar.gz", "awt.tar.gz"};
	private List<LinearLayout> lls;
	private List<Integer> ids;
	private List<Integer> ids2;
	private int term = 0;
	private InstallList il;
	private boolean uninstall = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		pkgs = b.getInt("packages");
		lls = new ArrayList<LinearLayout>();
		ids = new ArrayList<Integer>();
		ids2 = new ArrayList<Integer>();
		ListView lv = new ListView(this);
		il = new InstallList(lls);
		lv.setAdapter(il);
		setContentView(lv);
		init();
		new Thread(this).start();
	}
	
	public LinearLayout makell(int id){
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		TextView tv = new TextView(this);
		tv.setText(((uninstall)?"uninstalling ":"installing ")+MainActivity.checks[id].text);
		ll.addView(tv);
		if(!uninstall){
			ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
			pb.setId(1);
			pb.setProgress(0);
			pb.setMax(100);
			ll.addView(pb);
			TextView tv1 = new TextView(this);
			tv1.setId(2);
			tv1.setText("0/0kb  0/100%");
			ll.addView(tv1);
		}
		return ll;
	}

	@Override
	public void run() {
		try {
			if((3&pkgs)==2 || (!MainActivity.checks[0].installed && pkgs >> 2 != 0)){
				new Download((ProgressBar)lls.get(0).findViewById(1), (TextView)lls.get(0).findViewById(2), new URL(MainActivity.checks[0].getSource()), handler, "/data/data/julianwi.javainstaller/"+Checkforfile.file[0], this).run();
				chmod(new File("/data/data/julianwi.javainstaller/androidterm.apk"), 0644);
				Intent intent = new Intent(Intent.ACTION_VIEW);
			    intent.setDataAndType(Uri.fromFile(new File("/data/data/julianwi.javainstaller/androidterm.apk")), "application/vnd.android.package-archive");
			    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    this.startActivity(intent);
			    if(checkCallingOrSelfPermission("jackpal.androidterm.permission.RUN_SCRIPT")!=PackageManager.PERMISSION_GRANTED)return;
			}
			if(ids.size()!=0){
				Writer writer;
				if(testrunactivity(1)){
					writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/data/data/julianwi.javainstaller/install.sh"), "utf-8"));
				}else{
					writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/data/data/julianwi.javainstaller/install2.sh"), "utf-8"));
				}
				writer.write("#!/system/bin/sh\nbbdir="+MainActivity.checks[1].getPath()+"/busybox\n");
				int i = -1;
				for(int id : ids){
					i++;
					CheckPoint mcheck = MainActivity.checks[id];
					if(!uninstall){
						new Download((ProgressBar)lls.get(term+i).findViewById(1), (TextView)lls.get(term+i).findViewById(2), new URL(MainActivity.checks[id].getSource()), handler, "/data/data/julianwi.javainstaller/"+Checkforfile.file[id], this).run();
					}
					writesh(writer, mcheck);
				}
				writer.write("echo installation complete\n");
				writer.write("exit");
				writer.close();
				runterm(testrunactivity(ids.get(0)));
			}
			
			if(ids2.size()!=0){
				Writer writer;
				if(!testrunactivity(1)){
					writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/data/data/julianwi.javainstaller/install.sh"), "utf-8"));
				}else{
					writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/data/data/julianwi.javainstaller/install2.sh"), "utf-8"));
				}
				writer.write("#!/system/bin/sh\nbbdir="+MainActivity.checks[1].getPath()+"/busybox\n");
				int i = -1;
				for(int id : ids2){
					i++;
					CheckPoint mcheck = MainActivity.checks[id];
					if(!uninstall){
						new Download((ProgressBar)lls.get(term+ids.size()+i).findViewById(1), (TextView)lls.get(term+ids.size()+i).findViewById(2), new URL(MainActivity.checks[id].getSource()), handler, "/data/data/julianwi.javainstaller/"+Checkforfile.file[id], this).run();
					}
					writesh(writer, mcheck);
				}
				writer.write((uninstall)?"echo uninstallation complete\n":"echo installation complete\n");
				writer.write("exit");
				writer.close();
				runterm(testrunactivity(ids2.get(0)));
			}
			if((3&pkgs)==3){
				Uri packageURI = Uri.parse("package:jackpal.androidterm");
				Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
				this.startActivity(uninstallIntent);
			}
			writejava();
			//URL url = new URL(MainActivity.checks[0].getSource());
			//new Download((ProgressBar)lls.get(0).findViewById(1), (TextView)lls.get(0).findViewById(2), url, handler, "/data/data/julianwi.javainstaller/"+tmp[0]).run();
		} catch (final Exception e) {
			handler.post(new Error(e, this));
			e.printStackTrace();
		}
	}
	
	public Boolean testrunactivity(int id){
		String runmode = MainActivity.context.getSharedPreferences("julianwi.javainstaller_preferences", 1).getString("runmode", "auto");
		return(runmode.equals("Run Activity") || (runmode.equals("auto")&&MainActivity.checks[id].getPath().startsWith("/data/data/julianwi.javainstaller")));
	}
	
	public static int chmod(File path, int mode) throws Exception {
		Class fileUtils = Class.forName("android.os.FileUtils");
	    Method setPermissions = fileUtils.getMethod("setPermissions", String.class, int.class, int.class, int.class);
	    return (Integer) setPermissions.invoke(null, path.getAbsolutePath(), mode, -1, -1);
	}
	
	public void writesh(Writer writer, CheckPoint mcheck) throws Exception{
		if(uninstall){
			for(String file : Checkforfile.files[mcheck.id]){
				writer.write("rm "+mcheck.getPath()+"/"+file+"\n");
			}
			if(mcheck.id == 8){
				writer.write("am start --user 0 -a android.intent.action.DELETE -d package:julianwi.awtpeer\n");
			}
			return;
		}
		if(mcheck.id == 1){
			chmod(new File("/data/data/julianwi.javainstaller/busybox"), 0755);
		}
		else{
			chmod(new File("/data/data/julianwi.javainstaller/"+Checkforfile.file[mcheck.id]), 0644);
		}
		writer.write("echo installing "+mcheck.text+"\n");
		if(mcheck.id == 1){
			writer.write("/data/data/julianwi.javainstaller/busybox mkdir -p "+mcheck.getPath()+"\n");
			writer.write("/data/data/julianwi.javainstaller/busybox chmod 0755 "+mcheck.getPath()+"\n");
			writer.write("/data/data/julianwi.javainstaller/busybox cp /data/data/julianwi.javainstaller/busybox "+mcheck.getPath()+"/busybox\n");
			writer.write("/data/data/julianwi.javainstaller/busybox chmod 0755 "+mcheck.getPath()+"/busybox\n");
		}
		else{
			writer.write("$bbdir mkdir -p "+mcheck.getPath()+"\n$bbdir chmod 0755 "+mcheck.getPath()+"\ncd "+mcheck.getPath()+"\n");
			writer.write("$bbdir tar -xvzpf /data/data/julianwi.javainstaller/"+Checkforfile.file[mcheck.id]+"\n");
			writer.write("$bbdir chmod 0755 *\n");
		}
		if(mcheck.id == 8){
			writer.write("am start --user 0 -a android.intent.action.VIEW -d file://"+mcheck.getPath()+"/awtonandroid.apk -t application/vnd.android.package-archive\n");
		}
		/*if(mcheck.id == 3 || mcheck.id == 4){
			writer.write("echo \"#!/system/bin/sh\" > "+MainActivity.checks[3].getPath()+"/java\n"
					   + "echo \"export LD_LIBRARY_PATH="+MainActivity.checks[3].getPath()+"/lib:"+MainActivity.checks[2].getPath()+"\" >> "+MainActivity.checks[3].getPath()+"/java\n");
			if(MainActivity.checks[4].installed || mcheck.id == 4){
				writer.write("echo \"exec "+MainActivity.checks[3].getPath()+"/jamvm -Xbootclasspath:"+MainActivity.checks[3].getPath()+"/lib/classes.zip:"+MainActivity.checks[3].getPath()+"/lib/glibj.zip:"+MainActivity.checks[3].getPath()+"/lib/awtpeer.zip -Dawt.toolkit=julianwi.awtpeer.AndroidToolkit \\$@\" >> "+MainActivity.checks[3].getPath()+"/java\n");
			}
			else{
				writer.write("echo \"exec "+MainActivity.checks[3].getPath()+"/jamvm -Xbootclasspath:"+MainActivity.checks[3].getPath()+"/lib/classes.zip:"+MainActivity.checks[3].getPath()+"/lib/glibj.zip \\$@\" >> "+MainActivity.checks[3].getPath()+"/java\n");
			}
			writer.write("$bbdir chmod 0755 "+MainActivity.checks[3].getPath()+"/java\n");
			writer.write("$bbdir chmod 0755 "+MainActivity.checks[3].getPath()+"/lib/*\n");
		}*/
	}
	
	public void runterm(Boolean runactivity) throws Exception {
		if(runactivity){
			chmod(new File("/data/data/julianwi.javainstaller/install.sh"), 0755);
			Intent intent = new Intent(MainActivity.context, RunActivity.class);
			Bundle b = new Bundle();
			b.putBoolean("install", true);
			intent.putExtras(b);
			this.startActivity(intent);
		}
		else{
			chmod(new File("/data/data/julianwi.javainstaller/install2.sh"), 0755);
			Intent intent = new Intent("jackpal.androidterm.RUN_SCRIPT");
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			if(MainActivity.context.getSharedPreferences("julianwi.javainstaller_preferences", 1).getString("rootmode", "off").equals("on")){
				intent.putExtra("jackpal.androidterm.iInitialCommand", "su\nsh /data/data/julianwi.javainstaller/install2.sh");
			}
			else{
				intent.putExtra("jackpal.androidterm.iInitialCommand", "sh /data/data/julianwi.javainstaller/install2.sh");
			}
			this.startActivity(intent);
		}
	}
	
	public void writejava() throws Exception{
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/data/data/julianwi.javainstaller/java"), "utf-8"));
		writer.write("#!/system/bin/sh\n");
		writer.write("export LD_LIBRARY_PATH="+MainActivity.checks[2].getPath());
		for (int id : new int[]{3,4,6,7,8}) {
			if(MainActivity.checks[id].installed && !MainActivity.checks[id].getPath().equals(MainActivity.checks[2].getPath())){
				writer.write(":"+MainActivity.checks[id].getPath());
			}
		}
		writer.write("\nexec "+MainActivity.checks[2].getPath()+"/ld-linux.so.* "+MainActivity.checks[5].getPath()+"/jamvm -Xbootclasspath:"+MainActivity.checks[5].getPath()+"/classes.zip:"+MainActivity.checks[6].getPath()+"/glibj.zip:"+MainActivity.checks[8].getPath()+"/awtpeer.zip -Dawt.toolkit=julianwi.awtpeer.AndroidToolkit $@\n");
		writer.close();
		chmod(new File("/data/data/julianwi.javainstaller/java"), 0755);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(checkCallingOrSelfPermission("jackpal.androidterm.permission.RUN_SCRIPT")!=PackageManager.PERMISSION_GRANTED){
			try {
				getPackageManager().getPackageInfo("jackpal.androidterm", PackageManager.GET_ACTIVITIES);
				Intent intent = new Intent(Intent.ACTION_VIEW);
			    intent.setDataAndType(Uri.fromFile(new File(getPackageCodePath())), "application/vnd.android.package-archive");
			    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    startActivity(intent);
			} catch (NameNotFoundException e) {
			}
		}
	}
	
	public void init(){
		if((3&pkgs)==2 || (!MainActivity.checks[0].installed && pkgs >> 2 != 0)){
			lls.add(makell(0));
			term=1;
		}
		uninstall = (pkgs & 1)==1;
		if((4&pkgs)==4 || (!MainActivity.checks[1].installed && pkgs >> 3 != 0 && !uninstall)){
			lls.add(makell(1));
			ids.add(1);
		}
		for (int i = 2; i < MainActivity.checks.length; i++) {
			if(((2<<i)&pkgs)==(2<<i)&&testrunactivity(i)==testrunactivity(1)){
				lls.add(makell(i));
				ids.add(i);
			}
		}
		for (int i = 2; i < MainActivity.checks.length; i++) {
			if(((2<<i)&pkgs)==(2<<i)&&testrunactivity(i)!=testrunactivity(1)){
				lls.add(makell(i));
				ids2.add(i);
			}
		}
		if((3&pkgs)==3){
			lls.add(makell(0));
		}
		il.notifyDataSetChanged();
	}

}
