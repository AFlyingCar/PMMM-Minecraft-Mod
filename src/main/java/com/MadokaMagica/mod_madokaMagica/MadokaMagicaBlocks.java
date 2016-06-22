package com.MadokaMagica.mod_madokaMagica;

import net.minecraft.block.Block;

import com.MadokaMagica.mod_madokaMagica.blocks.BlockLabrynthTeleporter;

public class MadokaMagicaBlocks{
    public static Block labrynthTeleporter;

    public static void loadBlocks(){
        labrynthTeleporter = new BlockLabrynthTeleporter().setBlockName("labrynthTeleporter");
    }
}

