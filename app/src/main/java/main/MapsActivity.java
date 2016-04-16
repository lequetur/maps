package main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Locale;

import main.maps.R;
import utilisateur.SQLiteHandler;
import utilisateur.SessionManager;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener  {

    private DrawerLayout mDrawerLayout;
    private GoogleApiClient mGoogleApiClient;
    Locale mLocale;
    Configuration config;
    private LocationManager locationManager;
    private GoogleMap gMap;
    private Marker marker;
    public static final int RESULT_Main = 1;
    private TextView tv;

    private SQLiteHandler db;
    private SessionManager session;

    public NavigationView navigationView;

    private GoogleApiClient client;

    public static Menu menu;

    public static boolean bControlLog = false;
    public static int iControlCo=0;

    public static String sNomUtil="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

       // gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
       // marker = gMap.addMarker(new MarkerOptions().title("Vous êtes ici").position(new LatLng(0, 0)));

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // session manager
        session = new SessionManager(getApplicationContext());
        //Au lancement si utilisateur connecté, ca déconnecte
        if (session.isLoggedIn()) {
            logoutUser();
        }
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navCo:
                        Intent intent = new Intent(MapsActivity.this, utilisateur.MainConnexion.class);
                        startActivity(intent);
                        return true;
                    case R.id.navDeco:
                        logoutUser();
                        navigationView.getMenu().findItem(R.id.itemProfile).setTitle("Profile");
                        bControlLog = false;
                        navigationView.getMenu().findItem(R.id.navCo).setVisible(true);
                        navigationView.getMenu().findItem(R.id.navNew).setVisible(true);
                        navigationView.getMenu().findItem(R.id.navDeco).setVisible(false);
                        navigationView.getMenu().findItem(R.id.navFav).setVisible(false);
                        return true;
                    case R.id.navNew:
                        Intent intent1 = new Intent(MapsActivity.this, utilisateur.MainNouveau.class);
                        startActivity(intent1);
                        return true;
                    case R.id.navChateau:
                        Intent intent2 = new Intent(MapsActivity.this, informations.MainInfo.class);
                        startActivity(intent2);
                        return true;
                    case R.id.navMontagne:
                        Intent intent3 = new Intent(MapsActivity.this, informations.MainInfo.class);
                        startActivity(intent3);
                        return true;
                    default:
                        return true;
                }
            }
        });

/*
        mapView = (MapView) this.findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);

        mc = mapView.getController();
        mc.setZoom(17);*/

        actualiseNavigationBar();

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void actualiseNavigationBar () {
        if(iControlCo == 1) {
            View parentLayout = findViewById(R.id.map);
            Snackbar snackbar = Snackbar
                    .make(parentLayout, getString(R.string.bienvenue) +", "+ sNomUtil, Snackbar.LENGTH_LONG);
            snackbar.show();
            navigationView.getMenu().findItem(R.id.itemProfile).setTitle(getString(R.string.bienvenue) +", "+ sNomUtil);
            iControlCo = 0;
        }
        if (bControlLog) {
            navigationView.getMenu().findItem(R.id.navCo).setVisible(false);
            navigationView.getMenu().findItem(R.id.navNew).setVisible(false);
            navigationView.getMenu().findItem(R.id.navDeco).setVisible(true);
            navigationView.getMenu().findItem(R.id.navFav).setVisible(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.recherche);
        SearchView searchView =
                (SearchView) searchItem.getActionView();

        this.menu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fr:
                if (!item.isChecked()) {
                    setLangRecreate("fr");
                    item.setChecked(true);
                    break;
                }
                return true;
            case R.id.en:
                if (!item.isChecked()) {
                    setLangRecreate("en");
                    item.setChecked(true);
                    break;
                }
                return true;
            case R.id.ro:
                if (!item.isChecked()) {
                    setLangRecreate("ro");
                    item.setChecked(true);
                    break;
                }
                return true;
            case R.id.tr:
                if (!item.isChecked()) {
                    setLangRecreate("tr");
                    item.setChecked(true);
                    break;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    public void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == RESULT_Main && resultCode == RESULT_CANCELED)
            finish();
        else
            startup(data);
    }

    private void startup(Intent i)
    {
        // Récupère l'identifiant
        int user = i.getIntExtra("userid", -1);

        //Affiche les identifiants de l'utilisateur
        tv.setText("UserID: " + String.valueOf(user) + " logged in");
    }


    @Override
    public void onMapReady(GoogleMap map) {
        LatLng mapCenter = new LatLng(41.889, -87.622);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 13));

        // Flat markers will rotate when the map is rotated,
        // and change perspective when the map is tilted.
        map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.direction))
                .position(mapCenter)
                .flat(true)
                .rotation(245));

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(mapCenter)
                .zoom(13)
                .bearing(90)
                .build();

        // Animate the change in camera view over 2 seconds
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                2000, null);
    }

    public void setLangRecreate(String langval) {
        Configuration config = getBaseContext().getResources().getConfiguration();
        mLocale = new Locale(langval);
        Locale.setDefault(mLocale);
        config.locale = mLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        recreate();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        actualiseNavigationBar();
    }

    @Override
    public void onResume() {
        super.onResume();
        actualiseNavigationBar();

        //Obtention de la référence du service
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        //Si le GPS est disponible, on s'y abonne
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            abonnementGPS();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();

        //On appelle la méthode pour se désabonner
        desabonnementGPS();
    }

    /**
     * Méthode permettant de s'abonner à la localisation par GPS.
     */
    public void abonnementGPS() {
        //On s'abonne
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, (LocationListener) this);
    }

    public void desabonnementGPS() {
        //Si le GPS est disponible, on s'y abonne
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates((LocationListener) this);
    }

    @Override
    public void onLocationChanged(final Location location) {
        //On affiche dans un Toat la nouvelle Localisation
        final StringBuilder msg = new StringBuilder("lat : ");
        msg.append(location.getLatitude());
        msg.append( "; lng : ");
        msg.append(location.getLongitude());

        Toast.makeText(this, msg.toString(), Toast.LENGTH_SHORT).show();

        //Mise à jour des coordonnées
        final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        marker.setPosition(latLng);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onProviderDisabled(final String provider) {
        //Si le GPS est désactivé on se désabonne
        if("gps".equals(provider)) {
            desabonnementGPS();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onProviderEnabled(final String provider) {
        //Si le GPS est activé on s'abonne
        if("gps".equals(provider)) {
            abonnementGPS();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStatusChanged(final String provider, final int status, final Bundle extras) { }


}
