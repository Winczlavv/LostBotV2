package fr.winczlav.lostshop.commands.category.categoryAdd;

import fr.winczlav.gao.api.command.ICommand;
import fr.winczlav.gao.api.command.ISubCommand;
import fr.winczlav.lostshop.commands.category.CategoryData;
import fr.winczlav.lostshop.commands.category.CategoryManager;
import fr.winczlav.lostshop.config.ConfigurationManager;
import fr.winczlav.lostshop.utils.Info;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class CategoryAddCommand extends ISubCommand {

    private final CategoryAddManager categoryAddManager;
    private final CategoryManager categoryManager;

    public CategoryAddCommand(String command, ICommand iCommand, CategoryAddManager categoryAddManager, CategoryManager categoryManager) {
        super(command, iCommand);
        this.categoryAddManager = categoryAddManager;
        this.categoryManager = categoryManager;
    }

    @Override
    public void onCommand(Message message, User user, Member member, TextChannel textChannel, Guild guild, String[] args) {

        if (!(member.hasPermission(Permission.ADMINISTRATOR))) {
            EmbedBuilder builder = new EmbedBuilder()
                    .setDescription(Info.negatif_emoji + "Vous n'avez pas la permission d'exécuter cette commande.")
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(builder.build()).queue(message1 -> message1.delete().queueAfter(5, TimeUnit.SECONDS));
            message.delete().queue();
            return;
        }

        EmbedBuilder infoBuilder = new EmbedBuilder()
                .setDescription("Quel sera **le nom** de la catégorie que vous souhaitez ajouter au catalogue ?")
                .setFooter("→ Information : écrire `cancel` pour annuler la création de la catégorie.")
                .setColor(new Color(88, 214, 141));

        textChannel.sendMessage(infoBuilder.build()).queue(message1 -> {
            AtomicBoolean isFinish = new AtomicBoolean(false);

            Timer timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!isFinish.get()) {
                        categoryAddManager.unregister(member);
                        EmbedBuilder toolate = new EmbedBuilder()
                                .setDescription(Info.negatif_emoji + "Vous avez pris trop de temps à créer l'item.")
                                .setColor(new Color(234, 62, 51));

                        textChannel.sendMessage(toolate.build()).queue();
                    }
                }
            }, 2 * 60 * 1000);

            categoryAddManager.register(member, (m, s) -> {
                if (s.getChannel().getIdLong() != textChannel.getIdLong()) return;

                if (s.getContentRaw().equalsIgnoreCase("cancel")) {
                    EmbedBuilder cancel = new EmbedBuilder()
                            .setDescription(Info.positif_emoji + "L'ajout de l'item a bien été annulé.")
                            .setColor(new Color(88, 214, 141));

                    textChannel.sendMessage(cancel.build()).queue();
                    categoryAddManager.unregister(member);
                    isFinish.set(true);
                    timer.cancel();
                    timer.purge();
                    s.delete().queue();
                    message1.delete().queue();
                    return;
                }

                if (categoryManager.hasCategoryData(s.getContentRaw())) {
                    CategoryData categoryData = categoryManager.getCategoryDataByName(s.getContentRaw());

                    EmbedBuilder builder = new EmbedBuilder()
                            .setDescription((s.getContentRaw().equalsIgnoreCase(categoryData.getName()) ? Info.negatif_emoji + "La catégorie ``" + s.getContentRaw() + "`` existe déjà." :
                                    Info.negatif_emoji + "La catégorie ``" + s.getContentRaw() + "`` existe déjà en tant qu'aliase pour la catégorie ``" + categoryData.getName() + "``."))
                            .setColor(new Color(234, 62, 51));

                    s.delete().queue();
                    textChannel.sendMessage(builder.build()).queue(d -> {
                        d.delete().queueAfter(5, TimeUnit.SECONDS);
                    });
                    return;
                }

                String name = s.getContentRaw();
                s.delete().queue();
                message1.delete().queue();

                // -------------------------------------------- //

                EmbedBuilder infoBuilder2 = new EmbedBuilder()
                        .setDescription("Quel sera **la couleur** de la catégorie que vous voulez ajouter au catalogue ? (HEX only)")
                        .setFooter("→ Information : écrire `cancel` pour annuler la création de la catégorie.")
                        .setColor(new Color(88, 214, 141));

                textChannel.sendMessage(infoBuilder2.build()).queue(message3 -> categoryAddManager.register(member, (m2, s2) -> {
                    if (s2.getChannel().getIdLong() != textChannel.getIdLong()) return;

                    if (s2.getContentRaw().equalsIgnoreCase("cancel")) {
                        EmbedBuilder cancel = new EmbedBuilder()
                                .setDescription(Info.positif_emoji + "L'ajout de l'item a bien été annulé.")
                                .setColor(new Color(88, 214, 141));

                        textChannel.sendMessage(cancel.build()).queue();
                        categoryAddManager.unregister(member);
                        isFinish.set(true);
                        timer.cancel();
                        timer.purge();
                        s2.delete().queue();
                        message3.delete().queue();
                        return;
                    }

                    if (!s2.getContentRaw().startsWith("#")) {
                        EmbedBuilder builder = new EmbedBuilder()
                                .setDescription(Info.negatif_emoji + "Seul le format HEX commençant par un # est accepté !\nExemple : ``#2ecc71``")
                                .setFooter("Site HEX conseillé → https://flatuicolors.com")
                                .setColor(new Color(234, 62, 51));

                        textChannel.sendMessage(builder.build()).queue();
                        return;
                    }

                    int[] rgb = new int[3];

                    try {
                        String color = s2.getContentRaw().substring(1);
                        for (int i = 0, j = 0; i < color.length() - 1; i += 2, j++) {
                            String hex = color.substring(i, i + 2);
                            rgb[j] = Integer.parseInt(hex, 16);
                        }
                    } catch (NumberFormatException e) {
                        EmbedBuilder builder = new EmbedBuilder()
                                .setDescription(Info.negatif_emoji + "Seul le format HEX commençant par un # est accepté !\nExemple : ``#2ecc71``")
                                .setFooter("Site HEX conseillé → https://flatuicolors.com")
                                .setColor(new Color(234, 62, 51));

                        textChannel.sendMessage(builder.build()).queue();
                        return;
                    }

                    s2.delete().queue();
                    message3.delete().queue();

                    categoryManager.addCategoryData(new CategoryData(name, rgb[0], rgb[1], rgb[2]));

                    EmbedBuilder builder = new EmbedBuilder()
                            .setDescription(Info.positif_emoji + "La catégorie ``" + s.getContentRaw() + "`` a bien été créée.")
                            .setColor(new Color(88, 214, 141));

                    textChannel.sendMessage(builder.build()).queue();
                    categoryAddManager.unregister(member);
                    isFinish.set(true);
                    timer.cancel();
                    timer.purge();

                    Objects.requireNonNull(textChannel.getGuild().getTextChannelById(ConfigurationManager.channelLogs)).sendMessage(builder.build()).queue();

                }));
            });
        });
    }
}
