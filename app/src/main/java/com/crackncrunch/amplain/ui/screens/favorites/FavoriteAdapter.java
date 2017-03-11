package com.crackncrunch.amplain.ui.screens.favorites;

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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteAdapter extends
        RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private List<ProductRealm> mProductsList = new ArrayList<>();
    private FavoriteScreen.FavoritePresenter mPresenter;

    @Inject
    Picasso mPicasso;

    public FavoriteAdapter(FavoriteScreen.FavoritePresenter presenter) {
        mPresenter = presenter;
    }

    public void addItem(ProductRealm productRealm) {
        mProductsList.add(productRealm);
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        DaggerService.<FavoriteScreen.Component>getDaggerComponent(recyclerView
                .getContext()).inject(this);
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {
        ProductRealm product = mProductsList.get(position);
        holder.productName.setText(product.getProductName());
        holder.productDescription.setText(product.getDescription());
        holder.productPrice.setText(String.valueOf(product.getPrice()));
        String productImg = product.getImageUrl();
        if (productImg == null || productImg.isEmpty()) {
            productImg = "http://placehold.it/350x350";
        }

        mPicasso.load(productImg)
                .error(R.drawable.product_placeholder)
                .fit()
                .centerInside()
                .into(holder.productImg);

        holder.productImg.setOnClickListener(v -> {
            mPresenter.clickOnProduct(product);
        });
    }

    @Override
    public int getItemCount() {
        return mProductsList.size();
    }

    public void reloadAdapter(List<ProductRealm> products) {
        mProductsList.clear();
        mProductsList = products;
        notifyDataSetChanged();
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.favorite_product_img)
        ImageView productImg;
        @BindView(R.id.favorite_product_name)
        TextView productName;
        @BindView(R.id.favorite_product_description)
        TextView productDescription;
        @BindView(R.id.favorite_product_price)
        TextView productPrice;
        @BindView(R.id.favorite_basket_img)
        ImageView basketImg;
        @BindView(R.id.favorite_heart_img)
        ImageView heartImg;

        public FavoriteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
