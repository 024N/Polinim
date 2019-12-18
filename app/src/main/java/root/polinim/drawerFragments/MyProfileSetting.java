package root.polinim.drawerFragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import root.polinim.utils.FileUtil;
import root.polinim.MainActivity;
import root.polinim.R;

import static android.app.Activity.RESULT_OK;

public class MyProfileSetting extends Fragment
{
    private String TAG = "MyProfileSetting Fragment";

    private CircleImageView circleImageView;
    private Button mChangePhoto;
    private Button mSave;
    private EditText mNickname, mName, mEmail, mPhone, mFacebook, mTwitter;

    private String userNickname, userScore, userImage, userPost, userAnswer, userName, userEmail, userPhone, userFacebook, userTwitter;
    private String imagePath;

    private int photoChanged = 0;
    private Uri uri;
    private File actualImage;
    private File compressedImage;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private ProgressDialog progressDialog;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_myprofile_setting, container, false);

        setHideKeyboardOnTouch(getActivity(), view);

        Bundle bundle = this.getArguments();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        progressDialog = new ProgressDialog(getActivity(), R.style.dialogTheme);

        //fragment_myprofile_setting items
        circleImageView = view.findViewById(R.id.circleView_my_profile_setting);
        mChangePhoto = view.findViewById(R.id.btn_change_photo);
        mNickname = view.findViewById(R.id.my_profile_setting_nickname);
        mName = view.findViewById(R.id.my_profile_setting_name);
        mEmail = view.findViewById(R.id.my_profile_setting_email);
        mPhone = view.findViewById(R.id.my_profile_setting_phone);
        mFacebook = view.findViewById(R.id.my_profile_setting_facebook);
        mTwitter = view.findViewById(R.id.my_profile_setting_twitter);

        mSave = view.findViewById(R.id.btn_save);

        //User information
        userNickname = bundle.getString("mNickname");
        userScore = bundle.getString("mScore");
        userImage = bundle.getString("mImage");
        userPost = bundle.getString("mPost");
        userAnswer = bundle.getString("mAnswer");
        userName = bundle.getString("mName");
        userEmail = bundle.getString("mEmail");
        userPhone = bundle.getString("mPhone");
        userFacebook = bundle.getString("mFacebook");
        userTwitter = bundle.getString("mTwitter");
        imagePath = bundle.getString("imagePath");

        mNickname.setText(userNickname);
        mName.setText(userName);
        mEmail.setText(userEmail);
        mPhone.setText(userPhone);
        mFacebook.setText(userFacebook);
        mTwitter.setText(userTwitter);

        if(imagePath != null && imagePath != "") {
            File imgFile = new  File(imagePath);
            if(imgFile.exists()){
                Glide.with(this).load(new  File(imagePath)).into(circleImageView);
            }
            else
                Glide.with(this).load(R.drawable.user).into(circleImageView);
        }
        else
            Glide.with(this).load(R.drawable.user).into(circleImageView);

        /*
        if (Build.VERSION.SDK_INT >= 23) {
            // Check runtime storage permission
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
        */

        mChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage(getString(R.string.loading) + "");
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar);
                progressDialog.show();
                updateUserInformation();
            }
        });

        return view;
    }

    private void updateUserInformation()
    {
        DocumentReference DocumentRef = firebaseFirestore.collection("users").document("" + FirebaseAuth.getInstance().getUid());
        if(photoChanged == 1)
        {
            uploadImage();
            if( userImage != "" && userImage != null)
            {
                StringTokenizer tokens = new StringTokenizer(userImage.trim(), ":");
                String uid = tokens.nextToken();
                String photoNumber = tokens.nextToken();
                int number = Integer.parseInt(photoNumber);
                DocumentRef.update("image", "" + uid + ":" + (number + 1));
            }
            else
                DocumentRef.update("image", "" + FirebaseAuth.getInstance().getUid() + ":0");
        }
        DocumentRef.update("nickname", "" + mNickname.getText());
        DocumentRef.update("name", "" + mName.getText());
        DocumentRef.update("phone", "" + mPhone.getText());
        DocumentRef.update("twitter", "" + mTwitter.getText());
        DocumentRef.update("facebook", "" + mFacebook.getText()).addOnSuccessListener(new OnSuccessListener < Void > () {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                toast(getString(R.string.updated_successfully));
                //Toast.makeText(getActivity(), "" + getString(R.string.updated_successfully), Toast.LENGTH_LONG).show();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String error = e.getMessage();
                progressDialog.dismiss();
                toast("Error: " + error);
                //Toast.makeText(getActivity(), "Error:" + error, Toast.LENGTH_SHORT).show();
            }
        });

        photoChanged = 0;
    }

    private void uploadImage() {
        if(uri != null)
        {
            customCompressImage();
            uri = Uri.fromFile(compressedImage);
            StorageReference ref = storageReference.child("profile/"+ FirebaseAuth.getInstance().getUid().toString());
            ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "image uploaded");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            toast("Error: " + e.getMessage());
                            //Toast.makeText(getActivity(), "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    /*
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
                    */
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            uri = data.getData();
            try {
                circleImageView.setImageURI(uri);
                photoChanged = 1;
            }catch (Exception e){
            }
        }
    }

    public void customCompressImage() {
        try {
            actualImage = FileUtil.from(getActivity(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (actualImage == null) { }
        else {
            // Compress image in main thread using custom Compressor
            try {
                compressedImage = new Compressor(getActivity())
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(75)
                        .setCompressFormat(Bitmap.CompressFormat.WEBP)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + "deneme") // getString(R.string.app_name)
                        .compressToFile(actualImage);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Compress problem!");
            }
        }
    }

    private void toast(String value) {
        new StyleableToast
                .Builder(getActivity())
                .text("" + value)
                .textColor(Color.WHITE)
                .textSize(12)
                .backgroundColor(ResourcesCompat.getColor(getResources(), R.color.toast, null))
                .show();
    }

    public static void setHideKeyboardOnTouch(final Context context, View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        try {
            //Set up touch listener for non-text box views to hide keyboard.
            if (!(view instanceof EditText || view instanceof ScrollView)) {
                view.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        return false;
                    }
                });
            }
            //If a layout container, iterate over children and seed recursion.
            if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    View innerView = ((ViewGroup) view).getChildAt(i);
                    setHideKeyboardOnTouch(context, innerView);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}