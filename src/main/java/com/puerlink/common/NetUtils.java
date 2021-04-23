package com.puerlink.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.InputStream;
import java.util.Date;

public class NetUtils {

	public enum NetState
	{
		Disconnected,
		MobileConnected,
		WifiConnected
	}
	
	public interface NetworkTestCallback
	{
		void onDone(NetState state);
	}
	
	public static NetState getNetworkState(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.isConnected())
		{
			if (ni.getType() == ConnectivityManager.TYPE_MOBILE)
			{
				return NetState.MobileConnected;
			}
			else if (ni.getType() == ConnectivityManager.TYPE_WIFI)
			{
				return NetState.WifiConnected;
			}
		}
		return NetState.Disconnected;
	}
	
	private static final String CHARSET = HTTP.UTF_8;
	private static HttpClient S_HTTP_CLIENT;
	
	private static synchronized HttpClient getHttpClient() {
        if (null== S_HTTP_CLIENT) {
            HttpParams params =new BasicHttpParams();
            // 设置基本参数
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, CHARSET);
            HttpProtocolParams.setUseExpectContinue(params, false);
            HttpProtocolParams.setUserAgent(params,
                            "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
                                    +"AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");

            ConnManagerParams.setMaxTotalConnections(params, 300);
            ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(300));

            // 超时设置
            /* 从连接池中取连接的超时设置*/
            ConnManagerParams.setTimeout(params, 1000 * 3);
            /* 连接超时 */
            HttpConnectionParams.setConnectionTimeout(params, 1000 * 3);
            /* 请求超时 */
            HttpConnectionParams.setSoTimeout(params, 1000 * 3);
            
            // 设置我们的HttpClient支持HTTP和HTTPS两种模式
            SchemeRegistry schReg =new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

            // 使用线程安全的连接管理来创建HttpClient
            ClientConnectionManager conMgr =new ThreadSafeClientConnManager(params, schReg);
            S_HTTP_CLIENT = new DefaultHttpClient(conMgr, params);
        }
        return S_HTTP_CLIENT;
    }
	
	private static boolean S_NETWORK_CONNECTED = false;
	private static Date S_NETWORK_CHECK_TIME = new Date();
	private static int S_NETWORK_CHECK_INTERVAL = 1000 * 60 * 3;
	private static String S_CHECK_URL = "http://www.5idouniwan.com/test.html";
	public static void test(Context context, NetworkTestCallback callback)
	{
		final Context c = context;
		final NetworkTestCallback cb = callback;

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try
				{
					ConnectivityManager cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
					final NetworkInfo ni = cm.getActiveNetworkInfo();
					if (ni != null && ni.isConnected())
					{
						if (ni.isAvailable() && ni.isConnected())
						{
							NetState ns = NetState.Disconnected;
							if (ni.getType() == ConnectivityManager.TYPE_MOBILE)
							{
								ns = NetState.MobileConnected;
							}
							else if (ni.getType() == ConnectivityManager.TYPE_WIFI)
							{
								ns = NetState.WifiConnected;
							}
							
							Date currTime = new Date();
							if (!S_NETWORK_CONNECTED || 
									currTime.getTime() - S_NETWORK_CHECK_TIME.getTime() > S_NETWORK_CHECK_INTERVAL)
							{
								HttpClient client = getHttpClient();
								HttpGet req = new HttpGet(S_CHECK_URL);
								InputStream is = null;
								try {
									HttpResponse resp = client.execute(req);
									HttpEntity entity = resp.getEntity();
									if (entity != null)
									{
										is = entity.getContent();
									}
									
									S_NETWORK_CONNECTED = resp != null && resp.getStatusLine().getStatusCode() == 200;
									
									if (S_NETWORK_CONNECTED)
									{
										S_NETWORK_CHECK_TIME = currTime;
										
										if (cb != null)
										{
											cb.onDone(ns);
										}
									}
									else
									{
										if (cb != null)
										{
											cb.onDone(NetState.Disconnected);
										}
									}
								}
								catch (Exception exp)
								{
									S_NETWORK_CONNECTED = false;
									
									if (cb != null)
									{
										cb.onDone(NetState.Disconnected);
									}
								}
								finally
								{
									if (is != null)
									{
										try
										{
											is.close();
										}
										catch (Exception exp)
										{
										}
									}
									
									client = null;
								}
							}
							else
							{
								if (cb != null)
								{
									if (S_NETWORK_CONNECTED)
									{
										cb.onDone(ns);
									}
									else
									{
										cb.onDone(NetState.Disconnected);
									}
								}
							}
						}
						else
						{
							if (cb != null)
							{
								cb.onDone(NetState.Disconnected);
							}
						}
					}
					else
					{
						if (cb != null)
						{
							cb.onDone(NetState.Disconnected);
						}
					}
				}
				catch (Exception exp)
				{
					if (cb != null)
					{
						cb.onDone(NetState.Disconnected);
					}
				}
			}
		});
		t.start();
	}
	
}
