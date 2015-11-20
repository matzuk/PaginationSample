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
package com.matsyuk.autoloadingrecyclerviewsample.ui;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.matsyuk.autoloadingrecyclerviewsample.R;
import com.matsyuk.autoloadingrecyclerviewsample.data.EmulateResponseManager;
import com.matsyuk.autoloadingrecyclerviewsample.data.Item;
import com.matsyuk.autoloadingrecyclerviewsample.utils.auto_loading.AutoLoadingRecyclerView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final static int LIMIT = 50;
    private AutoLoadingRecyclerView<Item> recyclerView;
    private LoadingRecyclerViewAdapter recyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setRetainInstance(true);
        init(rootView, savedInstanceState);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void init(View view, Bundle savedInstanceState) {
        recyclerView = (AutoLoadingRecyclerView) view.findViewById(R.id.RecyclerView);
        GridLayoutManager recyclerViewLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerViewLayoutManager.supportsPredictiveItemAnimations();
        // init adapter for the first time
        if (savedInstanceState == null) {
            recyclerViewAdapter = new LoadingRecyclerViewAdapter();
            recyclerViewAdapter.setHasStableIds(true);
        }

        recyclerView.setSaveEnabled(true);

        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.setLimit(LIMIT);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLoadingObservable(offsetAndLimit -> EmulateResponseManager.getInstance().getEmulateResponse(offsetAndLimit.getOffset(), offsetAndLimit.getLimit()));
        // start loading for the first time
        if (savedInstanceState == null) {
            recyclerView.startLoading();
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // start loading after reorientation
        if (savedInstanceState != null) {
            recyclerView.startLoading();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
