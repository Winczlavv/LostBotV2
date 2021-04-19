package fr.winczlav.lostshop.commands.item;

import fr.winczlav.lostshop.utils.Info;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import fr.winczlav.gao.api.command.ICommand;
import fr.winczlav.gao.api.command.ISubCommand;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class ItemRemoveCommand extends ISubCommand {

    private final ItemManager itemManager;

    public ItemRemoveCommand(String command, ICommand iCommand, ItemManager itemManager) {
        super(command, iCommand);
        this.itemManager = itemManager;
        this.setDescription("Commande de remove je fais la description plus tard");
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

        // /item remove item(1)

        if(args.length == 0){
            EmbedBuilder builder = new EmbedBuilder()
                    .setDescription(Info.negatif_emoji + "Veuillez inclure un item existant.")
                    .setFooter("→ Exemple : /item remove paladium")
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(builder.build()).queue();
            return;
        } else if (args.length != 1) {
            EmbedBuilder builder = new EmbedBuilder()
                    .setDescription(Info.negatif_emoji + "Le nombre d'argument est invalide.")
                    .setFooter("→ Exemple : /item remove paladium")
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(builder.build()).queue();
            return;
        }

        String item = args[0];

        if(!itemManager.hasItemData(item)){
            EmbedBuilder builder = new EmbedBuilder()
                    .setDescription(Info.negatif_emoji + "L'item ``" + item + "`` n'existe pas.")
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(builder.build()).queue();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder()
                .setDescription(Info.positif_emoji + "L'item ``" + item + "`` a bien été retiré du catalogue.")
                .setColor(new Color(88, 214, 141));

        itemManager.removeItemData(itemManager.getItemDataByName(item));

        textChannel.sendMessage(builder.build()).queue();
    }
}
