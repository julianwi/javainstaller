package julianwi.javainstaller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;

public class Install implements OnClickListener, Runnable, OnCancelListener{
	
	public ProgressDialog mProgressDialog;
	private CheckPoint mcheck;
	private Handler handler = new Handler();
	private URL url;
	private String[] tmp = new String[]{"terminal.apk", "busybox", "libc.tar.gz", "java.tar.gz"};
	public static String[] arm = new String[]{null, "http://borcteam.bplaced.net/Daten/java/arm/busybox"};
	
	public Install(CheckPoint check){
		mcheck = check;
		showalert(0, mcheck.text);
	}
	
	public void showalert(int id, String title) {
		AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.context);

		alert.setTitle(title);
		alert.setMessage("source file");

		// Set an EditText view to get user input 
		final EditText input = new EditText(MainActivity.context);
		input.setId(0);
		input.setText(mcheck.source);
		alert.setView(input);

		alert.setPositiveButton("install", this);

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		mProgressDialog = new ProgressDialog(MainActivity.context);
		mProgressDialog.setTitle("Downloading Image ...");
		mProgressDialog.setMessage("Download in progress ...");
		mProgressDialog.setProgressStyle(mProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setProgress(0);
		mProgressDialog.setMax(100);
		mProgressDialog.show();

		// execute this when the downloader must be fired
		try {
			String source = ((EditText) ((AlertDialog)dialog).findViewById(0)).getText().toString();
			url = new URL(source);
			Thread thread = new Thread(this);
			thread.start();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			new Error("error", e.getMessage());
		}
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		try{
			new Download(mProgressDialog, url, handler, "/data/data/julianwi.javainstaller/"+tmp[mcheck.id]).start();
			if(mcheck.id == 0){
				chmod(new File("/data/data/julianwi.javainstaller/terminal.apk"), 0644);
				Intent intent = new Intent(Intent.ACTION_VIEW);
			    intent.setDataAndType(Uri.fromFile(new File("/data/data/julianwi.javainstaller/terminal.apk")), "application/vnd.android.package-archive");
			    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    MainActivity.context.startActivity(intent);
			}
			else{
				if(mcheck.id == 1){
					chmod(new File("/data/data/julianwi.javainstaller/busybox"), 0755);
				}
				else{
					chmod(new File("/data/data/julianwi.javainstaller/"+tmp[mcheck.id]), 0644);
				}
				Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/data/data/julianwi.javainstaller/install.sh"), "utf-8"));
				writer.write("#!/system/bin/sh\nbbdir="+MainActivity.checks[1].getPath()+"\n");
				if(mcheck.id == 1){
					writer.write("/data/data/julianwi.javainstaller/busybox mkdir -p /data/data/jackpal.androidterm/bin/\n");
					writer.write("/data/data/julianwi.javainstaller/busybox chmod 0755 /data/data/jackpal.androidterm/bin\n");
					writer.write("/data/data/julianwi.javainstaller/busybox cp /data/data/julianwi.javainstaller/busybox /data/data/jackpal.androidterm/bin/busybox\n");
					writer.write("/data/data/julianwi.javainstaller/busybox chmod 0755 /data/data/jackpal.androidterm/bin/busybox\n");
				}
				else if(mcheck.id == 2 || mcheck.id == 3){
					writer.write("$bbdir mkdir -p "+mcheck.getPath()+"\n$bbdir chmod 0755 "+mcheck.getPath()+"\ncd "+mcheck.getPath()+"\n");
					writer.write("$bbdir tar -xvzpf /data/data/julianwi.javainstaller/"+tmp[mcheck.id]+"\n");
					writer.write("$bbdir chmod 0755 *\n");
				}
				if(mcheck.id == 3){
					writer.write("echo \"#!/system/bin/sh\" > "+mcheck.getPath()+"/java\n"
							   + "echo \"export LD_LIBRARY_PATH="+mcheck.getPath()+"/lib:"+MainActivity.checks[2].getPath()+"\" >> "+mcheck.getPath()+"/java\n"
							   + "echo \""+mcheck.getPath()+"/jamvm -Xbootclasspath:"+mcheck.getPath()+"/lib/classes.zip:"+mcheck.getPath()+"/lib/glibj.zip \\$@\" >> "+mcheck.getPath()+"/java\n");
					writer.write("$bbdir chmod 0755 "+mcheck.getPath()+"/lib/*\n");
				}
				writer.write("echo installation complete\n");
				writer.write("exit");
				writer.close();
				chmod(new File("/data/data/julianwi.javainstaller/install.sh"), 0755);
				String runmode = MainActivity.context.getSharedPreferences("julianwi.javainstaller_preferences", 1).getString("runmode", "auto");
				if(runmode=="Run Activity" || (runmode=="auto"&&mcheck.getPath().startsWith("/data/data/julianwi.javainstaller"))){
					try{
					Intent intent = new Intent(MainActivity.context, RunActivity.class);
					Bundle b = new Bundle();
					b.putBoolean("install", true);
					intent.putExtras(b);
					MainActivity.context.startActivity(intent);
					} catch(Exception e){
						e.printStackTrace();
						new Error("error", e.toString());
					}
				}
				else{
					Intent i = new Intent("jackpal.androidterm.RUN_SCRIPT");
					i.addCategory(Intent.CATEGORY_DEFAULT);
					i.putExtra("jackpal.androidterm.iInitialCommand", "sh /data/data/julianwi.javainstaller/install.sh\n$bbdir sleep 5\nexit");
					MainActivity.context.startActivity(i);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			final String error = e.toString();
        	handler.post(new Runnable() {
				
				@Override
				public void run() {
					new Error("Error", error);
				}
			});
		}
	}
	
	public static int chmod(File path, int mode) throws Exception {
	    Class fileUtils = Class.forName("android.os.FileUtils");
	    Method setPermissions = fileUtils.getMethod("setPermissions", String.class, int.class, int.class, int.class);
	    return (Integer) setPermissions.invoke(null, path.getAbsolutePath(), mode, -1, -1);
	}
	
	public static String getArch() {
        // Returns the value of uname -m
        String machine = System.getProperty("os.arch");
        // Convert machine name to arch identifier
        if (machine.matches("armv[0-9]+(tej?)?l")) {
            return "arm";
        } else {
        	return "x86";
        }
    }

}
