package nadim.uvabuildings;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

/**
 * Activity that shows information regarding a data
 */
public class MarkerDescription extends AppCompatActivity {

    private static final String API_KEY = BuildConfig.API_KEY;
    private String newString;
    private String address;
    private String latitude;
    private String longitude;
    private String squareFootage;
    private String yearBuilt;
    private String category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_description);

        //get building name from map activity
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                newString = null;
            } else {
                newString = extras.getString("marker");
            }
        }
        else {
            newString = (String) savedInstanceState.getSerializable("marker");
        }

        //make the API call
        query();


        //call Image Download class, and set it to view
        imageDownload img = new imageDownload();
        Bitmap hold;
        try {
            hold = img.execute(newString).get();
            ImageView imageView = (ImageView) findViewById(R.id.image1);

            imageView.setImageBitmap(hold);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



        //initialize all table rows and text views from returned data
        TextView myTextView = (TextView) findViewById(R.id.title);
        myTextView.setText(newString);

        TableLayout ll = (TableLayout) findViewById(R.id.display);

        TableRow row= (TableRow) findViewById(R.id.tr1);
        TextView myTextView2 = (TextView) findViewById(R.id.row1);
        myTextView2.append(address);

        TableRow row2= (TableRow) findViewById(R.id.tr1);
        TextView myTextView3 = (TextView) findViewById(R.id.row2);
        myTextView3.append(latitude);

        TableRow row3= (TableRow) findViewById(R.id.tr1);
        TextView myTextView4 = (TextView) findViewById(R.id.row3);
        myTextView4.append(longitude);

        TableRow row4= (TableRow) findViewById(R.id.tr1);
        TextView myTextView5 = (TextView) findViewById(R.id.row4);
        myTextView5.append(squareFootage);

        TableRow row5= (TableRow) findViewById(R.id.tr1);
        TextView myTextView6 = (TextView) findViewById(R.id.row5);
        myTextView6.append(yearBuilt);

        TableRow row6= (TableRow) findViewById(R.id.tr1);
        TextView myTextView7 = (TextView) findViewById(R.id.row6);
        myTextView7.append(category);
    }

    /**
     * query method to call api
     */
    private void query() {
        String hold= "";
        //url encode building name and create url
        hold = newString.replace(" ", "%20");
        String url = "http://138.197.11.189:3000/api/" + API_KEY + "/buildings/" + hold;
        DescriptionDownload con = new DescriptionDownload();
        ArrayList<String> markInfo = new ArrayList<String>();
        try {
            markInfo = con.execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //get all values returned from background class
        address = markInfo.get(0);
        latitude = markInfo.get(1);
        longitude = markInfo.get(2);
        squareFootage = markInfo.get(3);
        yearBuilt = markInfo.get(4);
        category = markInfo.get(5);
    }


}
