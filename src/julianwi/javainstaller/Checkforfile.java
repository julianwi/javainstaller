package julianwi.javainstaller;

import java.io.File;
import android.content.SharedPreferences.Editor;

public class Checkforfile {
	
	 public void scan(CheckPoint[] checks){
		 if(!MainActivity.sharedP.contains("path0")){
			 Editor edit = MainActivity.sharedP.edit();
			 edit.putString("path0", "/data/app/jackpal.androidterm-1.apk");
			 edit.putString("path1", "/data/data/jackpal.androidterm/bin/busybox");
			 edit.putString("path2", "/data/data/jackpal.androidterm/libc");
			 edit.putString("path3", "/data/data/julianwi.javainstaller/java");
			 edit.commit();
		 }
		 if(checkfile(checks[0].getPath())){
			 checks[0].installed = true;
		 }
		 else{
			 checks[0].installed = false;
		 }
		 if(checkfile(checks[1].getPath())){
			 checks[1].installed = true;
		 }
		 else{
			 checks[1].installed = false;
		 }
		 if(checkfile(checks[2].getPath())){
			 checks[2].installed = true;
		 }
		 else{
			 checks[2].installed = false;
		 }
		 if(checkfile(checks[3].getPath())){
			 checks[3].installed = true;
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
}
