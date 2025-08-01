package com.smanzana.nostrumaetheria.rituals;

import java.util.List;

import com.smanzana.nostrumaetheria.blocks.AetherInfuser;
import com.smanzana.nostrummagica.block.CandleBlock;
import com.smanzana.nostrummagica.block.ChalkBlock;
import com.smanzana.nostrummagica.block.PedestalBlock;
import com.smanzana.nostrummagica.ritual.IRitualLayout;
import com.smanzana.nostrummagica.ritual.RitualRecipe;
import com.smanzana.nostrummagica.ritual.outcome.IRitualOutcome;
import com.smanzana.nostrummagica.tile.PedestalBlockEntity;
import com.smanzana.nostrummagica.util.TextUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class OutcomeCreateAetherInfuser implements IRitualOutcome {

	public OutcomeCreateAetherInfuser() {
	}
	
	@Override
	public void perform(Level world, Player player, BlockPos center, IRitualLayout layout, RitualRecipe recipe) {
		if (!world.isClientSide) {
			AetherInfuser.CreateAetherInfuser(world, center);
			// clear altar on server
			BlockEntity te = world.getBlockEntity(center.offset(0, 0, 0));
			if (te == null || !(te instanceof PedestalBlockEntity))
				return;
			((PedestalBlockEntity) te).setItem(ItemStack.EMPTY);
			
			// Break all altars, chalk, candles
			int radius = 4;
			for (int i = -radius; i <= radius; i++)
			for (int j = -radius; j <= radius; j++) {
				BlockPos pos = center.offset(i, 0, j);
				BlockState state = world.getBlockState(pos);
				if (state != null &&
						(state.getBlock() instanceof CandleBlock || state.getBlock() instanceof PedestalBlock || state.getBlock() instanceof ChalkBlock)) {
					world.destroyBlock(pos, true);
				}
			}
		}
	}

	@Override
	public List<Component> getDescription() {
		return TextUtils.GetTranslatedList("ritual.outcome.create_infuser.desc");
	}

	@Override
	public String getName() {
		return "create_aether_infuser";
	}

}
