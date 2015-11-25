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
package com.matsyuk.autoloadingrecyclerviewsample.utils.pagination;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.matsyuk.autoloadingrecyclerviewsample.utils.BackgroundExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * @author e.matsyuk
 */
public class PaginationTool {

    // for first start of items loading then on RecyclerView there are not items and no scrolling
    private static final int EMPTY_LIST_ITEMS_COUNT = 0;
    // default limit for requests
    private static final int DEFAULT_LIMIT = 50;

    private static final int MAX_ATTEMPTS_TO_RETRY_LOADING = 3;

    public static <T> Observable<List<T>> paging(RecyclerView recyclerView, PagingListener<T> pagingListener) {
        return paging(recyclerView, pagingListener, DEFAULT_LIMIT, EMPTY_LIST_ITEMS_COUNT);
    }

    public static <T> Observable<List<T>> paging(RecyclerView recyclerView, PagingListener<T> pagingListener, int limit) {
        return paging(recyclerView, pagingListener, limit, EMPTY_LIST_ITEMS_COUNT);
    }

    public static <T> Observable<List<T>> paging(RecyclerView recyclerView, PagingListener<T> pagingListener, int limit, int emptyListCount) {
        if (recyclerView == null) {
            throw new PagingException("null recyclerView");
        }
        if (recyclerView.getAdapter() == null) {
            throw new PagingException("null recyclerView adapter");
        }
        if (limit <= 0) {
            throw new PagingException("limit must be greater then 0");
        }
        if (limit < 0) {
            throw new PagingException("emptyListCount must be greater then 0");
        }

        return getScrollObservable(recyclerView, limit, emptyListCount)
                .distinctUntilChanged()
                .switchMap(offset -> getPagingObservable(pagingListener, pagingListener.onNextPage(offset), 0, offset))
                .subscribeOn(Schedulers.from(BackgroundExecutor.getSafeBackgroundExecutor()))
                .observeOn(AndroidSchedulers.mainThread());

    }

    private static Observable<Integer> getScrollObservable(RecyclerView recyclerView, int limit, int emptyListCount) {
        return Observable.create(subscriber -> {
            final RecyclerView.OnScrollListener sl = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (!subscriber.isUnsubscribed()) {
                        int position = getLastVisibleItemPosition(recyclerView);
                        int updatePosition = recyclerView.getAdapter().getItemCount() - 1 - (limit / 2);
                        if (position >= updatePosition) {
                            subscriber.onNext(recyclerView.getAdapter().getItemCount());
                        }
                    }
                }
            };
            recyclerView.addOnScrollListener(sl);
            subscriber.add(Subscriptions.create(() -> recyclerView.removeOnScrollListener(sl)));
            if (recyclerView.getAdapter().getItemCount() == emptyListCount) {
                subscriber.onNext(recyclerView.getAdapter().getItemCount());
            }
        });
    }

    private static int getLastVisibleItemPosition(RecyclerView recyclerView) {
        Class recyclerViewLMClass = recyclerView.getLayoutManager().getClass();
        if (recyclerViewLMClass == LinearLayoutManager.class || LinearLayoutManager.class.isAssignableFrom(recyclerViewLMClass)) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
            return linearLayoutManager.findLastVisibleItemPosition();
        } else if (recyclerViewLMClass == StaggeredGridLayoutManager.class || StaggeredGridLayoutManager.class.isAssignableFrom(recyclerViewLMClass)) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager)recyclerView.getLayoutManager();
            int[] into = staggeredGridLayoutManager.findLastVisibleItemPositions(null);
            List<Integer> intoList = new ArrayList<>();
            for (int i : into) {
                intoList.add(i);
            }
            return Collections.max(intoList);
        }
        throw new PagingException("Unknown LayoutManager class: " + recyclerViewLMClass.toString());
    }

    private static <T> Observable<List<T>> getPagingObservable(PagingListener<T> listener, Observable<List<T>> observable, int attemptToRetry, int offset) {
        return observable.onErrorResumeNext(throwable -> {
            if (attemptToRetry < MAX_ATTEMPTS_TO_RETRY_LOADING) {
                int attemptToRetryInc = attemptToRetry + 1;
                return getPagingObservable(listener, listener.onNextPage(offset), attemptToRetryInc, offset);
            } else {
                return Observable.empty();
            }
        });
    }

}
