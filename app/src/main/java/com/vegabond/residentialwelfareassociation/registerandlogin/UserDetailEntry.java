package com.vegabond.residentialwelfareassociation.registerandlogin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.vegabond.residentialwelfareassociation.MainActivity;
import com.vegabond.residentialwelfareassociation.R;
import com.vegabond.residentialwelfareassociation.apirequest.apiRegistration;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailEntry extends AppCompatActivity {

    Button button;

    CircleImageView profilePic;
    TextView profileName;
    private ProgressDialog Loadingbar;
    private static final int GalleryPick = 1;



    private AutoCompleteTextView ATname,ATemailid,ATcontact,ATadult,ATchild,ATflatno,ATownername;
    private Spinner ATresidenceType;
    private MultiAutoCompleteTextView ATsocietyName;
    private LinearLayout LLOwner;

    ArrayList<String> listResidence=new ArrayList<String>();
    ArrayList<String> listSociety=new ArrayList<String>();

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail_entry);

        profilePic = findViewById(R.id.user_detail_profile_picture);
        Loadingbar = new ProgressDialog(this);

        initializeGUI();

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");

                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),GalleryPick);

            }
        });

        ATresidenceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String resType = adapterView.getItemAtPosition(i).toString();
                if (resType.equals("Tenant")){
                    LLOwner.setVisibility(View.VISIBLE);
                }else{
                    LLOwner.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addResidenceAndSociety();
        addToSpinner();
        adapterSociety();


        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        final GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra("userGoogle");

        if (user == null) {
            email = googleSignInAccount.getEmail();
        } else {
            email = user.getEmail();
        }

        ATemailid.setText(email);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("regComplete");
                    UserRef.setValue(true);
                } else {
                    DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(googleSignInAccount.getEmail().replace(".", "")).child("regComplete");
                    UserRef.setValue(true);
                }


                String emailid = ATemailid.getText().toString();
                String name = ATname.getText().toString();
                String contactno = ATcontact.getText().toString();
                String adultno = ATadult.getText().toString();
                String childno = ATchild.getText().toString();
                String flatno = ATflatno.getText().toString();
                String ownername = ATownername.getText().toString();
                int residencetypePos = ATresidenceType.getSelectedItemPosition();
                String residencetype = listResidence.get(residencetypePos);
                String societyname = ATsocietyName.getText().toString();

                Log.d("Register", "Email :" + emailid + "\nName :" + name + "\nContact No" + contactno + "\nAdult No :" + adultno + "\nChild No :" + childno + "\nFlat No" + flatno + "\nOwner Name :" + ownername + "\nResidence Type :" + residencetype + "\nSocietyName :" + societyname);

                Toast.makeText(getApplicationContext(), "Email :" + emailid + "\nName :" + name + "\nContact No" + contactno + "\nAdult No :" + adultno + "\nChild No :" + childno + "\nFlat No" + flatno + "\nOwner Name :" + ownername + "\nResidence Type :" + residencetype + "\nSocietyName :" + societyname, Toast.LENGTH_LONG).show();


                String json = null;
                try {
                    json = jsonConvert(emailid,name,contactno,adultno,childno,flatno,societyname,residencetype,ownername);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("Check5","JSON - "+json);

                apiRegistration api = new apiRegistration();


                String res = "res";
                try {
                    res = api.userdetailspost("https://residenceassociation.herokuapp.com/UserProfiles",json);

                } catch (IOException e) {
                    Log.d("Check5","In catch - ");
                    e.printStackTrace();
                }
                Log.d("Check5","Response - "+res);
                startActivity(new Intent(UserDetailEntry.this, MainActivity.class));
            }







        });

    }

    //==============================================================================================

    public static String jsonConvert(String email,String name,String contact,String adult,String child,String flatno,String societyname,String residenttype,String ownername) throws JSONException {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("Email", email);
        jsonObject.put("Name", name);
        jsonObject.put("ContactNo", contact);
        jsonObject.put("AdultCount", adult);
        jsonObject.put("ChildCount", child);
        jsonObject.put("FlatNo", flatno);
        jsonObject.put("SocietyName", societyname);
        jsonObject.put("ResidentType", residenttype);
        jsonObject.put("OwnerName", ownername);

        Log.d("check5","JSON STRING : "+jsonObject.toString());

        return jsonObject.toString();
    }


    //==============================================================================================

    public void addResidenceAndSociety(){
        listResidence.add("Tenant");
        listResidence.add("Owner");

        listSociety.add("Society1");
        listSociety.add("Society2");
        listSociety.add("Society3");
        listSociety.add("Society4");
        listSociety.add("Society5");
        listSociety.add("Society6");
        listSociety.add("Society7");
        listSociety.add("Society8");
        listSociety.add("Society9");
        listSociety.add("Society10");

    }

    // Add the data items to the spinner
    void addToSpinner()
    {
        // Adapter for spinner
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listResidence);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ATresidenceType.setAdapter(adapter);
    }

    // setting adapter for auto complete text views
    void adapterSociety()
    {
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listSociety);
        ATsocietyName.setAdapter(adapter);
        hideKeyBoard();
    }

    // hide keyboard on selecting a suggestion
    public void hideKeyBoard()
    {
        ATsocietyName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });
    }




    private void initializeGUI(){
        ATname = findViewById(R.id.up_Name);
        ATemailid = findViewById(R.id.up_Email);
        ATcontact = findViewById(R.id.up_MobileNo);
        ATadult = findViewById(R.id.up_AdultCount);
        ATchild = findViewById(R.id.up_ChildCount);
        ATflatno = findViewById(R.id.up_FlatNo);
        ATresidenceType = findViewById(R.id.up_ResidentType);
        ATsocietyName = findViewById(R.id.up_SocietyName);
        ATsocietyName.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        ATsocietyName.setThreshold(2);
        ATownername = findViewById(R.id.up_OwnerName);
        LLOwner = findViewById(R.id.LLOwnerName);
        button = findViewById(R.id.nextButton);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== GalleryPick&& resultCode==RESULT_OK && data!=null) {
            Uri ImageUri = data.getData();
            Uri destUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
            Toast.makeText(getApplicationContext(),"Crop1",Toast.LENGTH_SHORT).show();
            UCrop.of(ImageUri, destUri)
                    .withAspectRatio(1, 1)
                    .start(this);
            Toast.makeText(getApplicationContext(),"Crop2",Toast.LENGTH_SHORT).show();

        }
        if (requestCode == UCrop.REQUEST_CROP) {
            Loadingbar.setTitle("Set Profile Image");
            Loadingbar.setMessage("Processing Profile Pic");
            Loadingbar.setCanceledOnTouchOutside(false);
            Loadingbar.show();

            final Uri resultUri = UCrop.getOutput(data);
            Picasso.get().load(resultUri).into(profilePic);

            Loadingbar.dismiss();

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }
}
