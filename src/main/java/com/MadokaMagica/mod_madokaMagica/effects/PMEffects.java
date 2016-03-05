package com.MadokaMagica.mod_madokaMagica.effects;

import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.Minecraft;
import com.MadokaMagica.mod_madokaMagica.util.Helper;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;

public class PMEffects{
    public final static int MBLUR_MAX_FRAME_WAIT = 10;
    private static int frameCount = 0;
    private static IntBuffer lastOverlay = null;

    public static void applyPlayerEffects(PMDataTracker pmdt){
        String personality = pmdt.getHighestScoreIden();
        generateMotionBlur((int)((pmdt.getCorruption()/100)*PMEffects.MBLUR_MAX_FRAME_WAIT));
        float opacity1 = calculateOpacity(pmdt,50,2);
        float opacity2 = calculateOpacity(pmdt,75,4);
        renderOverlay(personality,opacity2);
        renderGradient(opacity1);
    }

    private static float calculateOpacity(PMDataTracker pmdt, float lowestPercent, int mult){
        float corruption = pmdt.getCorruption();
        float opacity = (mult*(corruption-lowestPercent))/100.0F;
        if(opacity < 0)
            opacity = 0.0F;
        return opacity;
    }

    private static void renderOverlay(String personality, float opacity){
        IntBuffer overlay;
        if(personality.equals("HERO"))
            overlay = generateHeroOverlay();
        else if(personality.equals("NATURE"))
            overlay = generateNatureOverlay();
        else if(personality.equals("WATER"))
            overlay = generateWaterOverlay();
        else if(personality.equals("NIGHT"))
            overlay = generateNightOverlay();
        else if(personality.equals("AGGRESSIVE"))
            overlay = generateAggressiveOverlay();
        else
            overlay = generateMotionBlurOverlay();
        overlay = applyTransparency(overlay, opacity);
        ScaledResolution sres = new ScaledResolution(Minecraft.getMinecraft(),
                Minecraft.getMinecraft().displayWidth,
                Minecraft.getMinecraft().displayHeight);
        int width = sres.getScaledWidth();
        int height = sres.getScaledHeight();
        GL11.glDrawPixels(width,height,GL11.GL_RGBA,GL11.GL_INT,overlay);
    }

    private static void renderGradient(float opacity){
        int[] color = {0,0,0,255};
    }

    private static IntBuffer generateHeroOverlay(){
        IntBuffer screen = Helper.getScreenPixels();
        int[] colors = new int[screen.array().length];
        for(int i=0; i<colors.length;i++){
            int[] rgb = Helper.HexToRGB(screen.get(i));
            colors[i] = (rgb[0]+rgb[1]+rgb[2])/3;
        }
        return IntBuffer.wrap(colors);
    }

    private static IntBuffer generateAggressiveOverlay(){
        IntBuffer screen = Helper.getScreenPixels();
        int[] colors = new int[screen.array().length];
        for(int i=0;i<colors.length;i++){
            colors[i] = screen.get(i)&0xFF0000;
        }
        return IntBuffer.wrap(colors);
    }

    private static IntBuffer generateNatureOverlay(){
        IntBuffer screen = Helper.getScreenPixels();
        int[] colors = new int[screen.array().length];
        for(int i=0;i<colors.length;i++){
            colors[i] = screen.get(i)&0x00FF00;
        }
        return IntBuffer.wrap(colors);
    }

    private static IntBuffer generateWaterOverlay(){
        IntBuffer screen = Helper.getScreenPixels();
        int[] colors = new int[screen.array().length];
        for(int i=0;i<colors.length;i++){
            colors[i] = screen.get(i)&0x0000FF;
        }
        return IntBuffer.wrap(colors);
    }

    private static IntBuffer generateNightOverlay(){
        IntBuffer screen = Helper.getScreenPixels();
        int[] colors = new int[screen.array().length];
        for(int i=0;i<colors.length;i++){
            colors[i] = 0;
        }
        return IntBuffer.wrap(colors);
    }

    private static IntBuffer generateMotionBlurOverlay(){
        return PMEffects.lastOverlay;
    }

    private static void generateMotionBlur(int wait_frames){
        IntBuffer overlay = PMEffects.lastOverlay;
        if(PMEffects.frameCount >= wait_frames){
            PMEffects.frameCount = 0;
            PMEffects.lastOverlay = Helper.getScreenPixels();
        }else{
            PMEffects.frameCount++;
        }
        //return overlay;
    }

    private static IntBuffer applyTransparency(IntBuffer buff, float opacity){
        int[] colors = new int[buff.array().length];
        for(int i=0;i<colors.length;i++){
            colors[i] = (buff.get(i)<<8)|((int)(0xFF*opacity));
        }
        return IntBuffer.wrap(colors);
    }
}

