package mods.mud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * MUD is a client side only utility for mods to send automated report for updates and changelog
 */
public class ModUpdateDetector {
    public static Logger logger = LogManager.getLogger("M.U.D");
    private static boolean hasInitialised = false;
    private static Map<String, UpdateEntry> updateMap;
    public static boolean hasChecked = false;
    private static Configuration config;
    private static Property check;
    public static boolean enabled = true, verbose = false;
    private static boolean deleteOld, deleteFailed;
    private static ICommandSender sender = null;
    private static Thread update;

    /**
     * The main registration method for a mod
     * @param mc The FML wrapper for a mod, you can get it with {@link FMLCommonHandler#findContainerFor(Object)}
     * @param updateXML An expected url for an xml file, listing mod versions and download links by Minecraft releases
     * @param changelog An expected url for a file containing text to describe any changes, can be null
     */
    public static void registerMod(ModContainer mc, URL updateXML, URL changelog){
        registerMod(new UpdateEntry(mc, updateXML, changelog));
    }

    /**
     * Helper registration method for a mod
     * @param mc The FML wrapper for a mod, you can get it with {@link FMLCommonHandler#findContainerFor(Object)}
     * @param updateXML String that can be converted as an url for an xml file, listing mod versions and download links by Minecraft releases
     * @param changelog String that can be converted as an url for a file containing text to describe any changes, can be null
     * @throws MalformedURLException If no known protocol is found, or <tt>updateXML</tt> is <tt>null</tt>.
     */
    public static void registerMod(ModContainer mc, String updateXML, String changelog) throws MalformedURLException {
        registerMod(mc, new URL(updateXML), changelog!=null?new URL(changelog):null);
    }

    /**
     * Helper registration method for a mod
     * @param mod A modid or mod instance
     * @param updateXML String that can be converted as an url for an xml file, listing mod versions and download links by Minecraft releases
     * @param changelog String that can be converted as an url for a file containing text to describe any changes, can be null
     * @throws MalformedURLException If no known protocol is found, or <tt>updateXML</tt> is <tt>null</tt>.
     */
    public static void registerMod(Object mod, String updateXML, String changelog) throws MalformedURLException {
        registerMod(FMLCommonHandler.instance().findContainerFor(mod), updateXML, changelog);
    }

    /**
     * In-depth registration method for a mod, recommended with custom UpdateEntry instances
     */
    public static void registerMod(UpdateEntry entry){
        if(!hasInitialised){
            initialise();
            hasInitialised = true;
        }
        updateMap.put(entry.getMc().getModId(), entry);
    }

    public static void runUpdateChecker(){
        if(enabled){
            if(verbose) {
                ICommandSender sender = getSender();
                if(sender != null)
                sender.sendMessage(new TextComponentString(
                        TextFormatting.YELLOW + I18n.format("mud.name") +
                                TextFormatting.WHITE + ": " + I18n.format("message.checking")));
            }
            if(update == null || !update.isAlive()) {
                update = new Thread(new UpdateChecker(updateMap.values()));
                update.setDaemon(true);
                update.start();
            }
        }
    }

    public static boolean isChecking(){
        return enabled && update != null && update.isAlive();
    }

    public static Collection<UpdateEntry> getAllUpdateEntries(){
        return updateMap.values();
    }

    private static void initialise() {
        updateMap = new HashMap<String, UpdateEntry>();
        /*
         * The time between update checks in minutes.
         * A value <=0 will only run the updater when a player joins the world.
         */
        int Timer = 60*60*20;
        try{
	        config = new Configuration(new File(Loader.instance().getConfigDir(), "MUD.cfg"));
	        Timer = config.get(Configuration.CATEGORY_GENERAL, "Update Time", 60, "The time in minutes between update checks").getInt() * 60 * 20;
            check = config.get(Configuration.CATEGORY_GENERAL, "Update Check Enabled", true, "Should MUD automatically check for updates");
	        verbose = config.getBoolean("Chat stats", Configuration.CATEGORY_GENERAL, false, "Should MUD print in chat its status");
            enabled = check.getBoolean();
            deleteOld = config.getBoolean("Remove old file", Configuration.CATEGORY_GENERAL, true, "Should MUD try to remove old file when download is complete");
            deleteFailed = config.getBoolean("Remove failed download", Configuration.CATEGORY_GENERAL, true, "Should MUD try to remove the new file created if download is failed");
	        if(config.hasChanged()){
	            config.save();
	        }
        }catch(Exception handled){
        	handled.printStackTrace();
        }
        Object listener = new ModUpdateDetectorTickHandeler(Timer);
        MinecraftForge.EVENT_BUS.register(listener);
        ClientCommandHandler.instance.registerCommand(new MudCommands());
    }

    public static boolean deleteOnComplete(){
        return deleteOld;
    }

    public static boolean deleteOnFailure(){
        return deleteFailed;
    }

    public static void toggleState(){
        enabled = !enabled;
        check.set(enabled);
        config.save();
    }

    public static ICommandSender getSender() {
        if(sender == null){
        	sender = Minecraft.getMinecraft().player;
        }
        return sender;
    }

    public static void notifyUpdateDone(){
        ICommandSender sender = getSender();
        if(verbose && sender != null){
            sender.sendMessage(new TextComponentString(
                    TextFormatting.YELLOW + I18n.format("mud.name") +
                            TextFormatting.WHITE + ": "+I18n.format("message.check.done")
            ));
        }

        int outOfDateCount = 0;
        int failedCount = 0;
        for(UpdateEntry e : updateMap.values()){
            try {
                if(!e.isUpToDate()){
                    outOfDateCount ++;
                }
            } catch (Exception e1) {
                failedCount++;
            }
        }
        TextComponentTranslation chat;
        if(outOfDateCount > 0){
            if(sender != null){
                chat = new TextComponentTranslation("message.you.have.outdated", outOfDateCount);
                chat.getStyle().setColor(TextFormatting.RED);
                sender.sendMessage(chat);
                chat = new TextComponentTranslation("message.type.to.view");
                chat.getStyle().setColor(TextFormatting.RED).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mud"));
                sender.sendMessage(chat);
            }
        }else if (verbose){
            if(sender != null){
                chat = new TextComponentTranslation("message.up.to.date");
                chat.getStyle().setColor(TextFormatting.DARK_GREEN);
                sender.sendMessage(chat);
            }
        }
        hasChecked = true;
    }
}
