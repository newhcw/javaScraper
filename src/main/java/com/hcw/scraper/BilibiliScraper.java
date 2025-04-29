package com.hcw.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.hcw.myMp4.Mp4ToMp3;
import com.hcw.scraper.domain.BilibiliVideoInfo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;


/**
 * 抓取UP主页视频信息
 * 并且下载Mp4
 * 再将M4转成Mp3
 */
public class BilibiliScraper {

    private static ExecutorService executorService =  Executors.newFixedThreadPool(8);


    public static void main(String[] args) {
        // 设置ChromeDriver路径
        System.setProperty("webdriver.chrome.driver", "target/classes/chromedriver-win64/chromedriver.exe");
        // 配置Chrome选项
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--headless"); // 无头模式
        options.addArguments(
                "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

        ChromeDriver driver = new ChromeDriver(options);
        try {
            // 访问UP主视频页面
            String upId = "16022714";
            String url = "https://space.bilibili.com/" + upId + "/video";
            driver.get(url);

            // 等待页面加载
            driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);


            WebDriverWait waitPageTotal = new WebDriverWait(driver, 20);
            WebElement pageTotalElement = waitPageTotal
                    .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".be-pager-total")));
            String pageTotal = pageTotalElement.getText().split(" ")[1];        

            // 定义循环变量，控制翻页次数，可根据需求调整
            int maxPage = Integer.parseInt(pageTotal);
            for (int i = 0; i < maxPage; i++) {
                try {

                    if (i > 0) {
                        // 显式等待下一页按钮可点击
                        WebDriverWait wait = new WebDriverWait(driver, 20);

                        WebElement nextPageButton = wait
                                .until(ExpectedConditions.elementToBeClickable(By.cssSelector(".be-pager-next a")));
                        // 执行点击操作
                        nextPageButton.click();
                
                        // 等待页面加载
                        Thread.sleep(5000);
            
                    }

                    // 显式等待下一页按钮可点击
                    WebDriverWait wait = new WebDriverWait(driver, 20);
                    WebElement cubeLisElement = wait
                            .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".cube-list")));

                   // getHtmlContent(driver,i);

                    // 获取当前页视频标题列表
                    List<WebElement> titleElements = cubeLisElement.findElements(By.cssSelector(".fakeDanmu-item a.title"));
                    int videoCount = titleElements.size();        
                    System.out.println("总页数："+pageTotal+",当前页:"+(i+1)+"，爬取到了视频数量：" + videoCount);
                    // 筛选出直播回放的标题
                    titleElements = titleElements.stream()
                             //.filter(element -> element.getText().contains("直播回放"))
                            .collect(Collectors.toList());
                    for (int j=0;j<titleElements.size();j++) {
                        WebElement element = titleElements.get(j);
                        System.out.println("第" + j + "个视频:[" + element.getAttribute("title")+"] ,url:"+element.getAttribute("href"));
                        // String videoName = element.getAttribute("title");
                        // String href = element.getAttribute("href").toString();
                        // String bvid = href.split("/")[4];
                        // String videoPlayUrl = getVideoPlayUrl(bvid, getCid(bvid));
                        // String videoPath = downloads(videoPlayUrl, videoName);
                        // System.out.println("视频下载成功，存放路径:" + videoPath);

                        // String finalVideoPath = videoPath;
                        // String audioPath = "D:\\code\\javaScraper\\" + videoName + ".mp3";
                        // mp4ToMp3(finalVideoPath, audioPath);
                       
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    
/**
 * 获取视频的cid
 * @param bvid
 * @return
 * @throws IOException
 */
    public static String getCid(String bvid) throws IOException {
        String apiUrl = "https://api.bilibili.com/x/web-interface/view?bvid=" + bvid;
        Document doc = Jsoup.connect(apiUrl)
                .ignoreContentType(true)
                .userAgent("Mozilla/5.0...")
                .get();
        String jsonResponse = doc.text();

        Gson gson = new Gson();
        BilibiliVideoInfo videoInfo = gson.fromJson(jsonResponse, BilibiliVideoInfo.class);
        if (videoInfo.getCode() == 0 && videoInfo.getData() != null) {
            return String.valueOf(videoInfo.getData().getCid());
        }
        return "";
    }

    /*
     * 获取视频播放地址
     * @param avid 视频的avid
     * @param cid 视频的cid
     * @return 视频播放地址
     */
    public static String getVideoPlayUrl(String avid, String cid) throws IOException {
        String apiUrl = "https://api.bilibili.com/x/player/playurl";

        // 构建请求参数
        Map<String, String> params = new HashMap<>();
        params.put("bvid", avid);
        params.put("cid", cid);
        params.put("qn", "80"); // 视频清晰度参数

        // 使用Jsoup发送GET请求
        Document doc = Jsoup.connect(apiUrl)
                .ignoreContentType(true)
                .userAgent("Mozilla/5.0...")
                .data(params)
                .get();

        String json = doc.text();
        return JSON.parseObject(json).getJSONObject("data").getJSONArray("durl").getJSONObject(0).getString("url");
    }


    /*
     * 下载视频
     * @param url 视频地址
     * @param videoPath 视频存放路径
     * @return 视频存放路径
     */
    public static String downloads(String url, String videoPath) {
        String outputPath = videoPath + ".mp4";
        try {
            URL vedioUrl = new URL(url);
            // 打开连接
            HttpURLConnection imgURLConnection = (HttpURLConnection) vedioUrl.openConnection();
            // 添加必要的请求头
            imgURLConnection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
            imgURLConnection.setRequestProperty("Referer", "https://www.bilibili.com/"); // B站视频通常需要 Referer 头
            imgURLConnection.setRequestProperty("Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            imgURLConnection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");

            // 获取文件总大小
            int fileSize = imgURLConnection.getContentLength();
            if (fileSize == -1) {
                System.out.println("无法获取文件大小，可能不支持获取文件大小。");
            }

            // 获取输入流
            BufferedInputStream bis = new BufferedInputStream(imgURLConnection.getInputStream());

            // 输出流
            BufferedOutputStream bos = new BufferedOutputStream(new java.io.FileOutputStream(outputPath));

            // 已下载的字节数
            long downloadedBytes = 0;

            // 边读边写
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
                downloadedBytes += len;
                if (fileSize != -1) {
                    int progress = (int) ((downloadedBytes * 100) / fileSize);
                    System.out.print("\r下载进度: " + progress + "%");
                }
            }
            if (fileSize != -1) {
                System.out.println();
            }

            // 关闭流
            bos.close();
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputPath;
    }

    
    private static void printHtml(String html, String fileName) {
        try (java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(
                new java.io.FileOutputStream("output_" + fileName + ".html"), "UTF-8")) {
            writer.write(html);
            System.out.println("HTML content has been written to output_" + fileName + ".html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取页面的HTML内容并打印到文件中
     * @param driver
     * @param i
     * @throws IOException
     */
    private static void getHtmlContent(ChromeDriver driver, Integer i) throws IOException {
        Object o = driver.executeScript("return document.documentElement.outerHTML");
        printHtml(o.toString(), i.toString());
    }

    /**
     * 视频转MP3
     * @param videoName
     * @param finalVideoPath
     * @return
     */
    private static Future<Object> mp4ToMp3(String videoName, String finalVideoPath) {
        return executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                String audioPath = "D:\\code\\javaScraper\\" + videoName + ".mp3";
                Mp4ToMp3.videoToAudio(finalVideoPath, audioPath);
                return audioPath;
            }
        });
    }
}
