package com.smanzana.nostrumaetheria.blocks;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.blocks.AetherTileEntity;
import com.smanzana.nostrumaetheria.api.blocks.IAetherCapableBlock;

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
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
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
		this.setCreativeTab(NostrumAetheria.creativeTab);
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
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.values()[meta]);
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
				System.out.println(relay.missingLinks.isEmpty() ? ("Fully linked") : (relay.linkCache.isEmpty() ? "Fully UNLINKED" : "Mixed link status"));
				System.out.println(relay.links.size() + " " + relay.links.toString());
			}
			return true;
		}
		
		return false;
	}
	
	public static class AetherRelayEntity extends AetherTileEntity {

		private static final String NBT_LINK = "relay_links";
		
		protected final int range;
		protected EnumFacing side;
		
		private Set<BlockPos> links;
		
		// Transient list of relays for easy cleanup
		private List<AetherRelayEntity> linkCache;
		
		// transient list of the links we haven't fixed up yet
		private List<BlockPos> missingLinks;
		
		public AetherRelayEntity(int range) {
			super(0, 0);
			this.range = range;
			this.links = new HashSet<>();
			this.linkCache = new LinkedList<>();
			this.missingLinks = new LinkedList<>();
		}
		
		public AetherRelayEntity() {
			this(8);
		}
		
		@Override
		public void validate() {
			super.validate();
		}
		
		@Override
		public void setWorldObj(World world) {
			super.setWorldObj(world);
			
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
		
		protected void refreshSideInfo() {
			// Configure connections to only allow the block we're actually attached to
			for (EnumFacing side : EnumFacing.values()) { // Note: leaves out null, which we use for relay connections
				this.enableSide(side, false);
			}
			IBlockState state = this.worldObj.getBlockState(pos);
			side = state.getValue(FACING);
			this.enableSide(side.getOpposite(), true);
		}
		
		@Override
		public void updateContainingBlockInfo() {
			super.updateContainingBlockInfo();
			refreshSideInfo();
			
			if (links.isEmpty()) {
				autoLink();
			}
		}
		
		protected void dirty() {
			this.markDirty();
			IBlockState state = worldObj.getBlockState(pos);
			worldObj.notifyBlockUpdate(pos, state, state, 2);
		}
		
		protected void repairLinks() {
			if (!missingLinks.isEmpty()) {
				Iterator<BlockPos> it = missingLinks.iterator();
				while (it.hasNext()) {
					BlockPos pos = it.next();
					
					// Try to load it up
					if (!worldObj.isBlockLoaded(pos)) {
						continue;
					}
					
					TileEntity ent = worldObj.getTileEntity(pos);
					if (ent != null && ent instanceof AetherRelayEntity) {
						AetherRelayEntity relay = (AetherRelayEntity) ent;
						this.linkCache.add(relay);
						this.addAetherConnection(relay, null);
						
					} else {
						this.links.remove(pos);
					}
					
					// Either way, the block was loaded. Either we re-added the link or removed it from mlink list.
					// It can come out of trhis 'uninitialized' missing link list
					it.remove();
				}
			}
		}
		
		/**
		 * The provided relay has indicated that it's being unloaded and will require linking again once the chunk is loaded again
		 * @param relay
		 */
		protected void addUnloadedLink(AetherRelayEntity relay) {
			if (linkCache.remove(relay)) {
				missingLinks.add(relay.pos);
				this.removeAetherConnection(relay, null);
			}
		}
		
		@Override
		public void onChunkUnload() {
			super.onChunkUnload();
			
			// For any linked relays, let them know we're going away (but weren't destroyed)
			for (AetherRelayEntity relay : linkCache) {
				relay.addUnloadedLink(this);
			}
			linkCache.clear();
			missingLinks.clear();
			missingLinks.addAll(links);
		}
		
		/**
		 * Add this relay to our linked relays.
		 * @param relay
		 */
		public void link(AetherRelayEntity relay) {
			repairLinks();
			if (links.add(relay.getPos().toImmutable())) {
				this.linkCache.add(relay);
				this.addAetherConnection(relay, null);
				
				// Add ourselves as link on their side, too
				relay.link(this);
				this.dirty();
			}
		}
		
		/**
		 * Permanently remove the relay from our list of linked relays.
		 * @param relay
		 */
		public void unlink(AetherRelayEntity relay) {
			if (links.remove(relay.getPos().toImmutable())) {
				linkCache.remove(relay);
				missingLinks.remove(relay);
				this.removeAetherConnection(relay, null);
				
				// Dissolve their link to us if not already done
				relay.unlink(this);
				this.dirty();
			}
		}
		
//		public @Nullable AetherRelayEntity getPairedRelay() {
//			// Hack in some stupid initialization here to avoid making this a ticking entity for only init tick
//			if (!worldObj.isRemote && incomingLinks == null) { // stupid init flag
//				incomingLinks = new LinkedList<>();
//				
//				if (link == null) {
//					autoLink();
//				} else {
//					// link up with the tile entity
//					repairLink();
//				}
//			}
//			
//			if (link != null && worldObj.isBlockLoaded(link)) {
//				TileEntity ent = worldObj.getTileEntity(link);
//				if (ent != null && ent instanceof AetherRelayEntity) {
//					return (AetherRelayEntity) ent;
//				}
//			}
//			return null;
//		}
		
		public Collection<BlockPos> getLinkedPositions() {
			repairLinks();
			return links;
		}
		
		public EnumFacing getSide() {
			if (side == null) {
				refreshSideInfo();
			}
			return side;
		}
		
//		protected void link(AetherRelayEntity other) {
//			AetherRelayEntity current = getPairedRelay();
//			if (current != other) {
//				unlink();
//				
//				if (other != null) {
//					link = other.pos.toImmutable(); // getOther() will now return 'other'
//					
//					if (other.link == null || !other.link.equals(pos)) {
//						// we don't have to be the exclusive connection of the other.
//						if (other.link == null) {
//							other.link(this);
//						} else {
//							other.addAetherConnection(this, null);
//							other.addWeakLink(this);
//						}
//					}
//					
//					this.dirty();
//					this.addAetherConnection(other, null);
//				}
//			}
//		}
//		
//		protected void unlink() {
//			if (link != null) {
//				AetherRelayEntity other = getPairedRelay();
//				link = null;
//				if (other != null) {
//					other.removeAetherConnection(this, null);
//					if (other.link != null && other.link.equals(this.pos)) {
//						other.unlink();
//					} else {
//						other.removeWeakLink(this);
//					}
//				}
//				this.dirty();
//			}
//		}
//		
//		protected void addWeakLink(AetherRelayEntity relay) {
//			getPairedRelay();
//			incomingLinks.add(relay);
//		}
//		
//		protected void removeWeakLink(AetherRelayEntity relay) {
//			getPairedRelay();
//			incomingLinks.remove(relay);
//		}
		
		protected void unlinkAll() {
			repairLinks();
			for (AetherRelayEntity relay : Lists.newArrayList(linkCache)) {
				// For any we were able to actually find and link to, let them know we're going away
				unlink(relay);
			}
			
			// Any links that aren't loaded yet in our missingLinks list will eventually notice we're gone when they're
			// back and loaded.
			
			links.clear();
			missingLinks.clear();
			linkCache.clear();
		}
		
		protected void autoLink() {
			MutableBlockPos cursor = new MutableBlockPos();
			
			for (int i = -range; i <= range; i++) {
				int innerRadius = range - Math.abs(i);
				for (int j = -innerRadius; j <= innerRadius; j++) {
					int yRadius = innerRadius - Math.abs(j);
					for (int k = -yRadius; k <= yRadius; k++) {
						cursor.setPos(pos.getX() + i, pos.getY() + j, pos.getZ() + k);
						if (!worldObj.isBlockLoaded(cursor)) {
							continue;
						}
						if (cursor.equals(pos)) {
							continue;
						}
						
						TileEntity te = worldObj.getTileEntity(cursor);
						if (te != null && te != this && te instanceof AetherRelayEntity) {
							this.link((AetherRelayEntity) te);
						}
					}
				}
				
			}
		}
		
		/**
		 * Return the attached aether handler. That is, the aether handler corresponding to the block we're affixed to.
		 */
		protected @Nullable IAetherHandler getAttached() {
			if (side == null) {
				refreshSideInfo();
			}
			BlockPos pos = this.pos.offset(side.getOpposite());
			
			// First check for a TileEntity
			TileEntity te = worldObj.getTileEntity(pos);
			if (te != null && te instanceof IAetherHandler) {
				return (IAetherHandler) te;
			}
			
			// See if block boasts being able to get us a handler
			IBlockState attachedState = worldObj.getBlockState(pos);
			Block attachedBlock = attachedState.getBlock();
			if (attachedBlock instanceof IAetherCapableBlock) {
				return ((IAetherCapableBlock) attachedBlock).getAetherHandler(worldObj, attachedState, pos, side);
			}
			
			return null;
		}
		
		/**
		 * Paired relay is checking whether we can accept aether.
		 * Check if our attached block can accept it.
		 * @param amount
		 * @return
		 */
		protected boolean canForward(int amount, Set<AetherRelayEntity> visitted) {
			if (visitted == null) {
				visitted = new HashSet<>(); // first-time entry, so just make it and recurse.
			} else {
				if (visitted.contains(this)) {
					return false;
				}
				
				IAetherHandler handler = getAttached();
				if (handler != null) {
					if (handler.canAdd(side, amount)) {
						visitted.add(this); // for good measure
						return true;
					}
				}
			}
			visitted.add(this);
			
			repairLinks();
			
			// Try linked relay
			for (AetherRelayEntity relay : linkCache) {
				if (relay.canForward(amount, visitted)) {
					return true;
				}
			}
				
			return false;
		}
		
		protected int forwardAether(int amount, Set<AetherRelayEntity> visitted) {
			if (visitted == null) {
				visitted = new HashSet<>(); // first-time entry, so just make it and recurse.
			} else {
				if (visitted.contains(this)) {
					return amount;
				}
				
				IAetherHandler handler = getAttached();
				if (handler != null) {
					amount = handler.addAether(side, amount);
				}
			}
			
			visitted.add(this);
			repairLinks();
			
			if (amount != 0) {
				for (AetherRelayEntity relay : linkCache) {
					amount = relay.forwardAether(amount, visitted);
					if (amount <= 0) {
						break;
					}
				}
			}
			
			return amount;
		}
		
		@Override
		public boolean canAdd(EnumFacing side, int amount) {
			if (canAcceptOnSide(side)) {
				return canForward(amount, null);
			}
			
			return false;
		}
		
		@Override
		public int addAether(EnumFacing side, int amount) {
			// We don't store aether and try to push it instead
			if (canAcceptOnSide(side)) {
				return forwardAether(amount, null);
			}
			
			return amount;
		}
		
		@Override
		protected List<AetherFlowConnection> getConnections() {
			repairLinks();
			return super.getConnections();
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
		
		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
			nbt = super.writeToNBT(nbt);
			
			NBTTagList list = new NBTTagList();
			for (BlockPos link : links) {
				list.appendTag(new NBTTagLong(link.toLong()));
			}
			nbt.setTag(NBT_LINK, list);
			
			return nbt;
		}
		
		@Override
		public void readFromNBT(NBTTagCompound nbt) {
			super.readFromNBT(nbt);
			
			links.clear();
			linkCache.clear();
			missingLinks.clear();
			NBTTagList list = nbt.getTagList(NBT_LINK, NBT.TAG_LONG);
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagLong lTag = (NBTTagLong) list.get(i);
				BlockPos pos = BlockPos.fromLong(lTag.getLong());
				if (links.add(pos)) { // prevents dupes :)
					missingLinks.add(pos);
				}
			}
		}

	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new AetherRelayEntity();
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
		relay.unlinkAll();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
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
