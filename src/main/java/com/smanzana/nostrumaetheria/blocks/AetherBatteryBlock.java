package com.smanzana.nostrumaetheria.blocks;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.blocks.AetherTickingTileEntity;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AetherBatteryBlock extends BlockContainer {
	
	private static enum Size {
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
	
	public static void init() {
		GameRegistry.registerTileEntity(AetherBatteryEntity.class, "aether_battery_te");
//		GameRegistry.addShapedRecipe(new ItemStack(instance()),
//				"WPW", "WCW", "WWW",
//				'W', new ItemStack(Blocks.PLANKS, 1, OreDictionary.WILDCARD_VALUE),
//				'P', new ItemStack(Items.PAPER, 1, OreDictionary.WILDCARD_VALUE),
//				'C', NostrumResourceItem.getItem(ResourceType.CRYSTAL_LARGE, 1));
	}
	
	private final Size size;
	
	private AetherBatteryBlock(Size size) {
		super(Material.ROCK, MapColor.OBSIDIAN);
		this.size = size;
		this.setUnlocalizedName(size.ID);
		this.setHardness(3.0f);
		this.setResistance(10.0f);
		this.setCreativeTab(NostrumAetheria.creativeTab);
		this.setSoundType(SoundType.STONE);
		this.setHarvestLevel("axe", 0);
	}
	
	public String getID() {
		return size.ID;
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
	
	public static class AetherBatteryEntity extends AetherTickingTileEntity {

		private static final String NBT_SIZE = "battery_size";
		
		private Size size;
		
		public AetherBatteryEntity(Size size) {
			super(0, size.capacity);
			this.size = size;
			this.setAutoSync(5);
		}
		
		public AetherBatteryEntity() {
			this(Size.SMALL);
		}
		
		public Size getSize() {
			return size;
		}

		@Override
		public void update() {
			if (this.ticksExisted % 5 == 0) {
				this.flowIntoNearby();
			}
			
			super.update();
		}
		
		protected void flowIntoNearby() {
			// Look for adjacent batteries to flow into or fill from.
			// Get total sum'ed aether to figure out how much it looks like each should have.
			AetherBatteryEntity[] batteries = new AetherBatteryEntity[EnumFacing.values().length];
			int totalAether = this.handler.getAether(null);
			int neighborCount = 0;
			int max = totalAether;
			int min = totalAether;
			for (EnumFacing dir : EnumFacing.values()) {
				if (!handler.getSideEnabled(dir)) {
					continue;
				}
				
				BlockPos neighbor = pos.offset(dir);
				
				// First check for a TileEntity
				TileEntity te = worldObj.getTileEntity(neighbor);
				if (te != null && te instanceof AetherBatteryEntity) {
					AetherBatteryEntity other = (AetherBatteryEntity) te;
					batteries[dir.ordinal()] = other;
					int aether = other.getHandler().getAether(dir.getOpposite());
					totalAether += aether;
					if (aether > max) {
						max = aether;
					}
					if (aether < min) {
						min = aether;
					}
					neighborCount++;
				}
			}
			
			if (neighborCount > 0 && totalAether > 0 && (max - min) > 2) {
				// Found neighbors. How much should be in each?
				int each = totalAether / (neighborCount + 1);
				int spillover = (totalAether % (neighborCount + 1));
				int i = 0;
				
				//System.out.println(String.format("Evening out. Total %d (%d neighbors). I have %d", totalAether, neighborCount, handler.getAether(null)));
				
				// Repeat for each found battery
				// Note: Using a shuffled list to prevent spillover from causing bad surface tension
				List<AetherBatteryEntity> entities = Lists.newArrayList(batteries);
				entities.add(this);
				Collections.shuffle(entities);
				for (AetherBatteryEntity other : entities) {
					if (other != null) {
						int amt = (other.handler.getAether(null) - each + (spillover > i ? 1 : 0));
						if (amt > 0) {
							other.handler.drawAether(null, amt);
						} else if (amt < 0) {
							other.handler.addAether(null, -amt, true);
						}
						i++;
					}
				}
				
				// Hmmm I should be checking returns here and make sure not to vanish aether if
				// something wants to rate limit how much can flow per tick or something...
			}
		}
		
		@Override
		public void onAetherFlowTick(int diff, boolean added, boolean taken) {
			super.onAetherFlowTick(diff, added, taken);
		}
		
		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
			nbt = super.writeToNBT(nbt);
			
			nbt.setString(NBT_SIZE, this.size.name());
			return nbt;
		}
		
		@Override
		public void readFromNBT(NBTTagCompound nbt) {
			super.readFromNBT(nbt);
			
			try {
				this.size = Size.valueOf(nbt.getString(NBT_SIZE));
			} catch (Exception e) {
				this.size = Size.SMALL;
			}
		}
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
