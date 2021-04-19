package fr.winczlav.lostshop.commands.item;

import fr.winczlav.lostshop.commands.category.CategoryManager;
import fr.winczlav.lostshop.commands.item.itemAdd.ItemAddCommand;
import fr.winczlav.lostshop.commands.item.itemAdd.ItemAddManager;
import fr.winczlav.lostshop.utils.Info;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import fr.winczlav.gao.api.command.ICommand;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class ItemCommand extends ICommand {

    public ItemCommand(String command, ItemManager itemManager, ItemAddManager itemAddManager, CategoryManager categoryManager) {
        super(command, Info.marketPart);
        this.addISubCommand(new ItemAddCommand("add", this, itemManager, itemAddManager, categoryManager));
        this.addISubCommand(new ItemRemoveCommand("remove", this, itemManager));

        this.addISubCommand(new ItemAliasesCommand("aliases", this, itemManager));
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
                            "» /**item add** → Cette commande permet d'ajouter un item au catalogue\n" +
                            "» /**item remove** <Item> → Cette commande permet de retirer un item du catalogue\n" +
                            "» /**item aliases add <Item> <Aliase>** → Cette commande permet d'ajouter un aliase\n" +
                            "» /**item aliases remove <Aliase>** → Cette commande permet de retirer un aliase\n")
                    .setColor(new Color(88, 214, 141));

            textChannel.sendMessage(itemEmbedBuilder.build()).queue();
        }
    }
}
