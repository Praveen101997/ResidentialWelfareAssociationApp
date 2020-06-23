package com.vegabond.residentialwelfareassociation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vegabond.residentialwelfareassociation.registerandlogin.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnLogout;
    private FirebaseAuth firebaseAuth;
    private TextView emailandusername;

    private String email,uname;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailandusername = findViewById(R.id.textView);

        btnLogout = findViewById(R.id.btnLogout);
        firebaseAuth = FirebaseAuth.getInstance();

        user = firebaseAuth.getCurrentUser();
        final GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra("userGoogle");

        if (user == null&&googleSignInAccount==null){
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }else if (user == null){
            email = googleSignInAccount.getEmail();
            uname = googleSignInAccount.getDisplayName();
        }else{
            email = user.getEmail();
            uname = "";
        }

        emailandusername.setText(email+"-"+uname);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                if (user!=null) {
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }else {
                    LoginActivity.googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //On Succesfull signout we navigate the user back to LoginActivity
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });

                }


            }
        });

    }
}
