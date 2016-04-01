package main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.support.design.widget.NavigationView;
import android.widget.Toast;
import java.util.Locale;
import main.maps.R;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DrawerLayout mDrawerLayout;
    private GoogleApiClient mGoogleApiClient;
    Locale mLocale;
    Configuration config;
    private LocationManager lm;
    private double latitude;
    private double longitude;
    private double altitude;
    private float accuracy;
    private MapView mapView;

    private GoogleApiClient client;

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

        final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav1:
                        Intent intent = new Intent(MapsActivity.this, utilisateur.MainConnexion.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav2:
                        Intent intent1 = new Intent(MapsActivity.this, utilisateur.MainNouveau.class);
                        startActivity(intent1);
                        return true;
                    case R.id.nav3:
                        Intent intent2 = new Intent(MapsActivity.this, informations.MainInfo.class);
                        startActivity(intent2);
                        return true;
                    case R.id.nav4:
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

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        MenuItem m2;
        switch (item.getItemId()) {
            case R.id.fr:
                if (!item.isChecked()) {
                    setLangRecreate("fr");
                    break;
                }
                return true;
            case R.id.en:
                if (!item.isChecked()) {
                    setLangRecreate("en");
                    break;
                }
                return true;
            case R.id.ro:
                if (!item.isChecked()) {
                    setLangRecreate("ro");
                    break;
                }
                return true;
            case R.id.tr:
                if (!item.isChecked()) {
                    setLangRecreate("tr");
                    break;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.setMyLocationEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        // Add a marker in Sydney and move the camera
        LatLng lieuAc = new LatLng(longitude, latitude);
        mMap.addMarker(new MarkerOptions().position(lieuAc).title("Lieu Actuel"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lieuAc));
    }*/

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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.recherche);
        SearchView searchView =
                (SearchView) searchItem.getActionView();

        return super.onCreateOptionsMenu(menu);
    }

    public void setLangRecreate(String langval) {
        Configuration config = getBaseContext().getResources().getConfiguration();
        mLocale = new Locale(langval);
        Locale.setDefault(mLocale);
        config.locale = mLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        recreate();
    }

}
