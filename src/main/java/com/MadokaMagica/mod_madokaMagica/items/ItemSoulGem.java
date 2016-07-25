package com.MadokaMagica.mod_madokaMagica.items;

import java.util.Random;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;

import net.minecraftforge.common.MinecraftForge;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.managers.MadokaMagicaEventManager;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;
import com.MadokaMagica.mod_madokaMagica.managers.ItemSoulGemManager;
import com.MadokaMagica.mod_madokaMagica.events.MadokaMagicaWitchTransformationEvent;
import com.MadokaMagica.mod_madokaMagica.items.ItemSoulGem;
import com.MadokaMagica.mod_madokaMagica.items.ItemGriefSeed;
import com.MadokaMagica.mod_madokaMagica.util.Helper;

public class ItemSoulGem extends Item{
    public final static float MAX_DESPAIR = 100.0F;
    public final static float CREATE_WEAPON_DESPAIR = 10.0F;
    public final static float BUFF_PLAYER_DESPAIR = 20.0F;
    public final static float AMBIENT_DESPAIR = 1.0F;

    // small explosion
    public final static float SOUL_GEM_SHATTER_EXPLOSION_RADIUS = 1.0F;

    public final static float BLOCK_HURT_DAMAGE_AMOUNT = 4.0F; // 2 hearts of damage

    public static final Set softBlocks = Sets.newHashSet(new Block[] {  Blocks.sand,
                                                                        Blocks.leaves,
                                                                        Blocks.sponge,
                                                                        Blocks.web,
                                                                        Blocks.tallgrass,
                                                                        Blocks.deadbush,
                                                                        Blocks.wool,
                                                                        Blocks.yellow_flower,
                                                                        Blocks.red_flower,
                                                                        Blocks.redstone_wire,
                                                                        Blocks.wheat,
                                                                        Blocks.redstone_torch,
                                                                        Blocks.unlit_redstone_torch,
                                                                        Blocks.snow,
                                                                        Blocks.reeds,
                                                                        Blocks.portal,
                                                                        Blocks.cake,
                                                                        Blocks.vine,
                                                                        Blocks.waterlily,
                                                                        Blocks.end_portal,
                                                                        Blocks.cocoa,
                                                                        Blocks.tripwire,
                                                                        Blocks.carrots,
                                                                        Blocks.potatoes,
                                                                        Blocks.carpet,
                                                                     }
                                                        );
    public static final Set hurtBlocks = Sets.newHashSet(new Block[] {Blocks.fire});

    protected float despair;
    protected static ItemSoulGem instance;
    public EntityPlayer player;
    public PMDataTracker playerData;
    public Random random;

    public ItemSoulGem(){
        super();
        random = new Random();
        ItemSoulGem.instance = this;
        // setTextureName();
    }

    public ItemSoulGem(EntityPlayer entityPlayer, PMDataTracker pmPlayerData){
        super();
        player = entityPlayer;
        playerData = pmPlayerData;
        despair = 0;
        random = new Random();
    }

    public float getDespair(ItemStack stack){
        return stack.getTagCompound().getFloat("SG_DESPAIR");
    }

    public void setDespair(ItemStack stack, float val){
        stack.getTagCompound().setFloat("SG_DESPAIR",val);
    }
    
    public void addDespair(ItemStack stack, float val){
        this.setDespair(stack,val+this.getDespair(stack));
    }

/*
    public PMWeapon createWeapon(){
        // Do some stuff to create a weapon
        despair += ItemSoulGem.CREATE_WEAPON_DESPAIR;
    }
*/

    // TODO: Make this method smarter
    public void randomBuffPlayer(ItemStack stack){
        this.addDespair(stack,ItemSoulGem.BUFF_PLAYER_DESPAIR);
        switch(random.nextInt()*2){
            case 0:
                boostJump();
            case 1:
                boostRun();
        }
    }

    protected void boostJump(){

    }

    protected void boostRun(){

    }

