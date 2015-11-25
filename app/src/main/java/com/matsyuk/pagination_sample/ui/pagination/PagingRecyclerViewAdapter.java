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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.matsyuk.pagination_sample.R;
import com.matsyuk.pagination_sample.data.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * @author e.matsyuk
 */
public class PagingRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MAIN_VIEW = 0;

    private List<Item> listElements = new ArrayList<>();
    // after reorientation test this member
    // or one extra request will be sent after each reorientation
    private boolean allItemsLoaded;

    static class MainViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public MainViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
        }
    }

    public void addNewItems(List<Item> items) {
        if (items.size() == 0) {
            allItemsLoaded = true;
            return;
        }
        listElements.addAll(items);
    }

    public boolean isAllItemsLoaded() {
        return allItemsLoaded;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public Item getItem(int position) {
        return listElements.get(position);
    }

    @Override
    public int getItemCount() {
        return listElements.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MAIN_VIEW) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
            return new MainViewHolder(v);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return MAIN_VIEW;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case MAIN_VIEW:
                onBindTextHolder(holder, position);
                break;
        }
    }

    private void onBindTextHolder(RecyclerView.ViewHolder holder, int position) {
        MainViewHolder mainHolder = (MainViewHolder) holder;
        mainHolder.textView.setText(getItem(position).getItemStr());
    }

}