package com.ziq.baselib.service;

import com.ziq.baselib.model.BaseResponse;
import com.ziq.baselib.model.LiveListItemBean;
import com.ziq.baselib.model.ZQPlayerVideoListRequest;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * author: wuyanqiang
 * 2018/11/19
 */
public interface VideoService {

    @POST("/live/list")
    Observable<BaseResponse<ArrayList<LiveListItemBean>>> getZQVideoList(@Body ZQPlayerVideoListRequest zqPlayerVideoListRequest);

}
