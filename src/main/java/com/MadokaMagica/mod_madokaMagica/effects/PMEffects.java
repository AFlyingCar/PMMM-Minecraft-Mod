package com.MadokaMagica.mod_madokaMagica.effects;

/*
 ******************************************************
 *    READ ME! A LOT OF TEXT SO YOU DON'T MISS THIS   *
 ******************************************************
 *    NOTE: I Have figured out how to do this properly, or at least, a possible solution
 *    Look at the Minecraft Source code, specifically, the files net.minecraft.client.EntityRenderer.java, net.minecraft.client.shader.ShaderGroup.java, net.minecraft.client.shader.Shader.java, net.minecraft.client.shader.Framebuffer.java, and net.minecraft.client.renderer.OpenGlHelper.java
 *    These are the files that are involved in the Super Secret Settings part of Minecraft's options, which has a series of filters that almost seem to do exactly what I want.
 *    What needs to happen now is I need to find out how those filters are created, and recreate them here, with some modifications of course. This will be much much easier than trying to reinvent the wheel like I've been doing.
 *    Finally, this file is almost fucking done! :D
 */

import java.nio.IntBuffer;
import java.util.HashMap;
import java.io.IOException;

import com.google.gson.JsonSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.Minecraft;

import com.MadokaMagica.mod_madokaMagica.util.Helper;
import com.MadokaMagica.mod_madokaMagica.util.PMGuiHelper;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;

public class PMEffects{
    public final static int MBLUR_MAX_FRAME_WAIT = 10;
    private static int frameCount = 10; // Should equal 10 at the start, so that lastOverlay is created the very first time generateMotionBlur is called
    private static IntBuffer lastOverlay = null;
    private static final Logger logger = LogManager.getLogger();

    public static int failureCount = 0;

    public final static int MAXIMUM_FAILURE_COUNT = 20;

    private static int timer = 0;

    // A TEST COUNTER. DELETE ME SENPAI!
    private static int tick_counter = 0;

    // Construct a map of personality types with ResourceLocations to where they live
    private static HashMap<String,ResourceLocation> shaderLocations = new HashMap<String,ResourceLocation>(){
        {
            put("ARCHITECT",new ResourceLocation("shaders/post/architect.json"));
            put("ENGINEERING",new ResourceLocation("shaders/post/engineering.json"));
            put("GREED",new ResourceLocation("shaders/post/greed.json"));
            put("WATER",new ResourceLocation("shaders/post/water.json"));
            put("NATURE",new ResourceLocation("shaders/post/nature.json"));
            put("DAY",new ResourceLocation("shaders/post/day.json"));
            put("NIGHT",new ResourceLocation("shaders/post/night.json"));
            put("VILLAIN",new ResourceLocation("shaders/post/villain.json"));
            put("HERO",new ResourceLocation("shaders/post/hero.json"));
            put("PASSIVE",new ResourceLocation("shaders/post/passive.json"));
            put("AGGRESSIVE",new ResourceLocation("shaders/post/aggressive.json"));
            put("DEFAULT",new ResourceLocation("shaders/post/default.json"));
        }
    };

    public static void applyPlayerEffects(PMDataTracker pmdt){
        Minecraft mc = Minecraft.getMinecraft();
        EntityRenderer renderer = mc.entityRenderer;

        // If shaders aren't active, then we aren't doing anything anyways.
        if(!renderer.isShaderActive()) failureCount = MAXIMUM_FAILURE_COUNT;

        // Don't do anything if the failure rate is too high.
        if(failureCount >= MAXIMUM_FAILURE_COUNT) return;
        
        float opacity1 = calculateOpacity(pmdt,50,2);
        float opacity2 = calculateOpacity(pmdt,75,4);
        
        String personality = pmdt.getHighestScoreIden();

        try{
            // TODO: Find some way to tie opacity into each of these shaders
            renderer.theShaderGroup = new ShaderGroup(mc.getTextureManager(),mc.getResourceManager(),mc.getFramebuffer(),shaderLocations.get(personality));
            renderer.theShaderGroup.createBindFramebuffers(mc.displayWidth,mc.displayHeight);
        }catch(IOException exception){
            failureCount++;
            logger.warn("Unable to load shader \""+personality+"\". Failures="+failureCount,exception);
        }catch (JsonSyntaxException jsonsyntaxexception){
            failureCount++;
            logger.warn("Unable to load shader \""+personality+"\". Failures="+failureCount,jsonsyntaxexception);
        }
    }

