package root.polinim.outer;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import root.polinim.R;
import root.polinim.ask.ImageInfo;
import root.polinim.ask.QuestionPojo;
import root.polinim.ask.User;

public class OuterPostAdapter extends RecyclerView.Adapter<OuterPostAdapter.MyViewHolder> {

    private List<QuestionPojo> questionPojoList;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private ProgressDialog progressDialog;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.outer_post_list_row, parent, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView outerProfile, outerImageLeft, outerImageRight;
        public TextView outerNickName, outerTime, outerLikeNumber, outerCommentNumber;

        public MyViewHolder(View view) {
            super(view);

            outerProfile = view.findViewById(R.id.circleView_post_profile);
            outerImageLeft = view.findViewById(R.id.image_post_left);
            outerImageRight = view.findViewById(R.id.image_post_right);

            outerNickName = (TextView) view.findViewById(R.id.post_profile_nickname);
            outerTime = (TextView) view.findViewById(R.id.post_time);
            outerLikeNumber = (TextView) view.findViewById(R.id.total_like_number);
            outerCommentNumber = (TextView) view.findViewById(R.id.total_comment_number);
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        QuestionPojo questionPojo = questionPojoList.get(position);

        getImages(holder, questionPojo);

        int totalLikeNumber =  questionPojo.getImageInfo().getFirstImageLikeNumber() + questionPojo.getImageInfo().getSecondImageLikeNumber();
        holder.outerNickName.setText( questionPojo.getUser().getNickname() + "");
        holder.outerTime.setText( questionPojo.getDate() + "");
        holder.outerLikeNumber.setText( totalLikeNumber + "" );
        holder.outerCommentNumber.setText( questionPojo.getImageInfo().getTotalCommentNumber() + "");
    }

    public OuterPostAdapter(List<QuestionPojo> questionPojoList) {
        this.questionPojoList = questionPojoList;
    }
 
    @Override
    public int getItemCount() {
        return questionPojoList.size();
    }

    /*
    public void addItem(String country) {
        countries.add(country);
        notifyItemInserted(countries.size());
    }

    public void removeItem(int position) {
        countries.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, countries.size());
    }

    */

    // download file as a byte array
    private void getImages(final MyViewHolder holder, final QuestionPojo questionPojo) {

        final long ONE_MEGABYTE = 1024 * 512;

        if (questionPojo.getUser().getProfileLink() == null || questionPojo.getUser().getProfileLink() == "") {
            holder.outerProfile.setImageResource(R.drawable.user);
        }
        else {
            storageReference = firebaseStorage.getReferenceFromUrl("gs://polinim-59f69.appspot.com").child("/profile/" + questionPojo.getUser().getUid());

            storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    holder.outerProfile.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }

        storageReference = firebaseStorage.getReferenceFromUrl("gs://polinim-59f69.appspot.com").child("/question/" + questionPojo.getImageInfo().getFirstImageLink() );
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.outerImageLeft.setImageBitmap(bitmap);
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
                holder.outerImageRight.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
}