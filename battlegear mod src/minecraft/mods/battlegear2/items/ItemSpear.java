package mods.battlegear2.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import mods.battlegear2.api.IBackSheathedRender;
import mods.battlegear2.api.shield.IShield;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSpear extends TwoHandedWeapon implements IBackSheathedRender {

    //Will make it one more than a sword
    private final int mounted_extra_damage;
    private final float reach;

    public ItemSpear(ToolMaterial material, String name, int mount, float reach) {
		super(material,name);
        this.mounted_extra_damage = mount;
        this.reach = reach;
		//set the base damage to that of lower than usual (balance)
		this.baseDamage -= 2;
        GameRegistry.register(this);
	}

	@Override
	public boolean allowOffhand(ItemStack mainhand, ItemStack offhand, EntityPlayer player) {
		return super.allowOffhand(mainhand, offhand, player) || offhand.getItem() instanceof IShield;
	}

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        if(slot == EntityEquipmentSlot.MAINHAND) {
            Multimap<String, AttributeModifier> map = super.getItemAttributeModifiers(slot);
            map.put(extendedReach.getName(), new AttributeModifier(extendReachUUID, "Reach Modifier", this.reach, 0));
            map.put(mountedBonus.getName(), new AttributeModifier(mountedBonusUUID, "Attack Modifier", this.mounted_extra_damage, 0));
            return map;
        }
        return HashMultimap.<String, AttributeModifier>create();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void preRenderBackSheathed(ItemStack itemStack, int amountOnBack, RenderPlayerEvent event, boolean inMainHand) {
        GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
    }
}
