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
package com.matsyuk.pagination_sample.ui.pagination;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.matsyuk.pagination_sample.R;
import com.matsyuk.pagination_sample.data.EmulateResponseManager;
import com.matsyuk.pagination_sample.data.Item;
import com.matsyuk.pagination_sample.utils.pagination.PaginationTool;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;

/**
 * A placeholder fragment containing a simple view.
 */
public class PaginationFragment extends Fragment {

    private final static int LIMIT = 50;
    private PagingRecyclerViewAdapter recyclerViewAdapter;
    private Subscription pagingSubscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fmt_pagination, container, false);
        setRetainInstance(true);
        init(rootView, savedInstanceState);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void init(View view, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.RecyclerView);
        GridLayoutManager recyclerViewLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerViewLayoutManager.supportsPredictiveItemAnimations();
        // init adapter for the first time
        if (savedInstanceState == null) {
            recyclerViewAdapter = new PagingRecyclerViewAdapter();
            recyclerViewAdapter.setHasStableIds(true);
        }

        recyclerView.setSaveEnabled(true);

        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
        // if all items was loaded we don't need Pagination
        if (recyclerViewAdapter.isAllItemsLoaded()) {
            return;
        }
        // RecyclerView pagination
        pagingSubscription = PaginationTool
                .paging(recyclerView, offset -> EmulateResponseManager.getInstance().getEmulateResponse(offset, LIMIT), LIMIT)
                .subscribe(new Subscriber<List<Item>>() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) { }

                    @Override
                    public void onNext(List<Item> items) {
                        recyclerViewAdapter.addNewItems(items);
                        recyclerViewAdapter.notifyItemInserted(recyclerViewAdapter.getItemCount() - items.size());
                    }
                });
    }

    @Override
    public void onDestroyView() {
        if (pagingSubscription != null && !pagingSubscription.isUnsubscribed()) {
            pagingSubscription.unsubscribe();
        }
        super.onDestroyView();
    }

}
