# FastAPP

## 目录介绍

- fast : 主工程
- sample、sample2: fastAPP
- test.keystore：sample、sample2的签名文件

    alias: a    
    password：123456

## 简介

针对部分轻功能，并且非必要功能模块做拆分。如果按照常规的方法放在主工程，会增加安装包大小。如果拆分成单个工程，又会进行安装下载安装等等操作，如果此类模块过多，会让用户操作变得极为繁琐，依赖用户主动操作，是比较重的一种交互模式。

### 相关术语

- 主工程（mainAPP，以下统称mainAPP）：开发中的核心模块。

- 快应用（fastAPP，以下统称fastAPP）：需要免安装打开的应用。

比如应用市场和一些需要免安装打开的APP，这里的应用市场就是主工程，然后需要免安装打开的APP就是fastAPP了。

## 如何开发

### （一）fastAPP

本来是先写的主工程带配置，但是想到，如果根据主工程的文档开始引入，可能刚完成配置代码，就摩拳擦掌的丢几个apk进去试了，结果就会发现

```
Didn't find class "com.qihoo360.replugin.Entry"
```

所以想了一下，换一下顺序，先把准备工作做好，然后一步到位。

1. 根目录build.gradle如下

```
buildscript {
    
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.1.2'
        
        //添加如下依赖↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
        classpath 'com.qihoo360.replugin:replugin-plugin-gradle:2.2.1'
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```

2. app目录build.gradle如下

```
apply plugin: 'com.android.application'

android {
    compileSdkVersion 28
    defaultConfig {
        applicationId "module.http.jidouauto.com.instantsample"
        minSdkVersion 15
        targetSdkVersion 28
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

//添加如下代码↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
apply plugin: 'replugin-plugin-gradle' // 集成 RePlugin 添加的配置

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
    
    //添加如下依赖↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    implementation 'com.qihoo360.replugin:replugin-plugin-lib:2.2.4'
}

```

**注意**

添加的是如下两行代码

