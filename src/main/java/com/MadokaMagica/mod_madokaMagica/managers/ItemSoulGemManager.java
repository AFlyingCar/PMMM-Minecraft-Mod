package com.MadokaMagica.mod_madokaMagica.managers;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.MadokaMagica.mod_madokaMagica.items.ItemSoulGem;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;

public class ItemSoulGemManager{
    private Map<ItemStack,PMDataTracker> soulgem_player_map;
    private static ItemSoulGemManager instance;

    private ItemSoulGemManager(){
        soulgem_player_map = new HashMap<ItemStack,PMDataTracker>();
    }

    public void registerSoulGem(ItemStack stack,PMDataTracker player){
        if(stack.getItem() instanceof ItemSoulGem)
            soulgem_player_map.put(stack,player);
        else
            System.out.println("Unable to register soul gem! " + stack + " does not contain an ItemSoulGem!");
    }

    public Map.Entry<ItemStack,PMDataTracker> deregisterSoulGem(ItemStack stack){
        Set<Map.Entry<ItemStack,PMDataTracker>> set = soulgem_player_map.entrySet();
        int index = -1;
        for(int i=0;i<set.size();i++){
            if(((Map.Entry<ItemStack,PMDataTracker>)set.toArray()[i]).getKey() == stack){
                index = i;
                break;
            }
        }
        Map.Entry<ItemStack,PMDataTracker> entry;
        if(index != -1){
            entry = (Map.Entry<ItemStack,PMDataTracker>)(set.toArray()[index]);
        }else{
            return null;
        }
        soulgem_player_map.remove(stack);
        return entry;
    }

    /*
    public void addAllSoulGems(ArrayList<ItemSoulGem> list){
        all_soul_gems.addAll(list);
    }
    */

    public PMDataTracker getPlayerForSoulGemStack(ItemStack stack){
        if(stack.getItem() instanceof ItemSoulGem)
            return soulgem_player_map.get(stack);
        else
            return null;
    }
    
    /*
    public ItemSoulGem getSoulGemByPlayer(EntityPlayer player){
        for(ItemSoulGem sg : all_soul_gems)
            if(sg.player == player)
                return sg;
        return null;
    }

    public int getSoulGemCount(){
        return all_soul_gems.size();
    }
    */

    public void manage(){
        /*
        for(ItemSoulGem sg : all_soul_gems){
            //sg.update();
        }
        */
    }

    public static ItemSoulGemManager getInstance(){
        if(ItemSoulGemManager.instance == null)
            ItemSoulGemManager.instance = new ItemSoulGemManager();
        return ItemSoulGemManager.instance;
    }
}
