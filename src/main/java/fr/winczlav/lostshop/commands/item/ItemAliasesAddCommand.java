package fr.winczlav.lostshop.commands.item;

import fr.winczlav.lostshop.config.ConfigurationManager;
import fr.winczlav.lostshop.utils.Info;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import fr.winczlav.gao.api.command.ICommand;
import fr.winczlav.gao.api.command.ISubCommand;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ItemAliasesAddCommand extends ISubCommand {

    private final ItemManager itemManager;

    public ItemAliasesAddCommand(String command, ICommand iCommand, ItemManager itemManager) {
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

        //i /tem aliases add(1) item(2) aliase(3)

        if(args.length == 1){
            EmbedBuilder builder = new EmbedBuilder()
                    .setDescription(Info.negatif_emoji + "Veuillez inclure un item existant.")
                    .setFooter("→ Exemple : /item aliases add paladium pala")
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(builder.build()).queue();
        }else if(args.length == 2){
            EmbedBuilder builder = new EmbedBuilder()
                    .setDescription(Info.negatif_emoji + "Veuillez inclure un aliase à ajouter.")
                    .setFooter("→ Exemple : /item aliases add paladium pala")
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(builder.build()).queue();
        }else if (args.length != 3) {
            EmbedBuilder builder = new EmbedBuilder()
                    .setDescription(Info.negatif_emoji + "Le nombre d'argument est invalide.")
                    .setFooter("→ Exemple : /item aliases add paladium pala")
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(builder.build()).queue();
            return;
        }

        String item = args[1];
        String aliase = args[2];
        ItemData itemData = itemManager.getItemDataByName(item);

        if(itemData == null){
            EmbedBuilder builder = new EmbedBuilder()
                    .setDescription(Info.negatif_emoji + "L'item ``" + item + "`` n'existe pas.")
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(builder.build()).queue();
            return;
        }

        if(itemManager.hasItemData(aliase)) {
            EmbedBuilder builder = new EmbedBuilder()
                    .setDescription((aliase.equalsIgnoreCase(itemData.getItem()) ? Info.negatif_emoji +"L'aliase ``" + aliase + "`` est déjà le nom principal de l'item ````" + itemData.getItem() + "``." : Info.negatif_emoji + "L'aliase ``" + aliase + "`` existe déjà pour l'item ``" + itemData.getItem() + "``."))
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(builder.build()).queue();
            return;
        }

        itemData.getAliases().add(aliase);
        EmbedBuilder builder = new EmbedBuilder()
                .setDescription(Info.positif_emoji + "L'aliase ``" + aliase + "`` a bien été ajouté à l'item ``" + item +"``.")
                .setColor(new Color(88, 214, 141));

        textChannel.sendMessage(builder.build()).queue();
        Objects.requireNonNull(textChannel.getGuild().getTextChannelById(ConfigurationManager.channelLogs)).sendMessage(builder.build()).queue();
    }
}
