package icu.xuyijie.base64utils;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author 徐一杰
 * @date 2023/11/14 16:33
 * @description base64特殊前缀对应文件类型
 */
public enum Base64FileTypeEnum {
    // 文件类型
    DOC(".doc", "data:application/msword;base64,"),
    DOCX(".docx", "data:application/vnd.openxmlformats-officedocument.wordprocessingml.document;base64,"),
    XLS(".xls", "data:application/vnd.ms-excel;base64,"),
    XLSX(".xlsx", "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64,"),
    PDF(".pdf", "data:application/pdf;base64,"),
    PPT(".ppt", "data:application/vnd.ms-powerpoint;base64,"),
    PPTX(".pptx", "data:application/vnd.openxmlformats-officedocument.presentationml.presentation;base64,"),
    JSON(".json", "data:application/json;base64,"),
    TXT(".txt", "data:text/plain;base64,"),
    EXE(".exe", "data:application/x-msdownload;base64,"),
    ZIP(".zip", "data:application/x-zip-compressed;base64,"),
    //还可能是7z、tar.bz、tar.xz，不过没关系，就返回rar就行了，因为这些文件类型修改为rar也可以正常解压
    RAR(".rar", "data:application/x-compressed;base64,"),
    TAR(".tar", "data:application/x-tar;base64,"),
    TAR_GZ(".tar.gz", "data:application/x-gzip;base64,"),

    //音频类型
    MP3(".mp3", "data:audio/mpeg;base64,"),

    //视频类型
    MP4(".mp4", "data:video/mp4;base64,"),

    // 图片类型
    PNG(".png", "data:image/png;base64,"),
    JPG(".jpg", "data:image/jpeg;base64,"),
    JPEG(".jpeg", "data:image/jpeg;base64,"),
    GIF(".gif", "data:image/gif;base64,"),
    SVG(".svg", "data:image/svg+xml;base64,"),
    ICO(".ico", "data:image/x-icon;base64,"),
    BMP(".bmp", "data:image/bmp;base64,"),

    // 二进制流，这种前缀可能是rar、heic图片、md文件、sql文件等等，无法判断文件类型，随便指定一个
    OCTET_STREAM(".rar", "data:application/octet-stream;base64,"),
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
