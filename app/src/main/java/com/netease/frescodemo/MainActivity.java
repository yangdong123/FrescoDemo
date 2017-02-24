package com.netease.frescodemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.netease.frescodemo.fresco.CustomDraweeView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CustomDraweeView iv_custom = (CustomDraweeView) findViewById(R.id.iv_custom);
        iv_custom.loadRectangleByUrl("http://pic6.huitu.com/res/20130116/84481_20130116142820494200_1.jpg");

//        iv_custom.loadPicByUrl("http://pic6.huitu.com/res/20130116/84481_20130116142820494200_1.jpg");
//
//        iv_custom.loadRoundPicByUrl("http://pic6.huitu.com/res/20130116/84481_20130116142820494200_1.jpg");
//        iv_custom.setDraweeViewBorder(8,R.color.colorPrimaryDark);

    }
}
