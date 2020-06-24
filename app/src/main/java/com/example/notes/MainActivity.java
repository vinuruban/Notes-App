package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayAdapter<String> arrayAdapter;
    static ArrayList<String> notesList = new ArrayList<String>();
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);Log.i("sdf", "onCreate: " + notesList.size());
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);

        //clear object
        notesList.clear();

        try {
            //REMEMBER: DATA IS STILL STORED IN SHARED PREFERENCE REGARDLESS OF THE APP BEING CLOSED
            //ALTHOUGH notesList AND OTHERS ARE CLEARED ABOVE, DATA IS STILL STORED WITHIN SHARED PREFERENCE, SO IF I REOPEN THE APP WITH SAVED DATA, THE CODE BELOW WILL RESTORE THEM BACK INTO notesList BELOW (CHECK IF STATEMENT BELOW). IF I REOPEN WITH NO SAVED DATA, notesList WILL BE SET EMPTY (WHICH WILL SATISFY IF STATEMENT BELOW - "if (notesList.size() == 0)")
            //deserializes from String to readable ArrayList<String>!
            notesList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("notes", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (notesList.size() == 0) { //if there is no data added yet (on create)...
            notesList.add("Click here to add notes...");
        }

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, notesList);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, NotesActivity.class);
                intent.putExtra("notesPosition", position);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // To nothing
                } else {
                    notesList.remove(position); //update arraylist
                    arrayAdapter.notifyDataSetChanged(); //update adapter (to show on screen)
                    Toast.makeText(getApplicationContext(), "Note deleted", Toast.LENGTH_SHORT).show();
                    try {
                        sharedPreferences.edit().putString("notes", ObjectSerializer.serialize(MainActivity.notesList)).apply(); //store changes in shared preferences (as notes) to load again at app reopen
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

    }
}