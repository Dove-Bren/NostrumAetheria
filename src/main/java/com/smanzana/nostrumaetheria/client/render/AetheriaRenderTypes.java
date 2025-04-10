package com.smanzana.nostrumaetheria.client.render;

import java.util.OptionalDouble;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class AetheriaRenderTypes {

	public static final RenderType AETHER_FLAT;
	public static final RenderType AETHER_FLAT_TRIS;
	public static final RenderType RELAY_LINES;
	public static final RenderType INFUSER_ORB;
	public static final RenderType INFUSER_SPARK;
	public static final RenderType WISPBLOCK_BASE;
	public static final RenderType WISPBLOCK_PLATFORM;
	public static final RenderType WISPBLOCK_GEM;
	public static final RenderType WISPBLOCK_GEM_OUTLINE;
	
	private static final String Name(String suffix) {
		return "aetheriarender_" + suffix;
	}
	
	static {
		
		final RenderStateShard.TransparencyStateShard TRANSLUCENT_TRANSPARENCY = ObfuscationReflectionHelper.getPrivateValue(RenderStateShard.class, null, "TRANSLUCENT_TRANSPARENCY");
		final RenderStateShard.CullStateShard NO_CULL = new RenderStateShard.CullStateShard(false);
		//final RenderState.DepthTestState DEPTH_EQUAL = new RenderState.DepthTestState("==", GL11.GL_EQUAL);
		//final RenderState.DepthTestState NO_DEPTH = new RenderState.DepthTestState("none", GL11.GL_ALWAYS);
		//final RenderState.LightmapState NO_LIGHTING = new RenderState.LightmapState(false);
	    final RenderStateShard.LightmapStateShard LIGHTMAP_ENABLED = new RenderStateShard.LightmapStateShard(true);
	    final RenderStateShard.LineStateShard LINE_2 = new RenderStateShard.LineStateShard(OptionalDouble.of(2));
	    final RenderStateShard.LineStateShard LINE_3 = new RenderStateShard.LineStateShard(OptionalDouble.of(3));
	    //@SuppressWarnings("deprecation")
		//final RenderState.TextureState BLOCK_SHEET = new RenderState.TextureState(AtlasTexture.LOCATION_BLOCKS_TEXTURE, false, false);
	    final RenderStateShard.AlphaStateShard HALF_ALPHA = new RenderStateShard.AlphaStateShard(.5f);
	    final RenderStateShard.ShadeModelStateShard SHADE_ENABLED = new RenderStateShard.ShadeModelStateShard(true);
	    final RenderStateShard.WriteMaskStateShard NO_DEPTH_WRITE = new RenderStateShard.WriteMaskStateShard(true, false);
		
		// Define render types
		RenderType.CompositeState glState;
				
		glState = RenderType.CompositeState.builder()
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP_ENABLED)
				.setWriteMaskState(NO_DEPTH_WRITE)
			.createCompositeState(false);
		AETHER_FLAT = RenderType.create(Name("AetherFlat"), DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, GL11.GL_QUADS, 32, glState);
		AETHER_FLAT_TRIS = RenderType.create(Name("AetherFlatTris"), DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, GL11.GL_TRIANGLES, 32, glState);
		
		glState = RenderType.CompositeState.builder()
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP_ENABLED)
				.setLineState(LINE_3)
			.createCompositeState(false);
		RELAY_LINES = RenderType.create(Name("AetherRelayLines"), DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, GL11.GL_LINES, 64, glState);
		
		glState = RenderType.CompositeState.builder()
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setLightmapState(LIGHTMAP_ENABLED)
				.setWriteMaskState(NO_DEPTH_WRITE)
			.createCompositeState(false);
		INFUSER_ORB = RenderType.create(Name("AetherInfuserOrb"), DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, GL11.GL_QUADS, 64, glState);

		glState = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(TileEntityAetherInfuserRenderer.SPARK_TEX_LOC, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				//.depthTest(NO_DEPTH) // actually want this?
				.setLightmapState(LIGHTMAP_ENABLED)
				.setWriteMaskState(NO_DEPTH_WRITE)
			.createCompositeState(false);
		INFUSER_SPARK = RenderType.create(Name("AetherInfuserSpark"), DefaultVertexFormat.BLOCK, GL11.GL_QUADS, 64, glState);

		glState = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(TileEntityWispBlockRenderer.BASE_TEX_LOC, false, false))
				.setLightmapState(LIGHTMAP_ENABLED)
				.setAlphaState(HALF_ALPHA)
				.setShadeModelState(SHADE_ENABLED)
			.createCompositeState(true);
		WISPBLOCK_BASE = RenderType.create(Name("WispBlockBase"), DefaultVertexFormat.BLOCK, GL11.GL_QUADS, 32, glState);

		glState = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(TileEntityWispBlockRenderer.PLATFORM_TEX_LOC, false, false))
				.setLightmapState(LIGHTMAP_ENABLED)
				.setAlphaState(HALF_ALPHA)
				.setShadeModelState(SHADE_ENABLED)
			.createCompositeState(true);
		WISPBLOCK_PLATFORM = RenderType.create(Name("WispBlockPlatform"), DefaultVertexFormat.BLOCK, GL11.GL_TRIANGLES, 32, glState);

		glState = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(TileEntityWispBlockRenderer.GEM_TEX_LOC, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setLightmapState(LIGHTMAP_ENABLED)
			.createCompositeState(false);
		WISPBLOCK_GEM = RenderType.create(Name("WispBlockGem"), DefaultVertexFormat.BLOCK, GL11.GL_QUADS, 64, glState);

		glState = RenderType.CompositeState.builder()
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setLightmapState(LIGHTMAP_ENABLED)
				.setLineState(LINE_2)
			.createCompositeState(false);
		WISPBLOCK_GEM_OUTLINE = RenderType.create(Name("WispBlockGemOutline"), DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, GL11.GL_LINES, 64, glState);
	}
	
}
