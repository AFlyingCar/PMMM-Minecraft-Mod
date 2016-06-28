package com.MadokaMagica.mod_madokaMagica.factories;

import net.minecraft.world.chunk.IChunkProvider;

import com.MadokaMagica.mod_madokaMagica.world.LabrynthProvider;

public class LabrynthGeneratorFactory{
	public LabrynthProvider provider;

	public LabrynthGeneratorFactory(LabrynthProvider lp){
		this.provider = lp;
	}
	public IChunkProvider create(){
        return null;
    }
}
