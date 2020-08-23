package com.smanzana.nostrumaetheria.api.aether;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.aether.IAetherFlowHandler.AetherFlowConnection;

import net.minecraft.util.EnumFacing;

public final class AetherFlowMechanics {
	
	public static interface AetherIterateContext {
		public void addConnections(Collection<AetherFlowConnection> connections);
		
		public @Nullable AetherFlowConnection getNext();
		
		public boolean visitted(AetherFlowConnection connection);
		public void visit(AetherFlowConnection connection);
	}
	
	private static final class DepthFirst implements AetherIterateContext {
		private Set<AetherFlowConnection> visitted;
		private Stack<AetherFlowConnection> stack;
		
		public DepthFirst(AetherFlowConnection start) {
			visitted = new HashSet<>();
			stack = new Stack<>();
			stack.push(start);
		}

		@Override
		public void addConnections(Collection<AetherFlowConnection> connections) {
			for (AetherFlowConnection conn : connections) {
				stack.push(conn);
			}
		}

		@Override
		public AetherFlowConnection getNext() {
			if (stack.isEmpty()) {
				return null;
			}
			
			return stack.pop();
		}

		@Override
		public boolean visitted(AetherFlowConnection connection) {
			return visitted.contains(connection);
		}

		@Override
		public void visit(AetherFlowConnection connection) {
			visitted.add(connection);
		}
	}
	
	private static final class BreadthFirst implements AetherIterateContext {
		private Set<AetherFlowConnection> visitted;
		private List<AetherFlowConnection> queue;
		
		public BreadthFirst(AetherFlowConnection start) {
			visitted = new HashSet<>();
			queue = new LinkedList<>();
			queue.add(start);
		}

		@Override
		public void addConnections(Collection<AetherFlowConnection> connections) {
			queue.addAll(connections);
		}

		@Override
		public AetherFlowConnection getNext() {
			if (queue.isEmpty()) {
				return null;
			}
			return queue.remove(0);
		}
		
		@Override
		public boolean visitted(AetherFlowConnection connection) {
			return visitted.contains(connection);
		}
		
		@Override
		public void visit(AetherFlowConnection connection) {
			visitted.add(connection);
		}
	}
	
	private static final int drawFromConnection(AetherFlowConnection connection, int amount, AetherIterateContext context) {
		if (connection.handler instanceof IAetherFlowHandler) {
			IAetherFlowHandler flowHandler = (IAetherFlowHandler) connection.handler;
			return flowHandler.drawAether(connection.face, amount, context);
		} else {
			return connection.handler.drawAether(connection.face, amount);
		}
	}
	
	private static boolean IsRunning = false;
	public static final int drawFromHandler(@Nullable IAetherHandler originHandler, @Nullable EnumFacing originFace,
			IAetherHandler handler, EnumFacing face, int amount, boolean depthFirst) {
		if (IsRunning) {
			// This indicates that trying to draw from an aether handler ended up invoking a NEW handler walk.
			// This is usuaully avoided by overriding the IAetherFlowHandler interface and providing a selfish that-handler-only
			// draw method (as well as an easy place to add any connections for iteration!).
			throw new RuntimeException("Handler iteration already running! Did you forget to make your flow-channeling aether handler a 'IAetherFlowHandler' ?");
		}
		IsRunning = true;
		
		AetherFlowConnection start = new AetherFlowConnection(handler, face);
		AetherIterateContext context = depthFirst ? new DepthFirst(start) : new BreadthFirst(start);
		
		if (originHandler != null) {
			int unused; // hmmmmmmmmm should we EVER visit the origin on any face?
			context.visit(new AetherFlowConnection(originHandler, null));
			for (EnumFacing originOtherFace : EnumFacing.values()) {
				context.visit(new AetherFlowConnection(originHandler, originOtherFace));
			}
		}
		
		int left = amount;
		while (left > 0 && (start = context.getNext()) != null) {
			if (context.visitted(start)) {
				continue;
			}
			
			context.visit(start);
			left -= drawFromConnection(start, left, context);
		}
		
		IsRunning = false;
		
		return amount - left; 
	}
	
	public static final int drawFromHandler(IAetherHandler handler, EnumFacing face, int amount, boolean depthFirst) {
		return drawFromHandler(null, null, handler, face, amount, depthFirst);
	}
	
	public static final int drawFromHandler(IAetherHandler handler, EnumFacing face, int amount) {
		return drawFromHandler(handler, face, amount, false);
	}
	
	private static final int checkAetherFromConnection(AetherFlowConnection connection, AetherIterateContext context) {
		if (connection.handler instanceof IAetherFlowHandler) {
			IAetherFlowHandler flowHandler = (IAetherFlowHandler) connection.handler;
			return flowHandler.getAetherTotal(connection.face, context);
		} else {
			return connection.handler.getAether(connection.face);
		}
	}
	
	public static final int getTotalAether(IAetherHandler handler, EnumFacing face) {
		if (IsRunning) {
			// This indicates that trying to draw from an aether handler ended up invoking a NEW handler walk.
			// This is usuaully avoided by overriding the IAetherFlowHandler interface and providing a selfish that-handler-only
			// get total method (as well as an easy place to add any connections for iteration!).
			throw new RuntimeException("Handler iteration already running! Did you forget to make your flow-channeling aether handler a 'IAetherFlowHandler' ?");
		}
		IsRunning = true;
		
		AetherFlowConnection conn = new AetherFlowConnection(handler, face);
		AetherIterateContext context = new BreadthFirst(conn);
		
		int total = 0;
		while ((conn = context.getNext()) != null) {
			if (context.visitted(conn)) {
				continue;
			}
			
			context.visit(conn);
			total += checkAetherFromConnection(conn, context);
		}
		
		IsRunning = false;
		return total;
	}
}
