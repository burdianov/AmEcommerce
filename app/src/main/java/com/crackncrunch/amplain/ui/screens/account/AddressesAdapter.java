package com.crackncrunch.amplain.ui.screens.account;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.storage.dto.UserAddressDto;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressesAdapter extends RecyclerView
        .Adapter<AddressesAdapter.ViewHolder> implements SwipeListener {

    private ArrayList<UserAddressDto> mUserAddresses = new ArrayList<>();

    public void addItem(UserAddressDto address) {
        mUserAddresses.add(address);
        notifyDataSetChanged();
    }

    public void reloadAdapter() {
        mUserAddresses.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .item_address, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserAddressDto address = mUserAddresses.get(position);

        holder.mLabelAddressTxt.setText(address.getName());
        holder.mAddressTxt.setText(addressToString(address));
        holder.mCommentTxt.setText(address.getComment());
    }

    private StringBuilder addressToString(UserAddressDto info) {
        StringBuilder addressBuilder = new StringBuilder();

        addressBuilder.append("Str. ");
        addressBuilder.append(info.getStreet());
        addressBuilder.append(" ");
        addressBuilder.append(info.getBuilding());
        addressBuilder.append(" - ");
        addressBuilder.append(info.getApartment());
        addressBuilder.append(", ");
        addressBuilder.append(info.getFloor());
        addressBuilder.append(" floor");

        return addressBuilder;
    }

    @Override
    public int getItemCount() {
        return mUserAddresses.size();
    }

    @Override
    public void onSwipe(int position) {
        mUserAddresses.remove(position);
//        notifyDataSetChanged();
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.label_address_txt)
        TextView mLabelAddressTxt;
        @BindView(R.id.address_txt)
        TextView mAddressTxt;
        @BindView(R.id.comment_txt)
        TextView mCommentTxt;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
