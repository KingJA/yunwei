package com.tdr.yunwei.update;

import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;
import org.ksoap2.transport.ServiceConnectionSE;

import java.io.IOException;

/**
 * Created by Linus_Xie on 2016/3/12.
 */
public class MyHttpTransportSE extends HttpTransportSE {

    private int timeout = 60000; // 默认超时时间为20s

    public MyHttpTransportSE(String url) {
        super(url);
    }

    public MyHttpTransportSE(String url, int timeout) {
        super(url);
        this.timeout = timeout;
    }

    // 尤其注意此方法
    protected ServiceConnection getServiceConnection() throws IOException {
        ServiceConnectionSE serviceConnection = new ServiceConnectionSE(
                this.url, timeout);
        return serviceConnection;
    }
}
