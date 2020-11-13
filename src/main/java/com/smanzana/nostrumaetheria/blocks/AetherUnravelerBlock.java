package com.smanzana.nostrumaetheria.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.gui.NostrumAetheriaGui;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.items.SpellRune;
import com.smanzana.nostrummagica.items.SpellScroll;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;
import com.smanzana.nostrummagica.spells.EAlteration;
import com.smanzana.nostrummagica.spells.EMagicElement;
import com.smanzana.nostrummagica.spells.Spell;
import com.smanzana.nostrummagica.spells.Spell.SpellPart;
import com.smanzana.nostrummagica.spells.components.SpellShape;
import com.smanzana.nostrummagica.spells.components.SpellTrigger;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AetherUnravelerBlock extends BlockContainer implements ILoreTagged {
	
	public static final PropertyBool ON = PropertyBool.create("on");
	public static final String ID = "aether_unraveler";
	
	
	private static AetherUnravelerBlock instance = null;
	public static AetherUnravelerBlock instance() {
		if (instance == null)
			instance = new AetherUnravelerBlock();
		
		return instance;
	}
	
	public static void init() {
		GameRegistry.registerTileEntity(AetherUnravelerBlockEntity.class, "aether_unraveler_te");
	}
	
	public AetherUnravelerBlock() {
		super(Material.ROCK, MapColor.OBSIDIAN);
		this.setUnlocalizedName(ID);
		this.setHardness(3.0f);
		this.setResistance(10.0f);
		this.setCreativeTab(APIProxy.creativeTab);
		this.setSoundType(SoundType.STONE);
		this.setHarvestLevel("pickaxe", 0);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, ON);
	}
	
	private static boolean onFromMeta(int meta) {
		return (meta & 1) == 1;
	}
	
	private static int metaFromOn(boolean on) {
		return (on ? 1 : 0);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState()
				.withProperty(ON, onFromMeta(meta));
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return metaFromOn(state.getValue(ON));
	}
	
	public boolean getFurnaceOn(IBlockState state) {
		return state.getValue(ON);
	}
	
	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return true;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			playerIn.openGui(NostrumAetheria.instance, NostrumAetheriaGui.aetherUnravelerID, worldIn, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
		
		return true;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new AetherUnravelerBlockEntity();
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack) {
		return this.getDefaultState()
				.withProperty(ON, false);
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
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		destroy(world, pos, state);
		super.breakBlock(world, pos, state);
	}
	
	private void destroy(World world, BlockPos pos, IBlockState state) {
		TileEntity ent = world.getTileEntity(pos);
		if (ent == null || !(ent instanceof AetherUnravelerBlockEntity))
			return;
		
		AetherUnravelerBlockEntity furnace = (AetherUnravelerBlockEntity) ent;
		for (int i = 0; i < furnace.getSizeInventory(); i++) {
			if (furnace.getStackInSlot(i) != null) {
				EntityItem item = new EntityItem(
						world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5,
						furnace.removeStackFromSlot(i));
				world.spawnEntityInWorld(item);
			}
		}
		
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (null == stateIn || !stateIn.getValue(ON))
			return;
		
		double d0 = (double)pos.getX() + 0.5D;
		double d1 = (double)pos.getY() + 1.2D;
		double d2 = (double)pos.getZ() + 0.5D;
		
		worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
		worldIn.spawnParticle(EnumParticleTypes.CRIT_MAGIC, d0, d1, d2, (rand.nextFloat() - .5) * .2, .75, (rand.nextFloat() - .5) * .2, new int[0]);
		
		if (rand.nextFloat() < .1f) {
			worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, 1.0F, 0.25F, false);
			worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_LADDER_STEP, SoundCategory.BLOCKS, 0.1F, 0.25F, false);
		}
	}
	
	@Override
	public String getLoreKey() {
		return "aether_unraveler";
	}

	@Override
	public String getLoreDisplayName() {
		return "Aether Unraveling";
	}

	@Override
	public Lore getBasicLore() {
		return new Lore().add("This block uses aether to break down magical items into their base components.");
	}

	@Override
	public Lore getDeepLore() {
		return new Lore().add("This block uses aether to break down magical items into their base components.", "This allows it to take spell scrolls and return some of the runes that were used to craft it!");
	}

	@Override
	public InfoScreenTabs getTab() {
		return InfoScreenTabs.INFO_BLOCKS;
	}
	
	public static class AetherUnravelerBlockEntity extends NativeAetherTickingTileEntity implements ISidedInventory {
		
		private static int GetAetherCost(ItemStack stack) {
			return 2000; // If more items get added, adjust based on item
		}
		
		private static int GetMaxTicks(ItemStack stack) {
			return 20 * 100; // If more items get added, consider making them take different times
			// Note ideally a whole divisor of aether cost, since I'm being lazy and not adding
			// real support for fractions
		}
		
		private boolean on;
		private boolean aetherTick;
		
		private ItemStack stack;
		private int workTicks;
		
		public AetherUnravelerBlockEntity(int aether, int maxAether) {
			super(aether, maxAether);
			
			this.setAutoSync(5);
			this.handler.configureInOut(true, false);
		}
		
		public AetherUnravelerBlockEntity() {
			this(0, 500);
		}
		
		public ItemStack getItem() {
			return stack;
		}
		
		public void setItem(ItemStack stack) {
			this.stack = stack;
			forceUpdate();
		}
		
		private static final String NBT_ITEM = "item";
		private static final String NBT_WORK_TICKS = "work_ticks";
		
		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
			nbt = super.writeToNBT(nbt);
			
			if (stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag = stack.writeToNBT(tag);
				nbt.setTag(NBT_ITEM, tag);
			}
			
			if (workTicks > 0) {
				nbt.setInteger(NBT_WORK_TICKS, workTicks);
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
			
			workTicks = nbt.getInteger(NBT_WORK_TICKS); // defaults 0 :)
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
			
			if (!(stack.getItem() instanceof SpellScroll)) {
				return false;
			}
			
			if (stack.getItemDamage() > 0) {
				return false;
			}
			
			Spell spell = SpellScroll.getSpell(stack);
			if (spell == null) {
				return false;
			}
			
			return true;
		}

		@Override
		public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
			return !(oldState.getBlock().equals(newState.getBlock()));
		}
		
		@Override
		public int getField(int id) {
			if (id == 0) {
				return this.handler.getAether(null);
			} else if (id == 1) {
				if (stack == null) {
					return 0;
				}
				
				return (int) Math.round(((float) this.workTicks * 100f) / (float) GetMaxTicks(stack));
			}
			return 0;
		}

		@Override
		public void setField(int id, int value) {
			if (id == 0) {
				this.handler.setAether(value);
			} else if (id == 1) {
				if (value == 0 || stack == null) {
					workTicks = 0;
				} else {
					this.workTicks = (int) Math.round(((float) value * (float) GetMaxTicks(stack)) / 100);
				}
			}
		}

		@Override
		public int getFieldCount() {
			return 2;
		}
		
		@Override
		public void onAetherFlowTick(int diff, boolean added, boolean taken) {
			super.onAetherFlowTick(diff, added, taken);
			aetherTick = (stack != null);
		}
		
		@Override
		public void clear() {
			this.stack = null;
			forceUpdate();
		}

		@Override
		public String getName() {
			return "Aether Unraveler";
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
			if (index != 0 || !this.isItemValidForSlot(0, itemStackIn))
				return false;
			
			return stack == null;
		}

		@Override
		public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
			return false;
		}
		
		protected void processItem(ItemStack stack) {
			// If more items are added, add processing here!
			
			// Spell scroll
			{
				Spell spell = SpellScroll.getSpell(stack);
				if (spell == null) {
					NostrumAetheria.logger.error("Tried to process spell scroll in unraveler but found no spell");
					return;
				}
				
				for (SpellPart part : spell.getSpellParts()) {
					final ItemStack[] runes;
					if (part.isTrigger()) {
						SpellTrigger trigger = part.getTrigger();
						runes = new ItemStack[] {SpellRune.getRune(trigger)};
					} else {
						SpellShape shape = part.getShape();
						EMagicElement elem = part.getElement();
						int elemCount = part.getElementCount();
						EAlteration alt = part.getAlteration();
						runes = new ItemStack[1 + elemCount + (alt == null ? 0 : 1)];
						runes[0] = SpellRune.getRune(shape);
						if (alt != null) {
							runes[1] = SpellRune.getRune(alt);
						}
						for (; elemCount > 0; elemCount--) {
							runes[1 + (alt == null ? 0 : 1) + (elemCount - 1)]
									= SpellRune.getRune(elem, 1);
						}
					}
					
					for (ItemStack rune : runes) {
						EntityItem item = new EntityItem(worldObj, pos.getX() + .5, pos.getY() + 1.2, pos.getZ() + .5, rune);
						worldObj.spawnEntityInWorld(item);
					}
				}
			}
			
			// Effects!
			double x = pos.getX() + .5;
			double y = pos.getY() + 1.2;
			double z = pos.getZ() + .5;
			((WorldServer) worldObj).spawnParticle(EnumParticleTypes.CRIT_MAGIC,
					x,
					y,
					z,
					15,
					.25,
					.6,
					.25,
					.1,
					new int[0]);
			//worldObj.playSound(null, pos, SoundEvents.BLOCK_ANVIL_USE, null
			worldObj.playSound(null, pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1f, 1f);
		}
		
		@Override
		public void update() {
			// If we have an item, work and break it down
			if (!worldObj.isRemote) {
				if (stack != null) {
					boolean worked = true;
					final int aetherPerTick = GetAetherCost(stack) / GetMaxTicks(stack);
					final int drawn = handler.drawAether(null, aetherPerTick);
					if (drawn == 0) {
						worked = false;
					} else if (drawn < aetherPerTick) {
						// Not enough for a full tick. Use probability instead!
						final float chance = (float) drawn / (float) aetherPerTick;
						worked = NostrumAetheria.random.nextFloat() < chance;
					} else {
						worked = true;
					}
					
					if (worked) {
						// Had and took aether. Advance
						workTicks++;
						if (workTicks > GetMaxTicks(stack)) {
							workTicks = 0;
							processItem(stack);
							stack.stackSize--;
							if (stack.stackSize <= 0) {
								this.setItem(null); // dirties and updates
							} else {
								this.forceUpdate();
							}
						}
					} else {
						// Wanted aether but didn't get it. Lose progress
						workTicks = Math.max(0, workTicks - 1);
					}
					
					aetherTick = (drawn > 0); // worked; // Always appear lit up even if not enough aether to really go?
				} else {
					// Reset progress, if any
					final boolean hadProgress = workTicks != 0;
					workTicks = 0;
					aetherTick = false;
					if (hadProgress) {
						this.markDirty();
					}
				}
				
				if (aetherTick != on) {
					worldObj.setBlockState(pos, instance().getDefaultState().withProperty(ON, aetherTick));
				}
				
				on = aetherTick;
				aetherTick = false;
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
}
