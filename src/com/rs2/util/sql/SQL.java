package com.rs2.util.sql;

import java.sql.*;
//import java.security.MessageDigest;
import com.rs2.model.players.Player;
import com.rs2.model.content.skills.Skill;


public class SQL {


	public static Connection con;
	public static Statement stmt;
	int affected = 0;
	public static boolean connectionMade;
	public static void createConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection("jdbc:mysql://162.212.253.133:2082/revivalr_highscore","revivalr_high","!g%QsZx&(W&q");
			stmt = con.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static ResultSet query(String s) throws SQLException {
		try {
			if (s.toLowerCase().startsWith("select")) {
				ResultSet rs = stmt.executeQuery(s);
				return rs;
			} else {
				stmt.executeUpdate(s);
			}
			return null;
		} catch (Exception e) {
			destroyConnection();
			createConnection();
			e.printStackTrace();
		}
		return null;
	}

	public static void destroyConnection() {
		try {
			stmt.close();
			con.close();
			connectionMade = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean saveHighScore(Player player) {
		try {
			query("DELETE FROM `skills` WHERE playerName = '"+player.getUsername()+"';");
			query("DELETE FROM `skillsoverall` WHERE playerName = '"+player.getUsername()+"';");
			query("INSERT INTO `skills` (`playerName`,`Attacklvl`,`Attackxp`,`Defencelvl`,`Defencexp`,`Strengthlvl`,`Strengthxp`,`Hitpointslvl`,`Hitpointsxp`,`Rangelvl`,`Rangexp`,`Prayerlvl`,`Prayerxp`,`Magiclvl`,`Magicxp`,`Cookinglvl`,`Cookingxp`,`Woodcuttinglvl`,`Woodcuttingxp`,`Fletchinglvl`,`Fletchingxp`,`Fishinglvl`,`Fishingxp`,`Firemakinglvl`,`Firemakingxp`,`Craftinglvl`,`Craftingxp`,`Smithinglvl`,`Smithingxp`,`Mininglvl`,`Miningxp`,`Herblorelvl`,`Herblorexp`,`Agilitylvl`,`Agilityxp`,`Thievinglvl`,`Thievingxp`,`Slayerlvl`,`Slayerxp`,`Farminglvl`,`Farmingxp`,`Runecraftlvl`,`Runecraftxp`) VALUES ('"+player.getUsername()+"',"+player.getSkill().getLevel()[Skill.ATTACK]+","+player.getSkill().getExp()[Skill.ATTACK]+","+player.getSkill().getLevel()[Skill.DEFENCE]+","+player.getSkill().getExp()[Skill.DEFENCE]+","+player.getSkill().getLevel()[Skill.STRENGTH]+","+player.getSkill().getExp()[Skill.STRENGTH]+","+player.getSkill().getLevel()[Skill.HITPOINTS]+","+player.getSkill().getExp()[Skill.HITPOINTS]+","+player.getSkill().getLevel()[Skill.RANGED]+","+player.getSkill().getExp()[Skill.RANGED]+","+player.getSkill().getLevel()[Skill.PRAYER]+","+player.getSkill().getExp()[Skill.PRAYER]+","+player.getSkill().getLevel()[Skill.MAGIC]+","+player.getSkill().getExp()[Skill.MAGIC]+","+player.getSkill().getLevel()[Skill.COOKING]+","+player.getSkill().getExp()[Skill.COOKING]+","+player.getSkill().getLevel()[Skill.WOODCUTTING]+","+player.getSkill().getExp()[Skill.WOODCUTTING]+","+player.getSkill().getLevel()[Skill.FLETCHING]+","+player.getSkill().getExp()[Skill.FLETCHING]+","+player.getSkill().getLevel()[Skill.FISHING]+","+player.getSkill().getExp()[Skill.FISHING]+","+player.getSkill().getLevel()[Skill.FIREMAKING]+","+player.getSkill().getExp()[Skill.FIREMAKING]+","+player.getSkill().getLevel()[Skill.CRAFTING]+","+player.getSkill().getExp()[Skill.CRAFTING]+","+player.getSkill().getLevel()[Skill.SMITHING]+","+player.getSkill().getExp()[Skill.SMITHING]+","+player.getSkill().getLevel()[Skill.MINING]+","+player.getSkill().getExp()[Skill.MINING]+","+player.getSkill().getLevel()[Skill.HERBLORE]+","+player.getSkill().getExp()[Skill.HERBLORE]+","+player.getSkill().getLevel()[Skill.AGILITY]+","+player.getSkill().getExp()[Skill.AGILITY]+","+player.getSkill().getLevel()[Skill.THIEVING]+","+player.getSkill().getExp()[Skill.THIEVING]+","+player.getSkill().getLevel()[Skill.SLAYER]+","+player.getSkill().getExp()[Skill.SLAYER]+","+player.getSkill().getLevel()[Skill.FARMING]+","+player.getSkill().getExp()[Skill.FARMING]+","+player.getSkill().getLevel()[Skill.RUNECRAFTING]+","+player.getSkill().getExp()[Skill.RUNECRAFTING]+");");
			query("INSERT INTO `skillsoverall` (`playerName`,`lvl`,`xp`) VALUES ('"+player.getUsername()+"',"+(player.getSkill().getExp()[Skill.ATTACK]+ player.getSkill().getExp()[Skill.DEFENCE] + player.getSkill().getExp()[Skill.STRENGTH] + player.getSkill().getExp()[Skill.HITPOINTS] + player.getSkill().getExp()[Skill.RANGED] + player.getSkill().getExp()[Skill.PRAYER] + player.getSkill().getExp()[Skill.MAGIC] + player.getSkill().getExp()[Skill.COOKING] + player.getSkill().getExp()[Skill.WOODCUTTING] + player.getSkill().getExp()[Skill.FLETCHING] + player.getSkill().getExp()[Skill.FISHING] + player.getSkill().getExp()[Skill.FIREMAKING] + player.getSkill().getExp()[Skill.CRAFTING] + player.getSkill().getExp()[Skill.SMITHING] + player.getSkill().getExp()[Skill.MINING] + player.getSkill().getExp()[Skill.HERBLORE] + player.getSkill().getExp()[Skill.AGILITY] + player.getSkill().getExp()[Skill.THIEVING] + player.getSkill().getExp()[Skill.SLAYER] + player.getSkill().getExp()[Skill.FARMING] + player.getSkill().getExp()[Skill.RUNECRAFTING])+","+(player.getSkill().getExp()[Skill.ATTACK] + player.getSkill().getExp()[Skill.DEFENCE] + player.getSkill().getExp()[Skill.STRENGTH] + player.getSkill().getExp()[Skill.HITPOINTS] + player.getSkill().getExp()[Skill.RANGED] + (player.getSkill().getExp()[Skill.PRAYER]) + (player.getSkill().getExp()[Skill.MAGIC]) + (player.getSkill().getExp()[Skill.COOKING]) + (player.getSkill().getExp()[Skill.WOODCUTTING]) + (player.getSkill().getExp()[Skill.FLETCHING]) + (player.getSkill().getExp()[Skill.FISHING]) + (player.getSkill().getExp()[Skill.FIREMAKING]) + (player.getSkill().getExp()[Skill.CRAFTING]) + (player.getSkill().getExp()[Skill.SMITHING]) + (player.getSkill().getExp()[Skill.MINING]) + (player.getSkill().getExp()[Skill.HERBLORE]) + (player.getSkill().getExp()[Skill.AGILITY]) + (player.getSkill().getExp()[Skill.THIEVING]) + (player.getSkill().getExp()[Skill.SLAYER]) + (player.getSkill().getExp()[Skill.FARMING]) + (player.getSkill().getExp()[Skill.RUNECRAFTING]))+");");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}