package p2p;

import java.io.File;
import java.util.ArrayList;

public class FileInfo {
    private String fileName;
    private String fileOwner;
    private int fileSize;


    public FileInfo(String fileName, String fileOwner, int fileSize) {
        this.fileName = fileName;
        this.fileOwner = fileOwner;
        this.fileSize = fileSize;
    }

    /*public static FileInfo fromString(String message) {
        String[] parts = message.split(":");
        String type = parts[0];
        String fileName = parts[1];
        String fileOwner = parts[2];
        long fileSize = Long.parseLong(parts[3]);
        return new FileInfo(type, fileName, fileOwner, fileSize);
    }*/


    public static void getFileInsideFolder(String path, String fileOwner) {
        File folder = new File(path);
        File[] files = folder.listFiles();
        if (files != null) {
            // Tạo danh sách để lưu trữ thông tin về file
            Client.fileInfoList = new ArrayList<>();

            for (File file : files) {
                if (file.isFile()) {
                    // Lấy thông tin về file
                    String fileName = file.getName();
                    int fileSize = (int) file.length();

                    // Tạo đối tượng FileInfo và thêm vào danh sách
                    FileInfo fileInfo = new FileInfo(fileName, fileOwner, fileSize);
                    Client.fileInfoList.add(fileInfo);

                    System.out.println(fileInfo);
                }
            }
        }
    }
    public String toString(String delimiter) {
        return fileName + delimiter  + fileOwner + delimiter  + fileSize;
    }
}
