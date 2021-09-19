package com.smanzana.nostrumaetheria.blocks;

import java.util.Random;

import javax.annotation.Nonnull;

import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.blocks.tiles.AetherBathTileEntity;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AetherBathBlock extends Block implements ITileEntityProvider, ILoreTagged {
	
	public static final String ID = "aether_bath";
	protected static final AxisAlignedBB ALTAR_AABB = new AxisAlignedBB(0.2D, 0.0D, 0.2D, 0.8D, 0.9D, 0.8D);
	
	private static AetherBathBlock instance = null;
	public static AetherBathBlock instance() {
		if (instance == null)
			instance = new AetherBathBlock();
		
		return instance;
	}
	
	public AetherBathBlock() {
		super(Material.ROCK, MapColor.OBSIDIAN);
		this.setUnlocalizedName(ID);
		this.setHardness(3.5f);
		this.setResistance(10.0f);
		this.setCreativeTab(APIProxy.creativeTab);
		this.setSoundType(SoundType.STONE);
		
		this.hasTileEntity = true;
		this.setLightOpacity(1);
		this.setTickRandomly(true);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return ALTAR_AABB;
	}
	
//	@Override
//	public boolean isVisuallyOpaque() {
//		return false;
//	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
        return false;
    }
	
	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return true;
	}
	
	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		AetherBathTileEntity ent = new AetherBathTileEntity();
		
		return ent;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
//		EntityItem item = new EntityItem(world,
//				pos.getX() + .5,
//				pos.getY() + .5,
//				pos.getZ() + .5,
//				new ItemStack(AltarItem.instance()));
//		world.spawnEntity(item);
		
		TileEntity te = world.getTileEntity(pos);
		if (te != null) {
			AetherBathTileEntity altar = (AetherBathTileEntity) te;
			if (!altar.getItem().isEmpty()) {
				EntityItem item = new EntityItem(world,
						pos.getX() + .5,
						pos.getY() + .5,
						pos.getZ() + .5,
						altar.getItem());
				world.spawnEntity(item);
			}
		}
		
        world.removeTileEntity(pos);
		super.breakBlock(world, pos, state);
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return super.getItemDropped(state, rand, fortune);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int eventID, int eventParam) {
		super.eventReceived(state, worldIn, pos, eventID, eventParam);
		TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(eventID, eventParam);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) {
			return true;
		}
		
		TileEntity te = worldIn.getTileEntity(pos);
		if (te == null)
			return false;
		
		final @Nonnull ItemStack heldItem = playerIn.getHeldItem(hand);
		
		AetherBathTileEntity altar = (AetherBathTileEntity) te;
		if (altar.getItem().isEmpty()) {
			// Accepting items
			if (!heldItem.isEmpty() && altar.isItemValidForSlot(0, heldItem)) {
				altar.setItem(heldItem.splitStack(1));
				return true;
			} else
				return false;
		} else {
			// Has an item
			if (heldItem.isEmpty()) {
				if (!playerIn.inventory.addItemStackToInventory(altar.getItem())) {
					worldIn.spawnEntity(
							new EntityItem(worldIn,
									pos.getX() + .5, pos.getY() + 1.2, pos.getZ() + .5,
									altar.getItem())
							);
				}
				altar.setItem(ItemStack.EMPTY);
				return true;
			} else
				return false;
		}
		
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		super.randomDisplayTick(stateIn, worldIn, pos, rand);
		
		TileEntity te = worldIn.getTileEntity(pos);
		if (te == null) {
			return;
		}
		AetherBathTileEntity altar = (AetherBathTileEntity) te;
		if (!altar.getItem().isEmpty() && !altar.heldItemFull()) {
			worldIn.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, pos.getX() + .5, pos.getY() + 1.5, pos.getZ() + .5,
					2 * rand.nextFloat() - .5f, 0, 2 * rand.nextFloat() - .5f, new int[0]);
		}
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
	}
	
	@Override
	public String getLoreKey() {
		return "aether_bath";
	}

	@Override
	public String getLoreDisplayName() {
		return "Aether Bath";
	}

	@Override
	public Lore getBasicLore() {
		return new Lore().add("Fashioned from an altar and a large stone bowl, the aether bath aesthetically displays all of your aether.");
	}

	@Override
	public Lore getDeepLore() {
		return new Lore().add("Fashioned from an altar and a large stone bowl, the aether bath aesthetically displays all of your aether.", "Aether baths fill up items with aether slowly.");
	}

	@Override
	public InfoScreenTabs getTab() {
		return InfoScreenTabs.INFO_BLOCKS;
	}
}
