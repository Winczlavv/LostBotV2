package fr.winczlav.lostshop.order;

import fr.winczlav.lostshop.Core;

import java.util.Objects;

public class OrderData {

    private final long buyer;
    private long salesman;
    private final int amount;
    private final String item;
    private final String emoteName;
    private final String catalogueName;
    private final long emoteID;
    private final int price;
    private final int min;
    private final int max;
    private final long time;
    private String id;
    private OrderStatus orderStatus;
    private OrderLocationData orderLocationData;

    public OrderData(long buyer, int amount, String item, String emoteName, long emoteID, int price, String catalogueName, int min, int max) {
        this.buyer = buyer;
        this.amount = amount;
        this.item = item;
        this.catalogueName = catalogueName;
        this.emoteName = emoteName;
        this.emoteID = emoteID;
        this.price = price;
        this.min = min;
        this.max = max;
        this.time = System.currentTimeMillis();
        this.orderStatus = OrderStatus.WAITING;
    }

    public final long getBuyer() { return buyer; }
    public final int getAmount() { return amount; }
    public final String getItem() { return item; }
    public final String getCatalogueName() { return catalogueName; }
    public final String getEmoteName() { return emoteName; }
    public long getEmoteID() { return emoteID; }
    public final long getTime() { return time; }
    public int getMin() { return min; }
    public int getMax() { return max; }

    public int getPrice() { return price; }


    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        if (orderStatus == OrderStatus.COMPLETE || orderStatus == OrderStatus.CANCELED) Core.getInstance().getOrderManager().getOrderData().removeIf(orderData -> Objects.equals(orderData.getId(), id));
    }

    public long getSalesman() { return salesman; }
    public void setSalesman(long salesman) { this.salesman = salesman; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public OrderLocationData getOrderLocationData() { return orderLocationData; }
    public void setOrderLocationData(OrderLocationData channelData) { this.orderLocationData = channelData; }
}
