package morales.david.server.models.packets;

import morales.david.server.Server;

import java.util.Map;

public class Packet {

    private String type;

    private Map<String, Object> arguments;

    /**
     * Create a new instance of Packet with a given type and arguments
     * @param type
     * @param arguments
     */
    public Packet(String type, Map<String, Object> arguments) {
        this.type = type;
        this.arguments = arguments;
    }

    /**
     * Get type of the packet
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Set type of the packet
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get arguments of the packet
     * @return arguments
     */
    public Map<String, Object> getArguments() {
        return arguments;
    }

    /**
     * Set arguments of the packet
     * @param arguments
     */
    public void setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }

    /**
     * Get specific argument of the packet by the given key
     * @param key
     * @return argument
     */
    public Object getArgument(String key) { return arguments.get(key); }

    @Override
    public String toString() {
        return Server.GSON.toJson(this);
    }

}
