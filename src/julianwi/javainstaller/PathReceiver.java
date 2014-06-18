package julianwi.javainstaller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class PathReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals("jackpal.androidterm.broadcast.APPEND_TO_PATH")) {
			String pref = context.getSharedPreferences("julianwi.javainstaller_preferences", 1).getString("brodcast", "if java is installed");
			String javapath = context.getSharedPreferences("settings", 1).getString("path"+3, "");
			System.out.println(pref);
			System.out.println(new Checkforfile().checkfile(javapath));
			if (pref.equals("on") || (pref.equals("if java is installed") && new Checkforfile().checkfile(javapath))){
				Bundle result = getResultExtras(true);
				String path = context.getSharedPreferences("julianwi.javainstaller_preferences", 1).getString("broadcastpath", "%javapath%");
				System.out.println("path: " + path);
				path.replace("%javapath%", javapath);
				System.out.println("path: " + path);
	            result.putString(context.getPackageName(), path);
	            setResultCode(Activity.RESULT_OK);
			}
        }
	}

}
