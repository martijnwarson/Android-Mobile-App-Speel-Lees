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
import android.util.Log;
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

// PORTRAIT
public class ClientDetailActivity extends AppCompatActivity implements View.OnClickListener {
    Client client;
    Button btnUpdate, btnDelete;
    TextView tv_birthdate, tv_firstname, tv_lastname, tvAddress, tvPostalCode, tvCity;
    String clientId, firstname, lastname, birthdate, profilePic, address, zipcode, city;
    StorageReference storageReference;
    ImageView iv_ProfilePicture;
    private static final String TAG = "ClientDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_detail);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Back btn

        Log.i(TAG, "Portrait view succeed");

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Intent intent = new Intent(ClientDetailActivity.this, ClientListActivity.class);
            Log.i(TAG, "Activity to landscape");
            startActivity(intent);
        }

        init();
        fillData();

        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { //back btn
            onBackPressed();
            Log.i(TAG, "Back to previous activity succeed");
            return true;
        }

        Log.e(TAG, "Something went wrong");
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        Log.i(TAG, "Initialized successfully");
        client = new Client();
        tv_birthdate = findViewById(R.id.birthdate_database);
        tv_firstname = findViewById(R.id.firstname_database);
        tv_lastname = findViewById(R.id.lastname_database);
        tvAddress = findViewById(R.id.address_database);
        tvPostalCode = findViewById(R.id.postalCode_database);
        tvCity = findViewById(R.id.city_database);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        storageReference = FirebaseStorage.getInstance().getReference();
        iv_ProfilePicture = findViewById(R.id.iv_profilePic);

    }

    //Data vullen van vorige view
    private void fillData() {
        Log.i(TAG, "Fields filled successfully");
        clientId = getIntent().getStringExtra("clientId");
        firstname = getIntent().getStringExtra("firstname");
        lastname = getIntent().getStringExtra("lastname");
        birthdate = getIntent().getStringExtra("birthdate");
        //profilePic = getIntent().getStringExtra("profilePic");
        address = getIntent().getStringExtra("address");
        zipcode = getIntent().getStringExtra("postalCode");
        city = getIntent().getStringExtra("city");


        String name = firstname + " " + lastname;
        setTitle(name);

        /*Picasso.get().load(profilePic).into(iv_ProfilePicture);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) { //foto tonen (werkt nog niet!)
            int resid = bundle.getInt("profilePic");
            iv_ProfilePicture.setImageResource(resid);
        }*/

        //profilePic = getIntent().getStringExtra("profilePic");

        //Data inladen
        tv_birthdate.setText(birthdate);
        tv_firstname.setText(firstname);
        tv_lastname.setText(lastname);
        tvAddress.setText(address);
        tvPostalCode.setText(zipcode);
        tvCity.setText(city);

        fillImageView();
    }

    private void fillImageView() {
        String imageName = clientId;
        StorageReference storageRef = storageReference.child(imageName);

        final long ONE_MEGABYTE = 1024 * 1024; // Zo groot mag de foto zijn die opgeslagen wordt in de applicatie
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.i(TAG, "Image downloaded from Firebase Storage successfully");

                // Foto in ImageView zetten
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                iv_ProfilePicture.setImageBitmap(bitmap);

                Log.i(TAG, "Image posted in ImageView successfully");
            }
        });
    }

   public void onClick(View v) {
       if (v.getId() == R.id.btnUpdate) {
           // Naar Activity gaan om gegevens van lid te wijzigen
           Log.i(TAG, "Update-button clicked");

           goToUpdateActivity();
       }
       else if (v.getId() == R.id.btnDelete) {
           // Lid verwijderen
           Log.i(TAG, "Delete-button clicked");

           initializeMember();
           Log.i(TAG, "Delete: Member initialized successfully");

           createDeleteDialog(client.getClientId());
       }

    }

    private void goToUpdateActivity() {
        Intent intentToUpdateActivity = new Intent(ClientDetailActivity.this, UpdateActivity.class);

        // Gegevens meegeven aan de intent die getoond moeten worden in UpdateActivity
        intentToUpdateActivity.putExtra("clientId", clientId    );
        intentToUpdateActivity.putExtra("firstname", firstname);
        intentToUpdateActivity.putExtra("lastname", lastname);
        intentToUpdateActivity.putExtra("birthdate", birthdate);
        intentToUpdateActivity.putExtra("address", address);
        intentToUpdateActivity.putExtra("zipcode", zipcode);
        intentToUpdateActivity.putExtra("city", city);


        // Naar UpdateActivity gaan
        startActivity(intentToUpdateActivity);
    }

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
                Log.i(TAG, "Delete member confirmed");

                deleteClient(clientId); // Lid verwijderen uit database
            }
        });

        alertDialog.setNegativeButton("Nee", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "Delete member denied");
            }
        });

        alertDialog.create().show();
    }

    private void deleteClient(String clientId) {
        FirebaseDatabaseHelper databaseHelper = new FirebaseDatabaseHelper(clientId);
        databaseHelper.deleteClient();

        String toastMessage = client.getFirstname() + " " + client.getLastname() + " is verwijderd";
        Toast.makeText(ClientDetailActivity.this, toastMessage, Toast.LENGTH_LONG).show();
        Log.i(TAG, "Member deleted succesfully");

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
                Log.i(TAG, "Profilepicture deleted successfully");
            }
        });
    }

}
