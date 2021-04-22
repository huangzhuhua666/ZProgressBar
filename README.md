# ZProgressBar
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://github.com/huangzhuhua666/ZProgressBar/blob/main/LICENSE)![Maven Central](https://img.shields.io/maven-central/v/io.github.huangzhuhua666/zprogressbar)

这是一个可灵活配置的进度条，抛弃原生ProgressBar麻烦的配置style和progressDrawable形式，可直接在xml中配置进度条的颜色和圆角等功能。如果你觉得这个控件对你有帮助，请在右上角留下你的start。

<p align="center">
    <img src="https://github.com/huangzhuhua666/ZProgressBar/blob/main/images/rect.gif" style="zoom:50%;" /><img src="https://github.com/huangzhuhua666/ZProgressBar/blob/main/images/circle.gif" style="zoom:50%;" />
</p>

## 目录

- [特性](#特性)
- [引入](#引入)
- [使用](#使用)
- [拓展](#拓展)
- [未来计划](#未来计划)
- [更新日志](#更新日志)

## 特性

- 支持圆形、矩形、圆角矩形进度条
- 支持副进度条
- 支持在xml和代码中配置渐变色、矩形进度条还支持描边
- 支持文字显示（文字位置、大小、颜色、是否跟随进度移动、被进度条覆盖的字体变色等）
- 支持进度变化监听
- 支持拖动改变进度
- 灵活拓展
- 进度更新保留原生ProgressBar的形式
- 更多功能正在开发中...

## 引入

在项目build.gradle中添加下列代码

```groovy
allprojects {
    repositories {
        // jcenter即将不可用，不建议使用
        jcenter()
        
        // 1.1.1版本后改用mave central，建议使用这个
        mavenCentral()
    }
}
```

在模块build.gradle中添加下列代码

```groovy
// jcenter即将不可用，不建议使用
implementation 'com.hzh.ui:progressbar:最新版本(不需要v)'

// 1.1.1版本后改用mave central，建议使用这个
implementation 'io.github.huangzhuhua666:zprogressbar:最新版本(不需要v)'
```

## 使用

**示例**

矩形进度条

```xml
<com.hzh.zprogressbar.RectProgressBar
	android:id="@+id/pb3"
	android:layout_width="match_parent"
	android:layout_height="20dp"
	android:layout_margin="10dp"
	app:botCorner="10dp"
	app:botStrokeColor="@color/purple_200"
	app:botStrokeWidth="1dp"
	app:isShowTxt="true"
	app:max="100"
	app:progressCorner="10dp"
	app:progressGradientEnd="#f78316"
	app:progressGradientStart="#ffc859"
	app:txtAlign="LEFT"
	app:txtColor="@color/purple_200"
	app:txtMarginHorizontal="20dp"
    app:txtSize="10sp"/>
```

圆形进度条

```xml
<com.hzh.zprogressbar.CircleProgressBar
	android:id="@+id/pb4"
	android:layout_width="match_parent"
	android:layout_height="100dp"
	android:layout_margin="5dp"
	app:isShowTxt="true"
	app:max="100"
	app:progressGradientEnd="@color/teal_700"
	app:progressGradientStart="@color/teal_200"
	app:startPosition="RIGHT"/>
```

**通用属性**

|         属性名         |  值类型   |   默认值    |                  备注                  |
| :--------------------: | :-------: | :---------: | :------------------------------------: |
|          max           |  integer  |     100     |                最大进度                |
|   secondaryProgress    |  integer  |      0      |                 副进度                 |
|        progress        |  integer  |      0      |                当前进度                |
|        botColor        |   color   |  #ffededed  |            进度条的背景颜色            |
|    botGradientStart    |   color   |     -1      |  进度条的背景渐变色起始颜色，默认为-1  |
|     botGradientEnd     |   color   |     -1      |  进度条的背景渐变色终止颜色，默认为-1  |
|     secondaryColor     |   color   |  #ff00bfff  |             副进度条的颜色             |
| secondaryGradientStart |   color   |     -1      |   副进度条的渐变色起始颜色，默认为-1   |
|  secondaryGradientEnd  |   color   |     -1      |   副进度条的渐变色终止颜色，默认为-1   |
|     progressColor      |   color   |  #fffdc100  |            当前进度条的颜色            |
| progressGradientStart  |   color   |     -1      |  当前进度条的渐变色起始颜色，默认为-1  |
|  progressGradientEnd   |   color   |     -1      | 当前进度条的渐变色终止颜色，默认为-1。 |
|      animDuration      |  integer  |     500     |                动画时长                |
|       isShowTxt        |  boolean  |    false    |    是否显示当前进度文字，默认不显示    |
|    isShowPercentTag    |  boolean  |    true     |      是否显示百分号'%'，默认显示       |
|        txtSize         | dimension |  12(sp2px)  |                字体大小                |
|        txtColor        |   color   | Color.BLACK |                字体颜色                |

*注意：渐变色属性只有当对应进度条的起始色和终止色不为-1时生效，且会覆盖设置的对应的Color属性*

**矩形进度条特有的属性**

|       属性名        |  值类型   |       默认值       |                             备注                             |
| :-----------------: | :-------: | :----------------: | :----------------------------------------------------------: |
|      botCorner      | dimension |         0          |                       进度条的背景圆角                       |
|   secondaryCorner   | dimension |         0          |                        副进度条的圆角                        |
|   progressCorner    | dimension |         0          |                       当前进度条的圆角                       |
|   botStrokeWidth    | dimension |         0          |                     进度条背景的描边边宽                     |
|   botStrokeColor    |   color   |    Color.BLACK     |                     进度条背景的描边颜色                     |
| isTxtFollowProgress |  boolean  |       false        | 设置字体是否跟随当前进度移动，默认不跟随。如设置为跟随则设置的字体摆放位置属性失效 |
|    txtFollowType    |   enum    |       INNER        | 设置字体跟随当前进度移动时，字体是位于当前进度条的内部还是外部（INNER：内部，OUTER：外部；默认在内部） |
|      txtAlign       |   enum    |        LEFT        | 设置字体摆放的位置，如果设置了字体跟随进度移动，次属性会失效。（LEFT：左边，CENTER：中间，RIGHT：右边） |
|    txtColorCover    |   color   | 默认和字体颜色一样 |                设置字体被进度条覆盖部分的颜色                |
| txtMarginHorizontal | dimension |     10(dp2px)      |            设置字体水平方向上距离进度条边缘的间隔            |
|     isDraggable     |  boolean  |       false        |                     设置能否拖拽改变进度                     |

**圆形进度条特有的属性**

|        属性名        |  值类型   |  默认值   |                             备注                             |
| :------------------: | :-------: | :-------: | :----------------------------------------------------------: |
|    startPosition     |   enum    |   LEFT    | 进度出现的起始位置，默认为9点钟方向（LEFT：9点钟方向，TOP：12点钟方向，RIGHT：3点钟方向，BOT：6点钟方向） |
|    botStrokeWidth    | dimension | 10(dp2px) |                       进度条的背景边宽                       |
| secondaryStrokeWidth | dimension | 10(dp2px) |                        副进度条的边宽                        |
| progressStrokeWidth  | dimension | 10(dp2px) |                       当前进度条的边宽                       |

*注意：圆形进度条没有设置圆角的属性，因为设置圆角以后当进度接近100%时显示进度条满了的情况，所以目前暂不支持圆形进度条的圆角。此外，圆形进度条的渐变色目前感觉很不自然，这是此后版本需要优化的。*

### 更新进度

```kotlin
// 设置最大进度
progressBar.setMax(max: Int)

// 设置副进度
progressBar.setSecondaryProgress(progress: Int)

// 设置当前进度，默认不显示动画
progressBar.setProgress(progress: Int, isAnimate: Boolean = false)
```

*注意：上面这三个方法均可以在子线程调用。*

***重点：在设置layout_width和layout_height时，请不要设置为wrap_content或0dp而不设置layout_weight。尽量按照设计图的宽高来设置，否则当控件测量得到的width和height为0时会报错。***

### 设置进度监听

```kotlin
progressBar.setProgressListener{ fromUser, progress ->
	// do anything...
}
```

## 拓展

如果你对现在的进度条不满意或者有更多的效果要求，可自行继承BaseProgressBar并实现其方法或继承拓展现有的矩形、圆形进度条。

```kotlin
class CustomProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BaseProgressBar(context, attrs, defStyleAttr) {
    
    override fun drawPrimary(c: Canvas, paint: Paint) {
        
    }

    override fun drawSecondary(c: Canvas, ratio: Float, paint: Paint) {
        
    }

    override fun drawProgress(c: Canvas, ratio: Float, paint: Paint) {
        
    }

    override fun drawTxt(c: Canvas, ratio: Float, text: String, paint: Paint) {
        
    }
}
```

*注意：如需实现渐变色效果，请务必重写createGradient(colors: IntArray, paint: Paint)方法。*

## 未来计划

- 优化圆形进度条的渐变色效果
- ...更多的需求、改进地方等待你的提出，欢迎在issue提出问题

## 更新日志

| 日期       | 版本   | 更新内容                                                     |
| :--------- | ------ | :----------------------------------------------------------- |
| 2021/04/22 | v1.1.1 | 迁移到maven central上，建议使用maven central的方式引入       |
| 2020/02/23 | v1.1.1 | 支持代码配置进度条样式、支持进度变化监听、支持拖拽改变进度、优化等... |
| 2020/02/13 | v1.0.1 | 放出获取最大进度、副进度和当前进度的方法                     |
| 2020/01/22 | v1.0.0 | 发布1.0.0版本，上传到Bintray、JCenter                        |

