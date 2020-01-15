package android.example.speellees.activities.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.example.speellees.activities.data.FirebaseDatabase;
import android.example.speellees.activities.domain.Client;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.example.speellees.R;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;


public class AddFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private View view;
    private Client client;
    private EditText firstname;
    private EditText lastname;
    private ImageView profileImage;
    private String date;
    private boolean datePicked;
    private boolean imageSelected;
    private TextView showDate;
    private Button registerBtn;
    private Uri image;
    private static final int PICK_IMAGE_REQUEST = 654;
    DatabaseReference mDatabase;
    FirebaseDatabase firebaseDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add, container, false);
        mDatabase = com.google.firebase.database.FirebaseDatabase.getInstance().getReference();

        init();

        showDate.setOnClickListener(this);
        registerBtn.setOnClickListener(this);

        return view;
    }

    public void init() {
        firstname = view.findViewById(R.id.addFirstname);
        lastname = view.findViewById(R.id.addLastname);
        showDate = view.findViewById(R.id.addDatepicker);
        registerBtn = view.findViewById(R.id.btnRegister);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addDatepicker) {
            showDatePicker();
        } else if (v.getId() == R.id.btnRegister) {
            addClient();
        }
        //} else if (v.getId() == R.id.profileImage) {
          //  openFileSystem();
        //}

    }

    /*public void openFileSystem() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Kies een afbeelding"), PICK_IMAGE_REQUEST);
    }*/

    public void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePicker = new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT, this, day, month, year);
        datePicker.show();

    }

    public void addClient() {
        InitializeClient();
        //firebaseDatabase.addClient(client);
        mDatabase.child("clients").child("Client4").setValue(client);

        Toast.makeText(getActivity(), "Klant toegevoegd", Toast.LENGTH_LONG).show();

    }

    public void InitializeClient() {
        client.setFirstname(firstname.getText().toString().trim());
        client.setLastname(lastname.getText().toString().trim());
    }
/*
    public void redirect(){
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
    }*/
}
