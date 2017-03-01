package com.crackncrunch.amplain.ui.screens.product_details.comments;

import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.storage.dto.CommentDto;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.utils.ConstantsManager;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter
        .CommentsViewHolder> {

    private List<CommentDto> mCommentsList = new ArrayList<>();

    @Inject
    Picasso mPicasso;

    public void addItem(CommentDto commentDto) {
        mCommentsList.add(commentDto);
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        DaggerService.<CommentsScreen.Component>getDaggerComponent(recyclerView
                .getContext()).inject(this);
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentsViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(CommentsViewHolder holder, int position) {
        CommentDto comment = mCommentsList.get(position);
        holder.userNameTxt.setText(comment.getUserName());
        try {
            holder.dateTxt.setText(elapsedTime(comment.getCommentDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.rating.setRating(comment.getRating());
        holder.commentTxt.setText(comment.getComment());
        String urlAvatar = comment.getAvatarUrl();
        if (urlAvatar == null || urlAvatar.isEmpty()) {
            urlAvatar = "http://skill-branch.ru/img/avatar-1.png";
        }

        mPicasso.load(urlAvatar)
                .error(R.drawable.ic_account_circle_black_24dp)
                .fit()
                .into(holder.commentAvatarImg);
    }

    @Override
    public int getItemCount() {
        return mCommentsList.size();
    }

    private String elapsedTime(String dateString) throws ParseException {
        SimpleDateFormat timeFormat = new SimpleDateFormat(
                ConstantsManager.SERVER_DATE_FORMAT, Locale.US);
        Date commentDate = timeFormat.parse(dateString);
        long commentTime = commentDate.getTime();

        return DateUtils.getRelativeTimeSpanString(commentTime).toString();
    }

    public void reloadAdapter(List<CommentDto> commentDtos) {
        mCommentsList.clear();
        mCommentsList = commentDtos;
        notifyDataSetChanged();
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.comment_avatar_img)
        ImageView commentAvatarImg;
        @BindView(R.id.user_name_txt)
        TextView userNameTxt;
        @BindView(R.id.date_txt)
        TextView dateTxt;
        @BindView(R.id.comment_txt)
        TextView commentTxt;
        @BindView(R.id.rating)
        AppCompatRatingBar rating;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
