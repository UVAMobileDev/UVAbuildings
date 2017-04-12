package nadim.uvabuildings;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Double.parseDouble;


/**
 * Background class to call API
 */
public class HttpConnectionJsonVariable extends AsyncTask<String, Void, HashMap<String, LatLng>>{


    @Override
    protected HashMap<String, LatLng> doInBackground(String... strings) {
        //get API from other activity
        String API = strings[0];

        HttpURLConnection connection = null;
        try {
            //create connection to API server
            URL url = new URL(API);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-length", "0");
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            //save response in string builder
            StringBuilder stringBuilder = new StringBuilder();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                String response = null;
                while ((response = bufferedReader.readLine()) != null) {
                    stringBuilder.append(response);
                }
                bufferedReader.close();

                //turn stringbuilder into JSON Array
                JSONArray edges = new JSONArray(stringBuilder.toString());



                //save return values to hashmap
                HashMap<String, LatLng> mapVals = new HashMap<String, LatLng>();
                for (int i=0; i<edges.length(); i++) {
                    JSONObject node = edges.getJSONObject(i);
                    if(!(node.getString("Latitude").equals("null")) && !(node.getString("Longitude").equals("null"))) {
                        LatLng latLng = new LatLng(parseDouble(node.getString("Latitude")),parseDouble(node.getString("Longitude")));
                        mapVals.put(node.getString("Name"), latLng);
                    }

                }

                //return hashmap
                return mapVals;

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
