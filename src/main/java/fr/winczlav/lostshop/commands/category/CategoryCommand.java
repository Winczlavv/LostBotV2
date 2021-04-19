package fr.winczlav.lostshop.commands.category;

import fr.winczlav.lostshop.commands.category.categoryAdd.CategoryAddCommand;
import fr.winczlav.lostshop.commands.category.categoryAdd.CategoryAddManager;
import fr.winczlav.lostshop.commands.item.ItemManager;
import fr.winczlav.lostshop.utils.Info;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import fr.winczlav.gao.api.command.ICommand;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class CategoryCommand extends ICommand {

    public CategoryCommand(String command, CategoryAddManager categoryAddManager, CategoryManager categoryManager, ItemManager itemManager) {
        super(command, Info.marketPart);
        this.addISubCommand(new CategoryAddCommand("add", this, categoryAddManager, categoryManager));
        this.addISubCommand(new CategoryRemoveCommand("remove", this, categoryManager,itemManager));
        this.addISubCommand(new CategoryListCommand("list", this, categoryManager));
    }

    @Override
    public void onCommand(Message message, User user, Member member, TextChannel textChannel, Guild guild, String[] args) throws Exception {
        if(!(member.hasPermission(Permission.ADMINISTRATOR))){
            EmbedBuilder builder = new EmbedBuilder()
                    .setDescription(Info.negatif_emoji + "Vous n'avez pas la permission d'exécuter cette commande.")
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(builder.build()).queue(message1 -> message1.delete().queueAfter(5, TimeUnit.SECONDS));
            message.delete().queue();
            return;
        }

        if(args.length == 0){
            EmbedBuilder itemEmbedBuilder = new EmbedBuilder()
                    .setDescription("❓ Liste des commandes disponibles :\n\n" +
                            "» /**category add** → Cette commande permet d'ajouter une catégorie au catalogue\n" +
                            "» /**category remove <Catégorie>** → Cette commande permet de retirer une catégorie du catalogue\n" +
                            "» /**category list → Cette commande permet de voir les catégories existantes \n")
                    .setColor(new Color(88, 214, 141));

            textChannel.sendMessage(itemEmbedBuilder.build()).queue();
        }
    }
}
