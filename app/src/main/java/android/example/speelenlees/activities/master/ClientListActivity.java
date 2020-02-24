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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

//RECYCLERVIEW
public class ClientListActivity extends AppCompatActivity {
    private boolean twoPanes;
    //private static final String TAG = "ClientsListActivity";

    // Terug-knop functie
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //Log.i(TAG, "Back-button clicked");
            startActivity(new Intent(ClientListActivity.this, HomeActivity.class));
            //Log.i(TAG, "Went back to previous activity");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients_list); //bind activity_clients_list
        setTitle("Cliënten overzicht"); //Titel

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Terug-knop

        //Controleren of smartphone in portrait of landscape mode is
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            //Log.i(TAG, "In Landscape mode");
            twoPanes = false;
        } else {
            //Log.i(TAG, "In Portait mode");
            twoPanes = true;

        }

        /*if (checkLandscapeMode()) {
            //Log.i(TAG, "In landscape mode");
            twoPanes = true;
        }*/

        // Recyclerview declareren en implementeren om lijst te maken
        View recyclerView = findViewById(R.id.rv_clients_list); //clients list binden
        assert recyclerView != null; //recyclerview mag niet leeg zijn
        // Log.i(TAG, "Recyclerview declared");

        getDataFromFirebase((RecyclerView) recyclerView);
    }


    /*private boolean checkLandscapeMode() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Log.i(TAG, "In Landscape mode");

            return true;
        } else {
            //Log.i(TAG, "In Portait mode");

            return false;
        }
    }*/

    // Data ophalen uit Firebase
    private void getDataFromFirebase(final RecyclerView recyclerView) {
        new FirebaseDatabaseHelper().readClients(new DataStatus() {
            @Override
            public void DataIsLoaded(List<Client> clients, List<String> keys) {
               // Log.i(TAG, "Members loaded from Firebase Database successfully");
                setupRecyclerView(recyclerView, clients, keys); //clients en keys zijn data uit firebase
            }
        });
    }

    //Recyclerview opbouwen
    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<Client> clients, List<String> keys) {
        recyclerView.setAdapter(new ClientsRecyclerViewAdapter(this, clients, keys, twoPanes ));

        //Log.i(TAG, "Adapter set to RecyclerView successfully");
    }


    //Gegevens in de recyclerview steken (adapter)
    public static class ClientsRecyclerViewAdapter extends RecyclerView.Adapter<ClientsRecyclerViewAdapter.ViewHolder> {
        private final ClientListActivity clientListActivity;
        private final List<String> keys;
        private final List<Client> clients;

        private final boolean twoPanes; // landscape vs portrait

        ClientsRecyclerViewAdapter(ClientListActivity parent, List<Client> clients, List<String> keys, boolean twoPanes) {
            this.clientListActivity = parent;
            this.clients = clients;
            this.keys = keys;
            this.twoPanes = twoPanes;

            //Log.i(TAG, "Adapter constructed successfully");
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            String name = clients.get(position).getFirstname() + " " + clients.get(position).getLastname();
            holder.full_name.setText(name);

            String imageName = clients.get(position).getClientId();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imageName);

            final long ONE_MEGABYTE = 1024 * 1024;
            storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    holder.iv_profile_pic.setImageBitmap(bitmap);
                }
            });

            holder.itemView.setTag(clients.get(position));
            holder.itemView.setOnClickListener(onClickListener);

            //Log.i(TAG, "View within RecyclerViewList created successfully");
        }

        @Override
        public int getItemCount() {
            return clients.size();
        }

        private final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Client client = (Client) v.getTag();

                if (twoPanes) { // Landscape
                    Bundle bundle = new Bundle();

                    bundle.putString("clientId", client.getClientId());
                    bundle.putString("firstname", client.getFirstname());
                    bundle.putString("lastname", client.getLastname());
                    bundle.putString("birthdate", client.getBirthdate());
                    bundle.putString("address", client.getAddress());
                    bundle.putString("postalCode", client.getZipcode());
                    bundle.putString("city", client.getCity());

                    ClientDetailFragment fragment = new ClientDetailFragment();
                    fragment.setArguments(bundle);
                    clientListActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.client_detail_frame, fragment)
                            .commit();
                } else {
                    // Portrait
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ClientDetailActivity.class);

                    intent.putExtra("clientId", client.getClientId());
                    intent.putExtra("firstname", client.getFirstname());
                    intent.putExtra("lastname", client.getLastname());
                    intent.putExtra("birthdate", client.getBirthdate());
                    intent.putExtra("address", client.getAddress());
                    intent.putExtra("postalCode", client.getZipcode());
                    intent.putExtra("city", client.getCity());

                    context.startActivity(intent);  // DetailActivity openen
                }
            }
        };

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView iv_profile_pic;
            TextView full_name;

            ViewHolder(View view) {
                super(view);
                iv_profile_pic = view.findViewById(R.id.iv_profile_pic);
                full_name = view.findViewById(R.id.tv_client_full_name);
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
