package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //5g.
    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 1; //number is arbitrary when there is only one
                                                //activity

    List<String> items; // a list of the strings for the model

    // 2a. get a reference for each view in the main activity so that
    // logic can be added. do it by adding a member variable for each view
    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;
    //3f. by changing adapter from local variable to field, it can be accessed by all the
    //class's methods
    ItemsAdapter itemsAdapter;

    // 1. where the model is substantiated
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 1b. shows the ui

        // 2b. define member variables
        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);

        //construct adapter and pass in items
        // NOTE: Each view has a different set of methods I can call on them
        // Ex. etItem.setText("Hello World"); changes etItem text to parameter text
        //4c. empty arraylist & mock data not needed anymore.
        // ORIGINAL: items = new ArrayList<>();
        //        // 2c. added items are mock data
        //        items.add("Buy shoes");
        //        items.add("Workout");

        loadItems(); //4d. load model from file system on app startup

        // 3e. param to pass to items adapter
        ItemsAdapter.OnLongClickListener onLongClickListener = new
                ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                //Delete the item from the model
                items.remove(position);
                //Notify the adapter it was removed
                itemsAdapter.notifyItemRemoved(position);

                Toast toast = Toast.makeText(getApplicationContext(), "Removed item",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                saveItems(); //4e. should be called upon adding & removing items
                            //(store to file system after changes to model)
            }
        };

        //5e. need to define third parameter for itemsAdapter constructor
        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                //can verify method works with the log statement
                Log.d("MainActivity", "Single click at position " + position);
                //5f. create the new (edit) activity with an intent. this intent will tell the
                // android system to open another activity
                //!! MainActivity.this refers to the instance of the current (main) activity
                //while EditActivity.class refers to the class
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                //5g. pass the data being edited to the (edit) activity (contents of todo item & its
                //position). do this with put extra. it needs key param and value
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
                //display (edit) activity w/ startActivityForResult because I expect a result (the
                //updated todo item). needs intent and request code, which identifies a request for
                // a different activity.
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };
        //2e. ORIGINAL: returns the items adapter 3e. NEW: passes second param to adapter
        //ORIGINAL: ItemsAdapter itemsAdapter = new ItemsAdapter(items);
        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsAdapter); //set adapter on recycler view w/ setAdapter
        //sets layout on rv. default is vertical orientation
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        // 3a. when user taps on add button, take content from editText and add it to rv
        //do this by adding onclick listener to track button presses
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //grabs content of editText. getText returns Editable so convert to string
                String todoItem = etItem.getText().toString();
                //Add item to the model
                items.add(todoItem);
                //Notify adapter that an item is inserted
                //the position where item is inserted is the last position in the model
                itemsAdapter.notifyItemInserted(items.size() - 1);
                etItem.setText(""); //clears edit text after its submitted

                //give user feedback when event occurred successfully with toast
                //toast needs context, text, & length. use show method to make visible
                //ORIGINAL: Toast.makeText(getApplicationContext(), "Item was added",
                //                        Toast.LENGTH_SHORT).show();
                //my improved toast sets the position to the center of the screen:
                Toast toast = Toast.makeText(getApplicationContext(), "Added item",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                saveItems(); //4e. should be called upon adding & removing items
                            //(store to file system after changes to model)
            }
        });
    }

    //5i. to handle result of edit activity, override a method, specifically onActivityResult
    //??find out why onActivityResult stays in red w/o super call but class runs fine.??
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //check if request code matches with passed in request code & make sure its not an error
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            //Retrieve the updated text value (updated contents of item). The extras passed from
            //edit activity show in intent data
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            //Extract the original position of the edited item from the position key
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            //update model at right position with new item text
            items.set(position, itemText);
            //notify the adapter so recycler view knows something changed
            itemsAdapter.notifyItemChanged(position);
            //persist the changes
            saveItems();

            //toast to show the user their changes were saved
            Toast toast = Toast.makeText(getApplicationContext(), "Updated item",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult"); //w is warning
        }
    }

    //4b. methods for persistence. private bec. they are only called within main activity
    //returns file that stores list of new items. needs directory of app & name of file
    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }
    //This function will load items by reading every line of the data file (READS FILES)
    private void loadItems() {
        //reads all lines of data file & populates data into an arraylist for the models
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(),
                    Charset.defaultCharset()));
        } catch (IOException e) {
            //logs errors in logcat
            Log.e("MainActivity", "Error reading items", e);
            //if there is an exception the arraylist should be initialized to empty array
            items = new ArrayList<>();
        }
    }
    //This function saves items by writing them into the data file (WRITES FILES)
    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e);
        }
    }
}