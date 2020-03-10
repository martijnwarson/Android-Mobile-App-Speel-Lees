package android.example.speelenlees.activities.master;

import android.content.Intent;
import android.content.res.Configuration;
import android.example.speelenlees.R;
import android.example.speelenlees.activities.UpdateActivity;
import android.example.speelenlees.domain.Client;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

// CLIENT DETAILS - PORTRAIT
public class ClientDetailActivity extends AppCompatActivity implements View.OnClickListener {
    Client client;
    Button btn_update;
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
    String address;
    String zipcode;
    String city;
    StorageReference storageReference;
    ImageView iv_profile_pic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_detail); //bind
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // terug-knop


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Intent intent = new Intent(ClientDetailActivity.this, ClientListActivity.class);

            startActivity(intent);
        }

        initialize();
        fillViewWithData();
        btn_update.setOnClickListener(this);
    }

    private void initialize() {

        //wijzig btn
        btn_update = findViewById(R.id.btn_update);
        client = new Client();
        tv_birthdate = findViewById(R.id.birthdate_firebase);
        tv_firstname = findViewById(R.id.firstname_firebase);
        tv_lastname = findViewById(R.id.lastname_firebase);
        tv_address = findViewById(R.id.address_firebase);
        tv_zipcode = findViewById(R.id.zipcode_firebase);
        tv_city = findViewById(R.id.city_firebase);

        //afbeelding uit FireStorage halen
        storageReference = FirebaseStorage.getInstance().getReference();
        iv_profile_pic = findViewById(R.id.iv_profile_pic);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { //back btn
            onBackPressed();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }



    //Gegevens vullen
    private void fillViewWithData() {
        clientId = getIntent().getStringExtra("clientId");
        firstname = getIntent().getStringExtra("firstname");
        lastname = getIntent().getStringExtra("lastname");
        birthdate = getIntent().getStringExtra("birthdate");
        address = getIntent().getStringExtra("address");
        zipcode = getIntent().getStringExtra("postalCode");
        city = getIntent().getStringExtra("city");

        String full_name = firstname + " " + lastname;


        //Data inladen
        tv_birthdate.setText(birthdate);
        tv_firstname.setText(firstname);
        tv_lastname.setText(lastname);
        tv_address.setText(address);
        tv_zipcode.setText(zipcode);
        tv_city.setText(city);

        setTitle(full_name); //titel instellen (full_name)


        String imageName = clientId;
        StorageReference storageRef = storageReference.child(imageName);

        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                // Foto in ImageView zetten
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                iv_profile_pic.setImageBitmap(bitmap);


            }
        });
    }


   public void onClick(View v) {
       if (v.getId() == R.id.btn_update) {

           goToUpdate();
       }

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


}
