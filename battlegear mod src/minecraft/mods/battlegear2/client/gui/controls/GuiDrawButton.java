package mods.battlegear2.client.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;

import javax.annotation.Nonnull;

public class GuiDrawButton extends GuiButton {
    private final IDrawnHandler drawer;
    private final int initX, initY;
    private boolean dragged = false;
    private int varX, varY;
    public GuiDrawButton(int id, int posX, int posY, int width, int height, IDrawnHandler handler) {
        super(id, posX, posY, width, height, "");
        this.drawer = handler;
        this.initX = posX;
        this.initY = posY;
    }

    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY){
        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        this.drawer.drawElement(new ScaledResolution(mc), xPosition, yPosition);
        this.mouseDragged(mc, mouseX, mouseY);
    }

    @Override
    protected void mouseDragged(Minecraft par1Minecraft, int x, int y){
        if(this.dragged){
            this.xPosition = x - varX;
            this.yPosition = y - varY;
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int x, int y) {
        if(super.mousePressed(mc, x, y)){
            this.varX = x - this.xPosition;
            this.varY = y - this.yPosition;
            this.dragged = true;
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void mouseReleased(int x, int y) {
        this.dragged = false;
        this.xPosition = x - varX;
        this.yPosition = y - varY;
    }

    public int getDragX(){
        return this.xPosition - this.initX;
    }

    public int getDragY(){
        return this.yPosition - this.initY;
    }

    public interface IDrawnHandler{
        void drawElement(ScaledResolution resolution, int varX, int varY);
    }
}
