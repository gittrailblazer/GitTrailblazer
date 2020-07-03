package com.example.githubtrailblazer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;

import java.util.HashSet;
import java.util.Set;


public class QuestionaireActivity extends AppCompatActivity {
    Set<String> topics = new HashSet<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionaire);

    }
    public void buttonClicked(View view) {
        ToggleButton tb = (ToggleButton) view;
        String topic = tb.getTextOff().toString();
        if (tb.isChecked()) {
            topics.add(topic);
        } else {
            topics.remove(topic);
        }
    }
    public void addCustomTopic(View view) {
        EditText editText = (EditText) findViewById(R.id.editTextTextTopic);
        String customTopic = editText.getText().toString();
        topics.add(customTopic);
        editText.setText(null);
    }
    public void goNext(View view) {
        // Save topics to firestore

        Intent intent = new Intent(this, DrawerActivity.class);
        startActivity(intent);
    }
}