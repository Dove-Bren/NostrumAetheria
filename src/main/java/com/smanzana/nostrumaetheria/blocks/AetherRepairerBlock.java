package com.smanzana.nostrumaetheria.blocks;

import java.util.Random;

import javax.annotation.Nonnull;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.recipes.IAetherRepairerRecipe;
import com.smanzana.nostrumaetheria.gui.NostrumAetheriaGui;
import com.smanzana.nostrumaetheria.recipes.RepairerRecipeManager;
import com.smanzana.nostrumaetheria.tiles.AetherRepairerBlockEntity;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.items.SpellScroll;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

public class AetherRepairerBlock extends Block implements ILoreTagged {
	
	public static final BooleanProperty ON = BooleanProperty.create("on");
	public static final String ID = "aether_repairer";
	
	public static void initDefaultRecipes() {
		RepairerRecipeManager.instance().addRecipe(new ArmorRepairRecipe());
		RepairerRecipeManager.instance().addRecipe(new WeaponRepairRecipe());
		RepairerRecipeManager.instance().addRecipe(new ToolRepairRecipe());
		RepairerRecipeManager.instance().addRecipe(new ScrollRepairRecipe());
	}
	
	public AetherRepairerBlock() {
		super(Block.Properties.create(Material.ROCK)
				.hardnessAndResistance(3.0f, 10.0f)
				.sound(SoundType.STONE)
				.harvestTool(ToolType.PICKAXE)
				);
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(ON);
	}
	
