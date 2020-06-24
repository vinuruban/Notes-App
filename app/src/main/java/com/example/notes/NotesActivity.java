package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.io.IOException;

public class NotesActivity extends AppCompatActivity {

    /** To decide when to save changes */
    private boolean notesHasChanged = false;

    EditText editText;

    Intent intent;

    int listViewPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        editText = (EditText) findViewById(R.id.editText);

        intent = getIntent();

        listViewPosition = intent.getIntExtra("notesPosition", 0);

        if (listViewPosition == 0) {
            editText.setHint("Add notes here...");
        } else {
            editText.setText(MainActivity.notesList.get(listViewPosition));
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notesHasChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
        String editedNotes = editText.getText().toString();

        if (listViewPosition == 0) { //if I'm in the "Add notes here..." page, to add new notes
            // If the notes haven't changed, continue with handling back button press
            if (!notesHasChanged) {
                super.onBackPressed();
                return;
            } else {
                MainActivity.notesList.add(editedNotes);
                MainActivity.arrayAdapter.notifyDataSetChanged();

                try { //THIS SERIALIZES THE OBJECT WITH ALL THE NOTES DATA THAT WAS ADDED ABOVE - MainActivity.notesList.add(editedNotes);
                    sharedPreferences.edit().putString("notes", ObjectSerializer.serialize(MainActivity.notesList)).apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                notesHasChanged = false;
            }
        }
        else { //edit new notes
            MainActivity.notesList.set(listViewPosition, editedNotes);
            MainActivity.arrayAdapter.notifyDataSetChanged();

            try { //WE NEED TO SERIALIZE AGAIN WITH THE UPDATED VALUE. WE CAN SIMPLY SERIALIZE AGAIN WITH THE notesList THAT WAS UPDATED ABOVE - MainActivity.notesList.set(listViewPosition, editedNotes);
                sharedPreferences.edit().putString("notes", ObjectSerializer.serialize(MainActivity.notesList)).apply();
            } catch (IOException e) {
                e.printStackTrace();
            }

            super.onBackPressed();
            return;
        }
    }
}