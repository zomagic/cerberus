import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

class BBAsyncImageLoader {
    private static Activity _activity;
	private static Context _context;

    public static final String TAG = "[Cerberus]";
    public static void debugout(String msg) {
    	if (CerberusConfig.CONFIG.equals( "debug" )){
        	Log.i(TAG, "BBAsyncImageLoader.".concat(msg));
        }
    }

    private String imagefile = "";
	private static int id = 0;
	
	public void DownloadImage(final String url) {
		_activity=BBAndroidGame.AndroidGame().GetActivity();
		_context=_activity.getApplicationContext();		

		imagefile = "";
		Random random = new Random();
		id = id + 1;
		new ImageDownloader(_context, id).execute(url);
	}

    public String isDoneDownload() {
        if (imagefile == "") {
            return "";
        }else{
        	return imagefile;
		}
    }
    public void clearDownload() {
       if (!imagefile.isEmpty()) {
            try {
                // Extract the file path from the imagefile string
                String filePath = imagefile.replace("cerberus://internal/", "");
                File file = new File(_context.getFilesDir(), filePath);
                if (file.exists()) {
                    file.delete();
                }
            } catch (Exception e) {
                debugout("Error deleting file: " + e.toString());
            }
        }
        imagefile = "";
    }
    private static class ImageData {
        String imgUrl;
        boolean isLoaded;
        ImageData(String imgUrl, boolean isLoaded) {
            this.imgUrl = imgUrl;
            this.isLoaded = isLoaded;
        }
    }
	private class ImageDownloader extends AsyncTask<String, Void, String> {
		private final int id;
		private final Context context;

		ImageDownloader(Context context, int id) {
			this.context = context;
			this.id = id;
		}

		@Override
		protected String doInBackground(String... urls) {
			try {
				URL url = new URL(urls[0]);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				Bitmap bitmap = BitmapFactory.decodeStream(input);
				if (bitmap != null) {
					// Get the internal storage directory
					File internalDir = context.getFilesDir();
					// Create data folder under internal storage
					File dataFolder = new File(internalDir, "data");
					if (!dataFolder.exists()) {
						dataFolder.mkdirs();
					}
					String fileName = "image_" + String.valueOf(id) + ".png";
					
					// Save directly under data folder
					File imageFile = new File(dataFolder, fileName);
					FileOutputStream fos = new FileOutputStream(imageFile);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
					fos.flush();
					fos.close();
					return "cerberus://internal/data/" + fileName;  
				}
			} catch (Exception e) {
				//e.printStackTrace();
				debugout(e.toString());
			}
			return "";
		}
		@Override
		protected void onPostExecute(String result) {
			if (!result.isEmpty()) {
				imagefile = result;
			}
		}
	}
}
