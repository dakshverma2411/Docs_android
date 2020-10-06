package daksh.docs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticationActivity extends AppCompatActivity {

    Button LoginButton;
    EditText email;
    EditText pass;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    TextView createAccountTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        email=(EditText) findViewById(R.id.editTextPersonName);
        pass=(EditText) findViewById(R.id.editTextPassword);
        LoginButton=(Button)findViewById(R.id.LoginButton);
        createAccountTextView=(TextView) findViewById(R.id.CreateAccount);

        firebaseAuth=FirebaseAuth.getInstance();
        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user=firebaseAuth.getCurrentUser();
                if(user!=null)
                {
                    Toast.makeText(getApplicationContext(),"Signed In",Toast.LENGTH_SHORT).show();
                    MainActivity.USERNAME=user.getUid();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Not Signed in!", Toast.LENGTH_SHORT).show();
                }
            }
        };
        createAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Toast.makeText(getApplicationContext(),"C",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AuthenticationActivity.this,NewUserActivity.class));
                finish();
            }
        });
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!email.getText().toString().equals("") && !pass.getText().toString().equals(""))
                {
                    String emailID=email.getText().toString();
                    String password=pass.getText().toString();
                    login(emailID,password);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please fill in all the details",Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    public void login(String emailID,String password)
    {
        firebaseAuth.signInWithEmailAndPassword(emailID,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }
                        else
                        {
                            Toast.makeText(AuthenticationActivity.this,"User not recognized\nCreate a User instead",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}