package julianwi.javainstaller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.os.Bundle;
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
	private Boolean src;
	
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
		return MainActivity.sharedP.getString("path"+id, "");
	}
	
	public String getSource(){
		return MainActivity.sharedP.getString("source"+id, "");
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		AlertDialog ad = (AlertDialog) dialog;
		String value = ((EditText) ad.findViewById(1)).getText().toString();
		Editor edit = MainActivity.sharedP.edit();
		if(src==false){
			edit.putString("path"+id, value);
		}
		else{
			edit.putString("source"+id, value);
		}
		edit.commit();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case 0:
			Intent intent = new Intent(MainActivity.ma, InstallActivity.class);
			Bundle b = new Bundle();
			if(((Button) v).getText() == "install"){
				b.putInt("packages", 1 << id+1);
				//new Install(this);
			}
			else{
				b.putInt("packages", (1 << id+1)+1);
				//new UnInstall(this);
			}
			intent.putExtras(b);
			MainActivity.ma.startActivity(intent);
			break;
		case 1:
			src=false;
			showalert();
			break;
		case 2:
			src=true;
			showalert();
			break;
		case 3:
			Intent intent1 = new Intent(MainActivity.ma, InstallActivity.class);
			Bundle b1 = new Bundle();
			b1.putInt("packages", 1 << id+1);
			intent1.putExtras(b1);
			MainActivity.ma.startActivity(intent1);
			break;
		}
	}
	
	public void showalert() {
		AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.context);
		alert.setTitle(text);

		// Set an EditText view to get user input 
		final EditText input = new EditText(MainActivity.context);
		input.setId(1);
		if(src==false){
			alert.setMessage("path to install");
			input.setText(getPath());
		}
		else{
			alert.setMessage("source file");
			input.setText(getSource());
		}
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
				p = Runtime.getRuntime().exec(getPath()+"/busybox");
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
		if(id == 2 || id == 3 || id == 4 || id == 5 || id == 6){
			File versionfile = new File(getPath()+"/"+Checkforfile.file[id].substring(0, Checkforfile.file[id].length()-7)+".version");
			if(versionfile.exists()){
				try {
					version = new BufferedReader(new InputStreamReader(new FileInputStream(versionfile))).readLine();
				} catch (Exception e) {
				}
			}
		}
		System.out.println(text+" version = "+version);
		return version;
	}

}
