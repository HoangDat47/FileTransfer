package P2P;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ModelSerializationUtil {
    public static byte[] serializeModelList(List<ClientModel> clientList) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeInt(clientList.size());
            for (ClientModel client : clientList) {
                objectOutputStream.writeObject(client);
            }
            objectOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<ClientModel> deserializeModelList(byte[] serializeModelList) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializeModelList);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            int size = objectInputStream.readInt();
            List<ClientModel> clientList = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                ClientModel client = (ClientModel) objectInputStream.readObject();
                clientList.add(client);
            }
            objectInputStream.close();
            return clientList;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
