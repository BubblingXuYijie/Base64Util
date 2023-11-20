package icu.xuyijie.base64utils;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author 徐一杰
 * @date 2022/10/11
 * 将 Base64 解码生成文件保存到指定路径，或将文件转换成 Base64 码，第二个参数是是否生成前缀
 * // 将文件编码成Base64，可传入文件全路径，或者一个 File 对象
 * String s = Base64Util.transferToBase64("D:/下载/Screenshot_20221008-090627.png", false);
 * File file1 = new File(filePath);
 * String s = Base64Util.transferToBase64(file, false);
 * System.out.println(s);
 * // 将Base64转换成文件保存到指定位置，可传入文件全路径或者分别传入保存位置和文件名
 * String s1 = Base64Util.generateFile(s, "D:/下载/aaa.png");
 * String s1 = Base64Util.generateFile(s, "D:/下载", "aaa.png");
 * System.out.println(s1);
 * //也可以从base64获取文件对象呵呵流，或者文件类型
 * File file2 = Base64Util.getFile("D:/下载/a.png");
 * FileInputStream fileInputStream1 = Base64Util.getFileStream("D:/下载/a.png");
 * String fileType = Base64Util.getFileType("base64Str");
 */
public class Base64Utils {
    private Base64Utils() {

    }

    private static final Logger logger = Logger.getLogger(Base64Utils.class.getPackage().getName());
    private static final String WINDOWS_FILE_SEPARATOR = "\\";
    private static final String LINUX_FILE_SEPARATOR = "/";
    protected static final String FILE_TYPE_SEPARATOR = ".";
    private static final String BASE64_MAP_KEY = "base64";
    private static final String FILEPATH_MAP_KEY = "filePath";
    private static final String BASE64_PREFIX_SUBSTRING = ";base64,";

    /**
     * 将文件转换成 Base64 码
     *
     * @param filePath  文件全路径
     * @param hasPrefix 是否需要生成前缀
     * @return base64码
     */
    public static String transferToBase64(String filePath, boolean hasPrefix) {
        return transferToBase64(new File(filePath), hasPrefix);
    }

    /**
     * 将文件转换成 Base64 码
     *
     * @param file      文件对象
     * @param hasPrefix 是否需要生成前缀
     * @return base64码
     */
    public static String transferToBase64(File file, boolean hasPrefix) {
        byte[] fileContent = new byte[0];
        try {
            fileContent = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            logger.log(Level.WARNING, "读取文件失败——", e);
        }
        String base64 = Base64.getEncoder().encodeToString(fileContent);
        if (hasPrefix) {
            String fileName = file.getName();
            String fileType = fileName.substring(fileName.lastIndexOf(FILE_TYPE_SEPARATOR));
            base64 = Base64FileTypeEnum.getPrefix(fileType) + base64;
        }
        return base64;
    }

    /**
     * 处理 base64 前缀和 filePath 后缀名
     *
     * @param base64Str base64文本
     * @param filePath  文件全路径
     * @return 处理过的base64文本和文件路径
     */
    private static Map<String, String> handler(String base64Str, String filePath) {
        String base64NoPrefix = getNoPrefixBase64(base64Str);
        // 如果文件数据为空
        if (StringUtils.isEmpty(base64NoPrefix)) {
            return Collections.emptyMap();
        }

        // 获取文件后缀名
        String fileName = StringUtils.substringAfterLast(filePath, WINDOWS_FILE_SEPARATOR);
        if (StringUtils.isEmpty(fileName)) {
            fileName = StringUtils.substringAfterLast(filePath, LINUX_FILE_SEPARATOR);
        }
        // 如果传入的文件名没有后缀，就解析base64获取文件类型
        if (!fileName.contains(FILE_TYPE_SEPARATOR)) {
            String fileType = getFileType(base64Str);
            filePath += fileType;
        }

        Map<String, String> map = new HashMap<>(4, 1);
        map.put(BASE64_MAP_KEY, base64NoPrefix);
        map.put(FILEPATH_MAP_KEY, filePath);
        return map;
    }

    /**
     * 去除base64前缀
     *
     * @param base64Str base64
     * @return 去除前缀的base64
     */
    public static String getNoPrefixBase64(String base64Str) {
        // 如果文件数据为空
        if (StringUtils.isBlank(base64Str)) {
            logger.log(Level.WARNING, "传入的base64为空");
            return "";
        }
        // 去掉base64码的前缀，不去掉生成的文件会损坏
        String base64NoPrefix = StringUtils.substringAfter(base64Str, BASE64_PREFIX_SUBSTRING);
        // 如果传入的base64没有前缀
        if (StringUtils.isEmpty(base64NoPrefix)) {
            base64NoPrefix = base64Str;
        }
        return base64NoPrefix;
    }

