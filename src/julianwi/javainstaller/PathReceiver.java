package julianwi.javainstaller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class PathReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals("jackpal.androidterm.broadcast.APPEND_TO_PATH")) {
			SharedPreferences settings = context.getSharedPreferences("julianwi.javainstaller_preferences", 1);
			String pref = settings.getString("broadcast", "if jamvm is installed");
			String javapath = context.getSharedPreferences("settings", 1).getString("path5", "/data/data/julianwi.javainstaller/javafiles");
			System.out.println(pref);
			if (pref.equals("on") || (pref.equals("if jamvm is installed") && new Checkforfile().checkfile(javapath))){
				Bundle result = getResultExtras(true);
				String path = settings.getString("broadcastpath", "/data/data/julianwi.javainstaller");
				System.out.println("path: " + path);
	            result.putString(context.getPackageName(), path);
	            setResultCode(Activity.RESULT_OK);
			}
        }
	}

}
