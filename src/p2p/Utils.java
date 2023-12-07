package p2p;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class  Utils {
    public static String hashMapToString(HashMap<String, Integer> nodes) {
        StringBuilder message = new StringBuilder();
        for (String name : nodes.keySet()) {
            message.append(name).append("=").append(nodes.get(name)).append(",");
        }
        return message.toString();
    }

    //chuyen list thanh string
    public static String listToString(List<FileInfo> list, String delimiter) {
        StringBuilder message = new StringBuilder();
        for (FileInfo fileInfo : list) {
            message.append(fileInfo.toString(delimiter)).append(",");
        }
        return message.toString();
    }

    // Chuyển chuỗi thành danh sách FileInfo
    public static List<FileInfo> stringToList(String message, String delimiter) {
        List<FileInfo> fileInfoList = new ArrayList<>();

        String[] fileInfoStrings = message.split(",");

        for (String fileInfoStr : fileInfoStrings) {
            System.out.println("File Info String before splitting: " + fileInfoStr); // Debugging output

            // Kiểm tra xem fileInfoStr có giá trị không rỗng
            if (!fileInfoStr.isEmpty()) {
                String[] parts = fileInfoStr.split(delimiter);
                if (parts.length >= 3) {
                    String fileName = parts[0].trim().replace("\\", ""); // Trim and remove backslashes
                    //System.out.println(fileName);

                    String fileOwner = parts[1].trim().replace("\\", "");
                    //System.out.println(fileOwner);

                    // Check if the third part is a valid number
                    try {
                        int fileSize = Integer.parseInt(parts[2].trim());
                        if (fileSize < 0) {
                            System.out.println("Invalid format for file size: " + fileSize);
                        }

                        // in ra fileSize
                        //System.out.println(fileSize);

                        // Tạo đối tượng FileInfo và thêm vào danh sách
                        FileInfo fileInfo = new FileInfo(fileName, fileOwner, fileSize);
                        fileInfoList.add(fileInfo);
                        System.out.println("in");
                        System.out.println(fileInfo);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid format for file size: " + fileInfoStr);
                        // Handle the case where the file size is not a valid number
                    }
                } else {
                    System.out.println("Invalid format: " + fileInfoStr);
                    // Handle the case where the format is invalid
                }
            }
        }

        return fileInfoList;
    }




}
