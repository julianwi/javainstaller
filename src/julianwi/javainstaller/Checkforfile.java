package julianwi.javainstaller;

import java.io.File;

import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;

public class Checkforfile {
	
	 public void scan(CheckPoint[] checks){
		 Editor edit = MainActivity.sharedP.edit();
		 if(!MainActivity.sharedP.contains("path0")){
			 edit.putString("path0", "/data/app/jackpal.androidterm.apk");
			 edit.putString("path1", "/data/data/jackpal.androidterm/bin/busybox");
			 edit.putString("path2", "/data/data/jackpal.androidterm/libc");
			 edit.putString("path3", "/data/data/julianwi.javainstaller/java");
			 edit.commit();
		 }
		 if(checkpackage("jackpal.androidterm")){
			 checks[0].installed = true;
			 try {
				 edit.putString("path0", MainActivity.context.getPackageManager().getApplicationInfo("jackpal.androidterm", 0).sourceDir);
			} catch (Exception e) {
				 e.printStackTrace();
				new Error("error", e.getMessage());
			}
			 new File("/data/data/julianwi.javainstaller/"+Install.tmp[checks[0].id]).delete();
		 }
		 else{
			 checks[0].installed = false;
			 edit.putString("path0", "/data/app/jackpal.androidterm.apk");
		 }
		 edit.commit();
		 if(checkfile(checks[1].getPath())){
			 checks[1].installed = true;
			 new File("/data/data/julianwi.javainstaller/"+Install.tmp[checks[1].id]).delete();
		 }
		 else{
			 checks[1].installed = false;
		 }
		 if(checkfile(checks[2].getPath())){
			 checks[2].installed = true;
			 new File("/data/data/julianwi.javainstaller/"+Install.tmp[checks[2].id]).delete();
		 }
		 else{
			 checks[2].installed = false;
		 }
		 if(checkfile(checks[3].getPath())){
			 checks[3].installed = true;
			 new File("/data/data/julianwi.javainstaller/"+Install.tmp[checks[3].id]).delete();
		 }
		 else{
			 checks[3].installed = false;
		 }
	 }
	 
	public boolean checkfile(String filepath){
		File file = new File(filepath);
		if(file.exists()){
			return true;
		}
		return false;
	}
	
	public boolean checkpackage(String packagename) {
	    try {
	        MainActivity.context.getPackageManager().getPackageInfo(packagename, 0);
	        return true;
	    } catch (NameNotFoundException e) {
	        return false;
	    }
	}
}
