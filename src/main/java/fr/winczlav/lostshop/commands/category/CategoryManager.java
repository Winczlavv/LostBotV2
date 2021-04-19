package fr.winczlav.lostshop.commands.category;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.winczlav.lostshop.Core;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryManager {

    private final Gson gson;
    private final File item;
    private final List<CategoryData> categoryData;

    public CategoryManager(Core core) {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.categoryData = new ArrayList<>();
        this.item = new File(core.getDataFolder(), "category.json");
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
            gson.toJson(this.categoryData, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(){
        try {
            final CategoryData[] modData = gson.fromJson(new FileReader(item), CategoryData[].class);
            if (modData != null) {
                this.categoryData.addAll(Arrays.asList(modData));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addCategoryData(CategoryData category){
        this.categoryData.add(category);
    }

    public void removeCategoryData(CategoryData category){
        this.categoryData.remove(category);
    }

    public boolean hasCategoryData(String name){
        for (CategoryData data : this.categoryData) {
            if(data.getName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    public CategoryData getCategoryDataByName(String name){
        for (CategoryData data : this.categoryData) {
            if(data.getName().equalsIgnoreCase(name)) return data;
        }
        return null;
    }

    public List<CategoryData> getCategoryDataList(){
        return this.categoryData;
    }
}
