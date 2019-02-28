package com.king.zxing;

/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.king.zxing.camera.CameraManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This activity opens the camera and does the actual scanning on a background thread. It draws a
 * viewfinder to help the user place the barcode correctly, shows feedback as the image processing
 * is happening, and then overlays the results when a scan is successful.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public class CaptureActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    public static final String KEY_RESULT = Intents.Scan.RESULT;

    private static final String TAG = CaptureActivity.class.getSimpleName();

//    private static final long DEFAULT_INTENT_RESULT_DURATION_MS = 1500L;
//    private static final long BULK_MODE_SCAN_DELAY_MS = 1000L;

    private static final int DEVIATION = 6;

    private static final String[] ZXING_URLS = {"http://zxing.appspot.com/scan", "zxing://scan/"};

//    private static final Collection<ResultMetadataType> DISPLAYABLE_METADATA_TYPES =
//            EnumSet.of(ResultMetadataType.ISSUE_NUMBER,
//                    ResultMetadataType.SUGGESTED_PRICE,
//                    ResultMetadataType.ERROR_CORRECTION_LEVEL,
//                    ResultMetadataType.POSSIBLE_COUNTRY);

    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private Result savedResultToShow;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, ?> decodeHints;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private AmbientLightManager ambientLightManager;

    /**
     * 是否支持缩放（变焦），默认支持
     */
    private boolean isZoom = true;
    private float oldDistance;

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public BeepManager getBeepManager() {
        return beepManager;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(getLayoutId());
        hasSurface = false;

    }


    public int getLayoutId() {
        return 0;
    }

    public int getViewFinderViewId() {
        return R.id.viewfinder_view;
    }

    public int getPreviewViewId() {
        return R.id.preview_view;
    }

    /**
     * 扫码后声音与震动管理里入口
     *
     * @return true表示播放和震动，是否播放与震动还得看{@link BeepManager}
     */
    public boolean isBeepSoundAndVibrate() {
        return true;
    }

    private boolean isFirstVisiable = true;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isFirstVisiable) {
            new Handler().postDelayed(new Runnable() {
             @Override
             public void run() {
                 processVisiableThings();
             }
         },280);
        }
        isFirstVisiable = false;
    }

    private void processIntent() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (null != beepManager) beepManager.updatePrefs();
        if (null != ambientLightManager) ambientLightManager.start(cameraManager);
        if (null != inactivityTimer) inactivityTimer.onResume();
        handler = null;
        decodeFormats = null;
        characterSet = null;
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            String dataString = intent.getDataString();
            if (Intents.Scan.ACTION.equals(action)) {
                // Scan the formats the intent requested, and return the result to the calling activity.
//                source = IntentSource.NATIVE_APP_INTENT;
                decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
                decodeHints = DecodeHintManager.parseDecodeHints(intent);

                if (intent.hasExtra(Intents.Scan.WIDTH) && intent.hasExtra(Intents.Scan.HEIGHT)) {
                    int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
                    int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
                    if (width > 0 && height > 0) {
                        cameraManager.setManualFramingRect(width, height);
                    }
                }
                if (intent.hasExtra(Intents.Scan.CAMERA_ID)) {
                    int cameraId = intent.getIntExtra(Intents.Scan.CAMERA_ID, -1);
                    if (cameraId >= 0) {
                        cameraManager.setManualCameraId(cameraId);
                    }
                }
            } else if (dataString != null &&
                    dataString.contains("http://www.google") &&
                    dataString.contains("/m/products/scan")) {
                // Scan only products and send the result to mobile Product Search.
//                source = IntentSource.PRODUCT_SEARCH_LINK;
//                sourceUrl = dataString;
                decodeFormats = DecodeFormatManager.PRODUCT_FORMATS;
            } else if (isZXingURL(dataString)) {
                // Scan formats requested in query string (all formats if none specified).
                // If a return URL is specified, send the results there. Otherwise, handle it ourselves.
//                source = IntentSource.ZXING_LINK;
//                sourceUrl = dataString;
                Uri inputUri = Uri.parse(dataString);
//                scanFromWebPageManager = new ScanFromWebPageManager(inputUri);
                decodeFormats = DecodeFormatManager.parseDecodeFormats(inputUri);
                // Allow a sub-set of the hints to be specified by the caller.
                decodeHints = DecodeHintManager.parseDecodeHints(inputUri);
            }
            characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);
        }
        resetStatusView();

    }

    public void processVisiableThings() {

    }


    @Override
    protected void onResume() {
        super.onResume();
        // CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
        // want to open the camera driver and measure the screen size if we're going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the wrong size and partially
        // off screen.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null == cameraManager) cameraManager = new CameraManager(getApplication());
                SurfaceView surfaceView = findViewById(getPreviewViewId());
                surfaceView.setVisibility(View.VISIBLE);
                viewfinderView = findViewById(getViewFinderViewId());
                viewfinderView.setVisibility(View.VISIBLE);
                viewfinderView.setCameraManager(cameraManager);
                SurfaceHolder surfaceHolder = surfaceView.getHolder();
                if (hasSurface) {
                    // The activity was paused but not stopped, so the surface still exists. Therefore
                    // surfaceCreated() won't be called, so init the camera here.
                    initCamera(surfaceHolder);
                } else {
                    // Install the callback and wait for surfaceCreated() to init the camera.
                    surfaceHolder.addCallback(CaptureActivity.this);
                }
                if (null == inactivityTimer) inactivityTimer = new InactivityTimer(CaptureActivity.this);
                if (null == beepManager) beepManager = new BeepManager(CaptureActivity.this);
                if (null == ambientLightManager) ambientLightManager = new AmbientLightManager(CaptureActivity.this);
                processIntent();
            }
        },280);

    }