    /**
     * 解析base64获取文件类型
     *
     * @param base64Str base64
     * @return 文件类型
     */
    public static String getFileType(String base64Str) {
        String fileType;
        //从未处理过的base64码中判断文件类型
        String base64Prefix = StringUtils.substringBetween(base64Str, "/", BASE64_PREFIX_SUBSTRING);
        if (StringUtils.isEmpty(base64Prefix)) {
            fileType = ".png";
            logger.log(Level.WARNING, "传入的base64没有前缀，并且传入的文件名没有扩展名，所以无法确定文件类型，默认png格式");
        } else {
            //做文件后缀名检测，因为base64的一个缺点就是后缀名不能自动生成，有些特殊后缀无法直接获取
            fileType = Base64FileTypeEnum.getFileType(base64Prefix);
        }
        return fileType;
    }

    /**
     * base64转为文件对象
     *
     * @param base64Str base64
     * @return 文件流对象
     */
    public static File getFile(String base64Str) {
        try {
            String fileType = getFileType(base64Str);
            String base64NoPrefix = getNoPrefixBase64(base64Str);
            // Base64解码
            byte[] bytes = decodeBase64(base64NoPrefix);
            // 生成临时文件
            File tempFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), fileType);
            try (BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(tempFile.toPath()))) {
                out.write(bytes);
                out.flush();
            }
            tempFile.deleteOnExit();
            return tempFile;
        } catch (Exception e) {
            logger.log(Level.WARNING, "base64转换文件对象失败——", e);
        }
        return null;
    }

    /**
     * base64转为文件流
     *
     * @param base64Str base64
     * @return 文件流
     */
    public static FileInputStream getFileStream(String base64Str) {
        File tempFile = getFile(base64Str);
        if (tempFile != null) {
            try {
                return new FileInputStream(tempFile);
            } catch (Exception e) {
                logger.log(Level.WARNING, "base64转换文件流失败——", e);
            }
        }
        return null;
    }

    /**
     * 保存文件
     *
     * @param base64NoPrefix 无前缀文件base64
     * @param filePath       保存路径
     */
    private static void saveFile(String base64NoPrefix, String filePath) {
        try {
            // Base64解码
            byte[] bytes = decodeBase64(base64NoPrefix);
            // 生成文件
            try (BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(Paths.get(filePath)))) {
                out.write(bytes);
                out.flush();
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "文件保存失败——", e);
        }
    }

    /**
     * Base64解码字节数组
     *
     * @param base64Str Base64
     * @return 字节数组
     */
    private static byte[] decodeBase64(String base64Str) {
        // Base64解码
        byte[] bytes = Base64.getDecoder().decode(base64Str);
        for (int i = 0; i < bytes.length; ++i) {
            // 调整异常数据
            if (bytes[i] < 0) {
                bytes[i] += (byte) 256;
            }
        }
        return bytes;
    }

    /**
     * 进行Base64解码并生成文件保存到指定路径
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
            logger.log(Level.WARNING, "base64为空，保存失败");
            return "";
        }
        filePath = map.get(FILEPATH_MAP_KEY);
        String base64NoPrefix = map.get(BASE64_MAP_KEY);
        //如果路径不存在就创建文件目录
        File dir = new File(folderPath);
        if (!dir.isDirectory() && !dir.mkdirs()) {
            logger.log(Level.WARNING, "保存文件夹创建失败：{}", folderPath);
        }
        //保存文件
        saveFile(base64NoPrefix, filePath);
        // 返回文件保存路径
        return filePath;
    }

    /**
     * 进行Base64解码并生成文件保存到指定路径
     *
     * @param base64Str base64码
     * @param filePath  要保存的文件全路径（含文件名）
     * @return 保存的文件全路径（含文件名）
     */
    public static String generateFile(String base64Str, String filePath) {
        Map<String, String> map = handler(base64Str, filePath);
        if (map.isEmpty()) {
            logger.log(Level.WARNING, "base64为空，保存失败");
            return "";
        }
        filePath = map.get(FILEPATH_MAP_KEY);
        String base64NoPrefix = map.get(BASE64_MAP_KEY);
        //获取保存目录，因为无论什么系统，用户都可传入以/或者\风格的路径，例如D:/aa/aaa.png或D:\\aa\\aaa.png，所以先尝试使用/来获取
        String folderPath = StringUtils.substringBeforeLast(filePath, LINUX_FILE_SEPARATOR);
        File dir = new File(folderPath);
        //如果路径不存在就创建文件目录
        if (!dir.isDirectory() && !dir.mkdirs()) {
            // 如果使用/来获取生成文件失败，我们要再次尝试使用\来获取
            folderPath = StringUtils.substringBeforeLast(filePath, WINDOWS_FILE_SEPARATOR);
            dir = new File(folderPath);
            if (!dir.isDirectory() && (!dir.mkdirs())) {
                logger.log(Level.WARNING, "保存文件夹创建失败：{}", folderPath);
            }
        }
        //保存文件
        saveFile(base64NoPrefix, filePath);
        // 返回文件保存路径
        return filePath;
    }

}
