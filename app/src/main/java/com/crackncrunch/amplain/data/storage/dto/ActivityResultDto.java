package com.crackncrunch.amplain.data.storage.dto;

import android.content.Intent;
import android.support.annotation.Nullable;

public class ActivityResultDto {
    private int requestCode;
    private int resultCode;
    @Nullable
    private Intent data;

    public ActivityResultDto(int requestCode, int resultCode, Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    @Nullable
    public Intent getIntent() {
        return data;
    }
}
