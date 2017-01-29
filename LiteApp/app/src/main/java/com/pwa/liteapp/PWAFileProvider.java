package com.pwa.liteapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by anukalp on 9/1/17.
 */
public class PWAFileProvider extends ContentProvider {

    private static final String TAG = "PWAFileProvider";
    private static final int BACKGROUND_TASK_INITIALIZE = 0;
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) {

        Log.d(TAG, "fetching: " + uri);

        String path = getContext().getFilesDir().getAbsolutePath() + "/" + uri.getPath();
        File file = new File(path);
        ParcelFileDescriptor parcel = null;
        try {
            parcel = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "uri " + uri.toString(), e);
        }
        return parcel;
    }


    @Override
    public boolean onCreate() {
        initialize();
        return true;
    }

    private void initialize() {
        mBackgroundThread = new HandlerThread("ContactsProviderWorker",
                Process.THREAD_PRIORITY_BACKGROUND);
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                performBackgroundTask(msg.what, msg.obj);
            }
        };

        scheduleBackgroundTask(BACKGROUND_TASK_INITIALIZE);

    }

    protected void scheduleBackgroundTask(int task) {
        mBackgroundHandler.sendEmptyMessage(task);
    }


    private boolean listAssetFiles(AssetManager assetManager, String path) throws IOException {
        String[] list = assetManager.list(path);
        if (list.length > 0) {
            // This is a folder
            for (String file : list) {
                if (!listAssetFiles(assetManager, path + "/" + file))
                    return false;
            }
        } else {
            int index = path.lastIndexOf("/");
            String filename = path.substring(index + 1, path.length());
            String filepath = path.substring(0, index);
            File internalFile = new File(getContext().getFilesDir(), path);
            if(internalFile.exists()) {
                return false;
            }
            if (!internalFile.getParentFile().exists()) {
                internalFile.getParentFile().mkdirs();
            }
            copyAsset(assetManager,
                    filepath + "/" + filename,
                    internalFile);

        }
        return true;
    }

    private static boolean copyAsset(AssetManager assetManager,
                                     String fromAssetPath, File internalFile) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(fromAssetPath);
            out = new FileOutputStream(internalFile);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private void performBackgroundTask(int what, Object obj) {
        switch (what) {
            case BACKGROUND_TASK_INITIALIZE:
                try {
                    // push some files to the files dir as an example
                   listAssetFiles(getContext().getAssets(), "pwa");
                } catch (Exception e) {
                    Log.e("Main", "error with copying files", e);
                }
                break;
            default:
                break;
        }
    }

    private static final int IO_BUFFER_SIZE = 8 * 1024;

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
