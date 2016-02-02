package com.rs2.model.content.consumables;

import com.rs2.model.content.combat.effect.impl.PoisonEffect;
import com.rs2.model.content.combat.hit.HitType;
import com.rs2.model.content.minigames.duelarena.RulesData;
import com.rs2.model.content.skills.Skill;
import com.rs2.model.players.Player;
import com.rs2.model.players.item.Item;

/**
 * By Mikey` of Rune-Server
 */
public class Potion {

	Player player;

	public Potion(Player player) {
		this.player = player;
	}

	/**
	 * All the potion definitions.
	 */
	private static PotionLoader.PotionDefinition[] potionDefinitions = new PotionLoader.PotionDefinition[50];

	/**
	 * Potion definition count.
	 */
	public static int potionCount = 0;

	public int potionIndex = 0, potionIdIndex = 0;
	private static final int EMPTY_VIAL = 229;

	public boolean isPotion(int itemId) {
		for (int i = 0; i < potionCount; i++) {
			for (int i2 = 0; i2 < 4; i2++) {
				if (potionDefinitions[i].getPotionIds()[i2] == itemId) {
					potionIndex = i;
					potionIdIndex = i2;
					return true;
				}
			}
		}
		return false;
	}

	public void drinkPotion(int itemId, int slot) {
		if (RulesData.NO_DRINKS.activated(player)) {
			player.getActionSender().sendMessage("Usage of drinks have been disabled during this fight!");
			return;
		}
		if (player.getSkill().canDoAction2(600) && !player.isDead()) {
			player.setInstigatingAttack(false);
			int[] potionIds = potionDefinitions[potionIndex].getPotionIds();
			int[] affectedStats = potionDefinitions[potionIndex].getAffectedStats();
			int[] statAddons = potionDefinitions[potionIndex].getStatAddons();
			double[] statModifiers = potionDefinitions[potionIndex].getStatModifiers();
			for (int i = 0; i < affectedStats.length; i++) {
				if (potionDefinitions[potionIndex].getPotionType() == PotionLoader.PotionDefinition.PotionTypes.BOOST) {
					int index = affectedStats[i];
					int currentLevel = player.getSkill().getLevel()[index];
					int actualLevel = player.getSkill().getPlayerLevel(index);
					int levelAfterDrink = actualLevel;
					levelAfterDrink += actualLevel * statModifiers[i];
					levelAfterDrink += statAddons[i];
					if (currentLevel < actualLevel) {
						player.getSkill().getLevel()[index] += levelAfterDrink - actualLevel;
						player.getSkill().refresh(index);
					} else if (currentLevel < levelAfterDrink) {
						player.getSkill().getLevel()[index] = levelAfterDrink;
						player.getSkill().refresh(index);
					}
				} else if (potionDefinitions[potionIndex].getPotionType() == PotionLoader.PotionDefinition.PotionTypes.RESTORE) {
					int index = affectedStats[i];
					int currentLevel = player.getSkill().getLevel()[index];
					int actualLevel = player.getSkill().getPlayerLevel(index);
					int levelAfterDrink = currentLevel;
					levelAfterDrink += actualLevel * statModifiers[i];
					levelAfterDrink += statAddons[i];
					if (currentLevel > actualLevel) {
						continue;
					}
					if (levelAfterDrink <= actualLevel) {
						player.getSkill().getLevel()[index] = levelAfterDrink;
						player.getSkill().refresh(index);
					} else {
						player.getSkill().getLevel()[index] = player.getSkill().getPlayerLevel(index);
						player.getSkill().refresh(index);
					}
				}
			}
			doOtherPotionEffects(itemId);
			player.getUpdateFlags().sendAnimation(itemId == 3801 ? 1330 : 829, 0);
			player.getTask();
			player.getCombatDelayTick().setWaitDuration(player.getCombatDelayTick().getWaitDuration() + 2);
			if (potionIdIndex < 3) {
				if (player.getInventory().removeItemSlot(new Item(itemId, 1), slot)) {
					player.getInventory().addItemToSlot(new Item(potionIds[potionIdIndex + 1], 1), slot);
				} else if (player.getInventory().removeItem(new Item(itemId, 1))) {
					player.getInventory().addItem(new Item(potionIds[potionIdIndex + 1], 1));
				}
				player.getActionSender().sendMessage("You drink"+(isDose(itemId) ? " a dose of" :"")+" your " + potionDefinitions[potionIndex].getPotionName() + ".");
			} else {
				if (player.getInventory().removeItemSlot(new Item(itemId, 1), slot)) {
					player.getInventory().addItemToSlot(new Item(EMPTY_VIAL), slot);
				} else if (player.getInventory().removeItem(new Item(itemId, 1))) {
					player.getInventory().addItem(new Item(EMPTY_VIAL));
				}
				player.getActionSender().sendMessage("You drink the last dose of your " + potionDefinitions[potionIndex].getPotionName() + ".");
			}
		}
	}

	private boolean isDose(int itemId) {
		return itemId != 1993 && itemId != 1978 && itemId != 1917 && itemId != 7919 && itemId != 3801;
	}

