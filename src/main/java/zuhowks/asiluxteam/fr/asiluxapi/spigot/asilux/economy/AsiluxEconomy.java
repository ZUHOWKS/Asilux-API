package zuhowks.asiluxteam.fr.asiluxapi.spigot.asilux.economy;

public class AsiluxEconomy {

    private String nameSingular;
    private String namePlural;
    private String symbol;
    private boolean isEnable;

    public AsiluxEconomy(String nameSingular, String namePlural, String symbol, boolean isEnable) {
        this.nameSingular = nameSingular;
        this.namePlural = namePlural;
        this.symbol = symbol;
        this.isEnable = isEnable;
    }

    public String getNameSingular() {
        return nameSingular;
    }

    public String getNamePlural() {
        return namePlural;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean isEnable() {
        return isEnable;
    }
}
