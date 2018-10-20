package com.taiqudong.android.enayeh.utils;

import com.taiqudong.android.enayeh.adapter.FeedAdapter;
import com.taiqudong.android.enayeh.application.retrofit.bean.ContentList;
import com.taiqudong.android.enayeh.bean.Feed;

/**
 * Created by tangxianming on 2017/8/24.
 */

public class DataUtil {
    public DataUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获得数据类型
     *
     * @return
     */
    public static int getFeedType(ContentList.DataBean.RowsBean rowsBean) {
        String type = rowsBean.getType();
        if (type.equals("A")) {//这是文章
            if (rowsBean.getImage().size() == 1) {
                return FeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_ONE_PICTURE.ordinal();
            } else {
                return FeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_THREE_PICTURE.ordinal();
            }
        } else {
            return FeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_VIDEO.ordinal();
        }
    }

    /**
     * 转换数据大数据
     *
     * @return
     */
    public static String convertValue(int value) {
        String v = "";
        if (value > 1e6) {
            String temp = String.valueOf((float) value / 1e6);
            v = temp.substring(0, temp.indexOf(".") + 2) + "m";
            return v;
        }
        if (value > 1e3) {
            String temp = String.valueOf((float) value / 1e3);
            v = temp.substring(0, temp.indexOf(".") + 2) + "k";
            return v;
        } else {
            return String.valueOf(value);
        }
    }

    /**
     * 是不是大于1000
     *
     * @return
     */
    public static boolean maxThousand(int value) {
        if (value > 1000) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param value
     * @return
     */
    public static boolean isDigital(String value) {
        boolean isDigital = false;
        try {
            Integer.valueOf(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
