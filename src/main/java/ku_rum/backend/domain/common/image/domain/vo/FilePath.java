package ku_rum.backend.domain.common.image.domain.vo;


import java.util.UUID;

public record FilePath(String prefix, String fileName) {
    public static String createPath(String prefix, String fileName) {
        String fileId = UUID.randomUUID().toString();
        return String.format("%s/%s", prefix, fileId + "-" + fileName);
    }
}
