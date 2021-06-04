package morales.david.server.models.packets;

import java.util.HashMap;
import java.util.Map;

public class PacketBuilder {

    private String type;

    private Map<String, Object> arguments;

    /**
     * Empty constructor of PacketBuilder
     */
    public PacketBuilder() {
        this.type = "";
        this.arguments = new HashMap<>();
    }

    /**
     * Set type of the packetBuilder
     * @param type
     * @return type
     */
    public PacketBuilder ofType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Add argument to the packetBuilder
     * @param key
     * @param value
     * @return
     */
    public PacketBuilder addArgument(String key, Object value) {
        this.arguments.put(key, value);
        return this;
    }

    /**
     * Create a new instance of Packet with the parameters setted in the packerBuilder
     * @return packet object
     */
    public Packet build() {
        return new Packet(type, arguments);
    }

}
