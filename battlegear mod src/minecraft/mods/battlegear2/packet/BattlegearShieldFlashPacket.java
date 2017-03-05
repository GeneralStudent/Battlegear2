package mods.battlegear2.packet;

import io.netty.buffer.ByteBuf;
import mods.battlegear2.Battlegear;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.UUID;

public final class BattlegearShieldFlashPacket extends AbstractMBPacket{

    public static final String packetName = "MB2|ShieldFlash";
	private String username;
	private float damage;

    public BattlegearShieldFlashPacket(EntityPlayer player, float damage) {
    	this.username = player.getCachedUniqueIdString();
    	this.damage = damage;
    }

	public BattlegearShieldFlashPacket() {
	}
    
    @Override
    public void process(ByteBuf in,EntityPlayer player) {
        UUID id;
        try {
            username = ByteBufUtils.readUTF8String(in);
            damage = in.readFloat();
            id = UUID.fromString(username);
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        EntityPlayer targetPlayer = player.world.getPlayerEntityByUUID(id);
        if(targetPlayer!=null)
            Battlegear.proxy.startFlash(targetPlayer, damage);
    }

	@Override
	public String getChannel() {
		return packetName;
	}

	@Override
	public void write(ByteBuf out) {
        ByteBufUtils.writeUTF8String(out, username);
        out.writeFloat(damage);
	}
}
