package android.example.speelenlees.activities.master;

import android.content.DialogInterface;
import android.content.Intent;
import android.example.speelenlees.R;
import android.example.speelenlees.activities.UpdateActivity;
import android.example.speelenlees.data.FirebaseDatabaseHelper;
import android.example.speelenlees.domain.Client;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

// LANDSCAPE
public class ClientDetailFragment extends Fragment implements  View.OnClickListener {
    private static final String TAG = "ClientDetailFragment";
    Client client;
    TextView tvFirstname, tvLastname, tvBirthdate, tvAddress, tvZipcode, tvCity;
    ImageView ivProfilePicture;
    private Button btnUpdate, btnDelete;


    public ClientDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_client_detail, container, false);

        init(view);
        Log.i(TAG, "Initialized");

        initialize();
        Log.i(TAG, "Client initialized");

        if (client != null) {
            fillData();
            Log.i(TAG, "Filling fields succeed");
        }

        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        return view;
    }

    private void init(View view) {
        tvFirstname = view.findViewById(R.id.firstname_database);
        tvLastname = view.findViewById(R.id.lastname_database);
        tvBirthdate = view.findViewById(R.id.birthdate_database);
        tvAddress = view.findViewById(R.id.address_database);
        tvZipcode = view.findViewById(R.id.postalCode_database);
        tvCity = view.findViewById(R.id.city_database);
        ivProfilePicture = view.findViewById(R.id.iv_profilePic);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnDelete = view.findViewById(R.id.btnDelete);
    }

    private void initialize() {
        client = new Client();
        client.setClientId(getArguments().getString("clientId"));
        client.setFirstname(getArguments().getString("firstname"));
        client.setLastname(getArguments().getString("lastname"));
        client.setBirthdate(getArguments().getString("birthdate"));
        client.setAddress(getArguments().getString("address"));
        client.setZipcode(getArguments().getString("zipcode"));
        client.setCity(getArguments().getString("city"));

    }

    private void fillData() {
        tvFirstname.setText(client.getFirstname());
        tvLastname.setText(client.getLastname());
        tvBirthdate.setText(client.getBirthdate());
        tvAddress.setText(client.getAddress());
        tvZipcode.setText(client.getZipcode());
        tvCity.setText(client.getCity());

        fillImageView();

    }

    private void fillImageView() {
        String imageName = client.getClientId();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imageName);

        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.i(TAG, "Image downloaded from Firebase Storage successfully");

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ivProfilePicture.setImageBitmap(bitmap);

                Log.i(TAG, "Image posted in ImageView successfully");
            }
        });
    }


    public void onClick(View v) {
        if (v.getId() == R.id.btnUpdate) {
            // Naar activity gaan om gegevens van lid te wijzigen
            Log.i(TAG, "Update-button clicked");

            goToUpdateActivity();
        }
        else if (v.getId() == R.id.btnDelete) {
            // Lid verwijderen
            Log.i(TAG, "Delete-button clicked");

            createDeleteDialog(client.getClientId());
        }
    }

    private void goToUpdateActivity() {
        // Intent maken
        Intent intentToUpdateActivity = new Intent(this.getContext(), UpdateActivity.class);

        // Gegevens meegeven aan de intent die getoond moeten worden in UpdateActivity
        intentToUpdateActivity.putExtra("clientId", client.getClientId());
        intentToUpdateActivity.putExtra("firstname", client.getFirstname());
        intentToUpdateActivity.putExtra("lastname", client.getLastname());
        intentToUpdateActivity.putExtra("birthdate", client.getBirthdate());
        intentToUpdateActivity.putExtra("address", client.getAddress());
        intentToUpdateActivity.putExtra("zipcode", client.getZipcode());
        intentToUpdateActivity.putExtra("city", client.getCity());


        // Naar UpdateActivity gaan
        startActivity(intentToUpdateActivity);
    }

    private void createDeleteDialog(final String clientId) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setMessage("Weet je zeker dat je " + client.getFirstname() + " " + client.getLastname() + " wilt verwijderen?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "Delete member confirmed");

                deleteMember(clientId); // Lid verwijderen uit database
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

    private void deleteMember(String memberId) {
        FirebaseDatabaseHelper databaseHelper = new FirebaseDatabaseHelper(memberId);
        databaseHelper.deleteClient();

        String toastMessage = client.getFirstname() + " " + client.getLastname() + " is verwijderd";
        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_LONG).show();
        Log.i(TAG, "Member deleted succesfully");

        deleteProfilePicture();

        // Naar MemberListActivity gaan
        Intent intentToMemberListActivity = new Intent(getContext(), ClientListActivity.class);
        startActivity(intentToMemberListActivity);
    }

    private void deleteProfilePicture() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(client.getClientId());

        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "Profilepicture deleted successfully");
            }
        });
    }

}
