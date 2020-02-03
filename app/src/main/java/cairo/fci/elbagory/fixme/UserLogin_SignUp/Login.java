package cairo.fci.elbagory.fixme.UserLogin_SignUp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;

import cairo.fci.elbagory.fixme.Models.Users_Models;
import cairo.fci.elbagory.fixme.R;

public class Login extends AppCompatActivity {
    private EditText phone;
    private TextView texterror,sign ;
    private static Animation shakeAnimation;
    LinearLayout linearLayout;
    private UserValidation userValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phone = findViewById(R.id.phonenum_login);
        linearLayout = findViewById(R.id.user_info_login);
        shakeAnimation = AnimationUtils.loadAnimation(this,
                R.anim.shake);
        userValidation = new UserValidation(this);
        texterror = findViewById(R.id.error);
        sign =findViewById(R.id._signup_);

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Phone_Verification.class));
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);

            }
        });


    }

    public void login(View view) {

        final String Phone = phone.getText().toString();

        if (Phone.equals("") || Phone.length() < 11) {
            TastyToast.makeText(getApplicationContext(), "تأكد من رقم الهاتف",
                    TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            phone.setText("");
            linearLayout.startAnimation(shakeAnimation);


        }
        else {
            if (isNetworkAvailable()) {

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                ref.orderByChild("phone").equalTo(Phone).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            startActivity(new Intent(getApplicationContext(), Home.class));
                            TastyToast.makeText(getApplicationContext(), "تم الدخول بنجاح.",
                                    TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);

                            userValidation.writeLoginStatus(true);
                            userValidation.writephone(Phone);
                            texterror.setVisibility(View.VISIBLE);
                            linearLayout.startAnimation(shakeAnimation);
                            texterror.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorwhite));

                        } else {
                            texterror.setVisibility(View.VISIBLE);
                            linearLayout.startAnimation(shakeAnimation);


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
