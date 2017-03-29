package com.crackncrunch.amplain.data.storage.dto;


import android.content.pm.PackageManager;
import android.os.Parcel;
import android.os.Parcelable;

public class ActivityPermissionsResultDto implements Parcelable {
    private int requestCode;
    private String[] permissions;
    private int[] grantResult;

    public ActivityPermissionsResultDto(int requestCode, String[] permissions, int[] grantResult) {
        this.requestCode = requestCode;
        this.permissions = permissions;
        this.grantResult = grantResult;
    }

    protected ActivityPermissionsResultDto(Parcel in) {
        requestCode = in.readInt();
        permissions = in.createStringArray();
        grantResult = in.createIntArray();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(requestCode);
        dest.writeStringArray(permissions);
        dest.writeIntArray(grantResult);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ActivityPermissionsResultDto> CREATOR = new Creator<ActivityPermissionsResultDto>() {
        @Override
        public ActivityPermissionsResultDto createFromParcel(Parcel in) {
            return new ActivityPermissionsResultDto(in);
        }

        @Override
        public ActivityPermissionsResultDto[] newArray(int size) {
            return new ActivityPermissionsResultDto[size];
        }
    };

    public boolean isAllGranted() {
        boolean res = true;
        for (int grant : grantResult) {
            res = res && (grant == PackageManager.PERMISSION_GRANTED);
            if (res == false) {
                break;
            }
        }
        return res;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public int[] getGrantResult() {
        return grantResult;
    }
}
