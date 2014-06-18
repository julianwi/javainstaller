package julianwi.javainstaller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.widget.Toast;

public class Update extends Thread{
	
	public static boolean update[] = {false, false, false, false, false};
	public static String updatetext[] = new String[5];
	private ChecklistAdapter listadapter;
	private Activity activity;
	
	public Update(ChecklistAdapter la, Activity mactivity){
		listadapter = la;
		activity = mactivity;
	}

	@Override
	public void run() {
		try {
			URL url = new URL("http://borcteam.bplaced.net/Daten/java/versions");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            // download the file
            InputStream input = connection.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(input));
            boolean udate = false;
    		for(int i = 0;i<5;i++){
    			String version = read.readLine();
    			String oldversion = MainActivity.checks[i].getversion();
    			//System.out.println(version+" old: "+oldversion);
    			if(oldversion.equals(version)){
    				update[i] = false;
    			}
    			else{
    				udate = true;
    				update[i] = true;
    				updatetext[i] = "update available: "+oldversion+" to "+ version;
    			}
    		}
            input.close();
            final String toast;
            if(udate){
            	toast = "new uptates";
            }
            else{
            	toast = "nothing new";
			}
            activity.runOnUiThread(new Runnable() {
                public void run() {
                	listadapter.notifyDataSetChanged();
                	Toast.makeText(MainActivity.context, toast, Toast.LENGTH_LONG).show();
                }
            });
            
        } catch(Exception e){
        	e.printStackTrace();
        }
	}
}
