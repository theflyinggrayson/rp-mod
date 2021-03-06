package com.afg.rpmod.entities;

import com.afg.rpmod.client.gui.JobListGui;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class NPCJobGiver extends EntityNPC {

	public NPCJobGiver(World worldIn) {
		super(worldIn);
	}

	@Override
	public boolean interact(EntityPlayer player, @Nullable ItemStack stack,
			EnumHand hand) {
		if(this.world.isRemote){
			Minecraft.getMinecraft().displayGuiScreen(new JobListGui());
		}
		return true;
	}

}
