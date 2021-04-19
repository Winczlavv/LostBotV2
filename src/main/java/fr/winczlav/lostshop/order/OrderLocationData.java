package fr.winczlav.lostshop.order;

public class OrderLocationData {

    private long textchannel;
    private long closeMessage;
    private long orderMessage;

    public OrderLocationData(long textchannel, long closeMessage, long orderMessage) {
        this.textchannel = textchannel;
        this.closeMessage = closeMessage;
        this.orderMessage = orderMessage;
    }

    public long getTextchannel() { return textchannel; }
    public void setTextchannel(long textchannel) { this.textchannel = textchannel; }

    public long getCloseMessage() { return closeMessage; }
    public void setCloseMessage(long closeMessage) { this.closeMessage = closeMessage; }

    public long getOrderMessage() { return orderMessage; }
}
