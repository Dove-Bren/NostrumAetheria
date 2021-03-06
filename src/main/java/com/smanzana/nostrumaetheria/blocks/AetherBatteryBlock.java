package com.smanzana.nostrumaetheria.blocks;

import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.blocks.tiles.AetherBatteryEntity;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AetherBatteryBlock extends BlockContainer implements ILoreTagged {
	
	public static enum Size {
		SMALL("aether_battery_small", 1000),
		MEDIUM("aether_battery_medium", 3000),
		LARGE("aether_battery_large", 10000),
		GIANT("aether_battery_giant", 50000);
		
		public final String ID;
		public final int capacity;
		
		private Size(String id, int capacity) {
			ID = id;
			this.capacity = capacity;
		}
	}
	
	private static AetherBatteryBlock small = null;
	private static AetherBatteryBlock medium = null;
	private static AetherBatteryBlock large = null;
	private static AetherBatteryBlock giant = null;
	
	public static AetherBatteryBlock small() {
		if (small == null)
			small = new AetherBatteryBlock(Size.SMALL);
		
		return small;
	}
	
	public static AetherBatteryBlock medium() {
		if (medium == null)
			medium = new AetherBatteryBlock(Size.MEDIUM);
		
		return medium;
	}
	
	public static AetherBatteryBlock large() {
		if (large == null)
			large = new AetherBatteryBlock(Size.LARGE);
		
		return large;
	}
	
	public static AetherBatteryBlock giant() {
		if (giant == null)
			giant = new AetherBatteryBlock(Size.GIANT);
		
		return giant;
	}
	
	private final Size size;
	
	private AetherBatteryBlock(Size size) {
		super(Material.ROCK, MapColor.OBSIDIAN);
		this.size = size;
		this.setUnlocalizedName(size.ID);
		this.setHardness(3.0f);
		this.setResistance(10.0f);
		this.setCreativeTab(APIProxy.creativeTab);
		this.setSoundType(SoundType.GLASS);
		this.setHarvestLevel("axe", 0);
	}
	
	public String getID() {
		return size.ID;
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
		return new AetherBatteryEntity(size);
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isTranslucent(IBlockState state) {
		return true;
	}
	
//	@Override
//	public boolean isFullyOpaque(IBlockState state) {
//		return false;
//	}
	
	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
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

	@Override
	public String getLoreKey() {
		return "AetherBattery";
	}

	@Override
	public String getLoreDisplayName() {
		return "Aether Batteries";
	}

	@Override
	public Lore getBasicLore() {
		return new Lore().add("Aether batteries store aether, allowing you to build up a bunch for big costs.");
	}

	@Override
	public Lore getDeepLore() {
		return new Lore().add("Aether batteries store aether, allowing you to build up a bunch for big costs.", "There are four levels of battery. Batteries automatically flow into eachother, prefering to flow down when possible.");
	}

	@Override
	public InfoScreenTabs getTab() {
		return InfoScreenTabs.INFO_BLOCKS;
	}
	
	public int getMaxAether() {
		return size.capacity;
	}
}
