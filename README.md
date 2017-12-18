# Abstractlistadapters: 简单好用的列表控件数据适配器

[![versionInfo](https://img.shields.io/badge/abstractlistadapters-0.0.1--SNAPSHOT-brightgreen.svg?style=plastic)](http://www.jianshu.com/u/b9cbfe0a7f35)
[![简书个人页](https://img.shields.io/badge/%E7%AE%80%E4%B9%A6-Lucky__Zhang-orange.svg?style=plastic)](http://www.jianshu.com/u/b9cbfe0a7f35)


该适配器可为`ListView`、`GridView`、`RecyclerView`提供数据适配，通过使用抽象类和`java`泛型机制继承传统适配器类，将重复性代码整体封装，在保存`ViewHolder`缓存机制的前提下，将适配器的核心功能代码逻辑剥离，使得我们只需要通过编写继承于`BaseItemView<T>`的派生类即可完成数据的适配。大大减少了冗余代码的编写工作，让我们能将时间和精力聚焦于核心业务逻辑的编写从而提高编码效率。

### 核心类及说明：

---

* `BaseRecyclerAdapter<T>`  ：用于对`RecyclerView`的数据适配
* `ModeListAdapter<T> `：用于对`ListView`、`GridView` 的数据适配
* `BaseItemView<T> `：适用于`BaseRecyclerAdapter<T>`和`ModeListAdapter<T>`的通用`Item`布局类，该类承担了`Item`视图业务和数据处理业务的整合。通过编写该类的派生类可以定制化的实现各种`Item`视图以及对应的数据展示

### 引入：

---

1. 在项目的根目录中找到**`build.gradle `** 文件 （*不是module的`build.gradle `文件* ），加入如下代码：

   ```groovy
   allprojects {
       repositories {
           maven {
               url 'https://raw.githubusercontent.com/walkermanX/mvn-repo/master/Android/Lib/abstractlistadapters/snapshots/'
           }
       }
   }
   ```

2. 在你的项目module的`build.gradle `文件中引入依赖：

   ```groovy
    	//AndroidStudio3.0以上版本
       implementation 'com.walkermanx.android.lib:abstractlistadapters:0.0.1-SNAPSHOT'
       //AndroidStudio3.0以下版本
       compile 'com.walkermanx.android.lib:abstractlistadapters:0.0.1-SNAPSHOT'
   ```

### 使用示例：

---

**1. **编写`BaseItemView<T>`的派生类，实现自定义的视图业务

   * 编写自定义的`Item Layout`布局文件：

     ```xml
     <?xml version="1.0" encoding="utf-8"?>
     <merge xmlns:android="http://schemas.android.com/apk/res/android">
         <TextView
             android:id="@+id/tv_title"
             android:layout_width="match_parent"
             android:layout_height="match_parent"
             android:gravity="center"
             android:text="仓库A"
             android:textColor="@color/txt_color_black"
             android:textSize="@dimen/y18" />
     </merge>
     ```

   * 编写`BaseItemView<T>`的派生类：

     ```java
     public class AtomBeanItemView extends BaseItemView<Atombean> {

         TextView tv_title;
         public AtomBeanItemView(Context context) {
             this(context, null);
         }
         public AtomBeanItemView(Context context, AttributeSet attrs) {
             super(context, attrs);
             inflate(context, R.layout.item_warehouse, this);
             tv_title = (TextView) findViewById(R.id.tv_title);
         }

         @Override
         public void setModel(Atombean model) {
             super.setModel(model);
             tv_title.setText(model.getName());
         }

         @Override
         public void setPosition(int position, int sum) {
             super.setPosition(position, sum);
         }

         @Override
         public void setSelected(boolean b) {
             if (b) {
                 setBackgroundColor(0xffdde6fa);
                 tag.setVisibility(VISIBLE);
                 tv_title.setTextColor(0xff5A87EA);
             } else {
                 setBackgroundColor(0xfff4f4f4);
                 tag.setVisibility(GONE);
                 tv_title.setTextColor(0xff000000);
             }
         }
     }
     ```

**2.** 使用示例：

   * `ListView`、`GridView` 中

     ```java
     ListView listView = (ListView) findViewById(R.id.list);
     listView.setAdapter(new ModeListAdapter<AtomBean>(this) {
                 @NonNull
                 @Override
                 protected BaseItemView GetCurrentModelItemViewInstanceByViewType(Context context, int viewType) {
                     return null;
                 }

                 //如果item需要根据ViewType做不同的视图加载，则需要重写该方法即可 否则只需要重写GetCurrentModelItemViewInstanceByViewType即可
                 
                 // getItemViewType返回的数值其取值必须在getViewTypeCount返回的数值之内。
                 // 也就是如果getViewTypeCount返回的数值是3，
                 // 那么getItemViewType返回的数值就必须是0，1，2三个中的一个 否则无效
                 @Override
                 public int getItemViewType(int position) {
                     return super.getItemViewType(position);
                 }
             });
     ```

     ​

   * `RecyclerView` 中

     ```java
      RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
      recyclerView.setAdapter(new BaseRecyclerAdapter<AtomBean>() {
                 @NonNull
                 @Override
                 protected BaseItemView GetCurrentModelItemViewInstanceByViewType(Context context, int viewType) {
                     return new AtomBeanItemView(context);
                 }

                 //如果item需要根据ViewType做不同的视图加载，则需要重写该方法 否则只需要重写GetCurrentModelItemViewInstanceByViewType即可
                 @Override
                 protected int itemViewType(int pos) {
                     return super.itemViewType(pos); 
                 }
             });
     ```



### 后记：

---

有任何问题请邮件联系：zhangwei-baba@163.com

