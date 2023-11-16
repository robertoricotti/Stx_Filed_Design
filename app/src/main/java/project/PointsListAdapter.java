package project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.stx_field_design.R;
import java.util.ArrayList;

public class PointsListAdapter extends BaseAdapter {

    private ArrayList<String> list;
    private Context context;

    public PointsListAdapter(Context context, ArrayList<String> list) {
        this.list = list;
        this.context = context;
    }

    private class ViewHolder {
        TextView itemID;
        ImageButton itemEdit;
        public ViewHolder(View view) {
            itemID = view.findViewById(R.id.strID);
            itemEdit = view.findViewById(R.id.item_edit);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String strItem = getItem(position);

        viewHolder.itemID .setText(strItem);
        viewHolder.itemEdit.setOnClickListener((View v) -> {
            System.out.println("HELLOO");
        });

        return convertView;

    }
}