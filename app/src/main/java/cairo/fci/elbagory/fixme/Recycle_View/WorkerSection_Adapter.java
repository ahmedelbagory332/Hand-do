package cairo.fci.elbagory.fixme.Recycle_View;

import android.Manifest;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;

import cairo.fci.elbagory.fixme.Models.Workers_Models;

import java.util.ArrayList;
import java.util.List;

import cairo.fci.elbagory.fixme.NetworkUtils.NetworkUtils;
import cairo.fci.elbagory.fixme.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class WorkerSection_Adapter extends RecyclerView.Adapter<WorkerSection_Adapter.SectionHolder> {

    List<Workers_Models> list;
    Context context;
    AlertDialog.Builder dialogBuilder;
    AlertDialog alertDialog;
    private static long NumberOfRecomendations = 0;
    private static String KEY = "";
    private ProgressDialog dialog;
    private boolean Checked= false;
     DatabaseReference databaseReference ;






    public WorkerSection_Adapter(List<Workers_Models> list, Context context) {
        this.list = list;
        this.context = context;
        dialog = new ProgressDialog(context);


    }

    @NonNull
    @Override
    public SectionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.worker_itme_info, parent, false);
        return new SectionHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SectionHolder holder, final int position) {
        final Workers_Models mylist = list.get(position);
        holder.name.setText(mylist.getName());
        holder.sec.setText(mylist.getSection());

        Glide.with(context)
                .asBitmap()
                .load(mylist.getImg())
                .fitCenter()
                .placeholder(R.drawable.ic_doctor_placeholder)
                .into(holder.circleImageView);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkUtils.isNetworkAvailable(context)) {

                    showAlertDialog(R.layout.activity_worker, "", list.get(position).getPhone());
                }
                else
                {
                    TastyToast.makeText(context, "تأكد من اتصالك بالانترنت",
                            TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                }

            }
        });




    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class SectionHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView sec;
        private CircleImageView circleImageView;


        public SectionHolder(View itemView) {
            super(itemView);
            name= itemView.findViewById(R.id.worker_name);
            sec= itemView.findViewById(R.id.worker_sec);
            circleImageView = itemView.findViewById(R.id.profile_image);





        }


        }





    public void search(List<Workers_Models> newlist){
        list = new ArrayList<>();
        list.addAll(newlist);
        notifyDataSetChanged();
    }

    private void showAlertDialog(int layout , String section , String phone){

        dialogBuilder = new AlertDialog.Builder(context);
        final View layoutView = LayoutInflater.from(context).inflate(layout, null);

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
        Query query = databaseReference.child("Workers").orderByChild("phone").equalTo(phone);


        // shoW worker info & get the last Number Of rate & get the key of worker to update his info
        dialog.setMessage("جارى تحميل بيانات العامل....");
        dialog.show();
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    KEY = dataSnapshot1.getKey();
                    NumberOfRecomendations = dataSnapshot1.child("rate").getValue(Long.class);
                    Checked = dataSnapshot1.child("ischeked").getValue(boolean.class);
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

                        Glide.with(context)
                                .asBitmap()
                                .load(workers_models.getImg())
                                .fitCenter()
                                .placeholder(R.drawable.ic_doctor_placeholder)
                                .into(circleImageView);
                        if (Checked)
                            follow.setText("مُوصى بة");
                        else
                            follow.setText("التوصية بة");



                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                    } else
                        TastyToast.makeText(context, "تأكد من اتصالك بالانترنت",
                                TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                TastyToast.makeText(context, "تأكد من اتصالك بالانترنت",
                        TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowMess(view);
            }
        });

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (follow.getText().toString().equals("التوصية بة")) {
                    YoYo.with(Techniques.RotateInUpLeft)
                            .duration(2000)
                            .playOn(layoutView.findViewById(R.id._follow));
                    follow.setText("مُوصى بة");
                    NumberOfRecomendations++;
                    databaseReference.child("Workers").child(KEY).child("rate").setValue(NumberOfRecomendations);
                    databaseReference.child("Workers").child(KEY).child("ischeked").setValue(true);

                } else {
                    YoYo.with(Techniques.RotateInUpRight)
                            .duration(2000)
                            .playOn(layoutView.findViewById(R.id._follow));
                    follow.setText("التوصية بة");
                    NumberOfRecomendations--;
                    databaseReference.child("Workers").child(KEY).child("rate").setValue(NumberOfRecomendations);
                    databaseReference.child("Workers").child(KEY).child("ischeked").setValue(false);

                }            }
        });

    }

    private void ShowMess(View v) {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);

        dialogBuilder
                .withTitle("تأكيد الحجز")
                .withTitleColor("#FFFFFF")
                .withDividerColor("#018478")
                .withMessage("هل تريد تأكيد الحجز؟")
                .withMessageColor("#FFFFFFFF")
                .withDialogColor("#009688")
                .withIcon(context.getResources().getDrawable(R.drawable.attention))
                .withDuration(700)
                .withEffect(Effectstype.Newspager)
                .withButton1Text("تأكيد")
                .withButton2Text("إالغاء")
                .isCancelableOnTouchOutside(false)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String number = "23454568678";
                        Intent call = new Intent(Intent.ACTION_CALL);
                        call.setData(Uri.parse("tel:" + number));
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        context.startActivity(call);


                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                })
                .show();
    }

}
