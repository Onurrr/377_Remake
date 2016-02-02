package com.rs2.model.region.music;

import com.rs2.model.Position;
import com.rs2.model.players.Player;
/**
 * 
 * @author Faris
 * @param <Player>
 * 
 */

public class WorldData {
    
    // 1 = Tut
    // 2 = CW SPawn Room
    // 3 = Barrows under
    // 4 = Barrows top
    // 5 = Wildy
    // 6 = Ardy
    // 7 = Edgy
    // 8 = Wiz Tower
    // 9 = PC
    // 10 = Al Kharid
    // 11 = Lumbridge
    // 12 = Falador
    // 13 = Fight Caves
    // 14 = Pirate House
    // 15 = Barbarian Village
    // 16 = The Abyss
    // 17 = Ape Atoll
    // 18 = Bandit Camp
    // 19 = Barbarian Agility
    // 20 = Bedabin Camp
    // 21 = Crash Island
    // 22 = Catherby
    // 23 = White Wolf Moutain
    // 24 = Burthrope
    // 25 = Camelot
    // 26 = Canfis
    // 27 = Random Event Maze
    // 28 = Crandor
    // 29 = Draynor
    // 30 = Duel Arena
    // 31 = Entrana
    // 32 = Gnome Village
    // 33 = Goblin Village
    // 34 = HAM Base
    // 35 = Karamja
    // 36 = Lost & Found
    // 37 = Miscellania
    // 38 = Morton
    // 39 = Phasmatys
    // 40 = Port Sarim
    // 41 = Rimmington
    // 42 = Seers Village
    // 43 = Shilo Village
    // 44 = Taverly
    // 45 = Gnome Stronghold
    // 46 = Tzhaar
    // 47 = Varrock
    // 48 = Yanille
    // 49 = Castle Wars Game Area
    // 50 = Castle Wars Lobby
    // 51 = Castle Wars Underground
    // 52 = King Black Dragon  Lair
    // 53 = Kalphite Lair
    
 
            /**
             * Sets area id by integer in MusicHandler
             * @param c
             * @return
             */
             public int getAreaID(Player c) {
                 final Position position = c.getPosition();
				if (position.getX() >= 2625 && position.getX() <= 2687 && position.getY() >= 4670 && position.getY() <= 4735) 
							return 1;
				if ((position.getX() >= 2368 && position.getX() <= 2376 && position.getY() >= 3127 && position.getY() <= 3135 && position.getZ() == 1) || (position.getX() >= 2423 && position.getX() <= 2431 && position.getY() >= 3072 && position.getY() <= 3080 && position.getZ() == 1)) 
							return 2;
				if (position.getX() > 3520 && position.getX() < 3598 && position.getY() > 9653 && position.getY() < 9750) 
							return 3;
				if (position.getX() >= 3542 && position.getX() <= 3583 && position.getY() >= 3265 && position.getY() <= 3322) 
                    return 4;
                if(position.getX() > 2941 && position.getX() < 3392 && position.getY() > 3518 && position.getY() < 3966 ||
                    position.getX() > 3343 && position.getX() < 3384 && position.getY() > 9619 && position.getY() < 9660 ||
                    position.getX() > 2941 && position.getX() < 3392 && position.getY() > 9918 && position.getY() < 10366) 	
                    return 5;
				if (position.getX() > 2558 && position.getX() < 2729 && position.getY() > 3263 && position.getY() < 3343) 
							return 6;
				if (position.getX() > 3084 && position.getX() < 3111 && position.getY() > 3483 && position.getY() < 3509) 
							return 7;
				if (position.getX() > 2935 && position.getX() < 3061 && position.getY() > 3308 && position.getY() < 3396) 
							return 8;
				if (position.getX() >= 2659 && position.getX() <= 2664 && position.getY() >= 2637 && position.getY() <= 2644 || position.getX() >= 2623 && position.getX() <= 2690 && position.getY() >= 2561 && position.getY() <= 2688) 
							return 9;
				if (position.getX() > 3270 && position.getX() < 3455 && position.getY() > 2880 && position.getY() < 3330) 
							return 10;
				if (position.getX() > 3187 && position.getX() < 3253 && position.getY() > 3189 && position.getY() < 3263) 
							return 11;
				if (position.getX() > 3002 && position.getX() < 3004 && position.getY() > 3002 && position.getY() < 3004) 
							return 12;
				if (position.getX() >= 2360 && position.getX() <= 2445 && position.getY() >= 5045 && position.getY() <= 5125) 
                    return 13;
                if (position.getX() >= 3038 && position.getX() <= 3044 && position.getY() >= 3949 && position.getY() <= 3959) 
                    return 14;
                if (position.getX() >= 3060 && position.getX() <= 3099 && position.getY() >= 3399 && position.getY() <= 3450) 
                    return 15;
                if (position.getX() >= 3008 && position.getX() <= 3071 && position.getY() >= 4800 && position.getY() <= 4863) 
                    return 16;
                if (position.getX() >= 2691 && position.getX() <= 2826 && position.getY() >= 2690 && position.getY() <= 2831) 
                    return 17;
                if (position.getX() >= 3155 && position.getX() <= 3192 && position.getY() >= 2962 && position.getY() <= 2994) 
                    return 18;
                if (position.getX() >= 2526 && position.getX() <= 2556 && position.getY() >= 3538 && position.getY() <= 3575) 
                    return 19;
                if (position.getX() >= 3165 && position.getX() <= 3199 && position.getY() >= 3022 && position.getY() <= 3054) 
                    return 20;
                if (position.getX() >= 2785 && position.getX() <= 2804 && position.getY() >= 3312 && position.getY() <= 3327) 
                    return 21;
                if ((position.getX() >= 2792 && position.getX() <= 2829 && position.getY() >= 3412 && position.getY() <= 3472) ||
                (position.getX() > 2828 && position.getX() < 2841 && position.getY() > 3430 && position.getY() < 3459) ||
                (position.getX() > 2839 && position.getX() < 2861 && position.getY() > 3415 && position.getY() < 3441))
                    return 22;
                if (position.getX() >= 2850 && position.getX() <= 2879 && position.getY() >= 3446 && position.getY() <= 3522)
                    return 23;
                if (position.getX() >= 2878 && position.getX() <= 2937 && position.getY() >= 3524 && position.getY() <= 3582) 
                    return 24;
                if (position.getX() >= 2744 && position.getX() <= 2787 && position.getY() >= 3457 && position.getY() <= 3519) 
                    return 25;
                if (position.getX() >= 3425 && position.getX() <= 3589 && position.getY() >= 3256 && position.getY() <= 3582) 
                    return 26;
                if (position.getX() >= 2883 && position.getX() <= 2942 && position.getY() >= 4547 && position.getY() <= 4605) 
                    return 27;
                if (position.getX() >= 2819 && position.getX() <= 2859 && position.getY() >= 3224 && position.getY() <= 3312) 
                    return 28;
                if (position.getX() >= 3067 && position.getX() <= 3134 && position.getY() >= 3223 && position.getY() <= 3297) 
                    return 29;
                if (position.getX() >= 3324 && position.getX() <= 3392 && position.getY() >= 3196 && position.getY() <= 3329) 
                    return 30;
                if (position.getX() >= 2800 && position.getX() <= 2869 && position.getY() >= 3324 && position.getY() <= 3391) 
                    return 31;
                if (position.getX() >= 2492 && position.getX() <= 2563 && position.getY() >= 3132 && position.getY() <= 3203) 
                    return 32;
                if (position.getX() >= 2945 && position.getX() <= 2968 && position.getY() >= 3477 && position.getY() <= 3519) 
                    return 33;
                if (position.getX() >= 3136 && position.getX() <= 3193 && position.getY() >= 9601 && position.getY() <= 9664) 
                    return 34;
                if (position.getX() >= 2816 && position.getX() <= 2958 && position.getY() >= 3139 && position.getY() <= 3175) 
                    return 35;
                if (position.getX() >= 2334 && position.getX() <= 2341 && position.getY() >= 4743 && position.getY() <= 4751) 
                    return 36;
                if (position.getX() >= 2495 && position.getX() <= 2625 && position.getY() >= 3836 && position.getY() <= 3905) 
                    return 37;
                if (position.getX() >= 3465 && position.getX() <= 3520 && position.getY() >= 3266 && position.getY() <= 3309) 
                    return 38;
                if (position.getX() >= 3585 && position.getX() <= 3705 && position.getY() >= 3462 && position.getY() <= 3539) 
                    return 39;
                if (position.getX() >= 2985 && position.getX() <= 3064 && position.getY() >= 3164 && position.getY() <= 3261) 
                    return 40;
                if (position.getX() >= 2913 && position.getX() <= 2989 && position.getY() >= 3185 && position.getY() <= 3267) 
                    return 41;
                if (position.getX() >= 2639 && position.getX() <= 2740 && position.getY() >= 3391 && position.getY() <= 3503) 
                    return 42;
                if (position.getX() >= 2816 && position.getX() <= 2879 && position.getY() >= 2946 && position.getY() <= 3007) 
                    return 43;
                if (position.getX() >= 2874 && position.getX() <= 2934 && position.getY() >= 3390 && position.getY() <= 3492) 
                    return 44;
                if (position.getX() >= 2413 && position.getX() <= 2491 && position.getY() >= 3386 && position.getY() <= 3515) 
                    return 45;
                if (position.getX() >= 2431 && position.getX() <= 2495 && position.getY() >= 5117 && position.getY() <= 5180) 
                    return 46;
                if (position.getX() >= 3168 && position.getX() <= 3291 && position.getY() >= 3349 && position.getY() <= 3514) 
                    return 47;
                if (position.getX() >= 2532 && position.getX() <= 2621 && position.getY() >= 3071 && position.getY() <= 3112) 
                    return 48;
                if (position.getX() >= 2368 && position.getX() <= 2430 && position.getY() >= 3073 && position.getY() <= 3135) 
                    return 49;
                if (position.getX() >= 2440 && position.getX() <= 2444 && position.getY() >= 3083 && position.getY() <= 3095) 
                    return 50;
                if (position.getX() >= 2359 && position.getX() <= 2440 && position.getY() >= 9466 && position.getY() <= 9543) 
                    return 51;
                if (position.getX() >= 2251 && position.getX() <= 2295 && position.getY() >= 4675 && position.getY() <= 4719) 
                    return 52;
                if (position.getX() >= 3463 && position.getX() <= 3515 && position.getY() >= 9469 && position.getY() <= 9524) 
                    return 53;
                if (position.getX() >= 3200 && position.getX() <= 3303 && position.getY() >= 3273 && position.getY() <= 3353) 
                    return 54;;
                if (position.getX() >= 3274 && position.getX() <= 3328 && position.getY() >= 3315 && position.getY() <= 3353) 
                    return 55;
                if (position.getX() >= 3274 && position.getX() <= 3266 && position.getY() >= 3323 && position.getY() <= 3327) 
                    return 56;
                if (position.getX() >= 3274 && position.getX() <= 3200 && position.getY() >= 3323 && position.getY() <= 3265) 
                    return 57;
                if (position.getX() >= 3324 && position.getX() <= 3263 && position.getY() >= 3408 && position.getY() <= 3285) 
                    return 58;
                if (position.getX() >= 3324 && position.getX() <= 3286 && position.getY() >= 3408 && position.getY() <= 3327) 
                    return 59;
                if (position.getX() >= 3136 && position.getX() <= 3136 && position.getY() >= 3193 && position.getY() <= 3199) 
                    return 60; 
                if (position.getX() >= 3121 && position.getX() <= 3200 && position.getY() >= 3199 && position.getY() <= 3268) 
                    return 61; 
                if (position.getX() >= 3121 && position.getX() <= 3269 && position.getY() >= 3199 && position.getY() <= 3314)
                    return 62;
                if (position.getX() >= 3066 && position.getX() <= 3315 && position.getY() >= 3147 && position.getY() <= 3394) 
                    return 63;
                if (position.getX() >= 3200 && position.getX() <= 3354 && position.getY() >= 3315 && position.getY() <= 3394) 
                    return 64;
                if (position.getX() >= 3248 && position.getX() <= 3395 && position.getY() >= 3328 && position.getY() <= 3468) 
                    return 65;
                if (position.getX() >= 3111 && position.getX() <= 3469 && position.getY() >= 3264 && position.getY() <= 3524) 
                    return 66;
                if (position.getX() >= 3265 && position.getX() <= 3469 && position.getY() >= 3328 && position.getY() <= 3524) 
                    return 67;
                if (position.getX() >= 3329 && position.getX() <= 3447 && position.getY() >= 3418 && position.getY() <= 3524) 
                    return 68;
                if (position.getX() >= 2889 && position.getX() <= 3265 && position.getY() >= 2940 && position.getY() <= 3324) 
                    return 69;
                if (position.getX() >= 3014 && position.getX() <= 3261 && position.getY() >= 3065 && position.getY() <= 3324) 
                    return 70; 
                if (position.getX() >= 2880 && position.getX() <= 3325 && position.getY() >= 2935 && position.getY() <= 3394) 
                    return 71; 
            return 0;
        }

}