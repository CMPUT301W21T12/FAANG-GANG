package com.faanggang.wisetrack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PublishExperimentActivity extends AppCompatActivity {

    private EditText inputName;
    private EditText inputDescription;
    private EditText inputRegion;
    private EditText inputMinTrials;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_experiment);

        inputName = findViewById(R.id.name_input);
        inputDescription = findViewById(R.id.description_input);
        inputRegion = findViewById(R.id.region_input);
        inputMinTrials = findViewById(R.id.minTrials_input);

        nextButton = findViewById(R.id.choose_test_type_button);

        // Attaching OnClick listener to the submit button
        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String name = inputName.getText().toString();
                String description = inputDescription.getText().toString();
                String region = inputRegion.getText().toString();
                int minTrials = Integer.parseInt(inputMinTrials.getText().toString());

                Intent intent = new Intent(PublishExperimentActivity.this, PublishExperimentActivity2.class);

                intent.putExtra("EXTRA_NAME", name);
                intent.putExtra("EXTRA_DESCRIPTION", description);
                intent.putExtra("EXTRA_REGION", region);
                intent.putExtra("EXTRA_MIN_TRIALS", minTrials);

                startActivity(intent);

            }
        });

    }

}