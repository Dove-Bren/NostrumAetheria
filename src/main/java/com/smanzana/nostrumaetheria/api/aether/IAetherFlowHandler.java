package com.smanzana.nostrumaetheria.api.aether;

import com.smanzana.nostrumaetheria.api.aether.AetherFlowMechanics.AetherIterateContext;

import net.minecraft.core.Direction;

public interface IAetherFlowHandler extends IAetherHandler {
	
	public static final class AetherFlowConnection {
		public final IAetherHandler handler;
		public final Direction face;
		
		public AetherFlowConnection(IAetherHandler handler, Direction connectionFace) {
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
	
	/**
	 * Add additional IAetherHandlers that should be tried after this one as part of the aether graph.
	 * Notably this gives handlers the chance to add their other connections to the context to make a big
	 * web.
	 * @param context
	 */
	public void addFlowPropagationConnections(AetherIterateContext context);

	public int drawAether(Direction side, int amount, AetherIterateContext context);
	
	public int getAetherTotal(Direction side, AetherIterateContext context);
}
