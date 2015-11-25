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
package com.matsyuk.pagination_sample.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.matsyuk.pagination_sample.R;
import com.matsyuk.pagination_sample.ui.auto_loading.AutoLoadingFragment;
import com.matsyuk.pagination_sample.ui.pagination.PaginationFragment;

/**
 * @author e.matsyuk
 */
public class MainActivityFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fmt_main, container, false);
        setRetainInstance(true);
        init(rootView);
        return rootView;
    }

    private void init(View view) {
        Button autoLoadingButton = (Button)view.findViewById(R.id.btn_auto_loading);
        autoLoadingButton.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.container, new AutoLoadingFragment());
            transaction.commit();
        });

        Button paginationToolButton = (Button)view.findViewById(R.id.btn_pagination_tool);
        paginationToolButton.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.container, new PaginationFragment());
            transaction.commit();
        });
    }

}
