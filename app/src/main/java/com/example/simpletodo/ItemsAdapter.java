// 2d. the viewholder's adapter for the recycler view:

package com.example.simpletodo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Responsible for displaying data from the model into a row for the recycler view

// The adapter is parameterized by a view holder so define the view holder first
// select the one inside my package name
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder>{

    //3b. because the adapter is behind rv, main activity needs to be notified
    //of long clicks. an interface in the adapter can be used to talk to main activity
    public interface OnLongClickListener {
        //class needs to know position of long press so it can notify adapter that
        //it should be targeted for removal
        void onItemLongClicked(int position);
    }

    public interface OnClickListener {
        void onItemClicked(int position);
    }

    // 2e. to fill out adapter info use a constructor to grab data from main activity
    // (can right click generate constructor) data from model will be a list of strings
    // so its passed as parameter for constructor
    List<String> items; // member variable is used as reference to access all methods
    OnLongClickListener longClickListener;
    OnClickListener clickListener;

    //3c, 5c. Constructor needs to be modified to take in new listener
    //ORIGINAL: public ItemsAdapter(List<String> items)
    public ItemsAdapter(List<String> items, OnLongClickListener longClickListener, OnClickListener clickListener) {
        this.items = items; //sets member variable equal to constructor's variable
        this.longClickListener = longClickListener;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    //creates each view & wraps them inside view holders
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Use layout inflater to inflate a view
        //parts of method: context is from the viewGroup parameter, xml file of view im
        //creating (built in resource (simple_list...) us used), parent & false meaning
        //the view is attached with recycler view instead of the root
        View todoView = LayoutInflater.from(parent.getContext()).inflate
                (android.R.layout.simple_list_item_1, parent, false);

        //Wrap it inside a view holder and return it
        return new ViewHolder(todoView); //
    }

    //takes data from a particular position & places it into a particular view holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Grab the item at the position
        String item = items.get(position);

        //Bind the item into the specified view holder using bind method
        holder.bind(item);
    }

    //number of items available in data (tells RV the number of items)
    @Override
    public int getItemCount() {
        return items.size();
    }

    // Container to provide easy access to views that represent each row of the list
    class ViewHolder extends RecyclerView.ViewHolder // extends .. & requires constructor
    {
        //gets reference to simple_list xml
        TextView tvItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(android.R.id.text1); // R means builtin resource
        }

        //Update the view inside the view holder with this data
        public void bind(String item) {
            tvItem.setText(item); //sets etxt on textview to be contents of item

            //5b. need to know position of clicked item so i can pass position
            //to editactivity. do this by attaching a click listener to textview
            tvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            //5d.
            tvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //??find out why onclick cant be boolean??
                    clickListener.onItemClicked(getAdapterPosition());
                }
            });

            //3d. an interface in the adapter can be used to talk to main activity
            tvItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //Notify the listener which position was long pressed
                    //passed in position of viewholder w/ getadapterposition method
                    longClickListener.onItemLongClicked(getAdapterPosition());
                    return true; //was false. true means callback consumes the one click
                }
            });
        }



    }

}
