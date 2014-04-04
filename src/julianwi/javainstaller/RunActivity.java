package julianwi.javainstaller;

import java.io.InputStream;
import java.io.OutputStream;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;

public class RunActivity extends Activity {
	
	private View emulatorview;
	public static Object session;
    private static Process exec;
    public ClassLoader classloader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			String apkfile = getPackageManager().getApplicationInfo("jackpal.androidterm", 0).sourceDir;
			classloader = new dalvik.system.PathClassLoader(apkfile, ClassLoader.getSystemClassLoader());
			Bundle b = getIntent().getExtras();
			//create a new terminal session
			session = Class.forName("jackpal.androidterm.emulatorview.TermSession", true, classloader).getConstructor().newInstance(new Object[]{});
			//create a pty
			ProcessBuilder execBuild;
			if(b != null && (Boolean)b.get("install")==true){
				execBuild = new ProcessBuilder("/data/data/julianwi.javainstaller/bin/execpty", "/system/bin/sh", "/data/data/julianwi.javainstaller/install.sh");
			}
			else{
				String javapath = getSharedPreferences("settings", 1).getString("path3", "");
				execBuild = new ProcessBuilder("/data/data/julianwi.javainstaller/bin/execpty", javapath+"/java", "-jar", getIntent().getDataString());
			}
	        execBuild.redirectErrorStream(true);
	        exec = execBuild.start();
	        //connect the pty's I/O streams to the TermSession.
	        session.getClass().getMethod("setTermIn", InputStream.class).invoke(session, new Object[]{exec.getInputStream()});
	        session.getClass().getMethod("setTermOut", OutputStream.class).invoke(session, new Object[]{exec.getOutputStream()});
	        //create the EmulatorView
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			emulatorview = (View) Class.forName("jackpal.androidterm.emulatorview.EmulatorView", true, classloader).getConstructor(Context.class, session.getClass(), DisplayMetrics.class).newInstance(new Object[]{this, session, metrics});
			setContentView(emulatorview);
		}catch(Exception e){
			e.printStackTrace();
			new Error("error", e.toString(), this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add("settings");
		return true;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    try {
	    	DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			emulatorview = (View) Class.forName("jackpal.androidterm.emulatorview.EmulatorView", true, classloader).getConstructor(Context.class, session.getClass(), DisplayMetrics.class).newInstance(new Object[]{this, session, metrics});
			setContentView(emulatorview);
		} catch (Exception e) {
			e.printStackTrace();
			new Error("error", e.toString(), this);
		}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		try{
			exec.destroy();
		} catch(Exception e){
			e.printStackTrace();
			new Error("error", e.toString(), this);
		}
	}

}