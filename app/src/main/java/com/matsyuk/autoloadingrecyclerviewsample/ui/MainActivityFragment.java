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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        init(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.startLoading();
    }

    private void init(View view) {
        recyclerView = (AutoLoadingRecyclerView) view.findViewById(R.id.RecyclerView);
        GridLayoutManager recyclerViewLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerViewLayoutManager.supportsPredictiveItemAnimations();
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.setLimit(LIMIT);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLoadingObservable(offsetAndLimit -> EmulateResponseManager.getInstance().getEmulateResponse(offsetAndLimit.getOffset(), offsetAndLimit.getLimit()));
    }

    @Override
    public void onDestroyView() {
        recyclerView.onDestroy();
        super.onDestroyView();
    }

}
