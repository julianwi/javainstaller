package julianwi.javainstaller;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

public class SettingsActivity extends PreferenceActivity {
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
        listPref.setKey("runmode");
        listPref.setEntries(cs);
        listPref.setEntryValues(cs);
        listPref.setDialogTitle("run install.sh in");
        listPref.setTitle("run install.sh in");
        listPref.setSummary("run install.sh in");
        dialogBasedPrefCat.addPreference(listPref);
 
        return root;
    }

}