package julianwi.javainstaller;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.app.ProgressDialog;
import android.os.Handler;

public class Download {
	
	private ProgressDialog progress;
	private URL url;
	private Handler handler;
	private String path;
	
	public Download(ProgressDialog Progress, URL url, Handler Handler, String destpath){
		progress = Progress;
		this.url = url;
		handler = Handler;
		path = destpath;
	}

	public void start() {
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
	            	handler.post(new Runnable() {
						
						@Override
						public void run() {
							new Error("HTTP Error", error);
							
						}
					});
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
            	handler.post(new Runnable() {
					@Override
					public void run() {
						progress.setProgress(percent);
					}
				});
                output.write(data, 0, count);
            }
            input.close();
            output.close();
			progress.dismiss();
        } catch(Exception e){
        	e.printStackTrace();
        	progress.dismiss();
        	final String error = e.getMessage();
        	handler.post(new Runnable() {
				
				@Override
				public void run() {
					new Error("Error", error);
					
				}
			});
        }
	}
}
