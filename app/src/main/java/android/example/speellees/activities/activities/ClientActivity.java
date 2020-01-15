package android.example.speellees.activities.activities;

import android.example.speellees.R;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class ClientActivity extends AppCompatActivity {

    TextView fullname;
    TextView birthdate;

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        fullname = findViewById(R.id.fullname);
        birthdate = findViewById(R.id.birthdate);

        getData();
        setData();


    }

    private void getData() {
        name = getIntent().getStringExtra("firstname");
    }

    private void setData() {
        fullname.setText(name);
    }
}
