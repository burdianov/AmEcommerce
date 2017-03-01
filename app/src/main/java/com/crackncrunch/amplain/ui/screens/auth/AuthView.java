package com.crackncrunch.amplain.ui.screens.auth;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.mvp.views.IAuthView;
import com.crackncrunch.amplain.utils.ViewHelper;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import flow.Flow;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.crackncrunch.amplain.utils.ConstantsManager.CUSTOM_FONTS_ROOT;
import static com.crackncrunch.amplain.utils.ConstantsManager.CUSTOM_FONT_NAME;

/**
 * Created by Lilian on 21-Feb-17.
 */

public class AuthView extends RelativeLayout implements IAuthView {

    public static final int LOGIN_STATE = 0;
    public static final int IDLE_STATE = 1;

    @BindView(R.id.app_name_txt)
    TextView mAppNameTxt;
    @BindView(R.id.auth_card)
    CardView mAuthCard;
    @BindView(R.id.show_catalog_btn)
    Button mShowCatalogBtn;
    @BindView(R.id.login_btn)
    Button mLoginBtn;
    @BindView(R.id.login_email_et)
    EditText mEmailEt;
    @BindView(R.id.login_password_et)
    EditText mPasswordEt;
    @BindView(R.id.panel_wrapper)
    FrameLayout mPanelWrapper;
    @BindView(R.id.logo_img)
    ImageView mLogo;

    @Inject
    AuthScreen.AuthPresenter mPresenter;

    private final ChangeBounds mBounds;
    private final Fade mFade;
    private final Animator mScaleAnimator;

    private AuthScreen mScreen;
    private float mDen;
    private Subscription mAnimObs;

    public AuthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            mScreen = Flow.getKey(this);
            DaggerService.<AuthScreen.Component>getDaggerComponent(context)
                    .inject(this);
        }

        mBounds = new ChangeBounds();
        mFade = new Fade();
        mDen = ViewHelper.getDensity(context);

        mScaleAnimator = AnimatorInflater.loadAnimator(getContext(), R
                .animator.logo_scale_animator);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);

        if (!isInEditMode()) {
            showViewFromState();
            startLogoAnim();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            mPresenter.takeView(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!isInEditMode()) {
            mPresenter.dropView(this);
            mAnimObs.unsubscribe();
        }
    }

    private void showViewFromState() {
        if (!isIdle()) {
            showLoginState();
        } else {
            showIdleState();
        }
    }

    private void showLoginState() {
        CardView.LayoutParams cardParam = (CardView.LayoutParams)
                mAuthCard.getLayoutParams(); // получаем текущие параметры макета
        cardParam.height = LinearLayout.LayoutParams.MATCH_PARENT; // устанавливаем высоту на высоту родителя
        mAuthCard.setLayoutParams(cardParam); // устанавливаем параметры (requestLayout inside)
        mAuthCard.getChildAt(0).setVisibility(VISIBLE); // input wrapper делаем видимым
        mAuthCard.setCardElevation(4 * mDen); // устанавливаем подъем карточки авторизации
        mShowCatalogBtn.setClickable(false); // отключаем кликабельность кнопки входа в каталог
        mShowCatalogBtn.setVisibility(GONE); // скрываем кнопку
        mScreen.setCustomState(LOGIN_STATE); // устанавливаем стейт LOGIN
    }

    private void showIdleState() {
        CardView.LayoutParams cardParam = (CardView.LayoutParams)
                mAuthCard.getLayoutParams();
        cardParam.height = ((int) (44 * mDen));
        mAuthCard.setLayoutParams(cardParam);
        mAuthCard.getChildAt(0).setVisibility(INVISIBLE);
        mAuthCard.setCardElevation(0f);
        mShowCatalogBtn.setClickable(true);
        mShowCatalogBtn.setVisibility(VISIBLE);
        mScreen.setCustomState(IDLE_STATE);
    }

    //region ==================== Events ===================

    @OnClick(R.id.login_btn)
    void loginClick() {
        mPresenter.clickOnLogin();
    }

    @OnClick(R.id.fb_social_btn)
    void fbClick() {
        mPresenter.clickOnFb();
    }

    @OnClick(R.id.twitter_social_btn)
    void twitterClick() {
        mPresenter.clickOnTwitter();
    }

    @OnClick(R.id.vk_social_btn)
    void vkClick() {
        mPresenter.clickOnVk();
    }

    @OnClick(R.id.show_catalog_btn)
    void catalogClick() {
        mPresenter.clickOnShowCatalog();
    }

    //endregion

    //region ==================== IAuthView ===================

    @Override
    public void setTypeface() {
        mAppNameTxt.setTypeface(Typeface.createFromAsset(getContext().getAssets(),
                CUSTOM_FONTS_ROOT + CUSTOM_FONT_NAME));
    }

    @Override
    public void showLoginBtn() {
        mLoginBtn.setVisibility(VISIBLE);
    }

    @Override
    public void hideLoginBtn() {
        mLoginBtn.setVisibility(GONE);
    }

    @Override
    public String getUserEmail() {
        return String.valueOf(mEmailEt.getText());
    }

    @Override
    public String getUserPassword() {
        return String.valueOf(mPasswordEt.getText());
    }

    @Override
    public boolean isIdle() {
        return mScreen.getCustomState() == IDLE_STATE;
    }

    @Override
    public boolean viewOnBackPressed() {
        if (!isIdle()) {
            showIdleWithAnim();
            return true;
        } else {
            return false;
        }
    }

    //endregion

    //region ==================== Animation ===================

    private void invalidLoginAnimation() {
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(
                getContext(), R.animator.invalid_field_animator);
        set.setTarget(mAuthCard);
        set.start();
    }

    public void showLoginWithAnim() {
        TransitionSet set = new TransitionSet();
        set.addTransition(mBounds) // анимируем положение и границы (высоту элемента и подъем)
                .addTransition(mFade) // анимируем прозрачность (видимость элементов)
                //.setDuration(300) // продолжительность анимации
                .setInterpolator(new FastOutSlowInInterpolator()) // устанавливаем временную функцию
                .setOrdering(TransitionSet.ORDERING_SEQUENTIAL); // устанавливаем последовательность проигрывания анимаций при переходе
        TransitionManager.beginDelayedTransition(mPanelWrapper, set);
        showLoginState();
    }

    private void showIdleWithAnim() {
        TransitionSet set = new TransitionSet();
        Transition fade = new Fade();
        fade.addTarget(mAuthCard.getChildAt(0)); // анимация исчезновения для инпутов
        set.addTransition(fade)
                .addTransition(mBounds) // анимируем положение и границы (высоту элемента и подъем)
                .addTransition(mFade) // анимируем прозрачность (видимость элементов)
                //.setDuration(5000) // продолжительность анимации
                .setInterpolator(new FastOutSlowInInterpolator()) // устанавливаем временную функцию
                .setOrdering(TransitionSet.ORDERING_SEQUENTIAL); // устанавливаем последовательность проигрывания анимаций при переходе
        TransitionManager.beginDelayedTransition(mPanelWrapper, set);
        showIdleState();
    }

    private void startLogoAnim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AnimatedVectorDrawable avd = (AnimatedVectorDrawable) mLogo.getDrawable();
            mScaleAnimator.setTarget(mLogo);

            mAnimObs = Observable.interval(3000, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        mScaleAnimator.start();
                        avd.start();
                    });
            avd.start();
        }
    }

    //endregion
}
