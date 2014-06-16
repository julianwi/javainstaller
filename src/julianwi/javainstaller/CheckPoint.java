package julianwi.javainstaller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CheckPoint implements OnClickListener,
		android.content.DialogInterface.OnClickListener {
	
	public String text;
	public Boolean installed;
	public int id;
	public String source;
	
	public CheckPoint(String Text, String Source, int Id){
		text = Text;
		installed = false;
		id = Id;
		source = Source;
		if(Install.getArch()=="arm"){
			if(Install.arm.length>Id && Install.arm[Id] != null){
				source = Install.arm[Id];
			}
		}
	}
	
	public String getPath(){
		if(id == 4){
			return MainActivity.checks[3].getPath()+"/lib/awtpeer.zip";
		}
		return MainActivity.sharedP.getString("path"+id, "");
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		AlertDialog ad = (AlertDialog) dialog;
		String value = ((EditText) ad.findViewById(1)).getText().toString();
		Editor edit = MainActivity.sharedP.edit();
		edit.putString("path"+id, value);
		edit.commit();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case 0:
			if(((Button) v).getText() == "install"){
				new Install(this);
			}
			else{
				new UnInstall(this);
			}
			break;
		case 1:
			showalert();
			break;
		case 3:
			new Install(this);

		default:
			break;
		}
	}
	
	public void showalert() {
		AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.context);

		alert.setMessage("path to install");

		// Set an EditText view to get user input 
		final EditText input = new EditText(MainActivity.context);
		input.setId(1);
		input.setText(getPath());
		alert.setView(input);

		alert.setPositiveButton("save", this);

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
	}
	
	public String getversion(){
		String version = "unknown";
		if(id == 0){
			try {
				PackageInfo pInfo = MainActivity.context.getPackageManager().getPackageInfo("jackpal.androidterm", 0);
				version = pInfo.versionName;
			} catch (Exception e) {
			}
		}
		if(id == 1){
			try {
				Process p;
				p = Runtime.getRuntime().exec(getPath());
				InputStream a = p.getInputStream();
				InputStreamReader read = new InputStreamReader(a);
				String line = (new BufferedReader(read)).readLine();
				if(line == null){
					version = "unknown";
				}
				else{
					version = line.split("\\s+")[1];
				}
			} catch (IOException e) {
			}
		}
		if(id == 2 || id == 3){
			File versionfile = new File(getPath()+"/version");
			if(versionfile.exists()){
				try {
					version = new BufferedReader(new InputStreamReader(new FileInputStream(versionfile))).readLine();
				} catch (Exception e) {
				}
			}
		}
		if(id == 4){
			try {
				PackageInfo pInfo = MainActivity.context.getPackageManager().getPackageInfo("julianwi.awtpeer", 0);
				version = pInfo.versionName;
			} catch (Exception e) {
			}
		}
		if(version == null){
			version = "undefined";
		}
		return version;
	}

}
