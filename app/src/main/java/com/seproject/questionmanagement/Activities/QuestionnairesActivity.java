package com.seproject.questionmanagement.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.seproject.questionmanagement.Adapters.Questionnaires;
import com.seproject.questionmanagement.QuestionnaireScreens.QuestionnaireProfile;
import com.seproject.questionmanagement.R;

public class QuestionnairesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference mRef;
    private String userID;
    private String userRole;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    String from;
    ImageView mBack;

    Context context =this;
    Query query;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaires);

        mBack = findViewById(R.id.back);
        mBack.setOnClickListener(view -> onBackPressed());

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        from = getIntent().getStringExtra("status");
        if(from.equals("active")){
             query = db.collection("Questionnaires").whereGreaterThan("deadline", System.currentTimeMillis());

        }
        else if(from.equals("expired")){
            query = db.collection("Questionnaires").whereLessThan("deadline", System.currentTimeMillis());

        }
        else if(from.equals("my")){

            query = db.collection("Questionnaires").whereEqualTo("publisher",userID);

        }
        else if(from.equals("all")){

            query = db.collection("Questionnaires");

        }


        FirestoreRecyclerOptions<Questionnaires> response = new FirestoreRecyclerOptions.Builder<Questionnaires>()
                .setQuery(query, Questionnaires.class)
                .build();

        recyclerView = findViewById(R.id.recView);

        //Recview
        recyclerView = findViewById(R.id.recView);
        recyclerView.setNestedScrollingEnabled(false);
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        //setBottomBar();


        adapter = new FirestoreRecyclerAdapter<Questionnaires, mHolder>(response) {
            @Override
            public void onBindViewHolder(mHolder holder, int position, Questionnaires model) {



                holder.title.setText(model.getTitle());
                holder.explain.setText(model.getExplain());


                //holder.textCount.setText(String.valueOf(model.getClicks()));

                long date = model.getCreated_time();
                String dateText = DateFormat.format("MMM dd, yyyy", date).toString();
                holder.dateTextView.setText(dateText);


                holder.itemView.setOnClickListener(v -> {
                    Intent i = new Intent(context, QuestionnaireProfile.class);
                    i.putExtra("docID", model.getDocID());
                    startActivity(i);
                });
            }

            @Override
            public mHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.single_q_item_layout, group, false);

                return new mHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        adapter.stopListening();
        recyclerView.setAdapter(adapter);

    }




    public class mHolder extends RecyclerView.ViewHolder {

        TextView title, explain,dateTextView,textCount;


        public mHolder(View itemView) {
            super(itemView);
            //
            title = itemView.findViewById(R.id.title);
            explain = itemView.findViewById(R.id.option5);

            textCount = itemView.findViewById(R.id.item_count);
            dateTextView = itemView.findViewById(R.id.dateText);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }


}