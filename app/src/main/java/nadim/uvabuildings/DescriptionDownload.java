package nadim.uvabuildings;

import android.os.AsyncTask;
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

/**
 * Created by nadim on 4/3/17.
 * Download building information
 */
public class DescriptionDownload extends AsyncTask<String, Void, ArrayList> {

    @Override
    protected ArrayList doInBackground(String... strings) {
        //get api call from other class
        String API = strings[0];
        HttpURLConnection connection = null;
        try {
            //create connection
            URL url = new URL(API);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-length", "0");
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            //save response in stringbuilder
            StringBuilder stringBuilder = new StringBuilder();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                String response = null;
                while ((response = bufferedReader.readLine()) != null) {
                    stringBuilder.append(response);
                }
                bufferedReader.close();

                //create JSON object from stringbuilder
                JSONObject obj = new JSONObject(stringBuilder.toString());


                //add all the values returned in json
                String address = "";
                String allLats = "";
                String allLongs = "";
                String squareFootage = "";
                String yearBuilt = "";
                String category = "";
                ArrayList<String> desc = new ArrayList<String>();
                try {
                    address = obj.getString("Address");
                    allLats = obj.getString("Latitude");
                    allLongs = obj.getString("Longitude");
                    squareFootage = obj.getString("SquareFootage");
                    yearBuilt = obj.getString("YearBuilt");
                    category = obj.getString("Category");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                desc.add(address);
                desc.add(allLats);
                desc.add(allLongs);
                desc.add(squareFootage);
                desc.add(yearBuilt);
                desc.add(category);

                return desc;

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