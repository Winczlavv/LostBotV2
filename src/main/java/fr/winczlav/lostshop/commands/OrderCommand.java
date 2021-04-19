package fr.winczlav.lostshop.commands;

import fr.winczlav.gao.api.command.ICommand;
import fr.winczlav.lostshop.commands.item.ItemManager;
import fr.winczlav.lostshop.config.ConfigurationManager;
import fr.winczlav.lostshop.order.OrderData;
import fr.winczlav.lostshop.order.OrderManager;
import fr.winczlav.lostshop.order.OrderMessage;
import fr.winczlav.lostshop.utils.Info;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OrderCommand extends ICommand {

    private final OrderManager orderManager;
    private final ItemManager itemManager;

    public OrderCommand(String command, OrderManager orderManager, ItemManager itemManager) {
        super(command, Info.marketPart);
        this.orderManager = orderManager;
        this.itemManager = itemManager;
        addAliase("c", "commander");
    }

    @Override
    public void onCommand(Message message, User user, Member member, TextChannel textChannel, Guild guild, String[] args) throws Exception  {

        message.delete().queue();

        if (!ConfigurationManager.canDoOrderEveryWhere && ConfigurationManager.channelToOrder != textChannel.getIdLong()) {
            textChannel.sendMessage(new EmbedBuilder().setDescription("Merci d'aller dans le salon <#" + ConfigurationManager.channelToOrder + "> pour commander !").setColor(new Color(234, 62, 51)).build()).queue(message1 -> message1.delete().queueAfter(30, TimeUnit.SECONDS));
            return;
        }

        if (args.length < 2) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setDescription(Info.negatif_emoji + "Erreur de syntaxe : !commander <quantité> <item>")
                    .setFooter("→ Exemple : !commander 64 paladium")
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(embedBuilder.build()).queue(message1 -> message1.delete().queueAfter(30, TimeUnit.SECONDS));
            return;
        }

        try {
            int amount = Integer.parseInt(args[0]);

            final List<String> stringList = Arrays.asList(args);
            final String item = String.join(" ", stringList.subList(1, stringList.size()));

            if(itemManager.hasItemData(item)) {
                if (amount < itemManager.getItemDataByName(item).getMin()   || amount > itemManager.getItemDataByName(item).getMax()) {
                    EmbedBuilder embedBuilder = new EmbedBuilder()
                            .setDescription(Info.negatif_emoji + "Veuillez inclure une quantité entre **"+ itemManager.getItemDataByName(item).getMin() + "** et **" + itemManager.getItemDataByName(item).getMax() +"**.")
                            .setFooter("→ Exemple : !commander 64 " + itemManager.getItemDataByName(item).getItem()+ ".")
                            .setColor(new Color(234, 62, 51));

                    textChannel.sendMessage(embedBuilder.build()).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
                    return;
                }
                OrderData orderData = orderManager.createOrder(member, amount, itemManager.getItemDataByName(item).getItem(), itemManager.getItemDataByName(item).getEmoteName(), itemManager.getItemDataByName(item).getEmoteID(), itemManager.getItemDataByName(item).getPrice() * amount,itemManager.getItemDataByName(item).getCatalogueName(), itemManager.getItemDataByName(item).getMin(), itemManager.getItemDataByName(item).getMax());

                PrivateChannel privateChannel = user.openPrivateChannel().complete();

                if(privateChannel != null) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    orderManager.getOrderBuilder().callMessage(OrderMessage.DMSUMMARY, orderData, embedBuilder, textChannel.getGuild(), itemManager);
                    privateChannel.sendMessage(embedBuilder.build()).queue();
                }

                EmbedBuilder embedBuilder = new EmbedBuilder();
                orderManager.getOrderBuilder().callMessage(OrderMessage.ORDER_MESSAGE, orderData, embedBuilder, textChannel.getGuild(), itemManager);
                textChannel.sendMessage(embedBuilder.build()).queue(message1 -> message1.delete().queueAfter(5, TimeUnit.SECONDS));
                EmbedBuilder embedBuilder1 = new EmbedBuilder();
                orderManager.getOrderBuilder().callMessage(OrderMessage.NEWORDER, orderData, embedBuilder1, textChannel.getGuild(), itemManager);
                textChannel.getGuild().getTextChannelById(ConfigurationManager.channelLogs).sendMessage(embedBuilder1.build()).queue();
            } else {
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setDescription(Info.negatif_emoji + "L'item demandé n'apparait pas dans notre catalogue")
                        .setFooter("→ Information : Lisez bien les instructions ci-dessus pour obtenir le bon ID")
                        .setColor(new Color(234, 62, 51));

                textChannel.sendMessage(embedBuilder.build()).queue(message1 -> message1.delete().queueAfter(5, TimeUnit.SECONDS));
            }
        } catch (NumberFormatException ignored) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setDescription(Info.negatif_emoji + "Veuillez inclure une quantité entre 1 et 512.")
                    .setFooter("→ Exemple : !commander 64 paladium")
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(embedBuilder.build()).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
        }
    }
}

