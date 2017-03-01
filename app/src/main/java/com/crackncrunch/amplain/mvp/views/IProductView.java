package com.crackncrunch.amplain.mvp.views;

import com.crackncrunch.amplain.data.storage.dto.ProductDto;

public interface IProductView extends IView {
    void showProductView(ProductDto product);
    void updateProductCountView(ProductDto product);
}
