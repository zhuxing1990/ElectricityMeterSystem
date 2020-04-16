package com.vunke.electricity.server;

import android.content.Context;

import com.vunke.electricity.server.config.WebConfig;
import com.vunke.electricity.util.LogUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Created by Administrator on 2018-03-22.
 */
public class WebServer implements Runnable {

    private static final String TAG = "WebServer";
    private static WebServer instance;

    private Context mContext;
    private ServerSocket mServerSocket;
    private Thread mServerThread;

    private WebServer(Context context) throws IOException {
        mServerSocket = new ServerSocket(WebConfig.SERVER_PORT);
        mContext = context;
    }

    /**
     * 启动服务
     *
     * @param context
     * @return 服务器实例，任何时候只都有一个服务器实例
     * @throws IOException 当端口被占用时抛出
     */
    public static synchronized WebServer startServer(Context context) throws IOException {
        if (instance == null) {
            instance = new WebServer(context);
            WebConfig.init(context);
            instance.start0();
            return instance;
        }
        return null;
    }

    public static synchronized void stopServer() {
        if (instance != null) {
            try {
                instance.stop0();
            } catch (IOException e) {
                LogUtil.e(TAG, "stopServer: ", e);
            }
            instance = null;
        }
    }

    private void start0() {
        if (mServerThread == null) {
            mServerThread = new Thread(this);
            mServerThread.start();
        }
    }

    private void stop0() throws IOException {
        if (mServerThread == null) {
            return;
        }

        mServerThread.interrupt();
        mServerSocket.close();
    }

    @Override
    public void run() {
        Socket socket;

        while (true) {
            try {
                //收到连接请求
                socket = mServerSocket.accept();
//                socket.setKeepAlive(true);
                socket.setSoTimeout(5*1000);
                // 发送数据包，默认为 false，即客户端发送数据采用 Nagle 算法；
                // 但是对于实时交互性高的程序，建议其改为 true，即关闭 Nagle 算法，客户端每发送一次数据，无论数据包大小都会将这些数据发送出去
                socket.setTcpNoDelay(true);
                // 设置客户端 socket 关闭时，close() 方法起作用时延迟 30 秒关闭，如果 30 秒内尽量将未发送的数据包发送出去
                socket.setSoLinger(true, 30);
                // 设置输出流的发送缓冲区大小，默认是4KB，即4096字节
                socket.setSendBufferSize(4096);
                // 设置输入流的接收缓冲区大小，默认是4KB，即4096字节
                socket.setReceiveBufferSize(4096);
            } catch (IOException e) {
                LogUtil.e(TAG, "run: ", e);
                continue;
            }
            WebSession session = new WebSession(mContext, socket);
            session.start();
        }
    }
}
