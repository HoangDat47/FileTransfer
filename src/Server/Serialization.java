package Server;

import java.io.*;
import java.util.List;

public class Serialization {
    public static byte[] serializeModelList(List<ClientModel> models) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(models);
        oos.close();
        return bos.toByteArray();
    }

    public static List<ClientModel> deserializeModelList(byte[] serializeModelList) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(serializeModelList);
        ObjectInputStream ois = new ObjectInputStream(bis);
        List<ClientModel> models = (List<ClientModel>) ois.readObject();
        ois.close();
        return models;
    }
}
