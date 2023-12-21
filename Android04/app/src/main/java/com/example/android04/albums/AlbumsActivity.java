package com.example.android04.albums;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.android04.R;
import com.example.android04.album.AlbumActivity;
import com.example.android04.common.*;
import com.example.android04.common.SearchUtil;
import com.google.android.material.textfield.TextInputEditText;

public class AlbumsActivity extends AppCompatActivity implements AlbumsAdapter.OnItemClickListener {
    private RecyclerView albumsRecyclerView;
    private TextInputEditText albumNameEditText;
    private TextInputEditText currentlyClicked;
    private AlbumsAdapter albumsAdapter;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, AlbumsActivity.class);
        AppData.saveToFile(context);
        return intent;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.albums_view);
        // Retrieve the AppData from the system
        AppData.loadFromFile(getApplicationContext());
        SearchUtil.setupSearchButton(this);
        albumsRecyclerView = findViewById(R.id.albumsRecyclerView);
        albumNameEditText = findViewById(R.id.albumName);
        albumNameEditText.setOnClickListener(v -> {
            if(currentlyClicked != null) currentlyClicked.clearFocus();
            currentlyClicked = (TextInputEditText) v;
        });

        albumsAdapter = new AlbumsAdapter(this);
        albumsRecyclerView.setAdapter(albumsAdapter);
        albumsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Add left swipe of object to delete functionality
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new LeftSwipeDelete<>(albumsAdapter, albumsAdapter));
        itemTouchHelper.attachToRecyclerView(albumsRecyclerView);
        //albumsAdapter.updateData();

        // Used to de-focus albumNameEditText when the based is clicked in scenario in which the screen is not filled
        View rootView = getWindow().getDecorView().getRootView();
        rootView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Check if the touch is outside the TextInputEditText
                if (!isTouchInsideView(event, albumNameEditText)) {
                    // Clear focus from TextInputEditText
                    refocus(null);
                }
            }
            // Needed to suppress the messages that OnTouch also need to call OnClick
            v.performClick();
            return false;
        });

        // CREATE album will be done the hit on enter in the textfield, granted that the String in textfield is valid
        albumNameEditText.setOnKeyListener((view, keyCode, event) -> {
            // Check if key was pressed down and then if it was the enter key
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                String albumName = (albumNameEditText.getText() != null) ? albumNameEditText.getText().toString().trim() : null;
                if (!TextUtils.isEmpty(albumName) && AppData.getInstance().getAllAlbums().stream().noneMatch(a -> a.getName().equals(albumName.trim()))) {
                    // Create a new album and add it to list
                    AppData.getInstance().getAllAlbums().add(new Album(albumName.trim()));
                    albumNameEditText.setText(""); // clears the EditText
                    albumsAdapter.notifyItemInserted(AppData.getInstance().getAllAlbums().size() - 1); // Notify is used to prompt the RecyclerView to update
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(albumNameEditText.getWindowToken(), 0);
                    return true; // consume the event
                }else{
                    albumNameEditText.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(albumNameEditText.getWindowToken(), 0);
                    Toast.makeText(this, "Error: non-legal album name", Toast.LENGTH_LONG).show();
                }
            }
            return false; // let the system handle the event
        });
    }

    /**
     * The below is used for refocusing logic of TextInputEditText objects based on click
     */
    @Override
    public void onItemClick(int position) {
        // On single click reset focus and make the previous focused item non-editable if it is not the createAlbum text field
        AlbumsAdapter.AlbumViewHolder viewHolder = (AlbumsAdapter.AlbumViewHolder) albumsRecyclerView.findViewHolderForAdapterPosition(position);
        refocus(viewHolder.textAlbumItemName);
    }

    /**
     * Method used for refocusing can pass through null just sets currentlyClicked = null
     * Also hides the keyboard if necessary
     */
    private void refocus(TextInputEditText t){
        if(currentlyClicked != null){
            currentlyClicked.clearFocus();
            // If the text field is part of the RecyclableView then make the previous item uneditable and remove the keyboard
            if(currentlyClicked != albumNameEditText) {
                currentlyClicked.setEnabled(false);
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(albumNameEditText.getWindowToken(), 0);
            }
        }
        currentlyClicked = t;
        if(t != null) t.requestFocus();
    }

    /**
     * The below is used for OPEN album logic
     */
    @Override
    public void onItemDoubleClick(int position) {
        // On double click OPEN the album
        Album selectedAlbum = AppData.getInstance().getAllAlbums().get(position);
        startActivity(AlbumActivity.newIntent(this, position, selectedAlbum));
    }

    /**
     * The below is used for RENAME album logic
     */
    @Override
    public void onItemLongClick(int position) {
        // Get the ViewHolder for the selected album
        AlbumsAdapter.AlbumViewHolder viewHolder = (AlbumsAdapter.AlbumViewHolder) albumsRecyclerView.findViewHolderForAdapterPosition(position);
        // Check if the viewHolder is not null
        if (viewHolder != null) {
            // Get the TextInputEditText from the ViewHolder
            TextInputEditText albumItemNameEditText = viewHolder.textAlbumItemName;
            albumItemNameEditText.setEnabled(true);
            albumItemNameEditText.requestFocus();

            // Handle the enter key press separately
            albumItemNameEditText.setOnKeyListener((view, keyCode, event) -> {
                // Check if key was pressed down and then if it was the enter key
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    handleEnterKey(position, albumItemNameEditText);
                    return true; // consume the event
                }
                return false; // let the system handle the event
            });
        }
    }

    // TODO RENAME
    // Handles the rename action
    private void handleEnterKey(int position, TextInputEditText albumItemNameEditText) {
        String oldName = AppData.getInstance().getAllAlbums().get(position).getName();
        String albumName = albumItemNameEditText.getText().toString();
        if ((!TextUtils.isEmpty(albumName) || !albumName.equals("")) &&
                !AppData.getInstance().getAllAlbums()
                        .stream()
                        .anyMatch(a -> a.getName().equals(albumName))) {
            // RENAME the album
            AppData.getInstance().getAllAlbums().get(position).setName(albumName);
            albumItemNameEditText.setEnabled(false);
        } else {
            // Restore the previous name since invalid
            albumItemNameEditText.setText(oldName);
            albumItemNameEditText.setEnabled(false);
            Toast.makeText(this, "Error: Rename was not successful due to improper or duplicate name", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * @param event event caused by touch motion
     * @param view View view dimensions of a certain xml object
     */
    private boolean isTouchInsideView(MotionEvent event, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        float x = event.getRawX();
        float y = event.getRawY();
        return x > location[0] && x < location[0] + view.getWidth() && y > location[1] && y < location[1] + view.getHeight();
    }


    /**
     * Saves data when the activity goes into the background
     */
    @Override
    protected void onPause() {
        super.onPause();
        AppData.saveToFile(getApplicationContext());
    }

    /**
     * Save data when the activity is being destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppData.saveToFile(getApplicationContext());
    }
}
