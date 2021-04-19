package fr.winczlav.lostshop;

import fr.winczlav.gao.api.module.GAOModule;
import fr.winczlav.gao.api.module.GAOSet;
import fr.winczlav.lostshop.commands.CatalogueCommand;
import fr.winczlav.lostshop.commands.LivrerCommand;
import fr.winczlav.lostshop.commands.OrderCommand;
import fr.winczlav.lostshop.commands.category.CategoryCommand;
import fr.winczlav.lostshop.commands.category.CategoryManager;
import fr.winczlav.lostshop.commands.category.categoryAdd.CategoryAddManager;
import fr.winczlav.lostshop.commands.item.ItemCommand;
import fr.winczlav.lostshop.commands.item.ItemManager;
import fr.winczlav.lostshop.commands.item.itemAdd.ItemAddManager;
import fr.winczlav.lostshop.config.ConfigurationManager;
import fr.winczlav.lostshop.listener.DeleteChannelEvent;
import fr.winczlav.lostshop.order.OrderLoader;
import fr.winczlav.lostshop.order.OrderManager;
import net.dv8tion.jda.api.entities.Guild;

@GAOSet(name = "LostShop", version = "0.0.1", description = "LostBot", author = "French")
public class Core extends GAOModule {

    private static Core instance;

    private OrderManager orderManager;
    private OrderLoader orderLoader;
    private ItemManager itemManager;
    private ItemAddManager itemAddManager;
    private CategoryManager categoryManager;
    private CategoryAddManager categoryAddManager;
    private ConfigurationManager configurationManager;

    @Override
    public void onEnable() {
        instance = this;

        this.configurationManager = new ConfigurationManager();
        this.configurationManager.load();

        Guild guild = this.getJDA().getGuildById(ConfigurationManager.serverID);
        this.orderLoader = new OrderLoader(this);
        this.itemManager = new ItemManager(this);
        this.categoryManager = new CategoryManager(this);
        this.categoryAddManager = new CategoryAddManager(this.getJDA(), guild);
        this.itemAddManager = new ItemAddManager(this.getJDA());
        this.itemManager.load();
        this.categoryManager.load();
        this.orderManager = new OrderManager(guild, guild.getCategoryById(ConfigurationManager.shopCategoryID), guild.getTextChannelById(ConfigurationManager.tookOrderChannelID), orderLoader, itemManager, itemAddManager);

        this.getJDA().addEventListener(new DeleteChannelEvent(orderManager));

        this.addCommand(new OrderCommand("commande", orderManager, itemManager));
        this.addCommand(new ItemCommand("item", itemManager, itemAddManager, categoryManager));
        this.addCommand(new CatalogueCommand("catalogue", itemManager, categoryManager));
        this.addCommand(new CategoryCommand("category", categoryAddManager, categoryManager, itemManager));
        this.addCommand(new LivrerCommand("livrer", orderManager));
    }

    @Override
    public void onDisable() {
        this.orderLoader.save(orderManager.getOrderData());
        this.itemManager.save();
        this.categoryManager.save();

        System.out.println("Module " + GAOSet.class.getName() + " a été désactivé avec succès.");
    }

    public static Core getInstance() {
        return instance;
    }

    public OrderManager getOrderManager() {
        return orderManager;
    }

    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }
}
