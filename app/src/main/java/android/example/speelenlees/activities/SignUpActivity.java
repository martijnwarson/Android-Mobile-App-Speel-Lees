package android.example.speelenlees.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.example.speelenlees.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    static final String TAG = "SignUpActivity";
    EditText et_email, et_password;
    Button btn_sign_up;
    TextView tv_existing_account;
    FirebaseAuth firebaseAuth;
    String email, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Registreer nieuw account");

        init();

        btn_sign_up.setOnClickListener(this);
        tv_existing_account.setOnClickListener(this);
    }

    private void init() {
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_sign_up = findViewById(R.id.btn_sign_up);
        tv_existing_account = findViewById(R.id.tv_existing_account);
        firebaseAuth = FirebaseAuth.getInstance();
        Log.i(TAG, "Views initialized successfully");
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btn_sign_up) {
            SignUpClient();
        }
        else if (v.getId() == R.id.tv_existing_account) {
            goToLoginActivity();
        }
    }

    private void SignUpClient() {
        email = et_email.getText().toString();
        password = et_password.getText().toString();

        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, "Vul beide velden in", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Fields not filled in correctly");
        }
        else if (email.isEmpty()) {
            et_email.setError("Geef een e-mailadres in");
            et_email.requestFocus();
            Log.e(TAG, "Fields not filled in correctly");
        }
        else if (password.isEmpty()) {
            et_password.setError("Geef een wachtwoord in");
            et_password.requestFocus();
            Log.e(TAG, "Fields not filled in correctly");
        }

    }

    private void SignUp() {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Fout bij registreren account. Probeer opnieuw", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Something went wrong. No account registered.");
                }
                else {
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    Log.i(TAG, "Account created");
                    startActivity(intent);
                }
            }
        });
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
        startActivity(intent);
    }
}
