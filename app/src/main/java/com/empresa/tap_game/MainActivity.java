package com.empresa.tap_game;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private ListView rankingListView;
    private List<String> rankingList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://hito-cf94f-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference("users");

        EditText usernameEditText = findViewById(R.id.usernameEditText);
        Button startGameButton = findViewById(R.id.startGameButton);
        rankingListView = findViewById(R.id.rankingListView);

        rankingList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rankingList);
        rankingListView.setAdapter(adapter);

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                if (username.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Ingrese un Nombre de Usuario", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    intent.putExtra("USERNAME", username);
                    startActivity(intent);
                }
            }
        });

        // Leer datos de Firebase y actualizar el ranking
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rankingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String user = snapshot.getValue(String.class);
                    rankingList.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Failed to read data: " + databaseError.getMessage());
            }
        });
    }
}