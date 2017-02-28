package com.netease.frescodemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.netease.frescodemo.fresco.CustomDraweeView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CustomDraweeView iv_custom = (CustomDraweeView) findViewById(R.id.iv_custom);
        CustomDraweeView iv_customl = (CustomDraweeView) findViewById(R.id.iv_customl);
        CustomDraweeView iv_roundcustom = (CustomDraweeView) findViewById(R.id.iv_roundcustom);
        CustomDraweeView iv_native_pic = (CustomDraweeView) findViewById(R.id.iv_native_pic);


        iv_custom.loadPicByUrl("http://pic6.huitu.com/res/20130116/84481_20130116142820494200_1.jpg");

        iv_customl.loadRectangleByUrl("http://pic6.huitu.com/res/20130116/84481_20130116142820494200_1.jpg");

        iv_roundcustom.loadRoundPicByUrl("http://pic6.huitu.com/res/20130116/84481_20130116142820494200_1.jpg");
        iv_roundcustom.setDraweeViewBorder(8,R.color.colorAccent);

        iv_native_pic.loadPicByResId(R.drawable.home_page_default_icon);



    }
}
