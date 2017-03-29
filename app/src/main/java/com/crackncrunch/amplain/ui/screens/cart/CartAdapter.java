package com.crackncrunch.amplain.ui.screens.cart;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.storage.realm.OrdersRealm;
import com.crackncrunch.amplain.di.DaggerService;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class CartAdapter extends RealmRecyclerViewAdapter<OrdersRealm, CartAdapter.CartViewHolder> {
    private Context context;
    private CartViewHolder.OnClickListener mListener;
    private OrderedRealmCollection<OrdersRealm> mData;

    @Inject
    Picasso mPicasso;

    public CartAdapter(Context context, OrderedRealmCollection<OrdersRealm> data, CartViewHolder.OnClickListener listener) {
        super(context, data, true);
        this.context = context;
        mListener = listener;
        mData = data;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view, mListener);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        DaggerService.<CartScreen.Component>getDaggerComponent(recyclerView.getContext()).inject(this);
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        OrdersRealm order = mData.get(position);
        holder.mOrder = order;

        mPicasso.load(order.getImageUrl()).into(holder.mImage);
        holder.mName.setText(order.getProductName());
        if(order.getDescription().length() > 25) {
            String shortContent = order.getDescription().substring(0,25);
            holder.mDescription.setText(shortContent+"..");
        } else {
            holder.mDescription.setText(order.getDescription());
        }
        holder.mPrice.setText(String.valueOf(order.getPrice()));
        holder.mCount.setText(String.valueOf(order.getCount()));
        if(order.getCount() != 0) {
            holder.mTotal.setText(String.valueOf(order.getCount()*order.getPrice()));
        } else {
            holder.mTotal.setText(String.valueOf(order.getPrice()));
        }
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView mImage;
        public TextView mName;
        public TextView mDescription;
        public TextView mCount;
        public TextView mPrice;
        public TextView mTotal;
        public Button mDeleteProduct;

        private CartViewHolder.OnClickListener mClickListener;
        public OrdersRealm mOrder;

        public CartViewHolder(View itemView, CartViewHolder.OnClickListener listener) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.product_image);
            mName = (TextView) itemView.findViewById(R.id.product_name);
            mDescription = (TextView) itemView.findViewById(R.id.product_description);
            mPrice = (TextView)  itemView.findViewById(R.id.price_value);
            mCount = (TextView) itemView.findViewById(R.id.quantity_value);
            mTotal = (TextView) itemView.findViewById(R.id.subtotal_value);
            mDeleteProduct = (Button) itemView.findViewById(R.id.remove_button);

            mClickListener = listener;

            mDeleteProduct.setOnClickListener(this);
            mImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                switch (v.getId()) {
                    case R.id.product_image:
                        mClickListener.onImageClick(mOrder);
                        break;
                    case R.id.remove_button:
                        mClickListener.onDeleteProduct(mOrder);
                        break;
                }
            }
        }

        interface OnClickListener {
            void onImageClick(OrdersRealm order);

            void onDeleteProduct(OrdersRealm order);
        }
    }
}
