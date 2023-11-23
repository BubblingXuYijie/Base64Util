package icu.xuyijie.base64utils;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author 徐一杰
 * @date 2023/11/14 16:33
 * @description base64特殊前缀对应文件类型
 */
public enum Base64FileTypeEnum {
    // 文档文件
    DOC(".doc", "data:application/msword;base64,"),
    DOCX(".docx", "data:application/vnd.openxmlformats-officedocument.wordprocessingml.document;base64,"),
    XLS(".xls", "data:application/vnd.ms-excel;base64,"),
    XLSX(".xlsx", "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64,"),
    CSV(".csv", "data:text/csv;base64,"),
    PDF(".pdf", "data:application/pdf;base64,"),
    PPT(".ppt", "data:application/vnd.ms-powerpoint;base64,"),
    PPTX(".pptx", "data:application/vnd.openxmlformats-officedocument.presentationml.presentation;base64,"),
    SWF(".swf", "data:application/x-shockwave-flash;base64,"),
    PSD(".psd", "data:image/vnd.adobe.photoshop;base64,"),

    //安装包
    MSI(".msi", "data:application/x-ms-installer;base64,"),
    EXE(".exe", "data:application/x-msdownload;base64,"),
    EXE2(".exe", "data:application/x-dosexec;base64,"),
    MPKG(".mpkg", "data:application/vnd.apple.installer+xml;base64,"),

    //压缩文件
    ZIP(".zip", "data:application/zip;base64,"),
    ZIP2(".zip", "data:application/x-zip-compressed;base64,"),
    SEVEN_Z(".7z", "data:application/x-7z-compressed;base64,"),
    RAR(".rar", "data:application/x-rar-compressed;base64,"),
    //还可能是7z、tar.bz、tar.xz，不过没关系，就返回rar就行了，因为这些文件类型修改为rar也可以正常解压
    RAR2(".rar", "data:application/x-compressed;base64,"),
    TAR(".tar", "data:application/x-tar;base64,"),
    TAR_GZ(".tar.gz", "data:application/x-gzip;base64,"),
    BZ(".bz", "data:application/x-bzip;base64,"),
    BZ2(".bz2", "data:application/x-bzip2;base64,"),
    JAR(".jar", "data:application/java-archive;base64,"),

    //文本类型
    TXT(".txt", "data:text/plain;base64,"),
    JSON(".json", "data:application/json;base64,"),
    MD(".md", "data:text/x-web-markdown;base64,"),
    SQL(".sql", "data:text/x-sql;base64,"),
    JS(".js", "data:text/javascript;base64,"),
    JS2(".js", "data:application/javascript;base64,"),
    CSS(".css", "data:text/css;base64,"),
    HTML(".html", "data:text/html;base64,"),
    XHTML(".xhtml", "data:application/xhtml+xml;base64,"),
    XML(".xml", "data:application/xml;base64,"),
    XML2(".xml", "data:text/xml;base64,"),
    C(".c", "data:text/x-csrc;base64,"),
    CS(".cs", "data:text/x-csharp;base64,"),
    RS(".rs", "data:application/rls-services+xml;base64,"),
    PY(".py", "data:text/x-python;base64,"),
    PHP(".php", "data:text/x-php;base64,"),
    JAVA(".java", "data:text/x-java-source;base64,"),
    JAVA_PROPERTIES(".properties", "data:text/x-java-properties;base64,"),
    YAML(".yml", "data:text/x-yaml;base64,"),
    SH(".sh", "data:application/x-sh;base64,"),
    CSH(".csh", "data:application/x-csh;base64,"),
    BAT(".bat", "data:application/x-bat;base64,"),
    AZW(".azw", "data:application/vnd.amazon.ebook;base64,"),
    EPUB(".epub", "data:application/epub+zip;base64,"),

    //音频类型
    WEBA(".weba", "data:audio/webm;base64,"),
    THREE_GP_A(".3gp", "data:audio/3gpp;base64,"),
    THREE_GP_A2(".3g2", "data:audio/3gpp2;base64,"),
    MP3(".mp3", "data:audio/mpeg;base64,"),
    AAC(".aac", "data:audio/aac;base64,"),
    WAV(".wav", "data:audio/wav;base64,"),
    WAV2(".wav", "data:audio/x-wav;base64,"),
    M4A(".m4a", "data:audio/mp4;base64,"),

    //视频类型
    WEBM(".webm", "data:video/webm;base64,"),
    THREE_GP_V(".3gp", "data:video/3gpp;base64,"),
    THREE_GP_V2(".3g2", "data:video/3gpp2;base64,"),
    AVI(".avi", "data:video/x-msvideo;base64,"),
    MP4(".mp4", "data:video/mp4;base64,"),
    MPEG(".mpeg", "data:video/mpeg;base64,"),
    FLV(".flv", "data:video/x-flv;base64,"),
    MKV(".mkv", "data:video/x-matroska;base64,"),
    RMVB(".rmvb", "data:application/vnd.rn-realmedia-vbr;base64,"),

    // 图片类型
    WEBP(".webp", "data:image/webp;base64,"),
    PNG(".png", "data:image/png;base64,"),
    JPG(".jpg", "data:image/jpg;base64,"),
    JPEG(".jpg", "data:image/jpeg;base64,"),
    GIF(".gif", "data:image/gif;base64,"),
    SVG(".svg", "data:image/svg+xml;base64,"),
    ICO(".ico", "data:image/x-icon;base64,"),
    ICO2(".ico", "data:image/vnd.microsoft.icon;base64,"),
    BMP(".bmp", "data:image/bmp;base64,"),
    TIFF(".tiff", "data:image/tiff;base64,"),

    //字体文件
    TTF(".ttf", "data:font/ttf;base64,"),
    WOFF(".woff", "data:font/woff;base64,"),
    WOFF1(".woff", "data:application/x-font-woff;base64,"),
    WOFF2(".woff2", "data:font/woff2;base64,"),
    EOT(".eot", "data:application/vnd.ms-fontobject;base64,"),

    // 未知类型，这种前缀可能是rar、heic图片、md文件等等，无法判断文件类型，返回二进制文件吧
    OCTET_STREAM(".bin", "data:application/octet-stream;base64,"),
    ;

    Base64FileTypeEnum(String type, String prefix) {
        this.type = type;
        this.prefix = prefix;
    }

    private final String type;
    private final String prefix;

    public String getType() {
        return type;
    }

    public String getPrefix() {
        return prefix;
    }

    /**
     * 根据base64前缀获取文件类型
     *
     * @param prefix base64前缀，可以是完整前缀，也可以是mpeg这种不完整的
     * @return 文件类型，如.mp3
     */
    static String getFileType(String prefix) {
        Optional<Base64FileTypeEnum> optional = Stream
                .of(values())
                .filter(item -> item.getPrefix().contains(prefix))
                .findFirst();
        if (optional.isPresent()) {
            return optional.get().getType();
        }
        //没有匹配项返回prefix本身，有很多文件类型的base64前缀就是文件类型，比如png、gif、mp4等
        return Base64Utils.FILE_TYPE_SEPARATOR + prefix;
    }

    /**
     * 根据文件类型获取base64前缀
     *
     * @param fileType 文件类型
     * @return base64前缀
     */
    static String getPrefix(String fileType) {
        Optional<Base64FileTypeEnum> optional = Stream
                .of(values())
                .filter(item -> item.getType().contains(fileType))
                .findFirst();
        if (optional.isPresent()) {
            return optional.get().getPrefix();
        }
        //没有匹配项返回OCTET_STREAM
        return OCTET_STREAM.getPrefix();
    }

}