    public void cleanse(ItemGriefSeed gs){
        System.out.println("Calling Deprecated method ItemSoulGem().cleanse(ItemGriefSeed)\nPlease call static method ItemSoulGem.cleanse(ItemStack,ItemStack)");
        float subtract = ItemSoulGem.MAX_DESPAIR*0.1F; // 10% of MAX_DESPAIR
        if(subtract > despair) subtract = despair;
        gs.addDespair(subtract);
        despair -= subtract;
    }

    public static void cleanse(ItemStack soulgem,ItemStack griefseed){
        float subtract = ItemSoulGem.MAX_DESPAIR*0.1F; // 10% of MAX_DESPAIR
        float despair_sg = soulgem.getTagCompound().getFloat("SG_DESPAIR");
        float despair_gs = griefseed.getTagCompound().getFloat("SG_DESPAIR");
        if(subtract > despair_sg) subtract = despair_sg;
        griefseed.getTagCompound().setFloat("SG_DESPAIR",despair_gs+subtract);
        soulgem.getTagCompound().setFloat("SG_DESPAIR",despair_sg-subtract);

        // When we cleanse the soulgem, the grief seed used will have its traits altered slightly based on the cleanser
        instance.alterGriefSeed(ItemSoulGem.getOwner(soulgem),ItemGriefSeed.getOwner(griefseed));
    }

    public static PMDataTracker getOwner(ItemStack soulgem){
        // Loooooooooong sanity check
        if(soulgem == null ||
           soulgem.getTagCompound() == null ||
           !soulgem.getTagCompound().hasKey("PLAYER_UUID_LEAST_SIG") ||
           !soulgem.getTagCompound().hasKey("PLAYER_UUID_MOST_SIG"))
                return null;

        return PlayerDataTrackerManager.getInstance().getTrackerByUUID(
                    new UUID(
                        soulgem.getTagCompound().getLong("PLAYER_UUID_MOST_SIG"),
                        soulgem.getTagCompound().getLong("PLAYER_UUID_LEAST_SIG")
                    )
               );
    }

    public void alterGriefSeed(PMDataTracker soulgem, PMDataTracker griefseed){
        if(soulgem == null || griefseed == null){
            System.out.println("Warning! Either soulgem or griefseed was null!");
            return;
        }

        // Maximum amount of traits we are going to alter at once
        final int MAX_TRAITS_TO_ALTER = 2;
        int last_used = 0;

        for(int i=0; i<MAX_TRAITS_TO_ALTER; i++){ 
            int r = (int)(random.nextFloat() * 11); // There are 11 traits stored in PMDataTracker
            // Don't alter the same value twice
            if(r == last_used){
                i--;
                continue;
            }

            switch(r){
                case 0:
                    griefseed.setArchitectScore((float)(griefseed.getArchitectScore() + soulgem.getArchitectScore()*(random.nextFloat()*(0.25))));
                    break;
                case 1:
                    griefseed.setEngineeringScore((float)(griefseed.getEngineeringScore() + soulgem.getEngineeringScore()*(random.nextFloat()*(0.25))));
                    break;
                case 2:
                    griefseed.setGreedScore((float)(griefseed.getGreedScore() + soulgem.getGreedScore()*(random.nextFloat()*(0.25))));
                    break;
                case 3:
                    griefseed.setWaterScore((float)(griefseed.getWaterScore() + soulgem.getWaterScore()*(random.nextFloat()*(0.25))));
                    break;
                case 4:
                    griefseed.setNatureScore((float)(griefseed.getNatureScore() + soulgem.getNatureScore()*(random.nextFloat()*(0.25))));
                    break;
                case 5:
                    griefseed.setDayScore((float)(griefseed.getDayScore() + soulgem.getDayScore()*(random.nextFloat()*(0.25))));
                    break;
                case 6:
                    griefseed.setNightScore((float)(griefseed.getNightScore() + soulgem.getNightScore()*(random.nextFloat()*(0.25))));
                    break;
                case 7:
                    griefseed.setHeroScore((float)(griefseed.getHeroScore() + soulgem.getHeroScore()*(random.nextFloat()*(0.25))));
                    break;
                case 8:
                    griefseed.setVillainScore((float)(griefseed.getVillainScore() + soulgem.getVillainScore()*(random.nextFloat()*(0.25))));
                    break;
                case 9:
                    griefseed.setPassiveScore((float)(griefseed.getPassiveScore() + soulgem.getPassiveScore()*(random.nextFloat()*(0.25))));
                    break;
                case 10:
                    griefseed.setAggressiveScore((float)(griefseed.getAggressiveScore() + soulgem.getAggressiveScore()*(random.nextFloat()*(0.25))));
                    break;
                default:
                    System.out.println("Unknown random value " + r + "! Please check the Random Number Generator (tm)");
            }
            last_used = r;
        }
    }

