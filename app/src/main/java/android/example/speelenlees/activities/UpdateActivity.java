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
    Client client;
    boolean dateSelected;
    ImageView iv_profile_pic;

    static final int PICK_IMAGE_REQUEST = 124;

    //static final String TAG = "UpdateActivity";


   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // terug knop

       setTitle("Cliënt wijzigen");

        initialize();
        //Log.i(TAG, "Views initialized succesfully");

        fillViewWithData();

        iv_profile_pic.setOnClickListener(this);
        tv_birthdate.setOnClickListener(this);
        btn_update.setOnClickListener(this);


        //Om geboortedatum op te halen
        if (savedInstanceState != null) {
           //restoreSavedInstanceStates(savedInstanceState);
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
        storage_reference = FirebaseStorage.getInstance().getReference();
    }

    public void onClick(View v) {

        if (v.getId()  == R.id.tvDateSelector) {
            // Dialoogvenster tonen om data te selecteren
            showDatePickerDialog();
        }  else if (v.getId() == R.id.ivUpdateProfilePicture) {
            // Dialoogvenster tonen om afbeelding te selecteren
            showFileChooser();
        } else  if (v.getId() == R.id.btn_update) {
            // Gegevens van lid wijzigen
            updateMember();
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

        // Gewijzigde geboortedatum
        outState.putString("my_birthdate", date);
    }

   /* private void restoreSavedInstanceStates(Bundle savedInstanceState) {
        // Gewijzigde geboortedatum
        date = savedInstanceState.getString("my_birthdate");
        String birthdateString = "Geboortedatum: " + date;
        tv_birthdate.setText(birthdateString);
    }*/

    private void fillViewWithData() {
        clientId = getIntent().getStringExtra("clientId");
        firstname = getIntent().getStringExtra("firstname");
        lastname = getIntent().getStringExtra("lastname");
        date = getIntent().getStringExtra("birthdate");
        address = getIntent().getStringExtra("address");
        zipcode = getIntent().getStringExtra("zipcode");
        city = getIntent().getStringExtra("city");

        // Titel van ActionBar aanpassen
        //String titleName = "Wijzig " + firstname + " " + lastname;
        //setTitle(titleName);

        // Verzamelde data in de tekstvakken zetten
        et_firstname.setText(firstname);
        et_lastname.setText(lastname);
        et_address.setText(address);
        et_zipcode.setText(zipcode);
        et_city.setText(city);

        String birthdateString = "Geboortedatum: " + date;
        tv_birthdate.setText(birthdateString);

        // Foto tonen in de imageView
        showImage();
    }


    private void showImage() {
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

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(UpdateActivity.this, AlertDialog.THEME_HOLO_LIGHT, this, year, month, day);
        dialog.show();
    }


    private void updateMember() {
        if (checkUserInputValidity()) {
           //InitializeMember();

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
           // Log.i(TAG, "Member updated successfully");

           goToMemberListActivity();
        }
    }


    private boolean checkUserInputValidity() {
        boolean bool = true;

        if (et_firstname.getText().length() < 2) {
            String firstname = "Voornaam";
            et_firstname.setError("\"" + firstname + "\" dient minstens 2 karakters te hebben");
            bool = false;
        }

        if (et_lastname.getText().length() < 2) {
            String lastname = "Achternaam";
            et_lastname.setError("\"" + lastname + "\" dint minstens 2 karakters te hebben");
            bool = false;
        }

        /*if (!dateSelected) {
            tv_birthdate.setError("U dient een geboortedatum te kiezen");
            inputIsValid = false;
        }*/

        /*
        if (et_address.getText().length() < 5) {
            String address = "Straat + huisnummer";
            et_address.setError("\"" + address + "\" moet minstens 5 karakters hebben");
            inputIsValid = false;
        }*/

        //boolean postalCodeIsNumber = true;
       /* try {
            Integer.parseInt(et_zipcode.getText().toString());
        } catch (NumberFormatException e) {
            postalCodeIsNumber = false;
        }
        if (et_zipcode.getText().length() != 5 || !postalCodeIsNumber) {
            String zipcode = "Postcode";
            et_zipcode.setError("\"" + zipcode + "\" moet een getal bestaande uit 4 cijfers zijn");
            inputIsValid = false;
        }*/

       /*
        if (et_city.getText().length() < 2) {
            String city = "Gemeente";
            et_city.setError("\"" + city + "\" moet minstens 2 karakters hebben");
            inputIsValid = false;
        }

        if (inputIsValid) {
            //Log.i(TAG, "Fields fileld in correctly");
        } else {
            //Log.e(TAG, "Fields not filled in correctly");
        }*/

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
        startActivityForResult(Intent.createChooser(intent, "Kies een afbeelding"), PICK_IMAGE_REQUEST);
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
                iv_profile_pic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Foto in de storage zetten
    private void uploadPicture() {
        if (filePath != null) {
            String pictureName = clientId;
            storage_reference = storage_reference.child(pictureName);

            storage_reference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Log.i(TAG, "Image uploaded to Firebase Storage successfully");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UpdateActivity.this, "Er is iets fout gegaan bij het uploaden van de profielfoto. Probeer opnieuw", Toast.LENGTH_SHORT).show();
                    //Log.e(TAG, "Something went wrong uploading the picture to Firebase Storage");
                }
            });
        }
    }

    private void goToMemberListActivity() {
        Intent intentToDetailsActivity = new Intent(UpdateActivity.this, ClientListActivity.class);
        startActivity(intentToDetailsActivity);
    }
}
