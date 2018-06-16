//package com.example.smahadik.kloudbase;
//
//import android.content.Context;
//
//import java.util.HashMap;
//import java.util.List;
//
///**
// * Created by smahadik on 6/15/18.
// */
//
//public class StableArrayAdapter {
//
//    final int INVALID_ID = -1;
//
//    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
//
//    public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
//        super(context, textViewResourceId, objects);
//        for (int i = 0; i < objects.size(); ++i) {
//            mIdMap.put(objects.get(i), i);
//        }
//    }
//
//    @Override
//    public long getItemId(int position) {
//        if (position < 0 || position >= mIdMap.size()) {
//            return INVALID_ID;
//        }
//        String item = getItem(position);
//        return mIdMap.get(item);
//    }
//
//    @Override
//    public boolean hasStableIds() {
//        return true;
//    }
//
//}
