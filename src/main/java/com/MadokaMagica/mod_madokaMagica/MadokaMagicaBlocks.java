package com.MadokaMagica.mod_madokaMagica;

import net.minecraft.block.Block;

import com.MadokaMagica.mod_madokaMagica.blocks.BlockLabrynthTeleporter;
import com.MadokaMagica.mod_madokaMagica.MadokaMagicaMod;

public class MadokaMagicaBlocks{
    public static Block labrynthTeleporter;

    public static void loadBlocks(){
        labrynthTeleporter = new BlockLabrynthTeleporter().setBlockName("labrynthTeleporter").setCreativeTab(MadokaMagicaMod.PMMMCreativeTab);
    }
}

