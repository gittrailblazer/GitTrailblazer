package com.example.githubtrailblazer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
            topics.remove(topic);
            tb.setTextColor(Color.WHITE);
        } else {
            topics.add(topic);
            tb.setTextColor(Color.BLACK);
        }
    }
    public void addCustomTopic(View view) {
        EditText editText = (EditText) findViewById(R.id.editTextTextTopic);
        String customTopic = editText.getText().toString();
        topics.add(customTopic);
        editText.setText(null);

        Context context = getApplicationContext();
        CharSequence text = "Success!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
    public void goNext(View view) {
        // Save topics to firestore
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection("UserData");
        Map<String, Object> data = new HashMap<>();
        List<String> topicList = new ArrayList<String>();
        topicList.addAll(topics);
        data.put("Topics", topicList);

        ref.document(currentFirebaseUser.getUid()).set(data);
        Intent intent = new Intent(this, DrawerActivity.class);
        startActivity(intent);
    }
}