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

/**
 * An adapter that displays comments in a RecyclerView format
 * Allows admin users to edit or delete comments after comment 
 * data is connected to each item view
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    private boolean adminUser;
    private String artifactID;

    /**
     * Creates a new CommentAdapter with the specified fields
     * @param commentList - a lsit of comments to display
     * @param adminUser - true if the user is an admin and otherwise is false
     * @param artifactID - the unique ID of artifact which comments are displayed under
     */
    public CommentAdapter(List<Comment> commentList, boolean adminUser, String artifactID) {
        this.commentList = commentList;
        this.adminUser = adminUser;
        this.artifactID = artifactID;
    }

    /**
     * Creates a ViewHolder for each comment
     * @param parent - the parent ViewGroup
     * @param viewType - the type of the new view
     * @return a new instance of CommentViewHolder
     */
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_comment_adapter, parent, false);
        return new CommentViewHolder(view);
    }

    /**
     * Connects comment data to the specific ViewHolder
     * Displays username and the text of the comment
     * Allows admin users to edit and delete comments
     * @param holder - the ViewHolder that will be updated
     * @param position - the position of the comment regarding the list
     */
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

    /**
     * Returns the number of comments
     * @return the number of comments
     */
    @Override
    public int getItemCount() {
        return commentList.size();
    }

    /**
     * ViewHolder for each comment in the RecyclerView
     * It has references to the buttons
     */
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        TextView textViewComment;
        Button buttonEditComment;
        Button buttonDeleteComment;

        /**
        * Creates a ViewHolder for each comment
        * @param commentView - the view that represents each comment
        */
        public CommentViewHolder(@NonNull View commentView) {
            super(commentView);
            textViewUsername = commentView.findViewById(R.id.textViewUsername);
            textViewComment = commentView.findViewById(R.id.textViewComment);
            buttonEditComment = commentView.findViewById(R.id.buttonEditComment);
            buttonDeleteComment = commentView.findViewById(R.id.buttonDeleteComment);
        }
    }
}
