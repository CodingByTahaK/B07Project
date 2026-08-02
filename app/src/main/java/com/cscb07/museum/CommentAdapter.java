package com.cscb07.museum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    private boolean adminUser;
    private String artifactID;

    public CommentAdapter(List<Comment> commentList, boolean adminUser, String artifactID) {
        this.commentList = commentList;
        this.adminUser = adminUser;
        this.artifactID = artifactID;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_comment_adapter, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.textViewUsername.setText(comment.getUsername());
        holder.textViewComment.setText(comment.getComment());

        holder.itemView.setOnLongClickListener(null);
        holder.buttonEditComment.setOnClickListener(null);
        holder.buttonDeleteComment.setOnClickListener(null);

        // Show edit/delete button upon long press ONLY for admin users
        holder.buttonEditComment.setVisibility(View.GONE);
        holder.buttonDeleteComment.setVisibility(View.GONE);
        if (adminUser == true) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    holder.buttonEditComment.setVisibility(View.VISIBLE);
                    holder.buttonDeleteComment.setVisibility(View.VISIBLE);
                    return true;
                }
            });

            // Delete comment upon long press on a comment and confirmation if we want to delete or not
            holder.buttonDeleteComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Delete Comment");
                    builder.setMessage("Confirm whether you want to delete this comment");
                    
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance()
                                .getReference("artifacts")
                                .child(artifactID)
                                .child("comments")
                                .child(comment.getCommentID())
                                .removeValue();
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            holder.buttonEditComment.setVisibility(View.GONE);
                            holder.buttonDeleteComment.setVisibility(View.GONE);
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });

            // Edit comment upon long press on a comment and confirmation if we want to edit or not
            holder.buttonEditComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Edit Comment");
                    
                    EditText editText = new EditText(view.getContext());
                    editText.setText(comment.getComment());
                    builder.setView(editText);

                    builder.setPositiveButton("Save Changes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String updatedComment = editText.getText().toString();

                            FirebaseDatabase.getInstance()
                                .getReference("artifacts")
                                .child(artifactID)
                                .child("comments")
                                .child(comment.getCommentID())
                                .child("comment")
                                .setValue(updatedComment);
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            holder.buttonEditComment.setVisibility(View.GONE);
                            holder.buttonDeleteComment.setVisibility(View.GONE);
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });

        } else {
            holder.itemView.setOnLongClickListener(null);
            holder.buttonEditComment.setVisibility(View.GONE);
            holder.buttonDeleteComment.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        TextView textViewComment;
        Button buttonEditComment;
        Button buttonDeleteComment;

        public CommentViewHolder(@NonNull View commentView) {
            super(commentView);
            textViewUsername = commentView.findViewById(R.id.textViewUsername);
            textViewComment = commentView.findViewById(R.id.textViewComment);
            buttonEditComment = commentView.findViewById(R.id.buttonEditComment);
            buttonDeleteComment = commentView.findViewById(R.id.buttonDeleteComment);
        }
    }
}
