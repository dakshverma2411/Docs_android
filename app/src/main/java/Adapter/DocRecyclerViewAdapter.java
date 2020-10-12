package Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Queue;

import Data.Document;
import daksh.docs.EditorActivity;
import daksh.docs.EditorActivity2;
import daksh.docs.MainActivity;
import daksh.docs.R;

public class DocRecyclerViewAdapter extends RecyclerView.Adapter<DocRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Document> arrayList;
    private Context context;

    static final String INTENT_TITLE_TAG="title_name";
    static final String INTENT_DES_TAG="description";
    static final String INTENT_EDITABLE_TAG="editable";
    public DocRecyclerViewAdapter(ArrayList<Document> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doc_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Document doc=arrayList.get(position);
        holder.title.setText(doc.getTitle());
        holder.date.setText("Edited on : "+doc.getDate());
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child(MainActivity.USERNAME);
                        Query query=ref.orderByChild("title").equalTo(doc.getTitle());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                                {
                                    snapshot.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        arrayList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context,"Deleted a Document",Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();


            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_for_edit=new Intent(context, EditorActivity2.class);
                intent_for_edit.putExtra(INTENT_DES_TAG,doc.getDescription());
                intent_for_edit.putExtra(INTENT_TITLE_TAG,doc.getTitle());
                intent_for_edit.putExtra(MainActivity.USERNAME_TAG,MainActivity.USERNAME);
                intent_for_edit.putExtra(INTENT_EDITABLE_TAG,"edit_mode");
                context.startActivity(intent_for_edit);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView date;
        private ImageView edit;
        private ImageView deleteButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            edit=(ImageView) itemView.findViewById(R.id.docEditButton);
            deleteButton = (ImageView) itemView.findViewById(R.id.docDeleteButton);
            title=(TextView) itemView.findViewById(R.id.doc_title);
            date=(TextView) itemView.findViewById(R.id.doc_date);


        }
    }
}
