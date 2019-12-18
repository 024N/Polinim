package root.polinim.drawerFragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.ArrayList;

import root.polinim.MainActivity;
import root.polinim.R;
import root.polinim.ask.AskAdapter;
import root.polinim.ask.QuestionPojo;

public class Setting extends Fragment //implements CompoundButton.OnCheckedChangeListener
{
    private String TAG = "Setting Fragment";

    //left linearLayour
    private ImageView imageViewLeftOne;
    private ImageView imageViewLeftTwo;
    private ImageView imageViewLeftThree;
    private ImageView imageViewLeftFour;
    private ImageView imageViewLeftFive;
    private ImageView imageViewLeftSix;

    private CheckBox checkBoxLeftOne;
    private CheckBox checkBoxLeftTwo;
    private CheckBox checkBoxLeftThree;
    private CheckBox checkBoxLeftFour;
    private CheckBox checkBoxLeftFive;
    private CheckBox checkBoxLeftSix;

    private TextView textViewLeftOne;
    private TextView textViewLeftTwo;
    private TextView textViewLeftThree;
    private TextView textViewLeftFour;
    private TextView textViewLeftFive;
    private TextView textViewLeftSix;

    //right linearLayour
    private ImageView imageViewRightOne;
    private ImageView imageViewRightTwo;
    private ImageView imageViewRightThree;
    private ImageView imageViewRightFour;
    private ImageView imageViewRightFive;
    private ImageView imageViewRightSix;

    private CheckBox checkBoxRightOne;
    private CheckBox checkBoxRightTwo;
    private CheckBox checkBoxRightThree;
    private CheckBox checkBoxRightFour;
    private CheckBox checkBoxRightFive;
    private CheckBox checkBoxRightSix;

    private TextView textViewRightOne;
    private TextView textViewRightTwo;
    private TextView textViewRightThree;
    private TextView textViewRightFour;
    private TextView textViewRightFive;
    private TextView textViewRightSix;

    private Button save;

    private ProgressDialog progressDialog;

    private FirebaseFirestore firebaseFirestore;

    private ArrayList<String> myCategories = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        save = view.findViewById(R.id.btn_save);

        progressDialog = new ProgressDialog(getActivity(), R.style.dialogTheme);

        firebaseFirestore = FirebaseFirestore.getInstance();

        // Left side
        checkBoxLeftOne = view.findViewById(R.id.checkbox_left_one);
        checkBoxLeftTwo = view.findViewById(R.id.checkbox_left_two);
        checkBoxLeftThree = view.findViewById(R.id.checkbox_left_three);
        checkBoxLeftFour = view.findViewById(R.id.checkbox_left_four);
        checkBoxLeftFive = view.findViewById(R.id.checkbox_left_five);
        checkBoxLeftSix = view.findViewById(R.id.checkbox_left_six);

        textViewLeftOne = view.findViewById(R.id.checkbox_left_one_tv);
        textViewLeftTwo = view.findViewById(R.id.checkbox_left_two_tv);
        textViewLeftThree = view.findViewById(R.id.checkbox_left_three_tv);
        textViewLeftFour = view.findViewById(R.id.checkbox_left_four_tv);
        textViewLeftFive = view.findViewById(R.id.checkbox_left_five_tv);
        textViewLeftSix = view.findViewById(R.id.checkbox_left_six_tv);

        imageViewLeftOne = view.findViewById(R.id.checkbox_left_one_logo);
        imageViewLeftTwo = view.findViewById(R.id.checkbox_left_two_logo);
        imageViewLeftThree = view.findViewById(R.id.checkbox_left_three_logo);
        imageViewLeftFour = view.findViewById(R.id.checkbox_left_four_logo);
        imageViewLeftFive = view.findViewById(R.id.checkbox_left_five_logo);
        imageViewLeftSix = view.findViewById(R.id.checkbox_left_six_logo);

        // Right side
        checkBoxRightOne = view.findViewById(R.id.checkbox_right_one);
        checkBoxRightTwo = view.findViewById(R.id.checkbox_right_two);
        checkBoxRightThree = view.findViewById(R.id.checkbox_right_three);
        checkBoxRightFour = view.findViewById(R.id.checkbox_right_four);
        checkBoxRightFive = view.findViewById(R.id.checkbox_right_five);
        checkBoxRightSix = view.findViewById(R.id.checkbox_right_six);

        textViewRightOne = view.findViewById(R.id.checkbox_right_one_tv);
        textViewRightTwo = view.findViewById(R.id.checkbox_right_two_tv);
        textViewRightThree = view.findViewById(R.id.checkbox_right_three_tv);
        textViewRightFour = view.findViewById(R.id.checkbox_right_four_tv);
        textViewRightFive = view.findViewById(R.id.checkbox_right_five_tv);
        textViewRightSix = view.findViewById(R.id.checkbox_right_six_tv);

