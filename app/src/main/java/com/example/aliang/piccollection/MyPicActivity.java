package com.example.aliang.piccollection;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ActionMenuView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MyPicActivity extends AppCompatActivity {
    /**
     * 内置
     */
    static String SDCARD_INTERNAL = "internal";


    /**
     * 外置
     */
    static String SDCARD_EXTERNAL = "external";
    HashMap<String, SDCardInfo> aaaa;
    private String imgfile="";
    private String localTempImgFileName;
    private String localDir="基建照片采集";
    private String localcacheDir="cache";
    private String localimgDir="img";

    private TextView companyName,pic_text_1,pic_text_2,pic_text_3,pic_text_4,pic_text_5,pic_text_6,pic_CurrentTime;
    private LinearLayout pic_plane,pic_save;
    private ImageButton pic_yes,pic_error;

    public ImageView imageview;
    public ImageButton addpic,pic_back;
    private static final int PHOTO_SELECT = 2;
    public static int windowHeight,windowWidth;

    private PreferencesService service;
    private HashMap<String,String> params;

    private int resolutionID;
    private String company,name,type,road;
    public int position,transparent,savePosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_my_pic);
        SDCardUtil aa=new SDCardUtil();
        aaaa = aa.getSDCardInfo(getApplicationContext());


        Intent intent=getIntent();
        resolutionID= intent.getIntExtra("resolution", 0);
        name=intent.getStringExtra("name");
        type=intent.getStringExtra("type");
        road=intent.getStringExtra("road");
        company=intent.getStringExtra("company");
        position=intent.getIntExtra("position", 0);
        savePosition= intent.getIntExtra("savePosition", 0);
        transparent= intent.getIntExtra("transparent", 0);

        // 获取Android屏幕的服务
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        // 获取屏幕的分辨率，getHeight()、getWidth已经被废弃掉了
        // 应该使用getSize()，但是这里为了向下兼容所以依然使用它们
        windowHeight = wm.getDefaultDisplay().getHeight();
        windowWidth = wm.getDefaultDisplay().getWidth();

        initView();
        initEvent();



        pic_text_2.setText(name);
        pic_text_3.setText(type);
        pic_text_4.setText(road);
        companyName.setText(company);
        getCurrentTime();

    }



    //图库选取
    private void MethodPic() {
            Intent intent=new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");

            startActivityForResult(intent, PHOTO_SELECT);

            //startActivityForResult(intent, PHOTO_SELECT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO Auto-generated method stub
        Bitmap bit;

        if(resultCode!=RESULT_OK)
            return;
            switch(requestCode) {

                case PHOTO_SELECT:
                    addpic.setVisibility(View.INVISIBLE);
                    pic_save.setVisibility(View.VISIBLE);
                    imgfile = getPath(this, data.getData());

                    bit = createImageThumbnail(imgfile);
                    imageview.setImageBitmap(bit);



                    break;

                default:
                    break;
            }

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


    private void initEvent() {
        addpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentTime();
                MethodPic();

            }
        });
        pic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(MyPicActivity.this, index.class);
                startActivity(intent);
                finish();
            }
        });

        pic_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap savebit=BitmapFactory.decodeFile(imgfile);
                drawNewBitmap(savebit);
                imageview.setImageBitmap(null);
                pic_save.setVisibility(View.INVISIBLE);
                addpic.setVisibility(View.VISIBLE);
                imgfile="";
            }
        });

        pic_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pic_save.setVisibility(View.INVISIBLE);
                imageview.setImageBitmap(null);
                addpic.setVisibility(View.VISIBLE);
                imgfile="";
            }
        });
        pic_CurrentTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] item = new String[]{"系统时间", "图片时间"};
                new AlertDialog.Builder(MyPicActivity.this)
                        .setTitle("请选择")
                        .setIcon(android.R.drawable.ic_menu_agenda)
                        .setSingleChoiceItems(item, 0,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            getCurrentTime();
                                        } else {
                                            getPicTime();
                                        }

                                        //  Toast.makeText(getApplicationContext(),item[which].toString(),Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                }
                        )
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

    }

    private void initView() {
        imageview= (ImageView) findViewById(R.id.imageView);
        addpic= (ImageButton) findViewById(R.id.addpic);
        pic_back= (ImageButton) findViewById(R.id.pic_back);

        companyName= (TextView) findViewById(R.id.pic_company_content);
        pic_text_1= (TextView) findViewById(R.id.pic_text_1);
        pic_text_2= (TextView) findViewById(R.id.pic_text_2);
        pic_text_3= (TextView) findViewById(R.id.pic_text_3);
        pic_text_4= (TextView) findViewById(R.id.pic_text_4);
        pic_text_5= (TextView) findViewById(R.id.pic_text_5);
        pic_text_6= (TextView) findViewById(R.id.pic_text_6);

        pic_save= (LinearLayout) findViewById(R.id.pic_save);
        pic_plane= (LinearLayout) findViewById(R.id.pic_plane);
        pic_yes= (ImageButton) findViewById(R.id.pic_yes);
        pic_error= (ImageButton) findViewById(R.id.pic_error);

        pic_CurrentTime= (TextView) findViewById(R.id.pic_CurrentTime);


        FrameLayout.LayoutParams lparams= (FrameLayout.LayoutParams) pic_plane.getLayoutParams();
        if(position==0){

        }else {
            lparams.gravity=Gravity.RIGHT;
            lparams.gravity=Gravity.BOTTOM;
            pic_plane.setLayoutParams(lparams);
            //lparams.setMargins(0, windowHeight - pic_plane.getHeight(), windowWidth - pic_plane.getWidth(), 0);
        }
        if(transparent==0){
            pic_plane.getBackground().setAlpha(120);
        }else{
            pic_plane.getBackground().setAlpha(255);
        }
    }
    private void drawNewBitmap(Bitmap src){
        pic_plane.setDrawingCacheEnabled(true);

        Bitmap water=pic_plane.getDrawingCache();


        // 获取原始图片与水印图片的宽与高
        int w = src.getWidth();
        int h = src.getHeight();
        int ww = water.getWidth();
        int wh = water.getHeight();

        float targetW=w/4;
        float scale=targetW/ww;
        //产生ReSize之后的bmp对象
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap waterMak = Bitmap.createBitmap(water, 0, 0, water.getWidth(), water.getHeight(), matrix, true);



        //Log.i("jiangqq", "w = " + w + ",h = " + h + ",ww = " + ww + ",wh = "+ wh);
        Bitmap icon=Bitmap.createBitmap(w, h,Bitmap.Config.ARGB_8888);// 建立一个空的BItMap
        Canvas mCanvas = new Canvas(icon);
        // 往位图中开始画入src原始图片
        mCanvas.drawBitmap(src, 0, 0, null);
        // 在src的左下角添加水印
        Paint paint = new Paint();
        //paint.setAlpha(100);
        mCanvas.drawBitmap(waterMak, 0, h - wh * scale, paint);





        /*// 开始加入文字

            Paint textPaint = new Paint();
           // textPaint.setColor(Color.RED);
            textPaint.setTextSize(Project_CurrentTime.getTextSize());
            String familyName = "宋体";
            Typeface typeface = Typeface.create(familyName,
                    Typeface.BOLD_ITALIC);
            textPaint.setTypeface(typeface);
            textPaint.setTextAlign(Paint.Align.CENTER);
            mCanvas.drawText(Project_CurrentTime.getText().toString(), w-Project_CurrentTime.getWidth()-5, h-Project_CurrentTime.getHeight()-5, textPaint);

*/
        mCanvas.save(Canvas.ALL_SAVE_FLAG);
        mCanvas.restore();
        pic_CurrentTime.setDrawingCacheEnabled(true);
        Bitmap time=pic_CurrentTime.getDrawingCache();
        // 获取字体的宽与高

        int Tw = time.getWidth();
        int Th = time.getHeight();

        float timetargetW=w/4;
        float timescale=timetargetW/Tw;
        Log.i("timescale", timescale + "");
        //产生ReSize之后的bmp对象
        Matrix timematrix = new Matrix();
        timematrix.postScale(timescale, timescale);
        Bitmap timeMask = Bitmap.createBitmap(time, 0, 0, time.getWidth(), time.getHeight(), timematrix, true);

        // 在src的右下角添加时间
        Paint timepaint = new Paint();
        //paint.setAlpha(100);
        mCanvas.drawBitmap(timeMask, w-Tw*timescale-5, h - Th * timescale-5, timepaint);

        mCanvas.save(Canvas.ALL_SAVE_FLAG);
        mCanvas.restore();

        pic_plane.setDrawingCacheEnabled(false);
        pic_CurrentTime.setDrawingCacheEnabled(false);
        saveMyBitmap(icon);
    }

    private void saveMyBitmap(Bitmap icon) {
        if(savePosition==0) {
            //先验证手机是否有sdcard
            SDCardInfo SD = aaaa.get(SDCARD_INTERNAL);
            if(SD.isMounted())
            {
                File dir = null,filedir = null;
                try {
                    //文件名
                    localTempImgFileName=filenameWithTime();
                    filedir = new File(SD.getMountPoint()
                            + "/" +localDir+"/");
                    if(!filedir.exists())filedir.mkdirs();
                    dir = new File(SD.getMountPoint()
                            + "/" + localDir+"/"+ localTempImgFileName);

                    FileOutputStream out=new FileOutputStream(dir);
                    icon.compress(Bitmap.CompressFormat.JPEG,100,out);
                    out.flush();
                    out.close();
                    Toast.makeText(MyPicActivity.this, "保存成功",Toast.LENGTH_SHORT).show();



                } catch (ActivityNotFoundException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(MyPicActivity.this, "没有找到储存目录",Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 其次把文件插入到系统图库
                try {
                    MediaStore.Images.Media.insertImage(getContentResolver(),
                            filedir.getAbsolutePath(), String.valueOf(dir), null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                // 最后通知图库更新
                //this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(filedir+"")));
                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{filedir.toString()}, null, null);

            }else{
                Toast.makeText(MyPicActivity.this, "没有储存卡",Toast.LENGTH_LONG).show();
            }
        }else{
            //先验证手机是否有sdcard
            SDCardInfo SD = aaaa.get(SDCARD_EXTERNAL);
            if(SD.isMounted())
            {
                File dir = null,filedir = null;
                try {
                    //文件名
                    localTempImgFileName=filenameWithTime();
                    filedir = new File(SD.getMountPoint() + "/Android/data/com.example.aliang.piccollection/" +localDir+"/");

                    dir = new File(filedir,localTempImgFileName);
                    FileOutputStream out=new FileOutputStream(dir);
                    icon.compress(Bitmap.CompressFormat.JPEG,100,out);
                    out.flush();
                    out.close();
                    Toast.makeText(MyPicActivity.this, "保存成功",Toast.LENGTH_SHORT).show();



                } catch (ActivityNotFoundException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(MyPicActivity.this, "没有找到储存目录",Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 其次把文件插入到系统图库
                try {
                    MediaStore.Images.Media.insertImage(getContentResolver(),
                            filedir.getAbsolutePath(), String.valueOf(dir), null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                // 最后通知图库更新
                //this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(filedir+"")));
                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{filedir.toString()}, null, null);

            }else{
                Toast.makeText(MyPicActivity.this, "没有储存卡",Toast.LENGTH_LONG).show();
            }
        }


    }

    //时间命名文件

    private String filenameWithTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");// 获取时间
        Date date=new Date(System.currentTimeMillis());
        String str=format.format(date);
        return str+".jpg";
    }
    public void getCurrentTime(){
        Date currenttime=new Date();
        SimpleDateFormat    sDateFormat    =   new    SimpleDateFormat("yyyy年MM月dd日");
        String    date    =    sDateFormat.format(currenttime);
        pic_text_6.setText(date);
        pic_CurrentTime.setText(date);
    }

    public void getPicTime(){
        if(imgfile!=""){
            File   f=new   File(imgfile);
            try {
                FileInputStream fis = new FileInputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            long modifiedTime = f.lastModified();
            Date date=new Date(modifiedTime);
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日");
            String dd=sdf.format(date);
            pic_text_6.setText(dd);
            pic_CurrentTime.setText(dd);
        }else{
            Toast.makeText(getApplicationContext(),"还未选择图片",Toast.LENGTH_SHORT).show();
        }
    }
;
    public String getPath2() {
        String sdcard_path = null;
        String sd_default = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        if (sd_default.endsWith("/")) {
            sd_default = sd_default.substring(0, sd_default.length() - 1);
        }
        // 得到路径
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                if (line.contains("fat") && line.contains("/mnt/")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        if (sd_default.trim().equals(columns[1].trim())) {
                            continue;
                        }
                        sdcard_path = columns[1];
                    }
                } else if (line.contains("fuse") && line.contains("/mnt/")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        if (sd_default.trim().equals(columns[1].trim())) {
                            continue;
                        }
                        sdcard_path = columns[1];
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sdcard_path;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                Intent intent=new Intent();
                intent.setClass(MyPicActivity.this, index.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

}