//    private int getCurrentOrientation() {
//        int rotation = getWindowManager().getDefaultDisplay().getRotation();
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            switch (rotation) {
//                case Surface.ROTATION_0:
//                case Surface.ROTATION_90:
//                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
//                default:
//                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
//            }
//        } else {
//            switch (rotation) {
//                case Surface.ROTATION_0:
//                case Surface.ROTATION_270:
//                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//                default:
//                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
//            }
//        }
//    }

    private static boolean isZXingURL(String dataString) {
        if (dataString == null) {
            return false;
        }
        for (String url : ZXING_URLS) {
            if (dataString.startsWith(url)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        if(null!=inactivityTimer)inactivityTimer.onPause();
        if (null != ambientLightManager) ambientLightManager.stop();
        if (null != beepManager) beepManager.close();
        if(null!=cameraManager)cameraManager.closeDriver();
        //historyManager = null; // Keep for onActivityResult
        if (!hasSurface) {
            SurfaceView surfaceView = findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
//                if (source == IntentSource.NATIVE_APP_INTENT) {
//                    setResult(RESULT_CANCELED);
//                    finish();
//                    return true;
//                }
//                if ((source == IntentSource.NONE || source == IntentSource.ZXING_LINK) && lastResult != null) {
//                    restartPreviewAfterDelay(0L);
//                    return true;
//                }
                break;
            case KeyEvent.KEYCODE_FOCUS:
            case KeyEvent.KEYCODE_CAMERA:
                // Handle these events so they don't launch the Camera app
                return true;
            // Use volume up/down to turn on light
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if(null!=cameraManager)cameraManager.setTorch(false);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if(null!=cameraManager)cameraManager.setTorch(true);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
        // Bitmap isn't used yet -- will be used soon
        if (handler == null) {
            savedResultToShow = result;
        } else {
            if (result != null) {
                savedResultToShow = result;
            }
            if (savedResultToShow != null) {
                Message message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow);
                handler.sendMessage(message);
            }
            savedResultToShow = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // do nothing
    }

    /**
     * A valid barcode has been found, so give an indication of success and show the results.
     *
     * @param rawResult   The contents of the barcode.
     * @param scaleFactor amount by which thumbnail was scaled
     * @param barcode     A greyscale bitmap of the camera data which was decoded.
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        if(null!=inactivityTimer)inactivityTimer.onActivity();
        if (isBeepSoundAndVibrate()) {
            if (null != beepManager) beepManager.playBeepSoundAndVibrate();
        }
        String resultString = rawResult.getText();
        Intent intent = new Intent();
        intent.putExtra(KEY_RESULT, resultString);
        setResult(RESULT_OK, intent);
        finish();


//        ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);

//        boolean fromLiveScan = barcode != null;
//        if (fromLiveScan) {
////            historyManager.addHistoryItem(rawResult, resultHandler);
//            // Then not from history, so beep/vibrate and we have an image to draw on
//            beepManager.playBeepSoundAndVibrate();
//            drawResultPoints(barcode, scaleFactor, rawResult);
//        }


//        switch (source) {
//            case NATIVE_APP_INTENT:
//            case PRODUCT_SEARCH_LINK:
//                handleDecodeExternally(rawResult, resultHandler, barcode);
//                break;
//            case ZXING_LINK:
//                if (scanFromWebPageManager == null || !scanFromWebPageManager.isScanFromWebPage()) {
//                    handleDecodeInternally(rawResult, resultHandler, barcode);
//                } else {
//                    handleDecodeExternally(rawResult, resultHandler, barcode);
//                }
//                break;
//            case NONE:
//                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//                if (fromLiveScan && prefs.getBoolean(PreferencesActivity.KEY_BULK_MODE, false)) {
//                    Toast.makeText(getApplicationContext(),
//                            getResources().getString(R.string.msg_bulk_mode_scanned) + " (" + rawResult.getText() + ')',
//                            Toast.LENGTH_SHORT).show();
//                    maybeSetClipboard(resultHandler);
//                    // Wait a moment or else it will scan the same barcode continuously about 3 times
//                    restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
//                } else {
//                    handleDecodeInternally(rawResult, resultHandler, barcode);
//                }
//                break;
//        }
    }


    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
            }
            decodeOrStoreSavedBitmap(null, null);
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
//            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
//            displayFrameworkBugMessageAndExit();
        }
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
        resetStatusView();
    }

    private void resetStatusView() {
        viewfinderView.setVisibility(View.VISIBLE);
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isZoom && cameraManager.isOpen()) {
            Camera camera = cameraManager.getOpenCamera().getCamera();
            if (camera == null) {
                return super.onTouchEvent(event);
            }
            if (event.getPointerCount() == 1) {//单点触控，聚焦
//                focusOnTouch(event,camera);
            } else {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {//多点触控
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDistance = calcFingerSpacing(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float newDistance = calcFingerSpacing(event);

                        if (newDistance > oldDistance + DEVIATION) {//
                            handleZoom(true, camera);
                        } else if (newDistance < oldDistance - DEVIATION) {
                            handleZoom(false, camera);
                        }
                        oldDistance = newDistance;
                        break;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public boolean isZoom() {
        return isZoom;
    }

    public void setZoom(boolean zoom) {
        isZoom = zoom;
    }

    /**
     * 处理变焦缩放
     *
     * @param isZoomIn
     * @param camera
     */
    private void handleZoom(boolean isZoomIn, Camera camera) {
        Camera.Parameters params = camera.getParameters();
        if (params.isZoomSupported()) {
            int maxZoom = params.getMaxZoom();
            int zoom = params.getZoom();
            if (isZoomIn && zoom < maxZoom) {
                zoom++;
            } else if (zoom > 0) {
                zoom--;
            }
            params.setZoom(zoom);
            camera.setParameters(params);
        } else {
            Log.i(TAG, "zoom not supported");
        }
    }

    /**
     * 聚焦
     *
     * @param event
     * @param camera
     */
    public void focusOnTouch(MotionEvent event, Camera camera) {

        Camera.Parameters params = camera.getParameters();
        Camera.Size previewSize = params.getPreviewSize();

        Rect focusRect = calcTapArea(event.getRawX(), event.getRawY(), 1f, previewSize);
        Rect meteringRect = calcTapArea(event.getRawX(), event.getRawY(), 1.5f, previewSize);
        Camera.Parameters parameters = camera.getParameters();
        if (parameters.getMaxNumFocusAreas() > 0) {
            List<Camera.Area> focusAreas = new ArrayList<>();
            focusAreas.add(new Camera.Area(focusRect, 600));
            parameters.setFocusAreas(focusAreas);
        }

        if (parameters.getMaxNumMeteringAreas() > 0) {
            List<Camera.Area> meteringAreas = new ArrayList<>();
            meteringAreas.add(new Camera.Area(meteringRect, 600));
            parameters.setMeteringAreas(meteringAreas);
        }
        final String currentFocusMode = params.getFocusMode();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
        camera.setParameters(params);

        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                Camera.Parameters params = camera.getParameters();
                params.setFocusMode(currentFocusMode);
                camera.setParameters(params);
            }
        });

    }

    /**
     * 计算两指间距离
     *
     * @param event
     * @return
     */
    private float calcFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 计算对焦区域
     *
     * @param x
     * @param y
     * @param coefficient
     * @param previewSize
     * @return
     */
    private Rect calcTapArea(float x, float y, float coefficient, Camera.Size previewSize) {
        float focusAreaSize = 200;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        int centerX = (int) ((x / previewSize.width) * 2000 - 1000);
        int centerY = (int) ((y / previewSize.height) * 2000 - 1000);
        int left = clamp(centerX - (areaSize / 2), -1000, 1000);
        int top = clamp(centerY - (areaSize / 2), -1000, 1000);
        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        return new Rect(Math.round(rectF.left), Math.round(rectF.top),
                Math.round(rectF.right), Math.round(rectF.bottom));
    }

    /**
     * @param x
     * @param min
     * @param max
     * @return
     */
    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }


}