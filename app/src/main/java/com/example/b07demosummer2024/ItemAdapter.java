package com.example.b07demosummer2024;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<Item> itemList;
    private LikeClick likeClickListener;
    public interface LikeClick {
        void onLikeClick(Item item, int position);
    }
    public ItemAdapter(List<Item> itemList, LikeClick likeClickListener){
        this.itemList = itemList;
        this.likeClickListener=likeClickListener;
    }

    public ItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_adapater, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Item item = itemList.get(position);
        holder.textViewTitle.setText(item.getTitle());
        holder.textViewAuthor.setText(item.getAuthor());
        holder.textViewGenre.setText(item.getGenre());
        holder.textViewDescription.setText(item.getDescription());
        likebutton(holder, item);

        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean newLikeState = !item.isLiked();
                item.setLiked(newLikeState);

                int newCount;
                if (newLikeState) {
                    newCount = item.getLikeCount() + 1;
                } else {
                    newCount = item.getLikeCount() - 1;
                }
                item.setLikeCount(newCount);
                likebutton(holder, item);
                if (likeClickListener != null) {
                    likeClickListener.onLikeClick(item, position);
                }
            }
        });

    }

    private void likebutton(ItemViewHolder holder, Item item) {
        if (item.isLiked()) {
            holder.btnLike.setImageResource(R.drawable.ic_heart_filled);
        } else {
            holder.btnLike.setImageResource(R.drawable.ic_heart_outline);
        }
        holder.tvLikeCount.setText(String.valueOf(item.getLikeCount()));
    }

    public void updateItems(List<Item> newItems) {
        this.itemList = newItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewAuthor, textViewGenre, textViewDescription;
        ImageButton btnLike;
        TextView tvLikeCount;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewGenre = itemView.findViewById(R.id.textViewGenre);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            btnLike = itemView.findViewById(R.id.btnLike);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
        }
    }

}
