## 【Java爬虫Git log】

此项目为个人学习使用，使用Java爬取网页 

## 提示：
1. 目前只支持B站，其他网站待添加


## 涉及到的技术：

1. 利用chromeDriver操控浏览器，操作界面元素，控制cookie等
2. 利用jsoup解析静态网页操作页面DOM
3. 利用Selenium模拟浏览器行为，操作动态页面DOM


## 运行环境：
1. JDK 1.8
2. Maven 3.6.3

## 运行方式：
1. 下载项目
2. 配置chromeDriver路径
3. 运行BilibiliCrawler类


## 调试技巧：
1. 关闭无头模式，方便调试 ，
   {code}//options.addArguments("--headless"); // 无头模式{code}
2. chromeDriver路径配置在BilibiliCrawler类中
3. chromeDriver版本要和chrome版本一致
4. chromeDriver打开的B站与普通chrome浏览器打开的网页的源码不一样，需要使用chromeDriver打开的浏览器【相当于chrome用无痕模式打开】查看B站网页源码；

## 内置功能：

1. 支持B站爬取Up主页视频URL支持下载成mp4，有下载进度条
2. 支持翻页，B站翻页是局部刷新的，这里等待当前页码的文本变为预期值页数，则判断为翻页后页面渲染成功
2. 支持视频转音频：Mp4->Mp3

