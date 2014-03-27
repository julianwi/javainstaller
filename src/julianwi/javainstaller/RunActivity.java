package julianwi.javainstaller;

import jackpal.androidterm.emulatorview.EmulatorView;
import jackpal.androidterm.emulatorview.TermSession;
import jackpal.androidterm.emulatorview.UpdateCallback;
import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;

public class RunActivity extends Activity implements UpdateCallback {
	
	private EmulatorView emulatorview;
	public static TermSession session;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		if(session==null || savedInstanceState==null){
			//create a new terminal session
			session = new TermSession();
	        //create a pty
			ProcessBuilder execBuild;
			if(b != null && (Boolean)b.get("install")==true){
				execBuild = new ProcessBuilder("/data/data/jackpal.androidterm/java/execpty", "/system/bin/sh", "/data/data/julianwi.javainstaller/install.sh");
			}
			else{
				String javapath = getSharedPreferences("settings", 1).getString("path3", "");
				execBuild = new ProcessBuilder("/data/data/jackpal.androidterm/java/execpty", javapath+"/java", "-jar", getIntent().getDataString());
			}
	        execBuild.redirectErrorStream(true);
	        Process exec = null;
	        try {
	            exec = execBuild.start();
	        } catch (Exception e) {
	            e.printStackTrace();
	            new Error("error", e.toString(), this);
	        }
	        //connect the pty's I/O streams to the TermSession.
	        session.setTermIn(exec.getInputStream());
	        session.setTermOut(exec.getOutputStream());
		}
		//create the EmulatorView
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		emulatorview = new EmulatorView(this, session, metrics);
		emulatorview.setOnKeyListener(mKeyListener);
		setContentView(emulatorview);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add("settings");
		return true;
	}

	@Override
	public void onUpdate() {
		// TODO Auto-generated method stub
		
	}
	
	private View.OnKeyListener mKeyListener = new View.OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			return false;
		}
	};

}
