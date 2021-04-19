package fr.winczlav.lostshop.commands.item;

import fr.winczlav.lostshop.utils.Info;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import fr.winczlav.gao.api.command.ICommand;
import fr.winczlav.gao.api.command.ISubCommand;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class ItemAliasesRemoveCommand extends ISubCommand {

    private final ItemManager itemManager;

    public ItemAliasesRemoveCommand(String command, ICommand iCommand, ItemManager itemManager) {
        super(command, iCommand);
        this.itemManager = itemManager;
        this.setDescription("encore plus la flemme");
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

        if(args.length == 1){
            EmbedBuilder builder = new EmbedBuilder()
                    .setDescription(Info.negatif_emoji + "Veuillez inclure un aliase existant.")
                    .setFooter("→ Exemple : /item aliases remove pala")
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(builder.build()).queue();
            return;
        }else if (args.length != 2) {
            EmbedBuilder builder = new EmbedBuilder()
                    .setDescription(Info.negatif_emoji + "Le nombre d'argument est invalide.")
                    .setFooter("→ Exemple : /item aliases remove pala")
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(builder.build()).queue();
            return;
        }

        String aliase = args[1];

        if(!itemManager.hasItemData(aliase)){
            EmbedBuilder builder = new EmbedBuilder()
                    .setDescription(Info.negatif_emoji + "L'aliase " + aliase + "`` n'existe pas.")
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(builder.build()).queue();
            return;
        }
        EmbedBuilder builder = new EmbedBuilder()
                .setDescription(Info.positif_emoji + "L'aliase ``" + aliase + "`` a été retiré de l'item ``" + itemManager.getItemDataByName(aliase).getItem() + "``.")
                .setColor(new Color(88, 214, 141));

        itemManager.getItemDataByName(aliase).getAliases().remove(aliase);

        textChannel.sendMessage(builder.build()).queue();
    }
}
