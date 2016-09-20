package com.example.aliang.piccollection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetUp extends AppCompatActivity {
    /**
     * 内置
     */
    static String SDCARD_INTERNAL = "internal";


    /**
     * 外置
     */
    static String SDCARD_EXTERNAL = "external";
    HashMap<String, SDCardInfo> aaaa;
    private PreferencesService service;

    private HashMap<String, String> params;

    private String str;
    private int whichone;

    public LinearLayout name,Project_road;
    public RelativeLayout companyName,Project_type,Project_tran,Project_position,Project_save,Project_resolution,Project_isShow;

    public ImageView setup_back;
    public TextView companyName_content,proname,Project_name_content,Project_type_content,Project_road_content,Project_tran_content,Project_position_content,Project_save_content,Project_resolution_content,Project_isShow_content;

    PullParseService parser;
    String[] items1,items2,items3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);
        initView();
        initEvent();
        SDCardUtil aa=new SDCardUtil();
        aaaa = aa.getSDCardInfo(getApplicationContext());
        Log.i("aaaaa", aaaa.toString());

        params= (HashMap<String, String>) service.getPerferences();

            //Toast.makeText(getApplicationContext(),params+"",Toast.LENGTH_LONG).show();
        if(params.get("companyName")!="")    companyName_content.setText(params.get("companyName"));
        if(params.get("name")!="") Project_name_content.setText(params.get("name"));
        if(params.get("typecontent")!="") Project_type_content.setText(params.get("typecontent"));
        if(params.get("road")!="")    Project_road_content.setText(params.get("road"));
        if(params.get("transparentcontent")!="")    Project_tran_content.setText(params.get("transparentcontent"));
        if(params.get("positioncontent")!="")    Project_position_content.setText(params.get("positioncontent"));
        if(params.get("savepositioncontent")!="")    Project_save_content.setText(params.get("savepositioncontent"));
        if(params.get("resolutioncontent")!="")    Project_resolution_content.setText(params.get("resolutioncontent"));
        if(params.get("showcontent")!="")    Project_isShow_content.setText(params.get("showcontent"));
        //Toast.makeText(getApplicationContext(),Environment.getExternalStorageDirectory()+"",Toast.LENGTH_LONG).show();
        items1=pullxm("projects1.xml");
        items2=pullxm("projects2.xml");
        items3=pullxm("projects3.xml");
    }

    private String[] pullxm(String s) {
        List<project> projects=null;
        String[] ss=null;
        try {
            InputStream is = getApplication().getAssets().open(s);
            //          parser = new SaxBookParser();
            //          parser = new DomBookParser();
            parser = new PullParseService();
            projects = parser.getprojects(is);
            ss = new String[projects.size()];
            for (int i = 0; i < projects.size(); i++) {
                ss[i] =projects.get(i).getName();
                //Log.i("aaa", projects.get(i).getName());

                //Log.i(TAG, project.toString());
            }

        } catch (Exception e) {
            Log.e("projects", e.getMessage());
        }
        return ss;
    }

    private void initEvent() {

        companyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = params.get("companyID");
                if (str != "") {
                    whichone = Integer.parseInt(str);
                } else {
                    whichone = 0;
                }
                final String[] item = new String[]{"葫芦岛区域监理项目部", "葫芦岛东建监理"};
                new AlertDialog.Builder(SetUp.this)
                        .setTitle("请选择")
                        .setIcon(android.R.drawable.ic_menu_agenda)
                        .setSingleChoiceItems(item, whichone,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        companyName_content.setText(item[which].toString());
                                        service.savecompanyID(which);
                                        service.savecompanyName(item[which].toString());
                                        dialog.dismiss();
                                    }
                                }
                        )
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
        setup_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SetUp.this, index.class);
                startActivity(intent);
                SetUp.this.finish();
            }
        });



        setup_back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    setup_back.setImageResource(R.drawable.left_selected);
                return false;
            }
        });


        Project_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = params.get("type");
                if (str != "") {
                    whichone = Integer.parseInt(str);
                } else {
                    whichone = 0;
                }
                final String[] item = new String[]{"施工部位", "施工杆塔号"};
                new AlertDialog.Builder(SetUp.this)
                        .setTitle("请选择标识牌类型")
                        .setIcon(android.R.drawable.ic_menu_agenda)
                        .setSingleChoiceItems(item, whichone,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        Project_type_content.setText(item[which].toString());
                                        service.savetype(which);
                                        service.savetypecontent(item[which].toString());
                                        dialog.dismiss();
                                    }
                                }
                        )
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        Project_road.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = params.get("road");


                final EditText inputServer = new EditText(getApplicationContext());
                inputServer.setTextColor(Color.BLACK);
                inputServer.setText(str);
                new AlertDialog.Builder(SetUp.this).setTitle("拍摄内容")
                        .setIcon(android.R.drawable.ic_input_get)
                        .setView(inputServer)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Project_road_content.setText(inputServer.getText());
                                service.saveRoad(inputServer.getText().toString());

                            }
                        })
                        .setNegativeButton("取消", null).show();
            }
        });

        Project_tran.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                str = params.get("transparent");
                if (str != "") {
                    whichone = Integer.parseInt(str);
                } else {
                    whichone = 0;
                }
                final String[] item = new String[]{"半透明", "不透明"};
                new AlertDialog.Builder(SetUp.this)
                        .setTitle("请选择标识牌透明度")
                        .setIcon(android.R.drawable.ic_menu_agenda)
                        .setSingleChoiceItems(item, whichone,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        Project_tran_content.setText(item[which].toString());
                                        service.savetransparent(which);
                                        service.savetranparentcontent(item[which].toString());
                                        dialog.dismiss();
                                    }
                                }
                        )
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        Project_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = params.get("position");
                if (str != "") {
                    whichone = Integer.parseInt(str);
                } else {
                    whichone = 0;
                }
                final String[] item = new String[]{"左下方", "右下方"};
                new AlertDialog.Builder(SetUp.this)
                        .setTitle("请选择标识牌位置")
                        .setIcon(android.R.drawable.ic_menu_agenda)
                        .setSingleChoiceItems(item, whichone,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        Project_position_content.setText(item[which].toString());
                                        service.saveposition(which);
                                        service.savepositioncontent(item[which].toString());
                                        dialog.dismiss();
                                    }
                                }
                        )
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        Project_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str = params.get("savePosition");
                if (str != "") {
                    whichone = Integer.parseInt(str);
                } else {
                    whichone = 0;
                }
                final String[] item = new String[]{"本机","存储卡"};
                new AlertDialog.Builder(SetUp.this)
                        .setTitle("请选择存储位置")
                        .setIcon(android.R.drawable.ic_menu_agenda)
                        .setSingleChoiceItems(item, whichone,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            Project_save_content.setText(item[which].toString());
                                            service.savesave(which);
                                            service.savesavePositioncontent(item[which].toString());
                                        } else {
                                            if (aaaa.get(SDCARD_EXTERNAL).isMounted()) {
                                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                                    File[] a=getApplicationContext().getExternalFilesDirs("");
                                                }
                                                Project_save_content.setText(item[which].toString());
                                                service.savesave(which);
                                                service.savesavePositioncontent(item[which].toString());
                                                //Toast.makeText(getApplicationContext(),getPath2(),Toast.LENGTH_SHORT).show();
                                            } else {
                                                Project_save_content.setText(item[0].toString());
                                                service.savesave(0);
                                                service.savesavePositioncontent(item[0].toString());
                                                Toast.makeText(getApplicationContext(), "未发现外置存储卡", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        dialog.dismiss();
                                    }
                                }
                        )
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        Project_resolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = params.get("resolution");
                if (str != "") {
                    whichone = Integer.parseInt(str);
                } else {
                    whichone = 0;
                }
                final String[] item = new String[]{"默认", "1280x960", "1280x720", "640x480"};
                new AlertDialog.Builder(SetUp.this)
                        .setTitle("请选择相机分辨率")
                        .setIcon(android.R.drawable.ic_menu_agenda)
                        .setSingleChoiceItems(item, whichone,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        Project_resolution_content.setText(item[which].toString());
                                        service.saveresolution(which);
                                        service.saveresolutioncontent(item[which].toString());
                                        dialog.dismiss();
                                    }
                                }
                        )
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        Project_isShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = params.get("isShow");
                if (str != "") {
                    whichone = Integer.parseInt(str);
                } else {
                    whichone = 0;
                }
                final String[] item = new String[]{"显示", "隐藏"};
                new AlertDialog.Builder(SetUp.this)
                        .setTitle("请选择是否提示")
                        .setIcon(android.R.drawable.ic_menu_agenda)
                        .setSingleChoiceItems(item, whichone,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        Project_isShow_content.setText(item[which].toString());
                                        service.saveisShow(which);
                                        service.saveshowcontent(item[which].toString());
                                        //  Toast.makeText(getApplicationContext(),item[which].toString(),Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                }
                        )
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            final String[] nametype = new String[]{
                   "送电工程","配电工程","变电工程"
            };

            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SetUp.this).setTitle("选择工程类别").setIcon(
                       android.R.drawable.ic_dialog_info).setSingleChoiceItems(
                        nametype, 0, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    chosename(items1);
                                    dialog.dismiss();
                                } else if (which == 1) {
                                    chosename(items2);
                                    dialog.dismiss();
                                } else if (which == 2) {
                                    chosename(items3);
                                    dialog.dismiss();
                                } else {
                                    str = params.get("name");
                                    final EditText inputServer = new EditText(getApplicationContext());
                                    inputServer.setTextColor(Color.BLACK);
                                    inputServer.setText(str);
                                    new AlertDialog.Builder(SetUp.this).setTitle("工程名称")
                                            .setIcon(android.R.drawable.ic_input_get)
                                            .setView(inputServer)
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Project_name_content.setText(inputServer.getText());
                                                    service.savename(inputServer.getText().toString());

                                                }
                                            })
                                            .setNegativeButton("取消", null).show();
                                    dialog.dismiss();
                                }


                            }
                        }).setNegativeButton("取消", null).show();
            }
        });


    }

    private void chosename(final String[] item) {
        new AlertDialog.Builder(SetUp.this).setTitle("选择工程名称").setIcon(
                android.R.drawable.ic_dialog_info).setSingleChoiceItems(
                item, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Project_name_content.setText(item[which].toString());
                        service.savename(item[which].toString());
                        dialog.dismiss();

                    }
                }).setNegativeButton("取消", null).show();
    }

    private void initView() {
        service=new PreferencesService(this);
        Project_name_content= (TextView) findViewById(R.id.Project_name_content);
        setup_back= (ImageView) findViewById(R.id.setup_back);
        name= (LinearLayout) findViewById(R.id.Project_name);
        companyName= (RelativeLayout) findViewById(R.id.companyName);
        //proname= (TextView) findViewById(R.id.Project_name_content);
        Project_type= (RelativeLayout) findViewById(R.id.Project_type);
        Project_road= (LinearLayout) findViewById(R.id.Project_road);
        Project_tran =(RelativeLayout) findViewById(R.id.Project_tran);
        Project_position= (RelativeLayout) findViewById(R.id.Project_position);
        Project_save= (RelativeLayout) findViewById(R.id.Project_save);
        Project_resolution= (RelativeLayout) findViewById(R.id.Project_resolution);
        Project_isShow= (RelativeLayout) findViewById(R.id.Project_isShow);

        companyName_content= (TextView) findViewById(R.id.companyName_content);
        Project_type_content= (TextView) findViewById(R.id.Project_type_content);
        Project_road_content= (TextView) findViewById(R.id.Project_road_content);
        Project_tran_content= (TextView) findViewById(R.id.Project_tran_content);
        Project_position_content= (TextView) findViewById(R.id.Project_position_content);
        Project_save_content= (TextView) findViewById(R.id.Project_save_content);
        Project_resolution_content= (TextView) findViewById(R.id.Project_resolution_content);
        Project_isShow_content= (TextView) findViewById(R.id.Project_isShow_content);

    }

    public String getSD(){
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            String mount = new String();
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure")) continue;
                if (line.contains("asec")) continue;

                if (line.contains("fat")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        mount = mount.concat("*" + columns[1] + "\n");
                    }
                } else if (line.contains("fuse")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        mount = mount.concat(columns[1] + "\n");
                    }
                }
            }
            return mount;

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取外置SD卡路径
     * @return  应该就一条记录或空
     */

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
                intent.setClass(SetUp.this, index.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
}
