package org.silvercatcher.reforged.items.weapons;

import org.silvercatcher.reforged.api.ExtendedItem;
import org.silvercatcher.reforged.api.ItemExtension;
import org.silvercatcher.reforged.entities.EntityJavelin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemJavelin extends ExtendedItem {
	
	public ItemJavelin() {
		super();
		setUnlocalizedName("javelin");
		setMaxStackSize(8);
		setMaxDamage(32);
	}

	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
        net.minecraftforge.event.entity.player.ArrowNockEvent event = new net.minecraftforge.event.entity.player.ArrowNockEvent(playerIn, itemStackIn);
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return event.result;

        	if (playerIn.capabilities.isCreativeMode || playerIn.inventory.hasItem(this))
        		{
        			playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        		}

        return itemStackIn;
    }
	

	@Override
	public void registerRecipes() {
		
		GameRegistry.addRecipe(new ItemStack(this),
				"  f",
				" s ",
				"s  ",
				'f', new ItemStack(Items.flint),
				's', new ItemStack(Items.stick));
	}


	@Override
	public float getHitDamage() {
		return 3f;
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.BOW;
    }
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack)
    {
        return ItemExtension.USE_DURATON;
    }
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer playerIn, int timeLeft) {

		ItemStack throwStack = stack.copy();
		
		if(timeLeft <= getMaxItemUseDuration(stack) - 7 && (playerIn.capabilities.isCreativeMode || playerIn.inventory.consumeInventoryItem(this))) {
			
			worldIn.playSoundAtEntity(playerIn, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if (!worldIn.isRemote) {
				if(throwStack.stackSize > 1) {
		        	throwStack = throwStack.splitStack(1);
				}
	        	worldIn.spawnEntityInWorld(new EntityJavelin(worldIn, playerIn, throwStack, stack.getMaxItemUseDuration() - timeLeft));
	        }
	    }
    }
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		return stack;
	}
	
	@Override
	public int getItemEnchantability(ItemStack stack) {
		return ToolMaterial.STONE.getEnchantability();
	}
}
