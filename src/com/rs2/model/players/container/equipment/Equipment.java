package com.rs2.model.players.container.equipment;

import java.text.DecimalFormat;

import com.rs2.Constants;
import com.rs2.model.content.Following;
import com.rs2.model.content.WalkInterfaces;
import com.rs2.model.content.combat.special.SpecialType;
import com.rs2.model.content.combat.weapon.Weapon;
import com.rs2.model.content.minigames.duelarena.RulesData;
import com.rs2.model.content.skills.Skill;
import com.rs2.model.content.skills.runecrafting.Tiaras;
import com.rs2.model.players.Player;
import com.rs2.model.players.container.Container;
import com.rs2.model.players.container.Container.Type;
import com.rs2.model.players.item.Item;
import com.rs2.model.players.item.ItemDefinition;
import com.rs2.model.players.item.ItemManager;
import com.rs2.util.Misc;

public class Equipment {

	public static final int EQUIPMENT_INTERFACE = 1688;
	public static final int SIZE = 14;
	public static final int ACCURATE = 0;
	public static final int AGGRESSIVE = 1;
	public static final int CONTROLLED = 2;
	public static final int DEFENSIVE = 3;
	public int walkAnim;

	private int attackLevelReq, strengthLevelReq, defenceLevelReq, rangeLevelReq, magicLevelReq, prayerLevelReq, slayerLevelReq;

	private Player player;

	private static String[] hideArms = {"robe", "top", "shirt", "platebody", "brassard", "dragon chainbody"};
	private static String[] hideHairAndBeard = {"spiny helmet", "initiate helm", "full helm", "saradomin full", "veracs helm", "guthans helm", "torags helm", "saradomin helm"};
	private static String[] hideHair = {"cowl", "camel", "bandana", "decorative helm", "med helm", "coif", "hood", "bandanna", "berserker helm", "archer helm", "farseer helm", "warrior helm", "skeletal", "dharoks helm", "mask"};
	private static String[] hideBeard = {"facemask", "mime mask"};

	private Container itemContainer = new Container(Type.STANDARD, 14) {
		@Override
		public void clear() {
			super.clear();
			player.getEquipment().updateWeight();
		}
	};

	public Equipment(Player player) {
		this.player = player;
	}

	public void sendEquipmentOnLogin() {
		refresh();
	}

