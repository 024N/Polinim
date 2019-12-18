package root.polinim.entry;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import root.polinim.MainActivity;
import root.polinim.R;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = Login.class.getSimpleName();

    private RelativeLayout loginLayer, signupLayer;
    private Button btnSignup;
    private Button btnLogin;
    private Button btnForgotPw;
    private String activityName;
    private AutoCompleteTextView mEmail;
    private EditText mPassword;

    private ImageView logoUp, logoDown, logoName;

    private ProgressDialog progressDialog;

    private FirebaseAuth auth;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            loginLayer.setVisibility(View.VISIBLE);
            signupLayer.setVisibility(View.VISIBLE);
            logoDown.setVisibility(View.VISIBLE);
            logoUp.setVisibility(View.INVISIBLE);
            logoName.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this, R.style.dialogTheme);

        auth= FirebaseAuth.getInstance();

        logoUp = findViewById(R.id.imgView_logo_up);
        logoDown = findViewById(R.id.imgView_logo_down);
        logoName = findViewById(R.id.imgView_logo_name);

        loginLayer = (RelativeLayout) findViewById(R.id.login_layer);
        signupLayer = (RelativeLayout) findViewById(R.id.sign_up_layer);

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSignup = (Button) findViewById(R.id.btn_sign_up);
        btnForgotPw = (Button) findViewById(R.id.btn_forgot_pw);

        btnLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        btnForgotPw.setOnClickListener(this);

        mEmail = (AutoCompleteTextView) findViewById(R.id.login_email);
        mPassword = (EditText) findViewById(R.id.login_password);

        //mEmail.setText("ozanozfirat@gmail.com");
        //mPassword.setText("123456");

        /*
        if(auth.getCurrentUser() != null){
            //profile activity
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }
        */

        try {
            Bundle bundle = getIntent().getExtras();
            activityName = bundle.getString("activity");
            Log.d(TAG, "Activity name is " + activityName);
        }
        catch (Exception e) {
            Log.d(TAG, "Activity is null");
        }

        if (activityName == null)
            handler.postDelayed(runnable, 2000);
        else {
            loginLayer.setVisibility(View.VISIBLE);
            signupLayer.setVisibility(View.VISIBLE);
            logoDown.setVisibility(View.VISIBLE);
            logoUp.setVisibility(View.INVISIBLE);
            logoName.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view)
    {
        if(view == btnLogin) {
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)){
                toast(getString(R.string.enter_email));
                //Toast.makeText(getApplicationContext(), "" + getString(R.string.enter_email), Toast.LENGTH_LONG).show();
                return;
            }
            if(TextUtils.isEmpty(password)) {
                toast(getString(R.string.enter_pw));
                //Toast.makeText(getApplicationContext(), "" + getString(R.string.enter_pw), Toast.LENGTH_LONG).show();
                return;
            }

            progressDialog.setMessage(getString(R.string.loading) + "");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar);
            progressDialog.show();

            UserLogin(email, password);
        }
        else if(view == btnSignup) {
            finish();
            Intent i = new Intent(getApplicationContext(), Register.class);
            startActivity(i);
        }
        else if(view == btnForgotPw) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); //fragmentleri temizler
            transaction.addToBackStack(getBaseContext().toString());
            transaction.replace(R.id.login_relativelayout, new ForgotPassword()).commit();
        }
    }

    private void UserLogin(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                progressDialog.dismiss();
                if(!isNetworkAvailable()) {
                    toast(getString(R.string.internet));
                    //Toast.makeText(Login.this, "" + getString(R.string.internet), Toast.LENGTH_SHORT).show();
                }
                else if (!task.isSuccessful()) {
                    toast(getString(R.string.login_problem));
                    //Toast.makeText(Login.this, "" + getString(R.string.login_problem), Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d(TAG, "Login Successfully");
                    startActivity(new Intent(Login.this, MainActivity.class));
                    finish();
                }
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void toast(String value)
    {
        new StyleableToast
                .Builder(getBaseContext())
                .text("" + value)
                .textColor(Color.WHITE)
                .textSize(12)
                .backgroundColor(ResourcesCompat.getColor(getResources(), R.color.toast, null))
                .show();
    }
}

/*
Thread myThread = new Thread()
{
    @Override
    public void run()
    {
        try {
            sleep(3000);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
};
myThread.start();
*/