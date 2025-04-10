package com.smanzana.nostrumaetheria.integration.curios.items;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.integration.curios.items.NostrumCurio;
import com.smanzana.nostrummagica.sound.NostrumMagicaSounds;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NostrumAetheria.MODID)
public class EludeCloakItem extends NostrumCurio {

	public static final String ID = "elude_cloak";
	
	public EludeCloakItem() {
		super(AetheriaCurios.PropCurio(), ID);
	}
	
	@SubscribeEvent
	public static void onAttack(LivingAttackEvent event) {
		if (event.isCanceled()) {
			return;
		}
		
		if (event.getAmount() > 0f && event.getEntityLiving() instanceof Player && event.getSource() instanceof EntityDamageSource) {
			Entity source = ((EntityDamageSource) event.getSource()).getEntity();
			Player player = (Player) event.getEntityLiving();
			Container inv = NostrumMagica.instance.curios.getCurios(player);
			if (inv != null) {
				for (int i = 0; i < inv.getContainerSize(); i++) {
					ItemStack stack = inv.getItem(i);
					if (stack.isEmpty() || !(stack.getItem() instanceof EludeCloakItem))
						continue;
						
					float chance = .15f;
					int cost = 150;
					
					// Check to see if we're facing the enemy that attacked us
					Vec3 attackFrom = source.position().subtract(player.position());
					double attackFromYaw = -Math.atan2(attackFrom.x, attackFrom.z) * 180.0F / (float)Math.PI;
					
					if (Math.abs(((player.getYRot() + 360f) % 360f) - ((attackFromYaw + 360f) % 360f)) < 30f) {
						if (NostrumMagica.rand.nextFloat() < chance) {
							// If there's aether, dodge!
							int taken = APIProxy.drawFromInventory(player.level, player, player.getInventory(), cost, stack);
							if (taken > 0) {
								// Dodge!
								event.setCanceled(true);
								NostrumMagicaSounds.DAMAGE_WIND.play(player.level, player.getX(), player.getY(), player.getZ());
								float dir = player.getYRot() + (NostrumMagica.rand.nextBoolean() ? -1 : 1) * 90f;
								float velocity = .5f;
								player.setDeltaMovement(velocity * Mth.cos(dir), player.getDeltaMovement().y, velocity * Mth.sin(dir));
								player.hurtMarked = true;
							}
						}
					}
				}
			}
		}
	}
	
}
