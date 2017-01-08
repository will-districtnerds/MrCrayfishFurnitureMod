/**
 * MrCrayfish's Furniture Mod
 * Copyright (C) 2016  MrCrayfish (http://www.mrcrayfish.com/)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mrcrayfish.furniture.tileentity;

import com.mrcrayfish.furniture.gui.containers.ContainerCabinet;
import com.mrcrayfish.furniture.init.FurnitureSounds;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class TileEntityCabinetKitchen extends TileEntityLockable implements IInventory
{
	private ItemStack[] cabinetContents = new ItemStack[15];
	private String customName;

	public int getSizeInventory()
	{
		return 15;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return this.cabinetContents[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		if (this.cabinetContents[slot] != null)
		{
			ItemStack var3;

			if (this.cabinetContents[slot].stackSize <= amount)
			{
				var3 = this.cabinetContents[slot];
				this.cabinetContents[slot] = null;
				this.markDirty();
				return var3;
			}
			var3 = this.cabinetContents[slot].splitStack(amount);

			if (this.cabinetContents[slot].stackSize == 0)
			{
				this.cabinetContents[slot] = null;
			}

			this.markDirty();
			return var3;
		}
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int slot)
	{
		if (this.cabinetContents[slot] != null)
		{
			ItemStack var2 = this.cabinetContents[slot];
			this.cabinetContents[slot] = null;
			return var2;
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		this.cabinetContents[slot] = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit())
		{
			stack.stackSize = this.getInventoryStackLimit();
		}

		this.markDirty();
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);
		if(tagCompound.hasKey("cabinetItems"))
		{
			NBTTagList tagList = (NBTTagList) tagCompound.getTag("cabinetItems");
			this.cabinetContents = new ItemStack[this.getSizeInventory()];
			for (int i = 0; i < tagList.tagCount(); ++i)
			{
				NBTTagCompound itemTag = (NBTTagCompound) tagList.getCompoundTagAt(i);
				int slot = itemTag.getByte("cabinetSlot") & 255;

				if (slot >= 0 && slot < this.cabinetContents.length)
				{
					this.cabinetContents[slot] = ItemStack.loadItemStackFromNBT(itemTag);
				}
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
		NBTTagList tagList = new NBTTagList();
		for (int slot = 0; slot < this.cabinetContents.length; ++slot)
		{
			if (this.cabinetContents[slot] != null)
			{
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setByte("cabinetSlot", (byte) slot);
				this.cabinetContents[slot].writeToNBT(itemTag);
				tagList.appendTag(itemTag);
			}
		}
		tagCompound.setTag("cabinetItems", tagList);
		return tagCompound;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return this.worldObj.getTileEntity(pos) != this ? false : player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void invalidate()
	{
		this.updateContainingBlockInfo();
		super.invalidate();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack)
	{
		return true;
	}

	@Override
	public String getName()
	{
		return hasCustomName() ? customName : "Kitchen Cabinet";
	}

	@Override
	public boolean hasCustomName()
	{
		return customName != null;
	}

	@Override
	public ITextComponent getDisplayName() 
	{
		return new TextComponentString(getName());
	}

	@Override
	public void openInventory(EntityPlayer player)
	{
		worldObj.playSound(pos.getX(), pos.getY(), pos.getZ(), FurnitureSounds.cabinet_close, SoundCategory.BLOCKS, 0.75F, 0.9F, true);
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
		worldObj.playSound(pos.getX(), pos.getY(), pos.getZ(), FurnitureSounds.cabinet_close, SoundCategory.BLOCKS, 0.75F, 0.8F, true);
	}

	@Override
	public int getField(int id)
	{
		return 0;
	}

	@Override
	public void setField(int id, int value)
	{		
	}

	@Override
	public int getFieldCount()
	{
		return 0;
	}

	@Override
	public void clear()
	{
		for (int i = 0; i < cabinetContents.length; i++)
		{
			cabinetContents[i] = null;
		}	
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) 
	{
		return new ContainerCabinet(playerInventory, this);
	}

	@Override
	public String getGuiID() 
	{
		return "0";
	}
}
