# SwipeLayout
a layout that can be drag、slide to delete and slide to get more button.In this layout,you have to write two child layout and it
will put the second one over the first,then the upper one can be draged as the mainview, the underlying one can be clicked as the
buttons.


##What it can do
* slide left to get more button 
* slide right to do delete
* **it's better to use this layout in RecyclerView so we offer you a sample in source code**

well,it just like this:

![swipepic](https://github.com/RuiDu93/SwipeLayout/blob/master/pic/swipe.gif)
###How to use
* just donwload the source code and complie it, using it as normal layout

###ATTENTION!!
&emsp;since it assumes its child layouts are one covered by another, you should write two child in the XML file otherwise it could be wrong
