package com.ziq.baselib.presenter;

import android.util.Log;

import com.ziq.base.baserx.dagger.bean.IRepositoryManager;
import com.ziq.base.mvp.BasePresenter;
import com.ziq.base.mvp.IBaseView;
import com.ziq.baselib.model.PandaTvDataBean;
import com.ziq.baselib.model.VideoHttpResult;
import com.ziq.baselib.service.VideoService;

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

    public void getVideo(){
        mRepositoryManager
                .createService(VideoService.class)
                .getVideList("lol", 1, 20, 1, "3.3.1.5978")
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        Log.e("ziq", "doOnSubscribe: "+Thread.currentThread().getName());
                        mView.showLoading();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())//控制doOnSubscribe 所在线程
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(new Action() { // 位置 影响所在线程 放 observeOn后
                    @Override
                    public void run() throws Exception {
                        Log.e("ziq", "doFinally: "+Thread.currentThread().getName());
                        mView.hideLoading();
                    }
                })
                .compose(getDestroyLifecycleTransformer())
                .subscribe(new Observer<VideoHttpResult<PandaTvDataBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(VideoHttpResult<PandaTvDataBean> pandaTvDataBeanVideoHttpResult) {
                        Log.e("ziq", "onNext: "+Thread.currentThread().getName());
                        mView.update();
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
        void update();
    }

}
