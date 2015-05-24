package julianwi.javainstaller;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class RunActivity extends Activity implements Runnable {
	
	private View emulatorview;
	public static Object session, exec;
    private static ParcelFileDescriptor pseudoterm;
    public ClassLoader classloader;
	private Handler handler;
	private static Class<?> termexec;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			System.getProperties().setProperty("java.library.path", System.getProperty("java.library.path")+":/data/data/jackpal.androidterm/lib");
			
			String apkfile = getPackageManager().getApplicationInfo("jackpal.androidterm", 0).sourceDir;
			classloader = new dalvik.system.PathClassLoader(apkfile, ClassLoader.getSystemClassLoader());
			
			Bundle b = getIntent().getExtras();
			//create a new terminal session
			session = Class.forName("jackpal.androidterm.emulatorview.TermSession", true, classloader).getConstructor().newInstance(new Object[]{});
			SharedPreferences settings = getSharedPreferences("julianwi.javainstaller_preferences", 1);
			String[] arguments;
			//android terminal emulator version 1.0.66 and later wants the first argument twice
			if(b != null && (Boolean)b.get("install")==true){
				if(settings.getString("rootmode", "off").equals("on")){
					arguments = new String[]{"/system/bin/su", "/system/bin/su", "-c", "sh /data/data/julianwi.javainstaller/install.sh"};
				}
				else{
					arguments = new String[]{"/system/bin/sh", "/system/bin/sh", "/data/data/julianwi.javainstaller/install.sh"};
				}
			}
			else{
				String javapath = getSharedPreferences("settings", 1).getString("path3", "");
				if(settings.getString("rootmode2", "off").equals("on")){
					arguments = new String[]{"/system/bin/su", "/system/bin/su", "-c", "/data/data/julianwi.javainstaller/java -jar "+getIntent().getDataString()};
				}
				else{
					arguments = new String[]{"/data/data/julianwi.javainstaller/java", "/data/data/julianwi.javainstaller/java", "-jar", getIntent().getDataString()};
				}
			}
			//create a pty
			if(termexec == null){
				termexec = Class.forName("jackpal.androidterm.TermExec", true, classloader);
			}
			pseudoterm = ParcelFileDescriptor.open(new File("/dev/ptmx"), ParcelFileDescriptor.MODE_READ_WRITE);
			exec = termexec.getConstructor(new Class[]{String[].class}).newInstance(new Object[]{arguments});
			handler = new Handler();
			new Thread(this).start();

	        //connect the pty's I/O streams to the TermSession.
	        session.getClass().getMethod("setTermOut", OutputStream.class).invoke(session, new Object[]{new ParcelFileDescriptor.AutoCloseOutputStream(pseudoterm)});
	        session.getClass().getMethod("setTermIn", InputStream.class).invoke(session, new Object[]{new ParcelFileDescriptor.AutoCloseInputStream(pseudoterm)});
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
		menu.add(0,0,0,"settings");
		menu.add(0,1,0,"show softkeyboard");
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
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case 0:
	        	Intent intent = new Intent(MainActivity.context, SettingsActivity.class);
			    MainActivity.context.startActivity(intent);
	            return true;
	        case 1:
	        	Object keyboard = getSystemService(Context.INPUT_METHOD_SERVICE);
			try {
				Method showSoftInput = Thread.currentThread().getContextClassLoader().loadClass("android.view.inputmethod.InputMethodManager").getMethod("showSoftInput", new Class[]{View.class, int.class});
				showSoftInput.invoke(keyboard, new Object[]{emulatorview, 0});
			} catch (Exception e) {
				e.printStackTrace();
				new Error("error", e.toString(), this);
			}
	     }
		return false;
	}

	@Override
	public void run() {
		int pid;
		try {
			pid = (Integer) termexec.getMethod("start", new Class[]{ParcelFileDescriptor.class}).invoke(exec, new Object[]{pseudoterm});
			System.out.println("process id is "+pid);
		} catch (Exception e) {
			e.printStackTrace();
			handler.post(new Error(e, this));
		}
	}

}
