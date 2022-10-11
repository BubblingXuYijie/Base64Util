package icu.xuyijie.base64utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: 徐一杰
 * @date: 2022/10/11
 * 将 Base64 解码生成文件保存到指定路径
 * 将文件转换成 Base64 码
 */
public class Base64Util {

    /**
     * 将文件转换成 Base64 码
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
     * @param file 文件对象
     * @return base64码
     */
    public static String transferToBase64(File file) throws IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return Base64.encodeBase64String(fileContent);
    }

    /**
     * 处理 base64 前缀和 filePath 后缀名
     * @param base64Str
     * @param filePath
     * @return
     */
    private static Map<String, String> handler(String base64Str, String filePath){
        // 去掉base64码的前缀，不去掉生成的文件会损坏
        String base64 = StringUtils.substringAfter(base64Str,";base64,");
        // 如果传入的base64没有前缀
        if (StringUtils.isEmpty(base64)){
            base64 = base64Str;
        }
        // 获取文件后缀名
        String suffix;
        // 如果传入的文件名带有后缀
        if (filePath.contains(".")){
            suffix = StringUtils.substringAfter(filePath, ".");
            filePath = StringUtils.substringBefore(filePath, ".");
        }else {
            suffix = StringUtils.substringBetween(base64Str, "/", ";");
        }
        if (StringUtils.isEmpty(suffix)){
            suffix = "png";
            System.out.println("传入的base64Str没有前缀，并且传入的fileName没有扩展名，所以无法确定文件类型，默认以png格式输出");
        }
        //做文件后缀名检测，因为base64的一个缺点就是后缀名不能自动生成，有些特殊后缀无法直接获取
        if (suffix.contains("wordprocessing")){
            filePath += ".docx";
        }else if (suffix.contains("presentation")){
            filePath += ".pptx";
        }else if (suffix.contains("spreadsheet")){
            filePath += ".xlsx";
        }else if (suffix.contains("excel")){
            filePath += ".xls";
        }else if (suffix.contains("word")){
            filePath += ".doc";
        }else if (suffix.contains("powerpoint")){
            filePath += ".ppt";
        }else if (suffix.contains("octet-stream")){
            filePath += ".rar";
        }else if (suffix.contains("zip")){
            filePath += ".zip";
        }else {
            filePath += "." + suffix;
        }
        Map<String, String> map = new HashMap<>(8);
        map.put("base64", base64);
        map.put("filePath", filePath);
        return map;
    }

    /**
     * 进行Base64解码并生成文件保存到指定路径
     * imgFilePath 待保存的本地路径
     * @param base64Str base64码
     * @param folderPath 要保存的文件位置（不含文件名）
     * @param fileName 文件名
     * @return 保存的文件全路径（含文件名）
     */
    public static String generateFile(String base64Str,String folderPath, String fileName) {
        // 设置文件要保存的全路径
        String filePath;
        if (folderPath.endsWith("\\") || folderPath.endsWith("/")){
            filePath = folderPath + fileName;
        }else {
            filePath = folderPath + "/" + fileName;
        }
        Map<String, String> map = handler(base64Str, filePath);
        filePath = map.get("filePath");
        String base64 = map.get("base64");
        //如果路径不存在就创建文件目录
        File dir = new File(folderPath);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdirs();
        }
        // 如果文件数据为空
        if (base64 == null)
        {
            return "文件为空";
        }
        try {
            // Base64解码
            byte[] bytes = Base64.decodeBase64(base64);
            for (int i = 0; i < bytes.length; ++i) {
                // 调整异常数据
                if (bytes[i] < 0) {
                    bytes[i] += 256;
                }
            }
            // 生成文件
            BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(Paths.get(filePath)));
            out.write(bytes);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 返回文件保存路径
        return filePath;
    }

    /**
     * 进行Base64解码并生成文件保存到指定路径
     * imgFilePath 待保存的本地路径
     * @param base64Str base64码
     * @param filePath 要保存的文件全路径（含文件名）
     * @return 保存的文件全路径（含文件名）
     */
    public static String generateFile(String base64Str,String filePath) {
        Map<String, String> map = handler(base64Str, filePath);
        filePath = map.get("filePath");
        String base64 = map.get("base64");
        //如果路径不存在就创建文件目录
        String folderPath = StringUtils.substringBeforeLast(filePath, "/");
        File dir = new File(folderPath);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdirs();
        }
        // 如果文件数据为空
        if (base64 == null)
        {
            return "文件为空";
        }
        try {
            // Base64解码
            byte[] bytes = Base64.decodeBase64(base64);
            for (int i = 0; i < bytes.length; ++i) {
                // 调整异常数据
                if (bytes[i] < 0) {
                    bytes[i] += 256;
                }
            }
            // 生成文件
            BufferedOutputStream out;
            // 如果用户传入的filePath比较奇怪，例如D:/aa\aaa.png，那么就有可能生成文件失败，我们要再次处理一下全路径
            try{
                out = new BufferedOutputStream(Files.newOutputStream(Paths.get(filePath)));
            } catch (Exception e){
                folderPath = StringUtils.substringBeforeLast(filePath, "\\");
                dir = new File(folderPath);
                if (!dir.exists() && !dir.isDirectory()) {
                    dir.mkdirs();
                }
                out = new BufferedOutputStream(Files.newOutputStream(Paths.get(filePath)));
            }
            out.write(bytes);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 返回文件保存路径
        return filePath;
    }

//    public static void main(String[] args) throws IOException {
//        String s = Base64Util.transferToBase64("D:/下载/Screenshot_20221008-090627.png");
//        System.out.println(s);
//        String s1 = Base64Util.generateFile(s, "D:/下载\\aaa.png");
//        System.out.println(s1);
//    }

}
