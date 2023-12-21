package com.example.android04.common;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android04.R;
import com.example.android04.albums.AlbumsAdapter;

public class LeftSwipeDelete<T extends RecyclerView.Adapter> extends ItemTouchHelper.SimpleCallback{
    private T tAdapter;
    private SwipeToDeleteCallbackListener tListener;
    public interface SwipeToDeleteCallbackListener {
        void deleteItem(int position);
    }

    public LeftSwipeDelete(T tAdapter, SwipeToDeleteCallbackListener tListener) {
        super(0, ItemTouchHelper.LEFT);
        this.tAdapter = (T)tAdapter;
        this.tListener = tListener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        // Not used for swipe-to-delete
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        tListener.deleteItem(position);
    }

    @Override
    public void onChildDraw(
            @NonNull Canvas c,
            @NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder,
            float dX,
            float dY,
            int actionState,
            boolean isCurrentlyActive) {

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        // Get the background drawable
        Drawable background = ContextCompat.getDrawable(recyclerView.getContext(), R.color.swipe_delete_background);

        // Calculate the left and right boundaries for the background drawable
        int left = viewHolder.itemView.getRight() + (int) dX;
        int right = viewHolder.itemView.getRight();

        // Set the bounds for the drawable
        background.setBounds(left, viewHolder.itemView.getTop(), right, viewHolder.itemView.getBottom());

        // Draw the background drawable on the canvas
        background.draw(c);
    }
}
