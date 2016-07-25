package com.MadokaMagica.mod_madokaMagica.world.gen;

import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.block.Block;

import com.MadokaMagica.mod_madokaMagica.factories.LabrynthFactory.LabrynthDetails;
import com.MadokaMagica.mod_madokaMagica.MadokaMagicaBlocks;

public class LabrynthBuilder {
    public final static float DEFAULT_RADIUS = 100.0F; // TODO: Come up with a better number

    public final static int STARTING_LOCATION = 0;
    public final static int CORRIDOR = 1;
    public final static int TRAP = 2;
    public final static int EMPTY = 3;
    public final static int THRONE = 4;

    // Rooms must be at least these dimensions
    public final static int MIN_ROOM_WIDTH = 4;
    public final static int MIN_ROOM_DEPTH = 4;
    public final static int MIN_ROOM_HEIGHT = 2;

    // Every room must have at least 1 door/entrance
    public final static int MIN_ROOM_DOORS = 1;

    public static class Door{
        public int top;
        public int left;
        public int bottom;
        public int right;
        public int x;
    }

    public static class LabrynthPart {
        public LabrynthPart(){
            doors = new ArrayList<Door>();
            blocks = new HashMap<int[],Block>();
        }

        // Position
        public int x;
        public int y = 256; // Made maximum height. Labrynths will not have multiple levels for simplicity's sake. May add more levels later
        public int z;

        // Dimensions
        public int width;
        public int height;
        public int depth;

        public int type;

        public boolean spawnable;

        public AxisAlignedBB boundingBox;

        // An array specifying all doors that a part has
        public List<Door> doors;

        // {{x,y,z}:Block,...}
        public Map<int[],Block> blocks;

        public void addBlock(int x, int y, int z, Block b){
            int[] pos = {x,y,z};
            blocks.put(pos,b);
        }
    }

    public static boolean buildLabrynthMaze(LabrynthDetails details){
        if(details == null) return false;

        List<LabrynthPart> parts = new ArrayList<LabrynthPart>();

        LabrynthBuilder.buildLabrynthBoundaries(details.world,details.sizeMultiplier,0);
        LabrynthBuilder.buildLabrynthParts(parts,details);

        // Create all of the bounding boxes for each part
        for(LabrynthPart part : parts){
            createBoundingBox(part);
        }

        LabrynthBuilder.placeLabrynthParts(parts,details);

        return true;
    }

    public static void buildLabrynthParts(List<LabrynthPart> parts, LabrynthDetails details){
        generateThroneRoom(parts,details);
        List<LabrynthPart> illegals = getPartsIntersectingWithOuterWall(parts);
        for(LabrynthPart part : illegals){
            convertPartToStartingLocation(part,parts);
        }
        /*
        generateStartingLocations(parts,details);
        generateTraps(parts,details);
        generateEmptyRooms(parts,details); // Empty rooms, ther is nothing in them except a door
        generateCorridors(parts,details);
        */
    }

    public static List<LabrynthPart> getPartsIntersectingWithOuterWall(List<LabrynthPart> parts){
        List<LabrynthPart> intersecting = new ArrayList<LabrynthPart>();
        // TODO: Finish this method
        return intersecting;
    }

    public static void convertPartToStartingLocation(LabrynthPart part,List<LabrynthPart> parts){
        // TODO: Finish this method
    }

    public static void generateThroneRoom(List<LabrynthPart> parts, LabrynthDetails details){
        LabrynthPart part = new LabrynthPart();

        // TODO: Add some code here which uses PMDataTracker to custom build the room

        for(Door door : part.doors){
            int depth = 0;
            LabrynthPart corridor = generateCorridorOfRandomLengthFrom(parts,door);
            if(corridor != null){
                generateRandomRoom(parts,corridor,depth);
            }
        }

        parts.add(part);
    }

    // For now, all corridors are straight, may add bending in the future
    // NOTE: Corridors only generate facing one direction, this needs to be fixed for doors that aren't facing a certain direction
    public static LabrynthPart generateCorridorOfRandomLengthFrom(List<LabrynthPart> parts, Door door){
        // TODO: Generate length based on the size of the labrynth, where the door is, and which direction we are facing
        Random rand = new Random();
        int zlength = (int)(rand.nextFloat()*10); // Corridors can be of 0 length

        int[] from = {door.x,door.left};
        int[] to = {door.x,zlength};

        return generateCorridor(from,to,door.top-door.bottom);
    }

