package julianwi.javainstaller;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Download implements Runnable {
	
	private ProgressBar progress;
	private URL url;
	private Handler handler;
	private String path;
	private TextView tv;
	private Context ctx;
	
	public Download(ProgressBar Progress, TextView tv, URL url, Handler Handler, String destpath, Context c){
		progress = Progress;
		this.url = url;
		handler = Handler;
		path = destpath;
		this.tv = tv;
		ctx = c;
	}

	public void run() {
		InputStream input = null;
        OutputStream output = null;
        URLConnection connection = null;
        HttpURLConnection httpconnection = null;
		try {
            connection = url.openConnection();
            connection.connect();

            if(connection instanceof HttpURLConnection){
            	httpconnection = (HttpURLConnection) connection;

	            // expect HTTP 200 OK, so we don't mistakenly save error report
	            // instead of the file
	            if (httpconnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
	            	final String error = "Server returned HTTP " + httpconnection.getResponseCode() + " " + httpconnection.getResponseMessage();
	            	handler.post(new Error(new Exception(error), ctx)/*new Runnable() {
						
						@Override
						public void run() {
							new Error("HTTP Error", error);
							
						}
					}*/);
	            	System.out.println("Server returned HTTP " + httpconnection.getResponseCode()
	                        + " " + httpconnection.getResponseMessage());
	            }
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(path);

            final byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data, 0, 1024)) != -1) {
            	total += count;
            	final int percent = (int) (total * 100 / fileLength);
            	final String status = total/1024+"/"+fileLength/1024+"kb  "+percent+"/100%";
            	handler.post(new Runnable() {
					@Override
					public void run() {
						progress.setProgress(percent);
						tv.setText(status);
					}
				});
                output.write(data, 0, count);
            }
            input.close();
            output.close();
			//progress.dismiss();
        } catch(Exception e){
        	e.printStackTrace();
        	handler.post(new Error(e, ctx));
        	//progress.dismiss();
        	/*final String error = e.getMessage();
        	handler.post(new Runnable() {
				
				@Override
				public void run() {
					new Error("Error", error);
					
				}
			});*/
        }
	}
}
