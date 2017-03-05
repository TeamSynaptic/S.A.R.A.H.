package synaptic.dementiahack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListAdapter extends ArrayAdapter<Entry>{
    private final Context context;
    private ArrayList<Entry> entries;
    private ArrayList<Entry> finished_entries;
    private View.OnClickListener delete_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View parentRow = (View) v.getParent();
            ListView listView = (ListView) parentRow.getParent().getParent(); //First parent is relative layout
            int position = listView.getPositionForView(parentRow);
            String current_date_time_string = DateFormat.getDateTimeInstance().format(new Date());
            entries.get(position).setTimeEnded(current_date_time_string);
            entries.get(position).setFinished(true);
            finished_entries.add(entries.get(position));
            entries.remove(position);
            notifyDataSetChanged();
        }
    };
    public ListAdapter(Context context, ArrayList<Entry> e, ArrayList<Entry> x){
        super(context, -1, e);
        this.context = context;
        entries = e;
        finished_entries = x;
    }
    @Override
    public View getView(int position, View convert_view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row_view = inflater.inflate(R.layout.item_todo, parent, false);
        TextView task_title = (TextView) row_view.findViewById(R.id.task_title);
        task_title.setText(entries.get(position).getTitle());
        Button task_delete = (Button) row_view.findViewById(R.id.task_delete);
        task_delete.setOnClickListener(delete_listener);
        return row_view;
    }
}
