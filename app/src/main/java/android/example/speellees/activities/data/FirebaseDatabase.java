package android.example.speellees.activities.data;

import android.example.speellees.activities.domain.Client;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabase {
    private DatabaseReference databaseReference;
    private List<Client> clients = new ArrayList<>();

    public FirebaseDatabase(){
        databaseReference = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("Clients");
    }

    public FirebaseDatabase(String clientId) {
        databaseReference = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("Clients").child(clientId);
    }

    public void addClient(Client client) {
        String id = databaseReference.push().getKey(); // push() creëert een unieke string in 'Clients' in Firebase
        client.setClientId(id);

        databaseReference.child(id).setValue(client);
    }

    public void updateMember(Client client) {
        databaseReference.child(client.getClientId()).setValue(client);
    }

    public void readClients(final DataStatus dataStatus) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            // onDataChange wordt altijd aangeroepen als er iets verandert in de database
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clients.clear();
                List<String> keys = new ArrayList<>();

                // DataSnapshot bevat key en value van een specifieke node (dataSnapshot.getChildren bevat de key en value van members)
                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
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
        public void deleteClient() {
            databaseReference.removeValue();
        }
}


