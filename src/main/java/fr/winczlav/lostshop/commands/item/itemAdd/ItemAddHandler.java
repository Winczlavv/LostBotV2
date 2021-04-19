package fr.winczlav.lostshop.commands.item.itemAdd;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class ItemAddHandler extends ListenerAdapter {

    private final ItemAddManager itemAddManager;

    public ItemAddHandler(ItemAddManager itemAddManager) { this.itemAddManager = itemAddManager; }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if(event.getAuthor().isBot() || event.getAuthor().isFake()) return;
        if(itemAddManager.getMessages().containsKey(event.getMember())) itemAddManager.getMessages().get(event.getMember()).accept(event.getMember(), event.getMessage());
    }
}
