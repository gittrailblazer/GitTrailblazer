package com.git_trailblazer.v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.ToggleButton;

import com.git_trailblazer.v1.connector.Connector;
import com.git_trailblazer.v1.connector.UserHistoryData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class QuestionnaireActivity extends AppCompatActivity {
    Set<String> topics = new HashSet<String>();
    String githubUsername = null;
    // The row of topics that we are currently filling.
    TableRow rowOfTopics = null;
    // Counter of the number of topics added so we don't overfill rows
    int numTopics = 0;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        mHandler = new Handler(Looper.getMainLooper());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            githubUsername = extras.getString("githubUsername");
        }

        if (githubUsername != null) {
            Log.d("XXX", "GitHub username is " + githubUsername);
            // if logged in with github, use github activity to generate suggested topics
            new Connector.Query(Connector.QueryType.USER_HISTORY, githubUsername)
                    .exec(new Connector.ISuccessCallback() {
                        @Override
                        public void handle(Object result) {
                            UserHistoryData data = (UserHistoryData) result;
                            ArrayList<String> suggestedTopics = ((UserHistoryData) result).suggestedTopics;
                            // Move to a UI thread so can update the UI
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // create topic toggles for each of the suggested topics
                                    for (String suggestedTopic : suggestedTopics) {
                                        createTopicToggle(suggestedTopic, true);
                                    }
                                }
                            });
                        }
                    }, new Connector.IErrorCallback() {
                        @Override
                        public void error(String message) {
                            Log.e("GH_API_QUERY", "Failed query: " + message);
                        }
                    });
        } else {
            // otherwise, use a hardcoded list of topics
            ArrayList<String> suggestedTopics = new ArrayList<String>();
            suggestedTopics.add("React");
            suggestedTopics.add("NodeJS");
            suggestedTopics.add("JavaScript");
            suggestedTopics.add("CSS");
            suggestedTopics.add("HTML");
            suggestedTopics.add("Lisp");
            suggestedTopics.add("Python3");
            suggestedTopics.add("20xx");
            suggestedTopics.add("clxx");
            suggestedTopics.add("cl-20xx");

            // create topic toggles for each of the suggested topics
            for (String suggestedTopic : suggestedTopics) {
                createTopicToggle(suggestedTopic, true);
            }
        }
    }

    /**
     * When the toggle button is pressed, in addition to changing state, update the topics list.
     */
    public void topicToggled(View view) {
        ToggleButton tb = (ToggleButton) view;
        String topic = tb.getTextOff().toString();
        if (tb.isChecked()) {
            topics.remove(topic);
        } else {
            topics.add(topic);
        }
    }

    public void addCustomTopic(View view) {
        EditText editText = (EditText) findViewById(R.id.editTextTextTopic);
        String customTopic = editText.getText().toString();

        if (customTopic.isEmpty()) return;

        topics.add(customTopic);
        editText.setText(null);

        createTopicToggle(customTopic, true);
    }

    public void goNext(View view) {
        // Save topics to firestore
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection("UserData");
        Map<String, Object> data = new HashMap<>();
        List<String> topicList = new ArrayList<String>(topics);
        data.put("Topics", topicList);

        assert currentFirebaseUser != null;
        ref.document(currentFirebaseUser.getUid()).set(data);
        Intent intent = new Intent(this, DrawerActivity.class);
        startActivity(intent);
    }

    /**
     * Creates a topic toggle for the suggested topic
     *
     * @param suggestedTopic string representing the suggested topic
     * @param checked        whether the topic is checked on creation
     */
    public void createTopicToggle(String suggestedTopic, boolean checked) {
        // suggested topics table view ref
        TableLayout suggestedTopicsTable = findViewById(R.id.table_suggested_topics);
        // get a reference to the first toggle, we copy the layout params from this button
        ToggleButton firstTopicToggle = findViewById(R.id.toggleButton1);

        // Create a new row if the existing one is full
        if ((numTopics % 3) == 0) {
            // Add a list of topics to the suggested topics table
            rowOfTopics = (TableRow) getLayoutInflater().inflate(R.layout.topic_row, null);
            rowOfTopics.setPadding(5, 5, 5, 5);
        }

        ToggleButton aTopicToggle = (ToggleButton) getLayoutInflater().inflate(R.layout.topic_toggle_button, null);
        aTopicToggle.setText(suggestedTopic);
        aTopicToggle.setTextOff(suggestedTopic);
        aTopicToggle.setTextOn(suggestedTopic);
        TableRow.LayoutParams layoutParams = (TableRow.LayoutParams) firstTopicToggle.getLayoutParams();
        aTopicToggle.setLayoutParams(layoutParams);
        aTopicToggle.setOnClickListener(this::topicToggled);

        if (checked) {
            aTopicToggle.setChecked(true);
            topics.add(suggestedTopic);
        }

        assert rowOfTopics != null;
        rowOfTopics.addView(aTopicToggle);

        if ((numTopics % 3) == 0) {
            suggestedTopicsTable.addView(rowOfTopics);
        }

        numTopics++;

    }
}
