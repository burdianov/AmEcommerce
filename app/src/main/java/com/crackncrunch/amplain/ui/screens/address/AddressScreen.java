package com.crackncrunch.amplain.ui.screens.address;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.storage.dto.UserAddressDto;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.di.scopes.AddressScope;
import com.crackncrunch.amplain.flow.AbstractScreen;
import com.crackncrunch.amplain.flow.Screen;
import com.crackncrunch.amplain.mvp.models.AccountModel;
import com.crackncrunch.amplain.mvp.presenters.AbstractPresenter;
import com.crackncrunch.amplain.mvp.presenters.IAddressPresenter;
import com.crackncrunch.amplain.mvp.presenters.MenuItemHolder;
import com.crackncrunch.amplain.ui.screens.account.AccountScreen;

import javax.inject.Inject;

import dagger.Provides;
import flow.Flow;
import flow.TreeKey;
import mortar.MortarScope;

@Screen(R.layout.screen_add_address)
public class AddressScreen extends AbstractScreen<AccountScreen.Component>
        implements TreeKey {

    @Nullable
    private UserAddressDto mAddressDto;
    private boolean mIsAddAddress;

    public AddressScreen(@Nullable UserAddressDto addressDto) {
        mAddressDto = addressDto;
        if (mAddressDto == null) {
            mIsAddAddress = true;
            mAddressDto = new UserAddressDto();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (mAddressDto != null) {
            return o instanceof AddressScreen && mAddressDto.equals((
                    (AddressScreen) o).mAddressDto);
        } else {
            return super.equals(o);
        }
    }

    @Override
    public int hashCode() {
        return mAddressDto != null ? mAddressDto.hashCode() : super.hashCode();
    }

    @Override
    public Object createScreenComponent(AccountScreen.Component parentComponent) {
        return DaggerAddressScreen_Component.builder()
                .component(parentComponent)
                .module(new Module())
                .build();
    }

    @Override
    public Object getParentKey() {
        return new AccountScreen();
    }

    //region ==================== DI ===================

    @dagger.Module
    public class Module {
        @Provides
        @AddressScope
        AddressPresenter provideAddressPresenter() {
            return new AddressPresenter(mIsAddAddress);
        }
    }

    @dagger.Component(dependencies = AccountScreen.Component.class,
            modules = Module.class)
    @AddressScope
    public interface Component {
        void inject(AddressPresenter presenter);
        void inject(AddressView view);
    }

    //endregion

    //region ==================== Presenter ===================

    public class AddressPresenter
            extends AbstractPresenter<AddressView, AccountModel>
            implements IAddressPresenter {

        @Inject
        AccountModel mAccountModel;
        private boolean mIsAdd;

        public AddressPresenter(boolean isAddAddress) {
            mIsAdd = isAddAddress;
        }

        @Override
        protected void initActionBar() {
            String title = mIsAdd ? "Add Address" : "Edit Address";
            mRootPresenter.newActionBarBuilder()
                    .setTitle(title)
                    .addAction(new MenuItemHolder("Save", R.drawable
                            .ic_done_black_24dp, item -> {
                        clickOnAddAddress();
                        return true;
                    }))
                    .setBackArrow(true)
                    .build();
        }

        @Override
        protected void initFab() {
            // empty
        }

        @Override
        protected void initDagger(MortarScope scope) {
            // empty
        }

        @Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            if (mAddressDto != null && getView() != null) {
                getView().initView(mAddressDto);
            }
        }

        @Override
        public void clickOnAddAddress() {

            if (getView() != null) {
                UserAddressDto userAddress = getView().getUserAddress();

                mAddressDto.setId(userAddress.getId());
                mAddressDto.setName(userAddress.getName());
                mAddressDto.setStreet(userAddress.getStreet());
                mAddressDto.setBuilding(userAddress.getBuilding());
                mAddressDto.setApartment(userAddress.getApartment());
                mAddressDto.setFloor(userAddress.getFloor());
                mAddressDto.setComment(userAddress.getComment());
                mAddressDto.setFavorite(userAddress.isFavorite());

                mAccountModel.updateOrInsertAddress(mAddressDto);

                Flow.get(getView()).goBack();
            }
        }
    }

    //endregion
}