	private void doOtherPotionEffects(int itemId) {
		switch (itemId) {
			case 7919 : // Bottle of wine
				player.heal(14);
				break;
			case 3801 : // Keg of beer
				player.getActionSender().statEdit(Skill.ATTACK, -40, false);
				player.getActionSender().statEdit(Skill.STRENGTH, 10, true);
				player.heal(1);
				break;
			case 1993 : // Jug of wine
				player.getActionSender().statEdit(Skill.ATTACK, -2, false);
				player.heal(11);
				break;
			case 712 :
			case 4242 :
			case 4265 :
			case 4246 :
			case 4838 :
			case 1978 : // Cup of tea
				player.getUpdateFlags().sendForceMessage("Aaah, nothing like a nice cuppa tea!");
				player.heal(3);
				break;
			case 1917 : // Beer
				
				player.getActionSender().statEdit(Skill.ATTACK, (int) (player.getSkill().getPlayerLevel(Skill.ATTACK) * 0.07) * -1, false);
				player.heal(1);
				break;
			case 2446 : // Antipoison
			case 175 :
			case 177 :
			case 179 :
				player.removeAllEffects(PoisonEffect.class);
				player.getPoisonImmunity().setWaitDuration(300);
				player.getPoisonImmunity().reset();
				break;
			case 3008 : // Energy
			case 3010 :
			case 3012 :
			case 3014 :
				if (player.getEnergy() + 20 < 100) {
					player.setEnergy(player.getEnergy() + 20);
				} else {
					player.setEnergy(100);
				}
				break;
			case 2448 : // Super antipoison
			case 181 :
			case 183 :
			case 185 :
				player.removeAllEffects(PoisonEffect.class);
				player.getPoisonImmunity().setWaitDuration(600);
				player.getPoisonImmunity().reset();
				break;
			case 3016 : // Super energy
			case 3018 :
			case 3020 :
			case 3022 :
				if (player.getEnergy() + 40 < 100) {
					player.setEnergy(player.getEnergy() + 40);
				} else {
					player.setEnergy(100);
				}
				break;
			case 5943 : // Antipoison+
			case 5945 :
			case 5947 :
			case 5949 :
				player.removeAllEffects(PoisonEffect.class);
				player.getPoisonImmunity().setWaitDuration(900);
				player.getPoisonImmunity().reset();
				break;
			case 2452 : // Antifire
			case 2454 :
			case 2456 :
			case 2458 :
				player.getFireImmunity().setWaitDuration(600);
				player.getFireImmunity().reset();
				break;
			case 5952 : // Antipoison++
			case 5954 :
			case 5956 :
			case 5958 :
				player.removeAllEffects(PoisonEffect.class);
				player.getPoisonImmunity().setWaitDuration(1200);
				player.getPoisonImmunity().reset();
				break;
			case 6685 : // Saradomin brew
			case 6687 :
			case 6689 :
			case 6691 :
				player.heal(2);
				player.getActionSender().statEdit(Skill.HITPOINTS, (int) (player.getSkill().getPlayerLevel(Skill.HITPOINTS) * 0.15), true);
				player.getActionSender().statEdit(Skill.DEFENCE, (int) (player.getSkill().getPlayerLevel(Skill.DEFENCE) * 0.25), true);
				player.getActionSender().statEdit(Skill.ATTACK, (int) ((player.getSkill().getPlayerLevel(Skill.ATTACK) * 0.10) * -1), false);
				player.getActionSender().statEdit(Skill.STRENGTH, (int) ((player.getSkill().getPlayerLevel(Skill.STRENGTH) * 0.10) * -1), false);
				player.getActionSender().statEdit(Skill.MAGIC, (int) ((player.getSkill().getPlayerLevel(Skill.MAGIC) * 0.10) * -1), false);
				player.getActionSender().statEdit(Skill.RANGED, (int) ((player.getSkill().getPlayerLevel(Skill.RANGED) * 0.10) * -1), false);
				break;
			case 2450 : // Zamorak brew
			case 189 :
			case 191 :
			case 193 :
				player.getActionSender().statEdit(Skill.ATTACK, (int) (player.getSkill().getPlayerLevel(Skill.ATTACK) * 0.20) + 2, true);
				player.getActionSender().statEdit(Skill.STRENGTH, (int) (player.getSkill().getPlayerLevel(Skill.STRENGTH) * 0.12) + 2, true);
				player.getActionSender().statEdit(Skill.PRAYER, (int) (player.getSkill().getPlayerLevel(Skill.PRAYER) * 0.10), true);
				player.getActionSender().statEdit(Skill.DEFENCE, ((int) (player.getSkill().getPlayerLevel(Skill.DEFENCE) * 0.10) + 2) * -1, false);
				player.hit((int) (player.getSkill().getLevel()[Skill.HITPOINTS] * 0.1) + 2, HitType.NORMAL);
				break;
			default :
				break;
		}
	}

	public static PotionLoader.PotionDefinition[] getPotionDefinitions() {
		return potionDefinitions;
	}

}