package com.empresa.tap_game;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    private ProgressBar enemyHealthBar;
    private TextView enemyCountTextView;
    private int enemyHealth;
    private int minionCount = 0;
    private int defeatedEnemies = 0;
    private final int minionsPerBoss = 5;
    private final int minionHealth = 100;
    private final int bossHealth = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        enemyHealthBar = findViewById(R.id.enemyHealthBar);
        enemyCountTextView = findViewById(R.id.enemyCountTextView);
        Button attackButton = findViewById(R.id.attackButton);

        spawnEnemy();

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
}