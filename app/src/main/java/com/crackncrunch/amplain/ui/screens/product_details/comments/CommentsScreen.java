package com.crackncrunch.amplain.ui.screens.product_details.comments;

import android.os.Bundle;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.storage.dto.CommentDto;
import com.crackncrunch.amplain.data.storage.realm.CommentRealm;
import com.crackncrunch.amplain.data.storage.realm.ProductRealm;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.di.scopes.DaggerScope;
import com.crackncrunch.amplain.flow.AbstractScreen;
import com.crackncrunch.amplain.flow.Screen;
import com.crackncrunch.amplain.mvp.models.DetailModel;
import com.crackncrunch.amplain.mvp.presenters.AbstractPresenter;
import com.crackncrunch.amplain.ui.screens.product_details.DetailScreen;

import java.util.List;

import dagger.Provides;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import mortar.MortarScope;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Lilian on 25-Feb-17.
 */

@Screen(R.layout.screen_comments)
public class CommentsScreen extends AbstractScreen<DetailScreen.Component> {
    private ProductRealm mProductRealm;

    public CommentsScreen(ProductRealm productRealm) {
        mProductRealm = productRealm;
    }

    //region ==================== DI ===================

    @Override
    public Object createScreenComponent(DetailScreen.Component parentComponent) {
        return DaggerCommentsScreen_Component.builder()
                .component(parentComponent)
                .module(new Module())
                .build();
    }

    @dagger.Module
    public class Module {
        @Provides
        @DaggerScope(CommentsScreen.class)
        CommentsPresenter provideCommentsPresenter() {
            return new CommentsPresenter(mProductRealm);
        }
    }

    @dagger.Component(dependencies = DetailScreen.Component.class,
            modules = Module.class)
    @DaggerScope(CommentsScreen.class)
    public interface Component {
        void inject(CommentsPresenter presenter);
        void inject(CommentsView view);
        void inject(CommentsAdapter adapter);
    }


    //endregion

    //region ==================== Presenter ===================

    public class CommentsPresenter extends AbstractPresenter<CommentsView,
                DetailModel> {

        private final ProductRealm mProduct;
        private RealmChangeListener mListener;

        public CommentsPresenter(ProductRealm productRealm) {
            mProduct = productRealm;
        }

        @Override
        protected void initActionBar() {
            //empty
        }

        @Override
        protected void initFab() {
            // empty
        }

        @Override
        protected void initDagger(MortarScope scope) {
            ((Component) scope.getService(DaggerService.SERVICE_NAME))
                .inject(this);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);

            mListener = new RealmChangeListener<ProductRealm>() {
                @Override
                public void onChange(ProductRealm element) {
                    CommentsPresenter.this.updateProductList(element);
                }
            };

            mProduct.addChangeListener(mListener);

            RealmList<CommentRealm> comments = mProduct.getCommentsRealm();
            Observable<CommentDto> commentsObs = Observable.from(comments)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .map(CommentDto::new);

            mCompSubs.add(subscribe(commentsObs, new ViewSubscriber<CommentDto>() {
                @Override
                public void onNext(CommentDto commentDto) {
                    getView().getAdapter().addItem(commentDto);
                }
            }));
            getView().initView();
        }

        @Override
        public void dropView(CommentsView view) {
            mProduct.removeChangeListener(mListener);
            super.dropView(view);
        }

        private void updateProductList(ProductRealm element) {
            Observable<List<CommentDto>> obs = Observable.from(element.getCommentsRealm())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .map(CommentDto::new)
                    .toList();
            mCompSubs.add(subscribe(obs, new ViewSubscriber<List<CommentDto>>() {
                @Override
                public void onNext(List<CommentDto> commentDtos) {
                    getView().getAdapter().reloadAdapter(commentDtos);
                }
            }));
        }
    }

    //endregion
}
