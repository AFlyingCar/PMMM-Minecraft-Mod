package com.MadokaMagica.mod_madokaMagica.managers;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;

import com.MadokaMagica.mod_madokaMagica.items.ItemSoulGem;

public class ItemSoulGemManager{
    private ArrayList<ItemSoulGem> all_soul_gems;
    private static ItemSoulGemManager instance;

    private ItemSoulGemManager(){
        all_soul_gems = new ArrayList<ItemSoulGem>();
    }

    // private ItemSoulGemManager(ArrayList<ItemSoulGem> list){
    //     all_soul_gems = list;
    // }

    public void addSoulGem(ItemSoulGem sg){
        all_soul_gems.add(sg);
    }

    public void addAllSoulGems(ArrayList<ItemSoulGem> list){
        all_soul_gems.addAll(list);
    }

    public ItemSoulGem getSoulGem(int index){
        return all_soul_gems.get(index);
    }
    
    public ItemSoulGem getSoulGemByPlayer(EntityPlayer player){
        for(ItemSoulGem sg : all_soul_gems)
            if(sg.player == player)
                return sg;
        return null;
    }

    public int getSoulGemCount(){
        return all_soul_gems.size();
    }

    public void manage(){
        for(ItemSoulGem sg : all_soul_gems){
            sg.update();
        }
    }

    public static ItemSoulGemManager getInstance(){
        if(ItemSoulGemManager.instance == null)
            ItemSoulGemManager.instance = new ItemSoulGemManager();
        return ItemSoulGemManager.instance;
    }
}
