package edu.skku.map.personalassignment2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class Board extends BaseAdapter {
    Context c;
    Bitmap items[];

    private int a;

    Board(Context c,Bitmap arr[]){
        this.c=c;
        items=arr;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.layout_bitmap,null);
        }
        ImageView imageView= convertView.findViewById(R.id.imageView);

        imageView.setImageBitmap(items[position]);

        return convertView;
    }
}