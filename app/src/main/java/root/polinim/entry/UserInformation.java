package root.polinim.entry;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import root.polinim.MainActivity;
import root.polinim.R;

public class UserInformation extends AppCompatActivity {

    private Button btnFinish;

    private EditText mName;
    private EditText mNickname;

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;

    private ProgressDialog progressDialog;

    private ArrayList<String> myCategories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        btnFinish = (Button) findViewById(R.id.user_info_finish);

        mName = (EditText) findViewById(R.id.user_info_name);
        mNickname = (EditText) findViewById(R.id.user_info_nickname);

        progressDialog = new ProgressDialog(this, R.style.dialogTheme);

        firebaseFirestore = FirebaseFirestore.getInstance();

        btnFinish.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = mName.getText().toString().trim();
                String nickname = mNickname.getText().toString().trim();
                String userID = auth.getInstance().getUid();
                String email = auth.getInstance().getCurrentUser().getEmail();

                if (TextUtils.isEmpty(name)){
                    toast(getString(R.string.enter_name_surname));
                    //Toast.makeText(getApplicationContext(), "" + getString(R.string.enter_name_surname), Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(nickname)) {
                    toast(getString(R.string.enter_nickname));
                    //Toast.makeText(getApplicationContext(), "" + getString(R.string.enter_nickname), Toast.LENGTH_LONG).show();
                    return;
                }

                myCategories.add("CAR");
                myCategories.add("CLOTHES");
                myCategories.add("COSMETIC");
                myCategories.add("DESIGN");
                myCategories.add("FILM and BOOK");
                myCategories.add("FUNNY");
                myCategories.add("FURNITURE");
                myCategories.add("GLASSES");
                myCategories.add("ELECTRONIC");
                myCategories.add("SPORT");
                myCategories.add("PHONE and PC");
                myCategories.add("OTHER");

                Map<String, String> userMap = new HashMap<>();
                userMap.put("uid", userID);
                userMap.put("nickname", nickname);
                userMap.put("image", "");
                userMap.put("score", "0");
                userMap.put("posts", "0");
                userMap.put("answers", "0");
                userMap.put("name", name);
                userMap.put("email", email);
                userMap.put("phone", "");
                userMap.put("twitter", "");
                userMap.put("facebook", "");

                progressDialog.setMessage(getString(R.string.loading) + "");
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar);
                progressDialog.show();

                UserInformation(userMap);
            }
        });
    }

    private void UserInformation(Map userMap) {

        DocumentReference DocumentRef = firebaseFirestore.collection("users").document("" + FirebaseAuth.getInstance().getUid());
        DocumentRef.set(userMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>(){
            @Override
            public void onSuccess(DocumentReference documentReference) {
                //Toast.makeText(UserInformation.this, "Username added to database", Toast.LENGTH_SHORT).show();
                DocumentReference DocumentRefUser = firebaseFirestore.collection("users").document("" + FirebaseAuth.getInstance().getUid());
                DocumentRefUser.update("category", myCategories).addOnSuccessListener(new OnSuccessListener<Void>(){
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(UserInformation.this, MainActivity.class));
                        progressDialog.dismiss();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        toast("Error " + e.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String error = e.getMessage();
                progressDialog.dismiss();
                toast("Error");
                //Toast.makeText(UserInformation.this, "Error:" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), Login.class);
        startActivity(i);
        finish();
    }

    private void toast(String value) {
        new StyleableToast
                .Builder(getBaseContext())
                .text("" + value)
                .textColor(Color.WHITE)
                .textSize(12)
                .backgroundColor(ResourcesCompat.getColor(getResources(), R.color.toast, null))
                .show();
    }
}