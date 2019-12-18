package root.polinim.drawerFragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import root.polinim.R;

public class MyProfile extends Fragment
{
    private static String TAG = "MyProfile Fragment";
    private static Context activity;

    private CircleImageView circleImageView;
    private ImageButton mSetting;
    private TextView mNickname;
    private TextView mScore;
    private TextView mPost;
    private TextView mAnswer;
    private TextView mName, mEmail, mPhone, mFacebook, mTwitter;

    private String userNickname, userScore, userImage, userPost, userAnswer, userName, userEmail, userPhone, userFacebook, userTwitter;
    private String imagePath;

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "deneme";
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int IMAGE_ACTION_CODE = 100;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_myprofile, container, false);
        //activity = getActivity();
        Bundle bundle = this.getArguments();
        //auth = FirebaseAuth.getInstance();
        //firebaseFirestore = FirebaseFirestore.getInstance();
        //firebaseStorage = FirebaseStorage.getInstance();
        //storageReference = firebaseStorage.getReferenceFromUrl("gs://polinim-59f69.appspot.com").child("/profile/" + FirebaseAuth.getInstance().getUid().toString());

        //fragment_myprofile items
        circleImageView = view.findViewById(R.id.circleView_my_profile);
        mSetting = view.findViewById(R.id.my_profile_setting);
        mNickname = view.findViewById(R.id.my_profile_nickname);
        mScore = view.findViewById(R.id.my_profile_score);
        mPost = view.findViewById(R.id.my_profile_posts);
        mAnswer = view.findViewById(R.id.my_profile_answers);
        mName = view.findViewById(R.id.my_profile_name);
        mEmail = view.findViewById(R.id.my_profile_email);
        mPhone = view.findViewById(R.id.my_profile_phone);
        mFacebook = view.findViewById(R.id.my_profile_facebook);
        mTwitter = view.findViewById(R.id.my_profile_twitter);

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
        mScore.setText(userScore + " Point");
        mPost.setText(userPost);
        mAnswer.setText(userAnswer);
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
        if(userImage != null || userImage != "")
        {
            final long ONE_MEGABYTE = 1024 * 1024;
            //download file as a byte array
            storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    circleImageView.setImageBitmap(bitmap);
                    createImage(userImage , bitmap);
                }
            });
        }
        */

        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                //fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); //fragmentleri temizler
                transaction.addToBackStack(getActivity().toString());

                Bundle bundle = new Bundle();
                bundle.putString("mNickname", userNickname );
                bundle.putString("mScore", userScore );
                bundle.putString("mImage", userImage );
                bundle.putString("mPost", userPost );
                bundle.putString("mAnswer", userAnswer );
                bundle.putString("mName", userName );
                bundle.putString("mEmail", userEmail );
                bundle.putString("mPhone", userPhone );
                bundle.putString("mFacebook", userFacebook );
                bundle.putString("mTwitter", userTwitter );
                bundle.putString("imagePath", imagePath );

                MyProfileSetting myProfileSetting = new MyProfileSetting();
                myProfileSetting.setArguments(bundle);
                transaction.replace(R.id.content, myProfileSetting,"my_profile_setting").commit();
            }
        });

        return view;
    }

/*
    @Nullable
    private static Boolean createImage(String imageName, Bitmap bitmap) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        try {
            File filename = new File(mediaStorageDir.getAbsolutePath() + "/" + imageName + ".jpg");
            FileOutputStream out = new FileOutputStream(filename);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            ContentValues image = getImageContent(filename, IMAGE_DIRECTORY_NAME, imageName);
            Uri result = activity.getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, image);
            //Toast.makeText(activity.getApplicationContext(), "File is Saved in  " + filename, Toast.LENGTH_SHORT).show();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, imageName,"profile");
    }
    public static ContentValues getImageContent(File parent, String appName, String imageName) {
        ContentValues image = new ContentValues();
        image.put(Images.Media.TITLE, appName);
        image.put(Images.Media.DISPLAY_NAME, imageName);
        image.put(Images.Media.DESCRIPTION, "Profile");
        image.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
        image.put(Images.Media.MIME_TYPE, "image/jpg");
        image.put(Images.Media.ORIENTATION, 0);
        image.put(Images.ImageColumns.BUCKET_ID, parent.toString().toLowerCase().hashCode());
        image.put(Images.ImageColumns.BUCKET_DISPLAY_NAME, parent.getName().toLowerCase());
        image.put(Images.Media.SIZE, parent.length());
        image.put(Images.Media.DATA, parent.getAbsolutePath());
        return image;
    }
    */
}