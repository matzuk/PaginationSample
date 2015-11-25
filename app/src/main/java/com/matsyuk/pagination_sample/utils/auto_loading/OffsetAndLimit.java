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
package com.matsyuk.pagination_sample.utils.auto_loading;

/**
 * Offset and limit for {@link AutoLoadingRecyclerView AutoLoadedRecyclerView channel}
 *
 * @author e.matsyuk
 */
public class OffsetAndLimit {

    private int offset;
    private int limit;

    public OffsetAndLimit(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    @Override
    public String toString() {
        return "OffsetAndLimit{" +
                "offset=" + offset +
                ", limit=" + limit +
                '}';
    }
}
