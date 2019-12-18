package root.polinim.entry;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import root.polinim.R;

public class ForgotPassword extends Fragment
{
    private Button btnSendEmail;
    private AutoCompleteTextView txtEmail;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_pw, container, false);

        auth = FirebaseAuth.getInstance();

        txtEmail = (AutoCompleteTextView) view.findViewById(R.id.forgot_pw_email);
        btnSendEmail = (Button) view.findViewById(R.id.btn_send_mail);

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.sendPasswordResetEmail(txtEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    toast(getString(R.string.sent_successfully));
                                    //Toast.makeText(getActivity(), "" + getString(R.string.sent_successfully), Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getActivity(), Login.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("activity", "forgotpasswordfragment");
                                    i.putExtras(bundle);
                                    startActivity(i);
                                } else {
                                    toast("Error");
                                    //Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        return view;
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