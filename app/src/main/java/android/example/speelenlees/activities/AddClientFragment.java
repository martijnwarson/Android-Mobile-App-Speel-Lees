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
    private EditText et_firstname;
    private Client client;
    private String date;
    private ImageView iv_profile_pic;
    private Button btn_register;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private StorageReference storageReference;

    EditText et_lastname;
    EditText et_address;
    EditText et_zipcode;
    EditText et_city;
    TextView tv_date;
    TextView tv_image_selector;


    private boolean dateSelected;
    private boolean imageSelected;
    private static final int PICK_IMAGE_REQUEST = 123; // random nummer
    private Uri image;

    //private static final String TAG = "InsertFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_add_client, container, false);

        initialize();
        //Log.i(TAG, "Initialized successfully");
        iv_profile_pic.setOnClickListener(this);
        tv_date.setOnClickListener(this);
        btn_register.setOnClickListener(this);


        if (savedInstanceState != null) {
           // restoreSavedInstanceStates(savedInstanceState);
            tv_date.setText(savedInstanceState.getString("my_birthdate_string"));
            dateSelected = savedInstanceState.getBoolean("my_birthdate_boolean");
            date = savedInstanceState.getString("my_birthdate");
            imageSelected = savedInstanceState.getBoolean("my_profilepicture_boolean");
            image = savedInstanceState.getParcelable("my_profilepicture");

            if (image != null) {
                iv_profile_pic.setImageURI(image);
                tv_image_selector.setVisibility(View.INVISIBLE);
            }

            //Log.i(TAG, "All instance states restored successfully");
        }

        return root;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.tvDateSelector) {
            // Datum selecteren
            //Log.i(TAG, "Date selector clicked");

            showDatePicker();
        }
        else if (v.getId() == R.id.ivProfilePicture) {
            // Dialoogvenster openen om afbeelding te selecteren
            // Log.i(TAG, "Profile picture selector clicked");

            showFileChooser();
        }
        else if (v.getId() == R.id.btnRegister) {
            // Lid toevoegen aan de database
            // Log.i(TAG, "Register button clicked");

            addClient();
        }

    }

    private void initialize() {
        client = new Client();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper();
        storageReference = FirebaseStorage.getInstance().getReference();

        tv_image_selector = root.findViewById(R.id.tvImageSelector);
        et_firstname = root.findViewById(R.id.etFirstname);
        et_lastname = root.findViewById(R.id.etLastname);
        tv_date = root.findViewById(R.id.tvDateSelector);
        et_address = root.findViewById(R.id.etAddress);
        et_zipcode = root.findViewById(R.id.etPostcalCode);
        et_city = root.findViewById(R.id.etCity);
        iv_profile_pic = root.findViewById(R.id.ivProfilePicture);
        btn_register = root.findViewById(R.id.btnRegister);


    }

    // Bij draaien van portrait naar landscape moeten de gegevens opgeslagen worden
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("my_birthdate_string", tv_date.getText().toString());
        outState.putBoolean("my_birthdate_boolean", dateSelected);
        outState.putString("my_birthdate", date);

        outState.putBoolean("my_profilepicture_boolean", imageSelected);
        outState.putParcelable("my_profilepicture", image);

        //Log.i(TAG, "All instance states saved successfully");
    }

    // Laad opgeslagen instance states in
    /*private void restoreSavedInstanceStates(Bundle savedInstanceState) {

        tv_date.setText(savedInstanceState.getString("my_birthdate_string"));
        dateSelected = savedInstanceState.getBoolean("my_birthdate_boolean");
        date = savedInstanceState.getString("my_birthdate");


        imageSelected = savedInstanceState.getBoolean("my_profilepicture_boolean");
        image = savedInstanceState.getParcelable("my_profilepicture");
        if (image != null) {
            iv_profile_pic.setImageURI(image);
            tv_image_selector.setVisibility(View.INVISIBLE);
        }

        //Log.i(TAG, "All instances restored successfully");
    }*/

    private void addClient() {
        if (checkUserInputValidity()) {
            //Initialize();
            //Cliënt aanmaken
            client.setFirstname(et_firstname.getText().toString().trim());
            client.setLastname(et_lastname.getText().toString().trim());
            client.setBirthdate(date);
            client.setAddress(et_address.getText().toString().trim());
            client.setZipcode(et_zipcode.getText().toString().trim());
            client.setCity(et_city.getText().toString().trim());
            // Log.i(TAG, "Member initialized successfully");

            // Lid toevoegen aan database
            firebaseDatabaseHelper.addClient(client);
            // Log.i(TAG, "Member added to Firebase Database successfully");

            saveProfilePic();

            // Wanneer lid succesvol is toegevoegd aan database...
            Toast.makeText(getActivity(), client.getFirstname() + " " + client.getLastname() + " is toegevoegd", Toast.LENGTH_LONG).show();
            goToMainActivity();
        }
    }

    // Dialoogvenster tonen om een datum te selecteren
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT, this, year, month, day);
        dialog.show();

       // Log.i(TAG, "DatePicker opened successfully.");
    }

    // Deze methode wordt opgeroepen wanneer een datum geselecteerd wordt in de showDatePicker()-methode
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        //Log.i(TAG, "Date selected successfully");

        dateSelected = true;
        tv_date.setError(null);

        date = "" + dayOfMonth + "/" + (month + 1) + "/" + year;

        String result = "Geboortedatum: " + date;
        tv_date.setText(result);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecteer een afbeelding"), PICK_IMAGE_REQUEST);
        // Log.i(TAG, "Filechooser opened successfully");
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
            et_lastname.setError("\"" + lastname + "\" dient minstens 2 karakters te hebben");
            bool = false;
        }

        if (!dateSelected) {
            tv_date.setError("Selecteer uw geboortedatum");
            bool = false;
        }

        /*if (et_address.getText().length() < 5) {
            String address = "Adres";
            et_address.setError("\"" + address + "\" moet minstens 5 karakters hebben");
            inputIsValid = false;
        }

        boolean postalCodeIsNumber = true;
        try {
            Integer.parseInt(et_zipcode.getText().toString());
        } catch (NumberFormatException e) {
            postalCodeIsNumber = false;
        }
        if (et_zipcode.getText().length() != 4 || !postalCodeIsNumber) {
            String postalCode = "Postcode";
            et_zipcode.setError("\"" + postalCode + "\" moet een getal bestaande uit 4 cijfers zijn");
            inputIsValid = false;
        }

        if (et_city.getText().length() < 2) {
            String city = "Stad";
            et_city.setError("\"" + city + "\" moet minstens 2 karakters hebben");
            inputIsValid = false;
        }


        if (!imageSelected) {
            tv_image_selector.setError("Selecteer een afbeelding");
            inputIsValid = false;
        }

         */

        /*if (inputIsValid) {
            //Log.i(TAG, "Input is valid");
        } else {
           // Log.w(TAG, "Input is not valid");
        }*/

        return bool;
    }

    private void goToMainActivity() {
        Intent intentToMainActivity = new Intent(getActivity(), ClientListActivity.class);
        startActivity(intentToMainActivity);
    }



    // Deze methode wordt uitgevoerd als de gebruiker een afbeelding geselecteerd heeft in showFileChooser()-methode
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
           // Log.i(TAG, "Image selected successfully");
            image = data.getData();
            tv_image_selector.setVisibility(View.INVISIBLE);
            iv_profile_pic.setImageURI(image);
            tv_image_selector.setError(null);
            imageSelected = true;
        }
    }

    private void Initialize() {
        client.setFirstname(et_firstname.getText().toString().trim());
        client.setLastname(et_lastname.getText().toString().trim());
        client.setBirthdate(date);
        client.setAddress(et_address.getText().toString().trim());
        client.setZipcode(et_zipcode.getText().toString().trim());
        client.setCity(et_city.getText().toString().trim());

    }

    private void saveProfilePic() {
        if (image != null) {
            String pictureName = client.getClientId();
            storageReference = storageReference.child(pictureName);

            storageReference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
               // @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   // Log.i(TAG, "Image selected successfully");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Foto toevoegen aan storage = mislukt
                    Toast.makeText(getContext(), "Foto opslaan mislukt.", Toast.LENGTH_SHORT).show();

                   // Log.e(TAG, "Something went wrong uploading the image to Firebase Storage");
                }
            });
        }
    }
}
