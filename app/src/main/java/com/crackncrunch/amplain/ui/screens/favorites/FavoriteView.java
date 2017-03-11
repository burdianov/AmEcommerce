package com.crackncrunch.amplain.ui.screens.favorites;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.mvp.views.AbstractView;
import com.crackncrunch.amplain.mvp.views.IFavoriteView;

import butterknife.BindView;

/**
 * Created by Lilian on 03-Mar-17.
 */

public class FavoriteView
        extends AbstractView<FavoriteScreen.FavoritePresenter>
        implements IFavoriteView {

    private final int COLUMN_COUNT = 2;

    @BindView(R.id.favorite_list)
    RecyclerView mFavoriteList;

    private FavoriteAdapter mAdapter = new FavoriteAdapter(mPresenter);

    public FavoriteView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<FavoriteScreen.Component>getDaggerComponent(context)
                .inject(this);
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    public FavoriteAdapter getAdapter() {
        return mAdapter;
    }

    public void initView() {
        GridLayoutManager glm = new GridLayoutManager(getContext(), COLUMN_COUNT);
        mFavoriteList.setLayoutManager(glm);
        mFavoriteList.setAdapter(mAdapter);
    }
}
