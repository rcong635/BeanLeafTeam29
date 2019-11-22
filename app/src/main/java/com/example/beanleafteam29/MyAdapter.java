package com.example.beanleafteam29;

import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;
import java.util.Map;
import java.util.HashMap;
import android.util.SparseBooleanArray;
import java.lang.Integer;
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private static List<Map<String, Object> > print = new ArrayList<>();
    private static List<Map<String, Object>> deleteTracker = new ArrayList<>();
    public static List<Map<String, Object>> FireBaseTracker = new ArrayList<>();

    // sparse boolean array for checking the state of the items
    public static SparseBooleanArray itemStateArray = new SparseBooleanArray();

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<Map<String, Object> >p) {
        print = p;
    }

    public List<Map<String, Object> > delete_list(){
        System.out.println("itemCheck size: " + itemStateArray.size());
        System.out.println("Current size: " + print.size());
        for(int i = 0; i < itemStateArray.size(); i++) {
            if(!itemStateArray.valueAt(i)) {
                int key = itemStateArray.keyAt(i);
                Map<String, Object> m = print.get(key);
                FireBaseTracker.add(m);
            }
            else{
                int key = itemStateArray.keyAt(i);
                Map<String, Object> m = print.get(key);
                deleteTracker.add(m);
            }

        }
        System.out.println("itemCheck size: " + FireBaseTracker.size());
        System.out.println("Firebase size: " + FireBaseTracker.size());
        return FireBaseTracker;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder implements OnClickListener
    public class ViewHolder extends RecyclerView.ViewHolder  {
        //not in the row_layout.xml
        // each data item is just a string in this case
        public TextView txtHeader;
        public TextView txtFooter;
        public View layout;
        public CheckBox chk_box;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtHeader = (TextView) v.findViewById(R.id.firstLine);
            txtFooter = (TextView) v.findViewById(R.id.secondLine);
            chk_box = (CheckBox) v.findViewById(R.id.checkbox);
            //listener = v.setOnClickListener(this);
            //chk_box.setOnClickListener(this);
        }

        void bind(int position) {
            // use the sparse boolean array to check
            if (!itemStateArray.get(position, false)) {
                chk_box.setChecked(false);
            } else {
                chk_box.setChecked(true);
            }
        }

    }

    public void add(int position, Map<String,Object> item) {
        print.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        print.remove(position);
        notifyItemRemoved(position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final String name = print.get(position).get("Name").toString();
        String price = print.get(position).get("Price").toString();
        String caf = print.get(position).get("Caffeine").toString();
        holder.txtHeader.setText(name);
        holder.txtFooter.setText("Price: " + price + " Caffeine: " + caf);

        holder.chk_box.setTag(name);
        holder.chk_box.setId(position);
        holder.bind(position);
        holder.chk_box.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.out.println("Entered");
                int adapterPosition = position;
                if (!itemStateArray.get(adapterPosition, false)) {
                    holder.chk_box.setChecked(true);
                    itemStateArray.put(adapterPosition, true);
                }
                else  {
                    holder.chk_box.setChecked(false);
                    itemStateArray.put(adapterPosition, false);
                }
            }
        });

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return print.size();
    }


}
