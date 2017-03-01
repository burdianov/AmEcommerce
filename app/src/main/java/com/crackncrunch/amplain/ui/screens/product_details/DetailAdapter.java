package com.crackncrunch.amplain.ui.screens.product_details;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crackncrunch.amplain.data.storage.realm.ProductRealm;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.flow.AbstractScreen;
import com.crackncrunch.amplain.ui.screens.product_details.comments.CommentsScreen;
import com.crackncrunch.amplain.ui.screens.product_details.description.DescriptionScreen;

import mortar.MortarScope;

/**
 * Created by Lilian on 25-Feb-17.
 */
public class DetailAdapter extends PagerAdapter {
    private final ProductRealm mProductRealm;

    public DetailAdapter(ProductRealm product) {
        mProductRealm = product;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        AbstractScreen screen = null;
        switch (position) {
            case 0:
                screen = new DescriptionScreen(mProductRealm);
                break;
            case 1:
                screen = new CommentsScreen(mProductRealm);
                break;
        }
        MortarScope screenScope = createScreenScopeFromContext(container
                .getContext(), screen);
        Context screenContext = screenScope.createContext(container.getContext());
        View newView = LayoutInflater.from(screenContext).inflate
                (screen.getLayoutResId(), container, false);

        container.addView(newView);

        return newView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(((View) object));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "Description";
                break;
            case 1:
                title = "Comments";
                break;
        }
        return title;
    }

    private MortarScope createScreenScopeFromContext(Context context,
                                                     AbstractScreen screen) {
        MortarScope parentScope = MortarScope.getScope(context);
        MortarScope childScope = parentScope.findChild(screen.getScopeName());

        if (childScope == null) {
            Object screenComponent = screen.createScreenComponent(parentScope.getService
                    (DaggerService.SERVICE_NAME));
            if (screenComponent == null) {
                throw new IllegalStateException(" failed to create screen " +
                        "component for " + screen.getScopeName());
            }
            childScope = parentScope.buildChild()
                    .withService(DaggerService.SERVICE_NAME, screenComponent)
                    .build(screen.getScopeName());
        }
        return childScope;
    }
}
