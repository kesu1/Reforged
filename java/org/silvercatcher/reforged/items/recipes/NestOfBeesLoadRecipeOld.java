package org.silvercatcher.reforged.items.recipes;

import java.util.LinkedList;

import org.silvercatcher.reforged.api.CompoundTags;
import org.silvercatcher.reforged.api.ReforgedAdditions;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

//JUST A BACKUP

public class NestOfBeesLoadRecipeOld implements IRecipe {

	private ItemStack output;
	private ItemStack input [];
	
	private static void printInventory(String name, InventoryCrafting inventory) {
	
		if(Minecraft.getMinecraft().theWorld != null) {
			
			System.out.append(name);
			System.out.append(":\t[");
			for(int i = 0; i < inventory.getSizeInventory(); i++) {
				System.out.append(inventory.getStackInSlot(i) + ",");
			}
			System.out.append("]");
			System.out.println();
		}
	}
	
	@Override
	public boolean matches(InventoryCrafting inventory, World world) {
		
		//printInventory("match", inventory);
				
		int nestsOfBees = 0;
		int arrowBundles = 0;
		
		for(int i = 0; i < inventory.getSizeInventory(); i++) {
			
			ItemStack stack = inventory.getStackInSlot(i);
			
			if(stack != null) {
				if(stack.getItem() == ReforgedAdditions.NEST_OF_BEES) {
					if(stack.getTagCompound().getInteger(CompoundTags.AMMUNITION) + 1 > 32) {
						return false;
					} else {
						nestsOfBees++;
						output = stack.copy();
					}
				} else if(stack.getItem() == ReforgedAdditions.ARROW_BUNDLE) {
					arrowBundles++;
				} else {
					// we don't want any other stuff!
					return false;
				}
			}
		}
		
		return nestsOfBees == 1 && arrowBundles > 0;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventory) {

		//printInventory("result", inventory);
		
		int size = inventory.getSizeInventory();
		
		// remember where to get arrow bundles....better idea?
		LinkedList<Integer> arrowBundleIndizes = new LinkedList<Integer>();
		
		input = new ItemStack[size];
		
		for(int i = 0; i < size; i++) {
			
			ItemStack stack = inventory.getStackInSlot(i);
			
			if(stack != null) {
				Item item = stack.getItem();
				if(item == ReforgedAdditions.NEST_OF_BEES) {
					output = stack.copy();
				} else if(item == ReforgedAdditions.ARROW_BUNDLE) {
					arrowBundleIndizes.add(i);
					input[i] = stack.copy();
				}
			}
		}
		
		NBTTagCompound compound = CompoundTags.giveCompound(output);
		
		int arrows = compound.getInteger(CompoundTags.AMMUNITION);
		
		// please terminate, please terminate,...
		while(!arrowBundleIndizes.isEmpty()) {
			
			int arrowBundleSlot = arrowBundleIndizes.removeFirst();
			
			ItemStack arrowBundleStack = inventory.getStackInSlot(arrowBundleSlot);
			arrows += 8;
			if(arrows > 32) {
				arrows = 32;
			}
			input[arrowBundleSlot].stackSize -= 4;
		}

		compound.setInteger(CompoundTags.AMMUNITION, arrows);

		return output;
	}

	@Override
	public int getRecipeSize() {
		
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput() {
		
		return output;
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inventory) {
		
		//printInventory("remain", inventory);
		
		return ForgeHooks.defaultRecipeGetRemainingItems(inventory);
	}
}
