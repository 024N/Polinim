package root.polinim.entry;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import root.polinim.R;

public class Register extends AppCompatActivity {

    private Button btnRegister;
    private Button btnAreYouUser;

    private AutoCompleteTextView mEmail;
    private EditText mPassword;
    private ProgressDialog progressDialog;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = (Button) findViewById(R.id.btn_register);
        btnAreYouUser = (Button) findViewById(R.id.btn_are_you_member);

        mEmail = (AutoCompleteTextView) findViewById(R.id.register_email);
        mPassword = (EditText) findViewById(R.id.register_password);

        progressDialog = new ProgressDialog(this, R.style.dialogTheme);

        auth= FirebaseAuth.getInstance();

        btnAreYouUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Login.class);
                Bundle bundle = new Bundle();
                bundle.putString("activity", "register");
                i.putExtras(bundle);
                startActivity(i);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
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
                if(password.length() < 6) {
                    toast(getString(R.string.pw_min_char));
                    //Toast.makeText(getApplicationContext(), "" + getString(R.string.pw_min_char), Toast.LENGTH_LONG).show();
                    return;
                }

                progressDialog.dismiss();
                progressDialog.setMessage(getString(R.string.loading) + "");
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar);
                progressDialog.show();
                UserRegister(email, password);
            }
        });
    }

    private void UserRegister(String email, String password) {

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (!task.isSuccessful()) {
                    toast(getString(R.string.register_problem));
                    //Toast.makeText(Register.this, "" + getString(R.string.register_problem), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else {
                    toast(getString(R.string.register_successfully));
                    //Toast.makeText(Register.this, "" + getString(R.string.register_successfully), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Register.this, UserInformation.class));
                    progressDialog.dismiss();
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(getApplicationContext(), Login.class);
        Bundle bundle = new Bundle();
        bundle.putString("activity", "register");
        i.putExtras(bundle);
        startActivity(i);
        finish();
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