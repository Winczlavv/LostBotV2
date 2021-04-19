package fr.winczlav.lostshop.order;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.winczlav.lostshop.Core;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderLoader {

    private Core core;
    private Gson gson;
    private File order;

    public OrderLoader(Core core) {
        this.core = core;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.order = new File(core.getDataFolder(), "order.json");
        if (!order.exists()) {
            try {
                order.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<OrderData> load() {
        List<OrderData> list = new ArrayList<>();

        try {
            final OrderData[] modData = gson.fromJson(new FileReader(order), OrderData[].class);
            if (modData != null) {
                list.addAll(Arrays.asList(modData));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void save(List<OrderData> orderData) {
        try {
            Writer writer = new FileWriter(order);
            gson.toJson(orderData, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
