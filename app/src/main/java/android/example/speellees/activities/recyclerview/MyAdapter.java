package android.example.speellees.activities.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.example.speellees.R;
import android.example.speellees.activities.activities.ClientActivity;
import android.example.speellees.activities.domain.Client;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<Client> clients;

    public MyAdapter(Context cont, ArrayList<Client> cl) {
        context = cont;
        clients = cl;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.my_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        String fullname = clients.get(position).getFirstname() + " " + clients.get(position).getLastname();
        holder.fullname.setText(fullname);
        holder.birthdate.setText(clients.get(position).getBirthdate());
       // holder.remark.setText(clients.get(position).getRemark());

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Client client = clients.get(position);

                //Toast.makeText(context, position + " is clicked", Toast.LENGTH_SHORT).show();
                Intent intentDetails = new Intent(context, ClientActivity.class); //intent naar ClientActivity
                intentDetails.putExtra("firstname", client.getFirstname());

                context.startActivity(intentDetails);

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
        TextView fullname, remark, birthdate;
        ConstraintLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) { //constructor
            super(itemView);
            fullname = (TextView) itemView.findViewById(R.id.fullname);
            //remark = (TextView) itemView.findViewById(R.id.birthdate);
            birthdate = (TextView) itemView.findViewById(R.id.birthdate);

            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }

}
