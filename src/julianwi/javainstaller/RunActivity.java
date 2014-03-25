package julianwi.javainstaller;

import jackpal.androidterm.emulatorview.EmulatorView;
import jackpal.androidterm.emulatorview.TermSession;
import jackpal.androidterm.emulatorview.UpdateCallback;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class RunActivity extends Activity implements UpdateCallback {
	
	private EmulatorView emulatorview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		//create a new terminal session
		TermSession session = new TermSession();

        /* ... create a process ... */
        ProcessBuilder execBuild = new ProcessBuilder("/data/data/jackpal.androidterm/java/java", "-jar", "/sdcard/Download/HelloJava_1-0-0.jar");
        execBuild.redirectErrorStream(true);
        Process exec = null;
        try {
            exec = execBuild.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* ... and connect the process's I/O streams to the TermSession. */
        session.setTermIn(exec.getInputStream());
        session.setTermOut(exec.getOutputStream());
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
