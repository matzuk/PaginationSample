package com.matsyuk.pagination_sample.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;

/**
 * @author e.matsyuk
 */
public class EmulateResponseManager {

    private final static int MAX_LIMIT = 1000;
    private static final long FAKE_RESPONSE_TIME_IN_MS = 200;
    private final static int MAX_FAKE_ERROR_COUNT = 2;
    private final static int OFFSET_WHEN_FAKE_ERROR = 200;

    private static volatile EmulateResponseManager client;

    private int fakeErrorCount = 0;

    public static EmulateResponseManager getInstance() {
        if (client == null) {
            synchronized (EmulateResponseManager.class) {
                if (client == null) {
                    client = new EmulateResponseManager();
                }
            }
        }
        return client;
    }

    public Observable<List<Item>> getEmulateResponse(int offset, int limit) {
        if (offset == OFFSET_WHEN_FAKE_ERROR && fakeErrorCount < MAX_FAKE_ERROR_COUNT) {
            // emulate fake error in response
            fakeErrorCount++;
            return Observable
                    .error(new RuntimeException("fake error"));
        } else {
            return Observable
                    .defer(() -> Observable.just(getFakeItemList(offset, limit)))
                    .delaySubscription(FAKE_RESPONSE_TIME_IN_MS, TimeUnit.MILLISECONDS);
        }
    }

    private List<Item> getFakeItemList(int offset, int limit) {
        List<Item> list = new ArrayList<>();
        // If offset > MAX_LIMIT then there is no Items in Fake server. So we return empty List
        if (offset > MAX_LIMIT) {
            return list;
        }
        int concreteLimit = offset + limit;
        // In Fake server there are only MAX_LIMIT Items.
        if (concreteLimit > MAX_LIMIT) {
            concreteLimit = MAX_LIMIT;
        }
        // Generate List of Items
        for (int i = offset; i < concreteLimit; i++) {
            String itemStr = String.valueOf(i);
            list.add(new Item(i, itemStr));
        }
        return list;
    }

}
