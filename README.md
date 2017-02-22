# TVRecyclerViewAndFocus
Android TV 上使用的RecyclerView和焦点框架

一、TVRecyclerView

    1、获取焦点绘制在最上层，遮盖其他child效果
    
    2、notify后恢复焦点
    
    3、加载更多
    
    4、边缘按键拦截
    
    ![image](https://github.com/ShuKeW/TVRecyclerViewAndFocus/blob/master/app/gif/2%E6%9C%88-22-2017%2014-31-30.gif)
    
二、PageRecyclerView

    继承TVRecyclerView，实现一页一页的滑动，可设置滑动时间
    
三、RecyclerViewMiddleFocus

     获取焦点的view始终滑到recyclerView的中间
