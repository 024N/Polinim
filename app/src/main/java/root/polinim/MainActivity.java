package root.polinim;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import root.polinim.ask.Ask;
import root.polinim.drawerFragments.MyFollow;
import root.polinim.drawerFragments.MyGroup;
import root.polinim.drawerFragments.MyProfile;
import root.polinim.drawerFragments.MyShare;
import root.polinim.drawerFragments.Setting;
import root.polinim.entry.Login;
import root.polinim.entry.Register;
import root.polinim.entry.UserInformation;
import root.polinim.outer.OuterPost;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static Context activity;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private TextView drawerProfileNickname;
    private TextView drawerProfileScore;
    private CircleImageView drawerProfileCircleImageView;

    private ProgressDialog progressDialog;

    private String mNickname, mScore, mImage, mPost, mAnswer, mName, mEmail, mPhone, mFacebook, mTwitter;
    private ArrayList<String> myPosts = new ArrayList<>();
    private ArrayList<String> myCategories = new ArrayList<>();
    private ArrayList<String> myFollows = new ArrayList<>();
    private String imagePath;

    private static final String IMAGE_DIRECTORY_NAME = "deneme";
    private static final int IMAGE_ACTION_CODE = 100;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = getBaseContext();
        fab = (FloatingActionButton) findViewById(R.id.fab_ask);
        fab.setVisibility(View.VISIBLE);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = firebaseStorage.getReferenceFromUrl("gs://polinim-59f69.appspot.com").child("/profile/" + firebaseAuth.getUid().toString());

        if(!isNetworkAvailable()) {
            toast(getString(R.string.internet));
            //Toast.makeText(this, "" + getString(R.string.internet), Toast.LENGTH_SHORT).show();
        }

        devicePermission();
        ckeckUser();

        progressDialog = new ProgressDialog(this, R.style.dialogTheme);
        progressDialog.setMessage(getString(R.string.loading) + "");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar);
        progressDialog.show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setLogo(R.drawable.logo_name);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        disableNavigationViewScrollbars(navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                //...........3 den buyukse sıfırla
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); //fragmentleri temizler
                transaction.addToBackStack(getBaseContext().toString());

                if (id == R.id.nav_homepage) {
                    fab.setVisibility(View.VISIBLE);
                    //if(fragmentManager.getBackStackEntryCount() > 0 && getFragmentManager().findFragmentById(R.id.content) != null)
                    transaction.replace(R.id.content, new OuterPost(),"outerPost").commit();
                        //transaction.remove(getFragmentManager().findFragmentById(R.id.content)).commit();
                } else if (id == R.id.nav_profile) {
                    fab.setVisibility(View.INVISIBLE);
                    Bundle bundle = new Bundle();
                    bundle.putString("mNickname", mNickname );
                    bundle.putString("mScore", mScore );
                    bundle.putString("mImage", mImage );
                    bundle.putString("mPost", mPost );
                    bundle.putString("mAnswer", mAnswer );
                    bundle.putString("mName", mName );
                    bundle.putString("mEmail", mEmail );
                    bundle.putString("mPhone", mPhone );
                    bundle.putString("mFacebook", mFacebook );
                    bundle.putString("mTwitter", mTwitter );
                    bundle.putString("imagePath", imagePath );
                    MyProfile myProfile = new MyProfile();
                    myProfile.setArguments(bundle);
                    transaction.replace(R.id.content, myProfile,"my_profile").commit();
                } else if (id == R.id.nav_share) {
                    fab.setVisibility(View.INVISIBLE);
                    transaction.replace(R.id.content, new MyShare(),"my_share").commit();
                } else if (id == R.id.nav_follow) {
                    fab.setVisibility(View.INVISIBLE);
                    transaction.replace(R.id.content, new MyFollow(),"my_follow").commit();
                } else if (id == R.id.nav_groups) {
                    fab.setVisibility(View.INVISIBLE);
                    transaction.replace(R.id.content, new MyGroup(),"my_group").commit();
                } else if (id == R.id.nav_settings) {
                    fab.setVisibility(View.INVISIBLE);
                    Bundle bundle = new Bundle();
                    if(myCategories != null) {
                        bundle.putStringArrayList("category", myCategories);
                    }
                    Setting setting = new Setting();
                    setting.setArguments(bundle);
                    transaction.replace(R.id.content, setting,"setting").commit();
                } else if (id == R.id.nav_logout) {
                    fab.setVisibility(View.INVISIBLE);
                    Intent i = new Intent(getApplicationContext(), Login.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("activity", "mainactivity");
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                assert drawer != null;
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("mNickname", mNickname );
                bundle.putString("mImage", mImage );
                bundle.putString("mUID", firebaseAuth.getUid().toString());
                bundle.putString("mPost", mPost);
                bundle.putString("mScore", mScore );
                if(myPosts != null) {
                    bundle.putStringArrayList("postID", myPosts);
                }
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                fab.setVisibility(View.INVISIBLE);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); //fragmentleri temizler
                transaction.addToBackStack(getBaseContext().toString());
                Ask ask = new Ask();
                ask.setArguments(bundle);
                transaction.replace(R.id.content, ask,"ask").commit();
            }
        });

        View headerView = navigationView.getHeaderView(0);
        drawerProfileNickname = (TextView) headerView.findViewById(R.id.drawer_profile_nickname);
        drawerProfileScore = (TextView) headerView.findViewById(R.id.drawer_profile_score);
        drawerProfileCircleImageView = headerView.findViewById(R.id.drawer_circleView);

        getUserInfo();

    }

    private void startOuterPost() {
        /*
        Bundle bundle = new Bundle();
        bundle.putString("mNickname", mNickname );
        bundle.putString("mImage", mImage );
        if(myFollows != null) {
            bundle.putStringArrayList("followID", myFollows);
        }
        OuterPost outerPost = new OuterPost();
        outerPost.setArguments(bundle);
        */

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); //fragmentleri temizler
        transaction.addToBackStack(getBaseContext().toString());
        transaction.replace(R.id.content, new OuterPost(),"outerPost").commit();
    }

    private void getUserInfo() {
        //asynchronous olarak calısıyor task.issuccessful 1-2 saniye sonra aktif oluyor.
        DocumentReference documentReference = firebaseFirestore.collection("users").document("" + firebaseAuth.getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        mNickname = document.getData().get("nickname") + "";
                        mScore = document.getData().get("score") + "";
                        mImage = document.getData().get("image") + "";
                        mPost = document.getData().get("posts") + "";
                        mAnswer = document.getData().get("answers") + "";
                        mName = document.getData().get("name") + "";
                        mEmail = document.getData().get("email") + "";
                        mPhone = document.getData().get("phone") + "";
                        mFacebook = document.getData().get("facebook") + "";
                        mTwitter = document.getData().get("twitter") + "";
                        try {
                            myPosts = (ArrayList<String>) document.getData().get("postID");
                        }catch (Exception e){}
                        try {
                            myCategories = (ArrayList<String>) document.getData().get("category");
                        }catch (Exception e){}
                        try {
                            myFollows = (ArrayList<String>) document.getData().get("followID");
                        }catch (Exception e){}

                        if(mImage != null && mImage != "") {
                            final long ONE_MEGABYTE = 1024 * 1024;
                            //download file as a byte array
                            storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    imagePath = createFile(mImage , bitmap);
                                }
                            });
                        }
                        else {
                            Glide.with(getApplicationContext()).load(R.drawable.user).into(drawerProfileCircleImageView);
                            drawerProfileNickname.setText(mNickname);
                            drawerProfileScore.setText(mScore + " " + getString(R.string.point));
                            progressDialog.dismiss();
                        }

                        addSharedPreference();

                        startOuterPost();
                    }
                    else {
                        Log.d(TAG, "No such document");
                        if(mNickname == null)
                        {
                            progressDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), UserInformation.class));
                        }
                    }
                }
                else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void addSharedPreference() {
        Set<String> myFollowSet;
        if(myFollows != null)
           myFollowSet = new HashSet<String>(myFollows);
        else
            myFollowSet = new HashSet<String>();

        Set<String> myCategorySet;
        if(myCategories != null)
            myCategorySet = new HashSet<String>(myCategories);
        else
            myCategorySet = new HashSet<String>();

        Set<String> myPostSet;
        if(myPosts != null)
            myPostSet = new HashSet<String>(myPosts);
        else
            myPostSet = new HashSet<String>();

        SharedPreferences pref = this.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putStringSet("myCategorySet", myCategorySet);
        editor.putStringSet("myPostSet", myPostSet);
        editor.putString("mNickname", mNickname);
        editor.putString("mImage", mImage);
        editor.putString("mAnswer", mAnswer);
        editor.putString("mScore", mScore);
        editor.putStringSet("myFollowSet", myFollowSet);
        editor.apply();
    }

    @Nullable
    private String createFile(String imageName, Bitmap bitmap) {
        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                toast(getString(R.string.fail_directory));
                //Toast.makeText(getApplicationContext(), "" + getString(R.string.fail_directory), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                return null;
            }
        }

        String filePath = mediaStorageDir.getAbsolutePath() + "/" + imageName + ".jpg";
        /*
        File fdelete = new File(filePath);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted :" + filePath);
            } else {
                System.out.println("file not Deleted :" + filePath);
            }
        }
        */
        // Create the profile photo
        try {
            File file = new File(filePath);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            ContentValues image = getImageContent(file, IMAGE_DIRECTORY_NAME, imageName);
            Uri result = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);
            File imgFile = new  File(file.getPath());
            if(imgFile.exists()){
                //drawerProfileCircleImageView.setImageBitmap(bitmap);
                Glide.with(this).load(new  File(file.getPath())).into(drawerProfileCircleImageView);
            }
            else {
                Glide.with(this).load(R.drawable.user).into(drawerProfileCircleImageView);
            }

            /*
            //profil resmini alıp, profil foto ile değiştirir.
            File imgFile = new  File(filename.getPath());
            if(imgFile.exists()){
                drawerProfileCircleImageView.setImageURI(Uri.fromFile(imgFile));
                //Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                //drawerProfileCircleImageView.setImageBitmap(bitmap);
            }
            */
            drawerProfileNickname.setText(mNickname);
            drawerProfileScore.setText(mScore + " " + getString(R.string.point));

            progressDialog.dismiss();

            return file.getPath();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        //MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, imageName, "profile");
    }

    public static ContentValues getImageContent(File parent, String appName, String imageName) {
        ContentValues image = new ContentValues();
        image.put(MediaStore.Images.Media.TITLE, appName);
        image.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
        image.put(MediaStore.Images.Media.DESCRIPTION, "Profile");
        image.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        image.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        image.put(MediaStore.Images.Media.ORIENTATION, 0);
        image.put(MediaStore.Images.ImageColumns.BUCKET_ID, parent.toString().toLowerCase().hashCode());
        image.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, parent.getName().toLowerCase());
        image.put(MediaStore.Images.Media.SIZE, parent.length());
        image.put(MediaStore.Images.Media.DATA, parent.getAbsolutePath());
        return image;
    }

    public boolean devicePermission() {
        // Checking camera availability
        if (!getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            toast(getString(R.string.camera_problem));
            //Toast.makeText(getApplicationContext(), "" + getString(R.string.camera_problem), Toast.LENGTH_LONG).show();
        }

        if (Build.VERSION.SDK_INT >= 23) {
            // Check runtime camera permission
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, IMAGE_ACTION_CODE);
            }
            // Check runtime storage permission
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG,"Permission is granted");
                return true;
            }
            else {
                Log.d(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        //permission is automatically granted on sdk<23 upon installation
        else {
            Log.d(TAG,"Permission is granted");
            return true;
        }
    }

    private void ckeckUser() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                // Eğer geçerli bir kullanıcı oturumu yoksa LoginActivity e geçilir.
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, Login.class));
                }
            }
        };
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (fragmentManager.getBackStackEntryCount() < 1) {
            transaction.addToBackStack("outerPost");
            transaction.replace(R.id.content, new OuterPost(),"outerPost").commit();
            fab.setVisibility(View.VISIBLE);
        }
        else if(fragmentManager.findFragmentById(R.id.content).getTag().equals("outerPost"))
            fab.setVisibility(View.VISIBLE);

        /*
        if (fragmentManager.getBackStackEntryCount() < 1 || fragmentManager.getBackStackEntryCount() > 2) {
            //Log.i("XXXXXXXXXX", fragmentManager.getBackStackEntryCount()+"");
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); //fragmentleri temizler
            transaction.replace(R.id.content, new OuterPost(),"outerPost").commit();
            fab.setVisibility(View.VISIBLE);
        }
        */
    }

    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
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