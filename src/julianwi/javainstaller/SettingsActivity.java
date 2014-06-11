package julianwi.javainstaller;

import android.os.Bundle;
import android.preference.EditTextPreference;
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
        CharSequence[] cs2 = new String[] { "off", "on" };
 
        // Root
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
        PreferenceCategory dialogBasedPrefCat = new PreferenceCategory(this);
        dialogBasedPrefCat.setTitle("install");
        root.addPreference(dialogBasedPrefCat); // Adding a category
 
        // List preference under the category
        ListPreference listPref = new ListPreference(this);
        listPref.setKey("runmode");
        listPref.setDefaultValue(cs[2]);
        listPref.setEntries(cs);
        listPref.setEntryValues(cs);
        listPref.setDialogTitle("run install.sh in");
        listPref.setTitle("run install.sh in");
        listPref.setSummary("run install.sh in");
        dialogBasedPrefCat.addPreference(listPref);
        ListPreference rootmode = new ListPreference(this);
        rootmode.setKey("rootmode");
        rootmode.setDefaultValue(cs2[0]);
        rootmode.setEntries(cs2);
        rootmode.setEntryValues(cs2);
        rootmode.setDialogTitle("run install.sh as superuser");
        rootmode.setTitle("run install.sh as superuser");
        rootmode.setSummary("root required");
        dialogBasedPrefCat.addPreference(rootmode);
        
        PreferenceCategory dialogBasedPrefCat2 = new PreferenceCategory(this);
        dialogBasedPrefCat2.setTitle("run");
        root.addPreference(dialogBasedPrefCat2); // Adding a category
 
        // List preference under the category
        CharSequence[] csjar = new String[] { "Terminal Emulator", "Run Activity" };
        ListPreference listPref2 = new ListPreference(this);
        listPref2.setKey("runmode2");
        listPref2.setDefaultValue(csjar[1]);
        listPref2.setEntries(csjar);
        listPref2.setEntryValues(csjar);
        listPref2.setDialogTitle("run jar file in");
        listPref2.setTitle("run jar file in");
        listPref2.setSummary("run jar file in");
        dialogBasedPrefCat2.addPreference(listPref2);
        ListPreference rootmode2 = new ListPreference(this);
        rootmode2.setKey("rootmode2");
        rootmode2.setDefaultValue(cs2[0]);
        rootmode2.setEntries(cs2);
        rootmode2.setEntryValues(cs2);
        rootmode2.setDialogTitle("run jar file as superuser");
        rootmode2.setTitle("run jar file as superuser");
        rootmode2.setSummary("root required");
        dialogBasedPrefCat2.addPreference(rootmode2);
        
        PreferenceCategory dialogBasedPrefCat3 = new PreferenceCategory(this);
        dialogBasedPrefCat3.setTitle("path broadcast");
        root.addPreference(dialogBasedPrefCat3); // Adding a category
 
        // List preference under the category
        CharSequence[] cspath = new String[] { "on", "off", "if java is installed" };
        ListPreference listPref3 = new ListPreference(this);
        listPref3.setKey("broadcast");
        listPref3.setDefaultValue(cspath[2]);
        listPref3.setEntries(cspath);
        listPref3.setEntryValues(cspath);
        listPref3.setDialogTitle("broadcast path to terminal emulator");
        listPref3.setTitle("broadcast path to terminal emulator");
        listPref3.setSummary("broadcast path to terminal emulator");
        dialogBasedPrefCat3.addPreference(listPref3);
        EditTextPreference path = new EditTextPreference(this);
        path.setKey("broadcastpath");
        path.setDefaultValue("%javapath%");
        path.setDialogTitle("path to broadcast");
        path.setTitle("path to broadcast");
        path.setSummary("%javapath% for the install path of jamvm and gnu classpath");
        dialogBasedPrefCat3.addPreference(path);
 
        return root;
    }

}