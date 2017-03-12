package mods.battlegear2.client.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Olivier on 03/03/14.
 */
public class GuiToggeableButton extends GuiButton {

    private ResourceLocation image;
    private List<String> tooltip;

    public GuiToggeableButton(int id, int x, int y, int w, int h, String tooltip, boolean isOn, ResourceLocation image) {
        super(id, x, y, w, h, "");

        this.tooltip = new ArrayList<String>(1);
        this.tooltip.add(tooltip);

        this.image = image;

        enabled = !isOn;
    }

    public boolean isOn(){
        return !enabled;
    }

    public void setToggle(boolean toggle){
        enabled = !toggle;
    }

    @Override
    public void drawButton(@Nonnull Minecraft par1Minecraft, int par2, int par3) {
        super.drawButton(par1Minecraft, par2, par3);

        int start_x = (width - 16) / 2 + xPosition;
        int start_y = (height - 16) / 2 + yPosition;

        par1Minecraft.getTextureManager().bindTexture(image);


        GlStateManager.color(1, 1, 1);
        this.drawTexturedModalRect(start_x, start_y, 16, 16, 0, 1, 1, -1);

        if(par2 >= xPosition && par2 <=xPosition+width && par3 >= yPosition && par3 <=yPosition+height)
            drawHoveringText(tooltip, par2, par3, par1Minecraft.fontRenderer);
    }

    protected void drawHoveringText(List par1List, int par2, int par3, FontRenderer font)
    {
        if (!par1List.isEmpty())
        {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableDepth();
            int k = 0;
            Iterator iterator = par1List.iterator();

            while (iterator.hasNext())
            {
                String s = (String)iterator.next();
                int l = font.getStringWidth(s);

                if (l > k)
                {
                    k = l;
                }
            }

            int i1 = par2 + 12;
            int j1 = par3 - 12;
            int k1 = 8;

            if (par1List.size() > 1)
            {
                k1 += 2 + (par1List.size() - 1) * 10;
            }



            this.zLevel = 300.0F;
            zLevel = 300.0F;
            int l1 = -267386864;
            this.drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1);
            this.drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1);
            this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1);
            this.drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1);
            this.drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1);
            int i2 = 1347420415;
            int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
            this.drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, i2, j2);
            this.drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, i2, j2);
            this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2);
            this.drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);

            for (int k2 = 0; k2 < par1List.size(); ++k2)
            {
                String s1 = (String)par1List.get(k2);
                font.drawStringWithShadow(s1, i1, j1, -1);

                if (k2 == 0)
                {
                    j1 += 2;
                }

                j1 += 10;
            }

            this.zLevel = 0.0F;
            zLevel = 0.0F;
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    public void drawTexturedModalRect(int x, int y, int width, int height, int tex_x, int tex_y, int tex_width, int tex_height)
    {
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        tessellator.getBuffer().pos((double) (x + 0), (double) (y + height), (double) this.zLevel).tex((double) ((float) (tex_x + 0)), (double) ((float) (tex_y + tex_height))).endVertex();
        tessellator.getBuffer().pos((double) (x + width), (double) (y + height), (double) this.zLevel).tex((double) ((float) (tex_x + tex_width)), (double) ((float) (tex_y + tex_height))).endVertex();
        tessellator.getBuffer().pos((double) (x + width), (double) (y + 0), (double) this.zLevel).tex((double) ((float) (tex_x + tex_width)), (double) ((float) (tex_y + 0))).endVertex();
        tessellator.getBuffer().pos((double) (x + 0), (double) (y + 0), (double) this.zLevel).tex((double) ((float) (tex_x + 0)), (double) ((float) (tex_y + 0))).endVertex();
        tessellator.draw();
    }
}