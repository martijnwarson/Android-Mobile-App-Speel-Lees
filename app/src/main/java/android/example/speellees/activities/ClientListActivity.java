package android.example.speellees.activities;

import android.app.Activity;
import android.example.speellees.R;
import android.example.speellees.activities.domain.Client;
import android.example.speellees.activities.recyclerview.MyAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ClientListActivity extends AppCompatActivity {

    DatabaseReference reference;
    RecyclerView recyclerView;
    ArrayList<Client> list;
    MyAdapter adapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_client_list);

        setTitle(R.string.clientsOverview);

        progressBar = findViewById(R.id.progressBar);

        setContentView(R.layout.clients_list); //layout activity_main koppelen

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView); //view ophalen
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<Client>();

        findViewById(R.id.progressBar).setVisibility(View.GONE);

        //Data ophalen uit Firebase
        reference = FirebaseDatabase.getInstance().getReference().child("Clients");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<Client>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    Client c = dataSnapshot1.getValue(Client.class);
                    list.add(c);
                }
                adapter = new MyAdapter(ClientListActivity.this, list);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ClientListActivity.this, "Oops... Something went wrong", Toast.LENGTH_LONG).show();
            }


        });
    }
}
