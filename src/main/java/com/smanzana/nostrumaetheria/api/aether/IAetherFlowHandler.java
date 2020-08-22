package com.smanzana.nostrumaetheria.api.aether;

import com.smanzana.nostrumaetheria.api.aether.AetherFlowMechanics.AetherIterateContext;

import net.minecraft.util.EnumFacing;

public interface IAetherFlowHandler extends IAetherHandler {
	
	public static final class AetherFlowConnection {
		public IAetherHandler handler;
		public EnumFacing face;
		
		public AetherFlowConnection(IAetherHandler handler, EnumFacing connectionFace) {
			this.handler = handler;
			this.face = connectionFace;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof AetherFlowConnection) {
				AetherFlowConnection other = (AetherFlowConnection) o;
				return other.face == this.face == other.handler.equals(this.handler);
			}
			
			return false;
		}
		
		@Override
		public int hashCode() {
			return handler.hashCode() * 883 + (face == null ? 47 : face.hashCode());
		}
	}

	public int drawAether(EnumFacing side, int amount, AetherIterateContext context);
	
	public int getAetherTotal(EnumFacing side, AetherIterateContext context);
}
