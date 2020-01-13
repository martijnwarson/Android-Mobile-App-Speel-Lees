package android.example.speellees.activities.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.example.speellees.R;
import android.example.speellees.activities.GameActivity;
import android.example.speellees.activities.domain.Level;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    //INNER CLASS

    /*String data1[];
    String data2[];
   // int images[];
    Context context;
    private List<Level> levels;
    private List<String> keys;
    MyAdapter myAdapter;

    public void setConfig(RecyclerView recyclerView, Context context, List<Level> levels, List<String> keys) {
        this.context = context;
        myAdapter = new MyAdapter(levels, keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(myAdapter);
    }

    //INNER CLASS
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView myLevels;
        TextView myDescription;
        //ImageView myImageView;
        ConstraintLayout mainLayout;
        String key;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myLevels = itemView.findViewById(R.id.myLevels);
            myDescription = itemView.findViewById(R.id.myDescription);
            //myImageView = itemView.findViewById(R.id.myImageView);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
        public void bind(Level level, String key) {
            String title = level.getLevel();
            myLevels.setText(title);
            myDescription.setText(level.getDescription());
            this.key = key;
        }
    }




   // public MyAdapter(Context ct, String s1[], String s2[]) {
     public MyAdapter(List<Level> members, List<String> keys) {
        //context = ct;
        //data1 = s1;
        //data2 = s2;
        //images = img;
         this.levels = levels;
         this.keys = keys;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.bind(levels.get(position), keys.get(position));

        //holder.myLevels.setText(data1[position]);
        holder.myDescription.setText(data2[position]);
        //holder.myImageView.setImageResource(images[position]);
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GameActivity.class);
                intent.putExtra("data1", data1[position]);
                intent.putExtra("data2", data2[position]);
                //intent.putExtra("myImageView", images[position]);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data1.length;
    }

    //INNERCLASS*/

}
