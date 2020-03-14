package android.example.speelenlees.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.example.speelenlees.R;
import android.example.speelenlees.activities.master.ClientListActivity;
import android.example.speelenlees.data.FirebaseDatabaseHelper;
import android.example.speelenlees.domain.Client;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    StorageReference storage_reference;
    Uri filePath;
    String date;
    Button btn_update;
    Button upload_btn;
    EditText et_firstname;
    EditText et_lastname;
    EditText et_address;
    EditText et_zipcode;
    EditText et_city;
    TextView tv_birthdate;
    String clientId;
    String firstname;
    String lastname;
    String address;
    String zipcode;
    String city;
    ImageView iv_profile_pic;
    Client client;
    boolean dateSelected;
    static final int PICK_IMAGE_REQUEST = 124;


   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // terug knop

       setTitle("Cliënt wijzigen");

        initialize();
        fillData();

        //iv_profile_pic.setOnClickListener(this);
        tv_birthdate.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        upload_btn.setOnClickListener(this);


        //Om geboortedatum op te halen
        if (savedInstanceState != null) {
            date = savedInstanceState.getString("my_birthdate");
            String birthdateString = "Geboortedatum: " + date;
            tv_birthdate.setText(birthdateString);
        }
    }

    private void initialize() {
        btn_update = findViewById(R.id.btn_update);
        et_firstname = findViewById(R.id.etFirstname);
        et_lastname = findViewById(R.id.etLastname);
        tv_birthdate = findViewById(R.id.tvDateSelector);
        et_address = findViewById(R.id.etAddress);
        et_zipcode = findViewById(R.id.etZipcode);
        et_city = findViewById(R.id.etCity);
        dateSelected = true;
        iv_profile_pic = findViewById(R.id.ivUpdateProfilePicture);
        upload_btn = findViewById(R.id.upload_btn);
        storage_reference = FirebaseStorage.getInstance().getReference();
    }

    public void onClick(View v) {

        if (v.getId()  == R.id.tvDateSelector) {
            showDatePicker();
        }  else if (v.getId() == R.id.upload_btn) {
            showFileChooser();
        } else  if (v.getId() == R.id.btn_update) {
            updateClient();
        }

    }

    // Back btn in navbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("my_birthdate", date); //nieuwe geboortedatum
    }

    private void fillData() {
        clientId = getIntent().getStringExtra("clientId");
        firstname = getIntent().getStringExtra("firstname");
        lastname = getIntent().getStringExtra("lastname");
        date = getIntent().getStringExtra("birthdate");
        address = getIntent().getStringExtra("address");
        zipcode = getIntent().getStringExtra("zipcode");
        city = getIntent().getStringExtra("city");

        // Verzamelde data in de tekstvakken zetten
        et_firstname.setText(firstname);
        et_lastname.setText(lastname);
        et_address.setText(address);
        et_zipcode.setText(zipcode);
        et_city.setText(city);

        String birthdateString = "Geboortedatum: " + date;
        tv_birthdate.setText(birthdateString);

        // Foto tonen in de imageView
        showProfilePicture();
    }


    private void showProfilePicture() {
        String image_name = clientId;
        StorageReference storageReference = storage_reference.child(image_name);

        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                iv_profile_pic.setImageBitmap(bitmap);
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(UpdateActivity.this, AlertDialog.THEME_HOLO_LIGHT, this, year, month, day);
        dialog.show();
    }


    private void updateClient() {
        if (validateUserInput()) {
            //Client maken
            client = new Client();
            client.setClientId(clientId);
            client.setFirstname(et_firstname.getText().toString().trim());
            client.setLastname(et_lastname.getText().toString().trim());
            client.setAddress(et_address.getText().toString().trim());
            String birthdateTextView = tv_birthdate.getText().toString().trim();
            date = birthdateTextView.substring(15);
            client.setBirthdate(date);
            client.setZipcode(et_zipcode.getText().toString().trim());
            client.setCity(et_city.getText().toString().trim());

            new FirebaseDatabaseHelper().updateClient(client);

            Toast.makeText(UpdateActivity.this, client.getFirstname() + " " + client.getLastname() + " is gewijzigd", Toast.LENGTH_LONG).show();

           goToClientListActivity();
        }
    }


    private boolean validateUserInput() {
        boolean bool = true;

        if (et_firstname.getText().length() < 2) {
            String firstname = "Voornaam";
            et_firstname.setError(firstname + " is te kort");
            bool = false;
        }

        if (et_lastname.getText().length() < 2) {
            String lastname = "Achternaam";
            et_lastname.setError(lastname + " is te kort");
            bool = false;
        }


        return bool;
    }

    private void InitializeMember() {
        client = new Client();
        client.setClientId(clientId);
        client.setFirstname(et_firstname.getText().toString().trim());
        client.setLastname(et_lastname.getText().toString().trim());
        client.setAddress(et_address.getText().toString().trim());
        String birthdateTextView = tv_birthdate.getText().toString().trim();
        date = birthdateTextView.substring(15);
        client.setBirthdate(date);
        client.setZipcode(et_zipcode.getText().toString().trim());
        client.setCity(et_city.getText().toString().trim());
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
       // Log.i(TAG, "Date selected successfully");
        dateSelected = true;
        tv_birthdate.setError(null);
        date = "" + dayOfMonth + "/" + (month + 1) + "/" + year;
        String result = "Geboortedatum: " + date;
        tv_birthdate.setText(result);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Kies een profielfoto"), PICK_IMAGE_REQUEST);
    }

    //na kiezen van afbeelding in filesystem
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            //foto uploaden naar storage
            if (filePath != null) {
                String pictureId = clientId;
                storage_reference = storage_reference.child(pictureId);

                storage_reference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateActivity.this, "Er is iets fout gegaan bij het uploaden van de profielfoto. Probeer opnieuw", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // Toon geselecteerde afbeelding in ImageView
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                iv_profile_pic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void goToClientListActivity() {
        Intent intent = new Intent(UpdateActivity.this, ClientListActivity.class);
        startActivity(intent);
    }
}
