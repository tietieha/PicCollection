package com.example.aliang.piccollection;
import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

/**
 * ImageSwitcher和Gallery如何展示SD卡中的资源图片
 * @author Andy.Chen
 * @email:Chenjunjun.ZJ@gmail.com
 */
public class myGallery extends Activity
        implements OnItemSelectedListener,ViewFactory{
    /**
     * 内置
     */
    static String SDCARD_INTERNAL = "internal";


    /**
     * 外置
     */
    static String SDCARD_EXTERNAL = "external";
    HashMap<String, SDCardInfo> aaaa;
    List<SoftReference<Bitmap>> imageRefLst = new ArrayList<SoftReference<Bitmap>>();
    List<SoftReference<Bitmap>> thumbRefLst = new ArrayList<SoftReference<Bitmap>>();
    BitmapFactory.Options bfOpt;

    private String imgFolder;
    List<File> imgFileList = new ArrayList<File>();

    private static int windowHeight,windowWidth;
    private int galleryindex;
    private List<String> imagePathList;
    private String[] list;
    private ImageSwitcher mSwitcher;
    private Gallery mGallery;
    private String localTempImgFileName;
    private String localDir="基建照片采集";
    private String localcacheDir="cache";
    private String localimgDir="img";

    private ImageButton previous,next,delete;
    private int savePosition;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//没有标题
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        //设置手机屏幕朝向，一共有7种
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //SCREEN_ORIENTATION_BEHIND： 继承Activity堆栈中当前Activity下面的那个Activity的方向
        //SCREEN_ORIENTATION_LANDSCAPE： 横屏(风景照) ，显示时宽度大于高度
        //SCREEN_ORIENTATION_PORTRAIT： 竖屏 (肖像照) ， 显示时高度大于宽度
        //SCREEN_ORIENTATION_SENSOR  由重力感应器来决定屏幕的朝向,它取决于用户如何持有设备,当设备被旋转时方向会随之在横屏与竖屏之间变化
        //SCREEN_ORIENTATION_NOSENSOR： 忽略物理感应器——即显示方向与物理感应器无关，不管用户如何旋转设备显示方向都不会随着改变("unspecified"设置除外)
        //SCREEN_ORIENTATION_UNSPECIFIED： 未指定，此为默认值，由Android系统自己选择适当的方向，选择策略视具体设备的配置情况而定，因此不同的设备会有不同的方向选择
        //SCREEN_ORIENTATION_USER： 用户当前的首选方向
        setContentView(R.layout.activity_my_gallery);

        SDCardUtil aa=new SDCardUtil();
        aaaa = aa.getSDCardInfo(getApplicationContext());

        Intent intent=getIntent();

        savePosition= intent.getIntExtra("savePosition", 0);


        // 获取Android屏幕的服务
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        // 获取屏幕的分辨率，getHeight()、getWidth已经被废弃掉了
        // 应该使用getSize()，但是这里为了向下兼容所以依然使用它们
        windowHeight = wm.getDefaultDisplay().getHeight();
        windowWidth = wm.getDefaultDisplay().getWidth();
        previous= (ImageButton) findViewById(R.id.previous);
        next= (ImageButton) findViewById(R.id.next);
        delete= (ImageButton) findViewById(R.id.delete);




		/* 设定Switcher */
        mSwitcher = (ImageSwitcher) findViewById(R.id.switcher);
        mSwitcher.setFactory(this);
		/* 设定载入Switcher的模式 */
        mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in));
		/* 设定输出Switcher的模式 */
        mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out));
        mSwitcher.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "你点击了ImageSwitch上的图片",
                //       Toast.LENGTH_SHORT).show();

            }

        });

        mGallery = (Gallery) findViewById(R.id.mygallery);

        jiazai();



        mGallery.setOnItemSelectedListener(this);

		/* 设定一个itemclickListener事件 */
        mGallery.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //Toast.makeText(getApplicationContext(), "你点击了Gallery上的图片",
                //      Toast.LENGTH_SHORT).show();
            }
        });

        previous.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                galleryindex--;
                if (galleryindex <=0){
                    galleryindex=0;
                }
                String photoURL = list[galleryindex];
                // Log.i("A", String.valueOf(position));
                Bitmap bit=createImageThumbnail(photoURL);
                Drawable dw=new BitmapDrawable(bit);
                mSwitcher.setImageDrawable(dw);
                //mSwitcher.setImageURI(Uri.parse(photoURL));

            }
        });

        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryindex++;
                if (galleryindex > list.length-1 ) {
                    galleryindex = list.length - 1;
                }
                String photoURL = list[galleryindex];
                // Log.i("A", String.valueOf(position));
                Bitmap bit=createImageThumbnail(photoURL);
                Drawable dw=new BitmapDrawable(bit);
                mSwitcher.setImageDrawable(dw);
                //mSwitcher.setImageURI(Uri.parse(photoURL));
            }
        });

        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                File deletefile=new File(list[galleryindex]);
                deletefile.delete();
                jiazai();
            }
        });
    }

    private void jiazai() {
        imagePathList=getImagePathFromSD();
        list = imagePathList.toArray(new String[imagePathList.size()]);
		/* 新增几ImageAdapter并设定给Gallery对象 */
        mGallery.setAdapter(new ImageAdapter(this, imagePathList));
        if(list.length<=1){
            next.setEnabled(false);
            previous.setEnabled(false);
            delete.setEnabled(false);
        }

    }

    private void getImagePath() {
        //根据自己的需求读取SDCard中的资源图片的路径
        String imagePath = Environment.getExternalStorageDirectory() + "/" +localDir+"/"+localimgDir+"/";
        imgFileList.addAll(Arrays.asList(new File(imagePath).listFiles()) );
        for (File f:imgFileList){
            if (".nomedia".equals(f.getName())){
                imgFileList.remove(f);
                break;
            }
        }
        //Log.d(LOG_TAG, "Image count :"+imgFileList.size());
        //防止大图片内存不足
        bfOpt =  new BitmapFactory.Options();
//			bfOpt.inTempStorage = new byte[1024*1024*10]; //10MB的临时存储空间
        bfOpt.inSampleSize = 4;
    }
    /** 从SD卡中获取资源图片的路径 */
    private List<String> getImagePathFromSD() {
		/* 设定目前所在路径 */
        List<String> it = new ArrayList<String>();
        String imagePath = null;
        if(savePosition==0) {
            //根据自己的需求读取SDCard中的资源图片的路径
            imagePath =aaaa.get(SDCARD_INTERNAL).getMountPoint() + "/" + localDir + "/";
        }else{
            imagePath =aaaa.get(SDCARD_EXTERNAL).getMountPoint() + "//Android/data/com.example.aliang.piccollection/" + localDir + "/";
        }
        File mFile = new File(imagePath);
        File[] files = mFile.listFiles();
        //
        //galleryindex=files.length-1;
		/* 将所有文件存入ArrayList中 */
        if(files!=null) {
            galleryindex=files.length-1;

            for (int i = files.length - 1; i >= 0; i--) {
                File file = files[i];
                if (checkIsImageFile(file.getPath()))
                    it.add(file.getPath());
            }
        }else {
            Toast.makeText(getApplicationContext(),"没有图片",Toast.LENGTH_SHORT).show();
        }
        return it;
    }

    /** 判断是否相应的图片格式  */
    private boolean checkIsImageFile(String fName) {
        boolean isImageFormat;

		/* 取得扩展名 */
        String end = fName
                .substring(fName.lastIndexOf(".") + 1, fName.length())
                .toLowerCase();

		/* 按扩展名的类型决定MimeType */
        if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            isImageFormat = true;
        } else {
            isImageFormat = false;
        }
        return isImageFormat;
    }

    /* 改写BaseAdapter自定义一ImageAdapter class */
    public class ImageAdapter extends BaseAdapter {
        /* 声明变量 */
        int mGalleryItemBackground;
        private Context mContext;
        private List<String> lis;

        /* ImageAdapter的构造符 */
        public ImageAdapter(Context c, List<String> li) {
            mContext = c;
            lis = li;
			/*
			 * 使用res/values/attrs.xml中的<declare-styleable>定义 的Gallery属性.
			 */
            TypedArray mTypeArray = obtainStyledAttributes(R.styleable.Gallery);
			/* 取得Gallery属性的Index id */
            mGalleryItemBackground = mTypeArray.getResourceId(
                    R.styleable.Gallery_android_galleryItemBackground, 0);
			/* 让对象的styleable属性能够反复使用 */
            mTypeArray.recycle();
        }

        /* 重写的方法getCount,传回图片数目 */
        public int getCount() {
            return lis.size();
        }

        /* 重写的方法getItem,传回position */
        public Object getItem(int position) {
            return position;
        }

        /* 重写的方法getItemId,传并position */
        public long getItemId(int position) {
            return position;
        }

        /* 重写方法getView,传并几View对象 */
        public View getView(int position, View convertView, ViewGroup parent) {
			/* 产生ImageView对象 */
            ImageView i = new ImageView(mContext);
			/* 设定图片给imageView对象 */


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize=2;//图片高宽度都为原来的二分之一，即图片大小为原来的大小的四分之一
            options.inTempStorage = new byte[5*1024]; //设置16MB的临时存储空间（不过作用还没看出来，待验证）



            Bitmap bm = BitmapFactory.decodeFile(lis.get(position).toString(),options);
            i.setImageBitmap(bm);
			/* 重新设定图片的宽高 */
            i.setScaleType(ImageView.ScaleType.FIT_XY);
			/* 重新设定Layout的宽高 */
            i.setLayoutParams(new Gallery.LayoutParams(136, 88));
			/* 设定Gallery背景图 */
            i.setBackgroundResource(mGalleryItemBackground);
			/* 传回imageView对象 */
            return i;
        }
    }

    @Override
    public View makeView() {
        ImageView iv = new ImageView(this);
        iv.setBackgroundColor(0xFF000000);
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iv.setLayoutParams(new ImageSwitcher.LayoutParams(
                LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
        return iv;
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        // TODO Auto-generated method stub
        String photoURL = list[position];
        Log.i("A", String.valueOf(position));
        galleryindex=position;
        Bitmap bit=createImageThumbnail(photoURL);
        Drawable dw=new BitmapDrawable(bit);
        mSwitcher.setImageDrawable(dw);
        //mSwitcher.setImageURI(Uri.parse(photoURL));
        /*ImageView image = (ImageView)mSwitcher.getNextView();
        image.setImageBitmap(this.getImage(position));*/
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                Intent intent=new Intent();
                intent.setClass(myGallery.this, index.class);
                startActivity(intent);
                finish();
                break;
        }

        return super.onKeyUp(keyCode, event);
    }

    public static Bitmap createImageThumbnail(String filePath){
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);





        opts.inSampleSize = computeSampleSize(opts, -1, windowWidth*windowHeight);
        opts.inJustDecodeBounds = false;

        try {
            bitmap = BitmapFactory.decodeFile(filePath, opts);
        }catch (Exception e) {
            // TODO: handle exception
        }







        return bitmap;
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :(int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
// DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
// ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

// TODO handle non-primary volumes
            }
// DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
// MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
// MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

// Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
// File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }



    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}