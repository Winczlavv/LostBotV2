package fr.winczlav.lostshop.order;

import fr.winczlav.lostshop.commands.item.ItemManager;
import fr.winczlav.lostshop.utils.Info;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;

import static fr.winczlav.lostshop.utils.Util.getSplitSold;

public enum OrderMessage {

    NEWORDER((orderData, embedBuilder, guild, itemManager) -> {
        Member member = guild.retrieveMemberById(orderData.getBuyer()).complete();
        String item = orderData.getItem();
        String catalogueName = orderData.getCatalogueName();

        embedBuilder.setDescription(Info.positif_emoji + "Une nouvelle commande est arrivée :\n\n" +
                "Objet demandé : **" + catalogueName + " **(" + item + ")\n" +
                "Quantité : **" + orderData.getAmount() + "**\n" +
                "Prix total : **" + getSplitSold(orderData.getPrice()) + "$**\n\n" +
                "Numéro de suivi : **#" + orderData.getId() + "**")
                .setFooter("→ Acheteur : " + (member == null ?  "WARNING: L'utilisateur a quitté le serveur." : member.getUser().getName()), (member == null ? "https://miro.medium.com/max/720/1*W35QUSvGpcLuxPo3SRTH4w.png" : member.getUser().getAvatarUrl()))
                .setThumbnail("https://cdn.discordapp.com/emojis/" + orderData.getEmoteID() + ".png")
                .setColor(new Color(22, 160, 133));
    }),

    CHANNELCLOSE((orderData, embedBuilder, guild, itemManager) -> {
        Member member = guild.retrieveMemberById(orderData.getBuyer()).complete();
        Member salesman = guild.retrieveMemberById(orderData.getSalesman()).complete();
        assert member != null;
        assert salesman != null;
        String item = orderData.getItem();
        String catalogueName = orderData.getCatalogueName();

        embedBuilder.setDescription(Info.positif_emoji + "Votre commande vient d'être validée :\n\n" +
                "Objet demandé : **" + catalogueName + " **(" + item + ")\n" +
                "Quantité : **" + orderData.getAmount() + "**\n" +
                "Prix total : **" + getSplitSold(orderData.getPrice()) + "$**\n\n" +
                "Numéro de suivi : **#" + orderData.getId() + "**\n\n")
                .addField("Acheteur", "<@" + member.getIdLong() + ">", true)
                .addField("Vendeur", "<@" + salesman.getIdLong() + ">", true)
                .setFooter("→ Informations : Des salons vocaux sont à votre disposition pour faciliter votre échange.")
                .setThumbnail("https://cdn.discordapp.com/emojis/" + orderData.getEmoteID() + ".png")
                .setColor(new Color(22, 160, 133));
    }),

    CHANNELVALID((orderData, embedBuilder, guild, itemManager) -> {
        Member member = guild.retrieveMemberById(orderData.getBuyer()).complete();
        Member salesman = guild.retrieveMemberById(orderData.getSalesman()).complete();
        assert member != null;
        assert salesman != null;
        String item = orderData.getItem();
        String catalogueName = orderData.getCatalogueName();

        embedBuilder.setDescription(Info.valid_emoji + "Une commande vient d'être validée :\n\n" +
                "Contenu de la commande : **" + orderData.getAmount() + " "+ catalogueName + " (" +item + ") pour " + getSplitSold(orderData.getPrice()) +"$**\n")
                .setFooter("→ Information : Si vous n'êtes pas le Vendeur, vous ne pouvez plus voir cette commande.")
                .setThumbnail("https://cdn.discordapp.com/emojis/" + orderData.getEmoteID() + ".png")
                .setColor(new Color(22, 100, 140));
    }),

    DMSUMMARY((orderData, embedBuilder, guild, itemManager) -> {
        Member member = guild.retrieveMemberById(orderData.getBuyer()).complete();
        String item = orderData.getItem();
        String catalogueName = orderData.getCatalogueName();

        embedBuilder.setTitle("Suivi de commande n°" + orderData.getId())
                .setDescription(Info.positif_emoji + "Votre commande a bien été signalée aux vendeurs !\n\n" +
                        "Objet demandé : **" + catalogueName + " **(" + item + ")\n" +
                        "Quantité : **" + orderData.getAmount() + "**\n" +
                        "Prix total : **" + getSplitSold(orderData.getPrice()) + "$**\n\n" +
                        "Numéro de suivi : **#" + orderData.getId() + "**")
                .setFooter("→ Information : Vous receverez un message lorsque votre commande sera prête.", member.getUser().getAvatarUrl())
                .setThumbnail("https://cdn.discordapp.com/emojis/" + orderData.getEmoteID() + ".png")
                .setColor(new Color(22, 160, 133));
    }),

    ORDER_MESSAGE((orderData, embedBuilder, guild, itemManager) -> {
        Member member = guild.retrieveMemberById(orderData.getBuyer()).complete();
        assert member != null;
        String item = orderData.getItem();
        String catalogueName = orderData.getCatalogueName();

        embedBuilder.setTitle("Suivi de commande n°" + orderData.getId())
                .setDescription(Info.positif_emoji + "Votre commande a bien été signalé aux vendeurs !\n\n" +
                        "Objet demandé : **" + catalogueName + " **(" + item + ")\n" +
                        "Quantité : **" + orderData.getAmount() + "**\n" +
                        "Prix total : **" + getSplitSold(orderData.getPrice()) + "$**\n\n" +
                        "Numéro de suivi : **#" + orderData.getId() + "**")
                .setFooter("→ Information : Vous serez mentionné une fois votre commande prête.", member.getUser().getAvatarUrl())
                .setThumbnail("https://cdn.discordapp.com/emojis/" + orderData.getEmoteID() + ".png")
                .setColor(new Color(22, 160, 133));
    });

    private final OrderConsumer<OrderData, EmbedBuilder, Guild, ItemManager> consumer;

    OrderMessage(OrderConsumer<OrderData, EmbedBuilder, Guild, ItemManager> consumer) { this.consumer = consumer; }

    public OrderConsumer<OrderData, EmbedBuilder, Guild, ItemManager> getConsumer() {
        return consumer;
    }
}
