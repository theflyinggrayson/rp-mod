package com.afg.rpmod.capabilities;

import com.afg.rpmod.RpMod;
import com.afg.rpmod.jobs.Job;
import com.afg.rpmod.network.UpdateClientPlayerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public interface IPlayerData {

	@CapabilityInject(IPlayerData.class)
	public static final Capability<IPlayerData> PLAYER_DATA = null;

	public double getMoney();

	public void setMoney(double amount);

	public double getBankMoney();

	public void setBankMoney(double amount);

	public Job getJob();

	public void setJob(Job job);

	public int getJobLvl();

	public void setJobLvl(int level);

	public int getJobXP();

	public void setJobXP(int xp);
	
	public int getTotalPlaytime();
	
	public void increaseTotalPlaytime();

	/**
	 * Default NBTStorage required by Forge (Just defers to instance)
	 *
	 */
	static class Storage implements Capability.IStorage<IPlayerData> {

		@Override
		public NBTBase writeNBT(Capability<IPlayerData> capability, IPlayerData instance, EnumFacing side) {
			if(instance instanceof PlayerData)
				return ((PlayerData)instance).serializeNBT();
			return null;
		}

		@Override
		public void readNBT(Capability<IPlayerData> capability, IPlayerData instance, EnumFacing side, NBTBase nbt) {
			if(instance instanceof PlayerData && nbt instanceof NBTTagCompound)
				((PlayerData)instance).deserializeNBT((NBTTagCompound) nbt);
		}
	}

	static class PlayerData implements IPlayerData, ICapabilityProvider, INBTSerializable<NBTTagCompound> {
		private double money = 0.0, bankMoney = 0.0;
		private Job job;
		private int jobLevel = 1, jobXP = 0;
		private int totalPlaytime = 0;
		private EntityPlayer player;

		public PlayerData(EntityPlayer player) {
			this.player = player;
		}
		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble("money", this.money);
			tag.setDouble("bankmoney", this.bankMoney);
			if(this.job != null)
				tag.setInteger("job", this.job.getType().getID());
			tag.setInteger("joblvl", this.jobLevel);
			tag.setInteger("jobxp", this.jobXP);
			tag.setInteger("totalplaytime", this.totalPlaytime);
			return tag;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			this.money = nbt.getInteger("money");
			this.bankMoney = nbt.getInteger("bankmoney");
			int jobType = nbt.getInteger("job");
			if(this.job == null || this.job.getType().getID() != jobType){
				this.job = Job.createJob(jobType, this.player);
			}
			this.jobXP = nbt.getInteger("jobxp");
			this.jobLevel = nbt.getInteger("joblvl");
			this.totalPlaytime = nbt.getInteger("totalplaytime");
		}

		public void sync(){
			if(!this.player.world.isRemote)
				RpMod.networkWrapper.sendTo(new UpdateClientPlayerData(this), (EntityPlayerMP) this.player);
		}

		@Override
		public boolean hasCapability(Capability<?> capability,
				@Nullable EnumFacing facing) {
			return (PLAYER_DATA != null && capability == PLAYER_DATA);
		}

		@Override
		public <T> T getCapability(Capability<T> capability,
				@Nullable EnumFacing facing) {
			if (PLAYER_DATA != null && capability == PLAYER_DATA)
				return PLAYER_DATA.cast(this);
			return null;
		}

		@Override
		public double getMoney() {
			return this.money;
		}

		@Override
		public Job getJob() {
			return this.job;
		}
		@Override
		public int getJobLvl() {
			return this.jobLevel;
		}
		@Override
		public int getJobXP() {
			return this.jobXP;
		}

		@Override
		public double getBankMoney() {
			return this.bankMoney;
		}

		@Override
		public void setMoney(double amount) {
			this.money = amount;
			this.sync();
		}
		
		@Override
		public void setBankMoney(double amount) {
			this.bankMoney = amount;
			this.sync();
		}
		@Override
		public void setJob(Job job) {
			this.job = job;
			this.sync();
		}
		@Override
		public void setJobLvl(int level) {
			this.jobLevel = level;
			this.sync();
		}
		
		@Override
		public void setJobXP(int xp) {
			this.jobXP = xp;
			this.sync();
		}
		
		@Override
		public int getTotalPlaytime() {
			return this.totalPlaytime;
		}
		@Override
		public void increaseTotalPlaytime() {
			this.totalPlaytime++;
			this.sync();
		}

	}

	
}
