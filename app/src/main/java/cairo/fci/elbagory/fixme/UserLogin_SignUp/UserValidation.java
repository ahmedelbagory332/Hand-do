package cairo.fci.elbagory.fixme.UserLogin_SignUp;

import android.content.Context;
import android.content.SharedPreferences;

public class UserValidation {

    private  SharedPreferences sharedPreferences ,doctorssharedPreferences;
    private Context context;

    public UserValidation(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("UserValidation",Context.MODE_PRIVATE);
        doctorssharedPreferences = context.getSharedPreferences("doctorsValidation",Context.MODE_PRIVATE);

    }

    public void writeLoginStatus(boolean status) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Login",status);
        editor.commit();

    }

    public  boolean readLoginStatus() {

        return sharedPreferences.getBoolean("Login",false);
    }

    public void writephone(String phone) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phone",phone);
        editor.commit();

    }

    public  String readphone() {

        return sharedPreferences.getString("phone","");
    }






    public void  writeTapTargetSequence(boolean here) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("TapTarget",here);
        editor.commit();

    }

    public  boolean ReadTapTargetSequence() {

        return sharedPreferences.getBoolean("TapTarget",true);
    }













}
