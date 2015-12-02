package com.example.nookie.demotivatorsmaker;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;

import com.example.nookie.demotivatorsmaker.models.Demotivator;
import com.example.nookie.demotivatorsmaker.models.RVItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static FileManager instance = new FileManager();
    public static final String SAVE_TARGET_SYSTEM_PATH_EXT = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    public static final String SAVE_TARGET_FOLDER_NAME = "my dems";
    public static final String PREFERENCE_NAME = App.getAppContext().getPackageName()+"_pref";
    public static final String PREFERENCE_IMAGE_ID = "ID";
    public static final String DEFAULT_FILENAME_PREFIX = "mydem_";
    public static final String DEFAULT_EXT = ".jpg";


    private FileManager(){
    }

    private File getFolder(){
        File targetDir = new File(SAVE_TARGET_SYSTEM_PATH_EXT,SAVE_TARGET_FOLDER_NAME);
        return targetDir;
    }


    public List<RVItem> queryFiles(){
        ArrayList<RVItem> data = new ArrayList<>();
        File targetFile = new File(getFolder().getAbsolutePath());
        File[] files = targetFile.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".jpg");
            }
        });

        for (File file : files) {
            RVItem item = new RVItem(Uri.fromFile(file));
            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file.getAbsolutePath()), dpToPx(100), dpToPx(100));
            item.setThumbnail(thumbnail);
            data.add(item);
        }

        /*ContentResolver cr = App.getAppContext().getContentResolver();

        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] imagesProjection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
        String imagesSelection = MediaStore.Images.Media.DATA + " LIKE ? ";
        Cursor images = cr.query(mImageUri,imagesProjection,imagesSelection,new String[]{targetFolder+"%"},null);
        for (images.moveToFirst();!images.isAfterLast();images.moveToNext()){
            int imageID = images.getInt(images.getColumnIndex(MediaStore.Images.Media._ID));
            String filename = images.getString(images.getColumnIndex(MediaStore.Images.Media.DATA));
            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(filename), dpToPx(100), dpToPx(100));
            RVItem item = new RVItem(Uri.parse(filename),thumbnail);

            data.add(item);
        }*/

        return data;
    }

    private static boolean externalStorageReadable, externalStorageWritable;

    public static boolean isExternalStorageReadable() {
        checkStorage();
        return externalStorageReadable;
    }

    public static boolean isExternalStorageWritable() {
        checkStorage();
        return externalStorageWritable;
    }

    public static boolean isExternalStorageReadableAndWritable() {
        checkStorage();
        return externalStorageReadable && externalStorageWritable;
    }

    private static void checkStorage() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            externalStorageReadable = externalStorageWritable = true;
        } else if (state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            externalStorageReadable = true;
            externalStorageWritable = false;
        } else {
            externalStorageReadable = externalStorageWritable = false;
        }
    }

    public static FileManager getInstance(){
        return instance;
    }

    public Uri saveDem(Demotivator demotivator) throws ExternalStorageNotReadyException, DirectoryCreationFailed {

        Bitmap result = demotivator.toBitmap();

        if (!isExternalStorageWritable()){
            throw new ExternalStorageNotReadyException();
        }

        File targetDir = getFolder();
        createFolderIfNeeded();

        File f=new File("tmp");
        boolean nameAvailable=false;
        while (!nameAvailable) {
            String filename = generateFilename();
            f = new File(targetDir, filename);
            nameAvailable = !f.exists();
        }

        Uri fileUri = Uri.fromFile(f);
        try {
            FileOutputStream fos = new FileOutputStream(f);
            result.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            updateMediaScanner(fileUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileUri;
    }

    private String generateFilename() {
        Context context = App.getAppContext();
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        int id = preferences.getInt(PREFERENCE_IMAGE_ID, 0);

        SharedPreferences.Editor ed = preferences.edit();
        ed.putInt(PREFERENCE_IMAGE_ID, ++id);
        ed.apply();

        String filename = DEFAULT_FILENAME_PREFIX + String.valueOf(id) + DEFAULT_EXT;

        return filename;
    }


    private void createFolderIfNeeded() throws DirectoryCreationFailed {
        File folder = getFolder();
        if (!folder.exists()){
            boolean success = folder.mkdirs();
            if (!success){
                throw new DirectoryCreationFailed();
            }
        }
    }

    public void updateMediaScanner(Uri file){
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED,file);
        App.getAppContext().sendBroadcast(scanIntent);
    }

    public class ExternalStorageNotReadyException extends Throwable{
        public ExternalStorageNotReadyException() {
            super(App.getStringResource(R.string.error_storage_not_ready));
        }
    }

    public class DirectoryCreationFailed extends Throwable{
        public DirectoryCreationFailed() {
            super(App.getStringResource(R.string.error_directory_not_created));
        }
    }


    private int dpToPx(int dp){
        return (int)(dp * Resources.getSystem().getDisplayMetrics().density);
    }

}