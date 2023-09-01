package icu.xuyijie.base64utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 徐一杰
 * @date 2022/10/11
 * 将 Base64 解码生成文件保存到指定路径，或将文件转换成 Base64 码
 * // 将文件编码成Base64，可传入文件全路径，或者一个 File 对象
 * String s = Base64Util.transferToBase64("D:/下载/Screenshot_20221008-090627.png");
 * File file = new File(filePath);
 * String s = Base64Util.transferToBase64(file);
 * System.out.println(s);
 * // 将Base64转换成文件保存到指定位置，可传入文件全路径或者分别传入保存位置和文件名
 * String s1 = Base64Util.generateFile(s, "D:/下载/aaa.png");
 * String s1 = Base64Util.generateFile(s, "D:/下载", "aaa.png");
 * System.out.println(s1);
 */
public class Base64Utils {

    private Base64Utils() {

    }

    private static final Logger logger = LoggerFactory.getLogger(Base64Utils.class);
    private static final String WINDOWS_FILE_SEPARATOR = "\\";
    private static final String LINUX_FILE_SEPARATOR = "/";
    private static final String BASE64_MAP_KEY = "base64";
    private static final String FILEPATH_MAP_KEY = "filePath";

    /**
     * 将文件转换成 Base64 码
     *
     * @param filePath 文件全路径
     * @return base64码
     */
    public static String transferToBase64(String filePath) throws IOException {
        File file = new File(filePath);
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return Base64.encodeBase64String(fileContent);
    }

    /**
     * 将文件转换成 Base64 码
     *
     * @param file 文件对象
     * @return base64码
     */
    public static String transferToBase64(File file) throws IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return Base64.encodeBase64String(fileContent);
    }

    /**
     * 处理 base64 前缀和 filePath 后缀名
     *
     * @param base64Str base64文本
     * @param filePath  文件全路径
     * @return 处理过的base64文本和文件路径
     */
    private static Map<String, String> handler(String base64Str, String filePath) {
        // 如果文件数据为空
        if (StringUtils.isBlank(base64Str)) {
            return Collections.emptyMap();
        }
        // 去掉base64码的前缀，不去掉生成的文件会损坏
        String base64 = StringUtils.substringAfter(base64Str, ";base64,");
        // 如果传入的base64没有前缀
        if (StringUtils.isEmpty(base64)) {
            base64 = base64Str;
        }
        // 获取文件后缀名
        String fileName = StringUtils.substringAfterLast(filePath, WINDOWS_FILE_SEPARATOR);
        if (StringUtils.isEmpty(fileName)) {
            fileName = StringUtils.substringAfterLast(filePath, LINUX_FILE_SEPARATOR);
        }
        String suffix;
        // 如果传入的文件名没有后缀
        if (!fileName.contains(".")) {
            //从未处理过的base64码中判断文件类型
            suffix = StringUtils.substringBetween(base64Str, "/", ";");
            if (StringUtils.isEmpty(suffix)) {
                filePath += ".png";
                logger.warn("传入的base64没有前缀，并且传入的文件名没有扩展名，所以无法确定文件类型，默认以png格式保存");
            } else {
                //做文件后缀名检测，因为base64的一个缺点就是后缀名不能自动生成，有些特殊后缀无法直接获取
                if (suffix.contains("wordprocessing")) {
                    filePath += ".docx";
                } else if (suffix.contains("presentation")) {
                    filePath += ".pptx";
                } else if (suffix.contains("spreadsheet")) {
                    filePath += ".xlsx";
                } else if (suffix.contains("excel")) {
                    filePath += ".xls";
                } else if (suffix.contains("word")) {
                    filePath += ".doc";
                } else if (suffix.contains("powerpoint")) {
                    filePath += ".ppt";
                } else if (suffix.contains("octet-stream")) {
                    filePath += ".rar";
                } else if (suffix.contains("zip")) {
                    filePath += ".zip";
                } else if (suffix.contains("plain")) {
                    filePath += ".txt";
                } else {
                    filePath += "." + suffix;
                }
            }
        }
        Map<String, String> map = new HashMap<>(4);
        map.put(BASE64_MAP_KEY, base64);
        map.put(FILEPATH_MAP_KEY, filePath);
        return map;
    }

    /**
     * 保存文件
     *
     * @param base64   文件base64
     * @param filePath 保存路径
     */
    private static void saveFile(String base64, String filePath) {
        try {
            // Base64解码
            byte[] bytes = Base64.decodeBase64(base64);
            for (int i = 0; i < bytes.length; ++i) {
                // 调整异常数据
                if (bytes[i] < 0) {
                    bytes[i] += (byte) 256;
                }
            }
            // 生成文件
            try (BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(Paths.get(filePath)))) {
                out.write(bytes);
                out.flush();
            }
        } catch (Exception e) {
            logger.error("文件保存失败", e);
        }
    }

    /**
     * 进行Base64解码并生成文件保存到指定路径
     * imgFilePath 待保存的本地路径
     *
     * @param base64Str  base64码
     * @param folderPath 要保存的文件位置（不含文件名）
     * @param fileName   文件名
     * @return 保存的文件全路径（含文件名）
     */
    public static String generateFile(String base64Str, String folderPath, String fileName) {
        // 设置文件要保存的全路径
        String filePath;
        if (folderPath.endsWith(WINDOWS_FILE_SEPARATOR) || folderPath.endsWith(LINUX_FILE_SEPARATOR)) {
            filePath = folderPath + fileName;
        } else {
            filePath = folderPath + LINUX_FILE_SEPARATOR + fileName;
        }
        Map<String, String> map = handler(base64Str, filePath);
        if (map.isEmpty()) {
            logger.error("base64为空，保存失败");
            return "";
        }
        filePath = map.get(FILEPATH_MAP_KEY);
        String base64 = map.get(BASE64_MAP_KEY);
        //如果路径不存在就创建文件目录
        File dir = new File(folderPath);
        if (!dir.isDirectory() && !dir.mkdirs()) {
            logger.error("保存文件夹创建失败：{}", folderPath);
        }
        //保存文件
        saveFile(base64, filePath);
        // 返回文件保存路径
        return filePath;
    }

    /**
     * 进行Base64解码并生成文件保存到指定路径
     * imgFilePath 待保存的本地路径
     *
     * @param base64Str base64码
     * @param filePath  要保存的文件全路径（含文件名）
     * @return 保存的文件全路径（含文件名）
     */
    public static String generateFile(String base64Str, String filePath) {
        Map<String, String> map = handler(base64Str, filePath);
        if (map.isEmpty()) {
            logger.error("base64为空，保存失败");
            return "";
        }
        filePath = map.get(FILEPATH_MAP_KEY);
        String base64 = map.get(BASE64_MAP_KEY);
        //获取保存目录
        String folderPath = StringUtils.substringBeforeLast(filePath, LINUX_FILE_SEPARATOR);
        File dir = new File(folderPath);
        //如果路径不存在就创建文件目录
        if (!dir.isDirectory() && !dir.mkdirs()) {
            // 如果用户传入的filePath比较奇怪，例如D:/aa\aaa.png，那么就有可能生成文件失败，我们要再次处理一下全路径
            folderPath = StringUtils.substringBeforeLast(filePath, WINDOWS_FILE_SEPARATOR);
            dir = new File(folderPath);
            if (!dir.isDirectory() && (!dir.mkdirs())) {
                logger.error("保存文件夹创建失败：{}", folderPath);
            }
        }
        //保存文件
        saveFile(base64, filePath);
        // 返回文件保存路径
        return filePath;
    }

}
