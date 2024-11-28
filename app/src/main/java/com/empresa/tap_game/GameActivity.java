package com.empresa.tap_game;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GameActivity extends AppCompatActivity {
    private ProgressBar playerHealthBar;
    private ProgressBar enemyHealthBar;
    private TextView enemyCountTextView;
    private LinearLayout gameOverLayout;
    private DatabaseReference databaseReference;

    private int playerHealth = 100;
    private int enemyHealth;

    private int minionCount = 0;

    private int defeatedEnemies = 0;

    private final int minionsPerBoss = 5;
    private final int minionHealth = 100;
    private final int bossHealth = 300;
    private final int enemyAttackDamage = 5;

    private final Handler handler = new Handler();
    private final Runnable enemyAttackRunnable = new Runnable() {
        @Override
        public void run() {
            if (playerHealth > 0) {
                playerHealth -= enemyAttackDamage;
                playerHealthBar.setProgress(playerHealth);
                if (playerHealth <= 0) {
                    endGame();
                } else {
                    handler.postDelayed(this, 2000); // El enemigo ataca cada 2 segundos
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        playerHealthBar = findViewById(R.id.playerHealthBar);
        enemyHealthBar = findViewById(R.id.enemyHealthBar);
        enemyCountTextView = findViewById(R.id.enemyCountTextView);
        gameOverLayout = findViewById(R.id.gameOverLayout);
        Button attackButton = findViewById(R.id.attackButton);
        Button retryButton = findViewById(R.id.retryButton);
        Button titleScreenButton = findViewById(R.id.titleScreenButton);

        // Inicializar Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://hito-cf94f-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference("users");

        spawnEnemy();
        handler.postDelayed(enemyAttackRunnable, 2000); // Empeiza el ataque del enemigo

        attackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enemyHealth > 0) {
                    enemyHealth -= 10;
                    enemyHealthBar.setProgress(enemyHealth);
                    if (enemyHealth <= 0) {
                        Toast.makeText(GameActivity.this, "Enemigo Derrotado!", Toast.LENGTH_SHORT).show();
                        defeatedEnemies++;
                        minionCount++;
                        updateEnemyCount();
                        spawnEnemy();
                    }
                }
            }
        });

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
            }
        });

        titleScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToTitleScreen();
            }
        });
    }

    private void spawnEnemy() {
        if (minionCount > 0 && minionCount % minionsPerBoss == 0) {
            enemyHealth = bossHealth;
            Toast.makeText(this, "Ha Aparecido un Jefe!", Toast.LENGTH_SHORT).show();
        } else {
            enemyHealth = minionHealth;
        }
        enemyHealthBar.setMax(enemyHealth);
        enemyHealthBar.setProgress(enemyHealth);
    }

    private void updateEnemyCount() {
        enemyCountTextView.setText("Enemigos derrotados: " + defeatedEnemies);
    }

    private void endGame() {
        handler.removeCallbacks(enemyAttackRunnable);
        gameOverLayout.setVisibility(View.VISIBLE);

        // Guardar nombre del jugador y puntuación en Firebase
        String username = getIntent().getStringExtra("USERNAME");
        if (username != null) {
            databaseReference.push().setValue(username + " - Puntuación: " + defeatedEnemies, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e("FirebaseError", "Data could not be saved " + databaseError.getMessage());
                    } else {
                        Log.d("FirebaseSuccess", "Player score saved successfully.");
                    }
                }
            });
        }
    }

    private void restartGame() {
        playerHealth = 100;
        minionCount = 0;
        defeatedEnemies = 0;
        updateEnemyCount();
        playerHealthBar.setProgress(playerHealth);
        gameOverLayout.setVisibility(View.GONE);
        spawnEnemy();
        handler.postDelayed(enemyAttackRunnable, 2000); // Reanuda el ataque del enemigo
    }

    private void goToTitleScreen() {
        Intent intent = new Intent(GameActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}