package root.polinim.drawerFragments;

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
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import root.polinim.MainActivity;
import root.polinim.R;
import root.polinim.ask.QuestionPojo;
import root.polinim.inner.CommentPojo;
import root.polinim.inner.InnerPost;
import root.polinim.outer.OuterPostAdapter;
import root.polinim.outer.OuterRecyclerTouchListener;
import root.polinim.outer.OuterSerializable;

public class MyFollow extends Fragment
{
    private String TAG = "MyFollow Fragment";

    private ProgressDialog progressDialog;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private ArrayList<String> myFollow = new ArrayList<>();

    private List<QuestionPojo> questionPojoList;

    private RecyclerView recyclerView;
    private OuterPostAdapter outerPostAdapter;

    private SharedPreferences pref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.outer_post_content, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        startProgressDialog();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_outer);
        recyclerView.setHasFixedSize(true);

        questionPojoList = new ArrayList<QuestionPojo>();

        pref = getActivity().getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        Set<String> myFollowSet = pref.getStringSet("myFollowSet", new HashSet<String>());
        myFollow = new ArrayList<String>(myFollowSet);
        if(myFollow == null) {
            myFollow = new ArrayList<>();
        }

        Gson gson = new Gson();
        Set<String> questionPojoListSet = pref.getStringSet("questionPojoList", new HashSet<String>());
        ArrayList<String> arrayList = new ArrayList<String>();
        for (String str : questionPojoListSet) {
            questionPojoList.add(gson.fromJson(str, QuestionPojo.class));
        }

        if(myFollow != null)
        {
            List<QuestionPojo> questionPojoListForFollow = new ArrayList<QuestionPojo>();

            for(int i=0; i < questionPojoList.size(); i++) {
                if(myFollow.contains(questionPojoList.get(i).getQuestionID()))
                    questionPojoListForFollow.add(questionPojoList.get(i));
            }
            if(questionPojoList.size() != questionPojoListForFollow.size())
                questionPojoList = questionPojoListForFollow;
        }

        recyclerView();
        stopProgressDialog();

        return view;
    }

    private void recyclerView() {

        //store all questions to sharedpreference
        SharedPreferences.Editor prefsEditor = pref.edit();

        Gson gson = new Gson();
        ArrayList<String> gsonString = new ArrayList<>();
        for(int i=0; i < questionPojoList.size(); i++)
            gsonString.add(gson.toJson( questionPojoList.get(i)) );
        Set<String> questionPojoListSet = new HashSet<String>(gsonString);
        prefsEditor.putStringSet("questionPojoList", questionPojoListSet);
        prefsEditor.apply();

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
                transaction.addToBackStack(getActivity().toString());
                transaction.add(R.id.content, innerPost,"innerPost").commit();

                //EventBus.getDefault().postSticky(new InnerEvent(questionPojoList.get(position)));
            }

            @Override
            public void onLongClick(View view, int position) { }
        }));
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

    private void toast(String value) {
        new StyleableToast
                .Builder(getActivity())
                .text("" + value)
                .textColor(Color.WHITE)
                .textSize(12)
                .backgroundColor(ResourcesCompat.getColor(getResources(), R.color.toast, null))
                .show();
    }

}