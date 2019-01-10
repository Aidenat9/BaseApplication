package com.github.tianmu19.baselibrary.utils;

import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by 陶海峰 on 2018/5/14 0014.
 */

public class AndroidtoJs extends Object {

    private Context context;

    public AndroidtoJs(Context context) {
        this.context=context;
    }

    // 定义JS需要调用的方法
    // 被JS调用的方法必须加入@JavascriptInterface注解
    @JavascriptInterface
    public void downloadjava(String url) {
        System.out.println(url);
        String[] urls=url.split("\\$#\\$");
        String fileName=urls[1]+urls[0].substring(urls[0].lastIndexOf("."));
        url=urls[0];
        // 指定下载地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
// 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
        request.allowScanningByMediaScanner();
// 设置通知的显示类型，下载进行时和完成后显示通知
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
// 设置通知栏的标题，如果不设置，默认使用文件名
// request.setTitle("This is title");
// 设置通知栏的描述 //
        request.setDescription("This is description");
// 允许在计费流量下下载
        request.setAllowedOverMetered(false);
// 允许该记录在下载管理界面可见
        request.setVisibleInDownloadsUi(false);
// 允许漫游时下载
        request.setAllowedOverRoaming(true);
// 允许下载的网路类型
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
// 设置下载文件保存的路径和文件名
//                String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
//                System.out.println("fileName:{}"+fileName);
//                String fileName=url.substring(url.indexOf("="),url.length());
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
// 另外可选一下方法，自定义下载路径
// request.setDestinationUri()
// request.setDestinationInExternalFilesDir()
        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
// 添加一个下载任务
        long downloadId = downloadManager.enqueue(request);
//                log.debug("downloadId:{}", downloadId);



    }

    @JavascriptInterface
    public void findMap(String address,String longitude,String latitude) {
        if(MapUtil.isInstallByRead("com.baidu.BaiduMap")){
            MapUtil.getBaiduMapUri(this.context,address,longitude,latitude);
        }else if(MapUtil.isInstallByRead("com.autonavi.minimap")){
            double[] locations=MapUtil.bdToGaoDe(Double.parseDouble(longitude),Double.parseDouble(latitude));
            MapUtil.getGaoDeMapUri(this.context,address,locations[0]+"",locations[1]+"");
        }else{
            Toast.makeText(this.context, "您尚未安装百度地图或高德地图", Toast.LENGTH_LONG).show();
//            Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            startActivity(intent);
        }
    }

    @JavascriptInterface
    public void openBrowser(String url) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        // 官方解释 : Name of the component implementing an activity that can display the intent
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(context.getPackageManager());
            // 打印Log   ComponentName到底是什么
            System.out.println("componentName = " + componentName.getClassName());
            context.startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            Toast.makeText(context.getApplicationContext(), "请下载浏览器", Toast.LENGTH_SHORT).show();
        }

    }

}
