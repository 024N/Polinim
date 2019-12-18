package root.polinim.inner;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import root.polinim.MainActivity;
import root.polinim.R;
import root.polinim.ask.CommentInfo;
import root.polinim.ask.ImageInfo;
import root.polinim.ask.QuestionPojo;
import root.polinim.event.InnerEvent;
import root.polinim.outer.OuterPostAdapter;
import root.polinim.outer.OuterRecyclerTouchListener;
import root.polinim.outer.OuterSerializable;

public class InnerPost extends Fragment implements View.OnClickListener{

    private String TAG = "InnerPost Fragment";

    private ProgressDialog progressDialog;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private RecyclerView recyclerView;
    private InnerPostAdapter innerPostAdapter;
    private List<CommentPojo> commentPojoList;

    private QuestionPojo questionPojo = new QuestionPojo();

    private ImageView innerProfile, innerImageLeft, innerImageRight;
    private ImageButton follow;
    private TextView innerNickName, innerTime, innerLikeNumber, innerCommentNumber, innerLeftLikeNumber, innerRightLikeNumber;
    private TextView textComment;
    private EditText comment;
    private Button sendComment;
    private TextView firstImageTick, secondImageTick;

    private Button leftSearch, rightSearch;

    private CardView cardProfile;

    private ArrayList<String> myFollow = new ArrayList<>();
    private String userScore, userPost, userAnswer;
    private String userNickname, userPhotoLink;

    private int score, answers;

    private SharedPreferences pref;

