package android.example.speellees.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.example.speellees.R;
import android.example.speellees.activities.recyclerview.MyAdapter;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    DatabaseReference reff;

    String s1[];
    String s2[];
   // int images[] = {R.drawable.undraw_book_reading_kx9s};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //test connection Firebase
        Toast.makeText(MainActivity.this, "Firebase connection success", Toast.LENGTH_LONG).show();


        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        new FirebaseDatabase().readLevels((levels, keys) -> {
            new MyAdapter().setConfig(recyclerView, getContext(), levels, keys);
        });



        /*s1 = getResources().getStringArray(R.array.levels);
        s2 = getResources().getStringArray(R.array.description);*/

        MyAdapter myAdapter = new MyAdapter();
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
