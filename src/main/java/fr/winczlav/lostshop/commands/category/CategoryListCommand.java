package fr.winczlav.lostshop.commands.category;

import fr.winczlav.gao.api.command.ICommand;
import fr.winczlav.gao.api.command.ISubCommand;
import fr.winczlav.lostshop.utils.Info;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class CategoryListCommand extends ISubCommand {

    private final CategoryManager categoryManager;

    public CategoryListCommand(String command, ICommand iCommand, CategoryManager categoryManager) {
        super(command, iCommand);
        this.categoryManager = categoryManager;
    }

    @Override
    public void onCommand(Message message, User user, Member member, TextChannel textChannel, Guild guild, String[] strings) {

        if(!(member.hasPermission(Permission.ADMINISTRATOR))){
            EmbedBuilder builder = new EmbedBuilder()
                    .setDescription(Info.negatif_emoji + "Vous n'avez pas la permission d'exécuter cette commande.")
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(builder.build()).queue(message1 -> message1.delete().queueAfter(5, TimeUnit.SECONDS));
            message.delete().queue();
            return;
        }

        if (categoryManager.getCategoryDataList().isEmpty()){
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setDescription(Info.negatif_emoji + "Aucune catégorie n'a été créée.")
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(embedBuilder.build()).queue();
        } else {

           EmbedBuilder embedBuilder = new EmbedBuilder().setAuthor("Liste des catégories");
           categoryManager.getCategoryDataList().forEach(categoryData -> embedBuilder.appendDescription(categoryData.getName() + "\n"));

           textChannel.sendMessage(embedBuilder.build()).queue();
        }
    }
}
