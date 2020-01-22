package com.example.projectz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.Inflater;

public class MapActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<List<Locations>>{

    public static final String LOG_TAG = MapActivity.class.getName();
    private static final String URL="https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    //    private static final String URL="";
    private static final int LOCATION_LOADER_ID = 1;
    private static double userLat=151.2073788;
    private static double userLong=-33.8658185;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        permissionRequest();
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        //Toast.makeText(this,"main",//Toast.LENGTH_LONG);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        TextView noNetTV = (TextView)findViewById(R.id.noNetTV);
        if(!isConnected) {
            ProgressBar pb=(ProgressBar) findViewById(R.id.progressBar);
            TextView loadingTV=(TextView)findViewById(R.id.loadingTV);
            pb.setVisibility(View.GONE);
            loadingTV.setVisibility(View.GONE);
            //Toast.makeText(this,"NOT Connected",//Toast.LENGTH_LONG).show();

        }
        else {
            noNetTV.setVisibility(View.GONE);
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(LOCATION_LOADER_ID, null, this);
        }
    }

        @Override
        public Loader<List<Locations>> onCreateLoader(int i, Bundle bundle) {
            String finalUrl=URL+"?"+"type=hospital"+"&key="+getString(R.string.google_maps_key)+"&radius=1500"+"&location="+userLong+","+userLat;
            //Toast.makeText(this,"Uri bulit",//Toast.LENGTH_LONG).show();
            return new LocationAsyncTaskLoader(MapActivity.this,finalUrl);
        }

        @Override
        public void onLoadFinished(android.content.Loader<List<Locations>> loader, List<Locations> locat) {
            ProgressBar pb=(ProgressBar) findViewById(R.id.progressBar);
            TextView loadingTV=(TextView)findViewById(R.id.loadingTV);
            pb.setVisibility(View.GONE);
            loadingTV.setVisibility(View.GONE);
            TextView test=findViewById(R.id.locations);
            ArrayList<Locations> data=new ArrayList<>(locat.size());
            data.addAll(locat);
            Bundle bundle = new Bundle();
            bundle.putSerializable("key", data);
            Fragment myFrag=new MapFrag();
            myFrag.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.mapFrag,myFrag).commit();
        }


        @Override
        public void onLoaderReset(android.content.Loader<List<Locations>> loader) {
            //TODO:finish
        }

        private void permissionRequest() {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(this, new String[] {
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION },
                        1);
            }
        }
}
