package com.crackncrunch.amplain.ui.screens.cart;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.storage.realm.OrdersRealm;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.mvp.views.AbstractView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

public class CartView extends AbstractView<CartScreen.CartPresenter> {
    @BindView(R.id.cart_list)
    RecyclerView mOrdersList;
    @BindView(R.id.amount_value)
    TextView amountValue;
    @BindView(R.id.discount_value)
    TextView discountValue;
    @BindView(R.id.total_value)
    TextView totalValue;

    CartAdapter mAdapter;
    int mDiscount, mTotal, mAmount;

    public CartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<CartScreen.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    public void showCartList(RealmResults<OrdersRealm> allOrders) {
        mAdapter = new CartAdapter(getContext(), allOrders, listener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mOrdersList.setLayoutManager(layoutManager);
        mOrdersList.setAdapter(mAdapter);
    }

    CartAdapter.CartViewHolder.OnClickListener listener = new CartAdapter.CartViewHolder.OnClickListener() {
        @Override
        public void onImageClick(OrdersRealm order) {
            mPresenter.onProductImageClick(order);
        }

        @Override
        public void onDeleteProduct(OrdersRealm order) {
            mPresenter.onDeleteProduct(order);
        }
    };


    public void initPrice(RealmResults<OrdersRealm> orders) {
        mTotal = 0;
        mAmount = 0;
        for(OrdersRealm order : orders) {
            if(!order.isStatusPurchase()) {
                if(order.getCount()>0) {
                    mTotal += order.getPrice()*order.getCount();
                } else {
                    mTotal += order.getPrice();
                }
                mAmount += 1;
            }
        }
        if(mTotal > 3000) {
            mDiscount = (int) (mTotal *0.15);
            discountValue.setText(String.valueOf(mDiscount));
            totalValue.setText(String.valueOf(mTotal - mDiscount));
        } else {
            mDiscount = 0;
            discountValue.setText(String.valueOf(mDiscount));
            totalValue.setText(String.valueOf(mTotal));
        }
        amountValue.setText(String.valueOf(mAmount));
    }
}
