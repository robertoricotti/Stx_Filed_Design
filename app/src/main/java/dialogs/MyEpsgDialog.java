package dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;

import com.example.stx_field_design.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import project.DataProjectSingleton;
import utils.FullscreenActivity;
import utils.MyEpsgNumber;

public class MyEpsgDialog {
    Activity activity;
    public Dialog dialog;
    ArrayList<String> epsgList;
    SearchView searchView;
    ListView listView;
    CustomArrayAdapter arrayAdapter;
    String strEpsg;

    public MyEpsgDialog(Activity activity) {
        this.activity = activity;
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.epsg);

    }

    public void show() {
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        DisplayMetrics metrics = dialog.getContext().getResources().getDisplayMetrics();
        int parentHeight = (int) (metrics.heightPixels * 0.8);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, parentHeight);
        dialog.show();
        FullscreenActivity.setFullScreen(dialog);
        findView();
        init();
        onClick();


    }

    @SuppressLint("SetTextI18n")
    private void onClick() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFromList = (String) (listView.getItemAtPosition(position));
            try {
                strEpsg = String.valueOf(MyEpsgNumber.class.getField(selectedFromList).getInt(selectedFromList));
                DataProjectSingleton dataProject = DataProjectSingleton.getInstance();
                dataProject.clearData();
                dataProject.setEpsgCode(strEpsg,activity);
                dialog.dismiss();
            }
            catch (Exception ignored) {}
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                arrayAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });

        int searchCloseButtonId = searchView.findViewById(androidx.appcompat.R.id.search_close_btn).getId();
        ImageView closeButton = searchView.findViewById(searchCloseButtonId);

        closeButton.setOnClickListener(v -> {
            searchView.setQuery("", false);
            searchView.clearFocus();
            dialog.dismiss();
        });

    }



    private void findView() {
        listView = dialog.findViewById(R.id.list_item);
        searchView = dialog.findViewById(R.id.search);
        searchView.setQuery("",true);
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        Class<MyEpsgNumber> clazz = MyEpsgNumber.class;
        Field[] methods = clazz.getFields();
        epsgList = new ArrayList<>();

        try {
            for (Field method : methods) {
                epsgList.add(method.getName());
            }
        }
        catch (Exception ignored) {}

        arrayAdapter = new CustomArrayAdapter(activity, R.layout.text_row, R.id.my_text_row, epsgList);
        listView.setAdapter(arrayAdapter);
    }

    // La tua classe ArrayAdapter personalizzata
    private static class CustomArrayAdapter extends ArrayAdapter<String> {
        private final List<String> originalList;
        private final List<String> filteredList;

        public CustomArrayAdapter(@NonNull Activity context, int resource, int textViewResourceId, @NonNull List<String> objects) {
            super(context, resource, textViewResourceId, objects);
            originalList = new ArrayList<>(objects);
            filteredList = new ArrayList<>(objects);
        }

        @NonNull
        @Override
        public android.widget.Filter getFilter() {
            return new android.widget.Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    filteredList.clear();
                    final FilterResults results = new FilterResults();

                    if (constraint == null || constraint.length() == 0) {
                        // Nessun testo di filtro, mostra l'intera lista
                        filteredList.addAll(originalList);
                    } else {
                        // Filtra la lista in base alla sottostringa
                        final String filterPattern = constraint.toString().toLowerCase().trim();

                        for (final String item : originalList) {
                            if (item.toLowerCase().contains(filterPattern)) {
                                filteredList.add(item);
                            }
                        }
                    }

                    results.values = filteredList;
                    results.count = filteredList.size();
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    clear();
                    addAll((List<String>) results.values);
                    notifyDataSetChanged();
                }
            };
        }
    }
}

