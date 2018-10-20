package com.taiqudong.android.ad.cache;

import com.taiqudong.android.ad.AdContent;

/**
 * Created by zhangxiang on 2017/9/6.
 */

public class AdContentHolder {

    private long timeoutTs;
    private AdContent content;

    public long getTimeoutTs() {
        return timeoutTs;
    }

    public void setTimeoutTs(long timeoutTs) {
        this.timeoutTs = timeoutTs;
    }

    public AdContent getContent() {
        return content;
    }

    public void setContent(AdContent content) {
        this.content = content;
    }

    /**
     * 是否已经超时
     * @param ts
     * @return
     */
    public boolean isTimeout(long ts){
        return ts > timeoutTs;
    }
}
