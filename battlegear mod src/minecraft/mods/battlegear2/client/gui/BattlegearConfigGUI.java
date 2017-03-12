package mods.battlegear2.client.gui;

import mods.battlegear2.client.gui.controls.GUITextList;
import mods.battlegear2.client.gui.controls.GuiToggleButton;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.util.List;

public final class BattlegearConfigGUI extends GuiScreen{
    private final String[] availableRenderers = BattlegearConfig.renderNames;
    private final GuiScreen parent;
    private GUITextList possibleValues;
    public BattlegearConfigGUI(GuiScreen parent){
        this.parent = parent;
    }

    @Override
    public void initGui(){
        int listWidth = 0;
        String txt;
        GUITextList.Box[] renders = new GUITextList.Box[availableRenderers.length];
        for(int i=0;i<availableRenderers.length;i++){
            txt = availableRenderers[i];
            if(this.fontRenderer.getStringWidth(txt)>listWidth){
                listWidth = this.fontRenderer.getStringWidth(txt);
            }
            renders[i] = new GUITextList.Box(txt, !BattlegearConfig.hasRender(txt));
        }
        this.possibleValues = new GUITextList(this.fontRenderer,listWidth+10,this.height / 2,this.height / 2 + 60,this.width / 2,12,renders);
        this.buttonList.add(new GuiButton(1, this.width / 2 - 75, this.height - 38, I18n.format("gui.done")));
        this.buttonList.add(new GuiToggleButton(2, this.width / 2 - 180, this.height / 2 - 100, I18n.format("use.gui.buttons")+":"+BattlegearConfig.enableGuiButtons, this.fontRenderer));
        this.buttonList.add(new GuiToggleButton(3, this.width / 2 + 20, this.height / 2 - 100, I18n.format("use.gui.keys")+":"+BattlegearConfig.enableGUIKeys, this.fontRenderer));
        this.buttonList.add(new GuiToggleButton(4, this.width / 2 - 180, this.height / 2 - 70, I18n.format("render.quiver.skeleton")+":"+BattlegearConfig.enableSkeletonQuiver, this.fontRenderer));
        this.buttonList.add(new GuiToggleButton(5, this.width / 2 + 20, this.height / 2 - 70, I18n.format("render.arrow.bow")+":"+BattlegearConfig.arrowForceRendered, this.fontRenderer));
        this.buttonList.add(new GuiToggleButton(6, this.width / 2 - 180, this.height / 2 - 40, BattlegearConfig.forceSheath.format(), this.fontRenderer));
        this.buttonList.add(new GuiToggleButton(7, this.width / 2 + 20, this.height / 2 + 60, I18n.format("render.hud.forced")+":"+BattlegearConfig.forceHUD, this.fontRenderer));
        this.possibleValues.registerScrollButtons(this.buttonList, 8, 9);
        this.buttonList.add(new GuiButton(10, this.width / 2 - 180, this.height / 2 + 60, I18n.format("gui.open.fake")));
    }

    @Override
    protected void actionPerformed(GuiButton button){
        if (button.enabled){
            if(button.id == 1){
                FMLClientHandler.instance().showGuiScreen(parent);
            }else if(button.id == 2){
                BattlegearConfig.enableGuiButtons = !BattlegearConfig.enableGuiButtons;
            }else if(button.id == 3){
                BattlegearConfig.enableGUIKeys = !BattlegearConfig.enableGUIKeys;
            }else if(button.id == 4){
                BattlegearConfig.enableSkeletonQuiver = !BattlegearConfig.enableSkeletonQuiver;
            }else if(button.id == 5){
                BattlegearConfig.arrowForceRendered = !BattlegearConfig.arrowForceRendered;
            }else if(button.id == 6){
                BattlegearConfig.forceSheath = BattlegearConfig.forceSheath.next();
                button.displayString = BattlegearConfig.forceSheath.format();
            }else if(button.id == 7){
                BattlegearConfig.forceHUD = !BattlegearConfig.forceHUD;
            }else if(button.id == 10){
                FMLClientHandler.instance().showGuiScreen(new BattlegearFakeGUI(parent));
            }
            if(button instanceof GuiToggleButton){
                ((GuiToggleButton) button).toggleDisplayString();
            }else{
                this.possibleValues.actionPerformed(button);
            }
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3){
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18n.format("config.battlegear.title"), this.width / 2, this.height / 2 - 115, 0xFFFFFF);
        this.drawCenteredString(this.fontRenderer, I18n.format("config.battlegear.category"), this.width / 2, this.height / 2 - 15, 0xFFFFFF);
        this.fontRenderer.drawSplitString(I18n.format("config.battlegear.warn"), this.width / 2 + 60, this.height / 2 + 20, this.width / 2 - 60, 0xFFFFFF);
        this.possibleValues.drawScreen(par1, par2, par3);
        super.drawScreen(par1, par2, par3);
    }

    @Override
    public void onGuiClosed(){
        super.onGuiClosed();
        List<String> temp = this.possibleValues.getActivated();
        String[] disabled = temp.toArray(new String[temp.size()]);
        BattlegearConfig.refreshConfig(disabled);
    }
}
