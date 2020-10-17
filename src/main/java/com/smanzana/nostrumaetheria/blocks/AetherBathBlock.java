package com.smanzana.nostrumaetheria.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandlerItem;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandlerProvider;
import com.smanzana.nostrumaetheria.api.item.AetherItem;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
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
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.registry.GameRegistry;
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
	
	public static void init() {
		GameRegistry.registerTileEntity(AetherBathTileEntity.class, "nostrum_aether_altar_te");
	}
	
	public AetherBathBlock() {
		super(Material.ROCK, MapColor.OBSIDIAN);
		this.setUnlocalizedName(ID);
		this.setHardness(3.5f);
		this.setResistance(10.0f);
		this.setCreativeTab(APIProxy.creativeTab);
		this.setSoundType(SoundType.STONE);
		
		this.isBlockContainer = true;
		this.setLightOpacity(1);
		this.setTickRandomly(true);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return ALTAR_AABB;
	}
	
	@Override
	public boolean isVisuallyOpaque() {
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
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
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
//		world.spawnEntityInWorld(item);
		
		TileEntity te = world.getTileEntity(pos);
		if (te != null) {
			AetherBathTileEntity altar = (AetherBathTileEntity) te;
			if (altar.getItem() != null) {
				EntityItem item = new EntityItem(world,
						pos.getX() + .5,
						pos.getY() + .5,
						pos.getZ() + .5,
						altar.getItem());
				world.spawnEntityInWorld(item);
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
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (te == null)
			return false;
		
		AetherBathTileEntity altar = (AetherBathTileEntity) te;
		if (altar.getItem() == null) {
			// Accepting items
			if (heldItem != null && altar.isItemValidForSlot(0, heldItem)) {
				altar.setItem(heldItem.splitStack(1));
				return true;
			} else
				return false;
		} else {
			// Has an item
			if (heldItem == null) {
				if (!playerIn.inventory.addItemStackToInventory(altar.getItem())) {
					worldIn.spawnEntityInWorld(
							new EntityItem(worldIn,
									pos.getX() + .5, pos.getY() + 1.2, pos.getZ() + .5,
									altar.getItem())
							);
				}
				altar.setItem(null);
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
		if (altar.stack != null && !altar.heldItemFull()) {
			worldIn.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, pos.getX() + .5, pos.getY() + 1.5, pos.getZ() + .5,
					2 * rand.nextFloat() - .5f, 0, 2 * rand.nextFloat() - .5f, new int[0]);
		}
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
	}
	
	public static class AetherBathTileEntity extends NativeAetherTickingTileEntity implements ISidedInventory {
		
		private ItemStack stack;
		
		public AetherBathTileEntity(int aether, int maxAether) {
			super(aether, maxAether);
			
			this.setAutoSync(5);
			this.handler.configureInOut(true, false);
		}
		
		public AetherBathTileEntity() {
			this(0, 250);
		}
		
		public ItemStack getItem() {
			return stack;
		}
		
		public void setItem(ItemStack stack) {
			this.stack = stack;
			forceUpdate();
		}
		
		private static final String NBT_ITEM = "item";
		
		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
			nbt = super.writeToNBT(nbt);
			
			if (stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag = stack.writeToNBT(tag);
				nbt.setTag(NBT_ITEM, tag);
			}
			
			return nbt;
		}
		
		@Override
		public void readFromNBT(NBTTagCompound nbt) {
			super.readFromNBT(nbt);
			
			if (nbt == null)
				return;
				
			if (!nbt.hasKey(NBT_ITEM, NBT.TAG_COMPOUND)) {
				stack = null;
			} else {
				NBTTagCompound tag = nbt.getCompoundTag(NBT_ITEM);
				stack = ItemStack.loadItemStackFromNBT(tag);
			}
		}
		
		private void forceUpdate() {
			worldObj.notifyBlockUpdate(pos, this.worldObj.getBlockState(pos), this.worldObj.getBlockState(pos), 3);
			markDirty();
		}

		@Override
		public int getSizeInventory() {
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int index) {
			if (index > 0)
				return null;
			return this.stack;
		}

		@Override
		public ItemStack decrStackSize(int index, int count) {
			if (index > 0)
				return null;
			ItemStack ret = this.stack.splitStack(count);
			if (this.stack.stackSize == 0)
				this.stack = null;
			this.forceUpdate();
			return ret;
		}

		@Override
		public ItemStack removeStackFromSlot(int index) {
			if (index > 0)
				return null;
			ItemStack ret = this.stack;
			this.stack = null;
			forceUpdate();
			return ret;
		}

		@Override
		public void setInventorySlotContents(int index, ItemStack stack) {
			if (index > 0)
				return;
			this.stack = stack;
			forceUpdate();
		}

		@Override
		public int getInventoryStackLimit() {
			return 1;
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer player) {
			return true;
		}

		@Override
		public void openInventory(EntityPlayer player) {
			;
		}

		@Override
		public void closeInventory(EntityPlayer player) {
			;
		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack) {
			if (index != 0) {
				return false;
			}
			
			if (stack == null) {
				return true;
			}
			
			// We specifically want aether handlers
			return (stack.getItem() instanceof IAetherHandler
					|| stack.getItem() instanceof IAetherHandlerProvider
					|| stack.getItem() instanceof IAetherHandlerItem);
		}

		@Override
		public int getField(int id) {
			return 0;
		}

		@Override
		public void setField(int id, int value) {
			
		}

		@Override
		public int getFieldCount() {
			return 0;
		}

		@Override
		public void clear() {
			this.stack = null;
			forceUpdate();
		}

		@Override
		public String getName() {
			return "Aether Bath";
		}

		@Override
		public boolean hasCustomName() {
			return false;
		}

		@Override
		public int[] getSlotsForFace(EnumFacing side) {
			return new int[] {0};
		}

		@Override
		public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
			if (index != 0 || direction == EnumFacing.DOWN || !this.isItemValidForSlot(0, itemStackIn))
				return false;
			
			return stack == null;
		}

		@Override
		public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
			return index == 0 && direction == EnumFacing.DOWN && stack != null && heldItemFull();
		}
		
		public @Nullable IAetherHandler getHeldHandler() {
			if (stack == null) {
				return null;
			}
			
			if (stack.getItem() instanceof IAetherHandler) {
				return (IAetherHandler) stack.getItem();
			}
			
			if (stack.getItem() instanceof IAetherHandlerProvider) {
				return ((IAetherHandlerProvider) stack.getItem()).getHandler();
			}
			
			if (stack.getItem() instanceof IAetherHandlerItem) {
				return ((IAetherHandlerItem) stack.getItem()).getAetherHandler(stack);
			}
			
			// How??
			return null;
		}
		
		public boolean heldItemFull() {
			IAetherHandler handler = getHeldHandler();
			return handler == null || handler.getAether(null) >= handler.getMaxAether(null);
		}
		
		protected int maxAetherPerTick() {
			return 1;
		}
		
		@Override
		public void update() {
			// If we have an item, try to add aether to it
			if (!worldObj.isRemote) {
				
				if (stack != null && stack.getItem() instanceof AetherItem) {
					// Pretty dumb thing I'm doing here for these special items.
					AetherItem aetherItem = (AetherItem) stack.getItem();
					int start = Math.min(maxAetherPerTick(), handler.getAether(null));
					int leftover = aetherItem.addAether(stack, start);
					handler.drawAether(null, start - leftover);
				} else {
					IAetherHandler handler = getHeldHandler();
					if (handler != null) {
						int start = Math.min(maxAetherPerTick(), this.handler.getAether(null));
						int leftover = handler.addAether(null, start);
						this.handler.drawAether(null, start - leftover);
					}
				}
			}
			
			super.update();
		}
		
		@Override
		public void setWorldObj(World world) {
			super.setWorldObj(world);
			
			if (!world.isRemote) {
				this.handler.setAutoFill(true);
			}
		}
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
