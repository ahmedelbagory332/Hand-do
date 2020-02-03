package cairo.fci.elbagory.fixme.Admin;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import cairo.fci.elbagory.fixme.Models.ChooseSection_Model;
import cairo.fci.elbagory.fixme.R;
import cairo.fci.elbagory.fixme.Worker_Profile;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static cairo.fci.elbagory.fixme.UserLogin_SignUp.Home.RequestPermissionCode;


public class Admin_ChooseSection extends AppCompatActivity {
    DatabaseReference databaseReference;
    EditText addsection;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_section);

        EnableRuntimePermission();

        addsection = findViewById(R.id.editText);

        dialog = new ProgressDialog(this);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        final List<String> Sections = new ArrayList<String>();
        final ArrayAdapter<String>   adapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, Sections);
        ListView listView =  findViewById(R.id.list);
        listView.setAdapter(adapter);


        dialog.setMessage("جارى تحميل بيانات....");
        dialog.show();
        databaseReference.child("ChooseSection").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Sections.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String model =dataSnapshot1.child("section").getValue(String.class);
                    Sections.add(model);
                    adapter.notifyDataSetChanged();
                }

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

            }
        });




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String section = Sections.get(position);

                Intent intent = new Intent(getApplicationContext(), Admin.class);
                intent.putExtra("section", section);
                startActivity(intent);



            }
        });

    }

    public void section(View view) {
        ChooseSection_Model chooseSection_model = new ChooseSection_Model(addsection.getText().toString());
        databaseReference.child("ChooseSection").push().setValue(chooseSection_model);
    }

    public void EnableRuntimePermission(){


        if (ActivityCompat.shouldShowRequestPermissionRationale(Admin_ChooseSection.this,
                Manifest.permission.CAMERA))
        {


        } else {

            ActivityCompat.requestPermissions(Admin_ChooseSection.this,new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);

        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(Admin_ChooseSection.this,
                Manifest.permission.READ_EXTERNAL_STORAGE))
        {


        } else {

            ActivityCompat.requestPermissions(Admin_ChooseSection.this,new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE}, RequestPermissionCode);

        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(Admin_ChooseSection.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {


        } else {

            ActivityCompat.requestPermissions(Admin_ChooseSection.this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestPermissionCode);

        }


    }


}
