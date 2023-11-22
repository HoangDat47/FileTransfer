package File;

public class MyFile {
    private int id;
    private String name;
    private byte[] data;
    private String fileExtension;

    public MyFile(int id, String name, byte[] data, String fileExtension) {
        this.id = id;
        this.name = name;
        this.data = data;
        this.fileExtension = fileExtension;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    public void setData(byte[] data) {
        if (data != null) {
            this.data = data;
        }
    }

    public void setFileExtension(String fileExtension) {
        if (fileExtension != null) {
            this.fileExtension = fileExtension;
        }
    }
}
