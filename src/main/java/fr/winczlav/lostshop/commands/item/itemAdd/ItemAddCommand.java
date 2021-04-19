package fr.winczlav.lostshop.commands.item.itemAdd;

import fr.winczlav.lostshop.commands.category.CategoryManager;
import fr.winczlav.lostshop.commands.item.ItemData;
import fr.winczlav.lostshop.commands.item.ItemManager;
import fr.winczlav.lostshop.config.ConfigurationManager;
import fr.winczlav.lostshop.utils.Info;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import fr.winczlav.gao.api.command.ICommand;
import fr.winczlav.gao.api.command.ISubCommand;

import java.awt.*;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ItemAddCommand extends ISubCommand {

    private final ItemAddManager itemAddManager;
    private final ItemManager itemManager;
    private final CategoryManager categoryManager;

    public ItemAddCommand(String command, ICommand iCommand, ItemManager itemManager, ItemAddManager itemAddManager, CategoryManager categoryManager) {
        super(command, iCommand);
        this.itemManager = itemManager;
        this.itemAddManager = itemAddManager;
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

        if(categoryManager.getCategoryDataList().size() == 0){
            EmbedBuilder cancel = new EmbedBuilder()
                    .setDescription(Info.negatif_emoji + "Veuillez créer au moins une catégorie !")
                    .setFooter("→ Informations : /category add - permet de créer une catégorie ")
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(cancel.build()).queue();
            return;
        }

        EmbedBuilder infoBuilder = new EmbedBuilder()
                .setDescription("Quel sera **le nom** de l'item que vous voulez ajouter au catalogue ?")
                .setFooter("→ Information : écrire `cancel` pour annuler la création de l'item.")
                .setColor(new Color(88, 214, 141));

        textChannel.sendMessage(infoBuilder.build()).queue(message1 -> {
            AtomicBoolean isFinish = new AtomicBoolean(false);

            Timer timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!isFinish.get()) {
                        itemAddManager.unregister(member);
                        EmbedBuilder toolate = new EmbedBuilder()
                                .setDescription(Info.negatif_emoji + "Vous avez pris trop de temps à créer l'item.")
                                .setColor(new Color(234, 62, 51));

                        textChannel.sendMessage(toolate.build()).queue();
                    }
                }
            }, 2 * 60 * 1000);

            itemAddManager.register(member, (m, s) -> {
                if (s.getChannel().getIdLong() != textChannel.getIdLong()) return;

                if (s.getContentRaw().equalsIgnoreCase("cancel")) {
                    EmbedBuilder cancel = new EmbedBuilder()
                            .setDescription(Info.positif_emoji + "L'ajout de l'item a bien été annulé.")
                            .setColor(new Color(88, 214, 141));

                    textChannel.sendMessage(cancel.build()).queue();
                    itemAddManager.unregister(member);
                    isFinish.set(true);
                    timer.cancel();
                    timer.purge();
                    s.delete().queue();
                    message1.delete().queue();
                    return;
                }

                if (itemManager.hasItemData(s.getContentRaw())) {
                    ItemData itemData = itemManager.getItemDataByName(s.getContentRaw());

                    EmbedBuilder builder = new EmbedBuilder()
                            .setDescription((s.getContentRaw().equalsIgnoreCase(itemData.getItem()) ? Info.negatif_emoji + "L'item ``" + s.getContentRaw() + "`` existe déjà." : Info.negatif_emoji + "L'item ``" + s.getContentRaw() + "`` existe déjà en tant qu'aliase pour l'item ``" + itemData.getItem() + "``."))
                            .setColor(new Color(234, 62, 51));

                    s.delete().queue();
                    textChannel.sendMessage(builder.build()).queue(d -> d.delete().queueAfter(5, TimeUnit.SECONDS));
                    return;
                }

                s.delete().queue();
                message1.delete().queue();

                // -----------------------------------------------//

                EmbedBuilder info2 = new EmbedBuilder()
                        .setDescription("Quel sera **le nom catalogue** de l'item que vous souhaitez ajouter au catalogue ?")
                        .setFooter("→ Information : écrire `cancel` pour annuler la création de l'item.")
                        .setColor(new Color(88, 214, 141));

                textChannel.sendMessage(info2.build()).queue(message6 -> itemAddManager.register(member, (m6, s6) -> {
                    if (s6.getChannel().getIdLong() != textChannel.getIdLong()) return;

                    if (s6.getContentRaw().equalsIgnoreCase("cancel")) {
                        EmbedBuilder cancel = new EmbedBuilder()
                                .setDescription(Info.positif_emoji + "L'ajout de l'item a bien été annulé.")
                                .setColor(new Color(88, 214, 141));

                        textChannel.sendMessage(cancel.build()).queue();
                        itemAddManager.unregister(member);
                        isFinish.set(true);
                        timer.cancel();
                        timer.purge();
                        s6.delete().queue();
                        message6.delete().queue();
                        return;
                    }

                    s6.delete().queue();
                    message6.delete().queue();

                    // -----------------------------------------------//

                    EmbedBuilder info = new EmbedBuilder()
                            .setDescription("Quel sera **la catégorie** de l'item que vous voulez ajouter au catalogue ?")
                            .setFooter("→ Information : écrire `cancel` pour annuler la création de l'item.")
                            .setColor(new Color(88, 214, 141));

                    textChannel.sendMessage(info.build()).queue(message5 -> itemAddManager.register(member, (m5, s5) -> {
                        if (s5.getChannel().getIdLong() != textChannel.getIdLong()) return;

                        if (s5.getContentRaw().equalsIgnoreCase("cancel")) {
                            EmbedBuilder cancel = new EmbedBuilder()
                                    .setDescription(Info.positif_emoji + "L'ajout de l'item a bien été annulé.")
                                    .setColor(new Color(88, 214, 141));

                            textChannel.sendMessage(cancel.build()).queue();
                            itemAddManager.unregister(member);
                            isFinish.set(true);
                            timer.cancel();
                            timer.purge();
                            s5.delete().queue();
                            message5.delete().queue();
                            return;
                        }

                        if (!categoryManager.hasCategoryData(s5.getContentRaw())) {

                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < categoryManager.getCategoryDataList().size(); i++) {
                                stringBuilder.append(String.format("``%s.`` » %s",
                                        i+1,
                                        categoryManager.getCategoryDataList().get(i).getName()));
                            }

                            EmbedBuilder embedBuilder = new EmbedBuilder()
                                    .setDescription(Info.negatif_emoji + "La catégorie ``" + s5.getContentRaw() + "`` nexiste pas.\n" +
                                            "Voici la liste des catégories disponible :\n\n" +
                                            stringBuilder)
                                    .setColor(new Color(234, 62, 51));

                            textChannel.sendMessage(embedBuilder.build()).queue();
                            return;
                        }

                        s5.delete().queue();
                        message5.delete().queue();

                        // -----------------------------------------------//

                        EmbedBuilder infoBuilder1 = new EmbedBuilder()
                                .setDescription("Quel sera **le prix** de l'item que vous voulez ajouter au catalogue ?")
                                .setFooter("→ Information : écrire `cancel` pour annuler la création de l'item.")
                                .setColor(new Color(88, 214, 141));

                        textChannel.sendMessage(infoBuilder1.build()).queue(message2 -> itemAddManager.register(member, (m1, s1) -> {
                            if (s1.getChannel().getIdLong() != textChannel.getIdLong()) return;

                            if (s1.getContentRaw().equalsIgnoreCase("cancel")) {
                                EmbedBuilder cancel = new EmbedBuilder()
                                        .setDescription(Info.positif_emoji + "L'ajout de l'item a bien été annulé.")
                                        .setColor(new Color(88, 214, 141));

                                textChannel.sendMessage(cancel.build()).queue();
                                itemAddManager.unregister(member);
                                isFinish.set(true);
                                timer.cancel();
                                timer.purge();
                                s1.delete().queue();
                                message2.delete().queue();
                                return;
                            }

                            try {
                                int price = Integer.parseInt(s1.getContentRaw());

                                s1.delete().queue();
                                message2.delete().queue();

                                EmbedBuilder infoBuilder2 = new EmbedBuilder()
                                        .setDescription("Quel sera **l'emoji** de l'item que vous voulez ajouter au catalogue ?")
                                        .setFooter("→ Information : écrire `cancel` pour annuler la création de l'item.")
                                        .setColor(new Color(88, 214, 141));

                                textChannel.sendMessage(infoBuilder2.build()).queue(message3 -> itemAddManager.register(member, (m2, s2) -> {
                                    if (s2.getChannel().getIdLong() != textChannel.getIdLong()) return;

                                    if (s2.getContentRaw().equalsIgnoreCase("cancel")) {
                                        EmbedBuilder cancel = new EmbedBuilder()
                                                .setDescription(Info.positif_emoji + "L'ajout de l'item a bien été annulé.")
                                                .setColor(new Color(88, 214, 141));

                                        textChannel.sendMessage(cancel.build()).queue();
                                        itemAddManager.unregister(member);
                                        isFinish.set(true);
                                        timer.cancel();
                                        timer.purge();
                                        s2.delete().queue();
                                        message3.delete().queue();
                                        return;
                                    }

                                    if (itemAddManager.getCheckEmoji(s2.getGuild()).stream().noneMatch(s2.getContentRaw()::contains)) {
                                        EmbedBuilder builder = new EmbedBuilder()
                                                .setDescription(Info.negatif_emoji + "L'emoji renseigné n'existe pas sur ce serveur.")
                                                .setColor(new Color(234, 62, 51));

                                        s2.delete().queue();
                                        textChannel.sendMessage(builder.build()).queue(d -> d.delete().queueAfter(5, TimeUnit.SECONDS));
                                        return;
                                    }

                                    s2.delete().queue();
                                    message3.delete().queue();

                                    for (int i = 0; i < itemAddManager.getCheckEmoji(s2.getGuild()).size(); i++) {
                                        if (itemAddManager.getCheckEmoji(s2.getGuild()).get(i).equalsIgnoreCase(s2.getContentRaw())) {
                                            String emoteID = itemAddManager.getEmojiIdList(s2.getGuild()).get(i).replace("[", "").replace("]", "");
                                            String emoteName = itemAddManager.getEmojiNameList(s2.getGuild()).get(i).replace("[", "").replace("]", "");

                                            itemManager.addItemData(new ItemData(s.getContentRaw(), s6.getContentRaw(), s5.getContentRaw(), price, emoteName, Long.parseLong(emoteID)));
                                            EmbedBuilder builder = new EmbedBuilder()
                                                    .setDescription(Info.positif_emoji + "L'item ``" + s.getContentRaw() + "`` a bien été ajouté au prix de ``" + price + "$``.")
                                                    .setColor(new Color(88, 214, 141));

                                            textChannel.sendMessage(builder.build()).queue();
                                            itemAddManager.unregister(member);
                                            isFinish.set(true);
                                            timer.cancel();
                                            timer.purge();

                                            Objects.requireNonNull(textChannel.getGuild().getTextChannelById(ConfigurationManager.channelLogs)).sendMessage(builder.build()).queue();
                                        }
                                    }
                                }));
                            } catch (NumberFormatException e) {
                                EmbedBuilder numberFormatExceptionEmbed = new EmbedBuilder()
                                        .setDescription(Info.negatif_emoji + "Le prix renseigné est invalide")
                                        .setFooter("→ Veuillez inclure le chiffre sans espace et sans aucun autre caractère.")
                                        .setColor(new Color(234, 62, 51));

                                s1.delete().queue();
                                textChannel.sendMessage(numberFormatExceptionEmbed.build()).queue(d -> d.delete().queueAfter(5, TimeUnit.SECONDS));
                            }
                        }));
                    }));
                }));
            });
        });
    }
}
