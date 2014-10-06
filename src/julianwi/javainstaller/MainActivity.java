package julianwi.javainstaller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class MainActivity extends Activity implements OnSharedPreferenceChangeListener {
	
	public static CheckPoint[] checks = new CheckPoint[]{
			new CheckPoint("Android Terminal Emulator","A VT-100 terminal emulator for the Android OS" ,0),
			new CheckPoint("busybox","BusyBox combines tiny versions of many common UNIX utilities into a single small executable" ,1),
			new CheckPoint("gnu libc","The GNU C Library is used as the C library in the GNU systems and most systems with the Linux kernel" ,2),
			new CheckPoint("zlib", "A Massively Spiffy Yet Delicately Unobtrusive Compression Library", 3),
			new CheckPoint("libffi", "A portable foreign-function interface library", 4),
			new CheckPoint("jamvm","JamVM is a new Java Virtual Machine conforming to the JVM specification edition 2 (blue book)" ,5),
			new CheckPoint("gnu classpath","free implementation of the standard class library for the Java programming language" ,6),
			new CheckPoint("freetype", "FreeType is a freely available software library to render fonts", 7),
			new CheckPoint("awtonandroid graphic libraries","java awt graphic libraries for android" ,8)
	};
	//private String[] checklist = new String[]{"install busybox", "install Terminal Emulator", "install gnu libc", "install precompiled versions of jamvm and gnu classpath"};
	//private Boolean[] checklist2 = new Boolean[]{false, false, false, false};
	//private String[] checklist3 = new String[]{"/data/data/jackpal.androidterm/bin/busybox", "/data/app/jackpal.androidterm-1.apk", "", ""};
	private ListView lv;
	public ListView lv2;
	public ChecklistAdapter listenAdapter;
	public static SharedPreferences sharedP;
	public static Context context;
	public static Context termcontext;
	//public OnClickListener onclick = new Install(this ,checklist);
	public static MainActivity ma;
	public int state = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		try {
			termcontext = createPackageContext("jackpal.androidterm", CONTEXT_IGNORE_SECURITY);
		} catch (NameNotFoundException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		sharedP = getSharedPreferences("settings", 1);
		sharedP.registerOnSharedPreferenceChangeListener(this);
		//Checkforfile cff = new Checkforfile();
		//cff.scan(checks);
		lv = new ListView(this);
        //listenAdapter = new ChecklistAdapter(this, checks);
        //lv.setAdapter(listenAdapter);
		lv.setAdapter(new MainList(this));
        setContentView(lv);
        ma = this;
	}
	
	@Override
	protected void onResume() {
		Checkforfile cff = new Checkforfile();
		cff.scan(checks);
		listenAdapter.notifyDataSetChanged();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//menu.add("settings");
		menu.add(0, 0, 0, "settings");
		menu.add(0, 1, 0, "check for updates");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case 0:
	        	Intent intent = new Intent(MainActivity.context, SettingsActivity.class);
			    MainActivity.context.startActivity(intent);
	            return true;
	        case 1:
	        	Thread update = new Update(listenAdapter, this);
	        	update.start();
	     }
		return false;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Checkforfile cff = new Checkforfile();
		cff.scan(checks);
		listenAdapter.notifyDataSetChanged();
	}
	
	public void choosefile(String type){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        startActivityForResult(intent,0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println("result"+requestCode);
		switch(requestCode){
		case 0:
			if(resultCode==RESULT_OK){
				String FilePath = data.getData().getPath();
				if(getSharedPreferences("julianwi.javainstaller_preferences", 1).getString("runmode2", "Run Activity").equals("Run Activity")){
					Intent intent = new Intent(MainActivity.context, RunActivity.class);
					intent.setDataAndType(Uri.parse(FilePath), "application/java-archive");
				    MainActivity.context.startActivity(intent);
				}
				else{
					String javapath = sharedP.getString("path3", "");
					Intent i = new Intent("jackpal.androidterm.RUN_SCRIPT");
					i.addCategory(Intent.CATEGORY_DEFAULT);
					if(MainActivity.context.getSharedPreferences("julianwi.javainstaller_preferences", 1).getString("rootmode2", "off").equals("on")){
						i.putExtra("jackpal.androidterm.iInitialCommand", "su\n"+javapath+"/java -jar "+FilePath);
					}
					else{
						i.putExtra("jackpal.androidterm.iInitialCommand", javapath+"/java -jar "+FilePath);
					}
					startActivity(i);
				}
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	    	if(state == 1){
				setContentView(lv);
	    	}else if(state == 2){
	    		setContentView(lv2);
	    	}else{
	    		return super.onKeyDown(keyCode, event);
	    	}
	    	state = state-1;
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

}
