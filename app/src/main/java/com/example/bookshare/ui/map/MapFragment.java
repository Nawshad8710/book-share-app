package com.example.bookshare.ui.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.bookshare.R;
import com.example.bookshare.SplashActivity;
import com.example.bookshare.UserBooks;
import com.example.bookshare.adapter.ListAdapterMessageList;
import com.example.bookshare.model.GlobalData;
import com.example.bookshare.model.Message;
import com.example.bookshare.model.Request;
import com.example.bookshare.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapViewModel mapViewModel;
    private SupportMapFragment supportMapFragment;
    private GoogleMap gMap;
    private Button btnHideDetail;
    private TextView markerTitle, bookCount, sellBookCount, rentBookCount, btnViewBooks;
    private LinearLayout btnSendMessage, detailLayout, map_section;

    private List<User> userList = new ArrayList<User>();
    private LatLng myLocation = new LatLng(GlobalData.getInstance().userLatitude, GlobalData.getInstance().userLongitude);;
    private Context context;
    private LayoutInflater inflater;
    private static AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private Button btnMsg;
    private EditText etMessage;
    private int requesterId = 0;
    private String requesterUsername = "", requesterFullName = "";
    private ProgressBar pbMsg;

    SharedPreferences preferences;
    public static final String FILE_NAME = "preferenceFile";

    private String url = GlobalData.url;
    private String action = "?action=";
    private String apiName = "";

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    private Location lastKnownLocation;
    private CameraPosition cameraPosition;

    // Keys for storing activity state.
    // [START maps_current_place_state_keys]
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int DEFAULT_ZOOM = 14;

    //private LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapViewModel =
                ViewModelProviders.of(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        //final TextView textView = root.findViewById(R.id.text_share);
        mapViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        //initialize map fragment
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        //async map
        supportMapFragment.getMapAsync(this);

        map_section = (LinearLayout) root.findViewById(R.id.map_section);
        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) map_section.getLayoutParams();
        layoutParams1.setMargins(0, 0, 0, getBoottomNavHeight(root));


        detailLayout = (LinearLayout) root.findViewById(R.id.detailLayout);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) detailLayout.getLayoutParams();
        layoutParams.setMargins(0, 0, 0, getBoottomNavHeight(root));
        Button okButton = new Button(getContext());
        okButton.setText("some text");
        okButton.setVisibility(View.GONE);
        detailLayout.addView(okButton, layoutParams);
        detailLayout.setMinimumHeight(getBoottomNavHeight(root));
        detailLayout.setVisibility(View.GONE);

        markerTitle = (TextView) root.findViewById(R.id.marker_title);
        bookCount = (TextView) root.findViewById(R.id.book_count);
        sellBookCount = (TextView) root.findViewById(R.id.sell_book_count);
        rentBookCount = (TextView) root.findViewById(R.id.rent_book_count);

        btnHideDetail = (Button) root.findViewById(R.id.btn_hide);
        btnHideDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailLayout.setVisibility(View.GONE);
            }
        });

        btnViewBooks = (TextView) root.findViewById(R.id.btn_view_books);
        btnSendMessage = (LinearLayout) root.findViewById(R.id.btn_send_message);

        context = getContext();

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (gMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, gMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        //when map is loaded
        gMap = googleMap;
        myLocation = new LatLng(GlobalData.getInstance().userLatitude, GlobalData.getInstance().userLongitude);

        // Prompt the user for permission.
        getLocationPermission();
        // [END_EXCLUDE]

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        //get user & set them on map
        getUsers(gMap);
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    private void updateLocationUI() {
        if (gMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                gMap.setMyLocationEnabled(true);
                gMap.getUiSettings().setMyLocationButtonEnabled(true);
                gMap.getUiSettings().setZoomControlsEnabled(true);
                gMap.getUiSettings().setCompassEnabled(true);
                gMap.getUiSettings().setAllGesturesEnabled(true);
                gMap.getUiSettings().setMapToolbarEnabled(true);
                gMap.getUiSettings().setRotateGesturesEnabled(true);
                zoomToUserLocation();
            } else {
                gMap.setMyLocationEnabled(false);
                gMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
//                                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                                        new LatLng(lastKnownLocation.getLatitude(),
//                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        myLocation, DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            gMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(myLocation, DEFAULT_ZOOM));
                            gMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }

    }


    private int getBoottomNavHeight(View root){
        View view = getActivity().findViewById(R.id.bottom_nav_view);
        return view.getHeight();
    }

    protected void getUsers(GoogleMap googleMap){
        userList.clear();
        apiName = "api_getData.php";
        String actionN = action+"getUsers";
        String urlN = url+apiName+actionN;

        //System.out.println("--------------------------------------url:"+urlN+"#");
        JsonArrayRequest request = new JsonArrayRequest(com.android.volley.Request.Method.POST, urlN, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //System.out.println("------------------@@@@@@response");

                for(int i = 0; i < response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        User u = new User();

                        u.idUser = Integer.parseInt(object.getString("idUser"));
                        u.username = object.getString("username");

                        if(object.getString("firstName").isEmpty()|| object.getString("firstName").equals("null") || object.getString("firstName")==null){
                            u.firstName = "@";
                        }else {
                            u.firstName = object.getString("firstName");
                        }

                        if(object.getString("lastName").isEmpty()|| object.getString("lastName").equals("null") || object.getString("lastName")==null){
                            u.lastName = "@";
                        }else {
                            u.lastName = object.getString("lastName");
                        }

                        if(object.getString("email").isEmpty()|| object.getString("email").equals("null") || object.getString("email")==null){
                            u.email = "@";
                        }else {
                            u.email = object.getString("email");
                        }

                        u.phone = object.getString("phone");
                        u.gender = Integer.parseInt(object.getString("gender"));
                        u.latitude = Double.parseDouble(object.getString("latitude"));
                        u.longitude = Double.parseDouble(object.getString("longitude"));

                        if(object.getString("total_books").isEmpty()|| object.getString("total_books").equals("null") || object.getString("total_books")==null){
                            u.bookCount = 0;
                        }else {
                            u.bookCount = Integer.parseInt(object.getString("total_books"));
                        }

                        if(object.getString("total_sell_books").isEmpty()|| object.getString("total_sell_books").equals("null") || object.getString("total_sell_books")==null){
                            u.sellBookCount = 0;
                        }else {
                            u.sellBookCount = Integer.parseInt(object.getString("total_sell_books"));
                        }

                        u.rentBookCount = u.bookCount - u.sellBookCount;

                        System.out.println("Latitude:"+u.latitude+"; Longitude:"+u.longitude);

                        userList.add(u);

                    } catch (JSONException e) {
                        Toast.makeText(getContext(),"Exception on loading map data",Toast.LENGTH_LONG).show();
                        //serverError = true;
                        e.printStackTrace();
                    }
                }

                //showing some locations on map
                showUsersOnMap(gMap);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Error loading message data",Toast.LENGTH_LONG).show();
                //serverError = true;
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("myToken", "786");
                params.put("myId", GlobalData.getInstance().userId+"");
                params.put("myLat", GlobalData.getInstance().userLatitude+"");
                params.put("myLong", GlobalData.getInstance().userLongitude+"");

                return params;
            }
        };
        GlobalData.getInstance().addToRequestQueue(request);

    }

    public void showUsersOnMap(GoogleMap googleMap){
        System.out.println("---------------------------------Size:"+userList.size());

        for(User u : userList){
            Marker marker = gMap.addMarker(
                    new MarkerOptions()
                            .position(new LatLng(u.latitude,u.longitude))
                            .title(u.username)
                            .snippet("Books: "+u.bookCount)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarker))
            );
            marker.setTag(u);
            marker.showInfoWindow();
        }


        //zooming the map around my location
        zoomToUserLocation();

        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                markerTitle.setText(marker.getTitle());
                detailLayout.setVisibility(View.VISIBLE);
                final User u = (User) marker.getTag();
                bookCount.setText(u.bookCount+" books listed");
                if(u.bookCount>0){
                    if(u.bookCount==1) {
                        bookCount.setText(u.bookCount+" book listed");
                    }
                    sellBookCount.setVisibility(View.VISIBLE);
                    rentBookCount.setVisibility(View.VISIBLE);
                    btnViewBooks.setVisibility(View.VISIBLE);
                    sellBookCount.setText("Sell books: "+u.sellBookCount+" ; ");
                    rentBookCount.setText("Rent books: "+u.rentBookCount);
                }else {
                    sellBookCount.setVisibility(View.GONE);
                    rentBookCount.setVisibility(View.GONE);
                    btnViewBooks.setVisibility(View.GONE);
                    bookCount.setText("No book listed yet");
                }
                btnViewBooks.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), UserBooks.class);
                        intent.putExtra("user", u);
                        startActivity(intent);
                    }
                });
                btnSendMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createMessageDialog(u);
                    }
                });
                //Toast.makeText(getContext(), marker.getTitle(),Toast.LENGTH_LONG).show();
                return false;
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                //when clicked on map
                //initialize marker options
                //MarkerOptions markerOptions = new MarkerOptions();

                //set position of marker
                //markerOptions.position(myLocation);

                //set title of marker
                //markerOptions.title("Latitude:"+latLng.latitude+" ; Longitude:"+latLng.longitude);

                //remove all marker
                //gMap.clear();

                //add marker on map
                //gMap.addMarker(markerOptions);

                //Animating to zoom the marker
                //gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16));
            }
        });
    }

    private void zoomToUserLocation(){
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                myLocation, 14
        ));
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        }
        else return false;
    }

    public void createMessageDialog(final User u){
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        dialogBuilder = new AlertDialog.Builder(context);
        final View messagePopupView = inflater.inflate(R.layout.send_message_popup, null);
        dialogBuilder.setView(messagePopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        pbMsg = (ProgressBar) messagePopupView.findViewById(R.id.pbMsg);
        pbMsg.setVisibility(View.GONE);

        final LinearLayout msg_body_section = (LinearLayout) messagePopupView.findViewById(R.id.msg_body_section);
        msg_body_section.setVisibility(View.VISIBLE);

        etMessage = (EditText) messagePopupView.findViewById(R.id.etMessage);
        btnMsg = (Button) messagePopupView.findViewById(R.id.btnMsg);
        btnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setCancelable(false);
                pbMsg.setVisibility(View.VISIBLE);
                msg_body_section.setVisibility(View.GONE);
                //increase r value in database table process start
                apiName = "api_insertData.php";
                String actionN = action+"sendMessage";
                String urlN = url+apiName+actionN;

                //System.out.println(">>>>>>>>>>>>>>>>>>>URL= "+urlN);

                StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, urlN, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("yes")){
                            Toast.makeText(context,"Message sent successfully!",Toast.LENGTH_SHORT).show();
                            pbMsg.setVisibility(View.GONE);
                            dialog.dismiss();
                        } else if(response.toString().equals("tokenFail")){
                            Toast.makeText(context,"Wrong token, try again",Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(context,"Error occurred",Toast.LENGTH_SHORT).show();
                            //Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                            //System.out.println(">>>>>>>>>>>>>>>>>>>URL= "+urlN);
                            //Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,"Error occurred!",Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json");
                        params.put("myToken", "786");
                        params.put("fromId",GlobalData.getInstance().userId+"");
                        params.put("fromUsername",GlobalData.getInstance().userName);
                        if(GlobalData.getInstance().userFirstName.equals("@") || GlobalData.getInstance().userLastName.equals("@")){
                            params.put("fromFullName",GlobalData.getInstance().userName);
                        }else {
                            params.put("fromFullName",GlobalData.getInstance().userFullName);
                        }
                        params.put("toId",u.idUser+"");
                        params.put("toUsername",u.username);
                        if(u.firstName.equals("@") || u.lastName.equals("@")){
                            params.put("toFullName",u.username);
                        }else {
                            params.put("toFullName",u.firstName+" "+u.lastName);
                        }
                        params.put("message",etMessage.getText().toString());

                        return params;
                    }
                };

                GlobalData.getInstance().addToRequestQueue(request);
            }
        });
    }
}