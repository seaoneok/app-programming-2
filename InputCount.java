package edu.skku.map.personalassignment2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class InputCount extends BaseAdapter {
    private Context context;
    private final List<String> countBlacks;

    public InputCount(Context context, List<String> countBlacks) {
        this.context = context;
        this.countBlacks = countBlacks;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(context);

            gridView = inflater.inflate(R.layout.number_black, null);

            TextView textView = (TextView) gridView.findViewById(R.id.blackNum);
            textView.setText(String.valueOf(countBlacks.get(position)));
        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }

    @Override
    public int getCount() {
        return countBlacks.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
