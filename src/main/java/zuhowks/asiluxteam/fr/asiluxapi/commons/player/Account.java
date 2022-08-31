package zuhowks.asiluxteam.fr.asiluxapi.commons.player;

import java.util.UUID;

public class Account implements Cloneable {

    private UUID uuid;
    private String rank;
    private int coins;
    private int level;
    private int mmr;
    private String lang;

    public Account () {
    }

    public Account(UUID uuid, String rank, int coins, int level, int xp, int mmr, String lang) {
        this.uuid = uuid;
        this.rank = rank;
        this.coins = coins;
        this.level = level;
        this.xp = xp;
        this.mmr = mmr;
        this.lang = lang;
    }

    private int xp;

    public UUID getUuid() {
        return uuid;
    }

    public String getRank() {
        return rank;
    }

    public int getCoins() {
        return coins;
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public int getMMR() {
        return mmr;
    }

    public String getLang() {
        return lang;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setXp(int xp) {
        this.xp = xp;
        this.setLevel(this.xp / (1000 * (this.getLevel()^2)) >= 1 ? this.getLevel() + 1 : this.getLevel());
    }

    public void setMmr(int mmr) {
        this.mmr = mmr;
    }

    public void addCoins(int coins) {
        this.coins += coins;
    }

    public void addXp(int coins) { this.coins += coins; }

    public boolean equals (Object o) {
        if (o == null || !(o instanceof Account)) {
            return false;
        }
        return ((Account) o).getUuid() == this.uuid;
    }

    public Account clone() {
        try {
            return (Account) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
