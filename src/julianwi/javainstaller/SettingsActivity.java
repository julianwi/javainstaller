package julianwi.javainstaller;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.view.View;

public class SettingsActivity extends PreferenceActivity implements OnPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPreferenceScreen(createPreferenceHierarchy());
        
    }
 
    private PreferenceScreen createPreferenceHierarchy() {
 
        CharSequence[] cs = new String[] { "Terminal Emulator", "Run Activity", "auto" };
 
        // Root
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
        PreferenceCategory dialogBasedPrefCat = new PreferenceCategory(this);
        dialogBasedPrefCat.setTitle("Settings");
        root.addPreference(dialogBasedPrefCat); // Adding a category
 
        // List preference under the category
        ListPreference listPref = new ListPreference(this);
        listPref.setKey("runactivity");
        listPref.setEntries(cs);
        listPref.setEntryValues(cs);
        listPref.setDialogTitle("run install.sh in");
        listPref.setTitle("run install.sh in");
        listPref.setSummary("run install.sh in");
        listPref.setDefaultValue("auto");
        listPref.setOnPreferenceChangeListener(this);
        dialogBasedPrefCat.addPreference(listPref);
 
        return root;
    }

	@Override
	public boolean onPreferenceChange(Preference arg0, Object newvalue) {
		int value;
		System.out.println((String)newvalue);
		Editor edit = arg0.getSharedPreferences().edit();
		edit.putString("runactivity", (String) newvalue);
		edit.commit();
		System.out.println(arg0.getSharedPreferences().getAll());
		if((String)newvalue=="Terminal Emulator"){
			value = 1;
		}
		else if((String)newvalue=="Run Activity"){
			value =2;
		}
		else{
			value = 0;
		}System.out.println(value);
		getSharedPreferences("settings", 1).edit().putInt("runActivity", value);
		return false;
	}
}