	public boolean getFurnaceOn(BlockState state) {
		return state.get(ON);
	}
	
//	@Override
//	public boolean isSideSolid(BlockState state, IBlockAccess worldIn, BlockPos pos, Direction side) {
//		return true;
//	}
	
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (!worldIn.isRemote) {
			playerIn.openGui(NostrumAetheria.instance, NostrumAetheriaGui.aetherRepairerID, worldIn, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
		
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new AetherRepairerBlockEntity();
	}
	
	public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState()
				.with(ON, false);
	}
	
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			destroy(world, pos, state);
			world.removeTileEntity(pos);
		}
	}
	
	private void destroy(World world, BlockPos pos, BlockState state) {
		TileEntity ent = world.getTileEntity(pos);
		if (ent == null || !(ent instanceof AetherRepairerBlockEntity))
			return;
		
		AetherRepairerBlockEntity furnace = (AetherRepairerBlockEntity) ent;
		for (int i = 0; i < furnace.getSizeInventory(); i++) {
			if (furnace.getStackInSlot(i) != null) {
				ItemEntity item = new ItemEntity(
						world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5,
						furnace.removeStackFromSlot(i));
				world.addEntity(item);
			}
		}
		
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (null == stateIn || !stateIn.get(ON))
			return;
		
		double d0 = (double)pos.getX() + 0.5D;
		double d1 = (double)pos.getY() + 1.2D;
		double d2 = (double)pos.getZ() + 0.5D;
		
		worldIn.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		worldIn.addParticle(ParticleTypes.WITCH, d0, d1, d2, (rand.nextFloat() - .5) * .2, .75, (rand.nextFloat() - .5) * .2);
		
		if (rand.nextFloat() < .1f) {
			worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, 1.0F, 0.25F, false);
			worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_LADDER_STEP, SoundCategory.BLOCKS, 0.1F, 0.25F, false);
		}
	}
	
	@Override
	public String getLoreKey() {
		return "aether_repairer";
	}

	@Override
	public String getLoreDisplayName() {
		return "Aether Repairing";
	}

	@Override
	public Lore getBasicLore() {
		return new Lore().add("These blocks user aether to repair items and equipment.");
	}

	@Override
	public Lore getDeepLore() {
		return new Lore().add("These blocks user aether to repair items and equipment.", "The amount of aether it takes to repair each point of damage depends on the material, type of tool, or form of equipment.");
	}

	@Override
	public InfoScreenTabs getTab() {
		return InfoScreenTabs.INFO_BLOCKS;
	}
	
	public static @Nonnull ItemStack RepairTool(ItemStack tool, int amt) {
		tool.setDamage(Math.max(0, tool.getDamage() - amt));
		return tool;
	}
	
	protected static class ArmorRepairRecipe implements IAetherRepairerRecipe {

		@Override
		public boolean matches(ItemStack stack) {
			return stack.getItem() instanceof ArmorItem && stack.isDamaged();
		}

		@Override
		public int getAetherCost(ItemStack stack) {
			final float base;
			final float materialMod;
			final float enchantMod;
			
			ArmorItem armor = (ArmorItem) stack.getItem();
			switch (armor.getEquipmentSlot()) {
			case FEET:
			case HEAD:
				base = 20f;
				break;
			case LEGS:
				base = 30f;
				break;
			case CHEST:
			case MAINHAND:
			case OFFHAND:
			default:
				base = 40f;
				break;
			}
			
			final IArmorMaterial material = armor.getArmorMaterial();
			if (material == ArmorMaterial.LEATHER) {
				materialMod = .65f;
			} else if (material == ArmorMaterial.CHAIN || material == ArmorMaterial.IRON) {
				materialMod = 1f;
			} else if (material == ArmorMaterial.GOLD) {
				materialMod = .8f;
			} else if (material == ArmorMaterial.DIAMOND) {
				materialMod = 1.5f;
			} else {
				materialMod = 1.5f;
			}
				
			if (stack.isEnchanted()) {
				enchantMod = Math.min(1f, 1.2f * stack.getEnchantmentTagList().size());
			} else {
				enchantMod = 1f;
			}
			
			return Math.round(base * enchantMod * materialMod);
		}

		@Override
		public ItemStack repair(ItemStack stack) {
			return RepairTool(stack, 1);
		}
		
	}
	
	protected static class WeaponRepairRecipe implements IAetherRepairerRecipe {

		@Override
		public boolean matches(ItemStack stack) {
			return stack.getItem() instanceof SwordItem && stack.isDamaged();
		}

		@Override
		public int getAetherCost(ItemStack stack) {
			final float base;
			final float materialMod;
			final float enchantMod;
			
			SwordItem sword = (SwordItem) stack.getItem();
			
			base = 25;
			
			IItemTier tier = sword.getTier();
			if (tier == ItemTier.WOOD) {
				materialMod = .25f;
			} else if (tier == ItemTier.STONE) {
				materialMod = .6f;
			} else if (tier == ItemTier.IRON) {
				materialMod = 1f;
			} else if (tier == ItemTier.GOLD) {
				materialMod = 1.4f;
			} else if (tier == ItemTier.DIAMOND) {
				materialMod = 3f;
			} else {
				materialMod = 3f;
			}
			
			if (stack.isEnchanted()) {
				enchantMod = Math.min(1f, 1.2f * stack.getEnchantmentTagList().size());
			} else {
				enchantMod = 1f;
			}
			
			return Math.round(base * enchantMod * materialMod);
		}

		@Override
		public ItemStack repair(ItemStack stack) {
			return RepairTool(stack, 1);
		}
		
	}
	
	protected static class ToolRepairRecipe implements IAetherRepairerRecipe {

		@Override
		public boolean matches(ItemStack stack) {
			return stack.getItem() instanceof ToolItem && stack.isDamaged();
		}

		@Override
		public int getAetherCost(ItemStack stack) {
			final float base;
			float materialMod = 3f;
			final float enchantMod;
			
			ToolItem tool = (ToolItem) stack.getItem();
			base = 20f;
			
			IItemTier tier = tool.getTier();
			if (tier == ItemTier.WOOD) {
				materialMod = .25f;
			} else if (tier == ItemTier.STONE) {
				materialMod = .6f;
			} else if (tier == ItemTier.IRON) {
				materialMod = 1f;
			} else if (tier == ItemTier.GOLD) {
				materialMod = 1.4f;
			} else if (tier == ItemTier.DIAMOND) {
				materialMod = 3f;
			} else {
				materialMod = 3f;
			}
			
			if (stack.isEnchanted()) {
				enchantMod = Math.min(1f, 1.2f * stack.getEnchantmentTagList().size());
			} else {
				enchantMod = 1f;
			}
			
			return Math.round(base * enchantMod * materialMod);
		}

		@Override
		public ItemStack repair(ItemStack stack) {
			return RepairTool(stack, 1);
		}
		
	}
	
	protected static class ScrollRepairRecipe implements IAetherRepairerRecipe {

		@Override
		public boolean matches(ItemStack stack) {
			return stack.getItem() instanceof SpellScroll && stack.isDamaged();
		}

		@Override
		public int getAetherCost(ItemStack stack) {
			return 150;
		}

		@Override
		public ItemStack repair(ItemStack stack) {
			return RepairTool(stack, 1);
		}
		
	}
}
