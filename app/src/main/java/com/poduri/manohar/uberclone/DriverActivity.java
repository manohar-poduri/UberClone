package com.poduri.manohar.uberclone;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class DriverActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnRequests;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ListView listView;
    private ArrayList<String> nearByDriveRequest;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        btnRequests = findViewById(R.id.btnRequests);
        btnRequests.setOnClickListener(this);

        listView = findViewById(R.id.requestListView);
        nearByDriveRequest = new ArrayList<>();
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, nearByDriveRequest);

        listView.setAdapter(adapter);

        nearByDriveRequest.clear();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.driver_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.driverLogoutItem) {

            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        finish();
                    }
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                updateRequestListView(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            Location currentDriverLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            updateRequestListView(currentDriverLocation);

        } else if (Build.VERSION.SDK_INT >= 23) {

            if (ContextCompat.checkSelfPermission(DriverActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(DriverActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);

            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location currentDriverLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                updateRequestListView(currentDriverLocation);
            }
        }
    }

    private void updateRequestListView(Location driverLocation) {

        if (driverLocation != null) {

            nearByDriveRequest.clear();

            final ParseGeoPoint driverCurrentLocation = new ParseGeoPoint(driverLocation.getLatitude(), driverLocation.getLongitude());

            ParseQuery<ParseObject> requestCarQuery = ParseQuery.getQuery("RequestCar");
            requestCarQuery.whereNear("passengerLocation", driverCurrentLocation);

            requestCarQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null) {
                        if (objects.size() > 0) {
                            for (ParseObject nearRequest : objects) {

                                Double mileDistanceToPassenger = driverCurrentLocation.distanceInMilesTo((ParseGeoPoint) nearRequest.get("passengerLocation"));

                                float roundedDistanceValue = Math.round(mileDistanceToPassenger * 10) / 10;
                                nearByDriveRequest.add("There is " + roundedDistanceValue + " miles" + nearRequest.get("username"));
                            }

                        } else {
                            Toast.makeText(DriverActivity.this,"Sorry there are no requests...",Toast.LENGTH_LONG).show();
                        }
                        adapter.notifyDataSetChanged();

                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1000 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


            if (ContextCompat.checkSelfPermission(DriverActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location currentDriverLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                updateRequestListView(currentDriverLocation);
            }

        }

    }
}
