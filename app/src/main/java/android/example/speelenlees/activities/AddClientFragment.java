package android.example.speelenlees.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.example.speelenlees.activities.master.ClientListActivity;
import android.example.speelenlees.data.FirebaseDatabaseHelper;
import android.example.speelenlees.domain.Client;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.example.speelenlees.R;
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

import java.util.Calendar;

public class AddClientFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener{
    private View root;
    private EditText etFirstname, etLastname, etAddress, etPostalCode, etCity;
    private TextView tvDisplayDate, tvImageSelector;
    private ImageView ivProfilePicture;
    private Button btnRegister;
    private FirebaseDatabaseHelper databaseHelper;
    private Client client;
    private String date;
    private boolean dateSelected, imageSelected;
    private static final int PICK_IMAGE_REQUEST = 123; // random nummer
    private Uri image;
    private StorageReference storageReference;
    private static final String TAG = "InsertFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_add_client, container, false);

        init();
        Log.i(TAG, "Initialized successfully");

        tvDisplayDate.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        ivProfilePicture.setOnClickListener(this);

        if (savedInstanceState != null) {
            restoreSavedInstanceStates(savedInstanceState);

            Log.i(TAG, "All instance states restored successfully");
        }

        return root;
    }

    private void init() {
        etFirstname = root.findViewById(R.id.etFirstname);
        etLastname = root.findViewById(R.id.etLastname);
        tvDisplayDate = root.findViewById(R.id.tvDateSelector);
        etAddress = root.findViewById(R.id.etAddress);
        etPostalCode = root.findViewById(R.id.etPostcalCode);
        etCity = root.findViewById(R.id.etCity);
        ivProfilePicture = root.findViewById(R.id.ivProfilePicture);
        btnRegister = root.findViewById(R.id.btnRegister);
        databaseHelper = new FirebaseDatabaseHelper();
        client = new Client();
        tvImageSelector = root.findViewById(R.id.tvImageSelector);
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    // Sla instance states op bij het wijzigen van orientation
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Geboortedatum
        outState.putString("my_birthdate_string", tvDisplayDate.getText().toString());
        outState.putBoolean("my_birthdate_boolean", dateSelected);
        outState.putString("my_birthdate", date);

        // Profielfoto
        outState.putBoolean("my_profilepicture_boolean", imageSelected);
        outState.putParcelable("my_profilepicture", image);

        Log.i(TAG, "All instance states saved successfully");
    }

    // Laad opgeslagen instance states in
    private void restoreSavedInstanceStates(Bundle savedInstanceState) {
        // Geboortedatum
        tvDisplayDate.setText(savedInstanceState.getString("my_birthdate_string"));
        dateSelected = savedInstanceState.getBoolean("my_birthdate_boolean");
        date = savedInstanceState.getString("my_birthdate");

        // Profielfoto
        imageSelected = savedInstanceState.getBoolean("my_profilepicture_boolean");
        image = savedInstanceState.getParcelable("my_profilepicture");
        if (image != null) {
            ivProfilePicture.setImageURI(image);
            tvImageSelector.setVisibility(View.INVISIBLE);
        }

        Log.i(TAG, "All instances restored successfully");
    }

    public void onClick(View v) {
        if (v.getId() == R.id.tvDateSelector) {
            // Datum selecteren
            Log.i(TAG, "Date selector clicked");

            showDatePickerDialog();
        }
        else if (v.getId() == R.id.btnRegister) {
            // Lid toevoegen aan de database
            Log.i(TAG, "Register button clicked");

            addMember();
        }
        else if (v.getId() == R.id.ivProfilePicture) {
            // Dialoogvenster openen om afbeelding te selecteren
            Log.i(TAG, "Profile picture selector clicked");

            showFileChooser();
        }
    }

    // Dialoogvenster tonen om een datum te selecteren
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT, this, year, month, day);
        dialog.show();

        Log.i(TAG, "DatePicker opened successfully.");
    }

    // Deze methode wordt opgeroepen wanneer een datum geselecteerd wordt in de showDatePickerDialog()-methode
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.i(TAG, "Date selected successfully");

        dateSelected = true;
        tvDisplayDate.setError(null);

        date = "" + dayOfMonth + "/" + (month + 1) + "/" + year;

        String result = "Geboortedatum: " + date;
        tvDisplayDate.setText(result);
    }

    private void addMember() {
        if (checkUserInputValidity()) {
            InitializeMember();
            Log.i(TAG, "Member initialized successfully");

            // Lid toevoegen aan database
            databaseHelper.addClient(client);
            Log.i(TAG, "Member added to Firebase Database successfully");

            uploadProfilePicture();

            // Wanneer lid succesvol is toegevoegd aan database...
            Toast.makeText(getActivity(), client.getFirstname() + " " + client.getLastname() + " is toegevoegd", Toast.LENGTH_LONG).show();
            goToMainActivity();
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
            tvDisplayDate.setError("Selecteer uw geboortedatum");
            inputIsValid = false;
        }

        if (etAddress.getText().length() < 5) {
            String address = "Adres";
            etAddress.setError("\"" + address + "\" moet minstens 5 karakters hebben");
            inputIsValid = false;
        }

        boolean postalCodeIsNumber = true;
        try {
            Integer.parseInt(etPostalCode.getText().toString());
        } catch (NumberFormatException e) {
            postalCodeIsNumber = false;
        }
        if (etPostalCode.getText().length() != 4 || !postalCodeIsNumber) {
            String postalCode = "Postcode";
            etPostalCode.setError("\"" + postalCode + "\" moet een getal bestaande uit 4 cijfers zijn");
            inputIsValid = false;
        }

        if (etCity.getText().length() < 2) {
            String city = "Stad";
            etCity.setError("\"" + city + "\" moet minstens 2 karakters hebben");
            inputIsValid = false;
        }


        if (!imageSelected) {
            tvImageSelector.setError("Selecteer een afbeelding");
            inputIsValid = false;
        }

        if (inputIsValid) {
            Log.i(TAG, "Input is valid");
        } else {
            Log.w(TAG, "Input is not valid");
        }

        return inputIsValid;
    }

    private void goToMainActivity() {
        Intent intentToMainActivity = new Intent(getActivity(), ClientListActivity.class);
        startActivity(intentToMainActivity);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecteer een afbeelding"), PICK_IMAGE_REQUEST);
        Log.i(TAG, "Filechooser opened successfully");
    }

    // Deze methode wordt uitgevoerd als de gebruiker een afbeelding geselecteerd heeft in showFileChooser()-methode
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            Log.i(TAG, "Image selected successfully");
            image = data.getData();
            tvImageSelector.setVisibility(View.INVISIBLE);
            ivProfilePicture.setImageURI(image);
            tvImageSelector.setError(null);
            imageSelected = true;
        }
    }

    private void InitializeMember() {
        client.setFirstname(etFirstname.getText().toString().trim());
        client.setLastname(etLastname.getText().toString().trim());
        client.setBirthdate(date);
        client.setAddress(etAddress.getText().toString().trim());
        client.setZipcode(etPostalCode.getText().toString().trim());
        client.setCity(etCity.getText().toString().trim());

    }

    // Profielfoto uploaden naar Firebase Storage
    private void uploadProfilePicture() {
        if (image != null) {
            String pictureName = client.getClientId();
            storageReference = storageReference.child(pictureName);

            storageReference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG, "Image selected successfully");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Foto toevoegen aan storage = mislukt
                    Toast.makeText(getContext(), "Er is iets fout gegaan bij het uploaden van de profielfoto. Probeer opnieuw", Toast.LENGTH_SHORT).show();

                    Log.e(TAG, "Something went wrong uploading the image to Firebase Storage");
                }
            });
        }
    }
}
