package com.MadokaMagica.mod_madokaMagica.world.biomes;

import net.minecraft.world.biome.BiomeGenBase;

public class BiomeWitchLabrynth extends BiomeGenBase{
    public BiomeWitchLabrynth(int biomeID){
        super(biomeID);
        this.setBiomeName("Witch Labrynth");
        this.theBiomeDecorator.treesPerChunk = 0;
        this.theBiomeDecorator.flowersPerChunk = 0;
        this.theBiomeDecorator.grassPerChunk = 0;
        this.setDisableRain();
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
    }
}

