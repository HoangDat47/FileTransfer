package p2p;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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

    //tao ham kiem tra xem file co trong folder hay khong, neu co thi tra ve string Đã tải về, neu khong thi tra ve string Chưa tải về
    public static String checkFileInsideFolder(String path, String fileName) {
        File folder = new File(path);
        File[] files = folder.listFiles();
        String result = "Chưa tải về";
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    // Get information about the file
                    String name = file.getName();
                    if (name.equals(fileName)) {
                        result = "Đã tải về";
                        break;
                    }
                }
            }
        }
        return result;
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

    //tao ham sendFile
    public static void sendFile(DatagramSocket socket, String filePath, int recipientPort) {
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            int fileLength = (int) file.length();

            InetAddress serverAddress = InetAddress.getByName("localhost");
            byte[] buffer = new byte[fileLength];

            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                DatagramPacket packet = new DatagramPacket(buffer, bytesRead, serverAddress, recipientPort);
                socket.send(packet);
            }

            //in ra path va port
            System.out.println("Đã gửi file " + filePath + " đến " + serverAddress + ":" + recipientPort);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error sending file: " + e.getMessage());
        }

    }

    //tao ham receiveFile
    public static void receiveFile(String filePath, int port) {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            byte[] fileBytes = new byte[60000];
            DatagramPacket packet = new DatagramPacket(fileBytes, fileBytes.length);
            socket.receive(packet);
            String fileName = new File(filePath).getName();
            File file = new File(filePath);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] inputByte = new byte[60000];
            DatagramPacket inputPacket = new DatagramPacket(inputByte, inputByte.length);
            socket.receive(inputPacket);
            fos.write(inputPacket.getData(), 0, inputPacket.getLength());
            System.out.println("Đã tải file " + fileName + " về");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Lỗi server: " + e.getMessage());
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
