package nadim.uvabuildings;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;


/**
 * Created by nadim on 1/24/17.
 */
public class QueryCategory extends AsyncTask<String, Void, ArrayList>{

    @Override
    protected ArrayList doInBackground(String... strings) {
        //get url from ByCategory class
        String API = strings[0];
        HttpURLConnection connection = null;
        try {
            //create new HTTP connection
            URL url = new URL(API);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-length", "0");
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            //save response into stringbuilder
            StringBuilder stringBuilder = new StringBuilder();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                String response = null;
                while ((response = bufferedReader.readLine()) != null) {
                    stringBuilder.append(response);
                }
                bufferedReader.close();

                //turn string builder into JSON Array
                JSONArray edges = new JSONArray(stringBuilder.toString());


                //Save categories to ArrayList
                ArrayList<String> Categories = new ArrayList<String>();
                for (int i=0; i<edges.length(); i++) {
                    Categories.add(edges.get(i).toString());

                }

                return Categories;

            } else {
                return null;
            }
        } catch (Exception exception) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}