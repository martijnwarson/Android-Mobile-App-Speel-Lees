package android.example.speellees.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.example.speellees.R;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity {

    TextView title;
    TextView description;

    String data1;
    String data2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        title = findViewById(R.id.title);
        description = findViewById(R.id.description);

        getData();
        setData();

    }

    private void getData() {
        if (getIntent().hasExtra("data1") && getIntent().hasExtra("data2")) {

            data1 = getIntent().getStringExtra("data1");
            data2 = getIntent().getStringExtra("data2");


        } else {
            Toast.makeText(this, "No Data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setData() {
        title.setText(data1);
        description.setText(data2);

    }
}