    // Depth is how far we have traveled into this horrendous recursive mess
    // It is supposed to help us break out by being used by the part-generators so that they know how many doors to generate (the deeper we go, the less doors to generate)
    // It is also a fail-safe to make sure that we don't go too deep, lest we get lost
    public static void generateRandomRoom(List<LabrynthPart> parts,LabrynthPart corridor,int depth){
        // There can only be 32 rooms from the Throne room in any direction (not counting the exits)
        if(depth >= 32) return;

        Random rand = new Random();

        int type = (int)(rand.nextFloat()*2) + 2;

        LabrynthPart part = null;

        switch(type){
            case TRAP:
                part = generateRandomTrapFromCorridor(corridor,depth);
                break;
            case EMPTY:
                part = generateEmptyRoomFromCorridor(corridor,depth);
                break;
            default:
                System.out.println("ERROR WHEN BUILDING LABRYNTH ROOMS: Unknown part type " + type);
                break;
        }

        if(part == null){
            // If we failed to build a part, then simply return and don't continue on from here
            return;
        }

        for(Door door : part.doors){
            LabrynthPart newCorridor = generateCorridorOfRandomLengthFrom(parts,door);
            if(newCorridor != null){
                generateRandomRoom(parts,newCorridor,depth+1);
            }
        }
        
        parts.add(part);
    }

    @Deprecated
    public static void generateCorridors(List<LabrynthPart> parts, LabrynthDetails details){
        int num_doors = 2; // each corridor has two doors

        // basically we're just going to connect the pre-generated parts
        for(LabrynthPart part : parts){
            List<LabrynthPart> corridors = new ArrayList<LabrynthPart>();
            for(Door door : part.doors){
                LabrynthPart corridor = new LabrynthPart();
                corridor.doors.add(door);

                // Will return null if it is supposed to be a dead-end
                LabrynthPart otherEnd = getFacingPart(parts,door);
                if(otherEnd != null){
                    corridor.doors.add(getFacingDoor(otherEnd,door));
                }

                corridors.add(corridor);
            }
        }
    }

    public static LabrynthPart generateRandomTrapFromCorridor(LabrynthPart corridor, int depth){
        // TODO: Finish this method
        return null;
    }

    public static LabrynthPart generateEmptyRoomFromCorridor(LabrynthPart corridor, int depth){
        // TODO: Finish this method
        return null;
    }

    /*
     *
     * from (0,0)
     * to (10,1)
     * height 3
     *
     *  Room
     *(0,3,0)   (10,3,0)
     * +----------+
     * |\ (0,3,1) |\
     * | +----------+ (10,3,1)
     * |D|(0,0,0) |D|
     * +-|--------+ |(10,0,0)
     *  \|         \|
     *   +----------+
     * (0,0,1)       (10,0,1)
     *
     *    y   z
     *   TOP LEFT
     * Dl L = 0 <- From
     * Dl T = 3
     * Dl X = 0 <- From
     *
     *     y      z
     *   BOTTOM RIGHT
     * Dl R = 1 <- To
     * Dl B = 0
     * 
     */
    // pfrom={x,z},pto={x,z},height=h
    public static LabrynthPart generateCorridor(int[] pfrom, int[] pto, int height){
        LabrynthPart part = new LabrynthPart();
        part.type = CORRIDOR;
        part.x = pfrom[0];
        part.z = pfrom[1];
        part.height = height;

        int xstart;
        int xend;
        if(pfrom[0] < pto[0]){
            xstart = pfrom[0];
            xend = pto[0];
        }else{
            xstart = pto[0];
            xend = pfrom[0];
        }

        int zstart;
        int zend;
        if(pfrom[1] < pto[1]){
            zstart = pfrom[1];
            zend = pto[1];
        }else{
            zstart = pto[1];
            zend = pfrom[1];
        }

        part.width = xstart-xend;
        part.depth = zstart - zend;

        for(int x=xstart;x<xend;x++){
            for(int z=zstart;z<zend;z++){
                part.addBlock(x,0,z,MadokaMagicaBlocks.labrynthWallBlock);
                part.addBlock(x,height,z,MadokaMagicaBlocks.labrynthWallBlock);
            }
            for(int y=0;y<part.height;y++){
                // Actual y-position of block is the part's y-position minus the offset
                part.addBlock(x,part.y-y,0,MadokaMagicaBlocks.labrynthWallBlock);
                part.addBlock(x,part.y-y,zend,MadokaMagicaBlocks.labrynthWallBlock);
            }
        }
        for(int y=0;y<part.height;y++){
            for(int z=zstart;z<zend;z++){
                part.addBlock(xstart,y,z,MadokaMagicaBlocks.labrynthWallBlock);
                part.addBlock(xend,y,z,MadokaMagicaBlocks.labrynthWallBlock);
            }
        }
        Door d1 = new Door();
        d1.top = part.y;
        d1.left = pfrom[1];
        d1.bottom = part.y-height;
        d1.right = pto[1];
        d1.x = pfrom[0];

        Door d2 = new Door();
        d2.top = part.y;
        d2.left = pto[1];
        d2.bottom = part.y-height;
        d2.right = pfrom[1];
        d2.x = pfrom[0];

        part.doors.add(d1);
        part.doors.add(d2);

        return part;
    }

