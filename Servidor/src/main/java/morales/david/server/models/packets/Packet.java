package morales.david.server.models.packets;

import morales.david.server.Server;

import java.util.Map;

public class Packet {

    private String type;

    private Map<String, Object> arguments;

    public Packet(String type, Map<String, Object> arguments) {
        this.type = type;
        this.arguments = arguments;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }

    public Object getArgument(String key) { return arguments.get(key); }

    @Override
    public String toString() {
        return Server.GSON.toJson(this);
    }

}
