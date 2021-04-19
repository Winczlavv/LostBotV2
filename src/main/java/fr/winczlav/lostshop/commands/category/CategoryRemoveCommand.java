package fr.winczlav.lostshop.commands.category;

import fr.winczlav.gao.api.command.ICommand;
import fr.winczlav.gao.api.command.ISubCommand;
import fr.winczlav.lostshop.commands.item.ItemManager;
import fr.winczlav.lostshop.utils.Info;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class CategoryRemoveCommand extends ISubCommand {

    private final CategoryManager categoryManager;
    private final ItemManager itemManager;

    public CategoryRemoveCommand(String command, ICommand iCommand, CategoryManager categoryManager, ItemManager itemManager) {
        super(command, iCommand);
        this.categoryManager = categoryManager;
        this.itemManager = itemManager;
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

        if (args.length == 0) {
            EmbedBuilder builder = new EmbedBuilder()
                    .setDescription(Info.negatif_emoji + "Veuillez inclure une catégorie existante.")
                    .setFooter("→ Exemple : /category remove minerais")
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(builder.build()).queue();
            return;
        } else if (args.length != 1) {
            EmbedBuilder builder = new EmbedBuilder()
                    .setDescription(Info.negatif_emoji + "Le nombre d'arguments est invalide.")
                    .setFooter("→ Exemple : /category remove paladium")
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(builder.build()).queue();
            return;
        }

        String category = args[0];

        if (!categoryManager.hasCategoryData(category)){
            EmbedBuilder builder = new EmbedBuilder()
                    .setDescription(Info.negatif_emoji + "La catégorie ``" + category + "`` n'existe pas.")
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(builder.build()).queue();
            return;
        }

        itemManager.getItemsDataList().removeIf(itemData -> itemData.getCategory().equalsIgnoreCase(categoryManager.getCategoryDataByName(category).getName()));
        categoryManager.removeCategoryData(categoryManager.getCategoryDataByName(category));

        EmbedBuilder builder = new EmbedBuilder()
                .setDescription(Info.positif_emoji + "La carégorie ``" + category + "`` a bien été retirée du catalogue.")
                .setColor(new Color(88, 214, 141));

        textChannel.sendMessage(builder.build()).queue();
    }
}
