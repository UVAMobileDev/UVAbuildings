package nadim.uvabuildings;


import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import nadim.uvabuildings.R;

/**
 * Created by nadim on 2/23/17.
 * Class will return a list of all categories for Buildings
 */
public class ByCategory extends ListActivity {

    //declare listview and listadapter
    private static final String API_KEY = BuildConfig.API_KEY;
    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;

    ArrayList<String> values = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_by_category);

        //query the API for categories
        query();
        mainListView = (ListView) findViewById( android.R.id.list);

        // Create ArrayAdapter using the list.
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, values);

        // Set the ArrayAdapter as the ListView's adapter.
        mainListView.setAdapter( listAdapter );

        //when category is clicked, start map activity, pass on category name to next activity
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ByCategory.this, CategorySelectActivity.class);
                String data=(String)parent.getItemAtPosition(position);
                intent.putExtra("category", data);
                startActivity(intent);
            }
        });
    }


    private void query() {
        //url for api
        String url = "http://138.197.11.189:3000/api/" + API_KEY + "/categories";
        //initialize query background class
        QueryCategory con = new QueryCategory();
        ArrayList<String> cat = new ArrayList<String>();
        try {
            cat = con.execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //save the values returned from background class
        for(int i = 0; i < cat.size(); i++){
            if(!(values.contains(cat.get(i)))){
                values.add(cat.get(i));
            }
        }

        //sort the values in alphabetical order
        Collections.sort(values);


    }

}
