package com.crackncrunch.amplain.ui.screens.favorites;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.AttributeSet;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.storage.realm.ProductRealm;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.mvp.views.AbstractView;
import com.crackncrunch.amplain.mvp.views.IFavoriteView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

/**
 * Created by Lilian on 03-Mar-17.
 */

public class FavoriteView
        extends AbstractView<FavoriteScreen.FavoritePresenter>
        implements IFavoriteView {

    private final int COLUMN_COUNT = 2;

    @BindView(R.id.favorite_list)
    RecyclerView mFavoriteList;

    private Context mContext;
    private FavoriteAdapter mAdapter;

    public FavoriteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public Context getLocalContext() {
        return mContext;
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

    FavoriteAdapter.FavoriteViewHolder.OnClickListener listener = new FavoriteAdapter.FavoriteViewHolder.OnClickListener() {
        @Override
        public void onImageClick(ProductRealm product) {
            mPresenter.clickOnProduct(product);
        }

        @Override
        public void onFavoriteClick(ProductRealm product) {
            mPresenter.onFavoriteClick(product);
        }

        @Override
        public void onToCartClick(ProductRealm product) {
            mPresenter.onCartClick(product);
        }
    };

    public void  showFavoriteList(RealmResults<ProductRealm> favoriteList) {
        mAdapter = new FavoriteAdapter(getLocalContext(), favoriteList, listener);
        GridLayoutManager layoutManager = new GridLayoutManager(getLocalContext(), COLUMN_COUNT);
        mFavoriteList.setLayoutManager(layoutManager);
        mFavoriteList.setAdapter(mAdapter);
    }

    public void showOnRemoveFromFavoriteDialog(ProductRealm product) {
        String unformattedMessage = getContext()
                .getString(R.string.favorite_removing_dialog_message,
                        product.getProductName());

        String formattedMessage = String.valueOf(Html.fromHtml(unformattedMessage));

        new AlertDialog.Builder(getContext())
                .setTitle(getContext().getString(R.string.favorite_removing_dialog_title))
                .setMessage(formattedMessage)
                .setCancelable(false)
                .setPositiveButton(getContext().getString(R.string.favorite_removing_dialog_yes),
                        (dialogInterface, i) -> mPresenter.deleteProductFromFavorites(product))
                .setNegativeButton(getContext().getString(R.string.favorite_removing_dialog_no), null)
                .show();
    }
}
