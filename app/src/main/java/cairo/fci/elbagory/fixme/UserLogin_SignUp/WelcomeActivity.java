package cairo.fci.elbagory.fixme.UserLogin_SignUp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cairo.fci.elbagory.fixme.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        TextView textView_policy = findViewById(R.id.policy);
        textView_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uri = "https://sites.google.com/view/sal7ni-privacy-policy/privacy-policy?authuser=6";

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent.createChooser(intent, "Select app"));
            }
        });
    }

    public void goon(View view) {

        startActivity(new Intent(getApplicationContext(),Phone_Verification.class));
        overridePendingTransition(R.anim.right_enter, R.anim.left_out);
        finish();

    }
}
