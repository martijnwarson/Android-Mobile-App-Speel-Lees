package android.example.speelenlees.activities.master;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.example.speelenlees.R;
import android.example.speelenlees.activities.UpdateActivity;
import android.example.speelenlees.data.FirebaseDatabaseHelper;
import android.example.speelenlees.domain.Client;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

// CLIENT DETAILS - PORTRAIT
public class ClientDetailActivity extends AppCompatActivity implements View.OnClickListener {
    Client client;
    Button btnUpdate;
    //Button btnDelete;
    TextView tv_birthdate;
    TextView tv_firstname;
    TextView tv_lastname;
    TextView tv_address;
    TextView tv_zipcode;
    TextView tv_city;
    String clientId;
    String firstname;
    String lastname;
    String birthdate;
    String profilePic;
    String address;
    String zipcode;
    String city;
    StorageReference storageReference;
    ImageView iv_profile_pic;
    //private static final String TAG = "ClientDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_detail); //bind
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // terug-knop

        //Log.i(TAG, "Portrait view succeed");

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Intent intent = new Intent(ClientDetailActivity.this, ClientListActivity.class);
            //Log.i(TAG, "Activity to landscape");
            startActivity(intent);
        }

        initialize();
        fillViewWithData();
        btnUpdate.setOnClickListener(this);
        //btnDelete.setOnClickListener(this);
    }

    private void initialize() {
        //Log.i(TAG, "Initialized successfully");

        //wijzig btn
        btnUpdate = findViewById(R.id.btn_update);
        client = new Client();
        tv_birthdate = findViewById(R.id.birthdate_firebase);
        tv_firstname = findViewById(R.id.firstname_firebase);
        tv_lastname = findViewById(R.id.lastname_firebase);
        tv_address = findViewById(R.id.address_firebase);
        tv_zipcode = findViewById(R.id.zipcode_firebase);
        tv_city = findViewById(R.id.city_firebase);
        //btnDelete = findViewById(R.id.btnDelete);

        //afbeelding uit FireStorage halen
        storageReference = FirebaseStorage.getInstance().getReference();
        iv_profile_pic = findViewById(R.id.iv_profile_pic);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { //back btn
            onBackPressed();
            //Log.i(TAG, "Back to previous activity succeed");
            return true;
        }

        //Log.e(TAG, "Something went wrong");
        return super.onOptionsItemSelected(item);
    }



    //Gegevens vullen
    private void fillViewWithData() {
        //Log.i(TAG, "Fields filled successfully");
        clientId = getIntent().getStringExtra("clientId");
        firstname = getIntent().getStringExtra("firstname");
        lastname = getIntent().getStringExtra("lastname");
        birthdate = getIntent().getStringExtra("birthdate");
        //profilePic = getIntent().getStringExtra("profilePic");
        address = getIntent().getStringExtra("address");
        zipcode = getIntent().getStringExtra("postalCode");
        city = getIntent().getStringExtra("city");

        String full_name = firstname + " " + lastname;


        /*Picasso.get().load(profilePic).into(iv_profile_pic);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) { //foto tonen (werkt nog niet!)
            int resid = bundle.getInt("profilePic");
            iv_profile_pic.setImageResource(resid);
        }*/

        //profilePic = getIntent().getStringExtra("profilePic");

        //Data inladen
        tv_birthdate.setText(birthdate);
        tv_firstname.setText(firstname);
        tv_lastname.setText(lastname);
        tv_address.setText(address);
        tv_zipcode.setText(zipcode);
        tv_city.setText(city);

        setTitle(full_name); //titel instellen (full_name)

        //fillImageView();

        String imageName = clientId;
        StorageReference storageRef = storageReference.child(imageName);

        final long ONE_MEGABYTE = 1024 * 1024; // Zo groot mag de foto zijn die opgeslagen wordt in de applicatie
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                //Log.i(TAG, "Image downloaded from Firebase Storage successfully");

                // Foto in ImageView zetten
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                iv_profile_pic.setImageBitmap(bitmap);

                //Log.i(TAG, "Image posted in ImageView successfully");
            }
        });
    }

   /* private void fillImageView() {
        String imageName = clientId;
        StorageReference storageRef = storageReference.child(imageName);

        final long ONE_MEGABYTE = 1024 * 1024; // Zo groot mag de foto zijn die opgeslagen wordt in de applicatie
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                //Log.i(TAG, "Image downloaded from Firebase Storage successfully");

                // Foto in ImageView zetten
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                iv_profile_pic.setImageBitmap(bitmap);

                //Log.i(TAG, "Image posted in ImageView successfully");
            }
        });
    }*/

   public void onClick(View v) {
       if (v.getId() == R.id.btn_update) {
           // Naar Activity gaan om gegevens van lid te wijzigen
           //Log.i(TAG, "Update-button clicked");

           goToUpdate();
       }
       /*else if (v.getId() == R.id.btnDelete) {
           // Lid verwijderen
           //Log.i(TAG, "Delete-button clicked");

           initializeMember();
           //Log.i(TAG, "Delete: Member initialized successfully");

           createDeleteDialog(client.getClientId());
       }*/

    }

    private void goToUpdate() {
        Intent intent = new Intent(ClientDetailActivity.this, UpdateActivity.class);

        intent.putExtra("clientId", clientId    );
        intent.putExtra("firstname", firstname);
        intent.putExtra("lastname", lastname);
        intent.putExtra("birthdate", birthdate);
        intent.putExtra("address", address);
        intent.putExtra("zipcode", zipcode);
        intent.putExtra("city", city);

        startActivity(intent);
    }

    /*
    private void initializeMember() {
        client.setClientId(clientId);
        client.setFirstname(firstname);
        client.setLastname(lastname);
        client.setBirthdate(birthdate);
        client.setAddress(address);
        client.setZipcode(zipcode);
        client.setCity(city);
    }

    private void createDeleteDialog(final String clientId) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Weet je zeker dat je " + client.getFirstname() + " " + client.getLastname() + " wilt verwijderen?");
        alertDialog.setCancelable(false);

        // Mogelijke knoppen (JA/NEE)
        alertDialog.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Log.i(TAG, "Delete member confirmed");

                deleteClient(clientId); // Lid verwijderen uit database
            }
        });

        alertDialog.setNegativeButton("Nee", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Log.i(TAG, "Delete member denied");
            }
        });

        alertDialog.create().show();
    }

    private void deleteClient(String clientId) {
        FirebaseDatabaseHelper databaseHelper = new FirebaseDatabaseHelper(clientId);
        databaseHelper.deleteClient();

        String toastMessage = client.getFirstname() + " " + client.getLastname() + " is verwijderd";
        Toast.makeText(ClientDetailActivity.this, toastMessage, Toast.LENGTH_LONG).show();
        //Log.i(TAG, "Member deleted succesfully");

        deleteProfilePicture();

        // Naar MemberListActivity gaan
        Intent intentToMemberListActivity = new Intent(ClientDetailActivity.this, ClientListActivity.class);
        startActivity(intentToMemberListActivity);
    }

    private void deleteProfilePicture() {
        StorageReference pictureRef = storageReference.child(clientId);

        pictureRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Log.i(TAG, "Profilepicture deleted successfully");
            }
        });
    }

     */

}
