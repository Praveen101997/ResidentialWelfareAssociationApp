package com.vegabond.residentialwelfareassociation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vegabond.residentialwelfareassociation.registerandlogin.LoginActivity;
import com.vegabond.residentialwelfareassociation.registerandlogin.UserDetailEntry;

public class MainActivity extends AppCompatActivity {

    private Button btnLogout;
    private FirebaseAuth firebaseAuth;
    private TextView emailandusername;

    private String email,uname;
    private FirebaseUser user;
    DatabaseReference UserRef;

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


            UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(googleSignInAccount.getEmail().toString().replace(".", ""));
            Log.d("check1", googleSignInAccount.getEmail().toString().replace(".", ""));
            //===============================================
            UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("regComplete").getValue()!=null){
                        Log.d("check1", "if");
                        String value = dataSnapshot.child("regComplete").getValue().toString();
                        Log.d("check1", "value = " + value);
                        if (value.equals("false")) {
                            Intent intent = new Intent(MainActivity.this, UserDetailEntry.class);
                            intent.putExtra("userGoogle", googleSignInAccount);
                            startActivity(intent);
                        }
                    }else{
                        Log.d("check1", "else");
                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
//                        UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(googleSignInAccount.getEmail().toString().replace(".", ""));
//                        UserRef.child("regComplete").setValue(false);
//                        String value = dataSnapshot.child("regComplete").getValue().toString();
//                        Log.d("check1", "value = " + value);
//                        if (value.equals("false")) {
//                            Intent intent = new Intent(MainActivity.this, UserDetailEntry.class);
//                            intent.putExtra("userGoogle", googleSignInAccount);
//                            startActivity(intent);
//                        }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_LONG).show();
                }
            });

            //===============================================



        }else{
            email = user.getEmail();
            uname = "";

            UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
            Log.d("check1", user.getUid());
            //===============================================
            UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.child("regComplete").getValue().toString();
                    Log.d("check1", "value = " + value);
                    if (value.equals("false")) {
                        startActivity(new Intent(MainActivity.this, UserDetailEntry.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_LONG).show();
                }
            });

            //===============================================
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
                    if (LoginActivity.googleSignInClient != null) {
                        LoginActivity.googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //On Succesfull signout we navigate the user back to LoginActivity
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                    } else {

                    }


                }


            }
        });

    }
}
