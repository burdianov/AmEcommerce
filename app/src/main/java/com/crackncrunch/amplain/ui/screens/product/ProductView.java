package com.crackncrunch.amplain.ui.screens.product;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.storage.dto.ProductDto;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.mvp.views.AbstractView;
import com.crackncrunch.amplain.mvp.views.IProductView;
import com.crackncrunch.amplain.utils.ViewHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.ChangeImageTransform;
import com.transitionseverywhere.Explode;
import com.transitionseverywhere.SidePropagation;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class ProductView extends AbstractView<ProductScreen.ProductPresenter> implements
        IProductView {

    public static final String TAG = "ProductView";

    @BindView(R.id.product_name_txt)
    TextView mProductNameTxt;
    @BindView(R.id.product_description_txt)
    TextView mProductDescriptionTxt;
    @BindView(R.id.product_image)
    ImageView mProductImage;
    @BindView(R.id.product_count_txt)
    TextView mProductCountTxt;
    @BindView(R.id.product_price_txt)
    TextView mProductPriceTxt;
    @BindView(R.id.favorite_btn)
    CheckBox mFavoriteBtn;
    @BindView(R.id.product_wrapper)
    LinearLayout mProductWrapper;
    @BindView(R.id.product_card)
    CardView mProductCard;

    @Inject
    Picasso mPicasso;

    private AnimatorSet mResultSet;
    private ArrayList<View> mChildrenList;
    private boolean mIsZoomed;
    private int mInitImageHeight;
    private float mDen;

    public ProductView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<ProductScreen.Component>getDaggerComponent(context)
                .inject(this);
        mDen = ViewHelper.getDensity(context);
    }

    //region ==================== IProductView ===================

    @Override
    public void showProductView(final ProductDto product) {
        mProductNameTxt.setText(product.getProductName());
        mProductDescriptionTxt.setText(product.getDescription());
        mProductCountTxt.setText(String.valueOf(product.getCount()));
        if (product.getCount() > 0) {
            mProductPriceTxt.setText(String.valueOf(product.getCount() *
                    product.getPrice() + ".-"));
        } else {
            mProductPriceTxt.setText(String.valueOf(product.getPrice() + ".-"));
        }
        mFavoriteBtn.setChecked(product.getFavorite());

        mPicasso.load(product.getImageUrl())
                .networkPolicy(NetworkPolicy.OFFLINE)
                /*.fit()
                .centerCrop()*/
                .placeholder(R.drawable.placeholder)
                .into(mProductImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        mPicasso.load(product.getImageUrl())
                                /*.fit()
                                .centerCrop()*/
                                .placeholder(R.drawable.placeholder)
                                .into(mProductImage);
                    }
                });
    }

    @Override
    public void updateProductCountView(ProductDto product) {
        mProductCountTxt.setText(String.valueOf(product.getCount()));
        if (product.getCount() >= 0) {
            mProductPriceTxt.setText(String.valueOf(product.getCount() * product
                    .getPrice() + ".-"));
        }
    }

    @Override
    public boolean viewOnBackPressed() {
        if (mIsZoomed) {
            startZoomTransition();
            return true;
        }
        return false;
    }

    //endregion

    //region ==================== Events ===================

    @OnClick(R.id.plus_btn)
    void clickPlus() {
        mPresenter.clickOnPlus();
    }

    @OnClick(R.id.minus_btn)
    void clickMinus() {
        mPresenter.clickOnMinus();
    }

    @OnClick(R.id.favorite_btn)
    void clickOnFavorite() {
        mPresenter.clickFavorite();
    }

    @OnClick(R.id.show_more_btn)
    void clickOnShowMore() {
        mPresenter.clickShowMore();
    }

    @OnClick(R.id.product_image)
    void zoomImage() {
        startZoomTransition();
    }

    //endregion

    //region ==================== Animation ===================

    public void startAddToCartAnim() {
        final int cx = (mProductWrapper.getLeft() + mProductWrapper.getRight()) / 2;  // вычисляем центр карточки по X
        final int cy = (mProductWrapper.getTop() + mProductWrapper.getBottom()) / 2; // вычисляем центр карточки по Y
        final int radius = Math.max(mProductWrapper.getWidth(),
                mProductWrapper.getHeight());  // вычисляем радиус (максимальное значение высоты или ширины карточки)

        final Animator hideCircleAnim;
        final Animator showCircleAnim;
        Animator hideColorAnim = null;
        Animator showColorAnim = null;

        // VERY IMPORTANT!!! Reveal Animation must always be re-created!!!
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            hideCircleAnim = ViewAnimationUtils.createCircularReveal
                    (mProductWrapper, cx, cy, radius, 0);  // создаем анимацию  для объекта карточка, из центра, с максимального радиуса до 0
            hideCircleAnim.addListener(new AnimatorListenerAdapter() { // вешаем слушатель на на окончание анимации, когда анимация  заканчивается, делаем карточку невидимой
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mProductWrapper.setVisibility(INVISIBLE);
                }
            });

            showCircleAnim = ViewAnimationUtils.createCircularReveal
                    (mProductWrapper, cx, cy, 0, radius);
            showCircleAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationEnd(animation);
                    mProductWrapper.setVisibility(VISIBLE);
                }
            });

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                ColorDrawable cdr = ((ColorDrawable) mProductWrapper.getForeground());
                hideColorAnim = ObjectAnimator.ofArgb(cdr, "color",
                        getResources().getColor(R.color.colorAccent, null));
                showColorAnim = ObjectAnimator.ofArgb(cdr, "color",
                        getResources().getColor(R.color.transparent, null));
            }
        } else {
            // TODO: 27-Feb-17 add a prettier animation for the old devices
            hideCircleAnim = ObjectAnimator.ofFloat(mProductWrapper, "alpha", 0);
            showCircleAnim = ObjectAnimator.ofFloat(mProductWrapper, "alpha", 1);
        }
        AnimatorSet hideSet = new AnimatorSet();
        AnimatorSet showSet = new AnimatorSet();

        addAnimatorTogetherInSet(hideSet, hideCircleAnim, hideColorAnim); // добавляем аниматоры в сет (если они не null)
        addAnimatorTogetherInSet(showSet, showCircleAnim, showColorAnim);
        hideSet.setDuration(600); // устанавливаем длительность анимации
        hideSet.setInterpolator(new FastOutSlowInInterpolator()); // устанавливаем временную функцию (интерполятор) анимации

        showSet.setStartDelay(1000); // устанавливаем задержку анимации появления
        showSet.setDuration(600);
        showSet.setInterpolator(new FastOutSlowInInterpolator());

        if ((mResultSet != null && !mResultSet.isStarted()) || mResultSet == null) {
            mResultSet = new AnimatorSet();
            mResultSet.playSequentially(hideSet, showSet); // результирующий сет последовательно проигрывает анимации скрытия и появления
            mResultSet.start();
        }
    }

    private void addAnimatorTogetherInSet(AnimatorSet set, Animator... anims) {
        ArrayList<Animator> animatorList = new ArrayList<>();
        for (Animator animator : anims) {
            if (animator != null) {
                animatorList.add(animator);
            }
        }
        set.playTogether(animatorList);
    }

    private void startZoomTransition() {
        TransitionSet set = new TransitionSet();
        Transition explode = new Explode(); // анимация исчезновения от эпицентра (почти как Slide но от заданного эпицентра)

        final Rect rect = new Rect(mProductImage.getLeft(), mProductImage
                .getTop(), mProductImage.getRight(), mProductImage.getBottom());

        explode.setEpicenterCallback(new Transition.EpicenterCallback() {
            @Override
            public Rect onGetEpicenter(Transition transition) {
                return rect;
            }
        });

        SidePropagation propagation = new SidePropagation();  // чем удаленнее объект от эпицентра, тем позже начнется его анимация
        propagation.setPropagationSpeed(3f); // по умолчанию 3 - относительная задержка движения удаленных объектов относительно первого движущегося объекта
        explode.setPropagation(propagation);

        ChangeBounds changeBounds = new ChangeBounds();
        ChangeImageTransform imageTransform = new ChangeImageTransform();

        // TODO: 28-Feb-17 Clarify why the below lines affect the zooming
        /*if (!mIsZoomed) {
            changeBounds.setStartDelay(100);
            imageTransform.setStartDelay(100);
        }*/

        set.addTransition(explode)
                .addTransition(changeBounds)
                .addTransition(imageTransform)
                .setDuration(600)
                .setInterpolator(new FastOutSlowInInterpolator());

        TransitionManager.beginDelayedTransition(mProductCard, set);

        if (mChildrenList == null) {
            mChildrenList = ViewHelper.getChildrenExcludeView(mProductWrapper,
                    R.id.product_image);
        }
        ViewGroup.LayoutParams cardParam = mProductCard.getLayoutParams();
        cardParam.height = !mIsZoomed
                ? ViewGroup.LayoutParams.MATCH_PARENT
                : ViewGroup.LayoutParams.WRAP_CONTENT;
        mProductCard.setLayoutParams(cardParam);

        ViewGroup.LayoutParams wrapParam = mProductWrapper.getLayoutParams();
        wrapParam.height = !mIsZoomed
                ? ViewGroup.LayoutParams.MATCH_PARENT
                : ViewGroup.LayoutParams.WRAP_CONTENT;
        mProductWrapper.setLayoutParams(wrapParam);

        LinearLayout.LayoutParams imgParams;
        if (!mIsZoomed) {
            mInitImageHeight = mProductImage.getHeight();
            imgParams = new LinearLayout
                    .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mProductImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            imgParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, mInitImageHeight);
            int defMargin = ((int) (16 * mDen));
            imgParams.setMargins(defMargin, 0, defMargin, 0);
            mProductImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        mProductImage.setLayoutParams(imgParams);
        if (!mIsZoomed) {
            for (View view : mChildrenList) {
                view.setVisibility(GONE);
            }
        } else {
            for (View view : mChildrenList) {
                view.setVisibility(VISIBLE);
            }
        }
        mIsZoomed = !mIsZoomed;
    }

    //endregion
}