        imageViewRightOne = view.findViewById(R.id.checkbox_right_one_logo);
        imageViewRightTwo = view.findViewById(R.id.checkbox_right_two_logo);
        imageViewRightThree = view.findViewById(R.id.checkbox_right_three_logo);
        imageViewRightFour = view.findViewById(R.id.checkbox_right_four_logo);
        imageViewRightFive = view.findViewById(R.id.checkbox_right_five_logo);
        imageViewRightSix = view.findViewById(R.id.checkbox_right_six_logo);

        // Left side
        checkBoxLeftOne.setText(getString(R.string.car_name));
        checkBoxLeftTwo.setText(getString(R.string.clothes_name));
        checkBoxLeftThree.setText(getString(R.string.cosmetic_name));
        checkBoxLeftFour.setText(getString(R.string.design_name));
        checkBoxLeftFive.setText(getString(R.string.film_name));
        checkBoxLeftSix.setText(getString(R.string.fun_name));

        textViewLeftOne.setText(getString(R.string.car_expression));
        textViewLeftTwo.setText(getString(R.string.clothes_expression));
        textViewLeftThree.setText(getString(R.string.cosmetic_expression));
        textViewLeftFour.setText(getString(R.string.design_expression));
        textViewLeftFive.setText(getString(R.string.film_expression));
        textViewLeftSix.setText(getString(R.string.fun_expression));

        imageViewLeftOne.setImageDrawable(getResources().getDrawable( R.drawable.category_car ));
        imageViewLeftTwo.setImageDrawable(getResources().getDrawable( R.drawable.category_clothes ));
        imageViewLeftThree.setImageDrawable(getResources().getDrawable( R.drawable.category_cosmetic ));
        imageViewLeftFour.setImageDrawable(getResources().getDrawable( R.drawable.category_design ));
        imageViewLeftFive.setImageDrawable(getResources().getDrawable( R.drawable.category_film ));
        imageViewLeftSix.setImageDrawable(getResources().getDrawable( R.drawable.category_fun ));

        // Right side
        checkBoxRightOne.setText(getString(R.string.furniture_name));
        checkBoxRightTwo.setText(getString(R.string.jewelry_name));
        checkBoxRightThree.setText(getString(R.string.machine_name));
        checkBoxRightFour.setText(getString(R.string.sports_name));
        checkBoxRightFive.setText(getString(R.string.tablet_name));
        checkBoxRightSix.setText(getString(R.string.other_name));

        textViewRightOne.setText(getString(R.string.furniture_expression));
        textViewRightTwo.setText(getString(R.string.jewelry_expression));
        textViewRightThree.setText(getString(R.string.machine_expression));
        textViewRightFour.setText(getString(R.string.sports_expression));
        textViewRightFive.setText(getString(R.string.tablet_expression));
        textViewRightSix.setText(getString(R.string.other_expression));

        imageViewRightOne.setImageDrawable(getResources().getDrawable( R.drawable.category_furniture ));
        imageViewRightTwo.setImageDrawable(getResources().getDrawable( R.drawable.category_jewelry ));
        imageViewRightThree.setImageDrawable(getResources().getDrawable( R.drawable.category_machine ));
        imageViewRightFour.setImageDrawable(getResources().getDrawable( R.drawable.category_sports ));
        imageViewRightFive.setImageDrawable(getResources().getDrawable( R.drawable.category_tablet ));
        imageViewRightSix.setImageDrawable(getResources().getDrawable( R.drawable.category_other ));

