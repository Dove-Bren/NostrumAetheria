package com.smanzana.nostrumaetheria.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.blocks.AetherTickingTileEntity;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AetherPumpBlock extends BlockContainer implements ILoreTagged {
	
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public static final String ID = "aether_pump";
	
	private static final double BBWIDTH = 1.0/4.0;
	protected static final AxisAlignedBB PUMP_AABBS[] = new AxisAlignedBB[] {
			new AxisAlignedBB(.5 - BBWIDTH, 0, .5 - BBWIDTH, .5 + BBWIDTH, 1, .5 + BBWIDTH), //down
			new AxisAlignedBB(.5 - BBWIDTH, 0, .5 - BBWIDTH, .5 + BBWIDTH, 1, .5 + BBWIDTH), //up
			new AxisAlignedBB(.5 - BBWIDTH, .5 - BBWIDTH, 0, .5 + BBWIDTH, .5 + BBWIDTH, 1), //north
			new AxisAlignedBB(.5 - BBWIDTH, .5 - BBWIDTH, 0, .5 + BBWIDTH, .5 + BBWIDTH, 1), //south
			new AxisAlignedBB(0, .5 - BBWIDTH, .5 - BBWIDTH, 1, .5 + BBWIDTH, .5 + BBWIDTH), //east
			new AxisAlignedBB(0, .5 - BBWIDTH, .5 - BBWIDTH, 1, .5 + BBWIDTH, .5 + BBWIDTH), //west
	};
	
	private static AetherPumpBlock instance = null;
	public static AetherPumpBlock instance() {
		if (instance == null)
			instance = new AetherPumpBlock();
		
		return instance;
	}
	
	public static void init() {
		GameRegistry.registerTileEntity(AetherPumpBlockEntity.class, "aether_pump_te");
	}
	
	public AetherPumpBlock() {
		super(Material.ROCK, MapColor.OBSIDIAN);
		this.setUnlocalizedName(ID);
		this.setHardness(1.0f);
		this.setResistance(10.0f);
		this.setCreativeTab(APIProxy.creativeTab);
		this.setSoundType(SoundType.STONE);
		this.setHarvestLevel("pickaxe", 0);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}
	
	private static EnumFacing facingFromMeta(int meta) {
		return EnumFacing.VALUES[meta % EnumFacing.VALUES.length];
	}
	
	private static int metaFromFacing(EnumFacing facing) {
		return facing.ordinal();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState()
				.withProperty(FACING, facingFromMeta(meta));
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return metaFromFacing(state.getValue(FACING));
	}
	
	public EnumFacing getFacing(IBlockState state) {
		return state.getValue(FACING);
	}
	
	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return true;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
//		if (!worldIn.isRemote) {
//			playerIn.openGui(NostrumAetheria.instance, NostrumAetheriaGui.aetherChargerID, worldIn, pos.getX(), pos.getY(), pos.getZ());
//			return true;
//		}
		
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new AetherPumpBlockEntity();
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack) {
		return this.getDefaultState().withProperty(FACING, BlockPistonBase.getFacingFromEntity(pos, placer));
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		super.getSubBlocks(itemIn, tab, list);
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public boolean isVisuallyOpaque() {
		return false;
	}
	
	@Override
	public boolean isFullyOpaque(IBlockState state) {
		return false;
	}
	
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
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isTranslucent(IBlockState state) {
		return true;
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		return PUMP_AABBS[blockState.getValue(FACING).ordinal()];
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return PUMP_AABBS[state.getValue(FACING).ordinal()];
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		world.removeTileEntity(pos);
		super.breakBlock(world, pos, state);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		super.randomDisplayTick(stateIn, worldIn, pos, rand);
	}
	
	@Override
	public String getLoreKey() {
		return "aether_pump";
	}

	@Override
	public String getLoreDisplayName() {
		return "Aether Pump";
	}

	@Override
	public Lore getBasicLore() {
		return new Lore().add("Aether Pumps pull aether out of one block and put it in another.");
	}

	@Override
	public Lore getDeepLore() {	
		return new Lore().add("Aether Pumps pull aether out of one block and put it in another.", "The larger end of the pump is where it pulls from.");
	}

	@Override
	public InfoScreenTabs getTab() {
		return InfoScreenTabs.INFO_BLOCKS;
	}
	
	public static class AetherPumpBlockEntity extends AetherTickingTileEntity {
		
		public AetherPumpBlockEntity() {
			super(0, 500);
		}
		
		@Override
		public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
			return !(oldState.getBlock().equals(newState.getBlock()));
		}
		
		@Override
		public void onAetherFlowTick(int diff, boolean added, boolean taken) {
			super.onAetherFlowTick(diff, added, taken);
		}
		
		@Override
		public void update() {
			super.update();
			
			if (!worldObj.isRemote) {
				IBlockState state = worldObj.getBlockState(pos);
				if (!(state.getBlock() instanceof AetherPumpBlock)) {
					worldObj.removeTileEntity(pos);
					return;
				}
				
				final EnumFacing direction = ((AetherPumpBlock) state.getBlock()).getFacing(state);
				
				// Pull
				// Note: carts handled by the cart
				{
					final int maxAether = this.getHandler().getMaxAether(null);
					final int room = maxAether - this.getHandler().getAether(null);
					// Attempt to draw from a handler we're pointed at
					@Nullable IAetherHandler handler = IAetherHandler.GetHandlerAt(worldObj, pos.offset(direction), direction.getOpposite());
					if (handler != null) {
						final int drawn = handler.drawAether(direction.getOpposite(), room);
						this.getHandler().addAether(null, drawn);
						this.markDirty();
					}
				}
				
				// Push
				if (this.getHandler().getAether(null) > 0) {
					// Look up handler at pointed position
					@Nullable IAetherHandler handler = IAetherHandler.GetHandlerAt(worldObj, pos.offset(direction.getOpposite()), direction);
					if (handler != null && handler.canAdd(direction, 1)) {
						final int orig = this.getHandler().getAether(null);
						final int leftover = handler.addAether(direction, orig);
						if (leftover != orig) {
							// Pushed some out
							this.getHandler().drawAether(null, orig - leftover);
							this.markDirty();
						}
					}
				}
			}
		}

	}
}
