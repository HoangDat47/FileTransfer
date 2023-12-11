package p2p;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileInfo {
    private String fileName;
    private String fileOwner;
    private int fileSize;


    public FileInfo(String fileName, String fileOwner, int fileSize) {
        this.fileName = fileName;
        this.fileOwner = fileOwner;
        this.fileSize = fileSize;
    }



    public static List<FileInfo> getFileInsideFolder(String path, String fileOwner) {
        List<FileInfo> fileInfoList = new ArrayList<>();
        File folder = new File(path);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    // Get information about the file
                    String fileName = file.getName();
                    int fileSize = (int) file.length();

                    // Create FileInfo object and add it to the list
                    FileInfo fileInfo = new FileInfo(fileName, fileOwner, fileSize);
                    fileInfoList.add(fileInfo);

                    System.out.println(fileInfo);
                }
            }
        }
        return fileInfoList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileInfo fileInfo = (FileInfo) o;
        return fileSize == fileInfo.fileSize &&
                fileName.equals(fileInfo.fileName) &&
                fileOwner.equals(fileInfo.fileOwner);
    }

    public static String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }

    public static int formatSizeToBytes(String size) {
        String[] parts = size.split("\\s+");
        double value = Double.parseDouble(parts[0]);
        String unit = parts[1].toLowerCase();

        switch (unit) {
            case "b":
                return (int) value;
            case "kb":
                return (int) (value * 1024);
            case "mb":
                return (int) (value * 1024 * 1024);
            case "gb":
                return (int) (value * 1024 * 1024 * 1024);
            default:
                throw new IllegalArgumentException("Invalid size unit");
        }
    }

    public String toString(String delimiter) {
        return fileName + delimiter  + fileOwner + delimiter  + fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileOwner() {
        return fileOwner;
    }

    public int getFileSize() {
        return fileSize;
    }
}
