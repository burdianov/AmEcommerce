package com.crackncrunch.amplain.jobs;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.crackncrunch.amplain.data.managers.DataManager;
import com.crackncrunch.amplain.utils.AppConfig;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UploadAvatarJob extends Job {
    public static final String TAG = "UploadAvatarJob";
    private final String mImageUri;

    public UploadAvatarJob(String imageUri) {
        super(new Params(JobPriority.HIGH)
                .requireNetwork()
                .persist());
        mImageUri = imageUri;
    }

    @Override
    public void onAdded() {
        Log.e(TAG, "UPLOAD onAdded: ");
    }

    @Override
    public void onRun() throws Throwable {
        Log.e(TAG, "UPLOAD onRun: ");
        File file = new File(Uri.parse(mImageUri).getPath());
        RequestBody sendFile = RequestBody.create(MediaType.parse
                ("multipart/form-data"), file);

        MultipartBody.Part body = MultipartBody.Part.createFormData("avatar",
                file.getName(), sendFile);

        DataManager.getInstance().uploadUserPhoto(body)
            .subscribe(avatarUrlRes -> {
                Log.e(TAG, "Upload to server: ");
                DataManager.getInstance().getPreferencesManager().saveUserAvatar
                        (avatarUrlRes.getAvatarUrl());
            });
    }

    @Override
    protected void onCancel(int i, @Nullable Throwable throwable) {
        Log.e(TAG, "UPLOAD onCancel: ");
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(
            @NonNull Throwable throwable, int runCount, int maxRunCount) {
        Log.e(TAG, "UPLOAD shouldReRunOnThrowable: ");
        return RetryConstraint.createExponentialBackoff(runCount,
                AppConfig.INITIAL_BACK_OFF_IN_MS);
    }
}
