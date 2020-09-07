package com.smanzana.nostrumaetheria.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.blocks.AetherTileEntity;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.component.AetherHandlerComponent;
import com.smanzana.nostrumaetheria.component.AetherRelayComponent;
import com.smanzana.nostrumaetheria.component.AetherRelayComponent.AetherRelayListener;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
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

public class AetherRelay extends BlockContainer {
	
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
	
	public static void init() {
		GameRegistry.registerTileEntity(AetherRelayEntity.class, "aether_relay_te");
//		GameRegistry.addShapedRecipe(new ItemStack(instance()),
//				"WPW", "WCW", "WWW",
//				'W', new ItemStack(Blocks.PLANKS, 1, OreDictionary.WILDCARD_VALUE),
//				'P', new ItemStack(Items.PAPER, 1, OreDictionary.WILDCARD_VALUE),
//				'C', NostrumResourceItem.getItem(ResourceType.CRYSTAL_LARGE, 1));
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
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, getFacingFromMeta(meta));
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return super.getItemDropped(state, rand, fortune);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).ordinal();
	}
	
	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		IBlockState state = worldIn.getBlockState(pos);
		return state.getValue(FACING) == side;
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
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		return RELAY_AABBs[blockState.getValue(FACING).ordinal()];
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
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

	/**
	 * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
	 * IBlockstate
	 */
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack) {
		return this.getDefaultState().withProperty(FACING, facing);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		
		if (!worldIn.isRemote) {
			// r equest an update
			worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 2);
			
			TileEntity ent = worldIn.getTileEntity(pos);
			if (ent != null && ent instanceof AetherRelayEntity) {
				AetherRelayEntity relay = (AetherRelayEntity) ent;
				System.out.println(relay.relayHandler == null ? "Missing relay handler" : "has relay handler with " + relay.relayHandler.getLinkedPositions().size());
			}
			return true;
		}
		
		return false;
	}
	
	public static class AetherRelayEntity extends AetherTileEntity implements AetherRelayListener {

		private static final String NBT_SIDE = "relay_side";
		
		protected @Nullable AetherRelayComponent relayHandler;
		private EnumFacing side;
		
		public AetherRelayEntity() {
			super(0, 0);
			side = EnumFacing.UP;
		}
		
		public AetherRelayEntity(EnumFacing facing) {
			this();
			
			side = facing;
			this.relayHandler = new AetherRelayComponent(this, facing);
			this.handler = relayHandler;
		}
		
		@Override
		protected AetherHandlerComponent createComponent(int defaultAether, int defaultMaxAether) {
			// I wantd to override this and return a relay component but I can't think of a neasy way to get the side here.
			// I could relax the component to not care about side at first, and have it attached later. instead
			// I'll just throw the old one away?
			relayHandler = new AetherRelayComponent(this, EnumFacing.UP); 
			 return relayHandler;
		}
		
		@Override
		public void validate() {
			super.validate();
			
			if (this.worldObj != null) {
				relayHandler.setPosition(worldObj, pos.toImmutable());
			}
		}
		
		@Override
		public void setWorldObj(World world) {
			super.setWorldObj(world);
			
			if (this.pos != null && !this.pos.equals(BlockPos.ORIGIN)) {
				relayHandler.setPosition(worldObj, pos.toImmutable());
			}
			// if this is too early for side, let's save it :(
			
//			if (!isInvalid()) {
//				if (link == null) {
//					autoLink();
//				} else {
//					// link up with the tile entity
//					repairLink();
//				}
//			}
		}
		
		@Override
		public void onLoad() {
			super.onLoad();
//			if (!worldObj.isRemote) {
//				worldObj.getMinecraftServer().addScheduledTask(() -> {
//					if (worldObj != null && getPairedRelay() == null) {
//						if (link == null) {
//							autoLink();
//						} else {
//							// link up with the tile entity
//							repairLink();
//						}
//					}
//				});
//			}
		}
		
		@Override
		public void updateContainingBlockInfo() {
			super.updateContainingBlockInfo();
			
			if (relayHandler != null && !relayHandler.hasLinks()) {
				relayHandler.autoLink();
			}
		}
		
		@Override
		public void onLinkChange() {
			this.markDirty();
			IBlockState state = worldObj.getBlockState(pos);
			worldObj.notifyBlockUpdate(pos, state, state, 2);
		}
		
		@Override
		public void onChunkUnload() {
			super.onChunkUnload();
			
			// For any linked relays, let them know we're going away (but weren't destroyed)
			relayHandler.unloadRelay();
		}
		
		public EnumFacing getSide() {
			return side;
		}
		
		@Override
		public SPacketUpdateTileEntity getUpdatePacket() {
			return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
		}

		@Override
		public NBTTagCompound getUpdateTag() {
			return this.writeToNBT(new NBTTagCompound());
		}
		
		@Override
		public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
			super.onDataPacket(net, pkt);
			handleUpdateTag(pkt.getNbtCompound());
		}
		
		public NBTTagCompound writeToNBT(NBTTagCompound compound) {
			super.writeToNBT(compound);
			
			compound.setByte(NBT_SIDE, (byte) this.side.ordinal());
			
			return compound;
		}
		
		public void readFromNBT(NBTTagCompound compound) {
			super.readFromNBT(compound);
			
			this.side = EnumFacing.values()[compound.getByte(NBT_SIDE)];
		}
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new AetherRelayEntity(getFacingFromMeta(meta));
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
		TileEntity ent = world.getTileEntity(pos);
		if (ent == null || !(ent instanceof AetherRelayEntity))
			return;
		
		AetherRelayEntity relay = (AetherRelayEntity) ent;
		relay.relayHandler.unlinkAll();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		
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
	
	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
		super.neighborChanged(state, worldIn, pos, blockIn);
		
		if (worldIn.isRemote) {
			return;
		}
		
		EnumFacing facing = state.getValue(FACING);
		if (worldIn.isAirBlock(pos.offset(facing.getOpposite()))) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}
}
