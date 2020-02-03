package cairo.fci.elbagory.fixme;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.eftimoff.androipathview.PathView;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cairo.fci.elbagory.fixme.NetworkUtils.NetworkUtils;
import cairo.fci.elbagory.fixme.UserLogin_SignUp.Home;
import cairo.fci.elbagory.fixme.UserLogin_SignUp.Phone_Verification;
import cairo.fci.elbagory.fixme.UserLogin_SignUp.UserValidation;
import de.hdodenhof.circleimageview.CircleImageView;


public class Settings extends AppCompatActivity {
    AlertDialog.Builder dialogBuilder;
    AlertDialog alertDialog;
    UserValidation userValidation;
    private DatabaseReference databaseReference;
    private String KEY ="";
    DatabaseReference ref;
    TextView textViewinternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        userValidation = new UserValidation(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setTitle("Hand do");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

         ref= FirebaseDatabase.getInstance().getReference().child("Users");

        textViewinternet = findViewById(R.id.textViewConnectionSetting);
        textViewinternet.setVisibility(View.GONE);

        Timer timer = new Timer();
        final int MILLISECONDS = 5000; //5 seconds
        timer.schedule(new CheckConnection(getApplicationContext()), 0, MILLISECONDS);

        final PathView pathView = (PathView) findViewById(R.id.pathView);
        final LinearLayout linearLayout = findViewById(R.id.linear_setting);
        linearLayout.setVisibility(View.VISIBLE);

        pathView.useNaturalColors();
        pathView.setFillAfter(true);
        pathView.getPathAnimator()
                .delay(100)
                .duration(3000)
                .interpolator(new AccelerateDecelerateInterpolator())
                .start();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    Thread.sleep(4000);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            // Stuff that updates the UI
                            linearLayout.setVisibility(View.GONE);

                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

        ListView listView = findViewById(R.id.list_dynamic);
        ArrayList< Lists> itme = new ArrayList<>();
        itme.add(new  Lists("حدث التطبيق", R.drawable.update));
        itme.add(new  Lists("تغير رقم الهاتف", R.drawable.ic_iconfinder_mobile));
        itme.add(new Lists("حذف الحساب", R.drawable.delete));

        Adapter adapter = new  Adapter(itme);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                switch (position)
                {
                    case 0:

                        try {
                            Uri uri = Uri.parse("market://details?id=" + getPackageName());
                            Intent i = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(i);
                        } catch (ActivityNotFoundException e) {
                            Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName());
                            Intent i = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(i);
                        }

                        break;
                    case 1:
                        if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {

                            ref.orderByChild("phone").equalTo(userValidation.readphone()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {

                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                         KEY = dataSnapshot1.getKey();

                                    }

                                     showAlertDialog(R.layout.change_number,KEY);


                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        }
                        else
                        {
                            TastyToast.makeText(getApplicationContext(), "تاكد من اتصالك بالانترنت",
                                    TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        }
                        break;
                    case 2:

                        if(NetworkUtils.isNetworkAvailable(getApplicationContext())) {

                            ref.orderByChild("phone").equalTo(userValidation.readphone()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {

                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                            KEY = dataSnapshot1.getKey();

                                        }

                                        databaseReference.child("Users").child(KEY).child("phone").removeValue();
                                        databaseReference.child("Users").child(KEY).child("langtit").removeValue();
                                        databaseReference.child("Users").child(KEY).child("litit").removeValue();
                                        userValidation.writephone("");
                                        userValidation.writeLoginStatus(false);
                                        startActivity(new Intent(getApplicationContext(), Phone_Verification.class));
                                        TastyToast.makeText(getApplicationContext(), "تم حذف الحساب بنجاح",
                                                TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);


                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                        else
                        {
                            TastyToast.makeText(getApplicationContext(), "تاكد من اتصالك بالانترنت",
                                    TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        }
                        break;
                }
            }
        });
    }


    class Lists{

        String option ;
        int img;

        public Lists(String option, int img) {
            this.option = option;
            this.img = img;
        }
    }

    class Adapter extends BaseAdapter {

        ArrayList< Lists> itme = new ArrayList<>();

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
            View view1 = layoutInflater.inflate(R.layout.row_item_setting,null);

            TextView textView = view1.findViewById(R.id.option);
            ImageView imageView = view1.findViewById(R.id.image_setting);


            textView.setText(itme.get(i).option);
            imageView.setImageResource(itme.get(i).img);


            return view1;

        }
    }

    private void showAlertDialog(int layout , String key){

        dialogBuilder = new AlertDialog.Builder(this);
        final View layoutView = getLayoutInflater().inflate(layout, null);
        final EditText newNumber = layoutView.findViewById(R.id._newnumber);
        final Button Ok = layoutView.findViewById(R.id._change);



        dialogBuilder.setView(layoutView);
        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Phone = newNumber.getText().toString();

                if (Phone.equals("") || Phone.length() < 11) {
                    TastyToast.makeText(getApplicationContext(), "تأكد من رقم الهاتف",
                            TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    newNumber.setText("");
                }
                else {
                    databaseReference.child("Users").child(KEY).child("phone").setValue(newNumber.getText().toString());
                    TastyToast.makeText(getApplicationContext(), "تم تغير رقم الهاتف بنجاح",
                            TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                    userValidation.writephone(newNumber.getText().toString());
                    alertDialog.cancel();

                }
            }
        });



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
