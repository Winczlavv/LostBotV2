package fr.winczlav.lostshop.order;

import fr.winczlav.lostshop.commands.item.ItemManager;
import fr.winczlav.lostshop.commands.item.itemAdd.ItemAddManager;
import fr.winczlav.lostshop.config.ConfigurationManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class  OrderManager {

    private final Guild guild;
    private final Category category;
    private final TextChannel textChannel;
    private final List<OrderData> orderData;
    private final OrderBuilder orderBuilder;
    private final ItemManager itemManager;

    public OrderManager(Guild guild, Category category, TextChannel textChannel, OrderLoader orderLoader, ItemManager itemManager, ItemAddManager itemAddManager) {
        this.guild = guild;
        this.itemManager = itemManager;
        this.orderData = new ArrayList<>();
        this.category = category;
        this.textChannel = textChannel;
        this.orderBuilder = new OrderBuilder(this, this.itemManager, itemAddManager);

        this.orderData.addAll(orderLoader.load());

        this.initOrders();
    }

    public OrderData createOrder(Member member, int amount, String item, String emoteName, long emoteID, int price, String catalogueName, int min, int max) {
        OrderData orderData = new OrderData(member.getIdLong(), amount, item, emoteName, emoteID, price, catalogueName, min, max);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Europe/Paris")));
        orderData.setId(String.valueOf(calendar.getTimeInMillis()));
        this.orderData.add(orderData);
        sendMessageOrder(orderData);
        return orderData;
    }


    public void sendMessageOrder(OrderData data) {
        OrderData orderData = orderBuilder.getNextOrder(data);
        if (orderData == null) return;
        EmbedBuilder builder = new EmbedBuilder();
        if (ConfigurationManager.mentionSalesRoleOnOrder)
            ConfigurationManager.salesRoles.forEach(role -> textChannel.sendMessage(String.format("<@&%s>", role)).queue());
        orderBuilder.callMessage(OrderMessage.NEWORDER, orderData, builder, guild, itemManager);
        textChannel.sendMessage(builder.build()).queue(message -> {
            orderBuilder.initReactionOrder(message, orderData);
            orderData.setOrderLocationData(new OrderLocationData(0L, 0L, message.getIdLong()));
        });
    }

    private void initOrders() {
        for (OrderData orderData : this.orderData) {
            try {
                if (orderData.getOrderStatus() == OrderStatus.WAITING && orderData.getOrderLocationData() != null) {
                    textChannel.retrieveMessageById(orderData.getOrderLocationData().getOrderMessage()).queue(message -> {
                        message.clearReactions().queue();
                        orderBuilder.initReactionOrder(message, orderData);
                    }, (error) -> error.addSuppressed(new ContextException()));
                }
            } catch (ErrorResponseException e) {
                orderData.setOrderStatus(OrderStatus.NOT_FIND);
            }
            if (orderData.getOrderStatus() == OrderStatus.PROGRESS) {
                try {
                    TextChannel channel = category.getGuild().getTextChannelById(orderData.getOrderLocationData().getTextchannel());
                    assert channel != null;
                    channel.retrieveMessageById(orderData.getOrderLocationData().getCloseMessage()).queue(message -> {
                        message.clearReactions().queue();
                        orderBuilder.initReactionClose(message, orderData);
                    });

                } catch (NullPointerException e) {
                    orderData.setOrderStatus(OrderStatus.NOT_FIND);
                }
            }

            if (orderData.getOrderStatus() == null) {
                orderData.setOrderStatus(OrderStatus.NOT_FIND);
                orderBuilder.deleteOrder(orderData.getTime());
            }
        }
    }


    public OrderBuilder getOrderBuilder() {
        return orderBuilder;
    }

    public Guild getGuild() {
        return guild;
    }

    public Category getCategory() {
        return category;
    }

    public TextChannel getTextChannel() {
        return textChannel;
    }

    public List<OrderData> getOrderData() {
        return orderData;
    }
}
