package com.MadokaMagica.mod_madokaMagica.factories;

import com.MadokaMagica.mod_madokaMagica.factories.LabrynthGeneratorFactory;
import com.MadokaMagica.mod_madokaMagica.world.LabrynthProvider;

public class LabrynthGeneratorFactoryFactory {
	public static LabrynthGeneratorFactory create(LabrynthProvider provider){
		if(provider == null) return null;
		
		LabrynthGeneratorFactory factory = new LabrynthGeneratorFactory(provider);

		// TODO: Do some stuff to factory

		return factory;
	}
}

