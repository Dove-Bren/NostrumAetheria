package com.smanzana.nostrumaetheria.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class InfineAetherBlock extends BlockContainer {
	
	public static final String ID = "infinite_aether_block";
	
	private static InfineAetherBlock instance = null;
	public static InfineAetherBlock instance() {
		if (instance == null)
			instance = new InfineAetherBlock();
		
		return instance;
	}
	
	public static void init() {
		GameRegistry.registerTileEntity(InfiniteAetherBlockEntity.class, "infinite_aether_block_te");
//		GameRegistry.addShapedRecipe(new ItemStack(instance()),
//				"WPW", "WCW", "WWW",
//				'W', new ItemStack(Blocks.PLANKS, 1, OreDictionary.WILDCARD_VALUE),
//				'P', new ItemStack(Items.PAPER, 1, OreDictionary.WILDCARD_VALUE),
//				'C', NostrumResourceItem.getItem(ResourceType.CRYSTAL_LARGE, 1));
	}
	
	// Doing things weird here to allow this to be the creative tab icon
	public final ItemBlock itemBlock;
	
	public InfineAetherBlock() {
		super(Material.ROCK, MapColor.OBSIDIAN);
		this.setUnlocalizedName(ID);
		this.setHardness(3.0f);
		this.setResistance(10.0f);
		//this.setCreativeTab(NostrumAetheria.creativeTab);
		this.setSoundType(SoundType.STONE);
		this.setHarvestLevel("axe", 0);
		
		itemBlock = new ItemBlock(this);
	}
	
	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return true;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		
		if (!worldIn.isRemote) {
			// r equest an update
			worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 2);
		}
		
		return false;
	}
	
	public static class InfiniteAetherBlockEntity extends NativeAetherTickingTileEntity {

		public InfiniteAetherBlockEntity() {
			super(0, 10000);
			this.setAutoSync(5);
			this.handler.configureInOut(false, true);
		}

		@Override
		public void update() {
			if (!worldObj.isRemote) {
				int leftoverGen = this.handler.addAether(null, 10000, true); // 'force' to disable having aether added by others but force ourselves.
				
				this.handler.pushAether(10000);
				// Fix issue where a deficit at the start will never be recouped:
				if (leftoverGen > 0) {
					this.handler.addAether(null, leftoverGen, true);
				}
			}
			super.update();
		}
		
		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
			return super.writeToNBT(nbt);
		}
		
		@Override
		public void readFromNBT(NBTTagCompound nbt) {
			super.readFromNBT(nbt);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new InfiniteAetherBlockEntity();
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		destroy(world, pos, state);
		super.breakBlock(world, pos, state);
	}
	
	private void destroy(World world, BlockPos pos, IBlockState state) {
//		TileEntity ent = world.getTileEntity(pos);
//		if (ent == null || !(ent instanceof AetherBlockEntity))
//			return;
//		
//		AetherBlockEntity table = (AetherBlockEntity) ent;
//		for (int i = 0; i < table.getSizeInventory(); i++) {
//			if (table.getStackInSlot(i) != null) {
//				EntityItem item = new EntityItem(
//						world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5,
//						table.removeStackFromSlot(i));
//				world.spawnEntityInWorld(item);
//			}
//		}
		
	}
}
