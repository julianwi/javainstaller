package julianwi.javainstaller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class MainActivity extends Activity implements OnSharedPreferenceChangeListener {
	
	private ListView lv;
	public static CheckPoint[] checks = new CheckPoint[]{
			new CheckPoint("install Terminal Emulator","http://borcteam.bplaced.net/Daten/java/jackpal.androidterm.apk" ,0),
			new CheckPoint("install busybox","http://borcteam.bplaced.net/Daten/java/busybox" ,1),
			new CheckPoint("install gnu libc","http://borcteam.bplaced.net/Daten/java/libc.tar.gz" ,2),
			new CheckPoint("install precompiled versions of jamvm and gnu classpath","http://borcteam.bplaced.net/Daten/java/java.tar.gz" ,3)
	};
	//private String[] checklist = new String[]{"install busybox", "install Terminal Emulator", "install gnu libc", "install precompiled versions of jamvm and gnu classpath"};
	//private Boolean[] checklist2 = new Boolean[]{false, false, false, false};
	//private String[] checklist3 = new String[]{"/data/data/jackpal.androidterm/bin/busybox", "/data/app/jackpal.androidterm-1.apk", "", ""};
	private ChecklistAdapter listenAdapter;
	public static SharedPreferences sharedP;
	public static Context context;
	public static Context termcontext;
	//public OnClickListener onclick = new Install(this ,checklist);
	public static MainActivity ma;
	
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
        listenAdapter = new ChecklistAdapter(this, checks);
        lv.setAdapter(listenAdapter);
        setContentView(lv);
        ma = this;
        
        File binDir = new File("/data/data/julianwi.javainstaller/bin");
        if (!binDir.exists()) {
            try {
                binDir.mkdir();
                Install.chmod(binDir, 0755);
            } catch (Exception e) {
            	e.printStackTrace();
            	new Error("error", e.toString());
            }
        }
        File binary = new File(binDir, "execpty");
        try {
            InputStream src = getAssets().open("execpty");
            FileOutputStream dst = new FileOutputStream(binary);
            byte[] buffer = new byte[4096];
            int bytesRead = 0;
            while ((bytesRead = src.read(buffer)) >= 0) {
                dst.write(buffer, 0, bytesRead);
            }
            dst.close();
            Install.chmod(binary, 0755);
        } catch (Exception e) {
        	e.printStackTrace();
        	new Error("error", e.toString());
        }
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
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case 0:
	        	Intent intent = new Intent(MainActivity.context, SettingsActivity.class);
			    MainActivity.context.startActivity(intent);
	            return true;
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
				Intent intent = new Intent(MainActivity.context, RunActivity.class);
				intent.setDataAndType(Uri.parse(FilePath), "application/java-archive");
			    MainActivity.context.startActivity(intent);
			}
		}
	}

}
