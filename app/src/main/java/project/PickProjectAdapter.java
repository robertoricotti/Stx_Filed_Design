package project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stx_field_design.R;

import java.util.ArrayList;

public class PickProjectAdapter extends RecyclerView.Adapter <PickProjectAdapter.ViewHolder>{
    private ArrayList<String> files;
    private int selectedItem = -1;

    public PickProjectAdapter(ArrayList<String> filesName) {
        files = filesName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.pick_project_row, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        String nameFile = files.get(position);

        // Set item views based on your views and data model
        ConstraintLayout constraintLayout = holder.panel;
        TextView textView = holder.nameTextView;
        textView.setText(nameFile);

        constraintLayout.setBackgroundColor(selectedItem == position ? ContextCompat.getColor(constraintLayout.getContext(), R.color.orange) : ContextCompat.getColor(constraintLayout.getContext(), R.color.transparent));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public int getSelectedItem() {
        return  selectedItem;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public ConstraintLayout panel;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        @SuppressLint("NotifyDataSetChanged")
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = itemView.findViewById(R.id.path_tv);
            panel = itemView.findViewById(R.id.panel);

            nameTextView.setOnLongClickListener((View v) -> {
                selectedItem = getAdapterPosition();
                notifyDataSetChanged();
                return true;
            });
        }
    }
}

