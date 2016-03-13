package com.MadokaMagica.mod_madokaMagica.effects;

import java.nio.IntBuffer;

// Imports that allow me to be a terrible person
import java.io.PrintStream;
import java.io.OutputStream;
import java.io.IOException;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.Minecraft;

import com.MadokaMagica.mod_madokaMagica.util.Helper;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;

public class PMEffects{
    public final static int MBLUR_MAX_FRAME_WAIT = 10;
    private static int frameCount = 10; // Should equal 10 at the start, so that lastOverlay is created the very first time generateMotionBlur is called
    private static IntBuffer lastOverlay = null;


    // A TEST COUNTER. DELETE ME SENPAI!
    private static int tick_counter = 0;

    public static void applyPlayerEffects(PMDataTracker pmdt){
        String personality = pmdt.getHighestScoreIden();
        generateMotionBlur((int)((pmdt.getCorruption()/100)*PMEffects.MBLUR_MAX_FRAME_WAIT));
        float opacity1 = calculateOpacity(pmdt,50,2);
        float opacity2 = calculateOpacity(pmdt,75,4);
        renderOverlay(personality,opacity2);
        renderGradient(opacity1);
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
        overlay = applyTransparencyReal(overlay, opacity);
        ScaledResolution sres = new ScaledResolution(Minecraft.getMinecraft(),
                Minecraft.getMinecraft().displayWidth,
                Minecraft.getMinecraft().displayHeight);
        int width = sres.getScaledWidth();
        int height = sres.getScaledHeight();


        // Start of nightmare fuel
        // I am so, so sorry
        tick_counter++; // DELETE THIS LINE
        if(overlay != null){
            PrintStream stream = System.out;

            // Shhh... Only dreams now
            System.setOut(new PrintStream(new OutputStream() {
                @Override public void write(int b) {}
            }) {
                @Override public void flush() {}
                @Override public void close() {}
                @Override public void write(int b) {}
                @Override public void write(byte[] b) {}
                @Override public void write(byte[] buf, int off, int len) {}
                @Override public void print(boolean b) {}
                @Override public void print(char c) {}
                @Override public void print(int i) {}
                @Override public void print(long l) {}
                @Override public void print(float f) {}
                @Override public void print(double d) {}
                @Override public void print(char[] s) {}
                @Override public void print(String s) {}
                @Override public void print(Object obj) {}
                @Override public void println() {}
                @Override public void println(boolean x) {}
                @Override public void println(char x) {}
                @Override public void println(int x) {}
                @Override public void println(long x) {}
                @Override public void println(float x) {}
                @Override public void println(double x) {}
                @Override public void println(char[] x) {}
                @Override public void println(String x) {}
                @Override public void println(Object x) {}
                @Override public PrintStream printf(String format, Object... args) { return this; }
                @Override public PrintStream printf(java.util.Locale l, String format, Object... args) { return this; }
                @Override public PrintStream format(String format, Object... args) { return this; }
                @Override public PrintStream format(java.util.Locale l, String format, Object... args) { return this; }
                @Override public PrintStream append(CharSequence csq) { return this; }
                @Override public PrintStream append(CharSequence csq, int start, int end) { return this; }
                @Override public PrintStream append(char c) { return this; }
            });

            // Thank you StackOverflow, for allowing me to do this :)
            // http://stackoverflow.com/questions/9882487/disable-system-out-for-speed-in-java

            System.out.println("Calling glDrawPixels(int,int,int,int,IntBuffer) at tick="+tick_counter);

            draw(width,height,GL11.GL_STENCIL_INDEX,GL11.GL_UNSIGNED_BYTE,overlay);

            System.setOut(stream);
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

