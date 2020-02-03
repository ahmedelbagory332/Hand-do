package cairo.fci.elbagory.fixme.UserLogin_SignUp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.Random;

import cairo.fci.elbagory.fixme.Models.Users_Models;
import cairo.fci.elbagory.fixme.Models.Workers_Models;
import cairo.fci.elbagory.fixme.R;

public class Phone_Verification extends AppCompatActivity {

    private EditText phone , code;
    private Button sendcode , signup , Resend;
    private static int confirm_code=-1;
    private static int Reconfirm_code=-1;
    private Random rand;
    private UserValidation userValidation;
    private DatabaseReference databaseReference;
    private static Animation shakeAnimation;
    LinearLayout linearLayout;
    TextView texterror,login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone__verification);



        phone =findViewById(R.id.phonenum);
        code =findViewById(R.id.code);
        sendcode =findViewById(R.id._code);
        signup =findViewById(R.id._sign);
        login =findViewById(R.id._login);
        Resend =findViewById(R.id.resendcode);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        userValidation = new UserValidation(this);

        linearLayout = findViewById(R.id.user_info);
          texterror = findViewById(R.id.error);


        rand = new Random();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);

            }
        });

        Resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                confirm_code = rand.nextInt(999);
                Reconfirm_code = confirm_code;
                sendcode.setVisibility(View.GONE);
                signup.setVisibility(View.VISIBLE);
                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, ""+confirm_code, Snackbar.LENGTH_LONG)
                        .setAction("رمز التاكيد الخاص بك هو :", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        })
                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                        .show();

                        signup.setVisibility(View.VISIBLE);
                        Resend.setVisibility(View.GONE);


            }
        });

        shakeAnimation = AnimationUtils.loadAnimation(this,
                R.anim.shake);

        sendcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Phone = phone.getText().toString();

                if (Phone.equals("") || Phone.length() < 11) {
                    TastyToast.makeText(getApplicationContext(), "تأكد من رقم الهاتف",
                            TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    phone.setText("");
                    code.setText("");
                    linearLayout.startAnimation(shakeAnimation);


                } else {
                    if (isNetworkAvailable()) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                        ref.orderByChild("phone").equalTo(Phone).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {


                                    texterror.setVisibility(View.VISIBLE);
                                    linearLayout.startAnimation(shakeAnimation);


                                } else {
                                    texterror.setVisibility(View.INVISIBLE);
                                    confirm_code = rand.nextInt(999);
                                    Reconfirm_code = confirm_code;
                                    sendcode.setVisibility(View.GONE);
                                    signup.setVisibility(View.VISIBLE);
                                    View parentLayout = findViewById(android.R.id.content);
                                    Snackbar.make(parentLayout, "" + confirm_code, Snackbar.LENGTH_LONG)
                                            .setAction("رمز التاكيد الخاص بك هو :", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                }
                                            })
                                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                            .show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                    else{
                        TastyToast.makeText(getApplicationContext(), "تاكد من اتصالك بالانترنت",
                                TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    }
                }
            }

        });

    }

    @SuppressLint("ResourceAsColor")
    public void signup(View view) {

        final String Phone = phone.getText().toString();

        if(code.getText().toString().equals(String.valueOf(Reconfirm_code)))
        {
            if (isNetworkAvailable()) {

                texterror.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorwhite));
                startActivity(new Intent(getApplicationContext(), Home.class));
                TastyToast.makeText(getApplicationContext(), "تم الدخول بنجاح.", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                Users_Models users_models = new Users_Models();
                users_models.setPhone(Phone);
                users_models.setLangtit(0);
                users_models.setLitit(0);
                databaseReference.child("Users").push().setValue(users_models);
                userValidation.writeLoginStatus(true);
                userValidation.writephone(Phone);
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                finish();
            }
            else{
                TastyToast.makeText(getApplicationContext(), "تاكد من اتصالك بالانترنت",
                        TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }
        }
        else
        {
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, "الكود خطأ", Snackbar.LENGTH_LONG).show();
            signup.setVisibility(View.GONE);
            Resend.setVisibility(View.VISIBLE);

        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_enter, R.anim.right_out);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