    private Bitmap popupProfileBitmap;
    private Bitmap leftImageBitmap, rightImageBitmap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.inner_post_content, container, false);

        setHideKeyboardOnTouch(getActivity(), view);

        view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });

        startProgressDialog();

        //comment recycler
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_inner);
        recyclerView.setHasFixedSize(true);
        commentPojoList = new ArrayList<CommentPojo>();

        Bundle bundle = getArguments();
        OuterSerializable outerSerializable = (OuterSerializable) bundle.getSerializable("question_pojo");
        questionPojo = outerSerializable.getQuestionPojo();

        /*
        userNickname = bundle.getString("mNickname");
        userPhotoLink = bundle.getString("mImage");
        myFollow = bundle.getStringArrayList("followID"); // if user is asking question for first time, it will be NULL
        if(myFollow == null) {
            myFollow = new ArrayList<>();
        }
        */

        pref = getActivity().getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        Set<String> myFollowSet = pref.getStringSet("myFollowSet", new HashSet<String>());
        userNickname = pref.getString("mNickname", "DEFAULT");
        userPhotoLink = pref.getString("mImage", "DEFAULT");
        myFollow = new ArrayList<String>(myFollowSet);
        if(myFollow == null) {
            myFollow = new ArrayList<>();
        }

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        innerProfile = view.findViewById(R.id.circleView_inner_post_profile);
        innerImageLeft = view.findViewById(R.id.image_inner_post_left);
        innerImageRight = view.findViewById(R.id.image_inner_post_right);
        cardProfile = view.findViewById(R.id.profile_cardview);

        innerNickName = view.findViewById(R.id.inner_post_profile_nickname);
        innerTime = view.findViewById(R.id.inner_post_time);
        innerLikeNumber = view.findViewById(R.id.total_like_number);
        innerCommentNumber = view.findViewById(R.id.total_comment_number);
        innerLeftLikeNumber = view.findViewById(R.id.left_like_number);
        innerRightLikeNumber = view.findViewById(R.id.right_like_number);

        comment = view.findViewById(R.id.edittext_comment);
        follow = view.findViewById(R.id.follow);
        sendComment = view.findViewById(R.id.comment_send_btn);
        textComment = view.findViewById(R.id.comment_text);

        leftSearch = view.findViewById(R.id.button_left_bigger);
        rightSearch = view.findViewById(R.id.button_right_bigger);

        firstImageTick = view.findViewById(R.id.image_left_tick);
        secondImageTick = view.findViewById(R.id.image_right_tick);

        setInformation();
        getUserInfo();
        getComments();

        //innerProfile.setOnClickListener(this);
        innerImageLeft.setOnClickListener(this);
        innerImageRight.setOnClickListener(this);
        follow.setOnClickListener(this);
        sendComment.setOnClickListener(this);
        cardProfile.setOnClickListener(this);

        leftSearch.setOnClickListener(this);
        rightSearch.setOnClickListener(this);

        // getPostInfo();

        if(myFollow.contains(questionPojo.getQuestionID())) {
            //follow.getDrawable().setColorFilter(getResources().getColor(R.color.toast));
            follow.setColorFilter(getResources().getColor(R.color.toast));
        }

        if(questionPojo.getImageInfo().getFirstImageLikeID().contains(firebaseAuth.getCurrentUser().getUid())) {
            firstImageTick.setVisibility(View.VISIBLE);
        }
        else if(questionPojo.getImageInfo().getSecondImageLikeID().contains(firebaseAuth.getCurrentUser().getUid())) {
            secondImageTick.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private void getComments() {
        firebaseFirestore.collection("comments").document(questionPojo.getQuestionID()).collection("questionComments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                commentPojoList.add(document.toObject(CommentPojo.class));
                                //getImages();
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            recyclerView();
                            stopProgressDialog();
                        }
                        else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getUserInfo() {
        //asynchronous olarak calısıyor task.issuccessful 1-2 saniye sonra aktif oluyor.
        DocumentReference documentReference = firebaseFirestore.collection("users").document("" + questionPojo.getUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        userScore = document.getData().get("score") + "";
                        userPost = document.getData().get("posts") + "";
                        userAnswer = document.getData().get("answers") + "";
                    }
                    else {
                        Log.d(TAG, "No such document");
                    }
                }
                else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void setInformation(){
        getImages();

        int totalLike = questionPojo.getImageInfo().getFirstImageLikeNumber() + questionPojo.getImageInfo().getSecondImageLikeNumber();
        innerNickName.setText(questionPojo.getUser().getNickname() + "");
        innerTime.setText(questionPojo.getDate() + "");
        innerLikeNumber.setText(totalLike + "");
        innerCommentNumber.setText(questionPojo.getImageInfo().getTotalCommentNumber() + "");
        innerLeftLikeNumber.setText(questionPojo.getImageInfo().getFirstImageLikeNumber() + "");
        innerRightLikeNumber.setText(questionPojo.getImageInfo().getSecondImageLikeNumber() + "");
        textComment.setText(questionPojo.getQuestion());
    }

    // download file as a byte array
    private void getImages() {
        storageReference = firebaseStorage.getReferenceFromUrl("gs://polinim-59f69.appspot.com").child("/profile/" + questionPojo.getUser().getUid());
        final long ONE_MEGABYTE = 1024 * 512;

        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                innerProfile.setImageBitmap(bitmap);
                popupProfileBitmap = bitmap;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        storageReference = firebaseStorage.getReferenceFromUrl("gs://polinim-59f69.appspot.com").child("/question/" + questionPojo.getImageInfo().getFirstImageLink() );
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                innerImageLeft.setImageBitmap(bitmap);
                leftImageBitmap = bitmap;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        storageReference = firebaseStorage.getReferenceFromUrl("gs://polinim-59f69.appspot.com").child("/question/" + questionPojo.getImageInfo().getSecondImageLink() );
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                innerImageRight.setImageBitmap(bitmap);
                rightImageBitmap = bitmap;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    /*
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onInnerEvent(InnerEvent innerEvent){
        this.questionPojo = innerEvent.getQuestionPojo();
        EventBus.getDefault().removeStickyEvent(innerEvent);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
    */

    private void toast(String value) {
        new StyleableToast
                .Builder(getActivity())
                .text("" + value)
                .textColor(Color.WHITE)
                .textSize(12)
                .backgroundColor(ResourcesCompat.getColor(getResources(), R.color.toast, null))
                .show();
    }

    private void startProgressDialog() {
        progressDialog = new ProgressDialog(getActivity(), R.style.dialogTheme);
        progressDialog.setMessage(getString(R.string.loading) + "");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar);
        progressDialog.show();
    }

    private void stopProgressDialog() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                progressDialog.dismiss();
            }
        }, 1000);
    }

    @Override
    public void onClick(View v) {
        if(v == cardProfile) {
            showPopup();
        }
        else if(v == innerImageLeft) {
            if(questionPojo.getImageInfo().getFirstImageLikeID().contains(firebaseAuth.getCurrentUser().getUid())) {
                toast(getString(R.string.chose_it_before));
                return;
            }
            imageLinkUpdate(1);
        }
        else if(v == innerImageRight) {
            if(questionPojo.getImageInfo().getSecondImageLikeID().contains(firebaseAuth.getCurrentUser().getUid())) {
                toast(getString(R.string.chose_it_before));
                return;
            }
            imageLinkUpdate(2);
        }
        else if(v == follow) {
            if(myFollow.contains(questionPojo.getQuestionID())) {
                toast(getString(R.string.in_follow_list));
                return;
            }
            myFollow.add(questionPojo.getQuestionID());
            DocumentReference DocumentRefUser = firebaseFirestore.collection("users").document("" + FirebaseAuth.getInstance().getUid());
            DocumentRefUser.update("followID", myFollow ).addOnSuccessListener(new OnSuccessListener < Void > () {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Follow id was updated");
                    toast(getString(R.string.follow_added));
                    follow.setColorFilter(getResources().getColor(R.color.toast));

                    Set<String> postMyFollowSet = new HashSet<String>(myFollow);

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putStringSet("myFollowSet", postMyFollowSet);
                    editor.apply();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    toast("Error: " + e.getMessage());
                }
            });
        }
        else if(v == sendComment) {
            if(comment.getText().toString().trim().length() < 4) {
                toast(getString(R.string.add_comment));
                return;
            }

            String profileLink = pref.getString("mImage", "DEFAULT");
            String nickname = pref.getString("mNickname", "DEFAULT");

            score = Integer.parseInt(pref.getString("mScore", "DEFAULT").trim());
            answers = Integer.parseInt(pref.getString("mAnswer", "DEFAULT").trim());

            // Time
            String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            //Comment id
            String commentID = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            commentID = FirebaseAuth.getInstance().getUid() + "_" + commentID;

            CommentPojo commentPojo = new CommentPojo(questionPojo.getQuestionID(), commentID, firebaseAuth.getCurrentUser().getUid(), profileLink, nickname, comment.getText().toString().trim(), currentDateandTime);

            DocumentReference DocumentRefComment = firebaseFirestore.collection("comments").document(questionPojo.getQuestionID()).collection("questionComments").document(commentID);
            DocumentRefComment.set(commentPojo).addOnSuccessListener(new OnSuccessListener < Void > () {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Comment added");
                    toast(getString(R.string.comment_added));
                    comment.setText("");
                    commentPojoList.clear();
                    getComments();
                    recyclerView();

                    answers++;
                    score = score + 3;
                    DocumentReference DocumentRefUser = firebaseFirestore.collection("users").document("" + FirebaseAuth.getInstance().getUid());
                    DocumentRefUser.update("score", "" + score);
                    DocumentRefUser.update("answers", "" + answers ).addOnSuccessListener(new OnSuccessListener < Void > () {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Score and answers were updated.");

                            DocumentReference DocumentRefQuestion = firebaseFirestore.collection("questions").document(questionPojo.getQuestionID());
                            final int commentNumber = questionPojo.getImageInfo().getTotalCommentNumber() + 1;
                            DocumentRefQuestion.update("imageInfo.totalCommentNumber", commentNumber ).addOnSuccessListener(new OnSuccessListener < Void > () {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Comment number was updated.");
                                    innerCommentNumber.setText(commentNumber+"");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    toast("Error: " + e.getMessage());
                                }
                            });
                            //drawer score update edilmeli
                        }
                    }).addOnFailureListener(new OnFailureListener() {
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

        else if(v == leftSearch) {
            showBiggerImage(leftImageBitmap);
        }

        else if(v == rightSearch) {
            showBiggerImage(rightImageBitmap);
        }
    }

    private void showPopup() {
        final Dialog myDialog = new Dialog(getActivity());
        myDialog.setContentView(R.layout.inner_profile_popup);

        TextView popupClose, popupNickname, popupScore, popupAnswer, popupPost;
        CircleImageView popupProfile;

        popupClose = myDialog.findViewById(R.id.tv_popup_close);
        popupNickname =  myDialog.findViewById(R.id.tv_popup_nickname);
        popupScore = myDialog.findViewById(R.id.tv_popup_score);
        popupAnswer =  myDialog.findViewById(R.id.tv_popup_answers);
        popupPost =  myDialog.findViewById(R.id.tv_popup_posts);
        popupProfile =  myDialog.findViewById(R.id.circleView_popup_profile);

        popupNickname.setText(questionPojo.getUser().getNickname());
        popupScore.setText(userScore);
        popupAnswer.setText(userAnswer);
        popupPost.setText(userPost);
        popupProfile.setImageBitmap(popupProfileBitmap);

        popupClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void showBiggerImage(Bitmap bitmap) {
        final Dialog myDialog = new Dialog(getActivity());
        myDialog.setContentView(R.layout.inner_bigger_image);

        ImageView imageView = myDialog.findViewById(R.id.imageView_bigger_image);
        imageView.setImageBitmap(bitmap);

        TextView popupClose = myDialog.findViewById(R.id.tv_popup_close);

        popupClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void imageLinkUpdate(final int imageNo) {

        if(imageNo == 1) {
            if(questionPojo.getImageInfo().getSecondImageLikeID().contains(firebaseAuth.getCurrentUser().getUid())) {
                questionPojo.getImageInfo().getSecondImageLikeID().remove(FirebaseAuth.getInstance().getUid());
                questionPojo.getImageInfo().setSecondImageLikeNumber( questionPojo.getImageInfo().getSecondImageLikeNumber() - 1);
            }
            questionPojo.getImageInfo().getFirstImageLikeID().add(FirebaseAuth.getInstance().getUid());
            questionPojo.getImageInfo().setFirstImageLikeNumber( questionPojo.getImageInfo().getFirstImageLikeNumber() + 1);
        }
        else if(imageNo == 2) {
            if(questionPojo.getImageInfo().getFirstImageLikeID().contains(firebaseAuth.getCurrentUser().getUid())) {
                questionPojo.getImageInfo().getFirstImageLikeID().remove(FirebaseAuth.getInstance().getUid());
                questionPojo.getImageInfo().setFirstImageLikeNumber( questionPojo.getImageInfo().getFirstImageLikeNumber() - 1);
            }
            questionPojo.getImageInfo().getSecondImageLikeID().add(FirebaseAuth.getInstance().getUid());
            questionPojo.getImageInfo().setSecondImageLikeNumber( questionPojo.getImageInfo().getSecondImageLikeNumber() + 1);
        }

        //Map<String,Object> updates = new HashMap<>();
        //updates.put("imageInfo", questionPojo.getImageInfo());

        DocumentReference DocumentRefQuestion = firebaseFirestore.collection("questions").document(questionPojo.getQuestionID());
        DocumentRefQuestion.update(
                "imageInfo.firstImageLikeID", questionPojo.getImageInfo().getFirstImageLikeID(),
                "imageInfo.firstImageLikeNumber", questionPojo.getImageInfo().getFirstImageLikeNumber(),
                "imageInfo.secondImageLikeID", questionPojo.getImageInfo().getSecondImageLikeID(),
                "imageInfo.secondImageLikeNumber", questionPojo.getImageInfo().getSecondImageLikeNumber(),
                "imageInfo.totalCommentNumber", questionPojo.getImageInfo().getTotalCommentNumber())
                .addOnSuccessListener(new OnSuccessListener < Void > () {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "User Liked");
                if(imageNo == 1) {
                    firstImageTick.setVisibility(View.VISIBLE);
                    secondImageTick.setVisibility(View.INVISIBLE);
                }
                else if(imageNo == 2) {
                    firstImageTick.setVisibility(View.INVISIBLE);
                    secondImageTick.setVisibility(View.VISIBLE);
                }
                int totalLike = questionPojo.getImageInfo().getFirstImageLikeNumber() + questionPojo.getImageInfo().getSecondImageLikeNumber();
                innerLikeNumber.setText(totalLike + "");
                innerLeftLikeNumber.setText(questionPojo.getImageInfo().getFirstImageLikeNumber() + "");
                innerRightLikeNumber.setText(questionPojo.getImageInfo().getSecondImageLikeNumber() + "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toast("Error: " + e.getMessage());
            }
        });
    }

    private void recyclerView() {

        innerPostAdapter = new InnerPostAdapter(commentPojoList);

        // vertical RecyclerView
        // keep movie_list_row.xml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        // horizontal RecyclerView
        // keep movie_list_row.xml width to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        // adding inbuilt divider line
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        // adding custom divider line with padding 16dp
        // recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(innerPostAdapter);

        // row click listener
        recyclerView.addOnItemTouchListener(new InnerRecyclerTouchListener(getActivity(), recyclerView, new InnerRecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
            }
            @Override
            public void onLongClick(View view, int position) { }
        }));
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