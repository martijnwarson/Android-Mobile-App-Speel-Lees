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
    private ImageView profile_pic;
    private Button btn_register;
    private Button upload_btn;
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



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_add_client, container, false);

        initialize();

        profile_pic.setOnClickListener(this);
        tv_date.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        upload_btn.setOnClickListener(this);


        if (savedInstanceState != null) {

            tv_date.setText(savedInstanceState.getString("my_birthdate_string"));
            dateSelected = savedInstanceState.getBoolean("my_birthdate_boolean");
            date = savedInstanceState.getString("my_birthdate");
            imageSelected = savedInstanceState.getBoolean("my_profilepicture_boolean");
            image = savedInstanceState.getParcelable("my_profilepicture");

            if (image != null) {
                profile_pic.setImageURI(image);
                tv_image_selector.setVisibility(View.INVISIBLE);
            }


        }

        return root;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.tvDateSelector) {
            showDatePicker();
        }
        else if (v.getId() == R.id.upload_btn) {
            showFileChooser();
        }
        else if (v.getId() == R.id.btnRegister) {
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
        profile_pic = root.findViewById(R.id.ivProfilePicture);
        btn_register = root.findViewById(R.id.btnRegister);
        upload_btn = root.findViewById(R.id.upload_btn);


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


    }

    private void addClient() {
        if (checkUserInputValidity()) {
            //Cliënt aanmaken
            client.setFirstname(et_firstname.getText().toString().trim());
            client.setLastname(et_lastname.getText().toString().trim());
            client.setBirthdate(date);
            client.setAddress(et_address.getText().toString().trim());
            client.setZipcode(et_zipcode.getText().toString().trim());
            client.setCity(et_city.getText().toString().trim());


            // Lid toevoegen aan database
            firebaseDatabaseHelper.addClient(client);


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


    }

    // Deze methode wordt opgeroepen wanneer een datum geselecteerd wordt in de showDatePicker()-methode
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

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
    }


    private boolean checkUserInputValidity() {
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

        if (!dateSelected) {
            tv_date.setError("Selecteer uw geboortedatum");
            bool = false;
        }

        return bool;
    }

    private void goToMainActivity() {
        Intent intentToMainActivity = new Intent(getActivity(), ClientListActivity.class);
        startActivity(intentToMainActivity);
    }


    // als gebruiker foto heeft gekozen
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            image = data.getData();
            tv_image_selector.setVisibility(View.INVISIBLE);
            profile_pic.setImageURI(image);
            tv_image_selector.setError(null);
            imageSelected = true;
        }
    }



    private void saveProfilePic() {
        if (image != null) {
            String pictureName = client.getClientId();
            storageReference = storageReference.child(pictureName);

            storageReference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
               // @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Foto opslaan niet gelukt.", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
}
