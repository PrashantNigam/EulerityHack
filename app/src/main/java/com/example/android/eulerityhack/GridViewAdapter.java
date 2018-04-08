package com.example.android.eulerityhack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class GridViewAdapter extends BaseAdapter{

    private final String TAG = GridViewAdapter.class.getName();
    private Context context;
    private List<ImageDTO> images = new ArrayList<>();

    public GridViewAdapter(Context context, List<ImageDTO> images){
        this.context = context;
        this.images = images;
    }

    public void setItem(ImageDTO imageDTO, int position) {
        images.set(position, imageDTO);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {

        if (position < images.size()) {
            return images.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        /*ImageView img = null;
        if (convertView == null) {
            img = new ImageView(context);
            convertView = img;
            img.setPadding(5, 5, 5, 5);
        } else {
            img = (ImageView) convertView;
        }*/
        final ImageView img = new ImageView(context);
        convertView = img;
        img.setPadding(5,5,5,5);

        Target target = new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                images.get(position).setImage(bitmap);
                img.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.d(TAG, "onPrepareLoad");
            }
        };
        Picasso.get()
                .load(images.get(position).getUrl())
                .placeholder(R.drawable.placeholder)
                .resize(200, 300)
                .into(target);

        /*Picasso.get()
                .load(images.get(position).getUrl())
                .placeholder(R.drawable.placeholder)
                .resize(200, 300)
                .into(img);*/
        return convertView;
    }
}