package root.polinim.inner;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
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

import java.util.List;

import root.polinim.R;
import root.polinim.ask.QuestionPojo;

public class InnerPostAdapter extends RecyclerView.Adapter<InnerPostAdapter.MyViewHolder> {

    private List<CommentPojo> commentPojoList;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inner_post_list_row, parent, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView commentProfile;
        public TextView commentNickname, commentText;

        public MyViewHolder(View view) {
            super(view);

            commentProfile = view.findViewById(R.id.circleView_inner_comment_profile);

            commentNickname = (TextView) view.findViewById(R.id.inner_comment_profile_nickname);
            commentText = (TextView) view.findViewById(R.id.inner_comment_text);
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        CommentPojo commentPojo = commentPojoList.get(position);

        getImages(holder, commentPojo);
        //holder.commentNickname.setText( commentPojo.getNickname()+"");
        //holder.commentText.setText(  commentPojo.getComment()+"");
    }

    public InnerPostAdapter(List<CommentPojo> commentPojoList) {
        this.commentPojoList = commentPojoList;
    }
 
    @Override
    public int getItemCount() {
        return commentPojoList.size();
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
    private void getImages(final MyViewHolder holder, final CommentPojo commentPojo) {

        if (commentPojo.getProfileLink() == null || commentPojo.getProfileLink() == "") {
            holder.commentProfile.setImageResource(R.drawable.user);
            holder.commentNickname.setText( commentPojo.getNickname()+"");
            holder.commentText.setText(  commentPojo.getComment()+"");
        }
        else {
            storageReference = firebaseStorage.getReferenceFromUrl("gs://polinim-59f69.appspot.com").child("/profile/" + commentPojo.getUserUID());
            final long ONE_MEGABYTE = 1024 * 512;

            storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    holder.commentProfile.setImageBitmap(bitmap);
                    holder.commentNickname.setText( commentPojo.getNickname()+"");
                    holder.commentText.setText(  commentPojo.getComment()+"");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
    }
}