    @Deprecated
    public void addDespair(float despair){
        this.despair += despair;
    }

    @Deprecated
    public boolean canTransformIntoWitch(){
        return despair >= ItemSoulGem.MAX_DESPAIR;
    }

    public boolean canTransformIntoWitch(ItemStack stack){
        return this.getDespair(stack) >= ItemSoulGem.MAX_DESPAIR;
    }

    @Deprecated
    protected void transformIntoWitch(){
        // MadokaMagicaEventManager.getInstance().startEvent(new MadokaMagicaWitchTransformationEvent(this.playerData));
        // MadokaMagicaWitchTransformationEvent.getInstance().activate(this.playerData);
        MinecraftForge.EVENT_BUS.post(new MadokaMagicaWitchTransformationEvent(this.playerData));
    }

    protected void transformIntoWitch(ItemStack stack){
        MinecraftForge.EVENT_BUS.post(new MadokaMagicaWitchTransformationEvent(this.getPlayerDataTrackerFor(stack)));
    }

    // Returns a PMDataTracker for a player tied to ItemStack if one exists. Otherwise, returns null
    public PMDataTracker getPlayerDataTrackerFor(ItemStack stack){
        NBTTagCompound nbt = stack.getTagCompound();
        if(nbt != null){
            if(nbt.hasKey("PLAYER_UUID_MOST_SIG") && nbt.hasKey("PLAYER_UUID_LEAST_SIG")){
                return PlayerDataTrackerManager.getInstance().getTrackerByUUID(
                        new UUID(
                            nbt.getLong("PLAYER_UUID_MOST_SIG"),
                            nbt.getLong("PLAYER_UUID_LEAST_SIG")
                        )
                    );
            }
        }
        System.out.println("No Player exists for this stack.");
        return null;
    }

    // If the soul gem gets destroyed, kill the player
    // We kill the bat man
    public void destroySoulGem(ItemStack stack){
        PMDataTracker player_tracker = getPlayerDataTrackerFor(stack);
        if(player_tracker == null){
            System.out.println("getPlayerDataTrackerFor(ItemStack) returned null. Either no EntityPlayer exists for this ItemStack or ItemStack does not contain an ItemSoulGem/ItemGriefSeed.");
            return;
        }
        Entity entity = player_tracker.getEntity();
        if(entity == null){
            System.out.println("getEntity() returned null! WHAT HAPPENED?!");
            return;
        }

        if(!(entity instanceof EntityPlayer)){
            System.out.println("entity is not an instance of EntityPlayer. This shouldn't happen because only EntityPlayer's can have an ItemSoulGem object.");
            return;
        }

        EntityPlayer player = (EntityPlayer)entity;

        if(player != null){
            player.worldObj.playSoundAtEntity(player,"madokamagica:shatter",50,1.0F);
            // TODO: What does the last boolean mean?
            player.worldObj.createExplosion(player,player.posX,player.posY,player.posZ,ItemSoulGem.SOUL_GEM_SHATTER_EXPLOSION_RADIUS,true);
            stack.damageItem(stack.getMaxDamage()+1,player); // Add 1 as well as the max damage to ensure that it is broken
            player.setDead();
        }
        System.out.println("Player is null! Skipping.");
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity,int par4,boolean bool){
        super.onUpdate(stack,world,entity,par4,bool);

        float worldTime = player.worldObj.getTotalWorldTime();
        if(worldTime % 23000 == 0){
            despair += AMBIENT_DESPAIR; // This may no longer be needed
            // Update the despair in the ItemStack
            int nbtDespair = stack.getTagCompound().getInteger("SG_DESPAIR");
            stack.getTagCompound().setFloat("SG_DESPAIR",nbtDespair+AMBIENT_DESPAIR);
        }

        // TODO: Fix this random number thing
        if(this.canTransformIntoWitch(stack) && (this.random.nextInt(100) == despair))
            this.transformIntoWitch(stack);
    }

