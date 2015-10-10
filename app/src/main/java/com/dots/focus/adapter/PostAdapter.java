package com.dots.focus.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dots.focus.R;
import com.dots.focus.model.PostModel;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HanHailong on 15/9/7.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private List<PostModel> dataList = new ArrayList<>();

    /**
     * @param list
     */
    public void addAll(List<PostModel> list) {
        if (list == null) return;
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PostModel post = dataList.get(position);
        if (post == null) return;
        holder.usernameTv.setText(post.getUsername());
        holder.locationTv.setText(post.getLocation());
        holder.postTv.setText(post.getPost());
        holder.reasonTv.setText(post.getReason());
        holder.timeTv.setText(post.getAbsoluteTime());
        holder.likesBtn.setText(post.getLikes());
        holder.commentsBtn.setText(post.getComments());
        ImageLoader.getInstance().displayImage(post.getAvatar(), holder.avatarIv);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTv, locationTv, postTv, timeTv, reasonTv, likesBtn, commentsBtn;
        ImageView avatarIv;

        public ViewHolder(View itemView) {
            super(itemView);
            usernameTv = (TextView) itemView.findViewById(R.id.tv_username);
            locationTv = (TextView) itemView.findViewById(R.id.tv_location);
            timeTv = (TextView) itemView.findViewById(R.id.tv_time);
            reasonTv = (TextView) itemView.findViewById(R.id.tv_reason);
            likesBtn = (TextView) itemView.findViewById(R.id.btn_likes);
            commentsBtn = (TextView) itemView.findViewById(R.id.btn_comments);
            postTv = (TextView) itemView.findViewById(R.id.tv_post);
            avatarIv = (ImageView) itemView.findViewById(R.id.iv_avatar);
        }
    }
}
