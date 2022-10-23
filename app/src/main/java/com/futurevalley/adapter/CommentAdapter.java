package com.futurevalley.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.futurevalley.item.ItemComment;
import com.futurevalley.futurestudio.R;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ItemRowHolder> {

    private ArrayList<ItemComment> dataList;
    private Context mContext;

    public CommentAdapter(Context context, ArrayList<ItemComment> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comment_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemRowHolder holder, final int position) {
        final ItemComment singleItem = dataList.get(position);
        holder.textUserName.setText(singleItem.getUserName());
        holder.textComment.setText(singleItem.getCommentText());
        holder.textCommentDate.setText(singleItem.getCommentDate());

    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView textUserName, textComment, textCommentDate;

        ItemRowHolder(View itemView) {
            super(itemView);
            textUserName = itemView.findViewById(R.id.textView_userName_comment_adapter);
            textComment = itemView.findViewById(R.id.textView_comment_adapter);
            textCommentDate = itemView.findViewById(R.id.textView_date_comment_adapter);
        }
    }
}
