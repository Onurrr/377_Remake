package com.rs2.model.npcs.drop;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.rs2.model.players.item.Item;
import com.rs2.util.Misc;
import com.rs2.util.XStreamUtil;

/**
 * Controls all the NPC drops in game.
 * 
 */

public class NpcDropController {

	/**
	 * The map containing all the npc drops. ;)
	 */
	private static Map<Integer, NpcDropController> dropControllers = null;

	@SuppressWarnings("unchecked")
	public static void init() throws FileNotFoundException {
		List<NpcDropController> list = (List<NpcDropController>) XStreamUtil.getxStream().fromXML(new FileInputStream("data/npcs/npcDrops.xml"));
		dropControllers = new HashMap<Integer, NpcDropController>();
		for (NpcDropController npcDrop : list) {
			for (int id : npcDrop.getNpcIds()) {
				dropControllers.put(id, npcDrop);
			}
		}
		System.out.println("Loaded " + dropControllers.size() + " npc drops.");
	}

	/**
	 * The logger instance.
	 */
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(NpcDropController.class.getName());

	/**
	 * The id's of the NPC's that "owns" this class.
	 */
	private int[] npcIds;

	/**
	 * All the drops that belongs to this class.
	 */
	private NpcDropItem[] drops;
	
	private NpcDropItem[] common;
	private NpcDropItem[] uncommon;
	private NpcDropItem[] rare;
	private NpcDropItem[] superrare;

	
	  private static int superChance = 200;
	private static int rareChance = 101;
	private static int uncommonChance = 65;
	 
	
	//private static int superChance = 200; 105
//	private static int rareChance = 101;45
	//private static int uncommonChance = 65;10
	/**
	 * Gets the NPC drop controller by an id.
	 * 
	 * @return The NPC drops associated with this id.
	 */
	public static NpcDropController forId(int id) {
		return dropControllers.get(id);
	}

	public NpcDropItem[] getDropList() {
		return drops;
	}

	/**
	 * Gets an array of all the items an NPC should drop. This will get 100%
	 * drops as well.
	 * 
	 * @return An array of the items this NPC should drop.
	 */
	public Item[] getDrops() {
		if (drops == null) {
			return null;
		}
		NpcDropItem drop = null;
		int length = 1;
		for (NpcDropItem item : drops) {
			if (item.shouldAlwaysDrop()) {
				length++;
			}
		}
		Item dropItem = null;
		if (superrare != null && Misc.random(superChance) == 0) {
			drop = superrare[Misc.randomMinusOne(superrare.length)];
		} else if (rare != null && Misc.random(rareChance) == 0) {
			drop = rare[Misc.randomMinusOne(rare.length)];
		} else if (uncommon != null && Misc.random(uncommonChance) == 0) {
			drop = uncommon[Misc.randomMinusOne(uncommon.length)];
		} else if (common != null) {
			drop = common[Misc.randomMinusOne(common.length)];
		} else {
			length--;
		}
		if (drop != null) {
			if (drop.getCount().length == 1) {
				dropItem = new Item(drop.getId(), drop.getCount()[0]);
			} else if (drop.getCount().length == 2) {
				dropItem = new Item(drop.getId(), Misc.random(drop.getCount()[1] - drop.getCount()[0]) + drop.getCount()[0]);
			} else {
				dropItem = new Item(drop.getId(), drop.getCount()[Misc.randomMinusOne(drop.getCount().length)]);
			}
		}
		Item[] toReturn = new Item[length];
		int index = 0;
		for (NpcDropItem item : drops) {
			if (item.shouldAlwaysDrop()) {
				toReturn[index++] = item.getItem();
			}
		}
		if (dropItem != null) {
			toReturn[index] = dropItem;
		}
		return toReturn;
	}

	public void setDrop() {
		int c = 0, u = 0, r = 0, s = 0;
		for (NpcDropItem item : drops) {
			if (item.getChance() == 2) {
				c++;
			} else if (item.getChance() == 3) {
				u++;
			} else if (item.getChance() == 4 || item.getChance() == 6 || item.getChance() == 8) {
				r++;
			} else if (item.getChance() == 5) {
				s++;
			}
		}
		int c2 = 0, u2 = 0, r2 = 0, s2 = 0;
		common = c > 0 ? new NpcDropItem[c] : null;
		uncommon = u > 0 ? new NpcDropItem[u] : null;
		rare = r > 0 ? new NpcDropItem[r] : null;
		superrare = s > 0 ? new NpcDropItem[s] : null;
		for (NpcDropItem item : drops) {
			NpcDropItem drop = item;
			if ((drop.getCount().length > 1 || drop.getCount()[0] > 1) && !drop.getItem().getDefinition().isStackable() && drop.getItem().getDefinition().isNoteable()) {
				drop.setNoted();
			}
			if (item.getChance() == 2) {
				common[c2] = drop;
				c2++;
			} else if (item.getChance() == 3) {
				uncommon[u2] = drop;
				u2++;
			} else if (item.getChance() == 4 || item.getChance() == 6 || item.getChance() == 8) {
				rare[r2] = drop;
				r2++;
			} else if (item.getChance() == 5) {
				superrare[s2] = drop;
				s2++;
			}
		}
	}

	/**
	 * Gets the id's of the NPC's this class belongs to.
	 * 
	 * @return The id's of the NPC's.
	 */
	public int[] getNpcIds() {
		return npcIds;
	}

	/**
	 * Gets the id's of the NPC's this class belongs to.
	 * 
	 * @return The id's of the NPC's.
	 */
	public int getNpcId() {
		return npcIds[0];
	}
}
