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
import android.util.Log;
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

    Button btnUpdate;
    EditText etFirstname, etLastname, etAddress, etZipcode, etCity;
    TextView tvBirthdate;
    String clientId, firstname, lastname, address, zipcode, city;
    Client client;
    boolean dateSelected;
    ImageView ivProfilePicture;
    StorageReference storageReference;
    static final int PICK_IMAGE_REQUEST = 124;
    Uri filePath;
    static final String TAG = "UpdateActivity";
    String date;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        Log.i(TAG, "Views initialized succesfully");

        fillFieldsWithData();

        btnUpdate.setOnClickListener(this);
        tvBirthdate.setOnClickListener(this);
        ivProfilePicture.setOnClickListener(this);

        if (savedInstanceState != null) {
           restoreSavedInstanceStates(savedInstanceState);
        }
    }

    private void init() {
        btnUpdate = findViewById(R.id.btnUpdate);
        etFirstname = findViewById(R.id.etFirstname);
        etLastname = findViewById(R.id.etLastname);
        tvBirthdate = findViewById(R.id.tvDateSelector);
        etAddress = findViewById(R.id.etAddress);
        etZipcode = findViewById(R.id.etZipcode);
        etCity = findViewById(R.id.etCity);
        dateSelected = true;
        ivProfilePicture = findViewById(R.id.ivUpdateProfilePicture);
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Gewijzigde geboortedatum
        outState.putString("my_birthdate", date);
    }

    private void restoreSavedInstanceStates(Bundle savedInstanceState) {
        // Gewijzigde geboortedatum
        date = savedInstanceState.getString("my_birthdate");
        String birthdateString = "Geboortedatum: " + date;
        tvBirthdate.setText(birthdateString);
    }

    private void fillFieldsWithData() {
        // Data verzamelen die aan de Intent (vanuit DetailActivity) waren meegegeven
        clientId = getIntent().getStringExtra("clientId");
        firstname = getIntent().getStringExtra("firstname");
        lastname = getIntent().getStringExtra("lastname");
        date = getIntent().getStringExtra("birthdate");
        address = getIntent().getStringExtra("address");
        zipcode = getIntent().getStringExtra("zipcode");
        city = getIntent().getStringExtra("city");


        // Titel van ActionBar aanpassen
        String titleName = "Wijzig " + firstname + " " + lastname;
        setTitle(titleName);

        // Verzamelde data in de tekstvakken zetten
        etFirstname.setText(firstname);
        etLastname.setText(lastname);
        String birthdateString = "Geboortedatum: " + date;
        tvBirthdate.setText(birthdateString);
        etAddress.setText(address);
        etZipcode.setText(zipcode);
        etCity.setText(city);

        // Foto tonen in de imageView
        fillImageView();
    }


    private void fillImageView() {
        String imageName = clientId;
        StorageReference storageRef = storageReference.child(imageName);

        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ivProfilePicture.setImageBitmap(bitmap);
            }
        });
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnUpdate) {
            // Gegevens van lid wijzigen
            updateMember();
        }
        else if (v.getId() == R.id.tvDateSelector) {
            // Dialoogvenster tonen om data te selecteren
            showDatePickerDialog();
        }
        else if (v.getId() == R.id.ivUpdateProfilePicture) {
            // Dialoogvenster tonen om afbeelding te selecteren
            showFileChooser();
        }
    }


    //Wanneer er op het pijltje in de ActionBar wordt geklikt ...
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateMember() {
        if (checkUserInputValidity()) {
           InitializeMember();

            new FirebaseDatabaseHelper().updateMember(client);

            Toast.makeText(UpdateActivity.this, client.getFirstname() + " " + client.getLastname() + " is gewijzigd", Toast.LENGTH_LONG).show();
            Log.i(TAG, "Member updated successfully");

           goToMemberListActivity();
        }
    }


    private boolean checkUserInputValidity() {
        boolean inputIsValid = true;

        if (etFirstname.getText().length() < 2) {
            String firstname = "Voornaam";
            etFirstname.setError("\"" + firstname + "\" moet minstens 2 karakters hebben");
            inputIsValid = false;
        }

        if (etLastname.getText().length() < 2) {
            String lastname = "Achternaam";
            etLastname.setError("\"" + lastname + "\" moet minstens 2 karakters hebben");
            inputIsValid = false;
        }

        if (!dateSelected) {
            tvBirthdate.setError("Selecteer uw geboortedatum");
            inputIsValid = false;
        }

        if (etAddress.getText().length() < 5) {
            String address = "Straat + huisnummer";
            etAddress.setError("\"" + address + "\" moet minstens 5 karakters hebben");
            inputIsValid = false;
        }

        boolean postalCodeIsNumber = true;
       /* try {
            Integer.parseInt(etZipcode.getText().toString());
        } catch (NumberFormatException e) {
            postalCodeIsNumber = false;
        }
        if (etZipcode.getText().length() != 5 || !postalCodeIsNumber) {
            String zipcode = "Postcode";
            etZipcode.setError("\"" + zipcode + "\" moet een getal bestaande uit 4 cijfers zijn");
            inputIsValid = false;
        }*/

        if (etCity.getText().length() < 2) {
            String city = "Gemeente";
            etCity.setError("\"" + city + "\" moet minstens 2 karakters hebben");
            inputIsValid = false;
        }

        if (inputIsValid) {
            Log.i(TAG, "Fields fileld in correctly");
        } else {
            Log.e(TAG, "Fields not filled in correctly");
        }

        return inputIsValid;
    }

    private void InitializeMember() {
        client = new Client();
        client.setClientId(clientId);
        client.setFirstname(etFirstname.getText().toString().trim());
        client.setLastname(etLastname.getText().toString().trim());
        client.setAddress(etAddress.getText().toString().trim());
        String birthdateTextView = tvBirthdate.getText().toString().trim();
        date = birthdateTextView.substring(15);
        client.setBirthdate(date);
        client.setZipcode(etZipcode.getText().toString().trim());
        client.setCity(etCity.getText().toString().trim());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(UpdateActivity.this, AlertDialog.THEME_HOLO_LIGHT, this, year, month, day);
        dialog.show();
    }


    // Deze methode wordt opgeroepen wanneer een datum geselecteerd wordt in de DatePickerDialog()-methode
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.i(TAG, "Date selected successfully");
        dateSelected = true;
        tvBirthdate.setError(null);
        date = "" + dayOfMonth + "/" + (month + 1) + "/" + year;
        String result = "Geboortedatum: " + date;
        tvBirthdate.setText(result);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecteer een afbeelding"), PICK_IMAGE_REQUEST);
    }


    // Deze methode wordt uitgevoerd als de gebruiker een afbeelding geselecteerd heeft in showFileChooser()-methode
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            // Afbeelding uploaden in database. Dit wordt hier al gedaan omdat er een beetje vertraging zit in het updaten van de foto in de storage zelf
            uploadPicture();

            // Toon geselecteerde afbeelding in ImageView
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ivProfilePicture.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Foto in de storage zetten
    private void uploadPicture() {
        if (filePath != null) {
            String pictureName = clientId;
            storageReference = storageReference.child(pictureName);

            storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG, "Image uploaded to Firebase Storage successfully");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UpdateActivity.this, "Er is iets fout gegaan bij het uploaden van de profielfoto. Probeer opnieuw", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Something went wrong uploading the picture to Firebase Storage");
                }
            });
        }
    }

    private void goToMemberListActivity() {
        Intent intentToDetailsActivity = new Intent(UpdateActivity.this, ClientListActivity.class);
        startActivity(intentToDetailsActivity);
    }
}
