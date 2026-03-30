package de.pfannekuchen.lotas.core.utils;

import de.pfannekuchen.lotas.core.MCVer;

/**
 * Adds debug values to the Minecraft registry
 * 
 * @author Scribble
 * @since v1.0
 * @version v1.3
 */
public class RegistryUtils {
	public static void renderStaticPotion(Object poseStack, double memOffsetX, double memOffsetY, double flipOffset) {
		double flip=1;
		memOffsetX+=2.2;
		memOffsetY+=-0.5;
		flip+=0.26;
		//#if MC>=12000
//$$ 		com.mojang.blaze3d.vertex.PoseStack memstack = ((net.minecraft.client.gui.GuiGraphics)poseStack).pose();
//$$ 		MCVer.translated(memstack, memOffsetX, memOffsetY, 0);
//$$ 		MCVer.scaled(memstack, flip, flip, flip);
		//#else
		MCVer.translated(poseStack, memOffsetX, memOffsetY, 0);
		MCVer.scaled(poseStack, flip, flip, flip);
		//#endif
		
		
		//#if MC>=11903
		//#if MC>=12000
//$$ 		MCVer.stack.pose().mulPose(MCVer.fromYXZ(0F, 0F, (float) flipOffset) );
		//#else
//$$ 		MCVer.stack.mulPose(MCVer.fromYXZ(0F, 0F, (float) flipOffset) );
		//#endif
		//#else
		//#if MC>=11700
		MCVer.stack.mulPose(com.mojang.math.Quaternion.fromXYZ(0, 0, (float) flipOffset));
		//#else
//$$ 		MCVer.rotated(poseStack, flipOffset, 0, 0, 1);
		//#endif
		//#endif
		
		int oB=0xE35720;
		int o=0xC24218;
		int oD=0x9A3212;
		
		int w=0xFFFFFF;
		int c=0x546980;
		
		int y=0;
		drawPixel(8, y, oB);
		drawPixel(9, y, oB);
		
		y=1;
		drawPixel(7, y, o);
		drawPixel(8, y, o);
		drawPixel(9, y, oB);
		
		y=2;
		drawPixel(6, y, o);
		drawPixel(7, y, o);
		drawPixel(8, y, o);
		drawPixel(9, y, o);
		
		y=3;
		drawPixel(5, y, w);
		drawPixel(8, y, oD);
		drawPixel(9, y, o);
		
		y=4;
		drawPixel(4, y, w);
		drawPixel(7, y, w);
		drawPixel(8, y, oD);
		drawPixel(9, y, oD);
		
		y=5;
		drawPixel(3, y, w);
		drawPixel(7, y, w);
		
		y=6;
		drawPixel(2, y, w);
		drawPixel(3, y, c);
		drawPixel(4, y, w);
		drawPixel(5, y, c);
		drawPixel(6, y, c);
		drawPixel(7, y, c);
		drawPixel(8, y, w);
		
		y=7;
		drawPixel(1, y, w);
		drawPixel(2, y, c);
		drawPixel(3, y, w);
		drawPixel(4, y, c);
		drawPixel(5, y, c);
		drawPixel(6, y, c);
		drawPixel(7, y, c);
		drawPixel(8, y, c);
		drawPixel(9, y, w);
		
		y=8;
		drawPixel(1, y, w);
		drawPixel(2, y, c);
		drawPixel(3, y, w);
		drawPixel(4, y, c);
		drawPixel(5, y, c);
		drawPixel(6, y, c);
		drawPixel(7, y, c);
		drawPixel(8, y, c);
		drawPixel(9, y, w);
		
		y=9;
		drawPixel(1, y, w);
		drawPixel(2, y, c);
		drawPixel(3, y, c);
		drawPixel(4, y, c);
		drawPixel(5, y, c);
		drawPixel(6, y, c);
		drawPixel(7, y, w);
		drawPixel(8, y, c);
		drawPixel(9, y, w);
		
		y=10;
		drawPixel(1, y, w);
		drawPixel(2, y, c);
		drawPixel(3, y, c);
		drawPixel(4, y, c);
		drawPixel(5, y, c);
		drawPixel(6, y, c);
		drawPixel(7, y, w);
		drawPixel(8, y, c);
		drawPixel(9, y, w);
		
		y=11;
		drawPixel(1, y, w);
		drawPixel(2, y, w);
		drawPixel(3, y, c);
		drawPixel(4, y, c);
		drawPixel(5, y, c);
		drawPixel(6, y, w);
		drawPixel(7, y, c);
		drawPixel(8, y, w);
		drawPixel(9, y, w);
		
		y=12;
		drawPixel(3, y, w);
		drawPixel(4, y, w);
		drawPixel(5, y, w);
		drawPixel(6, y, w);
		drawPixel(7, y, w);
		
		//#if MC>=11903
		//#if MC>=12000
//$$ 		MCVer.stack.pose().mulPose(MCVer.fromYXZ(0F, 0F, (float) -flipOffset));
		//#else
//$$ 		MCVer.stack.mulPose(MCVer.fromYXZ(0F, 0F, (float) -flipOffset));
		//#endif
		//#else
		//#if MC>=11700
		MCVer.stack.mulPose(com.mojang.math.Quaternion.fromXYZ(0, 0, (float) -flipOffset));
		//#else
//$$ 		MCVer.rotated(poseStack, -flipOffset, 0, 0, 1);
		//#endif
		//#endif
		
		//#if MC>=12000
//$$ 		MCVer.scaled(memstack, (double)1/flip, (double)1/flip, (double)1/flip);
//$$ 		MCVer.translated(memstack, -memOffsetX, -memOffsetY, 0);
		//#else
		MCVer.scaled(poseStack, (double)1/flip, (double)1/flip, (double)1/flip);
		MCVer.translated(poseStack, -memOffsetX, -memOffsetY, 0);
		//#endif
		
		//#if MC<11600
//$$ 		MCVer.color4f(255, 255, 255, 255);
		//#endif
	}
	
	private static void drawPixel(int x, int y, int color) {
		int alpha=0x80000000;
		MCVer.fill(x, y, x+1,y+1, alpha+color);
	}
}