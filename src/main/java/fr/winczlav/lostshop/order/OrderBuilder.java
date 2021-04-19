package fr.winczlav.lostshop.order;

import fr.winczlav.gao.api.reaction.Reaction;
import fr.winczlav.lostshop.Core;
import fr.winczlav.lostshop.commands.item.ItemManager;
import fr.winczlav.lostshop.commands.item.itemAdd.ItemAddManager;
import fr.winczlav.lostshop.config.ConfigurationManager;
import fr.winczlav.lostshop.utils.Info;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class OrderBuilder {

    private final Category category;
    private final TextChannel textChannel;
    private final OrderManager orderManager;
    private final ItemManager itemManager;
    private final ItemAddManager itemAddManager;

    public OrderBuilder(OrderManager orderManager, ItemManager itemManager, ItemAddManager itemAddManager) {
        this.orderManager = orderManager;
        this.category = orderManager.getCategory();
        this.textChannel = orderManager.getTextChannel();
        this.itemManager = itemManager;
        this.itemAddManager = itemAddManager;
    }

    private void createChannel(Member member, OrderData orderData) {
        Member buyer = member.getGuild().retrieveMemberById(orderData.getBuyer()).complete();
        orderData.setSalesman(member.getUser().getIdLong());
        Member salesman = member.getGuild().retrieveMemberById(member.getUser().getIdLong()).complete();
        String name = "\uD83D\uDCB3" + buyer.getUser().getName();
        category.createTextChannel(name).queue(channel -> {
            channel.putPermissionOverride(member.getGuild().getPublicRole()).setDeny(Permission.MESSAGE_READ).queue();
            channel.putPermissionOverride(member).setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_HISTORY).queue();
            channel.putPermissionOverride(Objects.requireNonNull(buyer)).setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_HISTORY).queue();
            channel.getManager().setTopic("Vendeur : " + member.getUser().getName()).queue();

            channel.sendMessage(String.format("<@%s> | <@%s>", buyer.getIdLong(), salesman.getIdLong())).queue(messagee -> messagee.delete().queueAfter(1, TimeUnit.SECONDS));
            OrderLocationData orderLocationData = orderData.getOrderLocationData();
            orderLocationData.setTextchannel(channel.getIdLong());
            EmbedBuilder embedBuilder = new EmbedBuilder();
            callMessage(OrderMessage.CHANNELCLOSE, orderData, embedBuilder, textChannel.getGuild(), itemManager);
            channel.sendMessage(embedBuilder.build()).queue(message -> initReactionClose(message, orderData));
            callMessage(OrderMessage.CHANNELVALID, orderData, embedBuilder, textChannel.getGuild(), itemManager);
            Objects.requireNonNull(textChannel.getGuild().getTextChannelById(ConfigurationManager.channelLogs)).sendMessage(embedBuilder.build()).queue();


        });
    }

    public void initReactionOrder(Message message, OrderData orderData) {
        new Reaction(message)
                .addReaction("✅", event -> {
                    if (Objects.nonNull(event.getMember()) && event.getMember().getIdLong() != Core.getInstance().getJDA().getSelfUser().getIdLong()) {
                        if (nextOrder(orderData, message, event, OrderStatus.PROGRESS)) {
                            createChannel(Objects.requireNonNull(event.getMember()), orderData);
                        }
                    }
                }).addReaction("❌", event -> {
            if (Objects.requireNonNull(event.getMember()).getIdLong() == Core.getInstance().getJDA().getSelfUser().getIdLong()) return;
            if (Objects.nonNull(event.getMember())) {
                EmbedBuilder infoBuilder = new EmbedBuilder()
                        .setDescription("Quelle est la raison d'annulation de la commande ?")
                        .setFooter("→ Information : écrire `cancel` pour annuler.")
                        .setColor(new Color(88, 214, 141));

                Timer timer = new Timer();
                AtomicBoolean finish = new AtomicBoolean(false);

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!finish.get()) {
                            itemAddManager.unregister(event.getMember());
                            EmbedBuilder toolate = new EmbedBuilder()
                                    .setDescription(Info.negatif_emoji + "Vous avez pris trop de temps à choisir une raison.")
                                    .setColor(new Color(234, 62, 51));

                            textChannel.sendMessage(toolate.build()).queue(message1 -> message1.delete().queueAfter(30, TimeUnit.SECONDS));
                        }
                    }
                }, 60 * 1000);

                textChannel.sendMessage(infoBuilder.build()).queue(message1 -> itemAddManager.register(event.getMember(), (m, reason) -> {
                    if (reason.getChannel().getIdLong() != textChannel.getIdLong() || m.getIdLong() != event.getMember().getIdLong()) return;

                    if (reason.getContentRaw().equalsIgnoreCase("cancel")) {
                        EmbedBuilder cancel = new EmbedBuilder()
                                .setDescription(Info.positif_emoji + "La commande n'a pas été annulée.")
                                .setColor(new Color(88, 214, 141));

                        textChannel.sendMessage(cancel.build()).queue(message2 -> message2.delete().queueAfter(30, TimeUnit.SECONDS));
                        finish.set(true);
                        timer.cancel();
                        timer.purge();
                        reason.delete().queue();
                        message1.delete().queue();
                        itemAddManager.unregister(event.getMember());
                        return;
                    }

                    reason.getGuild().retrieveMemberById(orderData.getBuyer()).queue(member -> {
                        member.getUser().openPrivateChannel().queue(privateChannel -> {
                            if (member.getUser().hasPrivateChannel()) {
                                privateChannel.sendMessage(new EmbedBuilder().setDescription(Info.negatif_emoji + "Bonjour,\nnous sommes dans le regret de vous annoncer que la commande suivante a été annulée :\n\nItem: " + orderData.getAmount() + " " + orderData.getItem() + "\nPrix: " + orderData.getPrice() + "$\n\nRaison: " + reason.getContentRaw() + "\n\nEn vous remerciant de votre compréhension.").setColor(new Color(234, 62, 51)).build()).queue();
                            }
                        });

                        EmbedBuilder cancel = new EmbedBuilder()
                                .setDescription(Info.positif_emoji + "La commande a bien été annulée.")
                                .setColor(new Color(88, 214, 141));

                        textChannel.sendMessage(cancel.build()).queue(message2 -> message2.delete().queueAfter(30, TimeUnit.SECONDS));
                        finish.set(true);
                        reason.delete().queue();
                        message1.delete().queue();
                        nextOrder(orderData, message, event, OrderStatus.CANCELED);
                        deleteOrder(orderData.getTime());
                        timer.cancel();
                        timer.purge();
                        itemAddManager.unregister(event.getMember());

                        Objects.requireNonNull(textChannel.getGuild().getTextChannelById(ConfigurationManager.channelLogs)).sendMessage(new EmbedBuilder().setDescription(Info.negatif_emoji + Objects.requireNonNull(event.getUser()).getName() + " a annulé une commande.\n\nRaison: " + reason.getContentRaw() + "\nRappel de la commande: x" + orderData.getAmount() + " " + orderData.getItem() + "\nPrix: " + orderData.getPrice() + "$").setColor(new Color(234, 62, 51)).build()).queue();
                    });
                }));
            } else {
                event.getReaction().removeReaction(Objects.requireNonNull(event.getUser())).queue();
            }
        });
    }

    public void initReactionClose(Message message, OrderData orderData) {
        new Reaction(message)
                .addReaction("\uD83D\uDEAA", event -> {
                    if (Objects.nonNull(event.getMember()) && event.getMember().getIdLong() != Core.getInstance().getJDA().getSelfUser().getIdLong()) {
                        if (event.getMember().getUser().getIdLong() == orderData.getSalesman()) {

                            AtomicReference<Member> buyer = new AtomicReference<>();
                            event.getGuild().retrieveMemberById(orderData.getBuyer()).queue(member -> {
                                buyer.set(member);
                                member.getUser().openPrivateChannel().queue(privateChannel -> {
                                    if (member.getUser().hasPrivateChannel()) {
                                        privateChannel.sendMessage(new EmbedBuilder().setDescription("\uD83D\uDEAA Merci pour votre commande ! A bientôt sur le Marché des Âmes").build()).queue();
                                        privateChannel.sendMessage(new EmbedBuilder().setDescription("\uD83C\uDCCF Grâce à vos achats, vous avez reçu **un CashBack** que vous pouvez dépenser ou retirer sur le **BlackOld Casino**.\n Pour réclamer tes gains, va dans le salon <#820504053439135764> !\n\nVoici leur lien : **https://discord.gg/8mFzJQPXXd**\nVotre CashBack : **" + Math.round(getCasinoPrice(orderData)) + "$**").setFooter("→ Code Partenaire : ID 1.")
                                                .setThumbnail("https://cdn.discordapp.com/emojis/" + orderData.getEmoteID() + ".png").setColor(new Color(0, 0, 0)).build()).queue();
                                    event.getGuild().addRoleToMember(member, Objects.requireNonNull(event.getGuild().getRoleById(ConfigurationManager.certifiedrole))).queue();
                                    }
                                });
                            });

                            AtomicReference<Member> salesman = new AtomicReference<>();
                            event.getGuild().retrieveMemberById(orderData.getSalesman()).queue(member -> {
                                salesman.set(member);
                                member.getUser().openPrivateChannel().queue(privateChannel -> {
                                    if (member.getUser().hasPrivateChannel()) {
                                        privateChannel.sendMessage(new EmbedBuilder().setDescription("\uD83D\uDCB8 Merci d'avoir traité une commande ! N'oublie pas, les taxes ont disparu ;) ").build()).queue();
                                  }
                                });
                            });

                            orderData.setOrderStatus(OrderStatus.COMPLETE);
                            message.getTextChannel().delete().queue();

                            Objects.requireNonNull(textChannel.getGuild().getTextChannelById(ConfigurationManager.channelLogs)).sendMessage(
                                    new EmbedBuilder()
                                            .setDescription(String.format("♦ %s vient de terminer la commande de %s d'une valeur de %s$.", salesman.get().getUser().getName(), buyer.get().getUser().getName(), orderData.getPrice()))
                                            .setColor(new Color(226, 79, 0))
                                            .build()
                            ).queue();
                            Objects.requireNonNull(textChannel.getGuild().getTextChannelById(ConfigurationManager.channelVerif)).sendMessage(
                                    new EmbedBuilder()
                                            .setDescription(String.format("♦ %s vient de terminer la commande de %s d'une valeur de %s$.", salesman.get().getUser().getName(), buyer.get().getUser().getName(), orderData.getPrice()))
                                            .setColor(new Color(226, 79, 0))
                                            .build()
                            ).queue();
                            Objects.requireNonNull(textChannel.getGuild().getTextChannelById(ConfigurationManager.channelSpecial)).sendMessage(
                                    new EmbedBuilder()
                                            .setDescription(String.format("Coucou Dradon, \n %s a obtenu un cashback d'une valeur de %s$.", buyer.get().getUser(), Math.round(getCasinoPrice(orderData))))
                                            .setColor(new Color(200, 0, 0))
                                            .build()
                            ).queue();
                        } else {
                            event.getReaction().removeReaction(Objects.requireNonNull(event.getUser())).queue();
                        }
                    }
                }).addReaction("❌", event -> {
            if (Objects.nonNull(event.getMember()) && event.getMember().getIdLong() != Core.getInstance().getJDA().getSelfUser().getIdLong()) {
                if (event.getMember().getUser().getIdLong() == orderData.getSalesman()) {
                    if (event.getMember().getIdLong() == Core.getInstance().getJDA().getSelfUser().getIdLong()) return;
                    if (Objects.nonNull(event.getMember())) {
                        EmbedBuilder infoBuilder = new EmbedBuilder()
                                .setDescription("Quelle est la raison d'annulation de la commande ?")
                                .setFooter("→ Information : écrire `cancel` pour annuler l'annulation.")
                                .setColor(new Color(88, 214, 141));

                        Timer timer = new Timer();
                        AtomicBoolean finish = new AtomicBoolean(false);

                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (!finish.get()) {
                                    itemAddManager.unregister(event.getMember());
                                    EmbedBuilder toolate = new EmbedBuilder()
                                            .setDescription(Info.negatif_emoji + "Vous avez pris trop de temps à choisir une raison.")
                                            .setColor(new Color(234, 62, 51));

                                    textChannel.sendMessage(toolate.build()).queue(message1 -> message1.delete().queueAfter(30, TimeUnit.SECONDS));
                                }
                            }
                        }, 60 * 1000);
                        TextChannel channel = category.getGuild().getTextChannelById(orderData.getOrderLocationData().getTextchannel());

                        assert channel != null;
                        channel.sendMessage(infoBuilder.build()).queue(message1 -> itemAddManager.register(event.getMember(), (m, reason) -> {

                            if (reason.getChannel().getIdLong() != channel.getIdLong() || m.getIdLong() != event.getMember().getIdLong()) return;

                            if (reason.getContentRaw().equalsIgnoreCase("cancel")) {
                                EmbedBuilder cancel = new EmbedBuilder()
                                        .setDescription(Info.positif_emoji + "La commande n'a pas été annulée.")
                                        .setColor(new Color(88, 214, 141));

                                channel.sendMessage(cancel.build()).queue(message2 -> message2.delete().queueAfter(30, TimeUnit.SECONDS));
                                finish.set(true);
                                timer.cancel();
                                timer.purge();
                                reason.delete().queue();
                                message1.delete().queue();
                                itemAddManager.unregister(event.getMember());
                                return;
                            }

                            reason.getGuild().retrieveMemberById(orderData.getBuyer()).queue(member -> {
                                member.getUser().openPrivateChannel().queue(privateChannel -> {
                                    if (member.getUser().hasPrivateChannel()) {
                                        privateChannel.sendMessage(new EmbedBuilder().setDescription(Info.negatif_emoji + "Bonjour,\nnous sommes dans le regret de vous annoncer que la commande suivante a été annulée :\n\nItem: " + orderData.getAmount() + " " + orderData.getItem() + "\nPrix: " + orderData.getPrice() + "$\n\nRaison: " + reason.getContentRaw() + "\n\nEn vous remerciant de votre compréhension.").setColor(new Color(234, 62, 51)).build()).queue();
                                    }
                                });

                                EmbedBuilder cancel = new EmbedBuilder()
                                        .setDescription(Info.positif_emoji + "La commande a bien été annulée.")
                                        .setColor(new Color(88, 214, 141));

                                textChannel.sendMessage(cancel.build()).queue(message2 -> message2.delete().queueAfter(30, TimeUnit.SECONDS));
                                finish.set(true);
                                reason.delete().queue();
                                message1.delete().queue();
                                nextOrder(orderData, message, event, OrderStatus.CANCELED);
                                deleteOrder(orderData.getTime());
                                timer.cancel();
                                timer.purge();

                                Objects.requireNonNull(textChannel.getGuild().getTextChannelById(ConfigurationManager.channelLogs)).sendMessage(new EmbedBuilder().setDescription(Info.negatif_emoji + Objects.requireNonNull(event.getUser()).getName() + " a annulé une commande.\n\nRaison: " + reason.getContentRaw() + "\nRappel de la commande: x" + orderData.getAmount() + " " + orderData.getItem() + "\nPrix: " + orderData.getPrice() + "$").setColor(new Color(234, 62, 51)).build()).queue();
                                message.getTextChannel().delete().queue();
                            });
                        }));
                    } else {
                        event.getReaction().removeReaction(Objects.requireNonNull(event.getUser())).queue();
                    }
                }
            }
        });

        orderData.getOrderLocationData().setCloseMessage(message.getIdLong());
    }

    private double getRMultiplicatorPrice(Member member) {
        if (member.getRoles().parallelStream().anyMatch(role -> role.getIdLong() == 755926608262725722L)) return 0.15;
        else if (member.getRoles().parallelStream().anyMatch(role -> role.getIdLong() == 755929282869461101L || role.getIdLong() == 734458207317262388L))
            return 0.10;
        else return 0.10;
    }
    public double getCasinoPrice(OrderData orderData) {
        if (orderData.getPrice() < 5000) return 250;
        else if (orderData.getPrice() >= 5000 && orderData.getPrice() < 20000) return 1000;
        else if (orderData.getPrice() >= 20000 && orderData.getPrice() < 100000) return 2500;
        else if (orderData.getPrice() >= 100000 && orderData.getPrice() < 300000) return 5000;
        else if (orderData.getPrice() >= 300000 && orderData.getPrice() < 700000) return 7500;
        else if (orderData.getPrice() >= 700000 && orderData.getPrice() < 1000000) return 10000;
        else return 15000;
    }

    boolean nextOrder(OrderData orderData, Message message, MessageReactionAddEvent event, OrderStatus orderStatus) {
        if (!isAllow(Objects.requireNonNull(event.getMember()))) {
            event.getReaction().removeReaction(Objects.requireNonNull(event.getUser())).queue();
            return false;
        }
        orderData.setOrderStatus(orderStatus);
        message.delete().queue();
        OrderData next = getNextOrder(orderData);
        if (next != null) orderManager.sendMessageOrder(next);
        return true;
    }

    public OrderData getNextOrder(OrderData orderData) {
        for (OrderData data : this.orderManager.getOrderData()) {
            if (orderData.getItem().equalsIgnoreCase(data.getItem()) && data.getOrderStatus() == OrderStatus.WAITING && data.getOrderLocationData() == null && !hasOrderSend(orderData)) {
                return data;
            }
        }
        return null;
    }

    private boolean hasOrderSend(OrderData orderData) {
        for (OrderData data : this.orderManager.getOrderData()) {
            if (data.getItem().equalsIgnoreCase(orderData.getItem()) && data.getOrderStatus() == OrderStatus.WAITING && !orderData.equals(data) && data.getOrderLocationData() != null) {
                return true;
            }
        }
        return false;
    }

    void deleteOrder(long time) {
        for (int i = 0; i < this.orderManager.getOrderData().size(); i++) {
            OrderData orderData = this.orderManager.getOrderData().get(i);
            if (orderData.getTime() == time) orderData.setOrderStatus(OrderStatus.CANCELED);
        }
    }

    private boolean containsRole(Member member) {
        for (Role role : member.getRoles()) {
            if (ConfigurationManager.salesRoles.contains(role.getIdLong())) {
                return true;
            }
        }
        return false;
    }

    private boolean isAllow(Member member) {
        return /*member.getUser().getIdLong() != orderData.getBuyer() &&*/ containsRole(member);
    }

    public void callMessage(OrderMessage orderMessage, OrderData orderData, EmbedBuilder embedBuilder, Guild guild, ItemManager itemManager) {
        orderMessage.getConsumer().accept(orderData, embedBuilder, guild, itemManager);
    }
}
