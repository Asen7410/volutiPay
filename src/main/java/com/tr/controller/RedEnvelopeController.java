package com.tr.controller;

import com.tr.annotation.PassToken;
import com.tr.entity.User;
import com.tr.service.RedEnvelopeInfoService;
import com.tr.util.CommonResponse;
import com.tr.util.DataContextHolder;
import com.tr.util.ServerCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@Api(tags = "抢红包相关接口")
@RequestMapping("/red")
public class RedEnvelopeController {

    @Autowired
    private RedEnvelopeInfoService redEnvelopeInfoService;

    @ApiOperation(value = "抢红包", notes = "抢红白", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/grabRedEnvelope")
    @PassToken
    public CommonResponse<Boolean> grabRedEnvelope(@RequestParam long hbId) {
        User user = DataContextHolder.getUserIdLocal().get();
        if (ObjectUtils.isEmpty(user)) {
            return CommonResponse.buildRespose4Fail(ServerCode.ERROR_CODE.getCode(), "用户不存在！");
        }
        return redEnvelopeInfoService.grabRedEnvelope(user.getId(), hbId);
    }

    private static final Set<String> javaClassZHSet = new HashSet<>(); // 普通代码提取的内容
    private static final Set<String> annotationsSet = new HashSet<>(); // VO注解提取的内容
    private static final Set<String> resultCodeSet = new HashSet<>(); // ResultCode提取的内容
    private static final Set<String> formatStringsSet = new HashSet<>(); // 格式化字符串提取的中文内容



    public static void main(String[] args) {
        String[] paths = {
                "C:/Users/admin/Desktop/ak"  //改成你自己的zon
        };

        for (String path : paths) {
            File dir = new File(path);
            if (dir.exists() && dir.isDirectory()) {
                scanDirectory(dir);
            } else {
                System.out.println("路径不存在或不是目录：" + path);
            }
        }

        // 单独处理 ResultCodes.java 文件
        File resultCodeFile = new File("D:/TR/ak/lottery-parent-ok-php/lottery-parent-ok-php/lottery-service/src/main/java/com/ak/lottery/service/impl/betVerify/verifyCod/ResultCode.java");
        if (resultCodeFile.exists()) {
            processResultCodesFile(resultCodeFile);
        } else {
            System.out.println("路径不存在或不是文件：" + resultCodeFile.getPath());
        }

        // 输出结果
        printResults();

        // 输出集合到文件
        writeSetToFile("D:/set/javaClassZHSet.txt", javaClassZHSet);
        writeSetToFile("D:/set/annotationsSet.txt", annotationsSet);
        writeSetToFile("D:/set/resultCodeSet.txt", resultCodeSet);
        writeSetToFile("D:/set/formatStringsSet.txt", formatStringsSet);
        writeSetToFile("D:/set/allSet.txt", combineAllSets());
    }

    private static void printResults() {
        System.out.println("普通代码提取内容: " + javaClassZHSet.size());
        System.out.println(javaClassZHSet);
        System.out.println("注解提取内容: " + annotationsSet.size());
        System.out.println(annotationsSet);
        System.out.println("ResultCode提取的内容: " + resultCodeSet.size());
        System.out.println(resultCodeSet);
        System.out.println("格式化字符串提取的中文内容: " + formatStringsSet.size());
        System.out.println(formatStringsSet);

        Set<String> allSet = combineAllSets();
        System.out.println("提取的所有的中文: " + allSet.size());
        System.out.println(allSet);
    }

    private static Set<String> combineAllSets() {
        Set<String> allSet = new HashSet<>();
        allSet.addAll(javaClassZHSet);
        allSet.addAll(annotationsSet);
        allSet.addAll(resultCodeSet);
        allSet.addAll(formatStringsSet);
        return allSet;
    }

    private static void writeSetToFile(String filePath, Set<String> set) {
        try {
            // 确保文件父目录存在
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                boolean dirsCreated = parentDir.mkdirs();
                if (!dirsCreated) {
                    System.out.println("无法创建目录：" + parentDir.getAbsolutePath());
                    return;
                }
            }

            // 写入文件
            Files.write(Paths.get(filePath), set, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("成功写入文件：" + filePath);
        } catch (IOException e) {
            System.out.println("写入文件失败：" + filePath);
            e.printStackTrace();
        }
    }

    private static void scanDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return;

        Pattern codePattern = Pattern.compile(".*(throw\\s+new|ApiAssert\\.|Assert\\.).*");
        Pattern annotationPattern = Pattern.compile("@Not(?:Blank|Null)\\s*\\(.*?message\\s*=\\s*\"([^\"]*)\"");

        // 增加格式化字符串匹配的正则
        Pattern formatPattern = Pattern.compile("String\\.format\\(\"([^\"]*?)\"|\".*?%s.*?\"");

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file);
            } else if (file.getName().endsWith(".java")) {
                processFile(file, codePattern, annotationPattern, formatPattern);
            }
        }
    }

    private static void processFile(File file, Pattern codePattern, Pattern annotationPattern, Pattern formatPattern) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean multiLine = false;
            StringBuilder multiLineBuilder = new StringBuilder();
            boolean insideMultiLineComment = false;

            while ((line = reader.readLine()) != null) {
                // 检查是否在多行注释中
                if (insideMultiLineComment) {
                    if (line.contains("*/")) {
                        insideMultiLineComment = false;  // 结束多行注释
                    }
                    continue;  // 跳过多行注释内的内容
                }

                // 检查是否是单行注释
                if (line.trim().startsWith("//")) {
                    continue;  // 跳过单行注释内容
                }

                // 处理多行注释开始
                if (line.contains("/*")) {
                    insideMultiLineComment = true;
                    continue;  // 跳过多行注释内容
                }

                // 处理普通代码中的匹配逻辑
                if (multiLine) {
                    multiLineBuilder.append(" ").append(line.trim());
                    if (line.contains(";")) {
                        processLine(multiLineBuilder.toString());
                        multiLine = false;
                        multiLineBuilder.setLength(0);
                    }
                    continue;
                }

                if (codePattern.matcher(line).find()) {
                    if (line.contains(";")) {
                        processLine(line.trim());
                    } else {
                        multiLine = true;
                        multiLineBuilder.append(line.trim());
                    }
                }

                // 处理注解中的匹配逻辑
                processAnnotations(line, annotationPattern);

                // 处理格式化字符串中的匹配逻辑
                processFormatStrings(line, formatPattern);
            }
        } catch (IOException e) {
            System.out.println("无法读取文件：" + file.getAbsolutePath());
        }
    }

    private static void processAnnotations(String line, Pattern annotationPattern) {
        // 匹配 @NotBlank 和 @NotNull 注解
        Matcher annotationMatcher = annotationPattern.matcher(line);
        if (annotationMatcher.find()) {
            String messageContent = annotationMatcher.group(1).trim();
            if (!messageContent.isEmpty()) {
                annotationsSet.add(messageContent); // 直接添加，不需要占位符替换
            }
        }

        // 匹配 @OperateLog 注解的 description 字段
        Pattern operateLogPattern = Pattern.compile("@OperateLog\\s*\\(.*?description\\s*=\\s*\"([^\"]+)\".*?\\)");
        Matcher operateLogMatcher = operateLogPattern.matcher(line);
        if (operateLogMatcher.find()) {
            String description = operateLogMatcher.group(1).trim();

            // 判断是否包含中文字符
            if (containsChinese(description)) {
                annotationsSet.add(description);  // 只有包含中文的 description 才添加
            }
        }
    }

    private static void processFormatStrings(String line, Pattern formatPattern) {
        Matcher formatMatcher = formatPattern.matcher(line);
        while (formatMatcher.find()) {
            String formatContent = formatMatcher.group(1);
            if (formatContent == null) { // 对应 "%s" 匹配
                formatContent = line.trim();
            }
            extractChineseFromFormat(formatContent);
        }
    }

    private static void extractChineseFromFormat(String content) {
        //替换占位符 %s 和 {} 占位符为 {}，避免重复
        String combinedRegex = "([\\u4e00-\\u9fa5]+|%s|\\{\\})";  // 匹配中文字符和占位符

        Pattern combinedPattern = Pattern.compile(combinedRegex);
        Matcher matcher = combinedPattern.matcher(content);

        StringBuilder resultString = new StringBuilder();

        while (matcher.find()) {
            String matchedContent = matcher.group().trim();
            if (!matchedContent.isEmpty()) {
                resultString.append(matchedContent); // 拼接中文和占位符
            }
        }

        // 只有当拼接的结果字符串不为空时，才添加到 set 中
        if (resultString.length() > 0) {
            formatStringsSet.add(resultString.toString());  // 添加完整的格式化字符串
        }
    }

    private static String processStringWithPlaceholders(String content) {
        // 替换 %s 和 {} 占位符为 {}，避免重复
        content = content.replaceAll("%s", "{}");
        content = content.replaceAll("\\{\\}", "{}");
        return content;
    }

    private static void processLine(String line) {
        int startIndex = line.indexOf('(');
        int endIndex = line.lastIndexOf(';');

        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            String content = line.substring(startIndex + 1, endIndex);
            String result = extractMessage(content);
            if (!result.isEmpty()) {
                String replace = result.replace("{}{}", "{}");
                // 只将包含中文的内容添加到集合中
                if (containsChinese(replace)) {
                    javaClassZHSet.add(replace);
                }
            }
        }
    }

    private static String extractMessage(String content) {
        StringBuilder result = new StringBuilder();
        boolean initialLeftElement = false;

        // 使用正则表达式匹配字符串字面量
        String regex = "\"(.*?)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        int lastEnd = 0;
        boolean isFirstMatch = true;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            // 检查左边是否有元素
            String left = content.substring(lastEnd, start).trim();
            if (!left.isEmpty() && !left.equals("+")) {
                if (isFirstMatch) {
                    initialLeftElement = true;
                } else {
                    result.append("{}");
                }
            }

            // 添加字符串内容
            result.append(matcher.group(1));

            lastEnd = end;
            isFirstMatch = false;
        }

        if (initialLeftElement && !result.toString().startsWith("{}")) {
            result.insert(0, "{}");
        }

        return result.toString();
    }

    private static boolean containsChinese(String content) {
        // 使用正则表达式检查内容是否包含中文字符
        String chineseRegex = "[\\u4e00-\\u9fa5]+";
        Pattern chinesePattern = Pattern.compile(chineseRegex);
        Matcher matcher = chinesePattern.matcher(content);
        return matcher.find();
    }

    private static void processResultCodesFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int startIndex = line.indexOf('(');
                int endIndex = line.indexOf(')', startIndex);

                if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                    String content = line.substring(startIndex + 1, endIndex);

                    // 使用正则表达式提取双引号内的内容
                    String regex = "\"(.*?)\"";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(content);

                    // 提取双引号中的内容并添加到集合中
                    while (matcher.find()) {
                        String extractedContent = matcher.group(1);
                        if (!extractedContent.isEmpty()) {
                            resultCodeSet.add(extractedContent.trim());
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("无法读取文件：" + file.getAbsolutePath());
        }
    }
}


