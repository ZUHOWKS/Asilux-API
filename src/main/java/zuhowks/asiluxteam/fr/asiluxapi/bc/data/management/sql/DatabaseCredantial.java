package zuhowks.asiluxteam.fr.asiluxapi.bc.data.management.sql;

public class DatabaseCredantial {
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    public DatabaseCredantial(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public String toURL() {
        final StringBuilder sb = new StringBuilder();

        sb.append("jdbc:mysql://")
                .append(host)
                .append(":")
                .append(port)
                .append("/")
                .append(database);

        return sb.toString();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
