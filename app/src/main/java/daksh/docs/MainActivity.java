package daksh.docs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import Adapter.DocRecyclerViewAdapter;
import Data.Document;

public class MainActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    static DocRecyclerViewAdapter adapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FloatingActionButton floatingActionButton;
    static final String INTENT_TITLE_TAG="title_name";
    static final String INTENT_EDITABLE_TAG="editable";
    static final String INTENT_DES_TAG="description";
    public static final String USERNAME_TAG="userName";
    public static String USERNAME="daksh__verma";

    static ArrayList<Document> entries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent=getIntent();

        entries=new ArrayList<>();
        Log.i("error",entries.toString());
        recyclerView = findViewById(R.id.recyclerView);
        floatingActionButton=(FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,EditorActivity.class);
                intent.putExtra(INTENT_TITLE_TAG,"Untitled");
                intent.putExtra(INTENT_DES_TAG,"");
                intent.putExtra(USERNAME_TAG,USERNAME);
                intent.putExtra(INTENT_EDITABLE_TAG,"non_edit_mode");
                startActivity(intent);
            }
        });
//        adapter=new DocRecyclerViewAdapter(entries,this);

    }


    @Override
    protected void onStart() {
        super.onStart();

        entries.clear();

        setRecyclerView(entries);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference().child(USERNAME);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Log.i("update",dataSnapshot.getValue(Document.class).getTitle());
                Document doc=dataSnapshot.getValue(Document.class);
                entries.add(doc);
                Log.i("entries ",String.valueOf(entries.size()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("update","on child changed called");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                Log.i("update","on child removed");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.docs_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.reverseButton :
                changeSomeonesDoc();
                break;
            case R.id.deleteAll:
                FirebaseDatabase.getInstance().getReference().child(USERNAME).removeValue();
                entries.clear();
                adapter.notifyDataSetChanged();
                break;

            case R.id.signout:
                FirebaseAuth auth=FirebaseAuth.getInstance();
                entries.clear();
                auth.signOut();
                startActivity(new Intent(this,AuthenticationActivity.class));
                finish();
                break;
            case R.id.myAccount:
                popUpDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeSomeonesDoc()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.change_another_users_doc,null);
        builder.setView(view);
        final AlertDialog dialog=builder.create();
        dialog.show();

        final EditText Uid=(EditText) view.findViewById(R.id.another_ID);
        final EditText Doc_name=(EditText) view.findViewById(R.id.doc_name);
        Button edit=(Button) view.findViewById(R.id.changeButton);






        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!Uid.getText().toString().equals("") && !Doc_name.getText().toString().equals("")) {


                    final String[] desc = {""};
                    FirebaseDatabase fdatabase=FirebaseDatabase.getInstance();
                    DatabaseReference dbReference=fdatabase.getReference().child(Uid.getText().toString());
                    Query q=dbReference.orderByChild("title").equalTo(Doc_name.getText().toString());
                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.i("dfdf","on Data changed");
                            for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                            {
                                Document d =dataSnapshot1.getValue(Document.class);
                                desc[0] =d.getDescription();

                                Log.i("dfdf",desc[0]);




                            }
                            if(!desc[0].equals("")) {
                                Intent intent_for_change = new Intent(MainActivity.this, EditorActivity.class);
                                intent_for_change.putExtra(INTENT_TITLE_TAG, Doc_name.getText().toString());
                                intent_for_change.putExtra(INTENT_EDITABLE_TAG, "edit_mode");
                                intent_for_change.putExtra(INTENT_DES_TAG, desc[0]);
                                intent_for_change.putExtra(USERNAME_TAG, Uid.getText().toString());
                                startActivity(intent_for_change);
                                dialog.dismiss();
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this,"No such document exists",Toast.LENGTH_LONG).show();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.i("error","error");
                        }
                    });
                }else{
                    Toast.makeText(MainActivity.this,"Fill all the details",Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    public void popUpDialog()
    {
        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.popup_dialog,null);
        alertDialogBuilder.setView(view);
        AlertDialog alertDialog=alertDialogBuilder.create();
        alertDialog.show();
        TextView uid=(TextView) view.findViewById(R.id.uid);
        uid.setText(USERNAME);
        ImageView imageButton=(ImageView) view.findViewById(R.id.copyText);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", MainActivity.USERNAME);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this,"UID copied to clipboard",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void reverseOrder()
    {
        ArrayList<Document> entries2=new ArrayList<>();
        for(int i=entries.size()-1;i>=0;i--)
        {
            entries2.add(entries.get(i));
        }
        entries=entries2;
        setRecyclerView(entries);
    }

    void setRecyclerView(ArrayList<Document> entries)
    {
        adapter=new DocRecyclerViewAdapter(entries,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}