package julianwi.javainstaller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
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

}
