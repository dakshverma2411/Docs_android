package daksh.docs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import Data.Document;

public class EditorActivity extends AppCompatActivity {

    EditText title;
    EditText description;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private boolean editable;
    private String old_title;
    private String userName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent=getIntent();
        final String title_name=intent.getStringExtra(MainActivity.INTENT_TITLE_TAG);
        title=(EditText) findViewById(R.id.titleEt);
        final ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("");
        userName=MainActivity.USERNAME;
        description=(EditText) findViewById(R.id.descriptionEt);
        String description_text=intent.getStringExtra(MainActivity.INTENT_DES_TAG);
        if(!description.equals(""))
        {
            description.setText(description_text);
        }
        String edit_mode=intent.getStringExtra(MainActivity.INTENT_EDITABLE_TAG);
        if(edit_mode.equals("edit_mode"))
        {
            userName=intent.getStringExtra(MainActivity.USERNAME_TAG);
            editable=true;
            old_title=title_name;
        }
        else
        {
            editable=false;
        }
        title.setText(title_name);
        actionBar.setTitle(title_name);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference().child(userName);
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                actionBar.setTitle(title.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.editor_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public String getCurrentDate()
    {
        long timestamp=java.lang.System.currentTimeMillis();
        java.text.DateFormat dateFormat=java.text.DateFormat.getDateInstance();
        String formattedDate=dateFormat.format(new Date(timestamp).getTime());

        return formattedDate;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.saveButton:
                if(!description.getText().toString().equals("") && !title.getText().toString().equals("")) {
                    if(!editable) {
                        databaseReference.push().setValue(new Document(title.getText().toString(), description.getText().toString(), getCurrentDate()));
                        Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child(userName);
                        Query query=ref.orderByChild("title").equalTo(old_title);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                                {
                                    snapshot.getRef().setValue(new Document(title.getText().toString(), description.getText().toString(), getCurrentDate()));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        MainActivity.adapter.notifyDataSetChanged();
                        Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show();


                    }
                    startActivity(new Intent(this,MainActivity.class));
                    finish();
                }else
                {
                    Toast.makeText(this,"Please fill title and desciption",Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.clearAll:
                description.setText("");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}