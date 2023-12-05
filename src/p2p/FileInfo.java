package p2p;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileInfo {
    private String fileName;
    private String fileOwner;
    private long fileSize;

    public FileInfo(String fileName, String fileOwner, long fileSize) {
        this.fileName = fileName;
        this.fileOwner = fileOwner;
        this.fileSize = fileSize;
    }

    public String getFilename() {
        return fileName;
    }

    public void setFilename(String fileName) {
        this.fileName = fileName;
    }

    public String getFileowner() {
        return fileOwner;
    }

    public void setFileowner(String fileOwner) {
        this.fileOwner = fileOwner;
    }

    public long getFilesize() {
        return fileSize;
    }

    public void setFilesize(long fileSize) {
        this.fileSize = fileSize;
    }

    public static void sendFileList(String path, String fileOwner) {
        File folder = new File(path);
        File[] files = folder.listFiles();
        if (files != null) {
            // Tạo danh sách để lưu trữ thông tin về file
            List<FileInfo> fileInfoList = new ArrayList<>();

            for (File file : files) {
                if (file.isFile()) {
                    // Lấy thông tin về file
                    String fileName = file.getName();
                    long fileSize = file.length();

                    // Tạo đối tượng FileInfo và thêm vào danh sách
                    FileInfo fileInfo = new FileInfo(fileName, fileOwner, fileSize);
                    fileInfoList.add(fileInfo);

                    System.out.println(fileInfo);
                }
            }
        }
    }


    @Override
    public String toString() {
        return "File{" +
                "filename='" + fileName + '\'' +
                ", fileowner='" + fileOwner + '\'' +
                ", filesize=" + fileSize +
                '}';
    }
}
