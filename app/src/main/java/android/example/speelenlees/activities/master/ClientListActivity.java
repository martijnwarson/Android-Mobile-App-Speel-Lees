package android.example.speelenlees.activities.master;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.example.speelenlees.activities.ui.homepages.HomeActivity;
import android.example.speelenlees.data.DataStatus;
import android.example.speelenlees.data.FirebaseDatabaseHelper;
import android.example.speelenlees.domain.Client;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import android.example.speelenlees.R;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

//RECYCLERVIEW
public class ClientListActivity extends AppCompatActivity {
    private boolean twoPanes;
    private static final String TAG = "MemberListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients_list);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Back-knop

        // Titel van Actionbar instellen
        setTitle("Cliënts overzicht");

        if (checkLandscapeMode()) {
            Log.i(TAG, "In landscape mode");
            twoPanes = true;
        }

        // Recyclerview declareren en implementeren om lijst te maken
        View recyclerView = findViewById(R.id.rv_clientsList);
        assert recyclerView != null;
        Log.i(TAG, "Recyclerview declared");

        readMembers((RecyclerView) recyclerView);
    }

    // Wanneer er op het pijltje in de ActionBar wordt geklikt ...
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Log.i(TAG, "Back-button clicked");

            // Intent terug naar MainActivity
            startActivity(new Intent(ClientListActivity.this, HomeActivity.class));
            Log.i(TAG, "Went back to previous activity");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean checkLandscapeMode() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i(TAG, "In Landscape mode");

            return true;
        } else {
            Log.i(TAG, "In Portait mode");

            return false;
        }
    }

    // Members uit Firebase Database halen
    private void readMembers(final RecyclerView recyclerView) {
        new FirebaseDatabaseHelper().readClients(new DataStatus() {
            @Override
            public void DataIsLoaded(List<Client> clients, List<String> keys) {
                Log.i(TAG, "Members loaded from Firebase Database successfully");
                setupRecyclerView(recyclerView, clients, keys);
            }
        });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<Client> clients, List<String> keys) {
        recyclerView.setAdapter(new MemberRecyclerViewAdapter(this, clients, keys, twoPanes ));

        Log.i(TAG, "Adapter set to RecyclerView successfully");
    }


    public static class MemberRecyclerViewAdapter extends RecyclerView.Adapter<MemberRecyclerViewAdapter.ViewHolder> {
        private final ClientListActivity parentActivity;
        private final List<Client> clients;
        private final List<String> keys;
        private final boolean twoPanes;

        MemberRecyclerViewAdapter(ClientListActivity parent, List<Client> clients, List<String> keys, boolean twoPanes) {
            this.parentActivity = parent;
            this.clients = clients;
            this.keys = keys;
            this.twoPanes = twoPanes;

            Log.i(TAG, "Adapter constructed successfully");
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            // Per MemberItem in de RecyclerView worden foto, naam en instrument getoond
            // Naam
            String name = clients.get(position).getFirstname() + " " + clients.get(position).getLastname();
            holder.fullname.setText(name);

            //Profielfoto
            String imageName = clients.get(position).getClientId();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imageName);

            final long ONE_MEGABYTE = 1024 * 1024;
            storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    holder.iv_profilepic.setImageBitmap(bitmap);
                }
            });

            // OnClickListener op het volledig item zetten
            holder.itemView.setTag(clients.get(position));
            holder.itemView.setOnClickListener(onClickListener);

            Log.i(TAG, "View within RecyclerViewList created successfully");
        }

        @Override
        public int getItemCount() {
            return clients.size();
        }

        private final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Client client = (Client) v.getTag();

                if (twoPanes) {
                    // LANDSCAPE
                    Bundle arguments = new Bundle();

                    // Gegegens meegeven aan fragment a.d.h.v. arguments
                    arguments.putString("clientId", client.getClientId());
                    arguments.putString("firstname", client.getFirstname());
                    arguments.putString("lastname", client.getLastname());
                    arguments.putString("birthdate", client.getBirthdate());
                    arguments.putString("address", client.getAddress());
                    arguments.putString("postalCode", client.getZipcode());
                    arguments.putString("city", client.getCity());

                    // MemberDetailFragment openen
                    ClientDetailFragment fragment = new ClientDetailFragment();
                    fragment.setArguments(arguments);
                    parentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.client_detail_frame, fragment)
                            .commit();
                } else {
                    // PORTRAIT
                    Context context = v.getContext();
                    Intent intentToMemberDetailActivity = new Intent(context, ClientDetailActivity.class);

                    // Gegevens meegeven aan de intent
                    intentToMemberDetailActivity.putExtra("clientId", client.getClientId());
                    intentToMemberDetailActivity.putExtra("firstname", client.getFirstname());
                    intentToMemberDetailActivity.putExtra("lastname", client.getLastname());
                    intentToMemberDetailActivity.putExtra("birthdate", client.getBirthdate());
                    intentToMemberDetailActivity.putExtra("address", client.getAddress());
                    intentToMemberDetailActivity.putExtra("postalCode", client.getZipcode());
                    intentToMemberDetailActivity.putExtra("city", client.getCity());

                    // DetailActivity openen
                    context.startActivity(intentToMemberDetailActivity);
                }
            }
        };

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView iv_profilepic;
            TextView fullname;

            ViewHolder(View view) {
                super(view);
                iv_profilepic = view.findViewById(R.id.iv_profilePic);
                fullname = view.findViewById(R.id.tv_clientFullName);
            }
        }
    }
}





    /*private static final String TAG = "ClientListActivity";
    DatabaseReference reference;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MyAdapter adapter;
    ArrayList<Client> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClientListActivity parent = ClientListActivity.this;
        setTitle("Cliënten overzicht");


        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.client_list_land);
            Log.i(TAG, "Landscape mode");

        } else {
            Log.i(TAG, "Portait mode");
            setContentView(R.layout.clients_list);
        }

        recyclerView = (RecyclerView) findViewById(R.id.rv_clientsList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new GridLayoutManager(ClientListActivity.this, 1));


        list = new ArrayList<Client>();

        reference = FirebaseDatabase.getInstance().getReference().child("Clients");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<Client>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Client client = dataSnapshot1.getValue(Client.class);
                    list.add(client);
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
}*/
