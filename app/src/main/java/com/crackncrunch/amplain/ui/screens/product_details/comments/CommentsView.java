package com.crackncrunch.amplain.ui.screens.product_details.comments;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.mvp.views.AbstractView;

import butterknife.BindView;

/**
 * Created by Lilian on 25-Feb-17.
 */
public class CommentsView extends AbstractView<CommentsScreen.CommentsPresenter> {

    @BindView(R.id.comments_list)
    RecyclerView mCommentsList;

    private CommentsAdapter mAdapter = new CommentsAdapter();

    public CommentsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<CommentsScreen.Component>getDaggerComponent(context).inject
                (this);
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    public CommentsAdapter getAdapter() {
        return mAdapter;
    }

    public void initView() {
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        mCommentsList.setLayoutManager(llm);
        mCommentsList.setAdapter(mAdapter);
    }
}
