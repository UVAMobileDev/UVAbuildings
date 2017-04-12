package nadim.uvabuildings;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static nadim.uvabuildings.R.id.image;

/**
 * Download Image related to building from google using custom search
 *
 */
public class imageDownload extends AsyncTask<String, Void, Bitmap> {


    @Override
    protected Bitmap doInBackground(String... strings) {
        //get name of building, url encode
        String name = strings[0];
        name = name.replace(" ", "%20");
        //api key for custom search
        String key = "AIzaSyAroiIgW9Qyn2QOMvdjJxZqntnDkdNH8Fc";
        try{
            //make url call
            URL url = new URL("https://www.googleapis.com/customsearch/v1?key="+key+ "&cx=018201406608522780638:vc5scda7xfs&q=" + name + "%20" + "UVA" + "&searchType=image&alt=json");
            URLConnection connection = url.openConnection();

            String line;
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line = reader.readLine()) != null) {
                builder.append(line);
            }

            //get image url from returned json
            JSONObject json = new JSONObject(builder.toString());
            String imageUrl = json.getJSONArray("items").getJSONObject(0).getString("link");

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageUrl).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //send bitmap back
            return bitmap;
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
