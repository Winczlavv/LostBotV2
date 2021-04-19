package fr.winczlav.lostshop.commands.item;

import fr.winczlav.gao.api.command.ISubCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import fr.winczlav.gao.api.command.ICommand;

import java.awt.*;

public class ItemAliasesCommand extends ISubCommand {

    ItemAliasesCommand(String command, ICommand iCommand, ItemManager itemManager) {
        super(command, iCommand);

        this.addISubCommand(new ItemAliasesAddCommand("add", iCommand, itemManager));
        this.addISubCommand(new ItemAliasesRemoveCommand("remove", iCommand, itemManager));
    }

    @Override
    public void onCommand(Message message, User user, Member member, TextChannel textChannel, Guild guild, String[] args) {
        if(args.length == 0){
            EmbedBuilder itemEmbedBuilder = new EmbedBuilder()
                    .setDescription("❓ Liste des commandes disponibles :\n\n" +
                            "» /**item add** → Cette commande permet d'ajouter un item au catalogue\n" +
                            "» /**item remove** <Item> → Cette commande permet de retirer un item du catalogue\n" +
                            "» /**item aliases add <Item> <Aliase>** → Cette commande permet d'ajouter un aliase\n" +
                            "» **item aliases remove <Aliase>** → Cette commande permet de retirer un aliase\n")
                    .setColor(new Color(88, 214, 141));

            textChannel.sendMessage(itemEmbedBuilder.build()).queue();
        }
    }
}
