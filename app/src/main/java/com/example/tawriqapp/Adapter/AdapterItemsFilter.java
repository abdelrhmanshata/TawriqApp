package com.example.tawriqapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tawriqapp.Model.Item;
import com.example.tawriqapp.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterItemsFilter extends ArrayAdapter<Item> {
    Context context;

    public AdapterItemsFilter(@NonNull Context context, @NonNull List<Item> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_filter_item, parent, false);
        }

        Item itemData = getItem(position);

        ShapeableImageView PaperImage = listItemView.findViewById(R.id.paperImage);
        TextView courseName = listItemView.findViewById(R.id.courseName);
        TextView paperType = listItemView.findViewById(R.id.paperType);

        try {
            Picasso
                    .get()
                    .load(itemData.getCoverPhotoUrl_1().trim() + "")
                    .fit()
                    .placeholder(R.drawable.loading)
                    .into(PaperImage);
        } catch (Exception e) {
            Log.d("" + context, e.getMessage());
        }

        courseName.setText(itemData.getItemTitle());
        paperType.setText(itemData.getProgram().getProgramName());

        return listItemView;
    }
}
