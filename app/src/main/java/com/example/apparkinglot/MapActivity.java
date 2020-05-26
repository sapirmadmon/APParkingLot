package com.example.apparkinglot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.apparkinglot.logic.Boundaries.User.UserBoundary;
import com.example.apparkinglot.logic.Boundaries.User.UserIdBoundary;
import com.example.apparkinglot.logic.Boundaries.User.UserRole;
import com.example.apparkinglot.logic.JsonPlaceHolderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    GoogleMap mapAPI;
    SupportMapFragment mapFragment;

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    private Button bUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

        String email = LoginActivity.email.getText().toString();
        String domain = LoginActivity.domain.getText().toString();

        Log.d("TEST", "*******#####" + email + " " + domain + "#######*******");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.14.20:8092/acs/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        bUpdate = findViewById(R.id.bottomUpdateDetails);

        if(bUpdate == null)
            Log.d("ERROR NULL", "&&&&&&&&&&&&&&&&&&&&7777");

        bUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getSupportFragmentManager();
                UpdateDetailsFragment fragment = new UpdateDetailsFragment();

                fm.beginTransaction().add(R.id.container1 , fragment).commit();
            }
        });

        //        mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.mapAPI);
//
//        mapFragment.getMapAsync(this);


    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude()
                            + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();


                    //calls to Actions
                    findViewById(R.id.bottomParkAction).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("CURRENT_LOCATION", "**********lat: " + currentLocation.getLatitude() + " lng: " + currentLocation.getLongitude() + "**********");
                            double lat = currentLocation.getLatitude();
                            double lng = currentLocation.getLongitude();

                            LatLng park = new LatLng(lat, lng);
                            mapAPI.addMarker(new MarkerOptions().position(park).title("parking Caught ").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                            mapAPI.moveCamera(CameraUpdateFactory.newLatLng(park));

                        }
                    });

                    findViewById(R.id.bottomReleaseAction).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("CURRENT_LOCATION", "**********lat: " + currentLocation.getLatitude() + " lng: " + currentLocation.getLongitude() + "**********");
                        }
                    });

                    findViewById(R.id.bottomSearchAction).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("CURRENT_LOCATION", "**********lat: " + currentLocation.getLatitude() + " lng: " + currentLocation.getLongitude() + "**********");
                        }
                    });


//                    findViewById(R.id.bottomUpdateDetails).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Log.d("UPDATE", "*********UPDATE**********");
//
//                            //TODO update Details
//                            updateDetails("2020b.tamir.reznik", "sapir@gmail.com");
//
//                        }
//                    });
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.mapAPI);
                    supportMapFragment.getMapAsync(MapActivity.this);
                }
            }
        });
    }

    private void updateDetails(String userDomain, String userEmail) {
        UserBoundary user = new UserBoundary(new UserIdBoundary(userDomain, userEmail), UserRole.MANAGER, "tamir", ":>");

        jsonPlaceHolderApi.updateUserDetails(userDomain, userEmail, user);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapAPI = googleMap;

        LatLng prak2 = new LatLng(32.114892, 34.818029);
        mapAPI.addMarker(new MarkerOptions().position(prak2).title("parking Caught").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        mapAPI.moveCamera(CameraUpdateFactory.newLatLng(prak2));

        LatLng park3 = new LatLng(32.116430, 34.818353);
        mapAPI.addMarker(new MarkerOptions().position(park3).title("parking Caught").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        mapAPI.moveCamera(CameraUpdateFactory.newLatLng(park3));

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                .title("I'm here!");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        googleMap.addMarker(markerOptions);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                }
                break;
        }
    }



    //    private void createElement() {
//
//        String type = "parking";
//        String name = "number car";
//
//        String email = LoginActivity.email.getText().toString();
//        String domain = LoginActivity.domain.getText().toString();
//
//        UserIdBoundary userId = new UserIdBoundary(domain, email);
//        Location latLng = new Location(currentLocation.getLongitude(), currentLocation.getLatitude());
//
//        Log.d("TEST", "#####" + email + " " + domain + "#######");
//
//        ElementBoundary elementBoundary = new ElementBoundary(null, type, name, Boolean.FALSE, null, latLng, null, userId);
//
//        Call<UserBoundary> call = jsonPlaceHolderApi.CreateNewElement(elementBoundary);
//
//        call.enqueue(new Callback<ElementBoundary>() {
//            @Override
//            public void onResponse(Call<ElementBoundary> call, Response<ElementBoundary> response) {
//
//                if(!response.isSuccessful()) {
//                    textViewResult.setText("code: "+ response.code());
//                    return;
//                }
//
//                ElementBoundary ElementBoundaryResponse = response.body();
//                String  content="";
//                content += "code: " + response.code() + "\n";
//                content += "domain: " + ElementBoundaryResponse.getElementId().getDomain() + "\n";
//                content += "id: " + ElementBoundaryResponse.getElementId().getId()+ "\n";
//                content += "type: " + ElementBoundaryResponse.getType() + "\n";
//                content += "name: " + ElementBoundaryResponse.getName() + "\n";
//                content += "active: " + ElementBoundaryResponse.getActive() + "\n";
//                content += "time: " + ElementBoundaryResponse.getCreatedTimestamp()+ "\n";
//                content += "createdBy: " + ElementBoundaryResponse.getCreateBy().values() + "\n";
//                content += "location: " + ElementBoundaryResponse.getLocation().getLat() + "," + ElementBoundaryResponse.getLocation().getLng() + "\n";
//
//                Log.d("ELEMENT BOUNDARY", content);
//                //textViewResult.setText(content);
//            }
//
//            @Override
//            public void onFailure(Call<ElementBoundary> call, Throwable t) {
//               // textViewResult.setText(t.getMessage());
//                Log.d("ON FAILURE", t.getMessage());
//            }
//        });
//    }

}
