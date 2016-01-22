package org.silvercatcher.reforged.entities;

import org.silvercatcher.reforged.ReforgedResources.GlobalValues;
import org.silvercatcher.reforged.items.weapons.ItemBoomerang;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityBoomerang extends EntityThrowable {
	
	public EntityBoomerang(World worldIn) {
		
		super(worldIn);
	}
	
	public EntityBoomerang(World worldIn, EntityLivingBase getThrowerIn, ItemStack stack) {
		
		super(worldIn, getThrowerIn);
		setItemStack(stack);
		setThrowerName(getThrowerIn.getName());
		setCoords(getThrowerIn.posX, getThrowerIn.posY, getThrowerIn.posZ);
		this.setPositionAndRotation(getThrowerIn.posX, getThrowerIn.posY, getThrowerIn.posZ, getThrowerIn.rotationYaw, getThrowerIn.rotationPitch);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		
		// id 5 = ItemStack of Boomerang, type 5 = ItemStack
		dataWatcher.addObjectByDataType(5, 5);
		
		// id 6 = Name of Thrower, type 4 = String
		dataWatcher.addObjectByDataType(6, 4);
		
		// id 7 = posX, type 3 = float
		dataWatcher.addObjectByDataType(7, 3);
		
		// id 8 = posY, type 3 = float
		dataWatcher.addObjectByDataType(8, 3);
		
		// id 9 = posZ, type 3 = float
		dataWatcher.addObjectByDataType(9, 3);
	}

	public ItemStack getItemStack() {
		
		return dataWatcher.getWatchableObjectItemStack(5);
	}
	
	public void setItemStack(ItemStack stack) {
		
		if(stack == null || !(stack.getItem() instanceof ItemBoomerang)) {
			throw new IllegalArgumentException("Invalid Itemstack!");
		}
		dataWatcher.updateObject(5, stack);
	}
	
	public void setCoords(double playerX, double playerY, double playerZ) {
		dataWatcher.updateObject(7, (float) playerX);
		dataWatcher.updateObject(8, (float) playerY);
		dataWatcher.updateObject(9, (float) playerZ);
	}
	
	public double getCoord(int coordId) {
		switch(coordId) {
		//1 returns X, 2 returns Y, 3 returns Z
		case 1: return (double) dataWatcher.getWatchableObjectFloat(7);
		case 2: return (double) dataWatcher.getWatchableObjectFloat(8);
		case 3: return (double) dataWatcher.getWatchableObjectFloat(9);
		default: throw new IllegalArgumentException("Invalid coordId!");
		}
	}
	
	public EntityLivingBase getThrowerASave() {
		return getEntityWorld().getPlayerEntityByName(getThrowerName());
	}
	
	public String getThrowerName() {
		return dataWatcher.getWatchableObjectString(6);
	}
	
	public void setThrowerName(String name) {
		
		dataWatcher.updateObject(6, name);
	}
	
	public ToolMaterial getMaterial() {

		return ((ItemBoomerang) getItemStack().getItem()).getMaterial();
	}

	private float getImpactDamage() {
		
		return getMaterial().getDamageVsEntity()  + 3;
	}
	
	private static final double returnStrength = 0.05D;
	
	@Override
	public void onUpdate() {
		if(!getEntityWorld().isRemote) {
			super.onUpdate();
			double dx = this.posX - getCoord(1);
			double dy = this.posY - getCoord(2);
			double dz = this.posZ - getCoord(3);
			
			double d = Math.sqrt(dx * dx + dy * dy + dz * dz);
			dx /= d;
			dy /= d;
			dz /= d;
			
			motionX -= returnStrength * dx;
			motionY -= returnStrength * dy;
			motionZ -= returnStrength * dz;
		}
	}
	
	@Override
	protected float getGravityVelocity() {
		return 0.0F;
	}

	@Override
	protected void onImpact(MovingObjectPosition target) {
			
		//Target is entity or block?
		if(target.entityHit == null) {
			//It's a block
			//Distance specifies the range the boomerang should get auto-collected [CONFIG STUFF!]
			int distance = GlobalValues.DISTANCE_BOOMERANG;
			this.setDead();
			BlockPos bp = target.getBlockPos();
			BlockPos pp = getThrowerASave().getPosition();
			if(!worldObj.isRemote && Math.abs(bp.getX() - pp.getX()) <= distance && Math.abs(bp.getY() - pp.getY()) <= distance && Math.abs(bp.getY() - pp.getY()) <= distance) {
				EntityPlayer p = (EntityPlayer) getThrowerASave();
				p.inventory.addItemStackToInventory(getItemStack());
			} else if(!worldObj.isRemote) {
				entityDropItem(getItemStack(), 0.5f);
			}
		} else {
			//It's an entity
			if(target.entityHit != getThrowerASave()) {
				//It's an hit entity
				target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(
						target.entityHit, getThrowerASave()), getImpactDamage());
				ItemStack stack = getItemStack();
				if(stack.attemptDamageItem(1, rand)) {
					this.setDead();
				} else {
					setItemStack(stack);
				}
			} else {
				//It's the thrower himself
				this.setDead();
				ItemStack stack = getItemStack();
				EntityPlayer p = (EntityPlayer) target.entityHit;
				p.inventory.addItemStackToInventory(stack);
			}
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tagCompound) {
		
		super.writeEntityToNBT(tagCompound);
		
		tagCompound.setString("thrower", getThrower().getName());
		tagCompound.setDouble("throwerX", getCoord(1));
		tagCompound.setDouble("throwerY", getCoord(2));
		tagCompound.setDouble("throwerZ", getCoord(3));
		
		if(getItemStack() != null) {
			tagCompound.setTag("item", getItemStack().writeToNBT(new NBTTagCompound()));
		}
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound tagCompund) {
		
		super.readEntityFromNBT(tagCompund);
		setItemStack(ItemStack.loadItemStackFromNBT(tagCompund.getCompoundTag("item")));
		setCoords(tagCompund.getDouble("throwerX"), tagCompund.getDouble("throwerY"), tagCompund.getDouble("throwerZ"));
		setThrowerName(tagCompund.getString("thrower"));
	}
}
