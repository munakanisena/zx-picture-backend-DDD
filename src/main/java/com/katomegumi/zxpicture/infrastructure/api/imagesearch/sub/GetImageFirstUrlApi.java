package com.katomegumi.zxpicture.infrastructure.api.imagesearch.sub;

import com.katomegumi.zxpicture.infrastructure.exception.BusinessException;
import com.katomegumi.zxpicture.infrastructure.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取以图搜图 图片文档 步骤二
 */
@Slf4j
public class GetImageFirstUrlApi {
    public static String getImageFirstUrl(String url) {
        try {
            //获取文档内容
            Document document = Jsoup.connect(url)
                    .timeout(5000) //5秒钟
                    .get();
            // 获取所有 <script> 标签
            Elements scriptElements = document.getElementsByTag("script");

            // 遍历找到包含 `firstUrl` 的脚本内容
            for (Element script : scriptElements) {
                String scriptContent = script.html();
                if (scriptContent.contains("\"firstUrl\"")) {
                    // 正则表达式提取 firstUrl 的值
                    Pattern pattern = Pattern.compile("\"firstUrl\"\\s*:\\s*\"(.*?)\"");
                    Matcher matcher = pattern.matcher(scriptContent);
                    if (matcher.find()) {
                        String firstUrl = matcher.group(1);
                        // 处理转义字符
                        firstUrl = firstUrl.replace("\\/", "/");
                        return firstUrl;
                    }
                }
            }
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未找到 url");
        } catch (Exception e) {
            log.error("搜索失败地址{}",url);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"搜索失败");
        }

    }


    /**
     * 测试运行
     * @param args
     */
    public static void main(String[] args) {
        // 请求目标 URL
        String url = "https://graph.baidu.com/s?card_key=&entrance=GENERAL&extUiData%5BisLogoShow%5D=1&f=all&isLogoShow=1&session_id=4475000568563082567&sign=12698e97cd54acd88139901741340112&tpl_from=pc";
        String imageFirstUrl = getImageFirstUrl(url);
        System.out.println("搜索成功，结果 URL：" + imageFirstUrl);
    }
}
