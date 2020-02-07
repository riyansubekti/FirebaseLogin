package subekti.riyan.firebaselogin;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import subekti.riyan.firebaselogin.model.Requests;

public class RegisterActivity extends AppCompatActivity {

    private DatabaseReference database;
    private FirebaseAuth mAuth;
    private Button mSignUp;
    private EditText etEmail, etPassword, etRePassword;
    private ProgressDialog loading;
    private static final String TAG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        database = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mSignUp = findViewById(R.id.btn_signup);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etRePassword = findViewById(R.id.et_repassword);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();
                String repassword = etRePassword.getText().toString();

                if (email.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Silahkan isi Email anda", Toast.LENGTH_SHORT).show();
                } else if (password.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Silahkan isi Password anda", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(repassword)) {
                    Toast.makeText(RegisterActivity.this, "Password yang anda masukan tidak sama", Toast.LENGTH_SHORT).show();
                } else {

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        Toast.makeText(RegisterActivity.this, "Authentication success.",
                                                Toast.LENGTH_SHORT).show();
                                        loading = ProgressDialog.show(RegisterActivity.this, null, "Pleas Wait",
                                                true, false);
                                        submitUser(new Requests(email.toLowerCase(), password.toLowerCase()));
                                        mAuth.signOut();
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    // ...
                                }
                            });

                }
            }
        });

    }

    private void submitUser(Requests requests) {
        database.child("Users").child("Penjual").child(mAuth.getCurrentUser().getUid())
//                child adalah membuat cabang baru didalam database simple-crud-b171b
//               .child("Table_Manual")
//                Push adalah membuat child dengan nama acak

                .setValue(requests)
                .addOnSuccessListener(RegisterActivity.this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        loading.dismiss();

                        etEmail.setText("");
                        etPassword.setText("");

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        }
    }
}
