package cairo.fci.elbagory.fixme;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.List;

import cairo.fci.elbagory.fixme.Models.GPS_Models;
import cairo.fci.elbagory.fixme.Models.Workers_Models;
import cairo.fci.elbagory.fixme.UserLogin_SignUp.UserValidation;
import de.hdodenhof.circleimageview.CircleImageView;


public class Worker_Profile extends AppCompatActivity {
    public static final int RequestPermissionCode = 1;

    private static UserValidation userValidation;
    DatabaseReference databaseReference;
    private CircleImageView circleImageView;
    private TextView name;
    private TextView phone;
    private ProgressDialog dialog;
    AlertDialog.Builder dialogBuilder;
    AlertDialog alertDialog;
    List<Workers_Models> list = new ArrayList<>();
    Double getLongitude, getLatitude;
    private FusedLocationProviderClient client;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker__profile);


        EnableRuntimePermission();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        client = LocationServices.getFusedLocationProviderClient(this);


        if (isNetworkAvailable()) {

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                if (ActivityCompat.checkSelfPermission(Worker_Profile.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(Worker_Profile.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                client.getLastLocation().addOnSuccessListener(Worker_Profile.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            getLatitude = location.getLatitude();
                            getLongitude = location.getLongitude();
                            Toast.makeText(Worker_Profile.this, "" +
                                    getLatitude
                                    +
                                    getLongitude, Toast.LENGTH_SHORT).show();

                        } else {
                            TastyToast.makeText(getApplicationContext(), "حدث خطأ عند تحديد موقع الرجاء المحاوله مرة اخرى",
                                    TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        }

                    }
                });
            } else {
                TastyToast.makeText(getApplicationContext(), "من فضلك افتح GPS..",
                        TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }

        } else {
            TastyToast.makeText(getApplicationContext(), "تاكد من اتصالك بالانترنت",
                    TastyToast.LENGTH_SHORT, TastyToast.ERROR);
        }


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        userValidation = new UserValidation(this);

//to load worker info
        Query query = databaseReference.child("Workers").orderByChild("phone").equalTo(userValidation.readphone());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                list.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    Workers_Models model = dataSnapshot1.getValue(Workers_Models.class);
                    list.add(model);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                TastyToast.makeText(getApplicationContext(), "تأكد من اتصالك بالانترنت",
                        TastyToast.LENGTH_SHORT, TastyToast.ERROR);

            }
        });


        ListView listView = findViewById(R.id.pro_list);
        ArrayList<Lists> itme = new ArrayList<>();
        itme.add(new Lists("الملف الشخصى", R.drawable.account));
        itme.add(new Lists("تحديد موقعك", R.drawable.jpsprofile));
        itme.add(new Lists("الدعم", R.drawable.help));
        itme.add(new Lists("الخروج", R.drawable.exit));

        Adapter adapter = new Adapter(itme);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:

                        showAlertDialog(R.layout.activity_worker, "", list.get(position).getName());

                        break;
                    case 1:
                        if (isNetworkAvailable()) {
                            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                                Query query = databaseReference.child("GPS").orderByChild("phone").equalTo("01237947122");
                                query.addValueEventListener(new ValueEventListener() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                            GPS_Models gps_models = dataSnapshot1.getValue(GPS_Models.class);

                                            if (gps_models != null) {

                                                Double lit = gps_models.getLititud();
                                                Double logit = gps_models.getLOngtitud();


                                                String uri = "http://maps.google.com/maps?f=d&hl=en&saddr=" + getLatitude + "," + getLongitude + "&daddr=" + lit + "," + logit;

                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                                startActivity(intent.createChooser(intent, "Select app"));
                                            } else
                                                TastyToast.makeText(getApplicationContext(), "تأكد من اتصالك بالانترنت",
                                                        TastyToast.LENGTH_SHORT, TastyToast.ERROR);

                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                        TastyToast.makeText(getApplicationContext(), "تأكد من اتصالك بالانترنت",
                                                TastyToast.LENGTH_SHORT, TastyToast.ERROR);

                                    }
                                });

                            } else {
                                TastyToast.makeText(getApplicationContext(), "من فضلك افتح GPS..",
                                        TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                            }
                        } else {
                            TastyToast.makeText(getApplicationContext(), "تأكد من اتصالك بالانترنت",
                                    TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        }
                        break;
                    case 2:

                        String number = "23454568678";
                        Intent call = new Intent(Intent.ACTION_CALL);
                        call.setData(Uri.parse("tel:" + number));
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(call);


                        break;
                    case 3:


                        break;
                }
            }
        });

        circleImageView = findViewById(R.id.profile_image_worker);
        name = findViewById(R.id.workerprofile_name);
        phone = findViewById(R.id.worker_sec);

        dialog = new ProgressDialog(this);


        Query query1 = databaseReference.child("Workers").orderByChild("phone").equalTo(userValidation.readphone());

        dialog.setMessage("جارى تحميل بياناتك....");
        dialog.show();
        query1.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    Workers_Models workers_models = dataSnapshot1.getValue(Workers_Models.class);

                    if (workers_models != null) {


                        name.setText(workers_models.getName());
                        phone.setText(workers_models.getPhone());


                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load(workers_models.getImg())
                                .fitCenter()
                                .placeholder(R.drawable.ic_doctor_placeholder)
                                .into(circleImageView);

                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                    } else
                        TastyToast.makeText(getApplicationContext(), "تأكد من اتصالك بالانترنت",
                                TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                TastyToast.makeText(getApplicationContext(), "تأكد من اتصالك بالانترنت",
                        TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

            }
        });


    }


    class Lists {

        String option;
        int img;

        public Lists(String option, int img) {
            this.option = option;
            this.img = img;
        }
    }

    class Adapter extends BaseAdapter {

        ArrayList<Lists> itme = new ArrayList<>();

        public Adapter(ArrayList<Lists> itme) {
            this.itme = itme;
        }

        @Override
        public int getCount() {
            return itme.size();
        }

        @Override
        public Object getItem(int position) {
            return itme.get(position).option;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {

            LayoutInflater layoutInflater = getLayoutInflater();
            View view1 = layoutInflater.inflate(R.layout.row_item_worker_profile, null);

            TextView textView = view1.findViewById(R.id.option);
            ImageView imageView = view1.findViewById(R.id.profile_image_option);


            textView.setText(itme.get(i).option);
            imageView.setImageResource(itme.get(i).img);


            return view1;

        }
    }

    private void showAlertDialog(int layout, String section, String name) {

        dialogBuilder = new AlertDialog.Builder(Worker_Profile.this);
        final View layoutView = getLayoutInflater().inflate(layout, null);
        Button reservation = layoutView.findViewById(R.id._reservation);
        final Button follow = layoutView.findViewById(R.id._follow);
        final CircleImageView circleImageView = layoutView.findViewById(R.id.workser_image);
        final TextView info = layoutView.findViewById(R.id._workerinfo_);


        dialogBuilder.setView(layoutView);
        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        Query query = databaseReference.child("Workers").orderByChild("name").equalTo(name);


        // shoW worker info & get the last Number Of rate & get the key of worker to update his info
        dialog.setMessage("جارى تحميل بيانات العامل....");
        dialog.show();
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Workers_Models workers_models = dataSnapshot1.getValue(Workers_Models.class);

                    if (workers_models != null) {

                        if (workers_models.getRate() == 0) {
                            info.setText(workers_models.getName() + "\n" + "من قسم " + workers_models.getSection() +
                                    "\n" + "لم يتم التوصيه من قبل أحد");
                        } else if (workers_models.getRate() == 1) {
                            info.setText(workers_models.getName() + "\n" + "من قسم " + workers_models.getSection() +
                                    "\n" + "تم التوصيه من قبل شخص");
                        } else if (workers_models.getRate() == 2) {
                            info.setText(workers_models.getName() + "\n" + "من قسم " + workers_models.getSection() +
                                    "\n" + "تم التوصيه من قبل شخصان");
                        } else if (workers_models.getRate() == 3 || workers_models.getRate() == 4 || workers_models.getRate() == 5
                                || workers_models.getRate() == 6 || workers_models.getRate() == 7 || workers_models.getRate() == 8
                                || workers_models.getRate() == 9 || workers_models.getRate() == 10) {
                            info.setText(workers_models.getName() + "\n" + "من قسم " + workers_models.getSection() +
                                    "\n" +
                                    "تم التوصيه من قبل " + workers_models.getRate() + " أشخاص");
                        } else {
                            info.setText(workers_models.getName() + "\n" + "من قسم " + workers_models.getSection() +
                                    "\n" +
                                    "تم التوصيه من قبل " + workers_models.getRate() + " شخص");
                        }

                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load(workers_models.getImg())
                                .fitCenter()
                                .placeholder(R.drawable.ic_doctor_placeholder)
                                .into(circleImageView);


                        follow.setEnabled(false);

                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                    } else
                        TastyToast.makeText(getApplicationContext(), "تأكد من اتصالك بالانترنت",
                                TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                TastyToast.makeText(getApplicationContext(), "تأكد من اتصالك بالانترنت",
                        TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        reservation.setEnabled(false);

    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void EnableRuntimePermission() {


        if (ActivityCompat.shouldShowRequestPermissionRationale(Worker_Profile.this,
                Manifest.permission.CALL_PHONE)) {


        } else {

            ActivityCompat.requestPermissions(Worker_Profile.this, new String[]{
                    Manifest.permission.CALL_PHONE}, RequestPermissionCode);

        }


        if (ActivityCompat.shouldShowRequestPermissionRationale(Worker_Profile.this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {


        } else {

            ActivityCompat.requestPermissions(Worker_Profile.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, RequestPermissionCode);

        }
    }

    private void get(LocationManager locationManager) {

        if (isNetworkAvailable()) {

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                if (ActivityCompat.checkSelfPermission(Worker_Profile.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(Worker_Profile.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                client.getLastLocation().addOnSuccessListener(Worker_Profile.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            getLatitude = location.getLatitude();
                            getLongitude = location.getLongitude();
                            Toast.makeText(Worker_Profile.this, "" +
                                    getLatitude
                                    +
                                    getLongitude, Toast.LENGTH_SHORT).show();

                        } else {
                            TastyToast.makeText(getApplicationContext(), "حدث خطأ عند تحديد موقع الرجاء المحاوله مرة اخرى",
                                    TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        }

                    }
                });
            } else {
                TastyToast.makeText(getApplicationContext(), "من فضلك افتح GPS..",
                        TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }

        } else {
            TastyToast.makeText(getApplicationContext(), "تاكد من اتصالك بالانترنت",
                    TastyToast.LENGTH_SHORT, TastyToast.ERROR);
        }


    }

}
