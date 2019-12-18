package root.polinim.outer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.gson.Gson;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import root.polinim.MainActivity;
import root.polinim.R;
import root.polinim.ask.QuestionPojo;
import root.polinim.drawerFragments.MyProfile;
import root.polinim.event.InnerEvent;
import root.polinim.inner.InnerPost;

public class OuterPost extends Fragment {

    private String TAG = "OutherPost Fragment";

    private String SharedTAG = "MainActivity";

    private ProgressDialog progressDialog;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private RecyclerView recyclerView;
    private OuterPostAdapter outerPostAdapter;

    private List<QuestionPojo> questionPojoList;

    private FloatingActionButton fab;

    private SharedPreferences pref;

    //private ArrayList<String> myFollow = new ArrayList<>();

    //private String userNickname, userPhotoLink;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.outer_post_content, container, false);

        pref = getActivity().getSharedPreferences(SharedTAG, Context.MODE_PRIVATE);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        startProgressDialog();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_outer);
        recyclerView.setHasFixedSize(true);

        questionPojoList = new ArrayList<QuestionPojo>();

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_ask);

        //getUserInfo();

        getPostInfo();

        return view;
    }

    /*
    private void getUserInfo() {
        Bundle bundle = this.getArguments();
        try {
            userNickname = bundle.getString("mNickname");
            userPhotoLink = bundle.getString("mImage");
            myFollow = bundle.getStringArrayList("followID"); // if user is asking question for first time, it will be NULL
            if(myFollow == null) {
                myFollow = new ArrayList<>();
            }
        }catch (Exception e){
        }
    }*/

    private void getPostInfo() {
        firebaseFirestore.collection("questions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                questionPojoList.add(document.toObject(QuestionPojo.class));
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

    private void recyclerView() {

        Set<String> myCategorySet = pref.getStringSet("myCategorySet", new HashSet<String>());

        //store all questions to sharedpreference
        SharedPreferences.Editor prefsEditor = pref.edit();

        Gson gson = new Gson();
        ArrayList<String> gsonString = new ArrayList<>();
        for(int i=0; i < questionPojoList.size(); i++)
            gsonString.add(gson.toJson( questionPojoList.get(i)) );
        Set<String> questionPojoListSet = new HashSet<String>(gsonString);
        prefsEditor.putStringSet("questionPojoList", questionPojoListSet);
        prefsEditor.apply();

        if(myCategorySet != null)
        {
            List<QuestionPojo> questionPojoListForCategory = new ArrayList<QuestionPojo>();

            for(int i=0; i < questionPojoList.size(); i++) {
                if(myCategorySet.contains(questionPojoList.get(i).getCaregory()))
                    questionPojoListForCategory.add(questionPojoList.get(i));
            }
            if(questionPojoList.size() != questionPojoListForCategory.size())
                questionPojoList = questionPojoListForCategory;
        }

        outerPostAdapter = new OuterPostAdapter(questionPojoList);

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

        recyclerView.setAdapter(outerPostAdapter);

        // row click listener
        recyclerView.addOnItemTouchListener(new OuterRecyclerTouchListener(getActivity(), recyclerView, new OuterRecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                InnerPost innerPost = new InnerPost();
                Bundle bundle = new Bundle();

                /*
                bundle.putString("mNickname", userNickname );
                bundle.putString("mImage", userPhotoLink );
                if(myFollow != null) {
                    bundle.putStringArrayList("followID", myFollow);
                }
                */

                bundle.putSerializable("question_pojo", new OuterSerializable(questionPojoList.get(position)));
                innerPost.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                //fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); //fragmentleri temizler
                transaction.addToBackStack("innerPost");
                transaction.add(R.id.content, innerPost,"innerPost").commit();

                fab.setVisibility(View.INVISIBLE);

                //EventBus.getDefault().postSticky(new InnerEvent(questionPojoList.get(position)));
            }

            @Override
            public void onLongClick(View view, int position) { }
        }));
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
    public void onStop() {
        super.onStop();
        progressDialog.dismiss();
    }
}

