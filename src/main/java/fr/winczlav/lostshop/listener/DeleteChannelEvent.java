package fr.winczlav.lostshop.listener;

import fr.winczlav.lostshop.order.OrderManager;
import fr.winczlav.lostshop.order.OrderStatus;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Objects;

public class DeleteChannelEvent extends ListenerAdapter {

    private OrderManager orderManager;

    public DeleteChannelEvent(OrderManager orderManager) {
        this.orderManager = orderManager;
    }

    @Override
    public void onTextChannelDelete(@Nonnull TextChannelDeleteEvent event) {
        TextChannel textChannel = event.getChannel();

        orderManager.getOrderData()
                .parallelStream()
                .filter(orderData -> Objects.nonNull(orderData.getOrderLocationData()) && orderData.getOrderLocationData().getTextchannel() == textChannel.getIdLong()).forEach(orderData -> orderData.setOrderStatus(OrderStatus.COMPLETE));
    }
}
