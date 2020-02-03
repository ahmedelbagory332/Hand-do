package cairo.fci.elbagory.fixme;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ProgressBar;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import cairo.fci.elbagory.fixme.Models.Workers_Models;
import cairo.fci.elbagory.fixme.NetworkUtils.NetworkUtils;
import cairo.fci.elbagory.fixme.Recycle_View.WorkerSection_Adapter;
import de.hdodenhof.circleimageview.CircleImageView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.ybq.android.spinkit.style.Wave;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;
import com.willowtreeapps.spruce.Spruce;
import com.willowtreeapps.spruce.animation.DefaultAnimations;
import com.willowtreeapps.spruce.sort.DefaultSort;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Workers_Section extends AppCompatActivity implements SearchView.OnQueryTextListener{
    List<Workers_Models> list= new ArrayList<>();
    RecyclerView recyclerView;
    Animator spruceAnimator;
    ProgressBar progressBar;
    private static  String SEC;
    DatabaseReference databaseReference;
    private ProgressDialog dialog;
    WorkerSection_Adapter adapter;
    TextView textViewinternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker__section);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_worker);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setTitle("Hand do");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SEC= getIntent().getStringExtra("Workers_Section");

        //loading animation
        progressBar = (ProgressBar)findViewById(R.id.spin_kit2);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);

        dialog = new ProgressDialog(this);

        textViewinternet = findViewById(R.id.textViewConnectionworker);
        textViewinternet.setVisibility(View.GONE);

        Timer timer = new Timer();
        final int MILLISECONDS = 5000; //5 seconds
        timer.schedule(new CheckConnection(getApplicationContext()), 0, MILLISECONDS);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference();



        recyclerView = findViewById(R.id.docs);
          adapter = new WorkerSection_Adapter(list,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //recyclerView animation
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext()) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                super.onLayoutChildren(recycler, state);
                initSpruce();
            }
        };

        Query query = databaseReference.child("Workers").orderByChild("section").equalTo(SEC);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                list.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    Workers_Models model =dataSnapshot1.getValue(Workers_Models.class);
                    list.add(model);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    recyclerView.setLayoutManager(linearLayoutManager);


                }

                progressBar.setVisibility(View.GONE);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                TastyToast.makeText(getApplicationContext(), "تأكد من اتصالك بالانترنت",
                        TastyToast.LENGTH_SHORT, TastyToast.ERROR);

            }
        });





    }





    private void initSpruce() {
        spruceAnimator = new Spruce.SpruceBuilder(recyclerView)
                .sortWith(new DefaultSort(100))
                .animateWith(DefaultAnimations.shrinkAnimator(recyclerView, 800),
                        ObjectAnimator.ofFloat(recyclerView, "translationX", -recyclerView.getWidth(), 0f).setDuration(800))
                .start();
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
        List<Workers_Models> newlist= new ArrayList<>();
        for (Workers_Models search : list)
        {
            if (search.getName().contains(userinput)) {

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
