package com.crackncrunch.amplain.ui.screens.product_details;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRatingBar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.storage.realm.CommentRealm;
import com.crackncrunch.amplain.data.storage.realm.ProductRealm;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.mvp.views.AbstractView;

import butterknife.BindView;

/**
 * Created by Lilian on 25-Feb-17.
 */

public class DetailView extends AbstractView<DetailScreen.DetailPresenter> {

    @BindView(R.id.detail_pager)
    ViewPager mViewPager;

    public DetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<DetailScreen.Component>getDaggerComponent(context)
            .inject(this);
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    public void initView(ProductRealm product) {
        DetailAdapter adapter = new DetailAdapter(product);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mPresenter.initFab(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void showAddCommentDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View dialogView = inflater.inflate(R.layout.dialog_comment, null);

        AppCompatRatingBar ratingBar = (AppCompatRatingBar) dialogView.findViewById(R.id.comment_rb);
        EditText commentEt = (EditText) dialogView.findViewById(R.id.comment_et);

        dialogBuilder.setTitle("Comment the product?")
                .setView(dialogView)
                .setPositiveButton("Comment the product", (dialog, which) -> {
                    CommentRealm comment = new CommentRealm(ratingBar.getRating(),
                            commentEt.getText().toString());
                    mPresenter.addComment(comment);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
    }
}
