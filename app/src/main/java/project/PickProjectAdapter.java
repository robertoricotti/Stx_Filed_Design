package project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stx_field_design.R;

import java.util.ArrayList;

import activity_portrait.MyApp;
import dialogs.CustomToast;

public class PickProjectAdapter extends RecyclerView.Adapter<PickProjectAdapter.ViewHolder> {
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
        ViewHolder viewHolder = new ViewHolder(contactView);

        // Aggiungi il codice per il clic lungo qui

        viewHolder.nameTextView.setOnLongClickListener((View v) -> {

            if (String.valueOf(context).contains("activity.UsbActivity")) {

                selectedItem = viewHolder.getAdapterPosition();
                notifyDataSetChanged();
                Log.d("file selezionato", String.valueOf(selectedItem));

            } else {
                String fileExtension = getFileExtension(files.get(viewHolder.getAdapterPosition()));

                if (fileExtension != null && (fileExtension.equals("pstx"))) {
                    selectedItem = viewHolder.getAdapterPosition();
                    notifyDataSetChanged();

                } else {
                    new CustomToast(MyApp.visibleActivity, "Invalid File Selected").show();
                }
            }
            return true;
        });

        return viewHolder;
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        String nameFile = files.get(position);

        // Set item views based on your views and data model
        ConstraintLayout constraintLayout = holder.panel;
        TextView textView = holder.nameTextView;
        ImageView imageView = holder.imageView;  // Aggiunto
        textView.setText(nameFile);
        String fileExtension = getFileExtension(nameFile);
        if (fileExtension != null) {
            switch (fileExtension.toLowerCase()) {


                case "pdf":
                case "doc":
                case "docx":
                case "xls":
                case "xlsx":
                case "txt":
                case "dxf":
                case "xml":
                case "landxml":
                case "dwg":
                case "csv":
                    imageView.setImageResource(R.drawable.generic_file);
                    imageView.setImageTintList(ContextCompat.getColorStateList(MyApp.visibleActivity, R.color.red));
                    break;
                case "pstx":
                    imageView.setImageResource(R.drawable.generic_file);
                    imageView.setImageTintList(ContextCompat.getColorStateList(MyApp.visibleActivity, R.color.blue));
                    break;
                default:
                    // Imposta un'immagine di default o lasciala vuota
                    imageView.setImageResource(R.drawable.file_alet_96);
                    imageView.setImageTintList(ContextCompat.getColorStateList(MyApp.visibleActivity, R.color.black));
                    break;
            }
        }

        constraintLayout.setBackgroundColor(selectedItem == position ? ContextCompat.getColor(constraintLayout.getContext(), R.color.orange) : ContextCompat.getColor(constraintLayout.getContext(), R.color.transparent));
    }

    private String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public ImageView imageView;
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
            imageView = itemView.findViewById(R.id.imageView);
            nameTextView.setOnLongClickListener((View v) -> {
                selectedItem = getAdapterPosition();
                notifyDataSetChanged();
                return true;
            });
        }

    }

    public String getSelectedFilePath() {
        if (selectedItem != RecyclerView.NO_POSITION) {
            return files.get(selectedItem);
        }
        return null;
    }

    public void removeItem(int position) {
        if (position > -1) {
            files.remove(position);
            selectedItem = -1;
            notifyItemRemoved(position);
        } else {
            selectedItem = -1;
            notifyItemRemoved(position);
        }
    }


}

