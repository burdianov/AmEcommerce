package com.crackncrunch.amplain.data.network;

import com.crackncrunch.amplain.data.network.req.UserLoginReq;
import com.crackncrunch.amplain.data.network.req.UserSignInReq;
import com.crackncrunch.amplain.data.network.res.AvatarUrlRes;
import com.crackncrunch.amplain.data.network.res.CommentRes;
import com.crackncrunch.amplain.data.network.res.FbProfileRes;
import com.crackncrunch.amplain.data.network.res.ProductRes;
import com.crackncrunch.amplain.data.network.res.UserRes;
import com.crackncrunch.amplain.data.network.res.VkProfileRes;
import com.crackncrunch.amplain.utils.ConstantsManager;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Lilian on 21-Feb-17.
 */

public interface RestService {
    @GET("products")
    Observable<Response<List<ProductRes>>> getProductResObs
        (@Header(ConstantsManager.IF_MODIFIED_SINCE_HEADER) String
             lastEntityUpdate);

    @POST("products/{productId}/comments")
    Observable<CommentRes> sendCommentToServer
            (@Path("productId") String productId,
             @Body CommentRes commentRes);

    @Multipart
    @POST("avatar")
    Observable<AvatarUrlRes> uploadUserAvatar(@Part MultipartBody.Part file);

    @POST("login")
    Observable<Response<UserRes>> loginUser(@Body UserLoginReq userLoginReq);

    @GET
    Observable<VkProfileRes> signInVk(@Url String baseVkApiUrl,
                                      @Query("access_token") String vkToken);

    @POST("socialLogin")
    Observable<Response<UserRes>> socialSignIn(@Body UserSignInReq userSocReq);

    @GET
    Observable<FbProfileRes> getFbProfile(@Url String baseUrl,
                                          @Query("access_token") String accessToken);
}