    public boolean isBlockSoft(Block block){
        return this.softBlocks.contains(block);
    }

    public boolean isBlockHurt(Block block){
        return this.hurtBlocks.contains(block);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity){
        this.destroySoulGem(stack);
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z,EntityLivingBase entity){
        if(!isBlockSoft(block)){
            this.destroySoulGem(stack);
        }else if(isBlockHurt(block)){
            PMDataTracker tracker = getPlayerDataTrackerFor(stack);
            if(tracker == null){
                System.out.println("getPlayerDataTrackerFor(ItemStack) returned null. Either no EntityPlayer exists for this ItemStack or ItemStack does not contain an ItemSoulGem/ItemGriefSeed.");
                return false;
            }
            Entity track_entity = tracker.getEntity();
            if(track_entity == null){
                System.out.println("getEntity() returned null! WHAT HAPPENED?!");
                return false;
            }
            if(!(track_entity instanceof EntityPlayer)){
                System.out.println("entity is not an instance of EntityPlayer. This shouldn't happen because only EntityPlayer's can have an ItemSoulGem object.");
                return false;
            }
            EntityPlayer player = (EntityPlayer)track_entity;
            player.attackEntityFrom(new DamageSource("Damaged Soul Gem").setDamageBypassesArmor().setMagicDamage(),ItemSoulGem.BLOCK_HURT_DAMAGE_AMOUNT);
        }else{
            stack.damageItem(1,entity);
        }

        // IMPORTANT NOTE: If this item becomes too damaged, then it will be destroyed through the destroySoulGem method
        // Unfortunately, there is no easy way to do this through the Item class.
        // So, we handle an event called PlayerDestroyItemEvent, and call the correct method in PMEventHandler where this event is handled

        return true;
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player){
        super.onCreated(stack,world,player);
        NBTTagCompound nbt = stack.getTagCompound();
        if(nbt == null){
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }

        nbt.setLong("PLAYER_UUID_MOST_SIG",player.getUniqueID().getMostSignificantBits());
        nbt.setLong("PLAYER_UUID_LEAST_SIG",player.getUniqueID().getLeastSignificantBits());
        nbt.setFloat("SG_DESPAIR",0);


        PMDataTracker tracker = PlayerDataTrackerManager.getInstance().getTrackerByUUID(player.getPersistentID());

        ItemSoulGemManager.getInstance().registerSoulGem(stack,tracker);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List subItems){
        subItems.add(new ItemStack(this,1));
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool){
        String sgORgs = (this instanceof ItemGriefSeed)?"Grief Seed":"Soul Gem";
        list.add(sgORgs + " of " + playerData.getEntityName());
        list.add("Despair: " + this.getDespair(stack) + "%");

        if((this.getDespair(stack)/MAX_DESPAIR)*100 > 75)
            addDespairTooHighInformation(list);
    }

    protected void addDespairTooHighInformation(List list){
        list.add("Despair is at dangerous levels! Acquire a Grief Seed before it is too late!");
    }
}

