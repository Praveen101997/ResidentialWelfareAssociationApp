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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;
import com.vegabond.residentialwelfareassociation.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

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

    }

    public void addResidenceAndSociety(){
        listResidence.add("Owner");
        listResidence.add("Tenant");

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
