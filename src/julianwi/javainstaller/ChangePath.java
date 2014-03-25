package julianwi.javainstaller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class ChangePath implements OnClickListener,
		android.content.DialogInterface.OnClickListener {
	
	private SharedPreferences sharedP;
	private Context mcontext;
	private ChecklistAdapter madapt;
	
	public ChangePath(SharedPreferences sharedP2, Context context, ChecklistAdapter adapt){
		sharedP = sharedP2;
		mcontext = context;
		madapt = adapt;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
	}

	@Override
	public void onClick(View v) {
		showalert(v.getId(), sharedP.getString("path"+v.getId(), ""));
	}
	
	public void showalert(int id, String path) {
		AlertDialog.Builder alert = new AlertDialog.Builder(mcontext);

		alert.setMessage("path to install");

		// Set an EditText view to get user input 
		final EditText input = new EditText(mcontext);
		input.setId(id);
		input.setText(path);
		alert.setView(input);

		alert.setPositiveButton("save", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    Editor edit = sharedP.edit();
		    edit.putString("path"+input.getId(), input.getText().toString());
		    edit.commit();
		    madapt.notifyDataSetChanged();
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
	}

}
