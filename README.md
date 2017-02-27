## FrescoDemo
#引入库 由于原生应用需要继承ReactNative 所以引入react-native库 , 引入该库 里面集成了 Fresco 库,当然 也可以引入原生的库
dependencies {
  compile "com.facebook.react:react-native:+"
}
//原生依赖
dependencies {
  compile 'com.facebook.fresco:fresco:0.12.0'
}
对Fresco 进行封装, 自定义了加载图片的View , 自动加载图片 

###注意
 在项目Application 中 ,一定要初始化Fresco , 如果没有初始化会有如下报错 .
 "SimpleDraweeView was not initialized!"
