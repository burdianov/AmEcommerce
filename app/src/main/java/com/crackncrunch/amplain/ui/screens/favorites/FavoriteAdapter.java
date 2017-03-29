package com.crackncrunch.amplain.ui.screens.favorites;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.storage.realm.ProductRealm;
import com.crackncrunch.amplain.di.DaggerService;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class FavoriteAdapter extends RealmRecyclerViewAdapter<ProductRealm, FavoriteAdapter.FavoriteViewHolder> {
    private Context context;
    private FavoriteViewHolder.OnClickListener mListener;
    private OrderedRealmCollection<ProductRealm> mData;

    @Inject
    Picasso mPicasso;

    public FavoriteAdapter(Context context, OrderedRealmCollection<ProductRealm> data, FavoriteViewHolder.OnClickListener listener) {
        super(context, data, true);
        this.context = context;
        mListener = listener;
        mData = data;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view, mListener);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        DaggerService.<FavoriteScreen.Component>getDaggerComponent(recyclerView.getContext()).inject(this);
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {
        ProductRealm product = mData.get(position);
        holder.mProduct = product;

        mPicasso.load(product.getImageUrl()).into(holder.mImage);
        holder.mName.setText(product.getProductName());
        holder.mDescription.setText(product.getDescription());
        holder.mPrice.setText(String.valueOf(product.getPrice()));
//        mPrice.text = context.getString(R.string.favorite_price_mask, product.price);
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView mImage;
        public TextView mName;
        public TextView mDescription;
        public TextView mPrice;
        public AppCompatImageButton mFavoriteButton;
        public AppCompatImageButton mCartButton;

        private FavoriteViewHolder.OnClickListener mClickListener;
        public ProductRealm mProduct;

        public FavoriteViewHolder(View itemView, FavoriteViewHolder.OnClickListener listener) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.favorite_product_img);
            mName = (TextView) itemView.findViewById(R.id.favorite_product_name);
            mDescription = (TextView) itemView.findViewById(R.id
                    .favorite_product_description);
            mPrice = (TextView)  itemView.findViewById(R.id.favorite_product_price);
            mFavoriteButton = (AppCompatImageButton) itemView.findViewById(R.id.favorite_button);
            mCartButton = (AppCompatImageButton) itemView.findViewById(R.id.cart_button);

            mClickListener = listener;

            mCartButton.setOnClickListener(this);
            mFavoriteButton.setOnClickListener(this);
            mImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                switch (v.getId()) {
                    case R.id.favorite_product_img:
                        mClickListener.onImageClick(mProduct);
                        break;
                    case R.id.cart_button:
                        mClickListener.onToCartClick(mProduct);
                        break;
                    case R.id.favorite_button:
                        mClickListener.onFavoriteClick(mProduct);
                        break;
                }
            }
        }

        interface OnClickListener {
            void onImageClick(ProductRealm product);
            void onFavoriteClick(ProductRealm product);
            void onToCartClick(ProductRealm product);
        }
    }
}

