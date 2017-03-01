package com.crackncrunch.amplain.ui.screens.product;

import android.os.Bundle;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.storage.dto.ProductDto;
import com.crackncrunch.amplain.data.storage.realm.ProductRealm;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.di.scopes.ProductScope;
import com.crackncrunch.amplain.flow.AbstractScreen;
import com.crackncrunch.amplain.flow.Screen;
import com.crackncrunch.amplain.mvp.models.CatalogModel;
import com.crackncrunch.amplain.mvp.presenters.AbstractPresenter;
import com.crackncrunch.amplain.mvp.presenters.IProductPresenter;
import com.crackncrunch.amplain.ui.screens.catalog.CatalogScreen;
import com.crackncrunch.amplain.ui.screens.product_details.DetailScreen;

import javax.inject.Inject;

import dagger.Provides;
import flow.Flow;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import mortar.MortarScope;

@Screen(R.layout.screen_product)
public class ProductScreen extends AbstractScreen<CatalogScreen.Component> {

    @Inject
    CatalogModel mCatalogModel;

    private ProductRealm mProductRealm;

    public ProductScreen(ProductRealm product) {
        mProductRealm = product;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ProductScreen && mProductRealm.equals(
                ((ProductScreen) o).mProductRealm);
    }

    @Override
    public int hashCode() {
        return mProductRealm.hashCode();
    }

    @Override
    public Object createScreenComponent(CatalogScreen.Component parentComponent) {
        return DaggerProductScreen_Component.builder()
                .component(parentComponent)
                .module(new Module())
                .build();
    }

    //region ==================== DI ===================

    @dagger.Module
    public class Module {
        @Provides
        @ProductScope
        ProductPresenter provideProductPresenter() {
            return new ProductPresenter(mProductRealm);
        }
    }

    @dagger.Component(dependencies = CatalogScreen.Component.class,
            modules = Module.class)
    @ProductScope
    public interface Component {
        void inject(ProductPresenter presenter);
        void inject(ProductView view);
    }

    //endregion

    //region ==================== Presenter ===================

    public class ProductPresenter
            extends AbstractPresenter<ProductView, CatalogModel>
            implements IProductPresenter {

        private ProductRealm mProduct;
        private RealmChangeListener mListener;

        public ProductPresenter(ProductRealm productRealm) {
            mProduct = productRealm;
        }

        @Override
        protected void initActionBar() {
            // empty
        }

        @Override
        protected void initFab() {
            mRootPresenter.newFabBuilder()
                    .setVisible(false)
                    .build();
        }

        @Override
        protected void initDagger(MortarScope scope) {
            ((Component) scope.getService(DaggerService.SERVICE_NAME))
                    .inject(this);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            if (getView() != null && mProduct.isValid()) {
                getView().showProductView(new ProductDto(mProduct));

                mListener = element -> {
                    if (getView() != null) {
                        getView().showProductView(new ProductDto(mProduct));
                    }
                };
                mProduct.addChangeListener(mListener);
            } else {

            }
        }

        @Override
        public void dropView(ProductView view) {
            mProduct.removeChangeListener(mListener);
            super.dropView(view);
        }

        @Override
        public void clickOnPlus() {
            if (getView() != null) {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(realm1 -> mProduct.add());
                realm.close();
            }
        }

        @Override
        public void clickOnMinus() {
            if (getView() != null) {
                if (mProduct.getCount() > 0) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(realm1 -> mProduct.remove());
                    realm.close();
                }
            }
        }

        public void clickFavorite() {
            if (getView() != null) {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(realm1 -> mProduct.toggleFavorite());
                realm.close();
            }
        }

        public void clickShowMore() {
            Flow.get(getView()).set(new DetailScreen(mProduct));
        }
    }

    //endregion
}
