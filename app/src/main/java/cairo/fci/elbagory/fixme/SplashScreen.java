package cairo.fci.elbagory.fixme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.style.CubeGrid;

import cairo.fci.elbagory.fixme.UserLogin_SignUp.Home;
import cairo.fci.elbagory.fixme.UserLogin_SignUp.UserValidation;
import cairo.fci.elbagory.fixme.UserLogin_SignUp.WelcomeActivity;


public class SplashScreen extends AppCompatActivity {
    UserValidation userValidation;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        userValidation = new UserValidation(this);

        progressBar = (ProgressBar)findViewById(R.id.spin_kit4);
        CubeGrid cubeGrid = new CubeGrid();
        progressBar.setIndeterminateDrawable(cubeGrid);

 if (userValidation.readLoginStatus())
         {

                 startActivity(new Intent(getApplicationContext(), Home.class));
                 finish();

         }
            else {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                        startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                        finish();


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }

    }
    }



