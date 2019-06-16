package com.ziq.baselib.presenter;

import android.util.Log;

import com.ziq.base.baserx.dagger.bean.IRepositoryManager;
import com.ziq.base.mvp.BasePresenter;
import com.ziq.base.mvp.IBaseView;
import com.ziq.baselib.model.BaseResponse;
import com.ziq.baselib.model.LiveListItemBean;
import com.ziq.baselib.model.ZQPlayerVideoListRequest;
import com.ziq.baselib.service.VideoService;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * author: wuyanqiang
 * 2018/11/19
 */
public class RetrofitActivityPresenter extends BasePresenter {

    @Inject
    RetrofitActivityPresenter.View mView;

    @Inject
    IRepositoryManager mRepositoryManager;

    @Inject
    public RetrofitActivityPresenter() {
    }

    public void testLifecycle(){
        Log.e("ziq", "getVideo: "+getDestroyLifecycleTransformer());
        Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getDestroyLifecycleTransformer())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.e("ziq", "onSubscribe: ");
                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        Log.e("ziq", String.valueOf(aLong));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("ziq", "onError: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.e("ziq", "onComplete: ");
                    }
                });
    }

    int currentPage = 0;
    public void getVideo(boolean isRefresh){
        if(isRefresh){
            currentPage = 0;
        }
        ZQPlayerVideoListRequest request = new ZQPlayerVideoListRequest();
        request.setOffset(String.valueOf(currentPage * 20));
        request.setLimit("20");
        request.setGame_type("ow");
        mRepositoryManager
                .createService(VideoService.class)
                .getZQVideoList(request)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showLoading();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())//控制doOnSubscribe 所在线程
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(new Action() { // 位置 影响所在线程 放 observeOn后
                    @Override
                    public void run() throws Exception {
                        mView.hideLoading();
                    }
                })
                .compose(getDestroyLifecycleTransformer())
                .subscribe(new Observer<BaseResponse<ArrayList<LiveListItemBean>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseResponse<ArrayList<LiveListItemBean>> response) {
                        if(response.isSuccess()){
                            currentPage++;
                            mView.update(response.getData(), isRefresh);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("ziq", "onError: "+e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void destroy() {

    }

    public interface View extends IBaseView{
        void update(ArrayList<LiveListItemBean> dataList , boolean isRefresh);
    }

}
