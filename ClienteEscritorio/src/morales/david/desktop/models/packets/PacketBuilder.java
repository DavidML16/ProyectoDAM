package morales.david.desktop.models.packets;

import java.util.HashMap;
import java.util.Map;

public class PacketBuilder {

    private String type;

    private Map<String, Object> arguments;

    public PacketBuilder() {
        this.type = "";
        this.arguments = new HashMap<>();
    }

    public PacketBuilder ofType(String type) {
        this.type = type;
        return this;
    }

    public PacketBuilder addArgument(String key, Object value) {
        this.arguments.put(key, value);
        return this;
    }

    public boolean hasArgument(String key) {
        return arguments.containsKey(key);
    }

    public Packet build() {
        return new Packet(type, arguments);
    }

}
