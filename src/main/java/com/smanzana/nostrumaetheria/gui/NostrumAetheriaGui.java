package com.smanzana.nostrumaetheria.gui;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.blocks.AetherBoilerBlock.AetherBoilerBlockEntity;
import com.smanzana.nostrumaetheria.blocks.AetherChargerBlock.AetherChargerBlockEntity;
import com.smanzana.nostrumaetheria.blocks.AetherFurnaceBlock.AetherFurnaceBlockEntity;
import com.smanzana.nostrumaetheria.blocks.AetherRepairerBlock.AetherRepairerBlockEntity;
import com.smanzana.nostrumaetheria.gui.container.ActivePendantGui;
import com.smanzana.nostrumaetheria.gui.container.AetherBoilerGui;
import com.smanzana.nostrumaetheria.gui.container.AetherBoilerGui.AetherBoilerGuiContainer;
import com.smanzana.nostrumaetheria.gui.container.AetherChargerGui;
import com.smanzana.nostrumaetheria.gui.container.AetherChargerGui.AetherChargerGuiContainer;
import com.smanzana.nostrumaetheria.gui.container.AetherFurnaceGui;
import com.smanzana.nostrumaetheria.gui.container.AetherFurnaceGui.AetherFurnaceGuiContainer;
import com.smanzana.nostrumaetheria.gui.container.AetherRepairerGui;
import com.smanzana.nostrumaetheria.gui.container.AetherRepairerGui.AetherRepairerGuiContainer;
import com.smanzana.nostrumaetheria.items.ActivePendant;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class NostrumAetheriaGui implements IGuiHandler {

	public static final int aetherFurnaceID = 0;
	public static final int aetherBoilerID = 1;
	public static final int activePendantID = 2;
	public static final int aetherChargerID = 3;
	public static final int aetherRepairerID = 4;
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		
		if (ID == aetherFurnaceID) {
			TileEntity ent = world.getTileEntity(new BlockPos(x, y, z));
			if (ent != null && ent instanceof AetherFurnaceBlockEntity) {
				return new AetherFurnaceGui.AetherFurnaceContainer(
						player.inventory,
						(AetherFurnaceBlockEntity) ent); // should be tile inventory
			}
		}
		
		if (ID == aetherBoilerID) {
			TileEntity ent = world.getTileEntity(new BlockPos(x, y, z));
			if (ent != null && ent instanceof AetherBoilerBlockEntity) {
				return new AetherBoilerGui.AetherBoilerContainer(
						player.inventory,
						(AetherBoilerBlockEntity) ent); // should be tile inventory
			}
		}
		
		if (ID == activePendantID) {
			ItemStack held = player.getHeldItemMainhand();
			if (held == null || !(held.getItem() instanceof ActivePendant)) {
				held = player.getHeldItemOffhand();
				if (held == null || !(held.getItem() instanceof ActivePendant)) {
					NostrumAetheria.logger.error("Was told to open pendant inventory, but no pendant found!");
					return null; // Error!
				}
			}
			
			return new ActivePendantGui.ActivePendantContainer(
					player.inventory,
					held);
		}
		
		if (ID == aetherChargerID) {
			TileEntity ent = world.getTileEntity(new BlockPos(x, y, z));
			if (ent != null && ent instanceof AetherChargerBlockEntity) {
				return new AetherChargerGui.AetherChargerContainer(
						player.inventory,
						(AetherChargerBlockEntity) ent); // should be tile inventory
			}
		}
		
		if (ID == aetherRepairerID) {
			TileEntity ent = world.getTileEntity(new BlockPos(x, y, z));
			if (ent != null && ent instanceof AetherRepairerBlockEntity) {
				return new AetherRepairerGui.AetherRepairerContainer(
						player.inventory,
						(AetherRepairerBlockEntity) ent); // should be tile inventory
			}
		}
		
		return null;
	}

	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		
		if (ID == aetherFurnaceID) {
			TileEntity ent = world.getTileEntity(new BlockPos(x, y, z));
			if (ent != null && ent instanceof AetherFurnaceBlockEntity) {
				return new AetherFurnaceGuiContainer(new AetherFurnaceGui.AetherFurnaceContainer(
						player.inventory,
						(AetherFurnaceBlockEntity) ent)); // should be tile inventory
			}
		}
		
		if (ID == aetherBoilerID) {
			TileEntity ent = world.getTileEntity(new BlockPos(x, y, z));
			if (ent != null && ent instanceof AetherBoilerBlockEntity) {
				return new AetherBoilerGuiContainer(new AetherBoilerGui.AetherBoilerContainer(
						player.inventory,
						(AetherBoilerBlockEntity) ent)); // should be tile inventory
			}
		}
		
		if (ID == activePendantID) {
			ItemStack held = player.getHeldItemMainhand();
			if (held == null || !(held.getItem() instanceof ActivePendant)) {
				held = player.getHeldItemOffhand();
				if (held == null || !(held.getItem() instanceof ActivePendant)) {
					NostrumAetheria.logger.error("Was told to open pendant inventory, but no pendant found!");
					return null; // Error!
				}
			}
			
			return new ActivePendantGui.ActivePendantGuiContainer(new ActivePendantGui.ActivePendantContainer(
					player.inventory,
					held));
		}
		
		if (ID == aetherChargerID) {
			TileEntity ent = world.getTileEntity(new BlockPos(x, y, z));
			if (ent != null && ent instanceof AetherChargerBlockEntity) {
				return new AetherChargerGuiContainer(new AetherChargerGui.AetherChargerContainer(
						player.inventory,
						(AetherChargerBlockEntity) ent)); // should be tile inventory
			}
		}
		
		if (ID == aetherRepairerID) {
			TileEntity ent = world.getTileEntity(new BlockPos(x, y, z));
			if (ent != null && ent instanceof AetherRepairerBlockEntity) {
				return new AetherRepairerGuiContainer(new AetherRepairerGui.AetherRepairerContainer(
						player.inventory,
						(AetherRepairerBlockEntity) ent)); // should be tile inventory
			}
		}
		
		return null;
	}
	
}
