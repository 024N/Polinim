package root.polinim.ask;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.liefery.android.icon_spinner.IconSpinnerAdapter;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import id.zelory.compressor.Compressor;
import root.polinim.MainActivity;
import root.polinim.R;
import root.polinim.utils.FileUtil;

import static android.app.Activity.RESULT_CANCELED;

public class Ask extends Fragment implements View.OnClickListener
{
    private String TAG = "Ask Fragment";

    private AskAdapter[] categories;
    private AskAdapter[] times;
    private String[] categoryTitles;
    private String[] Times;
    private ImageView imageLeft, imageRight;
    private TextView imageLeftText, imageRightText;
    private EditText question;
    private Spinner categorySpinner;
    private Spinner timeSpinner;
    private Button askButton;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private int isLeftImageSelected = 0;
    private int isRightImageSelected = 0;
    private Uri uriLeft, uriRight;
    private File actualImage;
    private File compressedImage;

    private File mediaStorageDir;
    private String mCurrentPhotoPath;
    private Uri galleryImageUri;

    private String leftImageName;
    private String rightImageName;

    private int postNumber;
    private int myScore;

    private ArrayList<String> myPosts = new ArrayList<>();

    private int GALLERY = 1, CAMERA = 2;
    private int selectedImageView = 0;

    private static final String IMAGE_DIRECTORY_NAME = "deneme/soru";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ask, container, false);

        setHideKeyboardOnTouch(getActivity(), view);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        imageLeft = view.findViewById(R.id.image_ask_left);
        imageRight = view.findViewById(R.id.image_ask_right);

        imageLeftText = view.findViewById(R.id.image_ask_left_text);
        imageRightText = view.findViewById(R.id.image_ask_right_text);

        question = view.findViewById(R.id.edittext_question);
        askButton = view.findViewById(R.id.btn_ask);

        progressDialog = new ProgressDialog(getActivity(), R.style.dialogTheme);

        // Categories spinner in ask question
        categories = new AskAdapter[] {
                new AskAdapter( getString(R.string.select_category), null, Color.WHITE, getResources().getDrawable( R.drawable.category_select ) ),
                new AskAdapter( getString(R.string.car_name), getString(R.string.car_expression), Color.WHITE, getResources().getDrawable( R.drawable.category_car ) ),
                new AskAdapter( getString(R.string.clothes_name), getString(R.string.clothes_expression), Color.WHITE, getResources().getDrawable( R.drawable.category_clothes ) ),
                new AskAdapter( getString(R.string.cosmetic_name), getString(R.string.cosmetic_expression), Color.WHITE, getResources().getDrawable( R.drawable.category_cosmetic ) ),
                new AskAdapter( getString(R.string.design_name), getString(R.string.design_expression), Color.WHITE, getResources().getDrawable( R.drawable.category_design ) ),
                new AskAdapter( getString(R.string.film_name), getString(R.string.film_expression), Color.WHITE, getResources().getDrawable( R.drawable.category_film ) ),
                new AskAdapter( getString(R.string.fun_name), getString(R.string.fun_expression), Color.WHITE, getResources().getDrawable( R.drawable.category_fun ) ),
                new AskAdapter( getString(R.string.furniture_name), getString(R.string.furniture_expression), Color.WHITE, getResources().getDrawable( R.drawable.category_furniture ) ),
                new AskAdapter( getString(R.string.jewelry_name), getString(R.string.jewelry_expression), Color.WHITE, getResources().getDrawable( R.drawable.category_jewelry ) ),
                new AskAdapter( getString(R.string.machine_name), getString(R.string.machine_expression), Color.WHITE, getResources().getDrawable( R.drawable.category_machine ) ),
                new AskAdapter( getString(R.string.sports_name), getString(R.string.sports_expression), Color.WHITE, getResources().getDrawable( R.drawable.category_sports ) ),
                new AskAdapter( getString(R.string.tablet_name), getString(R.string.tablet_expression), Color.WHITE, getResources().getDrawable( R.drawable.category_tablet ) ),
                new AskAdapter( getString(R.string.other_name), getString(R.string.other_expression), Color.WHITE, getResources().getDrawable( R.drawable.category_other ) ) };

        categoryTitles = new String[]{getString(R.string.select_category), getString(R.string.car_name), getString(R.string.clothes_name), getString(R.string.cosmetic_name),
                getString(R.string.design_name), getString(R.string.film_name), getString(R.string.fun_name), getString(R.string.furniture_name), getString(R.string.jewelry_name),
                getString(R.string.machine_name), getString(R.string.sports_name), getString(R.string.tablet_name), getString(R.string.other_name)};

        categorySpinner = view.findViewById( R.id.spinner_category );
        categorySpinner.setAdapter( new IconSpinnerAdapter( getActivity(), categories ) );

        // Categories spinner in ask question
        times = new AskAdapter[] {
                new AskAdapter( getString(R.string.select_time), null, Color.WHITE, getResources().getDrawable( R.drawable.category_time ) ),
                new AskAdapter( getString(R.string.half_hour), null, Color.WHITE, getResources().getDrawable( R.drawable.category_time ) ),
                new AskAdapter( getString(R.string.one_hour), null, Color.WHITE, getResources().getDrawable( R.drawable.category_time ) ),
                new AskAdapter( getString(R.string.two_hour), null, Color.WHITE, getResources().getDrawable( R.drawable.category_time ) ),
                new AskAdapter( getString(R.string.three_hour),null, Color.WHITE, getResources().getDrawable( R.drawable.category_time ) ),
                new AskAdapter( getString(R.string.six_hour), null, Color.WHITE, getResources().getDrawable( R.drawable.category_time ) ),
                new AskAdapter( getString(R.string.twelve_hour), null, Color.WHITE, getResources().getDrawable( R.drawable.category_time ) ),
                new AskAdapter( getString(R.string.one_day), null, Color.WHITE, getResources().getDrawable( R.drawable.category_time ) ),
                new AskAdapter( getString(R.string.two_day), null, Color.WHITE, getResources().getDrawable( R.drawable.category_time ) ) };

        Times = new String[]{getString(R.string.select_time), getString(R.string.half_hour), getString(R.string.one_hour), getString(R.string.two_hour),
                getString(R.string.three_hour), getString(R.string.six_hour), getString(R.string.twelve_hour), getString(R.string.one_day), getString(R.string.two_day)};

        timeSpinner = view.findViewById( R.id.spinner_time );
        timeSpinner.setAdapter( new IconSpinnerAdapter( getActivity(), times ) );

        imageLeft.setOnClickListener(this);
        imageRight.setOnClickListener(this);
        askButton.setOnClickListener(this);

        // Directory location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                toast(getString(R.string.fail_directory));
            }
        }
        return view;
    }

    private void uploadQuestion() {

        // Create image names
        String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        imageName = FirebaseAuth.getInstance().getUid() + "_" + imageName;
        leftImageName = imageName + ":1";
        rightImageName = imageName + ":2";

        // Compress selected images and send them to server
        uploadImage();

        // Question ID = documentName
        String questionID = imageName;

        // Date
        String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Time
        String time = Times[(int) timeSpinner.getSelectedItemId()].trim();

        // Category
        String category =  categoryTitles[(int) categorySpinner.getSelectedItemId()].trim();

        // Question
        String questions = question.getText().toString().trim();

        // User
        Bundle bundle = this.getArguments();
        String userUID = bundle.getString("mUID");
        String userNickname = bundle.getString("mNickname");
        String userPhotoLink = bundle.getString("mImage");

        postNumber = Integer.parseInt(bundle.getString("mPost").trim());
        myScore = Integer.parseInt(bundle.getString("mScore").trim());
        myPosts = bundle.getStringArrayList("postID"); // if user is asking question for first time, it will be NULL
        if(myPosts == null) {
            myPosts = new ArrayList<>();
        }
        User user = new User(userUID, userNickname, userPhotoLink);

        // ImageInfo
        int firstImageLikeNumber = 0;
        int secondImageLikeNumber = 0;
        int totalCommentNumber = 0;
        ArrayList<String> firstImageLikeID = new ArrayList<>();
        ArrayList<String> secondImageLikeID = new ArrayList<>();
        ImageInfo imageInfo = new ImageInfo(leftImageName, rightImageName, firstImageLikeNumber, secondImageLikeNumber, totalCommentNumber, firstImageLikeID, secondImageLikeID);

        // CommentInfo -> it was changed, it will be filled from other user
        //ArrayList<String> commentInfo = new ArrayList<>();
        //String cCommentID = new String();
        //User cUser = new User(null, null, null);
        //String cComment;
        //String cTime;
        //CommentInfo commentInfo = new CommentInfo(cUser, cCommentID, cComment, cTime);

        // Question POJO
        QuestionPojo questionPojo = new QuestionPojo(questionID, currentDateandTime, time, category, questions, user, imageInfo);

        // Firebase upload part
        uploadQuestionInformation(questionPojo, questionID);
    }

    private void uploadQuestionInformation(QuestionPojo questionPojo, final String documentName) {
        DocumentReference DocumentRefQuestion = firebaseFirestore.collection("questions").document(documentName);
        DocumentRefQuestion.set(questionPojo).addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void aVoid) {
                postNumber++;
                myScore = myScore + 5;
                myPosts.add(documentName);
                DocumentReference DocumentRefUser = firebaseFirestore.collection("users").document(FirebaseAuth.getInstance().getUid());
                DocumentRefUser.update("postID", myPosts);
                DocumentRefUser.update("score", "" + myScore);
                DocumentRefUser.update("posts", "" + postNumber ).addOnSuccessListener(new OnSuccessListener < Void > () {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Post number was updated and postID is added");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toast("Error: " + e.getMessage());
                    }
                });

                startActivity(new Intent(getActivity(), MainActivity.class));
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                toast("Error " + e.getMessage());
            }
        });
    }

    private void uploadImage() {
        if(uriLeft != null && uriRight != null) {
            customCompressImage(uriLeft, 1);
            customCompressImage(uriRight, 2);
            uploadImageToServer(uriLeft, uriRight);
        }
    }

    private void uploadImageToServer(Uri uriLeft, final Uri uriRight) {
        StorageReference refLeft = storageReference.child("question/"+ leftImageName);
        refLeft.putFile(uriLeft).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Left image uploaded");
                StorageReference refRight = storageReference.child("question/"+ rightImageName);
                refRight.putFile(uriRight).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "Right image uploaded");
                    }}).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toast("Error: " + e.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toast("Error: " + e.getMessage());
            }
        });
    }

    public void customCompressImage(Uri uri, int imageNumber) {
        try {
            actualImage = FileUtil.from(getActivity(), uri);
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "actualImage has problem in customCompressImage");
        }

        if (actualImage != null) {
            // Compress image in main thread using custom Compressor
            try {
                compressedImage = new Compressor(getActivity())
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(75)
                        .setCompressFormat(Bitmap.CompressFormat.WEBP)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + IMAGE_DIRECTORY_NAME) // getString(R.string.app_name)
                        .compressToFile(actualImage);

                if(imageNumber == 1) {
                    uriLeft = Uri.fromFile(compressedImage);
                    Log.d(TAG, "Left image compressed");
                }
                else {
                    uriRight = Uri.fromFile(compressedImage);
                    Log.d(TAG, "Right image compressed");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Compress problem!");
            }
        }
    }

    private void showPictureDialog(){

        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AlertDialogTheme);
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(contextThemeWrapper);
        pictureDialog.setTitle(getString(R.string.select_action) + "");
        String[] pictureDialogItems = { getString(R.string.select_gallery) + "", getString(R.string.select_camera) + "" };

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, GALLERY);
                        break;
                    case 1:
                        //Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //Uri uri = Uri.fromFile(getOutputMediaFile());
                        //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        //startActivityForResult(cameraIntent, CAMERA);
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            }
                            catch (IOException ex) {
                                ex.printStackTrace();
                                Log.d(TAG,"Problem while create photo file");
                            }

                            if (photoFile != null) {
                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                                    Uri photoURI = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", photoFile);
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                }
                                else {
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                                }

                                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                startActivityForResult(takePictureIntent, CAMERA);
                            }
                            galleryImageUri = Uri.fromFile(photoFile);
                        }
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    private File createImageFile() throws IOException {
        String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + IMAGE_DIRECTORY_NAME);
        File image = File.createTempFile(imageName,".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void addPicToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        galleryImageUri = contentUri;
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        if (requestCode == GALLERY) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                if(selectedImageView == 1) {
                    uriLeft = data.getData();
                    imageLeftText.setVisibility(View.INVISIBLE);
                    imageLeft.setImageBitmap(bitmap);
                    isLeftImageSelected = 1;
                }
                else if(selectedImageView == 2) {
                    uriRight = data.getData();
                    imageRightText.setVisibility(View.INVISIBLE);
                    imageRight.setImageBitmap(bitmap);
                    isRightImageSelected = 1;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Error:" + e.getMessage() + " in onActivityResult");
            }
        }
        else if (requestCode == CAMERA) {
            //uri kısmını tekrardan düzenle
            //addPicToGallery();
            //Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            if(selectedImageView == 1) {
                uriLeft = galleryImageUri;
                imageLeftText.setVisibility(View.INVISIBLE);
                imageLeft.setImageURI(uriLeft);
                isLeftImageSelected = 1;
            }
            else if(selectedImageView == 2) {
                uriRight = galleryImageUri;
                imageRightText.setVisibility(View.INVISIBLE);
                imageRight.setImageURI(uriRight);
                isRightImageSelected = 1;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v == imageLeft) {
            selectedImageView = 1;
            showPictureDialog();
        }
        else if(v == imageRight) {
            selectedImageView = 2;
            showPictureDialog();
        }
        else if(v == askButton) {

            if(categorySpinner.getSelectedItemId() == 0) {
                toast(getString(R.string.choose_category));
                return;
            }

            if(timeSpinner.getSelectedItemId() == 0) {
                toast(getString(R.string.choose_time));
                return;
            }

            if(question.getText().toString().trim().length() < 4) {
                toast(getString(R.string.add_question));
                return;
            }

            if(isLeftImageSelected != 1 || isRightImageSelected != 1) {
                toast(getString(R.string.add_image));
                return;
            }

            progressDialog.setMessage(getString(R.string.loading) + "");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar);
            progressDialog.show();
            uploadQuestion();
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