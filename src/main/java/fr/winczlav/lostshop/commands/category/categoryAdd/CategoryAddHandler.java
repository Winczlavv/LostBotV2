package fr.winczlav.lostshop.commands.category.categoryAdd;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class CategoryAddHandler extends ListenerAdapter {

    private final CategoryAddManager categoryAddManager;

    public CategoryAddHandler(CategoryAddManager categoryAddManager) { this.categoryAddManager = categoryAddManager; }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if(event.getAuthor().isBot() || event.getAuthor().isFake()) return;
        if(categoryAddManager.getMessages().containsKey(event.getMember())) categoryAddManager.getMessages().get(event.getMember()).accept(event.getMember(), event.getMessage());
    }
}
