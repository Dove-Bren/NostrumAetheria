package com.smanzana.nostrumaetheria.blocks;

import com.smanzana.nostrumaetheria.blocks.tiles.InfiniteAetherBlockEntity;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class InfineAetherBlock extends BlockContainer {
	
	public static final String ID = "infinite_aether_block";
	
	private static InfineAetherBlock instance = null;
	public static InfineAetherBlock instance() {
		if (instance == null)
			instance = new InfineAetherBlock();
		
		return instance;
	}
	
	public InfineAetherBlock() {
		super(Material.ROCK, MapColor.OBSIDIAN);
		this.setUnlocalizedName(ID);
		this.setHardness(3.0f);
		this.setResistance(10.0f);
		//this.setCreativeTab(NostrumAetheria.creativeTab);
		this.setSoundType(SoundType.STONE);
		this.setHarvestLevel("axe", 0);
	}
	
	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return true;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		
		if (!worldIn.isRemote) {
			// r equest an update
			worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 2);
		}
		
		return false;
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
//				world.spawnEntity(item);
//			}
//		}
		
	}
}
