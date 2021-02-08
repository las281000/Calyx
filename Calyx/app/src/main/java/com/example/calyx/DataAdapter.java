package com.example.calyx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class DataAdapter extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> values;

    public DataAdapter (Context context, ArrayList <String> values) {
        super(context, R.layout.server_file_itm, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.server_file_itm, parent, false);
        TextView textView = (TextView) view.findViewById(R.id.file_name);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.item);
        textView.setText(values.get(position));

        return view;
    }

}
