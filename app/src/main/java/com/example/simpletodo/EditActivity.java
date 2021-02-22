//5a. created file for edit feature

package com.example.simpletodo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditActivity extends AppCompatActivity {

    //grab references to views in activity
    EditText etItem;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        etItem = findViewById(R.id.etItem);
        btnSave = findViewById(R.id.btnSave);

        //makes title more descriptive by changing action bar text
        //so whenever user is on this screen they know they are editing the item
        getSupportActionBar().setTitle("Edit item");

        //5h. pass data from intent to edit activity w/ getIntent method. that data should be
        //pushed to editText
        etItem.setText(getIntent().getStringExtra(MainActivity.KEY_ITEM_TEXT));
        //click listener for button. when user is done editing, they click save button.
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create an intent which will contain the edited result of what the user modified
                Intent intent = new Intent(); //left empty as shell to pass data
                //pass results of editing. params are key & result of edited text. also need
                //position to tell main activity the point in which text should be updated
                intent.putExtra(MainActivity.KEY_ITEM_TEXT, etItem.getText().toString());
                intent.putExtra(MainActivity.KEY_ITEM_POSITION, getIntent().getExtras().getInt(MainActivity.KEY_ITEM_POSITION));
                //set th result of the intent
                setResult(RESULT_OK, intent);
                //use finish method to finish current activity, close the screen and go back
                finish();
            }
        });

    }
}