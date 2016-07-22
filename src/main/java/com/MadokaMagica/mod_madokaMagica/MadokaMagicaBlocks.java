package com.MadokaMagica.mod_madokaMagica;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import com.MadokaMagica.mod_madokaMagica.blocks.BlockLabrynthTeleporter;
import com.MadokaMagica.mod_madokaMagica.MadokaMagicaMod;

public class MadokaMagicaBlocks{
    public static Block labrynthTeleporter;
    public static Block labrynthWallBlock;

    public static void loadBlocks(){
        labrynthTeleporter = new BlockLabrynthTeleporter().setBlockName("labrynthTeleporter").setCreativeTab(MadokaMagicaMod.PMMMCreativeTab);
        labrynthWallBlock = Blocks.stone; // TODO: Make this an actual block, rather than a minecraft one
    }
}

