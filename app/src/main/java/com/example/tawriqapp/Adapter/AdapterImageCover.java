package com.example.tawriqapp.Adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tawriqapp.R;
import com.example.tawriqapp.databinding.ImageItemBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterImageCover extends RecyclerView.Adapter<AdapterImageCover.ViewHolder> {

    ArrayList<Uri> uriArrayList;
    ArrayList<String> urlArrayList;

    public AdapterImageCover(ArrayList<Uri> uriArrayList, ArrayList<String> urlArrayList) {
        this.uriArrayList = uriArrayList;
        this.urlArrayList = urlArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ImageItemBinding binding = ImageItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!uriArrayList.isEmpty()) {
            holder.binding.coverImage.setImageURI(uriArrayList.get(position));
        } else {
            try {
                Picasso
                        .get()
                        .load(urlArrayList.get(position) + "")
                        .fit()
                        .placeholder(R.drawable.loading)
                        .into(holder.binding.coverImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return uriArrayList.isEmpty() ? urlArrayList.size() : uriArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageItemBinding binding;

        public ViewHolder(ImageItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
