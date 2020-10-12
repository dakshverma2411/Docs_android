package daksh.docs;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chinalwb.are.AREditText;
import com.chinalwb.are.styles.toolbar.ARE_Toolbar;
import com.chinalwb.are.styles.toolbar.IARE_Toolbar;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentCenter;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentLeft;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentRight;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_At;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Bold;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Hr;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Image;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Italic;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Link;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_ListBullet;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_ListNumber;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Quote;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Strikethrough;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Subscript;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Superscript;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Underline;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_UpdaterDefault;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Video;
import com.chinalwb.are.styles.toolitems.IARE_ToolItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import Data.Document;
import ToolItems.BoldToolItem;
import ToolItems.ItalicToolItem;
import ToolItems.QuotesToolItem;
import ToolItems.StrikeToolItem;
import ToolItems.UnderLineToolItem;


public class EditorActivity2 extends AppCompatActivity {

    EditText title;
    private String userName;
    AREditText description;
    private Boolean editable;
    private String old_title;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    IARE_Toolbar mToolbar;
    ArrayList<String> undo;
    ArrayList<String> redo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor2);
        undo=new ArrayList<>();
        redo=new ArrayList<>();
        Intent intent=getIntent();
        final String title_name=intent.getStringExtra(MainActivity.INTENT_TITLE_TAG);
        title=(EditText) findViewById(R.id.titleEt);
        final ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("");
        userName=MainActivity.USERNAME;
        description=(AREditText) findViewById(R.id.arEditText);
        initToolbar();
        String description_text=intent.getStringExtra(MainActivity.INTENT_DES_TAG);
        if(!description.equals(""))
        {
            description.fromHtml(description_text);
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
        firebaseDatabase= FirebaseDatabase.getInstance();
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
                if(!description.getHtml().equals("") && !title.getText().toString().equals("")) {
                    if(!editable) {
                        databaseReference.push().setValue(new Document(title.getText().toString(), description.getHtml(), getCurrentDate()));
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
                                    snapshot.getRef().setValue(new Document(title.getText().toString(), description.getHtml().toString(), getCurrentDate()));
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

            case R.id.savePdf:
                savingPdf();
        }
        return super.onOptionsItemSelected(item);
    }

    public void savingPdf()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED)
            {
                String [] permissions={Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions,1);
            }
            else
            {
                Log.i("permission","in else means granted");
                savePdf();
            }
        }
        else
        {
            savePdf();
        }
    }

    public void savePdf()
    {


        com.itextpdf.text.Document doc = new com.itextpdf.text.Document();

        try {
            String path = Environment.getExternalStorageDirectory().getPath() ;

            File dir = new File(path);
            if(!dir.exists()) {
                dir.mkdir();
                Log.i("storage","in if");
            }


            String file_name=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
            File file = new File(dir, file_name+".pdf");
            FileOutputStream fOut = new FileOutputStream(file);
            Log.i("storage","after fout");


            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();

//            SpannableString ss=new SpannableString(Html.fromHtml("<i>Hello</i>"));
            Paragraph p1 = new Paragraph(String.valueOf(Html.fromHtml(description.getHtml())));
            p1.setAlignment(Paragraph.ALIGN_CENTER);

            //add paragraph to document
            doc.add(p1);
            Log.i("storge",path);
            Toast.makeText(this,"Saved as "+file_name,Toast.LENGTH_LONG).show();

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        } finally {
            doc.close();
        }

    }

    private void initToolbar() {
        mToolbar = this.findViewById(R.id.areToolbar);
        IARE_ToolItem bold = new BoldToolItem();
        IARE_ToolItem italic = new ItalicToolItem();
        IARE_ToolItem underline = new UnderLineToolItem();
        IARE_ToolItem strikethrough = new StrikeToolItem();
        IARE_ToolItem quote = new QuotesToolItem();
        IARE_ToolItem listNumber = new ARE_ToolItem_ListNumber();
        IARE_ToolItem hr = new ARE_ToolItem_Hr();
        IARE_ToolItem link = new ARE_ToolItem_Link();
        IARE_ToolItem subscript = new ARE_ToolItem_Subscript();
        IARE_ToolItem superscript = new ARE_ToolItem_Superscript();
        IARE_ToolItem left = new ARE_ToolItem_AlignmentLeft();
        IARE_ToolItem center = new ARE_ToolItem_AlignmentCenter();
        IARE_ToolItem right = new ARE_ToolItem_AlignmentRight();
        IARE_ToolItem image = new ARE_ToolItem_Image();
        IARE_ToolItem video = new ARE_ToolItem_Video();
        IARE_ToolItem at = new ARE_ToolItem_At();
        mToolbar.addToolbarItem(bold);
        mToolbar.addToolbarItem(italic);
        mToolbar.addToolbarItem(underline);
        mToolbar.addToolbarItem(strikethrough);
        mToolbar.addToolbarItem(quote);
        mToolbar.addToolbarItem(hr);
        mToolbar.addToolbarItem(link);
        mToolbar.addToolbarItem(subscript);
        mToolbar.addToolbarItem(superscript);
        mToolbar.addToolbarItem(listNumber);
        mToolbar.addToolbarItem(left);
        mToolbar.addToolbarItem(center);
        mToolbar.addToolbarItem(right);
        mToolbar.addToolbarItem(at);
        description = this.findViewById(R.id.arEditText);
        description.setToolbar(mToolbar);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String [] permissions,int i[]){
        switch (requestCode)
        {
            case 1:
                if(i.length>0 && i[0]==PackageManager.PERMISSION_GRANTED)
                {
                    savePdf();
                }
        }

    }




}