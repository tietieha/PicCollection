package com.example.aliang.piccollection;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-07-29.
 */
public class PullParseService {
    public List<project> getprojects(InputStream inputStream) throws Exception {
        List<project> projects = null;
        project project = null;
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();


        parser.setInput(inputStream, "UTF-8");

        int event = parser.getEventType();//产生第一个事件
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT://判断当前事件是否是文档开始事件
                    projects = new ArrayList<project>();//初始化projects集合
                    break;
                case XmlPullParser.START_TAG://判断当前事件是否是标签元素开始事件
                    if (parser.getName().equals("project")) {
                        project = new project();
                    } else if (parser.getName().equals("name")) {
                        event = parser.next();
                        project.setName(parser.getText());
                        //Log.i("aaa", "getprojects: "+parser.getText());
                    }
                    break;
                case XmlPullParser.END_TAG://判断当前事件是否是标签元素结束事件
                    if ("project".equals(parser.getName())) {//判断结束标签元素是否是project
                        projects.add(project);//将project添加到projects集合
                        project = null;
                    }
                    break;
            }
            event = parser.next();//进入下一个元素并触发相应事件
        }//end while
        return projects;
    }

}
