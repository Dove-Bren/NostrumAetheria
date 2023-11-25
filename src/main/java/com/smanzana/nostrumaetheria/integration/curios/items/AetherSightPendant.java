package com.smanzana.nostrumaetheria.integration.curios.items;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.item.IAetherVisionProvider;
import com.smanzana.nostrummagica.integration.curios.items.NostrumCurio;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NostrumAetheria.MODID)
public class AetherSightPendant extends NostrumCurio implements IAetherVisionProvider {

	public static final String ID = "aether_sight_pendant";
	
	public AetherSightPendant() {
		super(AetheriaCurios.PropCurio(), ID);
	}

	@Override
	public boolean shouldProvideAetherVision(ItemStack stack, PlayerEntity player, EquipmentSlotType slot) {
		return true;
	}
	
}