```
apply plugin: 'replugin-plugin-gradle'

implementation 'com.qihoo360.replugin:replugin-plugin-lib:2.2.4'
```
fastAPP的其他注意事项  [参见这里](https://github.com/Qihoo360/RePlugin/wiki/%E6%8F%92%E4%BB%B6%E7%9A%84%E7%BB%84%E4%BB%B6)

好了，fastAPP部分的配置完成了。




### （二）主工程

#### （1）添加依赖

1. 根目录build.gradle如下


```
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    
    repositories {
        google()
        
        //以下两行，如果遇到read time out，请添加
        mavenCentral()
        maven { url 'https://maven.google.com' }
        
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.1.2'
        //添加如下依赖↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
        classpath 'com.qihoo360.replugin:replugin-host-gradle:2.2.4'
    }
}

allprojects {
    repositories {
        google()
        
        //以下两行，如果遇到read time out，请添加
        mavenCentral()
        maven { url 'https://maven.google.com' }
        
        jcenter()
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```

为什么要加以下代码?


```
        mavenCentral()
        maven { url 'https://maven.google.com' }
```

androidstudio 采用的是gradle的构建方式，gradle是基于 ant 和maven的理念在构建，所以在他的build.gradle中会引入一些第三方的库。
比如 jencter(); 或者是mavenCentral(); 但是这两个库维护在不同的服务器上，所以两个基本上没有太大的关联，我们知道jencter()是全世界最大的java仓库，所以mavenCentral()有的 jencter()一定有，但是像我们之前采用的androidstudio的版本默认是用的mavenCentral()的库，但是更新以后发现google 将jencter()设为了默认库，所以有时候我们会出现读取超时的情况，所以为了解决这类问题的话可以将之前的库也放进去，并且放在jencter()之前加载，这样就可以避免一个库读取超时

2. app目录build.gradle如下

```
apply plugin: 'com.android.application'

android {
    compileSdkVersion 28
    defaultConfig {
        //applicationId必须要有
        applicationId "com.jidouauto.refast"
        minSdkVersion 15
        targetSdkVersion 28
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

//添加以下代码↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
/**
 * 下面代码的位置必须在android{ ... } 之后，因为需要读取applicationId
 */
apply plugin: 'replugin-host-gradle'
repluginHostConfig {
    /**
     * 是否使用 AppCompat 库
     */
    useAppCompat = true
    /**
     * 背景不透明的坑的数量
     */
    countNotTranslucentStandard = 6
    countNotTranslucentSingleTop = 2
    countNotTranslucentSingleTask = 3
    countNotTranslucentSingleInstance = 2
}
//到这里↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
    
    //添加以下依赖↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    implementation 'com.qihoo360.replugin:replugin-host-lib:2.2.4'
}

```

重点：看代码内注释。
-  applicationId必须要有
-  下面代码的位置必须在android{ ... } 之后，因为需要读取applicationId
  
```
apply plugin: 'replugin-host-gradle'
repluginHostConfig {
    /**
     * 是否使用 AppCompat 库
     */
    useAppCompat = true
    /**
     * 背景不透明的坑的数量
     */
    countNotTranslucentStandard = 6
    countNotTranslucentSingleTop = 2
    countNotTranslucentSingleTask = 3
    countNotTranslucentSingleInstance = 2
}
```
 -  添加依赖
 
```
implementation 'com.qihoo360.replugin:replugin-host-lib:2.2.4'
```

#### （2）如何使用

1. application集成
 
方式一（推荐该方式）：

```
public class MyApplication extends RePluginApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
```

方式二：

```
public class MainApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        RePlugin.App.attachBaseContext(this);
        ....
    }

    @Override
    public void onCreate() {
        super.onCreate();
        
        RePlugin.App.onCreate();
        ....
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        /* Not need to be called if your application's minSdkVersion > = 14 */
        RePlugin.App.onLowMemory();
        ....
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        /* Not need to be called if your application's minSdkVersion > = 14 */
        RePlugin.App.onTrimMemory(level);
        ....
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);

        /* Not need to be called if your application's minSdkVersion > = 14 */
        RePlugin.App.onConfigurationChanged(config);
        ....
    }
}
```

2. 配置AndroidManifest.xml

没什么好说的，既然创建并集成了application，当然需要在配置AndroidManifest里配置上。

```
    //主要配置项，其他忽略
    <application
        android:name=".MyApplication">
    </application>
```

3. fastAPP添加

```
RePlugin.install("apk的路径，filePath");
```

比如路径为  /jidoufile/apk/a.apk，则填入此路径即可。


**注意：**

无论安装还是升级，都会将“源文件”“移动”（而非复制）到插件的安装路径（如app_p_a）上，这样可大幅度节省安装和升级时间，但显然的，“源文件”也就会消失。

- 若想改变这个行为，您可以参考RePluginConfig中的 setMoveFileWhenInstalling() 方法
- 升级插件和此等同，故不再赘述
- 如果install之后，想知道那些已安装，或想获取到列表，调用RePlugin.getPluginInfoList()即可，返回参数的详细信息请直接看该方法源码。

4. 打开fastAPP

```
RePlugin.startActivity(MainActivity.this,"包名","包名+需要打开的activity"));
```
**注意**
- 包名不用自己管理，从RePlugin.getPluginInfoList()中获取到的List<PluginInfo>中带有该信息，找到对应PluginInfo，下面的name字段即为包名。

```
// 示例代码，仅仅是示例，实际代码不能这样写
// 参数一：
String "包名" = RePlugin.getPluginInfoList().get(0).getName()
// 参数二，比如要打开的是MainMctivity
String "包名+需要打开的activity"=RePlugin.getPluginInfoList().get(0).getName()+".MainMctivity"
```
5. sample

然后，还有[sample](https://github.com/Qihoo360/RePlugin/tree/master/replugin-sample)

如果不喜欢太复杂的，[这里还有](https://github.com/xintanggithub/fastAppSample)


