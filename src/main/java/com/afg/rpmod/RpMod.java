package com.afg.rpmod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.afg.rpmod.IPlayerData.PlayerData;
import com.afg.rpmod.IPlayerData.Storage;

@Mod(
		modid = RpMod.VERSION,
		name = "RP Mod",
		version = RpMod.VERSION,
		clientSideOnly = false,
		serverSideOnly = false,
		dependencies = "required-after:Forge@[12.18.2.2171,)"
		)
@Mod.EventBusSubscriber
public class RpMod
{
	public static final String MODID = "rp-mod";
	public static final String VERSION = "0.1";

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		CapabilityManager.INSTANCE.register(IPlayerData.class, new Storage(), PlayerData.class);
	}

	@SubscribeEvent
	public static void attachCapabilities(AttachCapabilitiesEvent<Entity> e){
		if(e.getObject() instanceof EntityPlayer){
			e.addCapability(new ResourceLocation(RpMod.MODID, "playerdata"), new PlayerData());
		}
	}

	@SubscribeEvent
	public void cloneCapabilitiesEvent(PlayerEvent.Clone event)
	{
		//Should only fire on return from death, otherwise the data is already there
		if(event.isWasDeath()){
			PlayerData sho = (PlayerData) event.getOriginal().getCapability(IPlayerData.PLAYER_DATA, null);
			NBTTagCompound nbt = sho.serializeNBT();
			PlayerData shn = (PlayerData) event.getEntityPlayer().getCapability(IPlayerData.PLAYER_DATA, null);
			shn.deserializeNBT(nbt);
		}
	}



}