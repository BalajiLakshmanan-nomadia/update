package com.synchroteam.listeners;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Interface to fire event when the search result for recycler view is empty.
 * Created by Trident on 11/10/2016.
 */

public interface RvEmptyListener {
    void onEmptyList(ArrayList<HashMap<String, String>> list);
}
