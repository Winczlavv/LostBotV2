package fr.winczlav.lostshop.commands.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.winczlav.lostshop.Core;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemManager {

    private final Gson gson;
    private final File item;
    private final List<ItemData> itemsData;

    public ItemManager(Core core) {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.itemsData = new ArrayList<>();
        this.item = new File(core.getDataFolder(), "item.json");
        if (!item.exists()) {
            try {
                item.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save(){
        try {
            Writer writer = new FileWriter(item);
            gson.toJson(this.itemsData, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(){
        try {
            final ItemData[] modData = gson.fromJson(new FileReader(item), ItemData[].class);
            if (modData != null) {
                this.itemsData.addAll(Arrays.asList(modData));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addItemData(ItemData itemData){
        this.itemsData.add(itemData);
    }

    public void removeItemData(ItemData item){
        this.itemsData.remove(item);
    }

    public boolean hasItemData(String name){
        for (ItemData data : this.itemsData) {
            if(data.getItem().equalsIgnoreCase(name) || data.getCatalogueName().equalsIgnoreCase(name)) return true;
            for (int i = 0; i < data.getAliases().size(); i++) {
                if (data.getAliases().get(i).equalsIgnoreCase(name)) return true;
            }
        }
        return false;
    }

    public ItemData getItemDataByName(String name){
        for (ItemData data : this.itemsData) {
            if(data.getItem().equalsIgnoreCase(name) || data.getCatalogueName().equalsIgnoreCase(name)) return data;
            for (int i = 0; i < data.getAliases().size(); i++) {
                if (data.getAliases().get(i).equalsIgnoreCase(name)) return data;
            }
        }
        return null;
    }

    public List<ItemData> getItemsDataList(){
        return this.itemsData;
    }

}
