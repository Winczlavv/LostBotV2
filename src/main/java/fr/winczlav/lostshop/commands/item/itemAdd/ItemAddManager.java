package fr.winczlav.lostshop.commands.item.itemAdd;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ItemAddManager {

    private final Map<Member, BiConsumer<Member, Message>> messages;

    public ItemAddManager(JDA jda) {
        this.messages = new HashMap<>();
        jda.addEventListener(new ItemAddHandler(this));
    }

    public Map<Member, BiConsumer<Member, Message>> getMessages() { return messages; }

    public void register(Member member, BiConsumer<Member, Message> biConsumer){
        messages.put(member, biConsumer);
    }

    public void unregister(Member member){
        messages.remove(member);
    }

    public List<String> getCheckEmoji(Guild guild){
        return guild.getEmotes().stream().map((emoji) -> "<:" + emoji.getName() + ":" + emoji.getId() + ">").collect(Collectors.toList());
    }

    public List<String> getEmojiIdList(Guild guild){
        return guild.getEmotes().stream()
                .map(ISnowflake::getId)
                .collect(Collectors.toList());
    }

    public List<String> getEmojiNameList(Guild guild){
        return guild.getEmotes().stream()
                .map(Emote::getName)
                .collect(Collectors.toList());
    }
}
