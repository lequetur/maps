package informations;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import main.MapsActivity;
import main.maps.R;
import utilisateur.SQLiteHandler;

/**
 * Created by Narmi on 25/03/2016.
 */
public class MainInfo extends AppCompatActivity {

    FloatingActionButton favoris;
    private SQLiteHandler db;
    MapsActivity M = new MapsActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        favoris = (FloatingActionButton) findViewById(R.id.fab);



        favoris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar
                        .make(favoris, R.string.btn_ajout_favoris, Snackbar.LENGTH_LONG);

                snackbar.show();
            }
        });

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if(M.bControlLog) {
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) favoris.getLayoutParams();
            favoris.setLayoutParams(p);
            favoris.setVisibility(View.VISIBLE); // View.INVISIBLE might also be worth trying
        }
        else {
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) favoris.getLayoutParams();
            favoris.setLayoutParams(p);
            favoris.setVisibility(View.GONE); // View.INVISIBLE might also be worth trying
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(M.bControlLog) {
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) favoris.getLayoutParams();
            favoris.setLayoutParams(p);
            favoris.setVisibility(View.VISIBLE); // View.INVISIBLE might also be worth trying
        }
        else {
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) favoris.getLayoutParams();
            favoris.setLayoutParams(p);
            favoris.setVisibility(View.GONE); // View.INVISIBLE might also be worth trying
        }
    }
}
