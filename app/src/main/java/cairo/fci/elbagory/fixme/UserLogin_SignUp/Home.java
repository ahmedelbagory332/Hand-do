package cairo.fci.elbagory.fixme.UserLogin_SignUp;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;
import com.willowtreeapps.spruce.Spruce;
import com.willowtreeapps.spruce.animation.DefaultAnimations;
import com.willowtreeapps.spruce.sort.DefaultSort;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import cairo.fci.elbagory.fixme.NetworkUtils.NetworkUtils;
import cairo.fci.elbagory.fixme.R;
import cairo.fci.elbagory.fixme.Recycle_View.ChooseSection_Adapter;
import cairo.fci.elbagory.fixme.Settings;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
        ,SearchView.OnQueryTextListener {

    private static final String TAG = Home.class.getSimpleName();
    UserValidation userValidation;

    LocationManager locationManager;
//////////////////
public static final int RequestPermissionCode = 1;

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 100000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 500000;

    private static final int REQUEST_CHECK_SETTINGS = 100;

    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;
    private String KEY ="";
    private DatabaseReference databaseReference;
    ///////////////
    List<String> list= new ArrayList<>();
    RecyclerView recyclerView;
    Animator spruceAnimator;
    ProgressBar progressBar;
    ChooseSection_Adapter adapter;
    TextView textViewinternet;
     LocationManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.inflateMenu(R.menu.home);
        getSupportActionBar().setTitle("Hand do");  // provide compatibility to all the versions

          EnableRuntimePermission();


        textViewinternet = findViewById(R.id.textViewConnection);
        textViewinternet.setVisibility(View.GONE);

        Timer timer = new Timer();
        final int MILLISECONDS = 5000; //5 seconds
        timer.schedule(new CheckConnection(getApplicationContext()), 0, MILLISECONDS);



        userValidation = new UserValidation(this);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        init();


        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users");
        ref.orderByChild("phone").equalTo(userValidation.readphone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        KEY = dataSnapshot1.getKey();

                    }



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        progressBar = (ProgressBar)findViewById(R.id.spin_kit);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);


        manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE );



        recyclerView = findViewById(R.id._recycle);



          adapter = new ChooseSection_Adapter(list,getApplicationContext());

        //recyclerView animation
        final GridLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(),2) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                super.onLayoutChildren(recycler, state);
                initSpruce();
            }
        };

        databaseReference.child("ChooseSection").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String model =dataSnapshot1.child("section").getValue(String.class);
                    list.add(model);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    recyclerView.setLayoutManager(linearLayoutManager);


                }

                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });





            if (userValidation.ReadTapTargetSequence()) {

                new TapTargetSequence(this)
                        .targets(
                                TapTarget.forToolbarMenuItem(toolbar, R.id.action_search, "ايقونة البحث", "تستطيع ان تبحث عن الاقسام")
                                        .outerCircleColor(R.color.colorPrimaryDark)      // Specify a color for the outer circle
                                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                        .targetCircleColor(R.color.colorwhite)   // Specify a color for the target circle
                                        .titleTextSize(30)                  // Specify the size (in sp) of the title text
                                        .titleTextColor(R.color.colorPrimaryDark)      // Specify the color of the title text
                                        .descriptionTextSize(20)            // Specify the size (in sp) of the description text
                                        .descriptionTextColor(R.color.colorwhite)  // Specify the color of the description text
                                        .textColor(R.color.colorwhite)            // Specify a color for both the title and description text
                                        .textTypeface(Typeface.MONOSPACE)  // Specify a typeface for the text
                                        .dimColor(R.color.colorPrimaryDark)            // If set, will dim behind the view with 30% opacity of the given color
                                        .drawShadow(true)
                                        .cancelable(false),
                                TapTarget.forToolbarNavigationIcon(toolbar, "ايقونة الخيارات", "اضغط لرؤيه الخيارات")
                                        .outerCircleColor(R.color.colorPrimaryDark)      // Specify a color for the outer circle
                                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                        .targetCircleColor(R.color.colorwhite)   // Specify a color for the target circle
                                        .titleTextSize(30)                  // Specify the size (in sp) of the title text
                                        .titleTextColor(R.color.colorPrimaryDark)      // Specify the color of the title text
                                        .descriptionTextSize(20)            // Specify the size (in sp) of the description text
                                        .descriptionTextColor(R.color.colorwhite)  // Specify the color of the description text
                                        .textColor(R.color.colorwhite)            // Specify a color for both the title and description text
                                        .textTypeface(Typeface.MONOSPACE)  // Specify a typeface for the text
                                        .dimColor(R.color.colorPrimaryDark)            // If set, will dim behind the view with 30% opacity of the given color
                                        .drawShadow(true)
                                        .cancelable(false))
                        .listener(new TapTargetSequence.Listener() {
                            @Override
                            public void onSequenceFinish() {

                            }

                            @Override
                            public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                            }

                            @Override
                            public void onSequenceCanceled(TapTarget lastTarget) {

                            }
                        }).start();

                userValidation.writeTapTargetSequence(false);
            }


    }




    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        if (spruceAnimator != null) {
            spruceAnimator.start();
            progressBar.setVisibility(View.GONE);
        }

    }

    private void initSpruce() {
        spruceAnimator = new Spruce.SpruceBuilder(recyclerView)
                .sortWith(new DefaultSort(100))
                .animateWith(DefaultAnimations.shrinkAnimator(recyclerView, 800),
                        ObjectAnimator.ofFloat(recyclerView, "translationX", -recyclerView.getWidth(), 0f).setDuration(800))
                .start();
    }

    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();


                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }
    private void updateLocationUI() {

        if (NetworkUtils.isNetworkAvailable(this)) {
            if (mCurrentLocation != null) {


              //  Toast.makeText(this, "Lat: " + mCurrentLocation.getLatitude() + ", " +
                   //     "Lng: " + mCurrentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                databaseReference.child("Users").child(KEY).child("langtit").setValue(mCurrentLocation.getLongitude());
                databaseReference.child("Users").child(KEY).child("litit").setValue(mCurrentLocation.getLatitude());

                TastyToast.makeText(getApplicationContext(), "تم تحديد موقعك بنجاح يمكنك الان ان تغلق GPS",
                        TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);



                // giving a blink animation on TextView


            }
        }
        else {
            TastyToast.makeText(getApplicationContext(), "تاكد من اتصالك بالانترنت",
                    TastyToast.LENGTH_SHORT, TastyToast.ERROR);


        }
    }

    public void showLastKnownLocation() {
        if (mCurrentLocation != null) {
           // Toast.makeText(getApplicationContext(), "Lat: " + mCurrentLocation.getLatitude()
               //     + ", Lng: " + mCurrentLocation.getLongitude(), Toast.LENGTH_LONG).show();


            databaseReference.child("Users").child(KEY).child("langtit").setValue(mCurrentLocation.getLongitude());
            databaseReference.child("Users").child(KEY).child("litit").setValue(mCurrentLocation.getLatitude());

            TastyToast.makeText(getApplicationContext(), "تم تحديد موقعك بنجاح يمكنك الان ان تغلق GPS",
                    TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);



        } else {
          //  TastyToast.makeText(getApplicationContext(), "حدث خطأ ما",
                 //   TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            // Toast.makeText(getApplicationContext(), "Last known location is not available!", Toast.LENGTH_SHORT).show();

        }
    }

    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                       // Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        TastyToast.makeText(getApplicationContext(), "تأكد من اعدادات تحديد الموقع",
                                   TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(Home.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(Home.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.choose) {
          // do nothing
       } else if (id == R.id.location) {


            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ){
                TastyToast.makeText(getApplicationContext(), "من فضلك افتح GPS",
                        TastyToast.LENGTH_SHORT, TastyToast.ERROR);
              }
            else {
                TastyToast.makeText(getApplicationContext(), "جارى تحديد موقعك",
                        TastyToast.LENGTH_SHORT, TastyToast.INFO);
                startLocationUpdates();

                //  showLastKnownLocation();
            }
        } else if (id == R.id.subscribe) {
            String uri = "";
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND, Uri.parse(uri));
            startActivity(Intent.createChooser(sharingIntent, "مشاركه مع"));

        } else if (id == R.id.share) {

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "صلحنى\n";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "سجل و ابحث عن عاملك المفضل");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "مشاركه مع"));

        } else if (id == R.id.rate) {
            try {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            } catch (ActivityNotFoundException e) {
                Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName());
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            }

        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(getApplicationContext(), Settings.class));


        } else if (id == R.id.logout) {
            startActivity(new Intent(getApplicationContext(), Phone_Verification.class));
            userValidation.writeLoginStatus(false);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





    private void EnableRuntimePermission() {


        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CALL_PHONE)) {


        } else {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CALL_PHONE}, RequestPermissionCode);

        }


        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {


        } else {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, RequestPermissionCode);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =  getMenuInflater();
        inflater.inflate(R.menu.home, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);


        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        String userinput = newText.toLowerCase();
        List<String> newlist= new ArrayList<>();
        for (String search : list)
        {
            if (search.contains(userinput)) {

                newlist.add(search);
            }
        }
        adapter.search(newlist);
        return true;
    }

    public class CheckConnection extends TimerTask {
        private Context context;
        public CheckConnection(Context context){
            this.context = context;
        }
        public void run() {
            if(NetworkUtils.isNetworkAvailable(context)){
                //CONNECTED
                runOnUiThread(new Runnable() {
                    public void run() {
                        textViewinternet.setVisibility(View.GONE);

                    }
                });



            }else {
                //DISCONNECTED
                runOnUiThread(new Runnable() {
                    public void run() {
                        textViewinternet.setVisibility(View.VISIBLE);

                    }
                });
            }
        }
    }
}
