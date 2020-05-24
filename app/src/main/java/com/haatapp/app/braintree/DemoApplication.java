//package com.shopjinu.app.braintree;
//
//import android.app.Application;
//import android.content.Context;
//import android.os.StrictMode;
//import android.support.compat.BuildConfig;
//
//
//import com.shopjinu.app.braintree.internal.ApiClient;
//import com.shopjinu.app.braintree.internal.ApiClientRequestInterceptor;
////import com.lukekorth.mailable_log.MailableLog;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
////import retrofit.RestAdapter;
//
//import java.lang.Thread.UncaughtExceptionHandler;
//
//
//
//public class DemoApplication extends Application implements UncaughtExceptionHandler {
//
//    private static ApiClient sApiClient;
//
//    private UncaughtExceptionHandler mDefaultExceptionHandler;
//
//    @Override
//    public void onCreate() {
//        if (BuildConfig.DEBUG) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                    .detectCustomSlowCalls()
//                    .detectNetwork()
//                    .penaltyLog()
//                    .penaltyDeath()
//                    .build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                    .detectActivityLeaks()
//                    .detectLeakedClosableObjects()
//                    .detectLeakedRegistrationObjects()
//                    .detectLeakedSqlLiteObjects()
//                    .penaltyLog()
//                    .penaltyDeath()
//                    .build());
//        }
//
//        super.onCreate();
//
//        if (Settings.getVersion(this) != BuildConfig.VERSION_CODE) {
//            Settings.setVersion(this);
//            MailableLog.clearLog(this);
//        }
//        MailableLog.init(this, BuildConfig.DEBUG);
//
////        DeveloperTools.setup(this);
//
//        mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
//        Thread.setDefaultUncaughtExceptionHandler(this);
//    }
//
//    @Override
//    public void uncaughtException(Thread thread, Throwable ex) {
//        Logger logger = LoggerFactory.getLogger("Exception");
//
//        logger.error("thread.toString(): " + thread.toString());
//        logger.error("Exception: " + ex.toString());
//        logger.error("Exception stacktrace:");
//        for (StackTraceElement trace : ex.getStackTrace()) {
//            logger.error(trace.toString());
//        }
//
//        logger.error("");
//
//        logger.error("cause.toString(): " + ex.getCause().toString());
//        logger.error("Cause: " + ex.getCause().toString());
//        logger.error("Cause stacktrace:");
//        for (StackTraceElement trace : ex.getCause().getStackTrace()) {
//            logger.error(trace.toString());
//        }
//
//        mDefaultExceptionHandler.uncaughtException(thread, ex);
//    }
//
//    static ApiClient getApiClient(Context context) {
//        if (sApiClient == null) {
//            sApiClient = new RestAdapter.Builder()
//                    .setEndpoint(Settings.getEnvironmentUrl(context))
//                    .setRequestInterceptor(new ApiClientRequestInterceptor())
//                    .build()
//                    .create(ApiClient.class);
//        }
//
//        return sApiClient;
//    }
//
//    static void resetApiClient() {
//        sApiClient = null;
//    }
//}
