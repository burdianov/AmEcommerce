package com.crackncrunch.amplain.ui.screens.product_details;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.crackncrunch.amplain.BuildConfig;
import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.storage.realm.CommentRealm;
import com.crackncrunch.amplain.data.storage.realm.ProductRealm;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.di.scopes.DaggerScope;
import com.crackncrunch.amplain.flow.AbstractScreen;
import com.crackncrunch.amplain.flow.Screen;
import com.crackncrunch.amplain.mvp.models.DetailModel;
import com.crackncrunch.amplain.mvp.presenters.AbstractPresenter;
import com.crackncrunch.amplain.mvp.presenters.MenuItemHolder;
import com.crackncrunch.amplain.mvp.presenters.RootPresenter;
import com.crackncrunch.amplain.ui.screens.catalog.CatalogScreen;
import com.squareup.picasso.Picasso;

import dagger.Provides;
import flow.TreeKey;
import io.realm.Realm;
import mortar.MortarScope;

/**
 * Created by Lilian on 25-Feb-17.
 */

@Screen(R.layout.screen_detail)
public class DetailScreen extends AbstractScreen<CatalogScreen.Component>
        implements TreeKey {

    private final ProductRealm mProductRealm;

    public DetailScreen(ProductRealm product) {
        mProductRealm = product;
    }

    @Override
    public Object createScreenComponent(CatalogScreen.Component parentComponent) {
        return DaggerDetailScreen_Component.builder()
                .component(parentComponent)
                .module(new Module())
                .build();
    }

    @NonNull
    @Override
    public Object getParentKey() {
        return new CatalogScreen();
    }

    //region ==================== DI ===================

    @dagger.Module
    public class Module {
        @Provides
        @DaggerScope(DetailScreen.class)
        DetailPresenter provideDetailPresenter() {
            return new DetailPresenter(mProductRealm);
        }

        @Provides
        @DaggerScope(DetailScreen.class)
        DetailModel provideDetailModel() {
            return new DetailModel();
        }

    }

    @dagger.Component(dependencies = CatalogScreen.Component.class,
            modules = Module.class)
    @DaggerScope(DetailScreen.class)
    public interface Component {
        void inject(DetailPresenter presenter);
        void inject(DetailView view);

        DetailModel getDetailModel();
        RootPresenter getRootPresenter();
        Picasso getPicasso();
    }

    //endregion

    //region ==================== Presenter ===================

    public class DetailPresenter extends AbstractPresenter<DetailView,
            DetailModel> {

        private final ProductRealm mProduct;

        public DetailPresenter(ProductRealm productRealm) {
            mProduct = productRealm;
        }

        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setTitle(mProduct.getProductName())
                    .setBackArrow(true)
                    .addAction(new MenuItemHolder("Add to Cart",
                            R.drawable.ic_shopping_basket_black_24dp,
                            item -> {
                                getRootView().showMessage("Go to Cart");
                                return true;
                            }))
                    .setTab(getView().getViewPager())
                    .build();
        }

        @Override
        protected void initFab() {
            // empty
        }

        protected void initFab(int page) {
            switch (page) {
                case 0:
                    mRootPresenter.newFabBuilder()
                            .setIcon(R.drawable.ic_favorite_white_24dp)
                            .setVisible(true)
                            .setOnClickListener(v -> getRootView().showMessage
                                    ("Favorite button clicked")).build();
                    break;
                case 1:
                    mRootPresenter.newFabBuilder()
                            .setIcon(R.drawable.ic_add_white_24dp)
                            .setVisible(true)
                            .setOnClickListener(v -> clickOnAddComment())
                            .build();
                    break;
            }
        }

        @Override
        protected void initDagger(MortarScope scope) {
            ((Component) scope.getService(DaggerService.SERVICE_NAME))
                    .inject(this);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            getView().initView(mProduct);
        }

        private void clickOnAddComment() {
            getView().showAddCommentDialog();
        }

        public void addComment(CommentRealm commentRealm) {
            switch (BuildConfig.FLAVOR) {
                case "base":
                    mModel.sendComment(mProduct.getId(), commentRealm);
                    break;
                case "realmMp":
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(realm1 -> mProduct.getCommentsRealm().add(commentRealm));
                    realm.close();
                    break;
            }
        }
    }

    //endregion
}
