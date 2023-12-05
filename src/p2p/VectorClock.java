package p2p;

import java.util.HashMap;

public class VectorClock {
    private final HashMap<String, Integer> vectorClock;

    public VectorClock() {
        this.vectorClock = new HashMap<>();
    }

    public VectorClock(String name) {
        this.vectorClock = new HashMap<>();
        this.vectorClock.put(name, 0);
    }

    public void increment(String name) {
        if (vectorClock.containsKey(name)) {
            vectorClock.put(name, vectorClock.get(name) + 1);
        } else {
            vectorClock.put(name, 1);
        }
    }

    public static VectorClock fromString(String vc) {
        if (vc != null) {
            VectorClock vectorClock = new VectorClock();
            String[] nodes = vc.split(",");
            for (String node : nodes) {
                String[] nodeInfo = node.split("=");
                vectorClock.vectorClock.put(nodeInfo[0], Integer.parseInt(nodeInfo[1]));
            }
            return vectorClock;
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder message = new StringBuilder();
        for (String name : vectorClock.keySet()) {
            message.append(name).append("=").append(vectorClock.get(name)).append(",");
        }
        return message.toString();
    }
}
