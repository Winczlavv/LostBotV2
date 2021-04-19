package fr.winczlav.lostshop.commands;

import fr.winczlav.gao.api.command.ICommand;
import fr.winczlav.lostshop.order.OrderManager;
import fr.winczlav.lostshop.utils.Info;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.concurrent.TimeUnit;

import static fr.winczlav.lostshop.utils.Util.getSplitSold;

public class LivrerCommand extends ICommand {

    private final OrderManager orderManager;

    public LivrerCommand(String command, OrderManager orderManager) {
        super(command, Info.marketPart);
        this.orderManager = orderManager;
    }

    @Override
    public void onCommand(Message message, User user, Member member, TextChannel textChannel, Guild guild, String[] args) {
        message.delete().queue();
        if (member.getRoles().parallelStream().noneMatch(role -> role.getIdLong() == 647138042464698378L))
            return;

        if (args.length < 2) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setDescription(Info.negatif_emoji + "Erreur de syntaxe : !livrer <pseudo> <serveur>")
                    .setFooter("→ Exemple : /livrer Frenchou_ Forestia")
                    .setColor(new Color(234, 62, 51));

            textChannel.sendMessage(embedBuilder.build()).queue(message1 -> message1.delete().queueAfter(10, TimeUnit.SECONDS));


        } else {
            orderManager.getOrderData().parallelStream().filter(orderData1 -> orderData1.getOrderLocationData().getTextchannel() == textChannel.getIdLong()).findFirst().ifPresent(orderData -> textChannel.sendMessage(new EmbedBuilder().setDescription(String.format("Ta commande est prête ! Je t’invite à te rendre sur le %s pour trade avec %s ! Tu devras payer %s$ avant d'être tp.", args[1], args[0], (getSplitSold(orderData.getPrice())))).build()).queue());
                   }
    }
}
