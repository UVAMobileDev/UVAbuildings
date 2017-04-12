package nadim.uvabuildings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import nadim.uvabuildings.R;

/**
 * First Activity on UVABuildings App
 */
public class MainActivity extends AppCompatActivity {

    /**
     * OnCreate Method
     * Set the xml related to this class
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Method that starts new activity NearYou on button click
     * @param v
     */
    public void nearYou(View v){
        Intent intent = new Intent(getApplicationContext(), NearYou.class);
        startActivity(intent);
    }

    /**
     * Method that starts new activity ByCategory on button click
     * @param v
     */
    public void ByCategory(View v){
        Intent intent = new Intent(getApplicationContext(), ByCategory.class);
        startActivity(intent);
    }
}
