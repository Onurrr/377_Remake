package com.rs2.model.region.music;

import com.rs2.model.players.Player;

/**
 *
 * @author Faris
 */
public class MusicHandler {
    
    /**
     * Constructor allocates client to be updated
     * @param c 
     */
    public MusicHandler(Player c) {
	this.c = c;
    }
    
    Player c; WorldData a = new WorldData(); MusicData s = new MusicData();
    
    private int currentArea, currentSong = 0;
    
    /**
     * Method which executes the music change
     */
    public void handleMusic() {
        updateAreaID(); 
        updateSongID();
        c.getActionSender().sendSong(currentSong);
    }
    
    /**
     * Sets the variable equal to the relevant Song ID
     */
    private void setSongID() {
        currentSong = s.getSongID(currentArea);
    }
    
    /**
     * calls the songID to be update
     */
    private void updateSongID() {
        setSongID();
    }
    
    /**
     * Sets area ID as the currentArea variable
     */
    private void setAreaID() {
        currentArea = a.getAreaID(c);
    }
    
    /**
     * Calls area ID to be updated
     */
    private void updateAreaID() {
        setAreaID();
    }
}