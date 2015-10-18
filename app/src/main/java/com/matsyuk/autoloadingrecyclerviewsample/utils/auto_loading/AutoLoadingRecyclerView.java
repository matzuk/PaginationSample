/**
 * Copyright 2015 Eugene Matsyuk (matzuk2@mail.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
package com.matsyuk.autoloadingrecyclerviewsample.utils.auto_loading;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;

import com.matsyuk.autoloadingrecyclerviewsample.utils.BackgroundExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * @author e.matsyuk
 */
public class AutoLoadingRecyclerView<T> extends RecyclerView {

    private static final String TAG = "AutoLoadingRecyclerView";
    private static  final int START_OFFSET = 0;

    private PublishSubject<OffsetAndLimit> scrollLoadingChannel = PublishSubject.create();
    private Subscription loadNewItemsSubscription;
    private Subscription subscribeToLoadingChannelSubscription;
    private int limit;
    private ILoading<T> iLoading;
    private AutoLoadingRecyclerViewAdapter<T> autoLoadingRecyclerViewAdapter;

    public AutoLoadingRecyclerView(Context context) {
        super(context);
        init();
    }

    public AutoLoadingRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoLoadingRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * required method
     * call after init all parameters in AutoLoadedRecyclerView
     */
    public void startLoading() {
        OffsetAndLimit offsetAndLimit = new OffsetAndLimit(START_OFFSET, getLimit());
        loadNewItems(offsetAndLimit);
    }

    private void init() {
        startScrollingChannel();
    }

    private void startScrollingChannel() {
        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int position = getLastVisibleItemPosition();
                int limit = getLimit();
                int updatePosition = getAdapter().getItemCount() - 1 - (limit / 2);
                if (position >= updatePosition) {
                    int offset = getAdapter().getItemCount() - 1;
                    OffsetAndLimit offsetAndLimit = new OffsetAndLimit(offset, limit);
                    scrollLoadingChannel.onNext(offsetAndLimit);
                }
            }
        });
    }

    private int getLastVisibleItemPosition() {
        Class recyclerViewLMClass = getLayoutManager().getClass();
        if (recyclerViewLMClass == LinearLayoutManager.class || LinearLayoutManager.class.isAssignableFrom(recyclerViewLMClass)) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager)getLayoutManager();
            return linearLayoutManager.findLastVisibleItemPosition();
        } else if (recyclerViewLMClass == StaggeredGridLayoutManager.class || StaggeredGridLayoutManager.class.isAssignableFrom(recyclerViewLMClass)) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager)getLayoutManager();
            int[] into = staggeredGridLayoutManager.findLastVisibleItemPositions(null);
            List<Integer> intoList = new ArrayList<>();
            for (int i : into) {
                intoList.add(i);
            }
            return Collections.max(intoList);
        }
        throw new AutoLoadingRecyclerViewExceptions("Unknown LayoutManager class: " + recyclerViewLMClass.toString());
    }

    public int getLimit() {
        if (limit <= 0) {
            throw new AutoLoadingRecyclerViewExceptions("limit must be initialised! And limit must be more than zero!");
        }
        return limit;
    }

    /**
     * required method
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Deprecated
    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter instanceof AutoLoadingRecyclerViewAdapter) {
            super.setAdapter(adapter);
        } else {
            throw new AutoLoadingRecyclerViewExceptions("Adapter must be implement IAutoLoadedAdapter");
        }
    }

    /**
     * required method
     */
    public void setAdapter(AutoLoadingRecyclerViewAdapter<T> autoLoadingRecyclerViewAdapter) {
        if (autoLoadingRecyclerViewAdapter == null) {
            throw new AutoLoadingRecyclerViewExceptions("Null adapter. Please initialise adapter!");
        }
        this.autoLoadingRecyclerViewAdapter = autoLoadingRecyclerViewAdapter;
        super.setAdapter(autoLoadingRecyclerViewAdapter);
    }

    public AutoLoadingRecyclerViewAdapter<T> getAdapter() {
        if (autoLoadingRecyclerViewAdapter == null) {
            throw new AutoLoadingRecyclerViewExceptions("Null adapter. Please initialise adapter!");
        }
        return autoLoadingRecyclerViewAdapter;
    }

    public void setLoadingObservable(ILoading<T> iLoading) {
        this.iLoading = iLoading;
    }

    public ILoading<T> getLoadingObservable() {
        if (iLoading == null) {
            throw new AutoLoadingRecyclerViewExceptions("Null LoadingObservable. Please initialise LoadingObservable!");
        }
        return iLoading;
    }

    private void subscribeToLoadingChannel() {
        Subscriber<OffsetAndLimit> toLoadingChannelSubscriber = new Subscriber<OffsetAndLimit>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "subscribeToLoadingChannel error", e);
            }

            @Override
            public void onNext(OffsetAndLimit offsetAndLimit) {
                unsubscribe();
                loadNewItems(offsetAndLimit);
            }
        };
        subscribeToLoadingChannelSubscription = scrollLoadingChannel
                .subscribe(toLoadingChannelSubscriber);
    }

    private void loadNewItems(OffsetAndLimit offsetAndLimit) {
        Subscriber<List<T>> loadNewItemsSubscriber = new Subscriber<List<T>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "loadNewItems error", e);
                subscribeToLoadingChannel();
            }

            @Override
            public void onNext(List<T> ts) {
                getAdapter().addNewItems(ts);
                getAdapter().notifyItemInserted(getAdapter().getItemCount() - ts.size());
                if (ts.size() > 0) {
                    subscribeToLoadingChannel();
                }
            }
        };

        loadNewItemsSubscription = getLoadingObservable().getLoadingObservable(offsetAndLimit)
                .subscribeOn(Schedulers.from(BackgroundExecutor.getSafeBackgroundExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loadNewItemsSubscriber);
    }

    /**
     * required method
     * call in OnDestroy(or in OnDestroyView) method of Activity or Fragment
     */
    public void onDestroy() {
        scrollLoadingChannel.onCompleted();
        if (subscribeToLoadingChannelSubscription != null && !subscribeToLoadingChannelSubscription.isUnsubscribed()) {
            subscribeToLoadingChannelSubscription.unsubscribe();
        }
        if (loadNewItemsSubscription != null && !loadNewItemsSubscription.isUnsubscribed()) {
            loadNewItemsSubscription.unsubscribe();
        }
    }

}
