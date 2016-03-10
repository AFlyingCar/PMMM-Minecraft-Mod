package com.MadokaMagica.mod_madokaMagica.util;

import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Dictionary {
	public static final byte[] MAGIC = new byte[] { (byte)'D', (byte)'I', (byte)'C', (byte)'T', 0, 0 };
	private String filename;
	private ArrayList<String> possibilities;

	public Dictionary(String new_filename){
		filename = new_filename;
		possibilities = new ArrayList<String>();
		this.Init();
	}

	public void Init(){
		byte[] allData = readFile();
		parseFile(allData);
		rawData = allData;
	}

	private void parse(byte[] data){
		int i=0;
		while(i<6)
			if(data[i] != MAGIC[i++])
				return;

		while(i != data.length){
			byte[] bsize = { data[i], data[++i] };
			int size = ByteBuffer.wrap(bsize).getInt();
			byte[] string = new byte[size];
			int pos = i;
			while(i++ != pos+size){
				string[i-pos] = data[i];
			}
			possibilities.add(new String(string));
		}
	}

	public byte[] readFile(String new_filename){
		filename = new_filename;
		return readFile();
	}

	private byte[] readFile(){
		// You have no idea how long it took me to find all this stuff
		// I really hate Java
		return Files.readAllBytes(Paths.get(filename));
	}

	public ArrayList<String> getAllPossibilities(){
		return possibilities;
	}

	public Pattern getPossibilitiesAsPatternObject(){
		String pat = getPossibilitiesAsPatternString();
		return Pattern.compile(pat);
	}

	public String getPossibilitiesAsPatternString(){
		return getPossibilitiesAsPatternString("","");
	}

	public String getPossibilitiesAsPatternString(String prefix){
		return getPossibilitiesAsPatternString(prefix,"");
	}

	public String getPossibilitiesAsPatternString(String prefix, String suffix){
		String str = prefix + "(";
		for(String possibility : possibilities){
			str += possibility + "|";
		}
		// Strip off trailing |
		return str.substring(0,str.length-1) + ")" + suffix;
	}
}
