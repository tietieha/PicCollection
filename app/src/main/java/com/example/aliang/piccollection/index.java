package com.example.aliang.piccollection;

import android.annotation.TargetApi;
import android.app.Activity;
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
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class index extends Activity {
    /**
     * 内置
     */
    static String SDCARD_INTERNAL = "internal";


    /**
     * 外置
     */
    static String SDCARD_EXTERNAL = "external";
    HashMap<String, SDCardInfo> aaaa;
    private static final int PHOTO_TAKE = 1;
    private static final int PHOTO_SELECT = 2;
    public ImageView imagevv;

    private String localTempImgFileName;
    private String localDir="基建照片采集";
    private String localcacheDir="cache";
    private String localimgDir="img";

    private File filedir;
    public int resolutionID=0;

    public int position,transparent,savePosition;
    public String company,code,name,type,road,savePositionContent;
    public FrameLayout frame_index;

    private PreferencesService service;

    private HashMap<String, String> params;
    private static final int Qr_code = 2;
    private LinearLayout Pro_name,pro_plane;
    private TextView companyName,Pro_content,Pro_identity_type,Pro_identity_name,Pro_Time,Project_CurrentTime;
    private ImageButton Pic_camera,Pic_file,gallery;

    private ImageButton setup;
    private long firstTime = 0;
    public Intent intent;
    public boolean selected;
    public static int  windowHeight,windowWidth;
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
        setContentView(R.layout.activity_index);
        // 获取Android屏幕的服务
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        // 获取屏幕的分辨率，getHeight()、getWidth已经被废弃掉了
        // 应该使用getSize()，但是这里为了向下兼容所以依然使用它们
        windowHeight = wm.getDefaultDisplay().getHeight();
        windowWidth = wm.getDefaultDisplay().getWidth();
        SDCardUtil aa=new SDCardUtil();
        aaaa = aa.getSDCardInfo(getApplicationContext());

            service=new PreferencesService(this);
            params= (HashMap<String, String>) service.getPerferences();

            position= Integer.parseInt(params.get("position"));
            savePosition= Integer.parseInt(params.get("savePosition"));
            transparent= Integer.parseInt(params.get("transparent"));
        transparent= Integer.parseInt(params.get("transparent"));
        code=params.get("code");
        company=params.get("companyName");
        savePositionContent=params.get("savepositioncontent");
        initView();
        initEvent();

        if(company!="") companyName.setText(company);



        if(params.get("name")!="") Pro_content.setText(params.get("name"));
        if(params.get("typecontent")=="") {
            Pro_identity_type.setText("施工部位：");
        }else{
            Pro_identity_type.setText(params.get("typecontent") + ":");
        }
        Pro_identity_name.setText(params.get("road"));


        resolutionID= Integer.parseInt(params.get("resolution"));
        getCurrentTime();
//        boolean v=position==1;
//        Toast.makeText(getApplicationContext(),v+"",Toast.LENGTH_LONG).show();
         /* lparams.gravity= Gravity.RIGHT;
            lparams.gravity=Gravity.BOTTOM;*/

        //lparams.setMargins(windowWidth - pro_plane.getWidth(), windowHeight - pro_plane.getHeight(),0, 0);
        //pro_plane.setLayoutParams(lparams);
        //filedir = new File(Environment.getExternalStorageDirectory() + "/"+localDir+"/" + localimgDir+"/");

        if(code==""){
            final EditText inputServer = new EditText(getApplicationContext());
            inputServer.setTextColor(Color.BLACK);

            new AlertDialog.Builder(index.this).setTitle("请输入验证码：")
                    .setIcon(android.R.drawable.ic_input_get)
                    .setView(inputServer)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(inputServer.getText().toString().equals(getResources().getString(R.string.code))){
                                service.savecode(inputServer.getText().toString());
                                try {
                                    //不关闭
                                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    field.set(dialog, true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(getApplicationContext(),"验证成功，请使用",Toast.LENGTH_SHORT ).show();
                            }else{
                                try {
                                    //不关闭
                                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    field.set(dialog, false);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(getApplicationContext(),"验证失败！",Toast.LENGTH_SHORT ).show();
                            }


                        }
                    })
                    .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.exit(0);
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    View.OnClickListener listener=new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            name=Pro_content.getText().toString();
            type=Pro_identity_type.getText().toString();
            road=Pro_identity_name.getText().toString();

            switch (v.getId()) {
                case R.id.Pic_camera:
                    /*if(params.get("showcontent")=="显示"||params.get("showcontent")=="") {isTip();}
                    else {

                        Intent intent = new Intent(index.this, MyCameraActivity.class);
                        intent.putExtra("resolution",resolutionID);
                        intent.putExtra("position",position);
                        intent.putExtra("savePosition",savePosition);
                        intent.putExtra("transparent",transparent);
                        intent.putExtra("name",name);
                        intent.putExtra("type",type);
                        intent.putExtra("road",road);
                        startActivity(intent);
                        finish();
                    }*/

                    MethodCmaera();
                    break;
                case R.id.Pic_file:
                    intent =new Intent(index.this,MyPicActivity.class);
                    intent.putExtra("resolution",resolutionID);
                    intent.putExtra("position",position);
                    intent.putExtra("savePosition",savePosition);
                    intent.putExtra("transparent",transparent);
                    intent.putExtra("company",company);
                    intent.putExtra("name",name);
                    intent.putExtra("type",type);
                    intent.putExtra("road",road);
                    startActivity(intent);
                    finish();


                    break;
                case R.id.setup:
                    intent=new Intent(index.this,SetUp.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.Pro_name:
                    break;
                case R.id.gallery:
                    //MediaScannerConnection.scanFile(getApplicationContext(), new String[]{filedir.toString()}, null, null);
                    //Context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(filedir + "")));
                    Intent intent = new Intent();
                    intent.setClass(index.this,myGallery.class);
                    intent.putExtra("savePosition",savePosition);
                    startActivity(intent);
                    finish();


                    break;
            }
        }
    };
    private void initEvent() {
        Pic_camera.setOnClickListener(listener);
        Pic_file.setOnClickListener(listener);
        setup.setOnClickListener(listener);
        Pro_name.setOnClickListener(listener);
        gallery.setOnClickListener(listener);
    }

    private void initView() {
        companyName= (TextView) findViewById(R.id.index_company_content);
        Project_CurrentTime= (TextView) findViewById(R.id.Project_CurrentTime);
        imagevv= (ImageView) findViewById(R.id.imagevv);
        frame_index= (FrameLayout) findViewById(R.id.Frame_index);
        pro_plane= (LinearLayout) findViewById(R.id.Pro_plane);
        Pro_name= (LinearLayout) findViewById(R.id.Pro_name);
        Pro_content= (TextView) findViewById(R.id.Pro_content);
        Pro_identity_type= (TextView) findViewById(R.id.Pro_identity_type);
        Pro_identity_name= (TextView) findViewById(R.id.Pro_identity_name);
        Pro_Time= (TextView) findViewById(R.id.Pro_Time);
        Pic_camera= (ImageButton) findViewById(R.id.Pic_camera);
        Pic_file= (ImageButton) findViewById(R.id.Pic_file);
        setup= (ImageButton) findViewById(R.id.setup);
        gallery= (ImageButton) findViewById(R.id.gallery);


        FrameLayout.LayoutParams lparams= (FrameLayout.LayoutParams) pro_plane.getLayoutParams();

        //Toast.makeText(getApplicationContext(),position+"",Toast.LENGTH_SHORT ).show();
        if(position==0){

        }else {
            lparams.gravity=Gravity.RIGHT;
            lparams.gravity=Gravity.BOTTOM;
            pro_plane.setLayoutParams(lparams);
            //lparams.setMargins(0, windowHeight - pic_plane.getHeight(), windowWidth - pic_plane.getWidth(), 0);
        }
        if(transparent==0){
            pro_plane.getBackground().setAlpha(120);
        }else{
            pro_plane.getBackground().setAlpha(255);
        }
    }
    private void isTip(){
        final String[] item=new String[]{"不再提示"};
        new AlertDialog.Builder(index.this)
                .setTitle("是否使用默认设置？")
                .setMultiChoiceItems(item, new boolean[]{false}, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked){
                            service.saveisShow(1);
                            service.saveshowcontent("隐藏");
                        }

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intent = new Intent(index.this, MyCameraActivity.class);
                        intent.putExtra("resolution",resolutionID);
                        intent.putExtra("position",position);
                        intent.putExtra("savePosition",savePosition);
                        intent.putExtra("transparent",transparent);
                        intent.putExtra("name",name);
                        intent.putExtra("type",type);
                        intent.putExtra("road",road);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intent = new Intent(index.this, SetUp.class);
                        startActivity(intent);
                        finish();
                    }
                }).show();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                dialog();
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO Auto-generated method stub
        Bitmap bit;
        String imgfile="";
        if(resultCode!=RESULT_OK)
            return;
        switch(requestCode){
            case PHOTO_TAKE://照相完成 点击确定
                getCurrentTime();
                if(savePosition==0) {
                    imgfile = aaaa.get(SDCARD_INTERNAL).getMountPoint()
                            + "/" + localDir + "/" + localcacheDir + "/" + localTempImgFileName;
                }else{
                    imgfile =aaaa.get(SDCARD_EXTERNAL).getMountPoint()+"/Android/data/com.example.aliang.piccollection"
                            + "/" + localDir + "/" + localcacheDir + "/" + localTempImgFileName;
                }

                Log.i("aaaa", imgfile);
                Bitmap savebit=BitmapFactory.decodeFile(imgfile);
                drawNewBitmap(savebit);

                File deletefile=new File(imgfile);
                deletefile.delete();

                break;
            case PHOTO_SELECT:
                imgfile=getPath(this,data.getData());
                bit=createImageThumbnail(imgfile);
                imagevv.setImageBitmap(bit);
                break;

            default:
                break;

        }
    }
    public void getCurrentTime(){
        Date currenttime=new Date();


        SimpleDateFormat    YearFormat    =   new    SimpleDateFormat("yyyy");
        SimpleDateFormat    MonthFormat    =   new    SimpleDateFormat("MM");
        SimpleDateFormat    DayFormat    =   new    SimpleDateFormat("dd");

        String year=YearFormat.format(currenttime);
        String month=MonthFormat.format(currenttime);
        String day=DayFormat.format(currenttime);

        Pro_Time.setText(year + "年" + month + "月" + day + "日");
        Project_CurrentTime.setText(year + "年" + month + "月" + day + "日");
    }


    //照相功能
    private void MethodCmaera() {
        if(savePosition==0){
            //先验证手机是否有sdcard
            SDCardInfo SD=aaaa.get(SDCARD_INTERNAL);
            if(SD.isMounted())
            {
                try {
                    File dir=new File(SD.getMountPoint() + "/"+localDir+"/");
                    if(!dir.exists())dir.mkdirs();
                    dir=new File(SD.getMountPoint() + "/"+localDir+"/"+localcacheDir+"/");
                    if(!dir.exists())dir.mkdirs();
                    Intent intent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    //文件名

                    localTempImgFileName=filenameWithTime();
                    File f=new File(dir, localTempImgFileName);
                    Uri u=Uri.fromFile(f);
                    intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
                    startActivityForResult(intent, PHOTO_TAKE);

                } catch (ActivityNotFoundException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(index.this, "没有找到储存目录",Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(index.this, "没有储存卡",Toast.LENGTH_LONG).show();
            }
        }else {
            //先验证手机是否有sdcard
            SDCardInfo SD=aaaa.get(SDCARD_EXTERNAL);
            if(SD.isMounted())
            {
                try {
                    File dir=new File(SD.getMountPoint()+"/Android/data/com.example.aliang.piccollection"
                            +"/"+localDir+"/"+localcacheDir+"/");
                    if(!dir.exists())dir.mkdirs();
                    Intent intent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    //文件名

                    localTempImgFileName=filenameWithTime();
                    File f=new File(dir, localTempImgFileName);
                    Uri u=Uri.fromFile(f);
                    intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
                    startActivityForResult(intent, PHOTO_TAKE);

                } catch (ActivityNotFoundException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(index.this, "没有找到储存目录",Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(index.this, "没有储存卡",Toast.LENGTH_LONG).show();
            }
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


    //时间命名文件

    private String filenameWithTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");// 获取时间
        Date date=new Date(System.currentTimeMillis());
        String str=format.format(date);
        return str+".jpg";
    }
    /**

     　　* 进行添加水印图片和文字
     　　*
     　　* @param src
     　　* @param waterMak
     　　* @return
     　　*/
    public void drawNewBitmap(Bitmap src) {
        pro_plane.setDrawingCacheEnabled(true);

        Bitmap water=pro_plane.getDrawingCache();


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
        Project_CurrentTime.setDrawingCacheEnabled(true);
        Bitmap time=Project_CurrentTime.getDrawingCache();
        // 获取字体的宽与高

        int Tw = time.getWidth();
        int Th = time.getHeight();

        float timetargetW=w/4;
        float timescale=timetargetW/Tw;
        Log.i("timescale",timescale+"");
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

        pro_plane.setDrawingCacheEnabled(false);
        Project_CurrentTime.setDrawingCacheEnabled(false);
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
                    Toast.makeText(index.this, "保存成功",Toast.LENGTH_SHORT).show();



                } catch (ActivityNotFoundException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(index.this, "没有找到储存目录",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(index.this, "没有储存卡",Toast.LENGTH_LONG).show();
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
                    Toast.makeText(index.this, "保存成功",Toast.LENGTH_SHORT).show();



                } catch (ActivityNotFoundException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(index.this, "没有找到储存目录",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(index.this, "没有储存卡",Toast.LENGTH_LONG).show();
            }
        }


    }

    protected void dialog() {
        new AlertDialog.Builder(index.this)
                .setTitle("确认退出？")
                .setIcon(android.R.drawable.ic_menu_agenda)
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.exit(0);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }



}
