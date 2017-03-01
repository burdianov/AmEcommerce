package com.crackncrunch.amplain.ui.screens.account;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.storage.dto.UserInfoDto;
import com.crackncrunch.amplain.data.storage.dto.UserSettingsDto;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.mvp.views.AbstractView;
import com.crackncrunch.amplain.mvp.views.IAccountView;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import flow.Flow;

public class AccountView extends AbstractView<AccountScreen.AccountPresenter>
        implements IAccountView {

    public static final int PREVIEW_STATE = 1;
    public static final int EDIT_STATE = 0;

    @Inject
    AccountScreen.AccountPresenter mPresenter;
    @Inject
    Picasso mPicasso;

    @BindView(R.id.profile_name_txt)
    TextView mProfileNameTxt;
    @BindView(R.id.user_avatar_img)
    ImageView mUserAvatarImg;
    @BindView(R.id.user_phone_et)
    EditText mUserPhoneEt;
    @BindView(R.id.user_full_name_et)
    EditText mUserFullNameEt;
    @BindView(R.id.profile_name_wrapper)
    LinearLayout mProfileNameWrapper;
    @BindView(R.id.address_list)
    RecyclerView mAddressList;
    @BindView(R.id.add_address_btn)
    Button mAddAddressBtn;
    @BindView(R.id.notification_order_sw)
    SwitchCompat mNotificationOrderSw;
    @BindView(R.id.notification_promo_sw)
    SwitchCompat mNotificationPromoSw;

    private AccountScreen mScreen;
    private TextWatcher mWatcher;
    private AddressesAdapter mAdapter;
    private Uri mAvatarUri;

    public AccountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            mScreen = Flow.getKey(this);
            DaggerService.<AccountScreen.Component>getDaggerComponent(context).inject
                    (this);
        }
    }

    @Override
    protected void initDagger(Context context) {
        // empty
    }

    public AddressesAdapter getAdapter() {
        return mAdapter;
    }

    //region ==================== Flow view lifecycle callbacks ===================

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    private void showViewFromState() {
        if (mScreen.getCustomState() == PREVIEW_STATE) {
            showPreviewState();
        } else {
            showEditState();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            mPresenter.takeView(this);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!isInEditMode()) {
            mPresenter.dropView(this);
        }
    }

    //endregion

    public void initView() {
        showViewFromState();

        mAdapter = new AddressesAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mAddressList.setLayoutManager(layoutManager);
        mAddressList.setAdapter(mAdapter);
        mAddressList.setVisibility(VISIBLE);

        initSwipe();
    }

    protected void initSettings(UserSettingsDto settings) {
        CompoundButton.OnCheckedChangeListener listener =
                (buttonView, isChecked) -> mPresenter.switchSettings();

        mNotificationOrderSw.setChecked(settings.isOrderNotification());
        mNotificationPromoSw.setChecked(settings.isPromoNotification());
        mNotificationOrderSw.setOnCheckedChangeListener(listener);
        mNotificationPromoSw.setOnCheckedChangeListener(listener);
    }

    private void initSwipe() {
        ItemSwipeCallback swipeCallback = new ItemSwipeCallback(getContext(), 0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    showRemoveAddressDialog(position);
                } else {
                    showEditAddressDialog(position);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(mAddressList);
    }

    private void showEditAddressDialog(int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle(R.string.edit_address)
                .setMessage(R.string.edit_address_question)
                .setPositiveButton(R.string.edit, (dialogInterface, i) -> mPresenter.editAddress(position))
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel())
                .setOnCancelListener(dialogInterface -> mAdapter.notifyDataSetChanged())
                .show();
    }

    private void showRemoveAddressDialog(int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle(R.string.delete_address)
                .setMessage(R.string.delete_address_question)
                .setPositiveButton(R.string.delete, (dialogInterface, i) -> mPresenter
                        .removeAddress(position))
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel())
                .setOnCancelListener(dialogInterface -> mAdapter.notifyDataSetChanged())
                .show();
    }

    //region ==================== IAccountView ===================

    @Override
    public void changeState() {
        if (mScreen.getCustomState() == PREVIEW_STATE) {
            mScreen.setCustomState(EDIT_STATE);
        } else {
            mScreen.setCustomState(PREVIEW_STATE);
        }
        showViewFromState();
    }

    @Override
    public void showEditState() {
        mWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mProfileNameTxt.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        mProfileNameWrapper.setVisibility(VISIBLE);
        mUserFullNameEt.addTextChangedListener(mWatcher);
        mUserPhoneEt.setEnabled(true);
        mPicasso.load(R.drawable.ic_add_white_24dp)
                .error(R.drawable.ic_add_white_24dp)
                .into(mUserAvatarImg);
    }

    @Override
    public void showPreviewState() {
        mProfileNameWrapper.setVisibility(GONE);
        mUserPhoneEt.setEnabled(false);
        mUserFullNameEt.removeTextChangedListener(mWatcher);
        if (mAvatarUri != null) {
            insertAvatar();
        }
    }

    @Override
    public void showPhotoSourceDialog() {
        String source[] = {"Upload from gallery", "Take a picture", "Cancel"};
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Place photo");
        alertDialog.setItems(source, (dialogInterface, i) -> {
            switch (i) {
                case 0:
                    mPresenter.chooseGallery();
                    break;
                case 1:
                    mPresenter.chooseCamera();
                    break;
                case 2:
                    dialogInterface.cancel();
                    break;
            }
        });
        alertDialog.show();
    }

    @Override
    public String getUserName() {
        return String.valueOf(mUserFullNameEt.getText());
    }

    @Override
    public String getUserPhone() {
        return String.valueOf(mUserPhoneEt.getText());
    }

    @Override
    public boolean viewOnBackPressed() {
        if (mScreen.getCustomState() == EDIT_STATE) {
            mPresenter.switchViewState();
            return true;
        } else {
            return false;
        }
    }

    public UserSettingsDto getSettings() {
        return new UserSettingsDto(mNotificationOrderSw.isChecked(),
                mNotificationPromoSw.isChecked());
    }

    public void updateAvatarPhoto(Uri uri) {
        mAvatarUri = uri;
        insertAvatar();
    }

    private void insertAvatar() {
        mPicasso.load(mAvatarUri)
                .resize(140, 140)
                .centerCrop()
                .into(mUserAvatarImg);
    }

    public UserInfoDto getUserProfileInfo() {
        return new UserInfoDto(mUserFullNameEt.getText().toString(),
                mUserPhoneEt.getText().toString(), String.valueOf(mAvatarUri));
    }

    public void updateProfileInfo(UserInfoDto userInfoDto) {
        mProfileNameTxt.setText(userInfoDto.getName());
        mUserFullNameEt.setText(userInfoDto.getName());
        mUserPhoneEt.setText(userInfoDto.getPhone());
        if (mScreen.getCustomState() == PREVIEW_STATE) {
            mAvatarUri = Uri.parse(userInfoDto.getAvatar());
            insertAvatar();
        }
    }

    //endregion

    //region ==================== Events ===================

    @OnClick(R.id.add_address_btn)
    void clickAddAddress() {
        mPresenter.onClickAddress();
    }

    @OnClick(R.id.user_avatar_img)
    void clickUserAvatar() {
        if (mScreen.getCustomState() == EDIT_STATE) {
            mPresenter.takePhoto();
        }
    }

    //endregion
}
