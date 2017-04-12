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
 * Background api call for Near You Activity
 */
public class HttpUrlConnectionJson extends AsyncTask<String, Void, HashMap<String, LatLng>>{

    @Override
    //doInBackground method is the main method for the class
    protected HashMap<String, LatLng> doInBackground(String... strings) {
        //get API url from NearYou Activity
        String API = strings[0];
        HttpURLConnection connection = null;
        try {
            //set up connection
            URL url = new URL(API);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-length", "0");
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            //get response and save it to stringbuilder
            StringBuilder stringBuilder = new StringBuilder();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                String response = null;
                while ((response = bufferedReader.readLine()) != null) {
                    stringBuilder.append(response);
                }
                bufferedReader.close();



                //save the result as a jsonArray
                JSONArray edges = new JSONArray(stringBuilder.toString());



                //hashmap to store returned values
                HashMap<String, LatLng> mapVals = new HashMap<String, LatLng>();
                for (int i=0; i<edges.length(); i++) {
                    //save each value in JSON array as a JSON Object
                    JSONObject node = edges.getJSONObject(i);
                    //if latitude or longitude is null, do not add object. Otherwise add values to hashmap
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