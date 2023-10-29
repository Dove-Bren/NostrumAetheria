package com.smanzana.nostrumaetheria.blocks;

import java.util.Random;

import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.component.AetherRelayComponent;
import com.smanzana.nostrumaetheria.tiles.AetherRelayEntity;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AetherRelay extends BlockContainer implements ILoreTagged {
	
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public static final String ID = "aether_relay";
	
	public static final double height = 0.575;
	public static final double width = 0.125;
	private static final double lowWidth = .5 - width;
	private static final double highWidth = .5 + width;
	protected static final AxisAlignedBB RELAY_AABBs[] = new AxisAlignedBB[] {
			new AxisAlignedBB(lowWidth, (1-height), lowWidth, highWidth, 1, highWidth), //down
			new AxisAlignedBB(lowWidth, 0.0D, lowWidth, highWidth, height, highWidth), //up
			new AxisAlignedBB(lowWidth, lowWidth, (1-height), highWidth, highWidth, 1), //north
			new AxisAlignedBB(lowWidth, lowWidth, 0, highWidth, highWidth, height), //south
			new AxisAlignedBB((1-height), lowWidth, lowWidth, 1, highWidth, highWidth), //east
			new AxisAlignedBB(0, lowWidth, lowWidth, height, highWidth, highWidth), //west
	};
	
	
	
	private static AetherRelay instance = null;
	
	public static AetherRelay instance() {
		if (instance == null)
			instance = new AetherRelay();
		
		return instance;
	}
	
	private AetherRelay() {
		super(Material.GLASS, MapColor.DIAMOND);
		this.setUnlocalizedName(ID);
		this.setHardness(0.5f);
		this.setResistance(2.0f);
		this.setCreativeTab(APIProxy.creativeTab);
		this.setSoundType(SoundType.GLASS);
		this.setHarvestLevel("axe", 0);
		this.setTickRandomly(true);
		this.setLightOpacity(0);
		this.setLightLevel(4f/16f);
		this.setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}
	
	private EnumFacing getFacingFromMeta(int meta) {
		return EnumFacing.values()[meta];
	}
	
	@Override
	public BlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, getFacingFromMeta(meta));
	}
	
	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return super.getItemDropped(state, rand, fortune);
	}
	
	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(FACING).ordinal();
	}
	
	@Override
	public boolean isSideSolid(BlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return state.getValue(FACING) == side;
	}
	
//	@Override
//	public boolean isVisuallyOpaque() {
//		return false;
//	}
//	
//	@Override
//	public boolean isFullyOpaque(BlockState state) {
//		return false;
//	}
	
	@Override
	public boolean isFullBlock(BlockState state) {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}
	
	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return RELAY_AABBs[blockState.getValue(FACING).ordinal()];
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
		return RELAY_AABBs[state.getValue(FACING).ordinal()];
	}
	
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		for (EnumFacing enumfacing : FACING.getAllowedValues()) {
			if (this.canPlaceAt(worldIn, pos, enumfacing)) {
				return true;
			}
		}

		return false;
	}

	private boolean canPlaceAt(World worldIn, BlockPos pos, EnumFacing facing) {
		BlockPos blockpos = pos.offset(facing.getOpposite());
		return worldIn.isSideSolid(blockpos, facing, true);// || facing.equals(EnumFacing.UP) && this.canPlaceOn(worldIn, blockpos);
	}

	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, facing);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		
		if (!worldIn.isRemote) {
			// r equest an update
			worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 2);
			
			TileEntity ent = worldIn.getTileEntity(pos);
			if (ent != null && ent instanceof AetherRelayEntity) {
				AetherRelayEntity relay = (AetherRelayEntity) ent;
				System.out.println(relay.getHandler() == null ? "Missing relay handler" : "has relay handler with " + ((AetherRelayComponent)relay.getHandler()).getLinkedPositions().size());
			}
			return true;
		}
		
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new AetherRelayEntity(getFacingFromMeta(meta));
	}
	
	@Override
	public EnumBlockRenderType getRenderType(BlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, BlockState state) {
		destroy(world, pos, state);
		super.breakBlock(world, pos, state);
	}
	
	private void destroy(World world, BlockPos pos, BlockState state) {
		TileEntity ent = world.getTileEntity(pos);
		if (ent == null || !(ent instanceof AetherRelayEntity))
			return;
		
		AetherRelayEntity relay = (AetherRelayEntity) ent;
		((AetherRelayComponent)relay.getHandler()).unlinkAll();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isTranslucent(BlockState state) {
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos from) {
		super.neighborChanged(state, worldIn, pos, blockIn, from);
		
		if (worldIn.isRemote) {
			return;
		}
		
		EnumFacing facing = state.getValue(FACING);
		if (worldIn.isAirBlock(pos.offset(facing.getOpposite()))) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}
	
	@Override
	public String getLoreKey() {
		return "aether_relay";
	}

	@Override
	public String getLoreDisplayName() {
		return "Aether Relays";
	}

	@Override
	public Lore getBasicLore() {
		return new Lore().add("Aether relays allow aether to be pulled from distant blocks.", "Relays automatically link to one another if placed close enough.");
	}

	@Override
	public Lore getDeepLore() {
		return new Lore().add("Aether relays allow aether to be pulled from distant blocks.", "Relays automatically link to one another if placed close enough.", "Relays can link to other relays up to 8 blocks away.");
	}

	@Override
	public InfoScreenTabs getTab() {
		return InfoScreenTabs.INFO_BLOCKS;
	}
}
