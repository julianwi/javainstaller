package julianwi.javainstaller;

import java.io.File;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;

public class Checkforfile {
	
	public static String file[] = new String[]{"androidterm.apk", "busybox", "libc.tar.gz", "zlib.tar.gz", "libffi.tar.gz", "jamvm.tar.gz", "classpath.tar.gz", "freetype.tar.gz", "awtonandroid.tar.gz"};
	public static String files[][] = new String[][]{
		new String[]{},
		new String[]{"busybox"},
		new String[]{"ld-linux.so.2", "libBrokenLocale.so.1", "libSegFault.so", "libanl.so.1", "libc.so.6" ,"libc.version" ,"libcidn.so.1", "libcrypt.so.1", "libdl.so.2", "libm.so.6", "libmemusage.so", "libnsl.so.1", "libnss_compat.so.2", "libnss_db.so.2", "libnss_dns.so.2", "libnss_files.so.2", "libnss_hesiod.so.2", "libnss_nis.so.2", "libnss_nisplus.so.2", "libpcprofile.so", "libpthread.so.0", "libresolv.so.2", "librt.so.1", "libthread_db.so.1", "libutil.so.1"},
		new String[]{"libz.so.1", "zlib.version"},
		new String[]{"libffi.so.6", "libffi.version"},
		new String[]{"classes.zip", "jamvm", "jamvm.version"},
		new String[]{"glibj.zip", "libgconfpeer.so", "libjavaio.so", "libjavalang.so", "libjavalangmanagement.so", "libjavalangreflect.so", "libjavanet.so", "libjavanio.so", "libjavautil.so", "tools.zip"}
	};
	
	 public void scan(CheckPoint[] checks){
		 Editor edit = MainActivity.sharedP.edit();
		 if(!MainActivity.sharedP.contains("path4")){
			 edit.putString("path0", "/data/app/");
			 edit.putString("source0", "http://borcteam.bplaced.net/files/java/androidterm.apk");
			 edit.putString("path1", "/data/data/jackpal.androidterm/bin");
			 edit.putString("source1", "http://borcteam.bplaced.net/files/java/busybox");
			 for(int i=2;i<=8;i++){
				 edit.putString("path"+i, "/data/data/julianwi.javainstaller/javafiles");
				 edit.putString("source"+i, "http://borcteam.bplaced.net/files/java/"+file[i]);
			 }
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
			 new File("/data/data/julianwi.javainstaller/"+file[0]).delete();
		 }
		 else{
			 checks[0].installed = false;
			 edit.putString("path0", "/data/app/jackpal.androidterm.apk");
		 }
		 edit.commit();
		for (int i = 1; i < files.length; i++) {
			Boolean installed = true;
			for (int j = 0; j < files[i].length; j++) {
				if(!checkfile(checks[i].getPath()+"/"+files[i][j])){
					installed = false;
				}
			}
			checks[i].installed = installed;
			if(installed){
				new File("/data/data/julianwi.javainstaller/"+file[i]).delete();
			}
		}
		 /*if(checkfile(checks[1].getPath())){
			 checks[1].installed = true;
			 new File("/data/data/julianwi.javainstaller/"+file[1]).delete();
		 }
		 else{
			 checks[1].installed = false;
		 }
		 if(checkfile(checks[2].getPath())){
			 checks[2].installed = true;
			 new File("/data/data/julianwi.javainstaller/"+file[2]).delete();
		 }
		 else{
			 checks[2].installed = false;
		 }
		 if(checkfile(checks[3].getPath())){
			 checks[3].installed = true;
			 new File("/data/data/julianwi.javainstaller/"+file[3]).delete();
		 }
		 else{
			 checks[3].installed = false;
		 }
		 if(checkfile(checks[4].getPath()) && checkpackage("julianwi.awtpeer")){
			 checks[4].installed = true;
			 new File("/data/data/julianwi.javainstaller/"+file[4]).delete();
		 }
		 else{
			 checks[4].installed = false;
		 }*/
	 }
	 
	public boolean checkfile(String filepath){
		File file = new File(filepath);
		return file.exists();
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
