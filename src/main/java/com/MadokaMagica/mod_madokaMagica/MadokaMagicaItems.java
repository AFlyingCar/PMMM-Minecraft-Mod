package com.MadokaMagica.mod_madokaMagica;

import net.minecraft.item.Item;

import com.MadokaMagica.mod_madokaMagica.items.ItemGriefSeed;
import com.MadokaMagica.mod_madokaMagica.items.ItemSoulGem;
import com.MadokaMagica.mod_madokaMagica.items.placers.LabrynthEntrancePlacer;
import com.MadokaMagica.mod_madokaMagica.items.placers.IncubatorMonsterPlacer;

public class MadokaMagicaItems{
    public static Item item_griefseed;
    public static Item item_soulgem;
    public static Item item_incubatormonsterplacer;
    public static Item item_labrynthentranceplacer;

    public static void loadItems(){
        item_griefseed = new ItemGriefSeed().setUnlocalizedName("itemGriefSeed");
        item_soulgem = new ItemSoulGem().setUnlocalizedName("itemSoulGem");

        item_incubatormonsterplacer = new IncubatorMonsterPlacer("Incubator",0xFFFFFF,0xFF0000).setUnlocalizedName("spawn egg"+"Incubator".toLowerCase()).setTextureName("madokamagica:spawn egg");
        item_labrynthentranceplacer = new LabrynthEntrancePlacer("PMWitchLabrynthEntrance",0x000000,0x000000).setUnlocalizedName("spawn egg"+"Labrynth Entrance".toLowerCase()).setTextureName("madokamagica:spawn egg");
    }
}

