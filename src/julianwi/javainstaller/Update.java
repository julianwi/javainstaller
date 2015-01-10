package julianwi.javainstaller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.widget.Toast;

public class Update extends Thread{
	
	public static boolean update[] = {false, false, false, false, false, false, false, false, false, false};
	public static boolean udate = false;
	public static String updatetext[] = new String[10];
	private MainActivity activity;
	
	public Update(ChecklistAdapter la, MainActivity mactivity){
		activity = mactivity;
	}

	@Override
	public void run() {
		try {
			udate = false;
			URL url = new URL((Checkforfile.getArch().equals("arm"))?"http://borcteam.bplaced.net/files/java/arm/versions":"http://borcteam.bplaced.net/files/java/versions");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            // download the file
            InputStream input = connection.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(input));
    		for(int i = 0;i<10;i++){
    			String version = read.readLine();
    			String oldversion = MainActivity.checks[i].getversion();
    			System.out.println(version+" old: "+oldversion);
    			if(oldversion.equals(version) || !MainActivity.checks[i].installed){
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
                	activity.update();
                	Toast.makeText(MainActivity.context, toast, Toast.LENGTH_LONG).show();
                }
            });
            
        } catch(Exception e){
        	e.printStackTrace();
        }
	}
}
