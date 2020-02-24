package android.example.speelenlees.data;

import android.example.speelenlees.domain.Client;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {
    private DatabaseReference databaseReference;
    private List<Client> clients = new ArrayList<>();

    public FirebaseDatabaseHelper(){
        databaseReference = FirebaseDatabase.getInstance().getReference("Clients");
    }

    public FirebaseDatabaseHelper(String clientId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Clients").child(clientId);
    }

    public void addClient(Client client) {
        String id = databaseReference.push().getKey(); //genereert automatisch id
        client.setClientId(id);

        databaseReference.child(id).setValue(client);
    }

    public void updateClient(Client client) {
        databaseReference.child(client.getClientId()).setValue(client);
    }

    public void readClients(final DataStatus dataStatus) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clients.clear();
                List<String> keys = new ArrayList<>();

                //bevat key en value
                for(DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                    Client client = keyNode.getValue(Client.class);
                    clients.add(client);
                }

                dataStatus.DataIsLoaded(clients, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

   /* public void deleteClient() {
        databaseReference.removeValue();
    }*/
}