        Bundle bundle = this.getArguments();
        myCategories = bundle.getStringArrayList("category"); // if user is asking question for first time, it will be NULL
        checkedBox();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage(getString(R.string.loading) + "");
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar);
                progressDialog.show();
                uploadCategory();
            }
        });

        /*
        // Check listener
        checkBoxLeftOne.setOnCheckedChangeListener(this);
        checkBoxLeftTwo.setOnCheckedChangeListener(this);
        checkBoxLeftThree.setOnCheckedChangeListener(this);
        checkBoxLeftFour.setOnCheckedChangeListener(this);
        checkBoxLeftFive.setOnCheckedChangeListener(this);
        checkBoxLeftSix.setOnCheckedChangeListener(this);

        checkBoxRightOne.setOnCheckedChangeListener(this);
        checkBoxRightTwo.setOnCheckedChangeListener(this);
        checkBoxRightThree.setOnCheckedChangeListener(this);
        checkBoxRightFour.setOnCheckedChangeListener(this);
        checkBoxRightFive.setOnCheckedChangeListener(this);
        checkBoxRightSix.setOnCheckedChangeListener(this);
        */

        return view;
    }

    private void uploadCategory() {
        ArrayList<String> newCategories = new ArrayList<>();
        if(checkBoxLeftOne.isChecked())
            newCategories.add("CAR");
        if(checkBoxLeftTwo.isChecked())
            newCategories.add("CLOTHES");
        if(checkBoxLeftThree.isChecked())
            newCategories.add("COSMETIC");
        if(checkBoxLeftFour.isChecked())
            newCategories.add("DESIGN");
        if(checkBoxLeftFive.isChecked())
            newCategories.add("FILM and BOOK");
        if(checkBoxLeftSix.isChecked())
            newCategories.add("FUNNY");

        if(checkBoxRightOne.isChecked())
            newCategories.add("FURNITURE");
        if(checkBoxRightTwo.isChecked())
            newCategories.add("GLASSES");
        if(checkBoxRightThree.isChecked())
            newCategories.add("ELECTRONIC");
        if(checkBoxRightFour.isChecked())
            newCategories.add("SPORT");
        if(checkBoxRightFive.isChecked())
            newCategories.add("PHONE and PC");
        if(checkBoxRightSix.isChecked())
            newCategories.add("OTHER");

        if(newCategories == null)
        {
            Log.d(TAG, "newCategories is null");
            return;
        }
        Log.d(TAG, "newCategories is " + newCategories.toString());

        DocumentReference DocumentRefUser = firebaseFirestore.collection("users").document("" + FirebaseAuth.getInstance().getUid());
        DocumentRefUser.update("category", newCategories).addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Log.d(TAG, "Categories was uploaded.");
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                toast("Error " + e.getMessage());
            }
        });
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

    private void checkedBox()
    {
        if(myCategories == null) {
            checkBoxLeftOne.setChecked(true);
            checkBoxLeftTwo.setChecked(true);
            checkBoxLeftThree.setChecked(true);
            checkBoxLeftFour.setChecked(true);
            checkBoxLeftFive.setChecked(true);
            checkBoxLeftSix.setChecked(true);

            checkBoxRightOne.setChecked(true);
            checkBoxRightTwo.setChecked(true);
            checkBoxRightThree.setChecked(true);
            checkBoxRightFour.setChecked(true);
            checkBoxRightFive.setChecked(true);
            checkBoxRightSix.setChecked(true);
        }
        else
        {
            for(int i=0; i<myCategories.size(); i++)
            {
                if(myCategories.get(i).trim().equals("CAR")) {
                    checkBoxLeftOne.setChecked(true);
                }
                else if(myCategories.get(i).trim().equals("CLOTHES")) {
                    checkBoxLeftTwo.setChecked(true);
                }
                else if(myCategories.get(i).trim().equals("COSMETIC")) {
                    checkBoxLeftThree.setChecked(true);
                }
                else if(myCategories.get(i).trim().equals("DESIGN")) {
                    checkBoxLeftFour.setChecked(true);
                }
                else if(myCategories.get(i).trim().equals("FILM and BOOK")) {
                    checkBoxLeftFive.setChecked(true);
                }
                else if(myCategories.get(i).trim().equals("FUNNY")) {
                    checkBoxLeftSix.setChecked(true);
                }
                else if(myCategories.get(i).trim().equals("FURNITURE")) {
                    checkBoxRightOne.setChecked(true);
                }
                else if(myCategories.get(i).trim().equals("GLASSES")) {
                    checkBoxRightTwo.setChecked(true);
                }
                else if(myCategories.get(i).trim().equals("ELECTRONIC")) {
                    checkBoxRightThree.setChecked(true);
                }
                else if(myCategories.get(i).trim().equals("SPORT")) {
                    checkBoxRightFour.setChecked(true);
                }
                else if(myCategories.get(i).trim().equals("PHONE and PC")) {
                    checkBoxRightFive.setChecked(true);
                }
                else if(myCategories.get(i).trim().equals("OTHER")) {
                    checkBoxRightSix.setChecked(true);
                }
            }
        }
    }

    /*
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()){
            // left side
            case R.id.checkbox_left_one:
                if(isChecked == true) {

                }
                else{

                }
                break;
            case R.id.checkbox_left_two:
                if(isChecked == true) {

                }
                else{

                }
                break;
            case R.id.checkbox_left_three:
                if(isChecked == true) {

                }
                else{

                }
                break;
            case R.id.checkbox_left_four:
                if(isChecked == true) {

                }
                else{

                }
                break;
            case R.id.checkbox_left_five:
                if(isChecked == true) {

                }
                else{

                }
                break;
            case R.id.checkbox_left_six:
                if(isChecked == true) {

                }
                else{

                }
                break;
            // right side
            case R.id.checkbox_right_one:
                if(isChecked == true) {

                }
                else{

                }
                break;
            case R.id.checkbox_right_two:
                if(isChecked == true) {

                }
                else{

                }
                break;
            case R.id.checkbox_right_three:
                if(isChecked == true) {

                }
                else{

                }
                break;
            case R.id.checkbox_right_four:
                if(isChecked == true) {

                }
                else{

                }
                break;
            case R.id.checkbox_right_five:
                if(isChecked == true) {

                }
                else{

                }
                break;
            case R.id.checkbox_right_six:
                if(isChecked == true) {

                }
                else{

                }
                break;
        }
    }
*/
}