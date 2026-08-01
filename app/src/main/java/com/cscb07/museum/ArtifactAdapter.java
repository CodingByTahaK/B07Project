package com.cscb07.museum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ArtifactAdapter extends RecyclerView.Adapter<ArtifactAdapter.ArtifactViewHolder> {
    private List<Artifact> artifactList;
    Context context;
    private final RecyclerExpandedViewInterface recyclerExpandedViewInterface;

    public ArtifactAdapter(List<Artifact> artifactList, Context context, RecyclerExpandedViewInterface recyclerExpandedViewInterface) {
        this.context = context;
        this.artifactList = artifactList;
        this.recyclerExpandedViewInterface = recyclerExpandedViewInterface;
    }

    @NonNull
    @Override
    public ArtifactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_adapater, parent, false);
        return new ArtifactViewHolder(view, recyclerExpandedViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtifactViewHolder holder, int position) {
        Artifact artifact = artifactList.get(position);
        holder.textViewName.setText(artifact.getName());
        holder.textViewCategory.setText(artifact.getCategory());
        holder.textViewMaterial.setText(artifact.getMaterial());
        holder.textViewPeriod.setText(artifact.getPeriod());
        Picasso.get().load(artifact.getImage()).into(holder.imageViewPic);
    }

    @Override
    public int getItemCount() {
        return artifactList.size();
    }

    public static class ArtifactViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewCategory, textViewMaterial, textViewPeriod;
        ImageView imageViewPic;

        public ArtifactViewHolder(@NonNull View artifactView, RecyclerExpandedViewInterface recyclerExpandedViewInterface) {
            super(artifactView);
            textViewName = artifactView.findViewById(R.id.textViewName);
            textViewCategory = artifactView.findViewById(R.id.textViewCategory);
            textViewMaterial = artifactView.findViewById(R.id.textViewMaterial);
            textViewPeriod = artifactView.findViewById(R.id.textViewPeriod);
            imageViewPic = artifactView.findViewById(R.id.imageView);

            //attaching onClick listener to each artifact
            artifactView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerExpandedViewInterface != null){
                        int position  = getAdapterPosition();

                        if(position != RecyclerView.NO_POSITION){
                            recyclerExpandedViewInterface.onArtifactClick(position);
                        }
                    }
                }
            });
        }
    }
}