    // A simple calculation to simulate 0%-100% within a range of lowestPercent%-100%
    private static float calculateOpacity(PMDataTracker pmdt, float lowestPercent, int mult){
        float corruption = pmdt.getCorruption();
        // lowestPercent is our new 0, but 100 is still the maximum
        // Multiply it by mult so that it rises faster than normal
        // EX: If the range is 50-100 (half of the normal range), then mult should be 2 so that the final number rises two times as fast.
        float opacity = (mult*(corruption-lowestPercent))/100.0F;
        if(opacity < 0)
            opacity = 0.0F;
        return opacity;
    }

    private static void renderOverlay(String personality, float opacity){
        IntBuffer overlay = lastOverlay;

        // Only update the overlay every 10 ticks
        if(timer >= 10){
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
                // overlay = generateMotionBlurOverlay();
                overlay = generateAggressiveOverlay(); // Replaced motion blur for debugging purposes so I don't have to run the long ass command every fucking time I want to test this
            overlay = applyTransparencyReal(overlay, opacity);

            lastOverlay = overlay;

            timer = 0;
        }else{
            timer++;
        }

        ScaledResolution sres = new ScaledResolution(Minecraft.getMinecraft(),
                Minecraft.getMinecraft().displayWidth,
                Minecraft.getMinecraft().displayHeight);
        int width = sres.getScaledWidth();
        int height = sres.getScaledHeight();

        if(overlay != null){
            System.out.println("Calling glDrawPixels(int,int,int,int,IntBuffer) at tick="+tick_counter);
            draw(width,height,GL11.GL_RED,GL11.GL_UNSIGNED_BYTE,overlay);
        }
    }

    @SuppressWarnings("all") // shh... nobody needs to know ;)
    private static void draw(int width, int height, int type, int format, IntBuffer overlay){
        GL11.glDrawPixels(width,height,type,format,overlay); // NOTE: This line seems to fail every once in a while for some reason. Appears to be random
        // Although, I'm not sure what it is actually doing, since it just seems to print:
        /*
            ########## GL ERROR ##########
            @ Post render
            1282: Invalid Operation
        */
        // Funny thing I just noticed, this is actually occurring because of glReadPixels over in .../util/Helper.java
        // Of course, I didn't notice since it's not even printing anything remotely useful
        // THANKS OPENGL
    }

    private static void renderGradient(String personality,float opacity){
        int[] fcolor;
        int[] tcolor = new int[]{0,0,0,0}; // Nothing
        int a = (int)(255*opacity);
        if(personality.equals("NATURE"))
            fcolor = new int[]{0,255,0,a};
        else if(personality.equals("WATER"))
            fcolor = new int[]{0,0,255,a};
        else if(personality.equals("GREED"))
            fcolor = new int[]{255,215,0,a};
        else if(personality.equals("NIGHT"))
            fcolor = new int[]{0,0,0,a};
        else if(personality.equals("AGGRESSIVE"))
            fcolor = new int[]{255,0,0,a};
        else
            fcolor = new int[]{255,255,255,a};

        ScaledResolution sres = new ScaledResolution(Minecraft.getMinecraft(),
            Minecraft.getMinecraft().displayWidth,
            Minecraft.getMinecraft().displayHeight);
        int width = sres.getScaledWidth();
        int height = sres.getScaledHeight();

        PMGuiHelper.drawGradientRectGetAround(0,0,width,height,Helper.RGBToHex(fcolor),Helper.RGBToHex(tcolor));
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
        return Helper.getScreenPixels(GL11.GL_RED,GL11.GL_UNSIGNED_BYTE,4);

        // IntBuffer screen = Helper.getScreenPixels();
        // int[] colors = new int[screen.array().length];
        // for(int i=0;i<colors.length;i++){
        //     colors[i] = screen.get(i)&0xFF0000;
        // }
        // return IntBuffer.wrap(colors);
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
        int[] colors;
        if(buff.hasArray()){
            colors = new int[buff.capacity()];
        }else{
            System.out.println("OH GOD WHAT? WHY DOES buff NOT HAVE AN ACCESSIBLE ARRAY?! AAAAAAAAAA");
            return null; // Return null because we can't do anything with a buff that doesn't have an array. Nothing will actually happen
        }
        for(int i=0;i<colors.length;i++){
            colors[i] = (buff.get(i)<<8)|((int)(0xFF*opacity));
        }
        return IntBuffer.wrap(colors);
    }

    private static IntBuffer applyTransparencyReal(IntBuffer buff, float opacity){
        IntBuffer returnable = buff.duplicate();
        if(returnable.hasArray()){
            System.out.println("OH GOD WHAT? WHY DOES buff NOT HAVE AN ACCESSIBLE ARRAY?! AAAAAAAAAA");
            return null; // Return null because we can't do anything with a buff that doesn't have an array. Nothing will actually happen
        }

        for(int i=0; i<returnable.capacity();i++){
            returnable.put(i,(returnable.get(i)<<8)|((int)(0xFF*opacity)));
        }
        return returnable;
    }
}

