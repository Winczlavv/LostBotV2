package fr.winczlav.lostshop.commands.item;

import java.util.ArrayList;
import java.util.List;

public class ItemData {

    private final String item;
    private final String catalogueName;
    private final String category;
    private final int price;
    private final String emoteName;
    private final long emoteID;
    private final List<String> aliases;

    private final int min;
    private final int max;

    public ItemData(String item, String catalogueName, String category, int price, String emoteName, long emoteID) {
        this.item = item;
        this.catalogueName = catalogueName;
        this.category = category;
        this.price = price;
        this.emoteName = emoteName;
        this.emoteID = emoteID;
        this.aliases = new ArrayList<>();
        this.min = 1;
        this.max = 512;
    }

    public String getItem() { return item; }
    public String getCatalogueName() { return catalogueName; }
    public String getCategory() { return category; }
    public int getPrice() { return price;}
    public List<String> getAliases() { return aliases; }
    public long getEmoteID() { return emoteID; }
    public String getEmoteName() { return emoteName; }
    public int getMin() { return min; }
    public int getMax() { return max; }

}
