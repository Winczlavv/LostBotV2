package fr.winczlav.lostshop.config;

import io.github.portlek.configs.annotations.Config;
import io.github.portlek.configs.annotations.Property;
import io.github.portlek.configs.managed.FileManaged;
import io.github.portlek.configs.type.YamlFileType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Config(name = "market_configuration", type = YamlFileType.class)
public class ConfigurationManager extends FileManaged {

    @Override
    public void onLoad() {
        setAutoSave(true);
    }

    @Property public static long serverID = 0L;
    @Property public static long shopCategoryID = 0L;
    @Property public static long tookOrderChannelID = 0L;
    @Property public static List<Long> salesRoles = new ArrayList<>(Arrays.asList(0L, 1L, 2L));
    @Property public static long certifiedrole = 0L;

    @Property public static boolean canDoOrderEveryWhere = true;
    @Property public static long channelToOrder = 0L;
    @Property public static long channelLogs = 0L;
    @Property public static long channelVerif = 0L;
    @Property public static long channelSpecial = 0L;


    @Property public static boolean mentionSalesRoleOnOrder = false;
    @Property public static long payChannelID = 0L;
    @Property public static String bank = "";
}
