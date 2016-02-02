package com.rs2.model.region.music;

/**
 *
 * @author Faris
 */
public class MusicData {
    
     WorldData a = new WorldData();
    
    /**
     * takes area IDs as switch, and returns the song ID for that area
     * @param area
     * @return
     */
    public int getSongID(int area) {
    
        switch(area){
            case 1:
                return 62;
            case 2:
                return 318;
            case 3:
            	return 381;
            case 4:
            	return 380;
            case 5:
            	return 96;
            case 6:
            	return 99;
            case 7:
            	return 98;
            case 8:
            	return 3;
            case 9:
            	return 587;
            case 10:
            	return 50;
            case 11:
                return 76;
            case 12:
            	return 72;
            case 13:
            	return 473;
            case 14:
            	//TODO find pirate music
            case 15:
            	return 141;
            case 16:
            	//TODO find abbys music
            case 17:
            	return 172;
            case 18:
            	//TODO find bandit camp music
            case 19:
            	return 66;
            case 20:
            	//TODO find bedibin camp music lol wut
            case 21:
            	//TODO find the song "morooned"
            case 22:
            	return 119;
            case 23:
            	return 87;
            case 24:
            	//TODO find burthrope music
            case 25:
            	return 104;
            case 26:
            	//TODO find canifis "village" music
            case 27:
            	//TODO find maze music (random
            case 28:
            	//TODO find cranador music lol
            case 29:
            	return 151;
            case 30:
            	return 47;
            case 31:
            	return 179;
            case 32:
            	return 150;
            case 33:
            	return 23;
            case 34:
            	//TODO find ham hideout music
            case 35:
            	return 114;
            case 36:
            	return 412;
            case 37:
            	//TODO find misc music
            case 38:
            	return 286;
            case 39:
            	//TODO find port Phasmatys music
            case 40:
            	return 35;
            case 41:
            	//TODO find long way home song
            case 42:
            	return 7;
            case 43:
            	return 90;
            case 44:
            	return 18;
            case 45:
            	return 23;
            case 46:
            	return 469;
            case 47:
            	return 125;
            case 48:
            	return 185;
            case 49:
            	return 314;
            case 50:
            	return 318;
            case 51:
            	return 318;
            case 52:
            	return 28;
            case 53:
            	//TODO find kalphite queen music
            case 54:
            	return 2;
            case 55:
            	return 111;
            case 56:
            	return 123;
            case 57:
            	return 36;
            case 58:
            	return 122;
            case 59:
            	return 541;
            case 60:
            	return 64;
            case 61:
            	return 327;
            case 62:
            	return 163;
            case 63:
            	return 333;
            case 64:
            	return 116;
            case 65:
            	return 157;
            case 66:
            	return 177;
            case 67:
            	return 93;
            case 68:
            	return 48;
            case 69:
            	return 107;
            case 70:
            	return 49;
            case 71:
            	return 186;
            default:
                return 3;
        }
    }
}