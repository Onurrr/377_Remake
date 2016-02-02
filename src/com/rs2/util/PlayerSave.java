package com.rs2.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.rs2.Constants;
import com.rs2.Server;
import com.rs2.model.World;
import com.rs2.model.content.combat.effect.impl.PoisonEffect;
import com.rs2.model.content.combat.hit.Hit;
import com.rs2.model.content.combat.hit.HitDef;
import com.rs2.model.content.combat.hit.HitType;
import com.rs2.model.content.skills.magic.SpellBook;
import com.rs2.model.players.BankManager;
import com.rs2.model.players.Player;
import com.rs2.model.players.item.Item;
import com.rs2.net.packet.packets.AppearancePacketHandler;

/**
 * Static utility methods for saving and loading players.
 * 
 * @author blakeman8192
 */
public class PlayerSave {

	/** The directory where players are saved. */
	public static final String directory = "./data/characters/";

	/**
	 * Saves the player.
	 * 
	 * @param player
	 *            the player to save
	 * @return
	 */
	public static void save(final Player player) {
		try {
            @SuppressWarnings("unused")
			Misc.Stopwatch stopwatch = new Misc.Stopwatch();
			File file = new File(directory + player.getUsername() + ".dat");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream outFile = new FileOutputStream(file);
			DataOutputStream write = new DataOutputStream(outFile);
			write.writeUTF(player.getUsername());
			write.writeUTF(player.getPassword());
			write.writeInt(player.getStaffRights());
			write.writeInt(player.getPosition().getX());
			write.writeInt(player.getPosition().getY());
			write.writeInt(player.getPosition().getZ());
			write.writeInt(player.getGender());
			write.writeBoolean(player.shouldAutoRetaliate());
			write.writeInt(player.getFightMode());
			write.writeInt(player.getScreenBrightness());
			write.writeInt(player.getMouseButtons());
			write.writeInt(player.getChatEffects());
			write.writeInt(player.getSplitPrivateChat());
			write.writeInt(player.getAcceptAid());
			write.writeInt(player.getMusicVolume());
			write.writeInt(player.getEffectVolume());
			write.writeInt(player.getQuestPoints());
			write.writeDouble(player.getSpecialAmount());
			write.writeBoolean(player.getBankPin().isChangingBankPin());
			write.writeBoolean(player.getBankPin().isDeletingBankPin());
			write.writeInt(player.getBankPin().getPinAppendYear());
			write.writeInt(player.getBankPin().getPinAppendDay());
			write.writeInt(player.getBindingNeckCharge());
			write.writeInt(player.getRingOfForgingLife());
			write.writeInt(player.getRingOfRecoilLife());
			write.writeInt(player.getSkullTimer());
			write.writeInt(player.getNewComersSide().getTutorialIslandStage());
			write.writeInt(player.getNewComersSide().getProgressValue());
			write.writeDouble(player.getEnergy());
			write.writeBoolean(player.getMovementHandler().isRunToggled());

			for (int i = 0; i < player.getBankPin().getBankPin().length; i++) {
				write.writeInt(player.getBankPin().getBankPin()[i]);
			}
			for (int i = 0; i < player.getBankPin().getPendingBankPin().length; i++) {
				write.writeInt(player.getBankPin().getPendingBankPin()[i]);
			}
			for (Object[] element : player.questData) {
				write.writeInt((Integer) element[1]);
			}
			for (int i = 0; i < 4; i++) {
				write.writeInt(player.getPouchData(i));
			}
			for (int i = 0; i < player.getAppearance().length; i++) {
				write.writeInt(player.getAppearance()[i]);
			}
			for (int i = 0; i < player.getColors().length; i++) {
				write.writeInt(player.getColors()[i]);
			}
			for (int i = 0; i < player.getSkill().getLevel().length; i++) {
				write.writeInt(player.getSkill().getLevel()[i]);
			}
			for (int i = 0; i < player.getSkill().getExp().length; i++) {
				write.writeInt((int) player.getSkill().getExp()[i]);
			}
			for (int i = 0; i < 28; i++) {
				Item item = player.getInventory().getItemContainer().get(i);
				if (item == null) {
					write.writeInt(65535);
				} else {
					write.writeInt(item.getId());
					write.writeInt(item.getCount());
					write.writeInt(item.getTimer());
				}
			}
			for (int i = 0; i < 14; i++) {
				Item item = player.getEquipment().getItemContainer().get(i);
				if (item == null) {
					write.writeInt(65535);
				} else {
					write.writeInt(item.getId());
					write.writeInt(item.getCount());
				}
			}
			for (int i = 0; i < BankManager.SIZE; i++) {
				Item item = player.getBank().get(i);
				if (item == null) {
					write.writeInt(65535);
				} else {
					write.writeInt(item.getId());
					write.writeInt(item.getCount());
					write.writeInt(item.getTimer());
				}
			}
			for (int i = 0; i < player.getFriends().length; i++) {
				write.writeLong(player.getFriends()[i]);
			}
			for (int i = 0; i < player.getIgnores().length; i++) {
				write.writeLong(player.getIgnores()[i]);
			}
			for (int i = 0; i < player.getPendingItems().length; i++) {
				write.writeInt(player.getPendingItems()[i]);
				write.writeInt(player.getPendingItemsAmount()[i]);
			}
			write.writeInt(player.getRunecraftNpc());
            write.writeLong(player.getMuteExpire());
            write.writeLong(player.getBanExpire());
			write.writeBoolean(player.hasKilledTreeSpirit());
			write.writeBoolean(player.hasReset());
			write.writeBoolean(player.hasKilledJungleDemon());
			for (int i = 0; i < 6; i++) {
				write.writeBoolean(player.getBarrowsNpcDead(i));
			}
			write.writeInt(player.getKillCount());
			write.writeInt(player.getRandomGrave());
			write.writeInt(player.getPoisonImmunity().ticksRemaining());
			write.writeInt(player.getFireImmunity().ticksRemaining());
			write.writeInt(player.getTeleblockTimer().ticksRemaining());
			write.writeDouble(player.getPoisonDamage());
			for (int i = 0; i < player.getAllotment().getFarmingStages().length; i++) {
				write.writeInt(player.getAllotment().getFarmingStages()[i]);
			}
			for (int i = 0; i < player.getAllotment().getFarmingSeeds().length; i++) {
				write.writeInt(player.getAllotment().getFarmingSeeds()[i]);
			}
			for (int i = 0; i < player.getAllotment().getFarmingHarvest().length; i++) {
				write.writeInt(player.getAllotment().getFarmingHarvest()[i]);
			}
			for (int i = 0; i < player.getAllotment().getFarmingState().length; i++) {
				write.writeInt(player.getAllotment().getFarmingState()[i]);
			}
			for (int i = 0; i < player.getAllotment().getFarmingTimer().length; i++) {
				write.writeLong(player.getAllotment().getFarmingTimer()[i]);
			}
			for (int i = 0; i < player.getAllotment().getDiseaseChance().length; i++) {
				write.writeDouble(player.getAllotment().getDiseaseChance()[i]);
			}
			for (int i = 0; i < player.getAllotment().getFarmingWatched().length; i++) {
				write.writeBoolean(player.getAllotment().getFarmingWatched()[i]);
			}
			for (int i = 0; i < player.getBushes().getFarmingStages().length; i++) {
				write.writeInt(player.getBushes().getFarmingStages()[i]);
			}
			for (int i = 0; i < player.getBushes().getFarmingSeeds().length; i++) {
				write.writeInt(player.getBushes().getFarmingSeeds()[i]);
			}
			for (int i = 0; i < player.getBushes().getFarmingState().length; i++) {
				write.writeInt(player.getBushes().getFarmingState()[i]);
			}
			for (int i = 0; i < player.getBushes().getFarmingTimer().length; i++) {
				write.writeLong(player.getBushes().getFarmingTimer()[i]);
			}
			for (int i = 0; i < player.getBushes().getFarmingChance().length; i++) {
				write.writeDouble(player.getBushes().getFarmingChance()[i]);
			}
			for (int i = 0; i < player.getBushes().getFarmingWatched().length; i++) {
				write.writeBoolean(player.getBushes().getFarmingWatched()[i]);
			}
			for (int i = 0; i < player.getFlowers().getFarmingStages().length; i++) {
				write.writeInt(player.getFlowers().getFarmingStages()[i]);
			}
			for (int i = 0; i < player.getFlowers().getFarmingSeeds().length; i++) {
				write.writeInt(player.getFlowers().getFarmingSeeds()[i]);
			}
			for (int i = 0; i < player.getFlowers().getFarmingState().length; i++) {
				write.writeInt(player.getFlowers().getFarmingState()[i]);
			}
			for (int i = 0; i < player.getFlowers().getFarmingTimer().length; i++) {
				write.writeLong(player.getFlowers().getFarmingTimer()[i]);
			}
			for (int i = 0; i < player.getFlowers().getDiseaseChance().length; i++) {
				write.writeDouble(player.getFlowers().getDiseaseChance()[i]);
			}
			for (int i = 0; i < player.getFruitTrees().getFarmingStages().length; i++) {
				write.writeInt(player.getFruitTrees().getFarmingStages()[i]);
			}
			for (int i = 0; i < player.getFruitTrees().getFarmingSeeds().length; i++) {
				write.writeInt(player.getFruitTrees().getFarmingSeeds()[i]);
			}
			for (int i = 0; i < player.getFruitTrees().getFarmingState().length; i++) {
				write.writeInt(player.getFruitTrees().getFarmingState()[i]);
			}
			for (int i = 0; i < player.getFruitTrees().getFarmingTimer().length; i++) {
				write.writeLong(player.getFruitTrees().getFarmingTimer()[i]);
			}
			for (int i = 0; i < player.getFruitTrees().getDiseaseChance().length; i++) {
				write.writeDouble(player.getFruitTrees().getDiseaseChance()[i]);
			}
			for (int i = 0; i < player.getFruitTrees().getFarmingWatched().length; i++) {
				write.writeBoolean(player.getFruitTrees().getFarmingWatched()[i]);
			}
			for (int i = 0; i < player.getHerbs().getFarmingStages().length; i++) {
				write.writeInt(player.getHerbs().getFarmingStages()[i]);
			}
			for (int i = 0; i < player.getHerbs().getFarmingSeeds().length; i++) {
				write.writeInt(player.getHerbs().getFarmingSeeds()[i]);
			}
			for (int i = 0; i < player.getHerbs().getFarmingHarvest().length; i++) {
				write.writeInt(player.getHerbs().getFarmingHarvest()[i]);
			}
			for (int i = 0; i < player.getHerbs().getFarmingState().length; i++) {
				write.writeInt(player.getHerbs().getFarmingState()[i]);
			}
			for (int i = 0; i < player.getHerbs().getFarmingTimer().length; i++) {
				write.writeLong(player.getHerbs().getFarmingTimer()[i]);
			}
			for (int i = 0; i < player.getHerbs().getDiseaseChance().length; i++) {
				write.writeDouble(player.getHerbs().getDiseaseChance()[i]);
			}
			for (int i = 0; i < player.getHops().getFarmingStages().length; i++) {
				write.writeInt(player.getHops().getFarmingStages()[i]);
			}
			for (int i = 0; i < player.getHops().getFarmingSeeds().length; i++) {
				write.writeInt(player.getHops().getFarmingSeeds()[i]);
			}
			for (int i = 0; i < player.getHops().getFarmingHarvest().length; i++) {
				write.writeInt(player.getHops().getFarmingHarvest()[i]);
			}
			for (int i = 0; i < player.getHops().getFarmingState().length; i++) {
				write.writeInt(player.getHops().getFarmingState()[i]);
			}
			for (int i = 0; i < player.getHops().getFarmingTimer().length; i++) {
				write.writeLong(player.getHops().getFarmingTimer()[i]);
			}
			for (int i = 0; i < player.getHops().getDiseaseChance().length; i++) {
				write.writeDouble(player.getHops().getDiseaseChance()[i]);
			}
			for (int i = 0; i < player.getHops().getFarmingWatched().length; i++) {
				write.writeBoolean(player.getHops().getFarmingWatched()[i]);
			}
			for (int i = 0; i < player.getSpecialPlantOne().getFarmingStages().length; i++) {
				write.writeInt(player.getSpecialPlantOne().getFarmingStages()[i]);
			}
			for (int i = 0; i < player.getSpecialPlantOne().getFarmingSeeds().length; i++) {
				write.writeInt(player.getSpecialPlantOne().getFarmingSeeds()[i]);
			}
			for (int i = 0; i < player.getSpecialPlantOne().getFarmingState().length; i++) {
				write.writeInt(player.getSpecialPlantOne().getFarmingState()[i]);
			}
			for (int i = 0; i < player.getSpecialPlantOne().getFarmingTimer().length; i++) {
				write.writeLong(player.getSpecialPlantOne().getFarmingTimer()[i]);
			}
			for (int i = 0; i < player.getSpecialPlantOne().getDiseaseChance().length; i++) {
				write.writeDouble(player.getSpecialPlantOne().getDiseaseChance()[i]);
			}
			for (int i = 0; i < player.getSpecialPlantTwo().getFarmingStages().length; i++) {
				write.writeInt(player.getSpecialPlantTwo().getFarmingStages()[i]);
			}
			for (int i = 0; i < player.getSpecialPlantTwo().getFarmingSeeds().length; i++) {
				write.writeInt(player.getSpecialPlantTwo().getFarmingSeeds()[i]);
			}
			for (int i = 0; i < player.getSpecialPlantTwo().getFarmingState().length; i++) {
				write.writeInt(player.getSpecialPlantTwo().getFarmingState()[i]);
			}
			for (int i = 0; i < player.getSpecialPlantTwo().getFarmingTimer().length; i++) {
				write.writeLong(player.getSpecialPlantTwo().getFarmingTimer()[i]);
			}
			for (int i = 0; i < player.getSpecialPlantTwo().getDiseaseChance().length; i++) {
				write.writeDouble(player.getSpecialPlantTwo().getDiseaseChance()[i]);
			}
			for (int i = 0; i < player.getTrees().getFarmingStages().length; i++) {
				write.writeInt(player.getTrees().getFarmingStages()[i]);
			}
			for (int i = 0; i < player.getTrees().getFarmingSeeds().length; i++) {
				write.writeInt(player.getTrees().getFarmingSeeds()[i]);
			}
			for (int i = 0; i < player.getTrees().getFarmingHarvest().length; i++) {
				write.writeInt(player.getTrees().getFarmingHarvest()[i]);
			}
			for (int i = 0; i < player.getTrees().getFarmingState().length; i++) {
				write.writeInt(player.getTrees().getFarmingState()[i]);
			}
			for (int i = 0; i < player.getTrees().getFarmingTimer().length; i++) {
				write.writeLong(player.getTrees().getFarmingTimer()[i]);
			}
			for (int i = 0; i < player.getTrees().getDiseaseChance().length; i++) {
				write.writeDouble(player.getTrees().getDiseaseChance()[i]);
			}
			for (int i = 0; i < player.getTrees().getFarmingWatched().length; i++) {
				write.writeBoolean(player.getTrees().getFarmingWatched()[i]);
			}
			for (int i = 0; i < player.getCompost().getCompostBins().length; i++) {
				write.writeInt(player.getCompost().getCompostBins()[i]);
			}
			for (int i = 0; i < player.getCompost().getCompostBinsTimer().length; i++) {
				write.writeLong(player.getCompost().getCompostBinsTimer()[i]);
			}
			for (int i = 0; i < player.getCompost().getOrganicItemAdded().length; i++) {
				write.writeInt(player.getCompost().getOrganicItemAdded()[i]);
			}
			for (int i = 0; i < player.getFarmingTools().getTools().length; i++) {
				write.writeInt(player.getFarmingTools().getTools()[i]);
			}
			write.writeInt(player.getSlayer().slayerMaster);
			write.writeUTF(player.getSlayer().slayerTask);
			write.writeInt(player.getSlayer().taskAmount);
			write.writeBoolean(player.getMagicBookType() == SpellBook.ANCIENT);
			write.writeBoolean(player.isBrimhavenDungeonOpen());
			write.writeBoolean(player.hasKilledClueAttacker());
            write.flush();
			write.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

    public static void load(Player player) {
    	readFile(player);
	}//try now kk

	public static void saveAllPlayers() {
        synchronized (World.getPlayers()) {
            final Player[] players = World.getPlayers();
            for (Player p : players) {
                if (p != null && p.getIndex() != -1) {
                    try {
                        PlayerSave.save(p);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
	}
    
    static int readFile(Player player) {
        File file = new File(directory + player.getUsername()
                + ".dat");
        if (!file.exists()) {
            if (Server.getSingleton() != null)
                Server.getSingleton().queueLogin(player);
            return 0;
        }
        try {
            FileInputStream inFile = new FileInputStream(file);
            DataInputStream load = new DataInputStream(inFile);
            player.setUsername(load.readUTF());
            String password = load.readUTF();
            if (!password.equalsIgnoreCase(player.getPassword())) {
            	System.out.println("Wrong login- "+password+"");
            	player.setReturnCode(Constants.LOGIN_RESPONSE_INVALID_CREDENTIALS);
            	load.close();
            	inFile.close();
            	return 3;
            } else {
            	System.out.println("correct login- "+password+"");//try bthat
            }
            
            player.setPassword(password);
            player.setStaffRights(load.readInt());
            player.getPosition().setX(load.readInt());
            player.getPosition().setLastX(
                    player.getPosition().getX());
            player.getPosition().setY(load.readInt());
            player.getPosition().setLastY(
                    player.getPosition().getY() + 1);
            player.getPosition().setZ(load.readInt());
            player.setGender(load.readInt());
            player.setAutoRetaliate(load.readBoolean());
            player.setFightMode(load.readInt());
            player.setScreenBrightness(load.readInt());
            player.setMouseButtons(load.readInt());
            player.setChatEffects(load.readInt());
            player.setSplitPrivateChat(load.readInt());
            player.setAcceptAid(load.readInt());
            player.setMusicVolume(load.readInt());
            player.setEffectVolume(load.readInt());
            player.setQuestPoints(load.readInt());
            player.setSpecialAmount((int) load.readDouble());
            player.getBankPin().setChangingBankPin(
                    load.readBoolean());
            player.getBankPin().setDeletingBankPin(
                    load.readBoolean());
            player.getBankPin()
                    .setPinAppendYear(load.readInt());
            player.getBankPin().setPinAppendDay(load.readInt());
            player.setBindingNeckCharge(load.readInt());
            player.setRingOfForgingLife(load.readInt());
            player.setRingOfRecoilLife(load.readInt());
        	int skullTimer = load.readInt();
        	if (skullTimer > 0)
        		player.addSkull(player, skullTimer);
            player.getNewComersSide().setTutorialIslandStage(
                    load.readInt(), false);
            player.getNewComersSide().setProgressValue(
                    load.readInt());
            player.setEnergy(load.readDouble());
            player.getMovementHandler().setRunToggled(load.readBoolean());
            for (int i = 0; i < player.getBankPin().getBankPin().length; i++) {
                player.getBankPin().getBankPin()[i] = load.readInt();
            }
            for (int i = 0; i < player.getBankPin()
                    .getPendingBankPin().length; i++) {
                player.getBankPin().getPendingBankPin()[i] = load
                        .readInt();
            }
            for (int i = 0; i < player.questData.length; i++) {
                player.questData[i][1] = load
                        .readInt();
            }
            for (int i = 0; i < 4; i++) {
                player.setPouchData(i, load.readInt());
            }
            for (int i = 0; i < player.getAppearance().length; i++) {
                player.getAppearance()[i] = load.readInt();
            }
            for (int i = 0; i < player.getColors().length; i++) {
                player.getColors()[i] = load.readInt();
            }
            AppearancePacketHandler.checkOutfitRanges(player);
            for (int i = 0; i < player.getSkill().getLevel().length; i++) {
                player.getSkill().getLevel()[i] = load
                        .readInt();
            }
            for (int i = 0; i < player.getSkill().getExp().length; i++) {
                player.getSkill().getExp()[i] = load.readInt();
            }
            for (int i = 0; i < 28; i++) {
                int id = load.readInt();
                if (id != 65535) {
                    int amount = load.readInt();
                    int timer = load.readInt();
                    if (id < Constants.MAX_ITEMS && amount > 0)  {
                        Item item = new Item(id, amount, timer);
                        if (item.getId() == 2696 || item.getId() == 2699 || item.getId() == 3510) {
                        	item = new Item(id - 1, amount, timer);
                        }
                        player.getInventory().getItemContainer().set(i, item);
                    }
                }
            }
            for (int i = 0; i < 14; i++) {
                int id = load.readInt();
                if (id != 65535) {
                    int amount = load.readInt();
                    if (id < Constants.MAX_ITEMS && amount > 0)  {
                        Item item = new Item(id, amount);
                        player.getEquipment().getItemContainer().set(i, item);
                    }
                }
            }
            try {
                for (int i = 0; i < BankManager.SIZE; i++) {
                    int id = load.readInt();
                    if (id != 65535) {
                        int amount = load.readInt();
                        int timer = load.readInt();
                        if (id < Constants.MAX_ITEMS && amount > 0)  {
                            Item item = new Item(id, amount, timer);
                            if (item.getId() == 2696 || item.getId() == 2699 || item.getId() == 3510) {
                            	item = new Item(id - 1, amount, timer);
                            }
                            player.getBank().set(i, item);
                        }
                    }
                }
	            for (int i = 0; i < player.getFriends().length; i++) {
	                player.getFriends()[i] = load.readLong();
	            }
	            for (int i = 0; i < player.getIgnores().length; i++) {
	                player.getIgnores()[i] = load.readLong();
	            }
	            for (int i = 0; i < player.getPendingItems().length; i++) {
	                player.getPendingItems()[i] = load.readInt();
	                player.getPendingItemsAmount()[i] = load.readInt();
	            }
                player.setRunecraftNpc(load.readInt());
                player.setMuteExpire(load.readLong());
                player.setBanExpire(load.readLong());
                player.setKilledTreeSpirit(load.readBoolean());
                player.setKilledJungleDemon(load.readBoolean());
                player.setResetBank(load.readBoolean());
                for (int i = 0; i < 6; i++) {
                	player.setBarrowsNpcDead(i, load.readBoolean());
                }
                player.setKillCount(load.readInt());
                player.setRandomGrave(load.readInt());
                player.getPoisonImmunity().setWaitDuration(load.readInt());
                player.getPoisonImmunity().reset();
                player.getFireImmunity().setWaitDuration(load.readInt());
                player.getFireImmunity().reset();
                player.getTeleblockTimer().setWaitDuration(load.readInt());
                player.getTeleblockTimer().reset();
            	double poison = load.readDouble();
                if (poison > 0) {
                	HitDef hitDef = new HitDef(null, HitType.POISON, Math.ceil(poison)).setStartingHitDelay(30);
    				Hit hit = new Hit(player, player, hitDef);
    				PoisonEffect p = new PoisonEffect(poison, false);
    				p.initialize(hit);
                }
    			for (int i = 0; i < player.getAllotment().getFarmingStages().length; i++) {
    				player.getAllotment().setFarmingStages(i, load.readInt());
    			}
    			for (int i = 0; i < player.getAllotment().getFarmingSeeds().length; i++) {
    				player.getAllotment().setFarmingSeeds(i, load.readInt());
    			}
    			for (int i = 0; i < player.getAllotment().getFarmingHarvest().length; i++) {
    				player.getAllotment().setFarmingHarvest(i, load.readInt());
    			}
    			for (int i = 0; i < player.getAllotment().getFarmingState().length; i++) {
    				player.getAllotment().setFarmingState(i, load.readInt());
    			}
    			for (int i = 0; i < player.getAllotment().getFarmingTimer().length; i++) {
    				player.getAllotment().setFarmingTimer(i, load.readLong());
    			}
    			for (int i = 0; i < player.getAllotment().getDiseaseChance().length; i++) {
    				player.getAllotment().setDiseaseChance(i, load.readDouble());
    			}
    			for (int i = 0; i < player.getAllotment().getFarmingWatched().length; i++) {
    				player.getAllotment().setFarmingWatched(i, load.readBoolean());
    			}
    			for (int i = 0; i < player.getBushes().getFarmingStages().length; i++) {
    				player.getBushes().setFarmingStages(i, load.readInt());
    			}
    			for (int i = 0; i < player.getBushes().getFarmingSeeds().length; i++) {
    				player.getBushes().setFarmingSeeds(i, load.readInt());
    			}
    			for (int i = 0; i < player.getBushes().getFarmingState().length; i++) {
    				player.getBushes().setFarmingState(i, load.readInt());
    			}
    			for (int i = 0; i < player.getBushes().getFarmingTimer().length; i++) {
    				player.getBushes().setFarmingTimer(i, load.readLong());
    			}
    			for (int i = 0; i < player.getBushes().getFarmingChance().length; i++) {
    				player.getBushes().setFarmingChance(i, load.readDouble());
    			}
    			for (int i = 0; i < player.getBushes().getFarmingWatched().length; i++) {
    				player.getBushes().setFarmingWatched(i, load.readBoolean());
    			}
    			for (int i = 0; i < player.getFlowers().getFarmingStages().length; i++) {
    				player.getFlowers().setFarmingStages(i, load.readInt());
    			}
    			for (int i = 0; i < player.getFlowers().getFarmingSeeds().length; i++) {
    				player.getFlowers().setFarmingSeeds(i, load.readInt());
    			}
    			for (int i = 0; i < player.getFlowers().getFarmingState().length; i++) {
    				player.getFlowers().setFarmingState(i, load.readInt());
    			}
    			for (int i = 0; i < player.getFlowers().getFarmingTimer().length; i++) {
    				player.getFlowers().setFarmingTimer(i, load.readLong());
    			}
    			for (int i = 0; i < player.getFlowers().getDiseaseChance().length; i++) {
    				player.getFlowers().setDiseaseChance(i, load.readDouble());
    			}
    			for (int i = 0; i < player.getFruitTrees().getFarmingStages().length; i++) {
    				player.getFruitTrees().setFarmingStages(i, load.readInt());
    			}
    			for (int i = 0; i < player.getFruitTrees().getFarmingSeeds().length; i++) {
    				player.getFruitTrees().setFarmingSeeds(i, load.readInt());
    			}
    			for (int i = 0; i < player.getFruitTrees().getFarmingState().length; i++) {
    				player.getFruitTrees().setFarmingState(i, load.readInt());
    			}
    			for (int i = 0; i < player.getFruitTrees().getFarmingTimer().length; i++) {
    				player.getFruitTrees().setFarmingTimer(i, load.readLong());
    			}
    			for (int i = 0; i < player.getFruitTrees().getDiseaseChance().length; i++) {
    				player.getFruitTrees().setDiseaseChance(i, load.readDouble());
    			}
    			for (int i = 0; i < player.getFruitTrees().getFarmingWatched().length; i++) {
    				player.getFruitTrees().setFarmingWatched(i, load.readBoolean());
    			}
    			for (int i = 0; i < player.getHerbs().getFarmingStages().length; i++) {
    				player.getHerbs().setFarmingStages(i, load.readInt());
    			}
    			for (int i = 0; i < player.getHerbs().getFarmingSeeds().length; i++) {
    				player.getHerbs().setFarmingSeeds(i, load.readInt());
    			}
    			for (int i = 0; i < player.getHerbs().getFarmingHarvest().length; i++) {
    				player.getHerbs().setFarmingHarvest(i, load.readInt());
    			}
    			for (int i = 0; i < player.getHerbs().getFarmingState().length; i++) {
    				player.getHerbs().setFarmingState(i, load.readInt());
    			}
    			for (int i = 0; i < player.getHerbs().getFarmingTimer().length; i++) {
    				player.getHerbs().setFarmingTimer(i, load.readLong());
    			}
    			for (int i = 0; i < player.getHerbs().getDiseaseChance().length; i++) {
    				player.getHerbs().setDiseaseChance(i, load.readDouble());
    			}
    			for (int i = 0; i < player.getHops().getFarmingStages().length; i++) {
    				player.getHops().setFarmingStages(i, load.readInt());
    			}
    			for (int i = 0; i < player.getHops().getFarmingSeeds().length; i++) {
    				player.getHops().setFarmingSeeds(i, load.readInt());
    			}
    			for (int i = 0; i < player.getHops().getFarmingHarvest().length; i++) {
    				player.getHops().setFarmingHarvest(i, load.readInt());
    			}
    			for (int i = 0; i < player.getHops().getFarmingState().length; i++) {
    				player.getHops().setFarmingState(i, load.readInt());
    			}
    			for (int i = 0; i < player.getHops().getFarmingTimer().length; i++) {
    				player.getHops().setFarmingTimer(i, load.readLong());
    			}
    			for (int i = 0; i < player.getHops().getDiseaseChance().length; i++) {
    				player.getHops().setDiseaseChance(i, load.readDouble());
    			}
    			for (int i = 0; i < player.getHops().getFarmingWatched().length; i++) {
    				player.getHops().setFarmingWatched(i, load.readBoolean());
    			}
    			for (int i = 0; i < player.getSpecialPlantOne().getFarmingStages().length; i++) {
    				player.getSpecialPlantOne().setFarmingStages(i, load.readInt());
    			}
    			for (int i = 0; i < player.getSpecialPlantOne().getFarmingSeeds().length; i++) {
    				player.getSpecialPlantOne().setFarmingSeeds(i, load.readInt());
    			}
    			for (int i = 0; i < player.getSpecialPlantOne().getFarmingState().length; i++) {
    				player.getSpecialPlantOne().setFarmingState(i, load.readInt());
    			}
    			for (int i = 0; i < player.getSpecialPlantOne().getFarmingTimer().length; i++) {
    				player.getSpecialPlantOne().setFarmingTimer(i, load.readLong());
    			}
    			for (int i = 0; i < player.getSpecialPlantOne().getDiseaseChance().length; i++) {
    				player.getSpecialPlantOne().setDiseaseChance(i, load.readDouble());
    			}
    			for (int i = 0; i < player.getSpecialPlantTwo().getFarmingStages().length; i++) {
    				player.getSpecialPlantTwo().setFarmingStages(i, load.readInt());
    			}
    			for (int i = 0; i < player.getSpecialPlantTwo().getFarmingSeeds().length; i++) {
    				player.getSpecialPlantTwo().setFarmingSeeds(i, load.readInt());
    			}
    			for (int i = 0; i < player.getSpecialPlantTwo().getFarmingState().length; i++) {
    				player.getSpecialPlantTwo().setFarmingState(i, load.readInt());
    			}
    			for (int i = 0; i < player.getSpecialPlantTwo().getFarmingTimer().length; i++) {
    				player.getSpecialPlantTwo().setFarmingTimer(i, load.readLong());
    			}
    			for (int i = 0; i < player.getSpecialPlantTwo().getDiseaseChance().length; i++) {
    				player.getSpecialPlantTwo().setDiseaseChance(i, load.readDouble());
    			}
    			for (int i = 0; i < player.getTrees().getFarmingStages().length; i++) {
    				player.getTrees().setFarmingStages(i, load.readInt());
    			}
    			for (int i = 0; i < player.getTrees().getFarmingSeeds().length; i++) {
    				player.getTrees().setFarmingSeeds(i, load.readInt());
    			}
    			for (int i = 0; i < player.getTrees().getFarmingHarvest().length; i++) {
    				player.getTrees().setFarmingHarvest(i, load.readInt());
    			}
    			for (int i = 0; i < player.getTrees().getFarmingState().length; i++) {
    				player.getTrees().setFarmingState(i, load.readInt());
    			}
    			for (int i = 0; i < player.getTrees().getFarmingTimer().length; i++) {
    				player.getTrees().setFarmingTimer(i, load.readLong());
    			}
    			for (int i = 0; i < player.getTrees().getDiseaseChance().length; i++) {
    				player.getTrees().setDiseaseChance(i, load.readDouble());
    			}
    			for (int i = 0; i < player.getTrees().getFarmingWatched().length; i++) {
    				player.getTrees().setFarmingWatched(i, load.readBoolean());
    			}
    			for (int i = 0; i < player.getCompost().getCompostBins().length; i++) {
    				player.getCompost().setCompostBins(i, load.readInt());
    			}
    			for (int i = 0; i < player.getCompost().getCompostBinsTimer().length; i++) {
    				player.getCompost().setCompostBinsTimer(i, load.readLong());
    			}
    			for (int i = 0; i < player.getCompost().getOrganicItemAdded().length; i++) {
    				player.getCompost().setOrganicItemAdded(i, load.readInt());
    			}
    			for (int i = 0; i < player.getFarmingTools().getTools().length; i++) {
    				player.getFarmingTools().setTools(i, load.readInt());
    			}
            } catch (IOException e) {
            }
            try {
            	if (player.getQuestPoints() == 1) {
            		player.getSlayer().slayerMaster = load.readInt();
            	} else {
            		load.readUTF();
            	}
            	player.setQuestPoints(1);
            } catch (IOException e) {
            	player.getSlayer().resetSlayerTask();
            }
            try {
            	player.getSlayer().slayerTask = load.readUTF();
            	player.getSlayer().taskAmount = load.readInt();
            } catch (IOException e) {
            }
            try {
            	boolean ancient = load.readBoolean();
            	player.setMagicBookType(ancient ? SpellBook.ANCIENT : SpellBook.MODERN);
            } catch (IOException e) {
            }
            try {
            	player.setBrimhavenDungeonOpen(load.readBoolean());
            } catch (IOException e) {
            }
            try {
            	player.setKilledClueAttacker(load.readBoolean());
            } catch (IOException e) {
            }
          //  player.setKilledJungleDemon(load.readBoolean());
            load.close();
            if (Server.getSingleton() != null)
                Server.getSingleton().queueLogin(player);
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
        	System.out.println("Account not loading: " + player);
            //corrupted save file
            if (Server.getSingleton() != null)
                Server.getSingleton().queueLogin(player);
            return 0;
        }
    }

    public static class ConnectionAttempt {
        private static final long TIMEOUT = 5 * 60 * 1000;
        private static final int MAX_ATTEMPTS = 7;
        Misc.Stopwatch timer;
        int attempts;

        public ConnectionAttempt() {
            timer = new Misc.Stopwatch();
        }

        public void addAttempt() {
            if (attempts < MAX_ATTEMPTS)
                attempts += 1;
            else timer.reset();
        }
        
        public void reset() {
            attempts = 0;
        }

        public boolean canConnect() {
            return attempts != MAX_ATTEMPTS || timer.elapsed() >= TIMEOUT;
        }
    }
}
