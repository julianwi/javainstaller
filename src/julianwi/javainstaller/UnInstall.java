package julianwi.javainstaller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class UnInstall {
	
	private CheckPoint mcheck;

	public UnInstall(CheckPoint checkPoint) {
		try{
			mcheck = checkPoint;
			if(mcheck.id == 0){
				Uri packageURI = Uri.parse("package:jackpal.androidterm");
				Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
				MainActivity.context.startActivity(uninstallIntent);
			}
			else{
				Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/data/data/julianwi.javainstaller/install.sh"), "utf-8"));
				writer.write("#!/system/bin/sh\nbbdir="+MainActivity.checks[1].getPath()+"\n");
				if(mcheck.id == 1 || mcheck.id == 4){
					writer.write("$bbdir rm "+mcheck.getPath()+"\n");
				}
				else{
					writer.write("$bbdir rm -r "+mcheck.getPath()+"\n");
				}
				if(mcheck.id == 4){
					writer.write("am start --user 0 -a android.intent.action.DELETE -d package:julianwi.awtpeer\n");
				}
				writer.write("echo uninstallation complete\n");
				writer.write("exit");
				writer.close();
				Install.chmod(new File("/data/data/julianwi.javainstaller/install.sh"), 0755);
				String runmode = MainActivity.context.getSharedPreferences("julianwi.javainstaller_preferences", 1).getString("runmode", "auto");
				if(runmode.equals("Run Activity") || (runmode.equals("auto")&&mcheck.getPath().startsWith("/data/data/julianwi.javainstaller"))){
					Intent intent = new Intent(MainActivity.context, RunActivity.class);
					Bundle b = new Bundle();
					b.putBoolean("install", true);
					intent.putExtras(b);
					MainActivity.context.startActivity(intent);
				}
				else{
					Intent i = new Intent("jackpal.androidterm.RUN_SCRIPT");
					i.addCategory(Intent.CATEGORY_DEFAULT);
					if(MainActivity.context.getSharedPreferences("julianwi.javainstaller_preferences", 1).getString("rootmode", "off").equals("on")){
						i.putExtra("jackpal.androidterm.iInitialCommand", "su\nsh /data/data/julianwi.javainstaller/install.sh");
					}
					else{
						i.putExtra("jackpal.androidterm.iInitialCommand", "sh /data/data/julianwi.javainstaller/install.sh");
					}
					MainActivity.context.startActivity(i);
				}
			}
		}catch(Exception e){
			new Error("error", e.getMessage());
		}
	}

}
