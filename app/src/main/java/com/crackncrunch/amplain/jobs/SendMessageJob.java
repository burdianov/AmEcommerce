package com.crackncrunch.amplain.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.crackncrunch.amplain.data.managers.DataManager;
import com.crackncrunch.amplain.data.network.res.CommentRes;
import com.crackncrunch.amplain.data.storage.realm.CommentRealm;
import com.crackncrunch.amplain.data.storage.realm.ProductRealm;
import com.crackncrunch.amplain.utils.AppConfig;

import java.util.Date;

import io.realm.Realm;

public class SendMessageJob extends Job {
    private final String mProductsId;
    private final CommentRealm mComment;
    public static final String TAG = "SendMessageJob";

    public SendMessageJob(String productId, CommentRealm comment) {
        super(new Params(JobPriority.MID) // приоритет задачи
                .requireNetwork() // необходимо соединение с сетью
                //.persist() // персистентная задача (должна быть выполнена вне зависимости от сети)
                .groupBy("Comments")); // группа задач - выполняются поочередно
        mProductsId = productId;
        mComment = comment;
    }

    @Override
    public void onAdded() {
        // задача добавлена
        Log.e(TAG, "SEND MESSAGE onAdded: ");
        Realm realm = Realm.getDefaultInstance();
        ProductRealm product = realm.where(ProductRealm.class)
                .equalTo("id", mProductsId)
                .findFirst();
        realm.executeTransaction(realm1 -> product.getCommentsRealm().add(mComment));
        realm.close();
    }

    @Override
    public void onRun() throws Throwable {
        // задача начала выполнение
        Log.e(TAG, "SEND MESSAGE onRun: ");

        CommentRes comment = new CommentRes(mComment);
        DataManager.getInstance().sendComment(mProductsId, comment)
                .subscribe(commentRes -> {
                    Realm realm = Realm.getDefaultInstance();
                    CommentRealm localComment = realm.where(CommentRealm.class)
                            .equalTo("id", mComment.getId())
                            .findFirst();

                    ProductRealm product = realm.where(ProductRealm.class)
                            .equalTo("id", mProductsId)
                            .findFirst();

                    CommentRealm serverComment = new CommentRealm(commentRes);

                    realm.executeTransaction(realm1 -> {
                        if (localComment.isValid()) {
                            localComment.deleteFromRealm(); // удаляем комментарий из локальной базы т.к. запрещен override PrimaryKey
                        }
                        product.getCommentsRealm().add(serverComment); // добавляем комментарий с сервера в базу
                    });
                    realm.close();
                });
    }

    @Override
    protected void onCancel(int i, @Nullable Throwable throwable) {
        // задача завершена
        Log.e(TAG, "SEND MESSAGE onCancel: ");
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(
            @NonNull Throwable throwable, int runCount, int maxRunCount) {
        // ошибка при выполнении задачи, политика повторений разрешается здесь
        Log.e(TAG, "SEND MESSAGE shouldReRunOnThrowable: " + runCount + " " +
                maxRunCount + " s " + new Date());
        return RetryConstraint.createExponentialBackoff(runCount, AppConfig
                .INITIAL_BACK_OFF_IN_MS);
    }
}