	public static boolean hideArms(int id) {
		String item = new Item(id).getDefinition().getName().toLowerCase();
		if (id == 426 || id == 544 || id == 6107) {
			return true;
		}
		for (String element : hideArms) {
			if (item.contains(element)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hideHairAndBeard(int id) {
		String item = new Item(id).getDefinition().getName().toLowerCase();
		for (String element : hideHairAndBeard) {
			if (item.contains(element)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hideHair(int id) {
		String item = new Item(id).getDefinition().getName().toLowerCase();
		for (String element : hideHair) {
			if (item.contains(element) && id != 2581 && id != 2631 && id != 3057 && id != 4164) {
				return true;
			}
		}
		return false;
	}

	public static boolean hideBeard(int id) {
		String item = new Item(id).getDefinition().getName().toLowerCase();
		for (String element : hideBeard) {
			if (item.contains(element)) {
				return true;
			}
		}
		return false;
	}

	public void refresh() {
		Item[] items = itemContainer.toArray();
		player.getActionSender().sendUpdateItems(EQUIPMENT_INTERFACE, items);
		player.getEquipment().checkRangeGear();
		player.getEquipment().checkBarrowsGear();
		player.setEquippedWeapon(Weapon.getWeapon(items[Constants.WEAPON]));
		sendBonus(player);
		sendWeaponInterface();
	}

	public void refresh(int slot, Item item) {
		player.getActionSender().sendUpdateItem(slot, EQUIPMENT_INTERFACE, item);
		player.getEquipment().checkRangeGear();
		player.getEquipment().checkBarrowsGear();
		sendBonus(player);
		sendWeaponInterface();
	}

	public void replaceEquipment(int id, int slot) {
		Item currentEquip = new Item(player.getEquipment().getId(slot));
		Item newItem = new Item(id);
		itemContainer.remove(currentEquip, slot);
		itemContainer.set(slot, newItem);
		refresh();
		player.setSpecialAttackActive(false);
		sendWeaponInterface();
		player.setInstigatingAttack(false);
		Following.resetFollow(player);
		player.getInventory().refresh();
		player.setAppearanceUpdateRequired(true);
		player.getAttributes().put("usedGlory", Boolean.FALSE);
	}

	public void equip(int slot) {
		Item item = player.getInventory().getItemContainer().get(slot);
		if (item == null) {
			return;
		}
		int equipSlot = item.getDefinition().getSlot();
        if(!player.getInventory().playerHasItem(item))
            return;
		if (!checkRequirements(item.getId(), equipSlot)) {
			return;
		}
		//if (!player.canTeleport()) {
		//	return;
		//}
		if (player.getNewComersSide().getTutorialIslandStage() < 38) {
			player.getDialogue().sendStatement("You haven't learned how to wield items yet!");
			return;
		}
		if (player.getNewComersSide().getTutorialIslandStage() == 42) {
			if ((item.getId() == 1171 && player.getEquipment().getItemContainer().contains(1277) || (item.getId() == 1277 && player.getEquipment().getItemContainer().contains(1171))))
				player.getNewComersSide().setTutorialIslandStage(player.getNewComersSide().getTutorialIslandStage() + 1, true);
		}
		if (item.getId() == 772 && !player.hasKilledTreeSpirit()) {
			player.getDialogue().sendStatement("You need to kill the tree spirit in Entrana to weild this.");
			return;
		}
		if (item.getId() == 1205 && player.getNewComersSide().getTutorialIslandStage() == 40)
			player.getNewComersSide().setTutorialIslandStage(player.getNewComersSide().getTutorialIslandStage() + 1, true);
        if (player.inDuelArena()) {
            for (int funWeapon : Constants.FUN_WEAPONS) {
                if (!RulesData.FUN_WEAPON.activated(player) && item.getId() == funWeapon) {
                    player.getActionSender().sendMessage("Usage of 'Fun weapons' haven't been enabled during this fight!");
                    return;
                }
            }
        }
		boolean disabled = false;
		switch (equipSlot) {
			case Constants.HAT :
				disabled = RulesData.NO_HEAD.activated(player);
				break;
			case Constants.CAPE :
				disabled = RulesData.NO_CAPE.activated(player);
				break;
			case Constants.AMULET :
				disabled = RulesData.NO_AMULET.activated(player);
				break;
			case Constants.ARROWS :
				disabled = RulesData.NO_ARROW.activated(player);
				break;
			case Constants.WEAPON :
				disabled = RulesData.NO_WEAPON.activated(player) || (item.getDefinition().isTwoHanded() && RulesData.NO_SHIELD.activated(player));
				break;
			case Constants.CHEST :
				disabled = RulesData.NO_BODY.activated(player);
				break;
			case Constants.SHIELD :
				disabled = RulesData.NO_SHIELD.activated(player);
				break;
			case Constants.LEGS :
				disabled = RulesData.NO_LEGS.activated(player);
				break;
			case Constants.HANDS :
				disabled = RulesData.NO_GLOVES.activated(player);
				break;
			case Constants.FEET :
				disabled = RulesData.NO_BOOTS.activated(player);
				break;
			case Constants.RING :
				disabled = RulesData.NO_RINGS.activated(player);
				break;
		}
		if (disabled) {
			player.getActionSender().sendMessage("You cannot wear this during this fight!");
			return;
		}
		boolean removedItem = false;
		if (item.getDefinition().isStackable()) {
			int slotType = equipSlot;
			Item equipItem = itemContainer.get(slotType);
			player.getInventory().removeItemSlot(item, slot);
			removedItem = true;
			if (itemContainer.get(slotType) != null) {
				if (item.getId() == equipItem.getId()) {
					itemContainer.set(slotType, new Item(item.getId(), item.getCount() + equipItem.getCount()));
				} else {
					player.getInventory().addItemToSlot(equipItem, slot);
					itemContainer.set(slotType, item);
				}
			} else {
				itemContainer.set(slotType, item);
			}
		} else {
			int slotType = equipSlot;
			if (slotType == Constants.WEAPON) {
				if (item.getDefinition().isTwoHanded()) {
					if (itemContainer.get(Constants.WEAPON) != null && itemContainer.get(Constants.SHIELD) != null && player.getInventory().getItemContainer().freeSlot() == -1) {
						player.getActionSender().sendMessage("Not enough space in your inventory.");
						return;
					}
					player.getInventory().removeItemSlot(item, slot);
					removedItem = true;
					unequip(Constants.SHIELD);
					if (itemContainer.get(Constants.SHIELD) != null) {
						return;
					}
				}
			}
			if (slotType == Constants.SHIELD && itemContainer.get(Constants.WEAPON) != null) {
				if (itemContainer.get(Constants.WEAPON).getDefinition().isTwoHanded()) {
					player.getInventory().removeItemSlot(item, slot);
					removedItem = true;
					unequip(Constants.WEAPON);
					if (itemContainer.get(Constants.WEAPON) != null) {
						return;
					}
				}
			}
			if (itemContainer.get(slotType) != null) {
				Item equipItem = itemContainer.get(slotType);
				if (!removedItem) {
					player.getInventory().removeItemSlot(item, slot);
					player.getInventory().addItemToSlot(equipItem, slot);
				} else {
					player.getInventory().addItem(equipItem);
				}
			} else {
				player.getInventory().removeItemSlot(item, slot);
			}
			itemContainer.set(slotType, new Item(item.getId(), item.getCount()));
		}
		player.getTask();
		player.setSpecialAttackActive(false);
		Following.resetFollow(player);
		player.getInventory().refresh();
		if (equipSlot == Constants.WEAPON) {
			player.setEquippedWeapon(Weapon.getWeapon(item));
			player.setSpecialType(SpecialType.getSpecial(item));
			player.setAutoSpell(null);
		}
		refresh();
		updateWeight();
		player.getAttributes().put("usedGlory", Boolean.FALSE);
		if (item.getId() == 6583 || item.getId() == 7927) {
			player.transformNpc = item.getId() == 6583 ? 2626 : 3689 + Misc.random(5);
			player.setAppearanceUpdateRequired(true);
			player.getActionSender().hideAllSideBars();
			player.getActionSender().sendSidebarInterface(3, 6014);
		}
        WalkInterfaces.checkChickenOption(player);
		Tiaras.handleTiara(player, item.getId());
		player.setAppearanceUpdateRequired(true);
	}

	public void unequip(int slot) {
		Item item = itemContainer.get(slot);
		if (item == null) {
			return;
		}
        if (player.getInventory().getItemContainer().freeSlot() == -1) {
            player.getActionSender().sendMessage("Not enough space in your inventory.");
            return;
        }
		if (item.getId() == 6583 || item.getId() == 7927) {
			player.transformNpc = -1;
			player.getActionSender().sendSideBarInterfaces();
			player.setAppearanceUpdateRequired(true);
		}
		if (slot == Constants.HAT) {
			Tiaras.handleTiara(player, -1);
		}
        if(!player.getEquipment().getItemContainer().contains(item.getId()))
            return;
		player.getTask();
		itemContainer.remove(item, slot);
		player.getInventory().addItem(new Item(item.getId(), item.getCount()));
		if (slot == Constants.WEAPON) {
			player.setEquippedWeapon(null);
           player.setSpecialType(null);
			player.setAutoSpell(null);
		}
		refresh(slot, new Item(-1, 0));
		updateWeight();
        WalkInterfaces.checkChickenOption(player);
		player.setAppearanceUpdateRequired(true);
	}

	public void updateWeight() {
		double totalWeight = 0;
		for (int i = 0; i < 11; i++)
			if (player.getEquipment().getItemContainer().get(i) != null)
				totalWeight += ItemDefinition.getWeight(player.getEquipment().getItemContainer().get(i).getId());

		for (int i = 0; i < 28; i++)
			if (player.getInventory().getItemContainer().get(i) != null) {
				totalWeight += ItemDefinition.getWeight(player.getInventory().getItemContainer().get(i).getId());
				if (player.getInventory().getItemContainer().get(i).getId() == 88)// boots
																					// of
																					// lightness
					totalWeight += 4.8;
			}

		player.totalWeight = totalWeight;
		player.getActionSender().sendWeight();
		// player.getActionSender().sendString((int)
		// Math.floor(player.totalWeight) + "kg", 184);

	}

	public void removeAmount(int slot, int amount) {
		Item item = itemContainer.get(slot);
		if (item == null) {
			return;
		}
		int total = itemContainer.remove(item, slot) - amount;
		if (total > 0) {
			Item newItem = new Item(item.getId(), total);
			itemContainer.add(newItem, slot);
			//refresh(slot, newItem);
		} else {
			itemContainer.add(new Item(-1, 0), slot);
			//itemContainer.remove(item);
			//refresh(slot, new Item(-1, 0));
		}
		refresh();
		player.setAppearanceUpdateRequired(true);
		sendBonus(player);
	}

	public void setBonus(Player player) {
		for (int i = 0; i < 14; i++) {
			if (player.getEquipment().getItemContainer().get(i) == null || i == 13) {
				continue;
			}
			Item item = player.getEquipment().getItemContainer().get(i);
			for (int bonus = 0; bonus < 12; bonus++) {
				player.setBonuses(bonus, (int) (item.getDefinition().getBonuses()[bonus] + player.getBonuses().get(bonus)));

			}
		}
	}

	public void sendBonus(Player player) {
		int offset = 0;
		String send = "";
		for (int i = 0; i < 12; i++) {
			player.setBonuses(i, 0);
		}
		setBonus(player);
		for (int i = 0; i < 12; i++) {
			if (player.getBonuses().get(i) >= 0) {
				send = Constants.BONUS_NAME[i] + ": +" + player.getBonuses().get(i);
			} else {
				send = Constants.BONUS_NAME[i] + ": -" + Math.abs(player.getBonuses().get(i));
			}
			if (i == 10) {
				offset = 1;
			}
			player.getActionSender().sendString(send, (1675 + i + offset));
		}
	}

	/**
	 * Wear Item
	 **/

	public void sendWeaponInterface() {
		Weapon weapon = Weapon.getWeapon(getItemContainer().get(Constants.WEAPON));
		player.getActionSender().sendSidebarInterface(0, weapon.getWeaponInterface().getInterfaceId());
        player.getActionSender().sendString(getItemContainer().get(Constants.WEAPON) == null ? "Unarmed" : getItemContainer().get(Constants.WEAPON).getDefinition().getName(), weapon.getWeaponInterface().weaponNameChild());
        adjustFightMode(weapon);
        player.getActionSender().sendConfig(43, player.getFightMode());
    	if(weapon.getWeaponInterface().weaponDisplayChild() != -1)
        	player.getActionSender().sendItemOnInterface(weapon.getWeaponInterface().weaponDisplayChild(), 200, getItemContainer().get(Constants.WEAPON).getId());
        if(weapon.getWeaponInterface().getSpecialBarId() != -1)
	        if(SpecialType.getSpecial(getItemContainer().get(Constants.WEAPON)) == null)
	            player.getActionSender().sendFrame171(1, weapon.getWeaponInterface().getSpecialBarId());
	        else {
	            //System.out.println(weapon.getWeaponInterface().getSpecialBarId());
	            player.getActionSender().sendFrame171(0, weapon.getWeaponInterface().getSpecialBarId());
	            player.updateSpecialBar();
	        }

    }

	public void adjustFightMode(Weapon weapon) {
		if (weapon.getAttackAnimations().length < 4 && player.getFightMode() == 3)  {
			player.setFightMode(2);
		}
	}

    public boolean setFightMode(int buttonId){
        Weapon weapon = Weapon.getWeapon(getItemContainer().get(Constants.WEAPON));
        for(int i = 0; i < weapon.getWeaponInterface().getAttackStyles().length; i++){
            if(buttonId == weapon.getWeaponInterface().getAttackStyles()[i].getButtonId()) {
            	player.disableAutoCast();
                player.setFightMode(i);
                player.resetAllActions();
                //player.setFollowDistance(DistanceCheck.getDistanceForCombatType(player));
                //player.resetActions();
                return true;
            }
        }
        return false;
    }


	public int getId(int slot) {
		Item item = getItemContainer().get(slot);
		if (item != null) {
			return item.getId();
		}
		return 0;
	}

	public int getStandAnim() {
		return player.getEquippedWeapon().getMovementAnimations()[0];
	}

	public int getWalkAnim() {
		return player.getEquippedWeapon().getMovementAnimations()[1];
	}

	public int getRunAnim() {
		return player.getEquippedWeapon().getMovementAnimations()[2];
	}

	public Container getItemContainer() {
		return itemContainer;
	}

	public int getRangeLevelReq() {
		return rangeLevelReq;
	}

	public int getSlayerLevelReq() {
		return slayerLevelReq;
	}

	public class AttackStyles {

		private int accurate = 422;
		private int aggressive = 423;
		private int controlled = 422;
		private int defensive = 422;

		public int get(int style) {
			switch (style) {
				case 0 :
					return accurate;
				case 1 :
					return aggressive;
				case 2 :
					return controlled;
				case 3 :
					return defensive;
			}
			return accurate;
		}
	}

	public boolean checkRequirements(int itemId, int targetSlot) {
		getRequirements(itemId);
		if (targetSlot == Constants.WEAPON) {
			if (attackLevelReq > 0) {
				if (player.getSkill().getPlayerLevel(Skill.ATTACK) < attackLevelReq) {
					player.getActionSender().sendMessage("You need an Attack level of " + attackLevelReq + " to wield this weapon.");
					return false;
				}
			}
			if (strengthLevelReq > 0) {
				if (player.getSkill().getPlayerLevel(Skill.STRENGTH) < strengthLevelReq) {
					player.getActionSender().sendMessage("You need a Strength level of " + strengthLevelReq + " to wear this weapon.");
					return false;
				}
			}
			if (rangeLevelReq > 0) {
				if (player.getSkill().getPlayerLevel(Skill.RANGED) < rangeLevelReq) {
					player.getActionSender().sendMessage("You need a Ranged level of " + rangeLevelReq + " to wield this weapon.");
					return false;
				}
			}
			if (magicLevelReq > 0) {
				if (player.getSkill().getPlayerLevel(Skill.MAGIC) < magicLevelReq) {
					player.getActionSender().sendMessage("You need a Magic level of " + magicLevelReq + " to wield this weapon.");
					return false;
				}
			}
			if (slayerLevelReq > 0) {
				if (player.getSkill().getPlayerLevel(Skill.SLAYER) < slayerLevelReq) {
					player.getActionSender().sendMessage("You need a Slayer level of " + slayerLevelReq + " to wield this weapon.");
					return false;
				}
			}
		} else if (targetSlot == Constants.FEET || targetSlot == Constants.LEGS || targetSlot == Constants.SHIELD || targetSlot == Constants.CHEST || targetSlot == Constants.HAT || targetSlot == Constants.HANDS) {
			if (defenceLevelReq > 0) {
				if (player.getSkill().getPlayerLevel(Skill.DEFENCE) < defenceLevelReq) {
					player.getActionSender().sendMessage("You need a Defence level of " + defenceLevelReq + " to wear this item.");
					return false;
				}
			}
			if (rangeLevelReq > 0) {
				if (player.getSkill().getPlayerLevel(Skill.RANGED) < rangeLevelReq) {
					player.getActionSender().sendMessage("You need a Ranged level of " + rangeLevelReq + " to wear this item.");
					return false;
				}
			}
			if (magicLevelReq > 0) {
				if (player.getSkill().getPlayerLevel(Skill.MAGIC) < magicLevelReq) {
					player.getActionSender().sendMessage("You need a Magic level of " + magicLevelReq + " to wear this item.");
					return false;
				}
			}
			if (slayerLevelReq > 0) {
				if (player.getSkill().getPlayerLevel(Skill.SLAYER) < slayerLevelReq) {
					player.getActionSender().sendMessage("You need a Slayer level of " + slayerLevelReq + " to wear this item.");
					return false;
				}
			}
		} else if (targetSlot == Constants.CAPE) {
			if (defenceLevelReq > 0) {
				if (player.getSkill().getPlayerLevel(Skill.DEFENCE) < defenceLevelReq) {
					player.getActionSender().sendMessage("You need a Defence level of " + defenceLevelReq + " to wear this item.");
					return false;
				}
			}
			if (attackLevelReq > 0) {
				if (player.getSkill().getPlayerLevel(Skill.ATTACK) < attackLevelReq) {
					player.getActionSender().sendMessage("You need an Attack level of " + attackLevelReq + " to wear this item.");
					return false;
				}
			}
			if (strengthLevelReq > 0) {
				if (player.getSkill().getPlayerLevel(Skill.STRENGTH) < strengthLevelReq) {
					player.getActionSender().sendMessage("You need a Strength level of " + strengthLevelReq + " to wear this item.");
					return false;
				}
			}
			if (rangeLevelReq > 0) {
				if (player.getSkill().getPlayerLevel(Skill.RANGED) < rangeLevelReq) {
					player.getActionSender().sendMessage("You need a Ranged level of " + rangeLevelReq + " to wear this item.");
					return false;
				}
			}
			if (magicLevelReq > 0) {
				if (player.getSkill().getPlayerLevel(Skill.MAGIC) < magicLevelReq) {
					player.getActionSender().sendMessage("You need a Magic level of " + magicLevelReq + " to wear this item.");
					return false;
				}
			}
			if (prayerLevelReq > 0) {
				if (player.getSkill().getPlayerLevel(Skill.PRAYER) < prayerLevelReq) {
					player.getActionSender().sendMessage("You need a Prayer level of " + prayerLevelReq + " to wear this item.");
					return false;
				}
			}
			if (slayerLevelReq > 0) {
				if (player.getSkill().getPlayerLevel(Skill.SLAYER) < slayerLevelReq) {
					player.getActionSender().sendMessage("You need a Slayer level of " + slayerLevelReq + " to wear this item.");
					return false;
				}
			}
		} else if (targetSlot == Constants.ARROWS) {
			if (slayerLevelReq > 0) {
				if (player.getSkill().getPlayerLevel(Skill.SLAYER) < slayerLevelReq) {
					player.getActionSender().sendMessage("You need a Slayer level of " + slayerLevelReq + " to equip this ammo.");
					return false;
				}
			}
		}
		return true;
	}

	public void getRequirements(int itemId) {
		String itemName = ItemManager.getInstance().getItemName(itemId).toLowerCase();
		attackLevelReq = defenceLevelReq = strengthLevelReq = rangeLevelReq = magicLevelReq = slayerLevelReq = 0;
		switch (itemId) {
			case 6524 : // obby shield
				defenceLevelReq = 60;
				return;
			case 6522 : // obby rings
				rangeLevelReq = 60;
				return;
			case 6523 : // obby sword
			case 6525 : // obby knife
			case 6527 : // obby mace
				attackLevelReq = 60;
				return;
			case 6526 : // obby staff
				attackLevelReq = 60;
				magicLevelReq = 60;
				return;
			case 4164 : // facemask
				slayerLevelReq = 10;
				return;
			case 4166 : // earmuffs
				slayerLevelReq = 15;
				return;
			case 4156 : // mirror shield
				slayerLevelReq = 25;
				defenceLevelReq = 20;
				return;
			case 7051 : // bug latern
			case 7053 :
				slayerLevelReq = 33;
				return;
			case 7159 : // slayer boots
				slayerLevelReq = 37;
				return;
			case 6708 : // slayer gloves
				slayerLevelReq = 42;
				return;
			case 4158 : // leaf-bladed spear
				slayerLevelReq = 55;
				return;
			case 4168 : // nose peg
				slayerLevelReq = 60;
				return;
			case 4170 : // slayer staff
				slayerLevelReq = 55;
				return;
			case 6528 : // obby maul
				strengthLevelReq = 60;
				return;
			case 6724 : // seercull
				rangeLevelReq = 50;
				return;
			case 6889 :
			case 6914 :
				magicLevelReq = 60;
				return;
			case 2503 :
				rangeLevelReq = 70;
				defenceLevelReq = 40;
				return;
			case 2501 :
				rangeLevelReq = 60;
				defenceLevelReq = 40;
				return;
			case 2499 :
				rangeLevelReq = 50;
				defenceLevelReq = 40;
				return;
			case 1135 :
				rangeLevelReq = 40;
				defenceLevelReq = 40;
				return;
			case 3751 :
			case 3749 :
			case 3755 :
				defenceLevelReq = 45;
				return;
			case 7460 :
				defenceLevelReq = 20;
				return;
			case 837 :
				rangeLevelReq = 61;
				return;
			case 4151 :
				attackLevelReq = 70;
				return;
			case 4153 :
				attackLevelReq = 50;
				strengthLevelReq = 50;
				return;
			case 2665 : // sara full
			case 2661 : // sara plate
			case 2663 : // sara legs
			case 2667 : // sara kite
			case 3479 : // sara skirt
				defenceLevelReq = 40;
				return;
		}
		if (itemName.contains("crystal")) {
			if (itemName.contains("shield")) {
				defenceLevelReq = 70;
			} else {
				rangeLevelReq = 70;
			}
			return;
		}
		if (itemName.contains("ahrim")) {
			magicLevelReq = 70;
			if (!itemName.contains("staff")) {
				attackLevelReq = 70;
			} else {
				defenceLevelReq = 70;
			}
			return;
		}
		if (itemName.contains("karil")) {
			rangeLevelReq = 70;
			if (!itemName.contains("crossbow")) {
				defenceLevelReq = 70;
			}
			return;
		}
		if (itemName.contains("verac") || itemName.contains("guthan") || itemName.contains("dharok") || itemName.contains("torag")) {
			if (itemName.contains("hammers") || itemName.contains("axe")) {
				attackLevelReq = 70;
				strengthLevelReq = 70;
			} else if (itemName.contains("warspear") || itemName.contains("flail")) {
				attackLevelReq = 70;
			} else {
				defenceLevelReq = 70;
			}
			return;
		}
		if (itemName.contains("arrow")) {
			if (itemName.contains("steel")) {
				rangeLevelReq = 5;
			} else if (itemName.contains("mithril")) {
				rangeLevelReq = 20;
			} else if (itemName.contains("adamant")) {
				rangeLevelReq = 20;
			} else if (itemName.contains("rune") || itemName.contains("ice")) {
				rangeLevelReq = 40;
			} else if (itemName.contains("broad")) {
				rangeLevelReq = 50;
				slayerLevelReq = 55;
			}
			return;
		}
		if (itemName.contains("bow")) {
			if (itemName.contains("oak")) {
				rangeLevelReq = 5;
			} else if (itemName.contains("willow")) {
				rangeLevelReq = 20;
			} else if (itemName.contains("maple") || itemName.contains("ogre")) {
				rangeLevelReq = 30;
			} else if (itemName.contains("yew")) {
				rangeLevelReq = 40;
			} else if (itemName.contains("magic")) {
				rangeLevelReq = 50;
			}
			return;
		}
		if (itemName.contains("dart") || itemName.contains("javelin") || itemName.contains("thrownaxe") || itemName.contains("knife")) {
			if (itemName.contains("steel")) {
				rangeLevelReq = 5;
			} else if (itemName.contains("black")) {
				rangeLevelReq = 10;
			} else if (itemName.contains("mithril")) {
				rangeLevelReq = 20;
			} else if (itemName.contains("adam")) {
				rangeLevelReq = 30;
			} else if (itemName.contains("rune")) {
				rangeLevelReq = 40;
			}
			return;
		}
		if (itemName.contains("mystic") || itemName.contains("nchanted")) {
			if (itemName.contains("staff")) {
				magicLevelReq = 20;
				attackLevelReq = 40;
			} else {
				magicLevelReq = 20;
				defenceLevelReq = 20;
			}
			return;
		}
		if (itemName.contains("infinity")) {
			magicLevelReq = 50;
			defenceLevelReq = 25;
			return;
		}
		if (itemName.contains("splitbark")) {
			magicLevelReq = 40;
			defenceLevelReq = 40;
			return;
		}
		if (itemName.contains("black d'hide")) {
			rangeLevelReq = 70;
			return;
		}
		if (itemName.contains("tzhaar-ket-om")) {
			strengthLevelReq = 60;
			return;
		}
		if (itemName.contains("red d'hide")) {
			rangeLevelReq = 60;
			return;
		}
		if (itemName.contains("blue d'hide")) {
			rangeLevelReq = 50;
			return;
		}
		if (itemName.contains("green d'hide")) {
			rangeLevelReq = 40;
			return;
		}
		if (itemName.contains("snakeskin")) {
			rangeLevelReq = 40;
			defenceLevelReq = 30;
			return;
		}
		if (itemName.contains("initiate")) {
			defenceLevelReq = 20;
			prayerLevelReq = 10;
			return;
		}
		if (itemName.contains("bronze")) {
			if (!itemName.contains("knife") && !itemName.contains("dart") && !itemName.contains("javelin") && !itemName.contains("thrownaxe")) {
				attackLevelReq = defenceLevelReq = 1;
			}
			return;
		}
		if (itemName.contains("iron")) {
			if (!itemName.contains("knife") && !itemName.contains("dart") && !itemName.contains("javelin") && !itemName.contains("thrownaxe")) {
				attackLevelReq = defenceLevelReq = 1;
			}
			return;
		}
		if (itemName.contains("steel")) {
			if (!itemName.contains("knife") && !itemName.contains("dart") && !itemName.contains("javelin") && !itemName.contains("thrownaxe")) {
				attackLevelReq = defenceLevelReq = 5;
			}
			return;
		}
		if (itemName.contains("black")) {
			if (!itemName.contains("knife") && !itemName.contains("dart") && !itemName.contains("javelin") && !itemName.contains("thrownaxe") && !itemName.contains("vamb") && !itemName.contains("chap") && !itemName.equals("black skirt") && !itemName.contains("robe") && !itemName.contains("beret") && !itemName.contains("cavalier") && !itemName.contains("headband")) {
				attackLevelReq = defenceLevelReq = 10;
			}
			return;
		}
		if (itemName.contains("mithril")) {
			if (!itemName.contains("knife") && !itemName.contains("dart") && !itemName.contains("javelin") && !itemName.contains("thrownaxe")) {
				attackLevelReq = defenceLevelReq = 20;
			}
			return;
		}
		if (itemName.contains("adamant")) {
			if (!itemName.contains("knife") && !itemName.contains("dart") && !itemName.contains("javelin") && !itemName.contains("thrownaxe")) {
				attackLevelReq = defenceLevelReq = 30;
			}
			return;
		}
		if ((itemName.contains("rune") || itemName.contains("gilded") || itemName.contains("zamorak") || itemName.contains("saradomin")) && !itemName.contains("runecraft")) {
			if (!itemName.contains("robe") && !itemName.contains("banner") && !itemName.contains("staff") && !itemName.contains("cape") && !itemName.contains("knife") && !itemName.contains("dart") && !itemName.contains("javelin") && !itemName.contains("thrownaxe") && !itemName.contains("'bow")) {
				attackLevelReq = defenceLevelReq = 40;
			}
			return;
		}
		if (itemName.contains("granite shield")) {
			if (!itemName.contains("maul")) {
				defenceLevelReq = 50;
			}
			return;
		}
		if (itemName.contains("granite maul")) {
			if (!itemName.contains("shield")) {
				attackLevelReq = 50;
			}
			return;
		}
		if (itemName.contains("warrior")) {
			if (!itemName.contains("ring")) {
				defenceLevelReq = 40;
			}
			return;
		}
		if (itemName.contains("enchanted")) {
			defenceLevelReq = 40;
			return;
		}
		if (itemName.contains("dragon dagger") || itemName.contains("drag dagger")) {
			attackLevelReq = 60;
			return;
		}
		if (itemName.contains("ancient")) {
			attackLevelReq = 50;
			return;
		}
		if (itemName.contains("hardleather")) {
			defenceLevelReq = 10;
			return;
		}
		if (itemName.contains("studded")) {
			rangeLevelReq = 20;
			if (!itemName.contains("chaps")) {
				defenceLevelReq = 20;
			}
			return;
		}
		if (itemName.contains("dragon")) {
			if (!itemName.contains("anti")) {
				attackLevelReq = defenceLevelReq = 60;
			}
			return;
		}
	}

	/* some math method */
	public String cutDouble(double Number) {
		double dNumber = Number;
		String masque = new String("#0.##");
		DecimalFormat form = new DecimalFormat(masque);
		return form.format(dNumber).replaceAll(",", ".");
	}

	public void checkBarrowsGear() {
		player.setFullAhrim(fullAhrim());
		player.setFullKaril(fullKaril());
		player.setFullDharok(fullDharok());
		player.setFullVerac(fullVerac());
		player.setFullTorag(fullTorag());
		player.setFullGuthan(fullGuthan());
	}

	public void checkRangeGear() {
		String wep = player.getEquippedWeapon().name().toLowerCase();
		player.setUsingBow((wep.contains("seercull") || wep.contains("bow")) && !wep.contains("crossbow"));
		player.setUsingCross(wep.contains("crossbow") || wep.contains("x-bow"));
		player.setUsingOtherRangedWeapon(wep.contains("knife") || wep.contains("dart") || wep.contains("javelin") || wep.contains("thrownaxe") || wep.contains("toktz-xil-ul") || wep.contains("throwing axe"));
		player.setUsingCrystalBow(wep.contains("crystal bow"));
		String ammo = ItemManager.getInstance().getItemName(player.getEquipment().getId(Constants.ARROWS)).toLowerCase();
		player.setUsingArrows(ammo.contains("arrow"));
		player.setUsingBolts(ammo.contains("bolt"));
		player.setDropArrow(!wep.contains("crystal") && !wep.contains("karils"));
	}

	public boolean fullAhrim() {
		if (player.getEquipment().getItemContainer().get(Constants.HAT) == null || player.getEquipment().getItemContainer().get(Constants.LEGS) == null || player.getEquipment().getItemContainer().get(Constants.CHEST) == null || player.getEquipment().getItemContainer().get(Constants.WEAPON) == null)
			return false;

		return (player.getEquipment().getItemContainer().get(Constants.HAT).getDefinition().getName().toLowerCase().contains("ahrims hood") && player.getEquipment().getItemContainer().get(Constants.CHEST).getDefinition().getName().toLowerCase().contains("ahrims robetop") && player.getEquipment().getItemContainer().get(Constants.LEGS).getDefinition().getName().toLowerCase().contains("ahrims robeskirt") && player.getEquipment().getItemContainer().get(Constants.WEAPON).getDefinition().getName().toLowerCase().contains("ahrims staff"));

	}

	public boolean fullKaril() {
		if (player.getEquipment().getItemContainer().get(Constants.HAT) == null || player.getEquipment().getItemContainer().get(Constants.LEGS) == null || player.getEquipment().getItemContainer().get(Constants.CHEST) == null || player.getEquipment().getItemContainer().get(Constants.WEAPON) == null)
			return false;

		return (player.getEquipment().getItemContainer().get(Constants.HAT).getDefinition().getName().toLowerCase().contains("karils coif") && player.getEquipment().getItemContainer().get(Constants.CHEST).getDefinition().getName().toLowerCase().contains("karils leathertop") && player.getEquipment().getItemContainer().get(Constants.LEGS).getDefinition().getName().toLowerCase().contains("karils leatherskirt") && player.getEquipment().getItemContainer().get(Constants.WEAPON).getDefinition().getName().toLowerCase().contains("karils crossbow"));
	}

	public boolean fullVerac() {
		if (player.getEquipment().getItemContainer().get(Constants.HAT) == null || player.getEquipment().getItemContainer().get(Constants.LEGS) == null || player.getEquipment().getItemContainer().get(Constants.CHEST) == null || player.getEquipment().getItemContainer().get(Constants.WEAPON) == null)
			return false;

		return (player.getEquipment().getItemContainer().get(Constants.HAT).getDefinition().getName().toLowerCase().contains("veracs helm") && player.getEquipment().getItemContainer().get(Constants.CHEST).getDefinition().getName().toLowerCase().contains("veracs brassard") && player.getEquipment().getItemContainer().get(Constants.LEGS).getDefinition().getName().toLowerCase().contains("veracs plateskirt") && player.getEquipment().getItemContainer().get(Constants.WEAPON).getDefinition().getName().toLowerCase().contains("veracs flail"));
	}

	public boolean fullGuthan() {
		if (player.getEquipment().getItemContainer().get(Constants.HAT) == null || player.getEquipment().getItemContainer().get(Constants.LEGS) == null || player.getEquipment().getItemContainer().get(Constants.CHEST) == null || player.getEquipment().getItemContainer().get(Constants.WEAPON) == null)
			return false;

		return (player.getEquipment().getItemContainer().get(Constants.HAT).getDefinition().getName().toLowerCase().contains("guthans helm") && player.getEquipment().getItemContainer().get(Constants.CHEST).getDefinition().getName().toLowerCase().contains("guthans platebody") && player.getEquipment().getItemContainer().get(Constants.LEGS).getDefinition().getName().toLowerCase().contains("guthans chainskirt") && player.getEquipment().getItemContainer().get(Constants.WEAPON).getDefinition().getName().toLowerCase().contains("guthans warspear"));
	}

	public boolean fullDharok() {
		if (player.getEquipment().getItemContainer().get(Constants.HAT) == null || player.getEquipment().getItemContainer().get(Constants.LEGS) == null || player.getEquipment().getItemContainer().get(Constants.CHEST) == null || player.getEquipment().getItemContainer().get(Constants.WEAPON) == null)
			return false;

		return (player.getEquipment().getItemContainer().get(Constants.HAT).getDefinition().getName().toLowerCase().contains("dharoks helm") && player.getEquipment().getItemContainer().get(Constants.CHEST).getDefinition().getName().toLowerCase().contains("dharoks platebody") && player.getEquipment().getItemContainer().get(Constants.LEGS).getDefinition().getName().toLowerCase().contains("dharoks platelegs") && player.getEquipment().getItemContainer().get(Constants.WEAPON).getDefinition().getName().toLowerCase().contains("dharoks greataxe"));
	}

	public boolean fullTorag() {
		if (player.getEquipment().getItemContainer().get(Constants.HAT) == null || player.getEquipment().getItemContainer().get(Constants.LEGS) == null || player.getEquipment().getItemContainer().get(Constants.CHEST) == null || player.getEquipment().getItemContainer().get(Constants.WEAPON) == null)
			return false;

		return (player.getEquipment().getItemContainer().get(Constants.HAT).getDefinition().getName().toLowerCase().contains("torags helm") && player.getEquipment().getItemContainer().get(Constants.CHEST).getDefinition().getName().toLowerCase().contains("torags platebody") && player.getEquipment().getItemContainer().get(Constants.LEGS).getDefinition().getName().toLowerCase().contains("torags platelegs") && player.getEquipment().getItemContainer().get(Constants.WEAPON).getDefinition().getName().toLowerCase().contains("torags hammers"));
	}

}
