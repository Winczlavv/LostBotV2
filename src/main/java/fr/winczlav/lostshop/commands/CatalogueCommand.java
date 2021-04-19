package fr.winczlav.lostshop.commands;

import fr.winczlav.lostshop.commands.category.CategoryData;
import fr.winczlav.lostshop.commands.category.CategoryManager;
import fr.winczlav.lostshop.commands.item.ItemData;
import fr.winczlav.lostshop.commands.item.ItemManager;
import fr.winczlav.lostshop.utils.Info;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import fr.winczlav.gao.api.command.ICommand;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CatalogueCommand extends ICommand {

    private final ItemManager itemManager;
    private final CategoryManager categoryManager;

    public CatalogueCommand(String command, ItemManager itemManager, CategoryManager categoryManager) {
        super(command, Info.marketPart);
        this.itemManager = itemManager;
        this.categoryManager = categoryManager;
    }

    @Override
    public void onCommand(Message message, User user, Member member, TextChannel textChannel, Guild guild, String[] args) {
        if(!(member.hasPermission(Permission.ADMINISTRATOR))){
            EmbedBuilder builder = new EmbedBuilder()
                    .setDescription(Info.negatif_emoji + "Vous n'avez pas la permission d'exécuter cette commande.")
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(builder.build()).queue(message1 -> message1.delete().queueAfter(5, TimeUnit.SECONDS));
            message.delete().queue();
            return;
        }

        if(itemManager.getItemsDataList().isEmpty() && categoryManager.getCategoryDataList().isEmpty()){
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setDescription(Info.negatif_emoji + "La liste des items est vide.")
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(embedBuilder.build()).queue();
        } else {

            List<String> list = new ArrayList<>();
            List<String> commands = new ArrayList<>();
            AtomicInteger number = new AtomicInteger(0);

            for (CategoryData data : categoryManager.getCategoryDataList()) {
                if(!list.contains(data.getName())){
                    list.add(data.getName());
                    commands.clear();

                    for (ItemData command : itemManager.getItemsDataList()) {
                        if (command.getCategory().equalsIgnoreCase(data.getName())){
                            number.set(number.get() + 1);
                            commands.add(String.format("%s __%s__ → *%s$* | (%s)",

                                    "<:"+command.getEmoteName()+":"+command.getEmoteID()+">",
                                    command.getCatalogueName(),
                                    command.getPrice(),
                                    command.getItem().toLowerCase()));
                        }
                    }

                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(new Color(data.getR(), data.getG(), data.getB()));
                    builder.appendDescription(String.format("Catégorie : **__%s__** \n\n", data.getName()));

                    for (String command : commands) {
                        builder.appendDescription(command).appendDescription("\n");
                    }

                    textChannel.sendMessage(builder.build()).queue();
                    number.set(0);
                }
            }
        }
    }
}
