package com.MadokaMagica.mod_madokaMagica;

import net.minecraft.item.Item;

import com.MadokaMagica.mod_madokaMagica.items.ItemGriefSeed;
import com.MadokaMagica.mod_madokaMagica.items.ItemSoulGem;
import com.MadokaMagica.mod_madokaMagica.items.placers.EntityPlacer;

public class MadokaMagicaItems{
    public static Item item_griefseed;
    public static Item item_soulgem;
    public static Item item_incubatormonsterplacer;
    public static Item item_labrynthentranceplacer;

    public static void loadItems(){
        item_griefseed = new ItemGriefSeed().setUnlocalizedName("itemGriefSeed");
        item_soulgem = new ItemSoulGem().setUnlocalizedName("itemSoulGem");

        item_incubatormonsterplacer = new EntityPlacer(null,0xFFFFFF,0xFF0000,"Incubator",null).setUnlocalizedName("spawn egg"+"Incubator".toLowerCase()).setTextureName("madokamagica:spawn egg");
        item_labrynthentranceplacer = new EntityPlacer(null,0x000000,0x000000,"PMWitchLabrynthEntrance",null).setUnlocalizedName("spawn egg"+"Labrynth Entrance".toLowerCase()).setTextureName("madokamagica:spawn egg");
    }
}

