package com.crackncrunch.amplain.mvp.views;

import com.crackncrunch.amplain.data.storage.dto.UserAddressDto;

public interface IAddressView extends IView {
    void showInputError();

    UserAddressDto getUserAddress();
}