    // NOTE: Are getFacingPart and getFacingDoor even needed anymore?
    public static LabrynthPart getFacingPart(List<LabrynthPart> parts, Door door){
        // TODO: Finish this method
        for(LabrynthPart part : parts){
        }
        return null;
    }

    public static Door getFacingDoor(LabrynthPart part, Door door){
        // TODO: Does this actually work the way I intend it to?
        for(Door d : part.doors){
            if(d.x == door.x && d.left == door.left)
                return d;
        }
        return null;
    }

    public static void placeLabrynthParts(List<LabrynthPart> parts,LabrynthDetails details){
        // Place every part
        for(LabrynthPart part : parts){
            // Place every block in each part
            for(Entry<int[],Block> block : part.blocks.entrySet()){
                int[] pos = block.getKey();
                details.world.setBlock(pos[0],pos[1],pos[2],block.getValue(),0,2);
            }

            // Tell LabrynthDetails if this spot is spawnable
            if(part.spawnable){
                details.startingLocations.add(part.boundingBox);
            }
        }
    }

    // The walls of every labrynth is to be a labrynthTeleporter
    // The world, the radius multiplier, the thickness of the walls
    public static void buildLabrynthBoundaries(World world,float mult,int thickness){
        float radius = (LabrynthBuilder.DEFAULT_RADIUS * mult) + thickness;

        int xsize = (int)radius*2;
        int ysize = (int)radius*2;
        int zsize = (int)radius*2;

        // TODO: Maybe make the walls out of bedrock instead?
        // Or maybe make it randomized?
        final Block wallBlock = MadokaMagicaBlocks.labrynthTeleporter;

        for(int x=0;x<=xsize;x++){
            for(int z=0;z<=zsize;z++){
                world.setBlock(x,0,z,wallBlock,0,2);
                world.setBlock(x,ysize,z,wallBlock,0,2);
            }

            for(int y=0;y<=ysize;y++){
                world.setBlock(x,y,0,wallBlock,0,2);
                world.setBlock(x,y,zsize,wallBlock,0,2);
            }
        }

        for(int y=0;y<=ysize;y++){
            for(int z=0;z<=zsize;z++){
                world.setBlock(0,y,z,wallBlock,0,2);
                world.setBlock(xsize,y,z,wallBlock,0,2);
            }
        }

        if(thickness != 0){
            try{
                buildLabrynthBoundaries(world,mult,thickness-1);
            }catch(StackOverflowError soe){
                // We've reached maximum recursion depth, time to leave
                System.out.println("WARNING! Thickness level was too big and recursion went too deep.");
                System.out.println("    As such, recursion is stopping with " + thickness + " thickness levels left");
            }
        }
    }

    /*
    public static void designateStartingLocation(LabrynthDetails details,LabrynthPart part){
        int[] starting = new int[3]; //{x,y,z}

        if(part.type == STARTING_LOCATION){
            starting[0] = part.x;
            starting[1] = part.y;
            starting[2] = part.z;
            details.startingLocations.add(starting);
        }
    }
    */

    public static void createBoundingBox(LabrynthPart part){
        part.boundingBox = AxisAlignedBB.getBoundingBox(part.x,part.y,part.z,part.width,part.height,part.depth);
    }
}

