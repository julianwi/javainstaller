package julianwi.javainstaller;

import java.net.URI;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.Menu;
import android.widget.ListView;

public class MainActivity extends Activity implements OnSharedPreferenceChangeListener {
	
	private ListView lv;
	public static CheckPoint[] checks = new CheckPoint[]{
			new CheckPoint("install Terminal Emulator","http://borcteam.bplaced.net/Daten/java/jackpal.androidterm.apk" ,0),
			new CheckPoint("install busybox","http://borcteam.bplaced.net/Daten/java/busybox" ,1),
			new CheckPoint("install gnu libc","http://borcteam.bplaced.net/Daten/java/libc.tar.gz" ,2),
			new CheckPoint("install precompiled versions of jamvm and gnu classpath","http://borcteam.bplaced.net/Daten/java/java.tar.gz" ,3)
	};
	//private String[] checklist = new String[]{"install busybox", "install Terminal Emulator", "install gnu libc", "install precompiled versions of jamvm and gnu classpath"};
	//private Boolean[] checklist2 = new Boolean[]{false, false, false, false};
	//private String[] checklist3 = new String[]{"/data/data/jackpal.androidterm/bin/busybox", "/data/app/jackpal.androidterm-1.apk", "", ""};
	private ChecklistAdapter listenAdapter;
	public static SharedPreferences sharedP;
	public static Context context;
	public static Context termcontext;
	//public OnClickListener onclick = new Install(this ,checklist);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		try {
			termcontext = createPackageContext("jackpal.androidterm", CONTEXT_IGNORE_SECURITY);
		} catch (NameNotFoundException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		sharedP = getSharedPreferences("settings", 1);
		sharedP.registerOnSharedPreferenceChangeListener(this);
		//Checkforfile cff = new Checkforfile();
		//cff.scan(checks);
		lv = new ListView(this);
        listenAdapter = new ChecklistAdapter(this, checks);
        lv.setAdapter(listenAdapter);
        setContentView(lv);
	}
	
	@Override
	protected void onResume() {
		Checkforfile cff = new Checkforfile();
		cff.scan(checks);
		listenAdapter.notifyDataSetChanged();
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add("settings");
		return true;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Checkforfile cff = new Checkforfile();
		cff.scan(checks);
		listenAdapter.notifyDataSetChanged();
	}
	
	public void choosefile(String type){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        startActivityForResult(intent,0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch(requestCode){
		case 0:
			if(resultCode==RESULT_OK){
				String FilePath = data.getData().getPath();
				Intent intent = new Intent(MainActivity.context, RunActivity.class);
				intent.setDataAndType(Uri.parse(FilePath), "application/java-archive");
			    MainActivity.context.startActivity(intent);
			}
			break;
	
		}
	}

}
