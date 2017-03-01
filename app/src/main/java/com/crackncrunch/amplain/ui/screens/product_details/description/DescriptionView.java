package com.crackncrunch.amplain.ui.screens.product_details.description;

import android.content.Context;
import android.support.v7.widget.AppCompatRatingBar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.storage.dto.DescriptionDto;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.mvp.views.AbstractView;

import butterknife.BindView;
import butterknife.OnClick;

public class DescriptionView extends AbstractView<DescriptionScreen.DescriptionPresenter> {
    @BindView(R.id.full_description_txt)
    TextView mFullDescriptionTxt;
    @BindView(R.id.product_count_txt)
    TextView mProductCountTxt;
    @BindView(R.id.product_price_txt)
    TextView mProductPriceTxt;
    @BindView(R.id.product_rating)
    AppCompatRatingBar mProductRating;

    public DescriptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<DescriptionScreen.Component>getDaggerComponent(context)
                .inject(this);
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    public void initView(DescriptionDto descriptionDto) {
        mFullDescriptionTxt.setText(descriptionDto.getDescription());
        mProductRating.setRating(descriptionDto.getRating());
        mProductCountTxt.setText(String.valueOf(descriptionDto.getCount()));

        if (descriptionDto.getCount() > 0) {
            mProductPriceTxt.setText(String.valueOf(descriptionDto.getCount() *
                    descriptionDto.getPrice() + ".-"));
        } else {
            mProductPriceTxt.setText(String.valueOf(descriptionDto.getPrice() + ".-"));
        }
    }

    //region ==================== Events ===================

    @OnClick(R.id.plus_btn)
    void clickOnPlus() {
        mPresenter.clickOnPlus();
    }

    @OnClick(R.id.minus_btn)
    void clickOnMinus() {
        mPresenter.clickOnMinus();
    }

    //endregion
}
