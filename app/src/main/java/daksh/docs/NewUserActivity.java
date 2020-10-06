package daksh.docs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewUserActivity extends AppCompatActivity {


    private EditText newName;
    private EditText newEmail;
    private EditText newPassword;
    private Button createAccount;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        newName=(EditText) findViewById(R.id.newName);
        newEmail=(EditText) findViewById(R.id.newEmail);
        newPassword=(EditText) findViewById(R.id.newPassword);
        createAccount=(Button) findViewById(R.id.newUserButton);

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference().child("Users");
        firebaseAuth=FirebaseAuth.getInstance();

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUserAccount();
            }
        });
    }

    public boolean empty(String s)
    {
        if(s.equals(""))
        {
            return true;
        }
        return false;
    }

    public void createUserAccount()
    {
        final String name=newName.getText().toString();
        final String email=newEmail.getText().toString();
        String pass=newPassword.getText().toString();

        if(!empty(name) && ! empty(email) && !empty(pass))
        {
            if(pass.length()<8)
            {
                Toast.makeText(this,"Password must be atleast 8 characters long",Toast.LENGTH_LONG).show();
            }
            else {
                firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                if (authResult != null) {
                                    String uid = firebaseAuth.getUid();
                                    MainActivity.USERNAME = uid;
                                    DatabaseReference ref = databaseReference.child(uid);
                                    ref.child("name").setValue(name);
                                    ref.child("email").setValue(email);
                                    startActivity(new Intent(NewUserActivity.this, MainActivity.class));
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(NewUserActivity.this,"Invalid Credentials",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewUserActivity.this,"Invalid Credentials",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        else
        {
            Toast.makeText(this,"Please fill in all the details",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,AuthenticationActivity.class));
        finish();
    }
}