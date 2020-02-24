package android.example.speelenlees.data;

import android.content.Context;
import android.content.Intent;
import android.example.speelenlees.R;
import android.example.speelenlees.activities.master.ClientDetailActivity;
import android.example.speelenlees.activities.master.ClientListActivity;
import android.example.speelenlees.domain.Client;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<Client> clients;
    boolean duoScreen;
    ClientListActivity parent;

    public MyAdapter(Context cont, ArrayList<Client> cl) {
        context = cont;
        clients = cl;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.client_list_content, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        String fullname = clients.get(position).getFirstname() + " " + clients.get(position).getLastname();
        holder.fullname.setText(fullname);
        holder.birthdate.setText(clients.get(position).getBirthdate());
        Picasso.get().load(clients.get(position).getProfilePic()).into(holder.profilePic);


        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Client client = clients.get(position);
                    Intent detailsIntent = new Intent(context, ClientDetailActivity.class);
                    detailsIntent.putExtra("clientId", client.getClientId());
                    detailsIntent.putExtra("firstname", client.getFirstname());
                    detailsIntent.putExtra("lastname", client.getLastname());
                    detailsIntent.putExtra("birthdate", client.getBirthdate());
                    detailsIntent.putExtra("profilePic", client.getProfilePic());

                    context.startActivity(detailsIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return clients.size();
    }

    //INNER CLASS
    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView fullname, birthdate;
        ImageView profilePic;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fullname = (TextView) itemView.findViewById(R.id.tv_client_full_name);
            birthdate = (TextView) itemView.findViewById(R.id.tv_client_birthdate);
            profilePic = (ImageView) itemView.findViewById(R.id.iv_profile_pic);

            mainLayout = itemView.findViewById(R.id.clientContentList);
        }
    }



}
