package zuhowks.asiluxteam.fr.asiluxapi.spigot.data.management.redis;

import zuhowks.asiluxteam.fr.asiluxapi.spigot.AsiluxAPI;

public class RedisCredentials {

    private String ip; //Redis server IP
    private String password; //Password to connect on redis server
    private int port;
    private int database;
    private String clientName;

    public RedisCredentials(String ip, String password, int port, int database) {
        this(ip, password, port, database, AsiluxAPI.INSTANCE.getServer().getName().replace(" ", "_"));
    }

    public RedisCredentials(String ip, String password, int port, int database, String clientName) {
        this.ip = ip;
        this.password = password;
        this.port = port;
        this.database = database;
        this.clientName = clientName;
    }

    public String getIp() {
        return ip;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public int getDatabase() {
        return database;
    }

    public String getClientName() {
        return clientName;
    }

    public String toRedisURL() {
        return "redis://" + ip + ":" + port;
    }
}
