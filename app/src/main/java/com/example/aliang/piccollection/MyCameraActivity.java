package com.example.aliang.piccollection;

/**
 * Created by ALIANG on 2016/3/31.
 */
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.FloatMath;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MyCameraActivity extends Activity implements SurfaceHolder.Callback{




    public int rectWidth,rectHeight;
    private static final String tag = "yan";
    private CameraSizeComparator sizeComparator = new CameraSizeComparator();
    private SeekBar seekBar;

    private PreferencesService service;
    private HashMap<String, String> param;


    private String localTempImgDir="Graphics";
    private String localTempImgFileName;
    private LinearLayout camera_save,camera_plane;
    private ImageButton back;
    private ImageView imageView;//返回和切换前后置摄像头
    private SurfaceView surface;
    private ImageButton shutter,camera_yes,camera_error;//快门
    private SurfaceHolder holder;
    private Camera camera;//声明相机
    private String filepath = "";//照片保存路径
   // private int cameraPosition = 1;//0代表前置摄像头，1代表后置摄像头

    private TextView camera_text_1,camera_text_2,camera_text_3,camera_text_4,camera_text_5,camera_text_6,camera_CurrentTime;

    private int resolutionID;
    private String name,type,road;

    public int position,transparent,savePosition;
    public static int windowHeight,windowWidth,PicHeight=0,PicWidth=0;

    public Bitmap bitmap;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//没有标题
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//拍照过程屏幕一直处于高亮
        //设置手机屏幕朝向，一共有7种
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //SCREEN_ORIENTATION_BEHIND： 继承Activity堆栈中当前Activity下面的那个Activity的方向
        //SCREEN_ORIENTATION_LANDSCAPE： 横屏(风景照) ，显示时宽度大于高度
        //SCREEN_ORIENTATION_PORTRAIT： 竖屏 (肖像照) ， 显示时高度大于宽度
        //SCREEN_ORIENTATION_SENSOR  由重力感应器来决定屏幕的朝向,它取决于用户如何持有设备,当设备被旋转时方向会随之在横屏与竖屏之间变化
        //SCREEN_ORIENTATION_NOSENSOR： 忽略物理感应器——即显示方向与物理感应器无关，不管用户如何旋转设备显示方向都不会随着改变("unspecified"设置除外)
        //SCREEN_ORIENTATION_UNSPECIFIED： 未指定，此为默认值，由Android系统自己选择适当的方向，选择策略视具体设备的配置情况而定，因此不同的设备会有不同的方向选择
        //SCREEN_ORIENTATION_USER： 用户当前的首选方向

        setContentView(R.layout.main);

        Intent intent=getIntent();
        resolutionID= intent.getIntExtra("resolution", 0);
        name=intent.getStringExtra("name");
        type=intent.getStringExtra("type");
        road=intent.getStringExtra("road");
        position=intent.getIntExtra("position", 0);
        savePosition= intent.getIntExtra("savePosition", 0);
        transparent= intent.getIntExtra("transparent", 0);
               // Toast.makeText(this,windowHeight+"  "+windowWidth,Toast.LENGTH_LONG);



        initView();



        camera_text_2.setText(name);
        camera_text_3.setText(type);
        camera_text_4.setText(road);



        holder = surface.getHolder();//获得句柄
        holder.addCallback(this);//添加回调
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//surfaceview不维护自己的缓冲区，等待屏幕渲染引擎将内容推送到用户面前

        //设置监听
        back.setOnClickListener(listener);
//        position.setOnClickListener(listener);
        shutter.setOnClickListener(listener);
        camera_yes.setOnClickListener(listener);
        camera_error.setOnClickListener(listener);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                Parameters p = camera.getParameters();
                p.setZoom(i);
                camera.setParameters(p);
                //Toast.makeText(getApplicationContext(),p.getMaxZoom()+"",Toast.LENGTH_SHORT ).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        getCurrentTime();
    }

    private void initView() {
        imageView= (ImageView) findViewById(R.id.imageView);
        back = (ImageButton) findViewById(R.id.camera_back);
        // position = (ImageView) findViewById(R.id.camera_position);
        surface = (SurfaceView) findViewById(R.id.camera_surface);
        shutter = (ImageButton) findViewById(R.id.camera_shutter);
        camera_save= (LinearLayout) findViewById(R.id.camera_save);
        camera_yes= (ImageButton) findViewById(R.id.camera_yes);
        camera_error= (ImageButton) findViewById(R.id.camera_error);
        camera_plane= (LinearLayout) findViewById(R.id.camera_plane);
        camera_text_1= (TextView) findViewById(R.id.camera_text_1);
        camera_text_2= (TextView) findViewById(R.id.camera_text_2);
        camera_text_3= (TextView) findViewById(R.id.camera_text_3);
        camera_text_4= (TextView) findViewById(R.id.camera_text_4);
        camera_text_5= (TextView) findViewById(R.id.camera_text_5);
        camera_text_6= (TextView) findViewById(R.id.camera_text_6);
        seekBar= (SeekBar) findViewById(R.id.seekBar);
        camera_CurrentTime= (TextView) findViewById(R.id.camera_CurrentTime);

        FrameLayout.LayoutParams lparams= (FrameLayout.LayoutParams) camera_plane.getLayoutParams();
        if(position==0){

        }else {
            lparams.gravity=Gravity.RIGHT;
            lparams.gravity=Gravity.BOTTOM;
            camera_plane.setLayoutParams(lparams);
            //lparams.setMargins(0, windowHeight - pic_plane.getHeight(), windowWidth - pic_plane.getWidth(), 0);
        }
        if(transparent==0){
            camera_plane.getBackground().setAlpha(120);
        }else{
            camera_plane.getBackground().setAlpha(255);
        }
    }

    //响应点击事件
    OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.camera_back:
                    Intent i=new Intent();
                    i.setClass(MyCameraActivity.this,index.class);
                    startActivity(i);
                    break;

               /* case R.id.camera_position:
                    //切换前后摄像头
                    int cameraCount = 0;
                    CameraInfo cameraInfo = new CameraInfo();
                    cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数

                    for(int i = 0; i < cameraCount; i ++  ) {
                        Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
                        if(cameraPosition == 1) {
                            //现在是后置，变更为前置
                            if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                                camera.stopPreview();//停掉原来摄像头的预览
                                camera.release();//释放资源
                                camera = null;//取消原来摄像头
                                camera = Camera.open(i);//打开当前选中的摄像头
                                try {
                                    camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                camera.startPreview();//开始预览
                                cameraPosition = 0;
                                break;
                            }
                        } else {
                            //现在是前置， 变更为后置
                            if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                                camera.stopPreview();//停掉原来摄像头的预览
                                camera.release();//释放资源
                                camera = null;//取消原来摄像头
                                camera = Camera.open(i);//打开当前选中的摄像头
                                try {
                                    camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                camera.startPreview();//开始预览
                                cameraPosition = 1;
                                break;
                            }
                        }

                    }
                    break;
*/
                case R.id.camera_shutter:
                    getCurrentTime();

                    //快门
                    String str=android.os.Build.MODEL+"";
                    if(str.contains("HUAWEI")){
                        camera.takePicture(null, null, jpeg);//将拍摄到的照片给自定义的对象
                    }else{
                        camera.autoFocus(new AutoFocusCallback() {//自动对焦
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {
                                if (success) {
                                    //setparams();
                                    camera.takePicture(null, null, jpeg);//将拍摄到的照片给自定义的对象
                                }
                            }
                        });
                    }



                    break;

                case R.id.camera_yes:
                    drawNewBitmap(surface);
                    camera.startPreview();//开始预览
                    seekBar.setVisibility(View.VISIBLE);
                    shutter.setVisibility(View.VISIBLE);
                    camera_save.setVisibility(View.INVISIBLE);
                    break;
                case R.id.camera_error:
                    camera.startPreview();//开始预览
                    seekBar.setVisibility(View.VISIBLE);
                    camera_save.setVisibility(View.INVISIBLE);
                    shutter.setVisibility(View.VISIBLE);
                    camera_save.setVisibility(View.INVISIBLE);
                    //Toast.makeText(getApplicationContext(),windowHeight+" "+windowWidth,Toast.LENGTH_LONG).show();
                    break;

            }
        }


    };



    /*surfaceHolder他是系统提供的一个用来设置surfaceView的一个对象，而它通过surfaceView.getHolder()这个方法来获得。
     Camera提供一个setPreviewDisplay(SurfaceHolder)的方法来连接*/
    private void setparams() {
        //设置参数

        switch (resolutionID){
            case 0:
                // 获取Android屏幕的服务
                WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                // 获取屏幕的分辨率，getHeight()、getWidth已经被废弃掉了
                // 应该使用getSize()，但是这里为了向下兼容所以依然使用它们
                windowHeight = wm.getDefaultDisplay().getHeight();
                windowWidth = wm.getDefaultDisplay().getWidth();
                break;
            case 1:
                windowWidth=1280;
                windowHeight=960;
                break;
            case 3:
                windowWidth=960;
                windowHeight=720;
                break;
            case 4:
                windowWidth=640;
                windowHeight=480;
                break;
        }

        Parameters params = camera.getParameters();
// 选择合适的预览尺寸
        List<Camera.Size> sizeList = params.getSupportedPreviewSizes();
        List<Camera.Size> picturesize=params.getSupportedPictureSizes();

//        Camera.Size cameraS = getPictureSize(sizeList, windowWidth);
//        Camera.Size pictureS = getPictureSize(picturesize, windowWidth);

        //Log.i("sizeList",sizeList+"   "+windowHeight+"  "+windowWidth);
       if (picturesize.size() > 1) {
            Iterator<Camera.Size> itor = picturesize.iterator();
            while (itor.hasNext()) {
                Camera.Size cur = itor.next();
                if (cur.width >= windowWidth
                        && cur.height >= windowHeight) {

                }
                PicWidth = cur.width;
                PicHeight = cur.height;
                Log.i("picturesize",PicHeight+" "+" "+PicWidth+android.os.Build.MODEL);
            }
        }
// 如果sizeList只有一个我们也没有必要做什么了，因为就他一个别无选择
         if (sizeList.size() > 1) {
            Iterator<Camera.Size> itor = sizeList.iterator();
            while (itor.hasNext()) {
                Camera.Size cur = itor.next();
                if (cur.width >= windowWidth
                        && cur.height >= windowHeight) {


                }
                windowWidth = cur.width;
                windowHeight = cur.height;
                Log.i("sizeC",windowWidth+" "+windowHeight);
            }
        }

        //List<Camera.Size> size1=params.getSupportedPictureSizes()
        params.setPictureFormat(PixelFormat.JPEG);//图片格式
        //

        String str=android.os.Build.MODEL+"";
        if(str.contains("HUAWEI Y635-CL00")) {

            rectWidth=1600;
            rectHeight=1200;
            //params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
            params.setPictureSize(rectWidth, rectHeight);//图片大小
            params.setPreviewSize(1920, 1080);
            //params.setPreviewSize(HWwindowWidth, HWwindowHeight);
        }else if(str.contains("EK-GC200")){
            Camera.Size cameraS = getPreviewSize(sizeList, 1100);
            Camera.Size pictureS = getPictureSize(picturesize, 1600);
            rectWidth=1600;
            rectHeight=1200;
            params.setPictureSize(1984, 1488);//图片大小
            params.setPreviewSize(1280,720);
            //params.setFocusMode(Parameters.FOCUS_MODE_AUTO);
        }else{
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
            Camera.Size cameraS = getPreviewSize(sizeList, 1500);
            Camera.Size pictureS = getPictureSize(picturesize, 1200);
            rectWidth=1600;
            rectHeight=1200;
            params.setPictureSize(pictureS.width, pictureS.height);//图片大小
            params.setPreviewSize(cameraS.width, cameraS.height);
        }



        seekBar.setMax(params.getMaxZoom());

        camera.setParameters(params);//将参数设置到我的camera
        camera.cancelAutoFocus();
    }
    private Camera.OnZoomChangeListener onZoomChangeListener=new Camera.OnZoomChangeListener() {
        @Override
        public void onZoomChange(int i, boolean b, Camera camera) {

        }


    };

    //SurfaceHolder.Callback,这是个holder用来显示surfaceView 数据的接口,他必须实现以下3个方法
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
//实现自动对焦
        camera.autoFocus(new AutoFocusCallback() {//自动对焦
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    //setparams();
                    camera.cancelAutoFocus();

                }
            }
        });

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        //当surfaceview创建时开启相机
        if(camera == null) {
            camera = Camera.open();
            try {
                setparams();
                camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                camera.startPreview();//开始预览
                camera.autoFocus(new AutoFocusCallback() {//自动对焦
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            //setparams();
                            camera.cancelAutoFocus();

                        }
                    }
                });
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        //当surfaceview关闭时，关闭预览并释放资源
        if(camera!=null) {
            camera.stopPreview();
            camera.release();
            camera = null;
            holder = null;
            surface = null;
        }
    }

    //创建jpeg图片回调数据对象
    PictureCallback jpeg = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            try {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                //自定义文件保存路径  以拍摄时间区分命名
                filepath = "/sdcard/Messages/MyPictures/"+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+ ".jpg";
                File file = new File(filepath);
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩的流里面
                bos.flush();// 刷新此缓冲区的输出流
                bos.close();// 关闭此输出流并释放与此流有关的所有系统资源
                camera.stopPreview();//关闭预览 处理数据
                camera.startPreview();//数据处理完后继续开始预览
                bitmap.recycle();//回收bitmap空间
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            seekBar.setVisibility(View.INVISIBLE);
            shutter.setVisibility(View.INVISIBLE);
            camera_save.setVisibility(View.VISIBLE);

        }
    };

    private void drawNewBitmap(SurfaceView imagevv){
      //  imagevv.setDrawingCacheEnabled(true);
        imagevv.setDrawingCacheEnabled(true);
        camera_plane.setDrawingCacheEnabled(true);
        Bitmap bit=camera_plane.getDrawingCache();
        //产生ReSize之后的bmp对象
        Matrix matrix = new Matrix();
        matrix.postScale(1.5f, 1.5f);
        Bitmap resizeBmp = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(), bit.getHeight(), matrix, true);
        int width=imagevv.getWidth();
        int height=imagevv.getHeight();

        Bitmap icon=Bitmap.createBitmap(rectWidth, rectHeight,Bitmap.Config.ARGB_8888);// 建立一个空的BItMap
        Canvas canvas = new Canvas(icon);// 初始化画布绘制的图像到icon上

        Paint photoPaint = new Paint(); // 建立画笔
        photoPaint.setDither(true); // 获取跟清晰的图像采样
        photoPaint.setFilterBitmap(true);// 过滤一些

        Rect src=new Rect(0,0,rectWidth,rectHeight);
        Rect dst=new Rect(0,0,rectWidth,rectHeight);

        int[] location=new int[2];
        camera_plane.getLocationOnScreen(location);

       // Toast.makeText(getApplicationContext(),location[0]+" "+location[1],Toast.LENGTH_LONG).show();
        Rect src1=new Rect(0,0,resizeBmp.getWidth(),resizeBmp.getHeight());
        Rect dst1=new Rect(0,rectHeight-resizeBmp.getHeight(),resizeBmp.getWidth(),rectHeight);

        canvas.drawBitmap(bitmap, src, dst, photoPaint);
        canvas.drawBitmap(resizeBmp, src1, dst1, photoPaint);


        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.DEV_KERN_TEXT_FLAG);// 设置画笔



           // canvas.rotate(list.get(i).getRotation(), list.get(i).getX(), list.get(i).getY());
            textPaint.setTextSize(camera_CurrentTime.getTextSize());
            textPaint.setTypeface(Typeface.DEFAULT_BOLD);
            textPaint.setColor(camera_CurrentTime.getCurrentTextColor());

            canvas.drawText((String) camera_CurrentTime.getText(), rectWidth-1.3f*camera_CurrentTime.getWidth(), rectHeight-camera_CurrentTime.getHeight(), textPaint);


           // Log.i("ROTATION", list.get(i).getRotation() + "");
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();



        imagevv.setDrawingCacheEnabled(false);
        camera_plane.setDrawingCacheEnabled(false);
        saveMyBitmap(icon);
    }
    /**
     *
     * @param

     */
    private void saveMyBitmap(Bitmap icon) {
//先验证手机是否有sdcard
        String status=Environment.getExternalStorageState();
        if(status.equals(Environment.MEDIA_MOUNTED))
        {
            File dir = null,filedir = null;
            try {
                //文件名
                localTempImgFileName=filenameWithTime();


                if(savePosition==0) {
                    filedir = new File(Environment.getExternalStorageDirectory() + "/" + localTempImgDir);
                    //判断文件夹是否存在,如果不存在则创建文件夹
                    if (!filedir.exists()) {
                        filedir.mkdir();
                    }
                    dir = new File(Environment.getExternalStorageDirectory() + "/" + localTempImgDir + "/" + localTempImgFileName);
                }else {
                    filedir = new File("mnt/media_rw/sdcard1" + "/" + localTempImgDir);
                    //判断文件夹是否存在,如果不存在则创建文件夹
                    if (!filedir.exists()) {
                        filedir.mkdir();
                    }
                    dir = new File("mnt/media_rw/sdcard1" + "/" + localTempImgDir + "/" + localTempImgFileName);
                }


                FileOutputStream out=new FileOutputStream(dir);
                icon.compress(Bitmap.CompressFormat.JPEG,100,out);
                out.flush();
                out.close();
                Toast.makeText(MyCameraActivity.this, "保存成功",Toast.LENGTH_LONG).show();



            } catch (ActivityNotFoundException e) {
                // TODO Auto-generated catch block
                Toast.makeText(MyCameraActivity.this, "没有找到储存目录",Toast.LENGTH_LONG).show();
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
            Toast.makeText(MyCameraActivity.this, "没有储存卡",Toast.LENGTH_LONG).show();
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

        SimpleDateFormat    sDateFormat    =   new    SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
        SimpleDateFormat    YearFormat    =   new    SimpleDateFormat("yyyy");
        SimpleDateFormat    MonthFormat    =   new    SimpleDateFormat("MM");
        SimpleDateFormat    DayFormat    =   new    SimpleDateFormat("dd");

        String year=YearFormat.format(currenttime);
        String month=MonthFormat.format(currenttime);
        String day=DayFormat.format(currenttime);

        camera_text_6.setText(year+"年"+month+"月"+day+"日");
        String    date    =    sDateFormat.format(currenttime);
        camera_CurrentTime.setText(date);
    }
    /**
     * 获取外置SD卡路径
     * @return  应该就一条记录或空
     */
    public List<String> getExtSDCardPath()
    {
        List<String> lResult = new ArrayList<String>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("extSdCard"))
                {
                    String [] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory())
                    {
                        lResult.add(path);
                    }
                }
            }
            isr.close();
        } catch (Exception e) {
        }
        return lResult;
    }
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
                intent.setClass(MyCameraActivity.this, index.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }


    public Camera.Size getPreviewSize(List<Camera.Size> list, int th){
        Collections.sort(list, sizeComparator);

        int i = 0;
        for(Camera.Size s:list){
            if(s.width > th){
                Log.i(tag, "最终设置预览尺寸:w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }

        return list.get(i);
    }
    public Camera.Size getPictureSize(List<Camera.Size> list, int th){
        Collections.sort(list, sizeComparator);

        int i = 0;
        for(Camera.Size s:list){
            if((s.height >= th) ){
                Log.i(tag, "最终设置图片尺寸:w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }

        return list.get(i);
    }

    public boolean equalRate(Camera.Size s, float rate){
        float r = (float)(s.width)/(float)(s.height);
        if(Math.abs(r - rate) <= 0.2)
        {
            return true;
        }
        else{
            return false;
        }
    }

    public  class CameraSizeComparator implements Comparator<Camera.Size> {
        //按升序排列
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            // TODO Auto-generated method stub
            if(lhs.width == rhs.width){
                return 0;
            }
            else if(lhs.width > rhs.width){
                return 1;
            }
            else{
                return -1;
            }
        }

    }
}