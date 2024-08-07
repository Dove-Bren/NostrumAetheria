package com.smanzana.nostrumaetheria.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.api.recipes.IAetherUnravelerRecipe;
import com.smanzana.nostrumaetheria.client.gui.container.AetherUnravelerGui;
import com.smanzana.nostrumaetheria.tiles.AetherUnravelerBlockEntity;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.item.SpellRune;
import com.smanzana.nostrummagica.item.SpellScroll;
import com.smanzana.nostrummagica.item.SpellTome;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;
import com.smanzana.nostrummagica.spell.Spell;
import com.smanzana.nostrummagica.spell.component.SpellEffectPart;
import com.smanzana.nostrummagica.spell.component.SpellShapePart;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

public class AetherUnravelerBlock extends Block implements ILoreTagged {
	
	public static final BooleanProperty ON = BooleanProperty.create("on");
	
	public static void initDefaultRecipes() {
		APIProxy.addUnravelerRecipe(new ScrollUnravelerRecipe());
		APIProxy.addUnravelerRecipe(new TomeUnravelerRecipe());
	}
	
	public AetherUnravelerBlock() {
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
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		AetherUnravelerBlockEntity unraveler = (AetherUnravelerBlockEntity) worldIn.getTileEntity(pos);
		NostrumMagica.instance.proxy.openContainer(player, AetherUnravelerGui.AetherUnravelerContainer.Make(unraveler));
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new AetherUnravelerBlockEntity();
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
		if (ent == null || !(ent instanceof AetherUnravelerBlockEntity))
			return;
		
		AetherUnravelerBlockEntity furnace = (AetherUnravelerBlockEntity) ent;
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
	
	protected static class ScrollUnravelerRecipe implements IAetherUnravelerRecipe {

		private static final int AETHER_COST = 2000;
		private static final int DURATION = 20 * 100;
		
		@Override
		public boolean matches(ItemStack stack) {
			return stack.getItem() instanceof SpellScroll;
		}

		@Override
		public int getAetherCost(ItemStack stack) {
			return AETHER_COST;
		}
		
		@Override
		public int getDuration(ItemStack stack) {
			return DURATION;
		}

		@Override
		public NonNullList<ItemStack> unravel(ItemStack stack) {
			NonNullList<ItemStack> ret = NonNullList.create();
			
			Spell spell = SpellScroll.GetSpell(stack);
			if (spell == null) {
				NostrumAetheria.logger.error("Tried to process spell scroll in unraveler but found no spell");
				return ret;
			}
			
			List<SpellShapePart> shapes = spell.getSpellShapeParts();
			List<SpellEffectPart> effects = spell.getSpellEffectParts();
			
			for (SpellShapePart shape : shapes) {
				ret.add(SpellRune.getRune(shape));
			}
			for (SpellEffectPart effect : effects) {
				ret.addAll(SpellRune.getRune(effect));
			}
			
			return ret;
		}
		
	}
	
	protected static class TomeUnravelerRecipe implements IAetherUnravelerRecipe {

		private static final int AETHER_COST = 5000;
		private static final int DURATION = 20 * 120;
		
		@Override
		public boolean matches(@Nonnull ItemStack stack) {
			return stack.getItem() instanceof SpellTome && !SpellTome.getSpellLibrary(stack).isEmpty();
		}

		@Override
		public int getAetherCost(ItemStack stack) {
			return AETHER_COST;
		}
		
		@Override
		public int getDuration(ItemStack stack) {
			return DURATION;
		}

		@Override
		public NonNullList<ItemStack> unravel(ItemStack stack) {
			NonNullList<ItemStack> ret = NonNullList.create();
			ret.add(stack.copy());
			
			List<Spell> spells = SpellTome.getSpellLibrary(stack);
			if (spells == null || spells.isEmpty()) {
				;
			} else {
				for (Spell spell : spells) {
					ItemStack scroll = SpellScroll.create(spell);
					ret.add(scroll);
				}
			}
			
			// Clear tome
			SpellTome.clearSpells(ret.get(0));
			
			return ret;
		}
	}

}
