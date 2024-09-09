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


import java.io.File;


public class PickProjectAdapterUSB extends RecyclerView.Adapter<PickProjectAdapterUSB.ViewHolder> {
    private ArrayList<String> files;
    private int selectedItem = -1;

    public PickProjectAdapterUSB(ArrayList<String> filesName) {
        files = filesName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.pick_project_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);

        // Set click listener per la selezione
        viewHolder.nameTextView.setOnClickListener((View v) -> {

           selectedItem = viewHolder.getAdapterPosition();
           notifyDataSetChanged();


        });



        return viewHolder;
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String nameFile = files.get(position);
        File file = new File(nameFile);

        // Set item views based on your views and data model
        ConstraintLayout constraintLayout = holder.panel;
        TextView textView = holder.nameTextView;
        ImageView imageView = holder.icon;
        Log.d("PickProjectAdapter", nameFile);
        if (file.isDirectory()) {
            // Set icona cartella
            imageView.setImageResource(R.drawable.folder_traffic_cone); // Assicurati di avere questa icona nelle risorse
            // Colore specifico per la cartella
        } else  {
            // Pulizia del nome file e verifica prefisso
            String cleanedName = nameFile.substring(nameFile.lastIndexOf("/")+1,nameFile.length()-1).trim(); // Rimuove eventuali spazi indesiderati
            String prefix = cleanedName.length() >= 5 ? cleanedName.substring(0, 5) : "";

            // Log per debug del prefisso estratto
            Log.d("PickProjectAdapter", "Prefisso estratto: " + cleanedName);


            // Usa startsWith per una gestione più robusta dei prefissi
            if (cleanedName.startsWith("#1P_#")) {
                imageView.setImageResource(R.drawable.image_1p);
                imageView.setImageTintList(ContextCompat.getColorStateList(MyApp.visibleActivity, R.color.blue));
            } else if (cleanedName.startsWith("#AB_#")) {
                imageView.setImageResource(R.drawable.image_ab);
                imageView.setImageTintList(ContextCompat.getColorStateList(MyApp.visibleActivity, R.color.blue));
            } else if (cleanedName.startsWith("#CS_#")) {
                imageView.setImageResource(R.drawable.image_cs);
                imageView.setImageTintList(ContextCompat.getColorStateList(MyApp.visibleActivity, R.color.blue));
            } else if (cleanedName.startsWith("#AR_#")) {
                imageView.setImageResource(R.drawable.area_image);
                imageView.setImageTintList(ContextCompat.getColorStateList(MyApp.visibleActivity, R.color.blue));
            } else {
                // Imposta un'icona di default se il prefisso non corrisponde
                imageView.setImageResource(R.drawable.disabled_visu);
                imageView.setImageTintList(ContextCompat.getColorStateList(MyApp.visibleActivity, R.color.transparentgray));

            }
        }

        textView.setText(file.getName());
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

    private void openFolder(File folder) {
        // Controlla se il parametro passato è effettivamente una directory
        if (!folder.isDirectory()) {
            Log.e("PickProjectAdapter", "Non è una directory: " + folder.getAbsolutePath());
            return;
        }

        // Ottiene una lista di file e cartelle all'interno della cartella selezionata
        File[] fileList = folder.listFiles();

        // Controlla se la cartella è vuota o se non riesce ad ottenere il contenuto
        if (fileList == null) {
            Log.e("PickProjectAdapter", "Impossibile leggere il contenuto della cartella: " + folder.getAbsolutePath());
            new CustomToast(MyApp.visibleActivity, "Errore nell'apertura della cartella").show();
            return;
        }

        // Pulisce la lista attuale dei file e aggiunge i nuovi elementi trovati
        files.clear();
        for (File file : fileList) {
            files.add(file.getAbsolutePath());
        }

        // Notifica all'adapter che i dati sono cambiati per aggiornare la visualizzazione
        notifyDataSetChanged();
        Log.d("PickProjectAdapter", "Contenuto della cartella caricato: " + folder.getAbsolutePath());
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public ImageView icon;
        public ConstraintLayout panel;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.imageView);
            nameTextView = itemView.findViewById(R.id.path_tv);
            panel = itemView.findViewById(R.id.panel);
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


