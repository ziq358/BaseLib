package com.ziq.baselib.presenter;

import android.util.Log;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.ziq.base.mvp.BasePresenter;
import com.ziq.base.mvp.IBasePresenter;
import com.ziq.base.mvp.IBaseView;
import com.ziq.base.utils.RetrofitUtil;
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
    public RetrofitActivityPresenter() {
    }

    public void testLifecycle(){
        Log.e("ziq", "getVideo: "+getLifecycleProvider());
        Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getLifecycleProvider().bindUntilEvent(ActivityEvent.DESTROY))
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
        RetrofitUtil.getInstance().getRetrofit()
                .create(VideoService.class)
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
                .compose(getLifecycleProvider().bindUntilEvent(ActivityEvent.DESTROY))
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
