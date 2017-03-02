package com.crackncrunch.amplain.ui.screens.address;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.EditText;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.storage.dto.UserAddressDto;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.mvp.views.AbstractView;
import com.crackncrunch.amplain.mvp.views.IAddressView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressView extends AbstractView<AddressScreen.AddressPresenter>
        implements IAddressView {

    @BindView(R.id.address_name_et)
    EditText mAddressNameEt;
    @BindView(R.id.street_et)
    EditText mStreetEt;
    @BindView(R.id.number_building_et)
    EditText mNumberBuildingEt;
    @BindView(R.id.number_apartment_et)
    EditText mNumberApartmentEt;
    @BindView(R.id.number_floor_et)
    EditText mNumberFloorEt;
    @BindView(R.id.comment_et)
    EditText mCommentEt;

    @Inject
    AddressScreen.AddressPresenter mPresenter;

    private String mAddressId;

    public AddressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            DaggerService.<AddressScreen.Component>getDaggerComponent(context)
                    .inject(this);
        }
    }

    @Override
    protected void initDagger(Context context) {
        // empty
    }

    //region ==================== Flow view lifecycle callbacks ===================

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
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

    //region ==================== IAddressView ===================

    public void initView(@Nullable UserAddressDto address) {
        if (address != null) {
            mAddressId = address.getId();
            mAddressNameEt.setText(address.getName());
            mStreetEt.setText(address.getStreet());
            mNumberBuildingEt.setText(address.getBuilding());
            mNumberApartmentEt.setText(address.getApartment());
            mNumberFloorEt.setText(String.valueOf(address.getFloor()));
            mCommentEt.setText(address.getComment());
        }
    }

    @Override
    public void showInputError() {
        // TODO: 29-Nov-16 implement this
    }

    @Override
    public UserAddressDto getUserAddress() {
        return new UserAddressDto(mAddressId,
                validateString(mAddressNameEt),
                validateString(mStreetEt),
                validateString(mNumberBuildingEt),
                validateString(mNumberApartmentEt),
                validateInteger(mNumberFloorEt),
                validateString(mCommentEt)
        );
    }

    private String validateString(EditText editText) {
        return editText.getText().toString().equals("")
                ? getContext().getString(R.string.blank)
                : editText.getText().toString();
    }

    private int validateInteger(EditText editText) {
        return editText.getText().toString().equals("")
                ? 0 : Integer.parseInt(editText.getText().toString());
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    //endregion
}
