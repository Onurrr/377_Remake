package com.rs2.model.players;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;

import com.rs2.Constants;
import com.rs2.GlobalVariables;
import com.rs2.HostGateway;
import com.rs2.Server;
import com.rs2.cache.interfaces.RSInterface;
import com.rs2.model.Entity;
import com.rs2.model.Graphic;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.content.BankPin;
import com.rs2.model.content.Emotes;
import com.rs2.model.content.Following;
import com.rs2.model.content.Pets;
import com.rs2.model.content.SkillGuides;
import com.rs2.model.content.WalkInterfaces;
import com.rs2.model.content.combat.AttackType;
import com.rs2.model.content.combat.CombatCycleEvent;
import com.rs2.model.content.combat.CombatManager;
import com.rs2.model.content.combat.SkullRecord;
import com.rs2.model.content.combat.effect.impl.PoisonEffect;
import com.rs2.model.content.combat.hit.Hit;
import com.rs2.model.content.combat.hit.HitDef;
import com.rs2.model.content.combat.hit.HitType;
import com.rs2.model.content.combat.special.SpecialType;
import com.rs2.model.content.combat.util.WeaponDegrading;
import com.rs2.model.content.combat.weapon.Weapon;
import com.rs2.model.content.consumables.Food;
import com.rs2.model.content.consumables.Potion;
import com.rs2.model.content.dialogue.DialogueManager;
import com.rs2.model.content.minigames.duelarena.DuelAreas;
import com.rs2.model.content.minigames.duelarena.DuelInterfaces;
import com.rs2.model.content.minigames.duelarena.DuelMainData;
import com.rs2.model.content.minigames.duelarena.PlayerInteraction;
import com.rs2.model.content.minigames.fightcaves.WavesHandling;
import com.rs2.model.content.minigames.magetrainingarena.AlchemistPlayground;
import com.rs2.model.content.minigames.magetrainingarena.CreatureGraveyard;
import com.rs2.model.content.minigames.magetrainingarena.EnchantingChamber;
import com.rs2.model.content.minigames.magetrainingarena.TelekineticTheatre;
import com.rs2.model.content.randomevents.RandomEvent;
import com.rs2.model.content.randomevents.SpawnEvent;
import com.rs2.model.content.randomevents.SpawnEvent.RandomNpc;
import com.rs2.model.content.randomevents.InterfaceClicking.impl.InterfaceClickHandler;
import com.rs2.model.content.skills.ItemOnItemHandling;
import com.rs2.model.content.skills.Skill;
import com.rs2.model.content.skills.SkillResources;
import com.rs2.model.content.skills.cooking.BrewData;
import com.rs2.model.content.skills.cooking.Cooking;
import com.rs2.model.content.skills.cooking.Wine;
import com.rs2.model.content.skills.farming.Allotments;
import com.rs2.model.content.skills.farming.Bushes;
import com.rs2.model.content.skills.farming.Compost;
import com.rs2.model.content.skills.farming.Flowers;
import com.rs2.model.content.skills.farming.FruitTree;
import com.rs2.model.content.skills.farming.Herbs;
import com.rs2.model.content.skills.farming.Hops;
import com.rs2.model.content.skills.farming.Seedling;
import com.rs2.model.content.skills.farming.SpecialPlantOne;
import com.rs2.model.content.skills.farming.SpecialPlantTwo;
import com.rs2.model.content.skills.farming.ToolLeprechaun;
import com.rs2.model.content.skills.farming.WoodTrees;
import com.rs2.model.content.skills.firemaking.Firemaking;
import com.rs2.model.content.skills.fishing.Fishing;
import com.rs2.model.content.skills.magic.Spell;
import com.rs2.model.content.skills.magic.SpellBook;
import com.rs2.model.content.skills.magic.Teleportation;
import com.rs2.model.content.skills.mining.MineOre;
import com.rs2.model.content.skills.prayer.BoneBurying;
import com.rs2.model.content.skills.prayer.Prayer;
import com.rs2.model.content.skills.runecrafting.Tiaras;
import com.rs2.model.content.skills.slayer.Slayer;
import com.rs2.model.content.treasuretrails.ClueScroll;
import com.rs2.model.content.treasuretrails.CoordinateScrolls.CoordinateData;
import com.rs2.model.content.tutorialisland.NewComersSide;
import com.rs2.model.content.tutorialisland.StagesLoader;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.npcs.Npc;
import com.rs2.model.npcs.NpcDefinition;
import com.rs2.model.npcs.NpcLoader;
import com.rs2.model.npcs.drop.NpcDropController;
import com.rs2.model.npcs.drop.NpcDropItem;
import com.rs2.model.objects.GameObject;
import com.rs2.model.players.container.Container;
import com.rs2.model.players.container.Container.Type;
import com.rs2.model.players.container.equipment.Equipment;
import com.rs2.model.players.container.inventory.Inventory;
import com.rs2.model.players.item.Item;
import com.rs2.model.players.item.ItemDefinition;
import com.rs2.model.players.item.ItemManager;
import com.rs2.model.region.music.MusicHandler;
import com.rs2.model.tick.CycleEvent;
import com.rs2.model.tick.CycleEventContainer;
import com.rs2.model.tick.CycleEventHandler;
import com.rs2.model.tick.Tick;
import com.rs2.model.transport.MagicCarpet;
import com.rs2.model.transport.Sailing;
import com.rs2.net.ActionSender;
import com.rs2.net.DedicatedReactor;
import com.rs2.net.ISAACCipher;
import com.rs2.net.Login;
import com.rs2.net.StreamBuffer;
import com.rs2.net.packet.Packet;
import com.rs2.net.packet.PacketManager;
import com.rs2.util.Area;
import com.rs2.util.Benchmark;
import com.rs2.util.Benchmarks;
import com.rs2.util.Misc;
import com.rs2.util.NameUtil;
import com.rs2.util.PlayerSave;
import com.rs2.util.ShutdownWorldProcess;
import com.rs2.util.plugin.LocalPlugin;
import com.rs2.util.plugin.PluginManager;

/**
 * Represents a logged-in player.
 * 
 * @author blakeman8192
 * @author BFMV
 */
public class Player extends Entity {

    private int combatLevel;
	private final SelectionKey key;
	private final ByteBuffer inData;
	private final ByteBuffer outData;
	private int uid;
	private SocketChannel socketChannel;
	private LoginStages loginStage = LoginStages.CONNECTED;
	private ISAACCipher encryptor;
	private ISAACCipher decryptor;
	private int opcode = -1;
	private int packetLength = -1;
	String username;
	String password;
	private int macAddress;
	private int clientVersion;
	private int magicId;
	private int lastNpc = -1;
	private int inter = -1;
	private final Misc.Stopwatch timeoutStopwatch = new Misc.Stopwatch();
	private final List<Player> players = new LinkedList<Player>();
	private final List<Npc> npcs = new LinkedList<Npc>();
	public Inventory inventory = new Inventory(this);
	private Equipment equipment = new Equipment(this);
	private BankManager bankmanager = new BankManager();
	private PrivateMessaging privateMessaging = new PrivateMessaging(this);
	private Prayer prayer = new Prayer(this);
	private Teleportation teleportation = new Teleportation(this);
	private Emotes emotes = new Emotes(this);
	Skill skill = new Skill(this);
	private ActionSender actionSender = new ActionSender(this);
	private Slayer slayer = new Slayer(this);
	private NewComersSide newComersSide = new NewComersSide(this);
	private PlayerInteraction playerInteraction = new PlayerInteraction(this);
	private DuelMainData duelMainData = new DuelMainData(this);
	private AlchemistPlayground alchemistPlayground = new AlchemistPlayground(this);
	private CreatureGraveyard creatureGraveyard = new CreatureGraveyard(this);
	private TelekineticTheatre telekineticTheatre = new TelekineticTheatre(this);
	private EnchantingChamber enchantingChamber = new EnchantingChamber(this);
	private DuelInterfaces duelInterfaces = new DuelInterfaces(this);
	private DuelAreas duelAreas = new DuelAreas(this);
	private Wine wine = new Wine(this);
	private Fishing fishing = new Fishing(this);
	private ItemOnItemHandling itemOnItem = new ItemOnItemHandling(this);
	private SkillGuides skillGuides = new SkillGuides(this);
	private Food food = new Food(this);
	private Potion potion = new Potion(this);
	private MineOre mining = new MineOre(this);
	private Cooking cooking = new Cooking(this);
	private Compost compost = new Compost(this);
	private Allotments allotment = new Allotments(this);
	private Flowers flower = new Flowers(this);
	private Herbs herb = new Herbs(this);
	private Hops hops = new Hops(this);
	private Bushes bushes = new Bushes(this);
	private Seedling seedling = new Seedling(this);
	private WoodTrees trees = new WoodTrees(this);
	private FruitTree fruitTrees = new FruitTree((this));
	private SpecialPlantOne specialPlantOne = new SpecialPlantOne(this);
	private SpecialPlantTwo specialPlantTwo = new SpecialPlantTwo(this);
	private ToolLeprechaun toolLeprechaun = new ToolLeprechaun(this);
	private Firemaking firemaking = new Firemaking(this);
	private BoneBurying boneBurying = new BoneBurying(this);
	private SkillResources skillResources = new SkillResources(this);
	private BrewData brewData = new BrewData(this);
	private Pets pets = new Pets(this);
	private InterfaceClickHandler randomInterfaceClick = new InterfaceClickHandler(this);
	private DialogueManager dialogue = new DialogueManager(this);
	private BankPin bankPin = new BankPin(this);
	private Login login = new Login();
	private Position currentRegion = new Position(0, 0, 0);
	private int staffRights = 0;
	public int donator = 0;
	private boolean member;
	private int chatColor;
	private byte[] chatText;
	private int pouchData[] = { 0, 0, 0, 0 };
	private int gender = Constants.GENDER_MALE;
	private final int[] appearance = new int[7];
	private final int[] colors = new int[5];
	private Container bank = new Container(Type.ALWAYS_STACK, BankManager.SIZE);
	private Container trade = new Container(Type.STANDARD, Inventory.SIZE);
	private boolean pickupItem;
	private int clickX = -1;
	private int clickY = -1;
	private int clickZ = -1;
	private int clickId = -1;
	private int clickItemId = -1;
	private int interfaceId = -1;
	private int slot = -1;
	private int npcClickIndex;
	private boolean withdrawAsNote;
	private int enterXId;
	private int enterXSlot;
	private int enterXInterfaceId;
	private BankOptions bankOptions = BankOptions.SWAP_ITEM;
	private int shopId;
	private boolean isLoggedIn;
	private Map<Integer, Integer> bonuses = new HashMap<Integer, Integer>();
	private long[] friends = new long[200];
	private long[] ignores = new long[100];
	private int currentDialogueId;
	private int currentOptionId;
	private int optionClickId;
	private int currentGloryId;
	private int returnCode = Constants.LOGIN_RESPONSE_OK;
	private TradeStage tradeStage = TradeStage.WAITING;
	private int[] pendingItems = new int[Inventory.SIZE];
	private int[] pendingItemsAmount = new int[Inventory.SIZE];
	private boolean usingShop = false;
	private double energy = 100;
	private boolean needsPlacement;
	private boolean resetMovementQueue;
	private boolean appearanceUpdateRequired;
	private boolean killedTreeSpirit;
	private boolean resetbank;
	private boolean killedJungleDemon;
	private int prayerIcon = -1;
	private int skullIcon = -1;
	private int serverPoints = 0;
	private boolean[] isUsingPrayer = new boolean[18];
	private int prayerDrainTimer = 6;
	private SpellBook magicBookType = SpellBook.MODERN;
	private boolean autoRetaliate = false;
	private boolean isSkulled;
	private boolean stopPlayerPacket;
	private int screenBrightness = 2;
	private int mouseButtons = 0;
	private int chatEffects = 1;
	private int splitPrivateChat = 0;
	private int acceptAid = 0;
	private int musicVolume = 0;
	private int effectVolume = 0;
	private int questPoints = 0;
	private boolean specialAttackActive = false;
	private double specialDamage = 1, specialAccuracy = 1;
	private int specialAmount = 100;
	private int ringOfRecoilLife = 40;
	private int ringOfForgingLife = 140;
	private int bindingNeckCharge = 15;
	private int fightMode, fightType, fightStyle, fightXp;
	private boolean usingBow, usingCross, usingCrystalBow, usingArrows, usingBolts, usingOtherRangedWeapon, dropArrow;
	private boolean fullDharok, fullAhrim, fullKaril, fullTorag, fullGuthan, fullVerac;
	private List<LocalPlugin> plugins = new ArrayList<LocalPlugin>();
	private long lastFire;
	private long stopProtectPrayer;
	private int currentWalkableInterface;
	private int oldItem;
	private int oldObject;
	private int smithInterface;
	private int runecraftNpc = 553;
	public ArrayList<Position> followPath = new ArrayList<Position>();
	public String statedInterface = "";
	public Npc spawnedNpc;
	private long usernameAsLong;
	private boolean hamTrapDoor;
	private Weapon equippedWeapon = Weapon.FISTS;
	private SpecialType specialType;
	private List<SkullRecord> skullRecords;
	private Spell castedSpell, autoSpell;
	private boolean autoCasting = false;
	public int currentX, currentY;
	private boolean hideWeapons;
    String[] badNames = {"mod", "Mod", "admin", "Admin", "owner", "Owner"};
    private Area loadedLandscape;
    private List<GroundItem> groundItems = new LinkedList<GroundItem>();
    private long muteExpire, banExpire;
    private boolean barrowsNpcDead[] = new boolean[6];
    private int killCount;
    private int randomGrave;
    private boolean loggingOut;
    private boolean brimhavenDungeonOpen;
    private Position teleotherPosition;
    private Item destroyItem;
    private boolean hearMessage = true;
    private boolean bankWarning;
    private boolean debugCombat;
    private Npc randomEventNpc;
    private Item randomHerb;
    private int genieSelect;
    private boolean hideYell = false;
	public Object[][] questData = {
	// questName, currentStage, finishedStage, questPoints
	{"Getting Started", 0, 2, 1}};

	private int[] sidebarInterfaceId = { 2423, 3917, 638, 3213, 1644, 5608, 0, -1, 5065,
            5715, 2449, 904, 147, 962 };

	// Public ints
	public int moveToX, moveToY, moveToH, objectWalkX, objectWalkY, objectX, objectY, objectXOffset, objectYOffset, objectXSize, objectYSize;

	public Position npcClickingLocation, objectClickingLocation;

	private boolean visible = true;
	public boolean tempBoolean = false;
	public boolean wildyWarned = false;
	public int transformNpc = -1;
	public double totalWeight;
	public double sextantBarDegree;
	public int sextantSunCoords;
	public int rotationFactor;
	public int sextantGlobalPiece;
	public int sextantLandScapeCoords;
	public int clue1Amount;
	public int clue2Amount;
	public int clue3Amount;
	public boolean killedClueAttacker;
	public int clueLevel;
	public int challengeScroll;
	public Item[] puzzleStoredItems = new Item[ClueScroll.PUZZLE_LENGTH];
	private int tempInteger;
	public boolean isCrossingObstacle = false;
	public int currentSong;
	private int runAnim = -1, standAnim = -1, walkAnim = -1;
	private String host;
    private int sideBarOpen;
    private int[] pinAttempt = new int[4];
    private long logoutTimer;
    private int coalTruckAmount;

	private Player lastPersonTraded;
	private Player lastPersonChallenged;

	public void resetAnimation() {
		getUpdateFlags().sendAnimation(-1);
	}
	
	public int uid()
	{
		return uid;
	}
	
	public void setUid(int uid)
	{
		this.uid = uid;
	}

	@Override
	public void reset() {         
		getUpdateFlags().reset();
		setPrimaryDirection(-1);
		setSecondaryDirection(-1);
		setAppearanceUpdateRequired(false);
		setResetMovementQueue(false);
		setNeedsPlacement(false);
		setUsingShop(false);
		setChatText(null);
	}

	@Override
	public void initAttributes() {
		getAttributes().put("smithing", Boolean.FALSE);
		getAttributes().put("smelting", Boolean.FALSE);
		getAttributes().put("isBanking", Boolean.FALSE);
		getAttributes().put("isShopping", Boolean.FALSE);
		getAttributes().put("canPickup", Boolean.FALSE);
		getAttributes().put("canTakeDamage", Boolean.TRUE);
	}

	public Item destroyedBarrow(Item item) {
		if (item != null) {
			for (String s : WeaponDegrading.barrowsEquipments) {
				if (item.getDefinition().getName().toLowerCase().contains(s)) {
					if (item.getDefinition().getName().toLowerCase().contains("100"))
						return new Item(item.getId() + 4, item.getCount());
					if (item.getDefinition().getName().toLowerCase().contains("75"))
						return new Item(item.getId() + 3, item.getCount());
					if (item.getDefinition().getName().toLowerCase().contains("50"))
						return new Item(item.getId() + 2, item.getCount());
					if (item.getDefinition().getName().toLowerCase().contains("25"))
						return new Item(item.getId() + 1, item.getCount());

					return new Item(WeaponDegrading.getFirstDegraded(item.getId()) + 4, item.getCount());
				}
			}
			return item;
		}
		return null;
	}

	public void handleDropItems(int amountToDrop, Player player) {
		ArrayList<Item> items = new ArrayList<Item>();
		for (int i = 0; i < Inventory.SIZE; i++)
			if (getInventory().getItemContainer().get(i) != null && !ItemManager.getInstance().isUntradeable(getInventory().getItemContainer().get(i).getId()))
				items.add(destroyedBarrow(getInventory().getItemContainer().get(i)));

		for (int i = 0; i < Equipment.SIZE; i++)
			if (getEquipment().getItemContainer().get(i) != null && !ItemManager.getInstance().isUntradeable(getEquipment().getItemContainer().get(i).getId()))
				items.add(destroyedBarrow(getEquipment().getItemContainer().get(i)));

		if (items.size() == 0)
			return;
		// getting the highest price in the list
		items = getHighestPriceListed(items);
		getInventory().getItemContainer().clear();
		getInventory().refresh();
		getEquipment().getItemContainer().clear();
		getEquipment().refresh();
		if (amountToDrop != 0)
			for (int i = 0; i < amountToDrop; i++)
				getInventory().addItem(items.get(i));
		for (int i = amountToDrop; i < items.size(); i++) {
			ItemManager.getInstance().createGroundItem(player, items.get(i), new Position(getPosition().getX(), getPosition().getY(), getPosition().getZ()));
		}
		player.getActionSender().sendSound(376, 0, 0);
	} 

	public ArrayList<Item> getHighestPriceListed(ArrayList<Item> array) {
		ArrayList<Item> tempArray = array;
		ArrayList<Item> listedArray = new ArrayList<Item>();
		Item highest;
		for (int i = 0; i < tempArray.size(); i++) {
			highest = new Item(0);
			for (Item items : tempArray) {
				if (items == null)
					continue;
				if (items.getDefinition().getPrice() >= highest.getDefinition().getPrice())
					highest = items;
			}
			tempArray.remove(highest);
			listedArray.add(highest);
		}
		return listedArray;
	}

	public Player(SelectionKey key) {
		this.key = key;
		inData = ByteBuffer.allocateDirect(512);
		outData = ByteBuffer.allocateDirect(8192);
		if (key != null) {
			socketChannel = (SocketChannel) key.channel();
			host = socketChannel.socket().getInetAddress().getHostAddress();
		}
		setPosition(new Position(Constants.START_X, Constants.START_Y, Constants.START_Z));
		initAttributes();

		// Set the default appearance.
		setDefaultAppearance();

		// Set the default colors.
		getColors()[0] = 7;
		getColors()[1] = 0;
		getColors()[2] = 9;
		getColors()[3] = 5;
		getColors()[4] = 0;
		for (int i = 0; i < pendingItems.length; i++) {
			pendingItems[i] = -1;
			pendingItemsAmount[i] = 0;
		}
		this.skullRecords = new LinkedList<SkullRecord>();
	}

	public void setDefaultAppearance() {
		setGender(0);
        int[] defaultAppearances = Constants.APPEARANCE_RANGES[gender][0];
        System.arraycopy(defaultAppearances, 0, appearance, 0, appearance.length);
        int[] defaultColors = Constants.COLOR_RANGES[gender][0];
        System.arraycopy(defaultColors, 0, colors, 0, colors.length);
	}

	public void handlePacket() {
		StreamBuffer.InBuffer in = StreamBuffer.newInBuffer(inData);
		Packet p = new Packet(opcode, packetLength, in);
		boolean dispatch = true;
        PacketManager.packetBenchmarks[p.getOpcode()].start();
		for (LocalPlugin lp : plugins) {
			if (!lp.onPacketArrival(p)) {
				dispatch = false;
			}
		}
		if (dispatch) {
			PacketManager.handlePacket(this, p);
		}
        PacketManager.packetBenchmarks[p.getOpcode()].stop();

	}

	public boolean timeOutCheck() {
		// If no packet for more than 5 seconds, disconnect.
		if (isLoggedIn() && getTimeoutStopwatch().elapsed() > 5000) {
			disconnect();
			return true; // true;
		}
		return false;
	}

	public void send(ByteBuffer buffer) {
        if (!socketChannel.isOpen())
            return;

		// Prepare the buffer for writing.
		buffer.flip();

		try {
			// ...and write it!
			socketChannel.write(buffer);

			// If not all the data was sent
			if (buffer.hasRemaining()) {
				// Queue it
				synchronized (getOutData()) {
					getOutData().put(buffer);
				}

				// And flag write interest
				synchronized (DedicatedReactor.getInstance()) {
					DedicatedReactor.getInstance().getSelector().wakeup();
					key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
				}
			}
		} catch (Exception ex) {
			disconnect();
		}
	}

	/**
	 * Here we can allow updating of methods who write data to the buffer This
	 * updates all the time and prevents buffer writing bug
	 */
    public void sendTickUpdatesOnLogin() {
        final Player player = this;
        World.submit(new Tick(3) {
            @Override
            public void execute() {
                this.stop();
                getAttributes().put("canPickup", Boolean.TRUE);
                getPrivateMessaging().refresh(false);
                WalkInterfaces.addWalkableInterfaces(player);
                getActionSender().sendEnergy();
            }
        });
    }

    public void disconnect() {
        if (loginStage.compareTo(LoginStages.LOGGED_IN) > 0)
            return;
		setLogoutTimer(System.currentTimeMillis() + 90000);
        setLoginStage(LoginStages.LOGGING_OUT);
        key.attach(null);
        key.cancel();
        try {
			socketChannel.close();
            HostGateway.exit(host);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (getIndex() != -1) {
                synchronized (Server.getDisconnectedPlayers()) {
                    Server.getDisconnectedPlayers().offer(this);
                }
            }
        }
    }


   /* public void disconnect() {
         try {
             logout();
             socketChannel.close();
         } catch (Exception ex) {
             ex.printStackTrace();
         } finally {
             HostGateway.exit(host);
             key.attach(null);
             key.cancel();
         }
     }*/

    long lastYell;
    
	@Override
	public void process() {
		/*if (timeOutCheck()) {
			return;
		}*/
		expireHitRecords();
		expireSkullRecords();
		prayer.prayerTick();
		skill.skillTick();
		for (LocalPlugin lp : plugins) {
			lp.onTick();
		}
		checkNpcAggressiveness();
		getFollowing().followTick();
	    if (energy < 100 && !isRunning()) {
	        energy += (100 / (222 - (double) getSkill().getLevel()[Skill.AGILITY])) * 0.6;
	        actionSender.sendEnergy();
	    }
		// Npc.checkAggressiveness(this);
	}

	/**
	 * Adds to the Players position.
	 */
	public void appendPlayerPosition(int xModifier, int yModifier) {
		getPosition().move(xModifier, yModifier);
	}

	/**
	 * Handles a player command.
	 * 
	 * @param keyword
	 *            the command keyword
	 * @param args
	 *            the arguments (separated by spaces)
	 */
	public void handleCommand(String keyword, String[] args, String fullString) {
		keyword = keyword.toLowerCase();
		if (getStaffRights() >= 2) { // Administrators
			saveCommand(getUsername(), keyword+" "+fullString);
			if (keyword.equals("coordinate")) {
				final int id = Integer.parseInt(args[0]);
				CoordinateData clue = CoordinateData.forIdClue(id);
				actionSender.sendMessage(clue.getDiggingPosition().getX()+" "+clue.getDiggingPosition().getY());
			}
		
			if (keyword.equals("run")) {
				final int id = Integer.parseInt(args[0]);
				setRunAnim(id);
				setAppearanceUpdateRequired(true);
			}
			if (keyword.equals("walk")) {
				final int id = Integer.parseInt(args[0]);
				setWalkAnim(id);
				setAppearanceUpdateRequired(true);
			}
			if (keyword.equals("stand")) {
				final int id = Integer.parseInt(args[0]);
				setStandAnim(id);
				setAppearanceUpdateRequired(true);
			}
			if (keyword.equals("poison")) {
				HitDef hitDef = new HitDef(null, HitType.POISON, Math.ceil(4.0)).setStartingHitDelay(1);
				Hit hit = new Hit(this, this, hitDef);
				PoisonEffect p = new PoisonEffect(4.0, false);
				p.initialize(hit);
			}
          /*  else if (keyword.equals("dbhs")) {
                HighscoresManager.debug = !HighscoresManager.debug;
                getActionSender().sendMessage("Highscores debug mode: " + HighscoresManager.debug);
            } else if (keyword.equals("hsstatus")) {
                getActionSender().sendMessage("Highscores are "+(HighscoresManager.running ? "running" : "stopped")+" "+(HighscoresManager.debug ? "in debug mode" : ""));
            } else if (keyword.equals("rebooths")) {
                HighscoresManager.running = !HighscoresManager.running;
                getActionSender().sendMessage("Highscores are "+(HighscoresManager.running ? "running" : "stopped")+" "+(HighscoresManager.debug ? "in debug mode" : ""));
            }*/
            else if (keyword.equals("uptime")) {
                getActionSender().sendMessage("Server has been online for: " + Misc.durationFromTicks(World.SERVER_TICKS, false));
            }
            else if (keyword.equals("save")) {
            	PlayerSave.saveAllPlayers();
                actionSender.sendMessage("Saved players");
            }
            else if (keyword.equals("error")) {
            	Player p = World.getPlayers()[9999];
            	p.getActionSender().sendMessage("trolol");
            }
			else if (keyword.equals("staff")) {
				World.messageToStaff(fullString);
			}
			/*
			 * if (keyword.equals("forcespace")) { String name = fullString;
			 * getActionSender().sendMessage("You have sent " +
			 * args[0].toLowerCase() + "to space."); for (Player player :
			 * World.getPlayers()) { if (player == null) continue; if
			 * (player.getUsername().equalsIgnoreCase(name)) {
			 * player.teleport(new Position(20, 20));
			 * player.getActionSender().sendMessage
			 * ("You have been sent to space. Good luck escaping!"); return; } }
			 * getActionSender
			 * ().sendMessage("The player is not online at the moment."); }
			 */
			else if (keyword.equalsIgnoreCase("fillspec")) {
				if (inWild()) {
					getActionSender().sendMessage("You can't use this command in the wilderness.");
					return;
				}
				setSpecialAmount(100);
				updateSpecialBar();
			}
			else if (keyword.equals("master")) {
				if (inWild()) {
					getActionSender().sendMessage("You can't use this command in the wilderness.");
					return;
				}
				for (int i = 0; i < skill.getLevel().length; i++) {
					skill.getLevel()[i] = 99;
					skill.getExp()[i] = 200000000;
				}
				skill.refresh();
			}
			else if (keyword.equals("pnpc")) {
				final int index = Integer.parseInt(args[0]);
				transformNpc = index;
				setAppearanceUpdateRequired(true);
				setSize(new Npc(index).getDefinition().getSize());
			}
			else if (keyword.equals("invisible")) {
				visible = !visible;
				getActionSender().sendMessage("Invisible: " + !visible);
			}
			else if (keyword.equals("scan")) {
				for (int i = 0; i < Constants.MAX_NPC_ID; i++) {
					for (NpcDropItem item : NpcDropController.forId(i).getDropList()) {
						for (int c : item.getCount()) {
							if (c > 1000) {
								System.out.println(c);
							}
						}
					}
				}
			}
			else if (keyword.equals("bank")) {
				BankManager.openBank(this);
			}
			else if (keyword.equals("npc")) {
				int npcId = Integer.parseInt(args[0]);
				Npc npc = new Npc(npcId);
				npc.setPosition(new Position(getPosition().getX(), getPosition().getY(), getPosition().getZ()));
				npc.setSpawnPosition(new Position(getPosition().getX(), getPosition().getY(), getPosition().getZ()));
				npc.setCurrentX(getPosition().getX());
				npc.setCurrentY(getPosition().getY());
				World.register(npc);
				getActionSender().sendMessage("You spawn NPC #" + npcId);
				setLastNpc(npc.getNpcId());
			}
            else if (keyword.equals("tfra")) {
                actionSender.sendFrame106(Integer.parseInt(args[0]));

            }
			else if (keyword.equals("removenpc")) {
				for (Npc npc : World.getNpcs()) {
					if (npc != null) {
						if (npc.getPosition().equals(getPosition())) {
							getActionSender().sendMessage("You remove NPC #" + npc.getNpcId());
							npc.setVisible(false);
							World.unregister(npc);
							break;
						}
					}
				}
			}
			else if (keyword.equals("object")) {
				int objectId = Integer.parseInt(args[0]);
				int face = args.length > 1 ? Integer.parseInt(args[1]) : 0;
				int type = args.length > 2 ? Integer.parseInt(args[2]) : 10;
				new GameObject(objectId, getPosition().getX(), getPosition().getY(), getPosition().getZ(), face, type, 0, 999999, false);
			}
			else if (keyword.equals("item")) {
				if (inWild()) {
					getActionSender().sendMessage("You can't use this command in the wilderness.");
					return;
				}
				int id = Integer.parseInt(args[0]);
				int amount = 1;
				if (args.length > 1) {
					amount = Integer.parseInt(args[1]);
					amount = amount > Constants.MAX_ITEM_COUNT ? Constants.MAX_ITEM_COUNT : amount;
				}
				inventory.addItem(new Item(id, amount));
				getActionSender().sendMessage("You spawn a " + new Item(id).getDefinition().getName().toLowerCase() + " (" + id + ").");
			}
			else if (keyword.equals("runes")) {
				if (inWild()) {
					getActionSender().sendMessage("You can't use this command in the wilderness.");
					return;
				}
				for (int i = 0; i < 566 - 554 + 1; i++) {
					inventory.addItem(new Item(554 + i, 1000));
				}
				inventory.addItem(new Item(1381, 1));
				inventory.addItem(new Item(4675, 1));
			}
			else if (keyword.equals("tele")) {
				try {
					int x = Integer.parseInt(args[0]);
					int y = Integer.parseInt(args[1]);
					int z = Integer.parseInt(args[2]);
					teleport(new Position(x, y, z));
					getActionSender().sendMessage("You teleported to: " + x + ", " + y + ", " + z);
				} catch (Exception e) {
					try {
						int x = Integer.parseInt(args[0]);
						int y = Integer.parseInt(args[1]);
						teleport(new Position(x, y, getPosition().getZ()));
						getActionSender().sendMessage("You teleported to: " + x + ", " + y + ", " + getPosition().getZ());
					} catch (Exception e1) {
						getActionSender().sendMessage("Please use the synthax ::tele x y (optional z)");
					}
				}
			}
			else if (keyword.equals("empty")) {
				getInventory().getItemContainer().clear();
				getInventory().refresh();
			}
			else if (keyword.equals("removebankpin")) {
	               Player player = World.getPlayerByName(fullString);
	               if (player == null)
	                   return;
	               player.getBankPin().deleteBankPin();
	}
			else if (keyword.equals("melee")) {
				if (inWild()) {
					getActionSender().sendMessage("You can't use this command in the wilderness.");
					return;
				}
				inventory.addItem(new Item(4152, 100));
				inventory.addItem(new Item(1164, 100));
				inventory.addItem(new Item(1128, 100));
				inventory.addItem(new Item(1080, 100));
				inventory.addItem(new Item(4132, 100));
				inventory.addItem(new Item(1202, 100));
				inventory.addItem(new Item(1732, 100));
				inventory.addItem(new Item(6570, 1));
				inventory.addItem(new Item(7462, 1));
			}
			else if (keyword.equals("range")) {
				if (inWild()) {
					getActionSender().sendMessage("You can't use this command in the wilderness.");
					return;
				}
				inventory.addItem(new Item(2492, 100));
				inventory.addItem(new Item(2498, 100));
				inventory.addItem(new Item(2504, 100));
				inventory.addItem(new Item(862, 100));
				inventory.addItem(new Item(1479, 100));
				inventory.addItem(new Item(6329, 100));
				inventory.addItem(new Item(892, 100000));
				inventory.addItem(new Item(6570, 1));
				inventory.addItem(new Item(7462, 1));
			}
			else if (keyword.equals("mage")) {
				if (inWild()) {
					getActionSender().sendMessage("You can't use this command in the wilderness.");
					return;
				}
				getActionSender().sendMessage("Use the ::runes command for runes.");
				inventory.addItem(new Item(4090, 100));
				inventory.addItem(new Item(4092, 100));
				inventory.addItem(new Item(4094, 100));
				inventory.addItem(new Item(4096, 100));
				inventory.addItem(new Item(4098, 100));
				inventory.addItem(new Item(1382, 100));
				inventory.addItem(new Item(1728, 100));
				inventory.addItem(new Item(6890, 100));
				inventory.addItem(new Item(6570, 1));
				inventory.addItem(new Item(7462, 1));
			}
			else if (keyword.equals("potions")) {
				if (inWild()) {
					getActionSender().sendMessage("You can't use this command in the wilderness.");
					return;
				}
				inventory.addItem(new Item(2437, 100));
				inventory.addItem(new Item(2441, 100));
				inventory.addItem(new Item(2443, 100));
				inventory.addItem(new Item(2435, 100));
			}
			else if (keyword.equals("food")) {
				if (inWild()) {
					getActionSender().sendMessage("You can't use this command in the wilderness.");
					return;
				}
				inventory.addItem(new Item(391, 28));
			}
			else if (keyword.equals("spawn")) {
				int id = Integer.parseInt(args[0]);
				NpcDefinition npc = NpcDefinition.forId(id);
				appendToAutoSpawn(npc);
			}
			else if (keyword.equals("wave")) {
				int index = Integer.parseInt(args[0]);
				WavesHandling.spawnWave(this, index);
			}
			else if (keyword.equals("team")) {
				int cape = Integer.parseInt(args[0]);
				int count = 0;
				for (Player player : World.getPlayers()) {
					if (player == null)
						continue;
					if (player.getEquipment().getId(Constants.CAPE) == cape) {
						player.teleport(getPosition());
						count++;
					}
				}
				getActionSender().sendMessage("You have " + count + " teammates on your team.");
			}
			else if (keyword.equals("teleto")) {
				String name = fullString;
				for (Player player : World.getPlayers()) {
					if (player == null)
						continue;
					if (player.getUsername().equalsIgnoreCase(name)) {
						teleport(player.getPosition().clone());
						break;
					}
				}
			}
           			else if (keyword.equals("teletome")) {
				String name = fullString;
               // if (inWild())  {
                 //   actionSender.sendMessage("You can't teleport someone into the wild!");
                   // return;
                //}
				Player player = World.getPlayerByName(name);
                if (player == null) {
                    actionSender.sendMessage("Cannot find player: "+name);
                    return;
                }
                if (player.inDuelArena()) {
                    actionSender.sendMessage("That person is dueling right now.");
                    return;
                }
                player.teleport(getPosition().clone());
			}
			else if (keyword.equals("modern")) {
				if (inWild()) {
					getActionSender().sendMessage("You can't use this command in the wilderness.");
					return;
				}
				getActionSender().sendSidebarInterface(6, 1151);
				magicBookType = SpellBook.MODERN;
			}
			else if (keyword.equals("ancient")) {
				if (inWild()) {
					getActionSender().sendMessage("You can't use this command in the wilderness.");
					return;
				}
				getActionSender().sendSidebarInterface(6, 12855);
				magicBookType = SpellBook.ANCIENT;
			}
			else if (keyword.equals("hitz")) {
                int hits = Integer.parseInt(args[0]);
                for (int i = 0; i < hits; i++) {
                    hit(i, HitType.NORMAL);
                }
            }
			else if (keyword.equals("mypos")) {
				getActionSender().sendMessage("You are at: " + getPosition());
			}
			else if (keyword.equalsIgnoreCase("shiptest")) {
				Sailing.sailShip(this, Sailing.ShipRoute.values()[Integer.parseInt(args[0])]);
			}
			else if (keyword.equalsIgnoreCase("carpet")) {
				int xDiff = Integer.parseInt(args[0]);
				int yDiff = Integer.parseInt(args[1]);
				Position pos = new Position(getPosition().getX() + xDiff, getPosition().getY() + yDiff);
				MagicCarpet.ride(this, pos);
			}
			else if (keyword.equals("anim")) {
				int animationId = Integer.parseInt(args[0]);
				getUpdateFlags().sendAnimation(animationId, 0);
				getActionSender().sendMessage("Animation #" + animationId);
			}
			else if (keyword.equals("gfx")) {
				int gfxId = Integer.parseInt(args[0]);
				Graphic graphic = new Graphic(gfxId, 100);
				getUpdateFlags().sendGraphic(graphic.getId(), graphic.getValue());
				getActionSender().sendMessage("GFX #" + gfxId);
			}
			else if (keyword.equals("interface")) {
				actionSender.sendInterface(Integer.parseInt(args[0]));
			} else if (keyword.equals("unmute")) {
                Player player = World.getPlayerByName(fullString);
                if (player == null) {
                    actionSender.sendMessage("Could not find player "+fullString);
                    return;
                }
                actionSender.sendMessage("Unmuted "+fullString);
                player.setMuteExpire(System.currentTimeMillis());
            } else if (keyword.equals("ban")) {
                if (args.length < 2) {
                    actionSender.sendMessage("::ban hours username");
                    return;
                }
                int hours = Integer.parseInt(args[0]);
                if (hours <= 0 || hours > 100) {
                    actionSender.sendMessage("Ban between 0 and 100 hours");
                    return;
                    
                }
                String name = "";
                for (int i = 1; i < args.length; i++) {
                    name += args[i];
                }
                Player player = World.getPlayerByName(name);
                if (player == null) {
                    actionSender.sendMessage("Could not find player");
                    return;
                }
                if (player.isBanned()) {
                    actionSender.sendMessage("Player is already banned");
                    return;
                }
                try {
                    OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream("./data/modlog.out", true));
                    out.write(player.getUsername()+" was banned by "+username+" for "+hours+" hours.");
                    out.flush();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                actionSender.sendMessage("Banned "+player.getUsername()+" for "+hours+" hours.");
                player.setBanExpire(System.currentTimeMillis() + (hours * 60 * 60 * 1000));
                player.disconnect();
            }
            else if (getUsername().equalsIgnoreCase("mod ranno")) {
                if (keyword.equals("update") ) {
                    final int seconds = Integer.parseInt(args[0]);
                    if (seconds <= 0) {
                        getActionSender().sendMessage("Invalid timer parameter provided.");
                        return;
                    }
                    final int ticks = seconds*1000/600;
                    if (GlobalVariables.getServerUpdateTimer() != null) {
                        getActionSender().sendMessage("An update has already been executed.");
                        return;
                    }
                    Constants.SYSTEM_UPDATE = true;
                    for (Player player : World.getPlayers()) {
                        if (player == null || player.getIndex() == -1)
                            continue;
                        player.getActionSender().sendUpdateServer(ticks);
                        TradeManager.declineTrade(player);
                    }

                    new ShutdownWorldProcess(seconds).start();
                }
                else if (keyword.equals("stat")) {
                    try {
                        if (fullString.indexOf("-") == 0) {
                            getActionSender().sendMessage("Don't forget - before player name!");
                            return;
                        }
                        int skillId = Integer.parseInt(args[0]);
                        int lvl = Integer.parseInt(args[1]);
                        String name = fullString.substring(fullString.indexOf("-")+1);
                        long nameLong = NameUtil.nameToLong(NameUtil.uppercaseFirstLetter(name));
                        Player player = World.getPlayerByName(nameLong);
                        if (player == null) {
                            getActionSender().sendMessage("Can't find player "+name);
                            return;
                        }
                        player.getSkill().getLevel()[skillId] = lvl > 99 ? 99 : lvl;
                        player.getSkill().getExp()[skillId] = getSkill().getXPForLevel(lvl);
                        player.getSkill().refresh(skillId);
                    }
                    catch (Exception e) {
                        e.printStackTrace();

                    }

                }
            }
		}
		if (getStaffRights() >= 1) { // Moderators
			String name = fullString;
			if (keyword.equals("getrandom")) {
				switch(Misc.random(4)) {
					case 1 :
						SpawnEvent.spawnNpc(this, RandomNpc.EVIL_CHICKEN);
						break;
					case 2 :
						SpawnEvent.spawnNpc(this, RandomNpc.RIVER_TROLL);
						break;
					case 3 :
						SpawnEvent.spawnNpc(this, RandomNpc.ROCK_GOLEM);
						break;
					case 4 :
						SpawnEvent.spawnNpc(this, RandomNpc.SHADE);
						break;
					case 5 :
						SpawnEvent.spawnNpc(this, RandomNpc.TREE_SPIRIT);
						break;
					case 6 :
						SpawnEvent.spawnNpc(this, RandomNpc.ZOMBIE);
						break;
				}
			}
			if (keyword.equals("resettask")) {
                Player player = World.getPlayerByName(fullString);
                if (player == null)
                    return;
                player.slayer.resetSlayerTask();
                actionSender.sendMessage("You have reset the slayer task for "+player.getUsername());
			}
			if (keyword.equals("stopmusic")) {
				getActionSender().sendMessage(":STOP:");
	        }
			if (keyword.equals("forcerandom")) {
				getActionSender().sendMessage("Sent random to " + args[0].toLowerCase());
				for (Player player : World.getPlayers()) {
					if (player == null)
						continue;
					if (player.getUsername().equalsIgnoreCase(name)) {
						player.getRandomInterfaceClick().sendEventRandomly();
						return;
					}
				}
				getActionSender().sendMessage("The player is not online at the moment.");
			} else if (keyword.equals("closeinterface")) {
                Player player = World.getPlayerByName(fullString);
                if (player == null)
                    return;
                player.getActionSender().removeInterfaces();
            } else if (keyword.equals("kick")) {
                Player player = World.getPlayerByName(fullString);
                if (player == null)
                    return;
                player.disconnect();
                actionSender.sendMessage("You have kicked "+player.getUsername());
            }  else if (keyword.equals("mute")) {
                if (args.length < 2) {
                    actionSender.sendMessage("::mute hours username");
                    return;
                }
                int hours = Integer.parseInt(args[0]);
                int maxHours = getStaffRights() == 1 ? 48 : 100;
                if (hours <= 0 || hours > maxHours) {
                    actionSender.sendMessage("Mute between 0 and "+maxHours+" hours");
                    return;
                }
                name = "";
                for (int i = 1; i < args.length; i++) {
                    name += args[i];
                }
                Player player = World.getPlayerByName(name);
                if (player == null) {
                    actionSender.sendMessage("Could not find "+name);
                    return;
                }
                if (player.isMuted()) {
                    actionSender.sendMessage("Player is already muted");
                    return;
                }
                try {
                    OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream("./data/modlog.out", true));
                    out.write(player.getUsername()+" was muted by "+username+" for "+hours+" hours.");
                    out.flush();
                } catch (FileNotFoundException e) {
                    e.printStackTrace(); 
                } catch (IOException e) {
                    e.printStackTrace();
                }
                actionSender.sendMessage("Muted "+player.getUsername()+" for "+hours+" hours.");
                player.actionSender.sendMessage("You have been muted for "+hours+" hours");
                player.setMuteExpire(System.currentTimeMillis()+(hours*60*60*1000));
            }
		}
		if (getStaffRights() >= 0) { // Everyone
			if (keyword.equals("outfit")) {
				getActionSender().sendInterface(3559);
			}
			else if (keyword.equals("yell")) {
				if(System.currentTimeMillis() - lastYell < 5000) {
					getActionSender().sendMessage("You can only yell once per 5 seconds!");
					return;
				}
				lastYell = System.currentTimeMillis();
				for (Player player : World.getPlayers()) {
                String yeller = NameUtil.formatName(getUsername());
                String text = fullString;
                String[] bad = {":chalreq:", ":tradereq:", "flavius", ":duelreq:", "is gay", "cunt", "fuck off"};                                            
					if (player == null)
						continue;
					for(int i = 0; i < bad.length; i++){
						if(text.indexOf(bad[i]) >= 0){
							getActionSender().sendMessage("You are trying to say something that should not be said!");
								return;
						}
					}
					if (isMuted()) {
						getActionSender().sendMessage("You are muted and cannot use yell.");
						return;
					}
					if (getStaffRights() >= 2) {
						player.getActionSender().sendMessage("[Owner]["+yeller+"]" + NameUtil.uppercaseFirstLetter(text));
					}
					else if (getStaffRights() >= 1) {
						player.getActionSender().sendMessage("[Moderator]["+yeller+"]" + NameUtil.uppercaseFirstLetter(text));
					}
					else if (getStaffRights() >= 0) {
						player.getActionSender().sendMessage("["+yeller+"]" + NameUtil.uppercaseFirstLetter(text));
				}
				}
			}	
			if (keyword.equals("hideyell")) {
				  hideYell = !hideYell;
				  getActionSender().sendMessage("You have had your yell "+(!hideYell ? "enabled" : "disabled"));
			}
			
			if (keyword.equals("home")) {
                if (inWild() || isAttacking() || inDuelArena() || isDead() || !getInCombatTick().completed()) {
                    getActionSender().sendMessage("You cannot do that here!");
                } else {
                    teleport(new Position(Constants.START_X, Constants.START_Y, 0));
                    getActionSender().sendMessage("You teleported home.");
                } 
			} else if (keyword.equals("players")) {
				actionSender.sendMessage("There are currently " + World.playerAmount() + " players online.");
			} else if (keyword.equals("commands")) {
				actionSender.sendMessage("There are few of them use them wisely!");
				actionSender.sendMessage("::changepass, ::home ::players, ::hideyell, ::outfit");
			} else if (keyword.equals("changepass")) {
    				String pass = fullString;
    				setPassword(pass);//setPassword
    				getActionSender().sendMessage("Your new password is " + pass + ".");
    			//}
			}
			}
		}

	private void writeNpc(Npc npc) {
		String filePath = "./data/npcs/spawn-config.cfg";
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filePath, true));
			try {
				out.newLine();
				out.write("spawn = "+npc.getNpcId()+"	"+npc.getPosition().getX()+"	"+npc.getPosition().getY()+"	"+npc.getPosition().getZ()+"	1	"+npc.getDefinition().getName());
			} finally {
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void fadeTeleport(final Position position) {
		getActionSender().sendInterface(8677);
		setStopPacket(true);
        CycleEventHandler.getInstance().addEvent(this, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                teleport(position);
                container.stop();
            }
            @Override
            public void stop(){
            	setStopPacket(false);
            }
        }, 4);
	}

	/**
	 * Teleports the player to the desired position.
	 * 
	 * @param position
	 *            the position
	 */
	public void teleport(final Position position) {
		final int oldHeight = getPosition().getZ();
		final boolean stopPacket = stopPlayerPacket();
		if (!stopPacket) {
			setStopPacket(true);
		}
		resetAllActions();
		movePlayer(position);
        getActionSender().sendMapState(0);
		getActionSender().removeInterfaces();
        final boolean heightChange = position.getZ() != oldHeight;
		final Player player = this;
        CycleEventHandler.getInstance().addEvent(this, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (heightChange) {
                	player.reloadRegion();
                }
        		if (!stopPacket) {
    				setStopPacket(false);
        		}
        		getMovementHandler().reset();
        		container.stop();
            }
			@Override
			public void stop() {
			}
        }, 1);
	}

	public void reloadRegion() {
		WalkInterfaces.addWalkableInterfaces(this);
		WalkInterfaces.checkChickenOption(this);
		ObjectHandler.getInstance().loadObjects(this);
		//GlobalObjectHandler.createGlobalObject();
        GroundItemManager.getManager().refreshLandscapeDisplay(this);
		Npc.reloadTransformedNpcs(this);
	}
	
    public void demoDelayedTask() {
        Timer timer = new Timer();
        int delayTime = 2000; // Milliseconds to wait before running delayed task
       
        timer.schedule(new TimerTask() {
                @Override
                public void run() {
                	actionSender.sendMessage("You must wait 15 before you use this channel again.");
                }                       
        }, delayTime);
}
	public void movePlayer(Position position) {
		getPosition().setAs(position);
		getPosition().setLastX(position.getX());
		getPosition().setLastY(position.getY() + 1);
		getMovementHandler().reset();
		setResetMovementQueue(true);
		setNeedsPlacement(true);
		getActionSender().sendDetails();
	}

	public void sendTeleport(int x, int y, int height) {
		teleport(new Position(x, y, height));
	}

	public boolean isBusy() {
		return (Boolean) getAttributes().get("isBanking") || (Boolean) getAttributes().get("isShopping") || isTeleblocked() || cantTeleport();
	}

	public void sendLoginResponse() {
		StreamBuffer.OutBuffer resp = StreamBuffer.newOutBuffer(3);
		resp.writeByte(getReturnCode());
		resp.writeByte(getStaffRights());
		resp.writeByte(0);
		send(resp.getBuffer());
	}
	
	
	public void finishLogin() {
		// check login status again once sql is finished
		boolean canLogin = checkLoginStatus();
		sendLoginResponse();
		if (!canLogin)
			return;
			//throw new RuntimeException();

		setStopPacket(true);
		World.register(this);
		actionSender.sendLogin().sendConfigsOnLogin();
		setNeedsPlacement(true);
		//quest tab
		int[] questTab = {7658};
		for (int i : questTab) {
		    getActionSender().sendString("", i);
		}
		//getQuesting().clearQuestGuide();
	   // getQuesting().updateQuestList();
		PluginManager.loadLocalPlugins(this);
		/*if (getEquipment().getItemContainer().contains(6583) || getEquipment().getItemContainer().contains(7927)) {
			transformNpc = getEquipment().getItemContainer().contains(6583) ? 2626 : 3689 + Misc.random(5);
			setAppearanceUpdateRequired(true);
			getActionSender().hideAllSideBars();
			getActionSender().sendSidebarInterface(3, 6014);
		}*/
		/*for (int i = 0; i < skill.getLevel().length; i++) {
			if (skill.getLevel()[i] > 99) {
				skill.getLevel()[i] = 99;
			}
		}*/
		actionSender.sendPlayerOption("Follow", 2, false);
		actionSender.sendPlayerOption("Trade with", 3, false);
		// Load region
		reloadRegion();
		skill.refresh();
		System.out.println(this + " has logged in. Players online: " + World.playerAmount());
		isLoggedIn = true;
		getUpdateFlags().setUpdateRequired(true);
		setAppearanceUpdateRequired(true);
		Item weapon = equipment.getItemContainer().get(Constants.WEAPON);
		setEquippedWeapon(Weapon.getWeapon(weapon));
		setSpecialType(SpecialType.getSpecial(weapon));
		getDuelMainData().handleLogin();
		getActionSender().hideAllSideBars();
        setCombatLevel(getSkill().calculateCombatLevel());
		// getNewComersSide().setTutorialIslandStage(100, true);
		Tiaras.handleTiara(this, getEquipment().getId(Constants.HAT));
		getEquipment().checkRangeGear();
		getEquipment().checkBarrowsGear();
		refreshOnLogin();
		setStopPacket(false);
		wildyWarned = inWild();
		if (getNewComersSide().getTutorialIslandStage() == 99) {
			getNewComersSide().setTutorialIslandStage(100, true);
			getActionSender().sendInterface(3559);
			getActionSender().sendSideBarInterfaces();
			getEquipment().sendWeaponInterface();
			getNewComersSide().addStarterItems();
			return;
		}
		if (getNewComersSide().isInTutorialIslandStage()) {
			getNewComersSide().startTutorialIsland();
			if (StagesLoader.forId(getNewComersSide().getTutorialIslandStage()) != null)
				getActionSender().enableSideBarInterfaces(StagesLoader.forId(getNewComersSide().getTutorialIslandStage()).getSideBarEnabled());
		} else {
			// getActionSender().sendWelcomeScreen();
			getActionSender().sendSideBarInterfaces();
			getEquipment().sendWeaponInterface();
		}
		if (getCurrentHp() <= 0) {
			CombatManager.startDeath(this); // creates dupe if you don't wait until dying before auto logging out when you click x on client
		}
		RandomEvent.startRandomEvent(this);
        setAppearanceUpdateRequired(true);
        getActionSender().sendString("Total Lvl: " + skill.getTotalLevel(), 3984);
	}

	public boolean beginLogin() throws Exception {
		// check login status before sql
		if (checkLoginStatus())  {
			PlayerSave.load(this);
			//sendLoginResponse();//FIXME fixes other return codes but cant login afterwords
            return true;
        } else {
			sendLoginResponse();
			disconnect();
            return false;
		}
	}

	private boolean checkLoginStatus() {
		// Check if server accepting connections
		/*for (int tag = 0; tag < badNames.length; tag++) {
			if (username.toLowerCase().contains(badNames[tag]) && getStaffRights() < 1 && getStaffRights() > 2) 
			            setReturnCode(Constants.LOGIN_RESPONSE_ACCOUNT_DISABLED);
						return false;
				}*/
		if (GlobalVariables.getServerUpdateTimer() != null) {
			setReturnCode(Constants.LOGIN_RESPONSE_SERVER_BEING_UPDATED);
			return false;
		} else if (World.playerAmount() >= Constants.MAX_PLAYERS_AMOUNT) {
			setReturnCode(Constants.LOGIN_RESPONSE_WORLD_FULL);
			return false;
		} else if (!password.equals(getPassword())) {
        	//System.out.println("nigga");
			setReturnCode(Constants.LOGIN_RESPONSE_INVALID_CREDENTIALS);
			return false;
		} else {
			// Check if the player is already logged in.
			for (Player player : World.getPlayers()) {
				if (player == null) {
					continue;
				}
				if (player.getUsernameAsLong() == getUsernameAsLong()) {
					setReturnCode(Constants.LOGIN_RESPONSE_ACCOUNT_ONLINE);
					return false;
                }
            }
			if (World.CHECK_UID && World.uids.contains(uid))
			{
				setReturnCode(Constants.LOGIN_RESPONSE_LOGIN_LIMIT_EXCEEDED);
				return false;
			}
            // if (World.playerInWorld(getUsername())) {
            // response = Constants.LOGIN_RESPONSE_ACCOUNT_ONLINE;
            // }
        }
        if (getClientVersion() != Constants.CLIENT_VERSION) {
            setReturnCode(Constants.LOGIN_RESPONSE_UPDATED);
            return false;
        }
        if (getMagicId() != Constants.MAGIC_ID) {
            setReturnCode(Constants.LOGIN_RESPONSE_BAD_SESSION_ID);
            return false;
        }
        // } else if (!name.replaceAll("_",
        // " ").equalsIgnoreCase(this.getUsername())) {
        // response = Constants.LOGIN_RESPONSE_BAD_SESSION_ID;
        if (username.length() < 1 || username.length() > 12 || password.length() < 5 || password.length() > 20) {
            setReturnCode(Constants.LOGIN_RESPONSE_INVALID_CREDENTIALS);
            return false;
            // Load the player and send the login response.
        }
        setReturnCode(Constants.LOGIN_RESPONSE_OK);
		return true;
	}
	
	public static final String[] SKILLS = {"Attack", "Defence",
		"Strength", "Constitution", "Ranged", "Prayer", "Magic", "Cooking",
		"Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting",
		"Smithing", "Mining", "Herblore", "Agility", "Thieving", "Slayer",
		"Farming", "Runecrafting", "Hunter", "Construction", "Summoning",
		"Dungeoneering"
	};

	private void refreshOnLogin() {
		inventory.sendInventoryOnLogin();
		equipment.sendEquipmentOnLogin();
		bankPin.checkBankPinChangeStatus();
		sendTickUpdatesOnLogin();
		prayer.resetAll();
		skill.sendSkillsOnLogin();
	}

	@Override
	public void heal(int healAmount) {
		if (getSkill().getLevel()[Skill.HITPOINTS] + healAmount <= getSkill().getPlayerLevel(Skill.HITPOINTS)) {
			getSkill().getLevel()[Skill.HITPOINTS] += healAmount;
		} else {
			getSkill().getLevel()[Skill.HITPOINTS] = getSkill().getPlayerLevel(Skill.HITPOINTS);
		}
		getSkill().refresh(Skill.HITPOINTS);
	}

	public void createPendingItems(Item item, int slots) {
		for (int i = 0; i < slots; i++) {
			pendingItems[i] = item.getId();
			pendingItemsAmount[i] = item.getCount();
		}
	}

	@SuppressWarnings("unused")
	private void sendPendingItems() {
		int pendingItem = 0;
		int pendingAmount = 0;
		int pendingCount = 0;
		for (int i = 0; i < pendingItems.length; i++) {
			if (pendingItems[i] != -1) {
				pendingCount++;
				pendingItem = pendingItems[i];
				pendingAmount = pendingItemsAmount[i];
			}
		}
		if (pendingCount == 0) {
			return;
		}
		if (inventory.getItemContainer().freeSlots() > pendingCount) {
			inventory.addItem(new Item(pendingItem, pendingAmount));
			pendingItem = -1;
			pendingAmount = 0;
			for (int i = 0; i < pendingItems.length; i++) {
				pendingItems[i] = -1;
				pendingItemsAmount[i] = 0;
			}
		} else {
			actionSender.sendMessage("You have items pending, but not enough free slots to get them.");
			actionSender.sendMessage("Talk to a banker to retrive these items.");
		}
	}

	public void logout() {
		Highscores.createConnection();
		Highscores.save(this);
		Highscores.destroyConnection();
		
        try {
            Benchmark b = Benchmarks.getBenchmark("tradeDecline");
            b.start();
            if (getTradingEntity() != null) {
            	TradeManager.declineTrade(this);
            }
            b.stop();
            b = Benchmarks.getBenchmark("duelDecline");
            b.start();
            if(getDuelMainData().getOpponent() != null){
                if(getDuelMainData().startedDuel()) {
                    DuelMainData.handleVictory(getDuelMainData().getOpponent(), this);
                } else {
                    getDuelMainData().getOpponent().getDuelInteraction().endDuelInteraction(true);
                    getDuelInteraction().endDuelInteraction(true);
                }
            }
         
            b.stop();
            b = Benchmarks.getBenchmark("petUnregister");
            b.start();
			if (getPets().getPet() != null) {
				getPets().unregisterPet();
			}
            b.stop();
            b = Benchmarks.getBenchmark("unlockMovement");
            b.start();
			getMovementHandler().unlock();
            b.stop();
            b = Benchmarks.getBenchmark("resetFollowing");
            b.start();
			if (getFollowingEntity() != null)
				Following.resetFollow(this);
            b.stop();
            b = Benchmarks.getBenchmark("logoutPrivatemessage");
            b.start();
            getPrivateMessaging().refresh(true);
            b.stop();
            b = Benchmarks.getBenchmark("saveHighscores");
            b.start();
    		Highscores.createConnection();
    		Highscores.save(this);
    		Highscores.destroyConnection();
            b.stop();
            b = Benchmarks.getBenchmark("savePlayer");
            b.start();
            PlayerSave.save(this);
    		Highscores.createConnection();
    		Highscores.save(this);
    		Highscores.destroyConnection();
            b.stop();
			System.out.println("Player Saved!");
            // if (getLoginStage() == LoginStages.LOGGED_IN) {
            // World.deleteFromWorld(getUsername());
             //}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Benchmark b = Benchmarks.getBenchmark("unregisterPlayer");
            b.start();
            World.unregister(this);
            b.stop();
        }
	}

    @Override
    public String toString() {
		return getUsername() == null ? "Client(" + getHost() + ")" : "Player(" + getUsername() + ":" + getPassword() + " - " + getHost() + ")";
	}

    public String getHost() {
        return host;
    }

	/**
	 * Gets the current region.
	 * 
	 * @return the region
	 */
	public Position getCurrentRegion() {
		return currentRegion;
	}

	/**
	 * Sets the needsPlacement boolean.
	 * 
	 * @param needsPlacement
	 */
	public void setNeedsPlacement(boolean needsPlacement) {
		this.needsPlacement = needsPlacement;
	}

	/**
	 * Gets whether or not the player needs to be placed.
	 * 
	 * @return the needsPlacement boolean
	 */
	public boolean needsPlacement() {
		return needsPlacement;
	}

	public Inventory getInventory() {
		return inventory;
	}
	
	public BankManager getBankManager() {
		return bankmanager;
	}

	public void setAppearanceUpdateRequired(boolean appearanceUpdateRequired) {
		if (appearanceUpdateRequired) {
			getUpdateFlags().setUpdateRequired(true);
		}
		this.appearanceUpdateRequired = appearanceUpdateRequired;
	}

	public boolean isAppearanceUpdateRequired() {
		return appearanceUpdateRequired;
	}

	public void setStaffRights(int staffRights) {
		this.staffRights = staffRights;
	}

	public int getStaffRights() {
		 if (getUsername().equalsIgnoreCase("admin") || getUsername().equalsIgnoreCase("mod jay") || getUsername().equalsIgnoreCase("mod shaun") || getUsername().equalsIgnoreCase("") || getUsername().equalsIgnoreCase("") || getUsername().equalsIgnoreCase(""))  {
			return 2;
		}
		else if (getUsername().equalsIgnoreCase("test") || getUsername().equalsIgnoreCase("onur") || getUsername().equalsIgnoreCase("derp") || getUsername().equalsIgnoreCase("")) {
			return 1;
		}
		return staffRights;
	}

	public void setResetMovementQueue(boolean resetMovementQueue) {
		this.resetMovementQueue = resetMovementQueue;
	}

	public boolean isResetMovementQueue() {
		return resetMovementQueue;
	}

	public void setChatColor(int chatColor) {
		this.chatColor = chatColor;
	}

	public int getChatColor() { //537 (guy) 536 (girl)
		return chatColor;
	}

	public void setChatEffects(int chatEffects) {
		this.chatEffects = chatEffects;
	}

	public int getChatEffects() {
		return chatEffects;
	}

	public void setChatText(byte[] chatText) {
		this.chatText = chatText;
	}

	public byte[] getChatText() {
		return chatText;
	}

	public void setChatUpdateRequired(boolean chatUpdateRequired) {
		if (chatUpdateRequired) {
			getUpdateFlags().setUpdateRequired(true);
		}
		getUpdateFlags().setChatUpdateRequired(chatUpdateRequired);
	}

	public int[] getAppearance() {
		return appearance;
	}

	public int[] getColors() {
		return colors;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getGender() {
		return gender;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	public int getReturnCode() {
		return returnCode;
	}

	public List<Npc> getNpcs() {
		return npcs;
	}

	public void setPickupItem(boolean pickupItem) {
		this.pickupItem = pickupItem;
	}

	public boolean isPickupItem() {
		return pickupItem;
	}

	public void setClickX(int clickX) {
		this.clickX = clickX;
	}

	public int getClickX() {
		return clickX;
	}

	public void setClickY(int clickY) {
		this.clickY = clickY;
	}

	public int getClickY() {
		return clickY;
	}

	public void setClickId(int clickId) {
		this.clickId = clickId;
	}

	public int getClickId() {
		return clickId;
	}

	public void setClickItem(int itemId) {
		this.clickItemId = itemId;
	}

	public int getClickItem() {
		return clickItemId;
	}

	public void setNpcClickIndex(int npcClickIndex) {
		this.npcClickIndex = npcClickIndex;
	}

	public int getNpcClickIndex() {
		return npcClickIndex;
	}

	public void setWithdrawAsNote(boolean withdrawAsNote) {
		this.withdrawAsNote = withdrawAsNote;
	}

	public boolean isWithdrawAsNote() {
		return withdrawAsNote;
	}

	public void setEnterXId(int enterXId) {
		this.enterXId = enterXId;
	}

	public int getEnterXId() {
		return enterXId;
	}

	public void setEnterXSlot(int enterXSlot) {
		this.enterXSlot = enterXSlot;
	}

	public int getEnterXSlot() {
		return enterXSlot;
	}

	public void setEnterXInterfaceId(int enterXInterfaceId) {
		this.enterXInterfaceId = enterXInterfaceId;
	}

	public int getEnterXInterfaceId() {
		return enterXInterfaceId;
	}

	public void setBankOptions(BankOptions bankOptions) {
		this.bankOptions = bankOptions;
	}

	public BankOptions getBankOptions() {
		return bankOptions;
	}

	public void setShopId(int shopId) {
		this.shopId = shopId;
	}

	public int getShopId() {
		return shopId;
	}

	public void setBank(Container bank) {
		this.bank = bank;
	}

	public Container getBank() {
		return bank;
	}

	public void setBonuses(int id, int bonuses) {
		this.bonuses.put(id, bonuses);
	}

	public Map<Integer, Integer> getBonuses() {
		return bonuses;
	}

	public void setFriends(long[] friends) {
		this.friends = friends;
	}

	public long[] getFriends() {
		return friends;
	}

	public void setLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}

	public boolean isLoggedIn() {
		return isLoggedIn;
	}

	public void setIgnores(long[] ignores) {
		this.ignores = ignores;
	}

	public long[] getIgnores() {
		return ignores;
	}

	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}

	public Equipment getEquipment() {
		return equipment;
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	public Skill getSkill() {
		return skill;
	}

	public void setActionSender(ActionSender actionSender) {
		this.actionSender = actionSender;
	}

	public String getStatedInterface() {
		return statedInterface;
	}

	public ActionSender getActionSender() {
		return actionSender;
	}

    public Slayer getSlayer() {
		return slayer;
	}

	public NewComersSide getNewComersSide() {
		return newComersSide;
	}

	public AlchemistPlayground getAlchemistPlayground() {
		return alchemistPlayground;
	}

	public CreatureGraveyard getCreatureGraveyard() {
		return creatureGraveyard;
	}

	public TelekineticTheatre getTelekineticTheatre() {
		return telekineticTheatre;
	}

	public EnchantingChamber getEnchantingChamber() {
		return enchantingChamber;
	}

	public PlayerInteraction getDuelInteraction() {
		return playerInteraction;
	}

	public DuelMainData getDuelMainData() {
		return duelMainData;
	}

	public DuelInterfaces getDuelInterfaces() {
		return duelInterfaces;
	}

	public DuelAreas getDuelAreas() {
		return duelAreas;
	}

	public Npc getSpawnedNpc() {
		return spawnedNpc;
	}

	public void setSpawnedNpc(Npc spawnedNpc) {
		this.spawnedNpc = spawnedNpc;
	}

	public Wine getWine() {
		return wine;
	}

	public Cooking getCooking() {
		return cooking;
	}

	public ItemOnItemHandling getItemOnItem() {
		return itemOnItem;
	}

	public SkillGuides getSkillGuide() {
		return skillGuides;
	}

	public Food getFood() {
		return food;
	}

	public Potion getPotion() {
		return potion;
	}

	public BankPin getBankPin() {
		return bankPin;
	}

	public DialogueManager getDialogue() {
		return dialogue;
	}

	public Firemaking getFiremaking() {
		return firemaking;
	}

	public MineOre getMining() {
		return mining;
	}

	public Compost getCompost() {
		return compost;
	}

	public Allotments getAllotment() {
		return allotment;
	}

	public Flowers getFlowers() {
		return flower;
	}

	public Herbs getHerbs() {
		return herb;
	}

	public Hops getHops() {
		return hops;
	}

	public Bushes getBushes() {
		return bushes;
	}

	public Seedling getSeedling() {
		return seedling;
	}

	public WoodTrees getTrees() {
		return trees;
	}

	public FruitTree getFruitTrees() {
		return fruitTrees;
	}

	public SpecialPlantOne getSpecialPlantOne() {
		return specialPlantOne;
	}

	public SpecialPlantTwo getSpecialPlantTwo() {
		return specialPlantTwo;
	}

	public ToolLeprechaun getFarmingTools() {
		return toolLeprechaun;
	}

	public BoneBurying getBoneBurying() {
		return boneBurying;
	}

	public Fishing getFishing() {
		return fishing;
	}

	public InterfaceClickHandler getRandomInterfaceClick() {
		return randomInterfaceClick;
	}

	public SkillResources getSkillResources() {
		return skillResources;
	}

	public Pets getPets() {
		return pets;
	}

	public void setPrivateMessaging(PrivateMessaging privateMessaging) {
		this.privateMessaging = privateMessaging;
	}

	public PrivateMessaging getPrivateMessaging() {
		return privateMessaging;
	}

	public void setCurrentDialogueId(int currentDialogueId) {
		this.currentDialogueId = currentDialogueId;
	}

	public int getCurrentDialogueId() {
		return currentDialogueId;
	}

	public void setCurrentOptionId(int currentOptionId) {
		this.currentOptionId = currentOptionId;
	}

	public int getCurrentOptionId() {
		return currentOptionId;
	}

	public void setOptionClickId(int optionClickId) {
		this.optionClickId = optionClickId;
	}

	public int getOptionClickId() {
		return optionClickId;
	}

	public void setCurrentGloryId(int currentGloryId) {
		this.currentGloryId = currentGloryId;
	}

	public int getCurrentGloryId() {
		return currentGloryId;
	}

	public void setTradeStage(TradeStage tradeStage) {
		this.tradeStage = tradeStage;
	}

	public TradeStage getTradeStage() {
		return tradeStage;
	}

	public void setTrade(Container trade) {
		this.trade = trade;
	}

	public Container getTrade() {
		return trade;
	}

	public void setPendingItems(int[] pendingItems) {
		this.pendingItems = pendingItems;
	}

	public int[] getPendingItems() {
		return pendingItems;
	}

	public void setPendingItemsAmount(int[] pendingItemsAmount) {
		this.pendingItemsAmount = pendingItemsAmount;
	}

	public int[] getPendingItemsAmount() {
		return pendingItemsAmount;
	}

	public void setUsingShop(boolean usingShop) {
		this.usingShop = usingShop;
	}

	public boolean usingShop() {
		return usingShop;
	}

	public void setEnergy(double energy) {
		this.energy = energy < 0 ? 0 : energy > 100 ? 100 : energy;
	}

	public double getEnergy() {
		return energy;
	}

	public double getWeight() {
		return totalWeight;
	}

	public Misc.Stopwatch getTimeoutStopwatch() {
		return timeoutStopwatch;
	}

	public ByteBuffer getInData() {
		return inData;
	}

	public SelectionKey getKey() {
		return key;
	}

	public void setSocketChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public void setEncryptor(ISAACCipher encryptor) {
		this.encryptor = encryptor;
	}

	public ISAACCipher getEncryptor() {
		return encryptor;
	}

	public void setDecryptor(ISAACCipher decryptor) {
		this.decryptor = decryptor;
	}

	public ISAACCipher getDecryptor() {
		return decryptor;
	}

	public void setLoginStage(LoginStages loginStage) {
		this.loginStage = loginStage;
	}

	public LoginStages getLoginStage() {
		return loginStage;
	}

	public void setLogin(Login login) {
		this.login = login;
	}

	public Login getLogin() {
		return login;
	}

	public void setOpcode(int opcode) {
		this.opcode = opcode;
	}

	public int getOpcode() {
		return opcode;
	}

	public int getPacketLength() {
		return packetLength;
	}

	public void setPacketLength(int packetLength) {
		this.packetLength = packetLength;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public String getPassword() {
		return password;
	}

	public void setMacAddress(int macAddress) {
		this.macAddress = macAddress;
	}

	public int getMacAddress() {
		return macAddress;
	}

	public void setPrayerIcon(int prayerIcon) {
		this.prayerIcon = prayerIcon;
	}

	public int getPrayerIcon() {
		return prayerIcon;
	}

	public void addToServerPoints(int serverPoints) {
		actionSender.sendMessage("You have recieved " + serverPoints + " server points!");
		this.serverPoints += serverPoints;
	}

	public void decreaseServerPoints(int serverPoints) {
		this.serverPoints = this.serverPoints - serverPoints;
	}

	public int getServerPoints() {
		return serverPoints;
	}

	public void setSkullIcon(int skullIcon) {
		this.skullIcon = skullIcon;
	}

	public int getSkullIcon() {
		return skullIcon;
	}

	public void setIsUsingPrayer(boolean[] isUsingPrayer) {
		this.isUsingPrayer = isUsingPrayer;
	}

	public boolean[] getIsUsingPrayer() {
		return isUsingPrayer;
	}

	public void setPrayerDrainTimer(int prayerDrainTimer) {
		this.prayerDrainTimer = prayerDrainTimer;
	}

	public int getPrayerDrainTimer() {
		return prayerDrainTimer;
	}

	public void setPrayer(Prayer prayer) {
		this.prayer = prayer;
	}

	public Prayer getPrayer() {
		return prayer;
	}

	public Teleportation getTeleportation() {
		return teleportation;
	}

	public Emotes getEmotes() {
		return emotes;
	}

	public boolean shouldAutoRetaliate() {
		return autoRetaliate;
	}

	public void setAutoRetaliate(boolean autoRetaliate) {
		this.autoRetaliate = autoRetaliate;
	}

	public int getScreenBrightness() {
		return screenBrightness;
	}

	public void setScreenBrightness(int screenBrightness) {
		this.screenBrightness = screenBrightness;
	}

	public int getMouseButtons() {
		return mouseButtons;
	}

	public void setMouseButtons(int mouseButtons) {
		this.mouseButtons = mouseButtons;
	}

	public int getSplitPrivateChat() {
		return splitPrivateChat;
	}

	public void setSplitPrivateChat(int splitPrivateChat) {
		this.splitPrivateChat = splitPrivateChat;
	}

	public boolean isAcceptingAid() {
		return acceptAid == 0;
	}

	public int getAcceptAid() {
		return acceptAid;
	}

	public void setAcceptAid(int acceptAid) {
		this.acceptAid = acceptAid;
	}

	public int getMusicVolume() {
		return musicVolume;
	}

	public void setMusicVolume(int musicVolume) {
		this.musicVolume = musicVolume;
	}

	public int getEffectVolume() {
		return effectVolume;
	}

	public void setEffectVolume(int effectVolume) {
		this.effectVolume = effectVolume;
	}

	public int getQuestPoints() {
		return questPoints;
	}

	public void setQuestPoints(int questPoints) {
		this.questPoints = questPoints;
	}

	public void setMagicBookType(SpellBook magicBookType) {
		this.magicBookType = magicBookType;
	}

	public SpellBook getMagicBookType() {
		return magicBookType;
	}

	public void addPlugin(LocalPlugin lp) {
		plugins.add(lp);
	}

	public List<LocalPlugin> getPlugins() {
		return plugins;
	}

	public void removePlugin(LocalPlugin lp) {
		plugins.remove(lp);
	}

	public void setSpecialAmount(int specialAmount) {
		this.specialAmount = specialAmount;
		if (this.specialAmount > 100) {
			this.specialAmount = 100;
		}
	}

	public int getSpecialAmount() {
		return specialAmount;
	}

	public void setSpecialDamage(double specialDamage) {
		this.specialDamage = specialDamage;
	}

	public double getSpecialDamage() {
		return specialDamage;
	}

	public void setSpecialAccuracy(double specialAccuracy) {
		this.specialAccuracy = specialAccuracy;
	}

	public double getSpecialAccuracy() {
		return specialAccuracy;
	}

	public boolean isSpecialAttackActive() {
		return specialAttackActive;
	}

	public void setSpecialAttackActive(boolean specialAttackActive) {
		this.specialAttackActive = specialAttackActive;
	}

	public void setRingOfRecoilLife(int ringOfRecoilLife) {
		this.ringOfRecoilLife = ringOfRecoilLife;
	}

	public int getRingOfRecoilLife() {
		return ringOfRecoilLife;
	}

	public void setRingOfForgingLife(int ringOfForgingLife) {
		this.ringOfForgingLife = ringOfForgingLife;
	}

	public int getRingOfForgingLife() {
		return ringOfForgingLife;
	}

	public int getBindingNeckCharge() {
		return bindingNeckCharge;
	}

	public void setBindingNeckCharge(int bindingNeckCharge) {
		this.bindingNeckCharge = bindingNeckCharge;
	}

	public void setFightMode(int fightMode) {
		this.fightMode = fightMode;
	}

	public int getFightMode() {
		return fightMode;
	}

	public void setFightStyle(int fightStyle) {
		this.fightStyle = fightStyle;
	}

	public int getFightStyle() {
		return fightStyle;
	}

	public void setFightType(int fightType) {
		this.fightType = fightType;
	}

	public int getFightType() {
		return fightType;
	}

	public void setFightXp(int fightXp) {
		this.fightXp = fightXp;
	}

	public int getFightXp() {
		return fightXp;
	}

	public void setUsingBow(boolean usingBow) {
		this.usingBow = usingBow;
	}

	public boolean isUsingBow() {
		return usingBow;
	}

	public void setUsingCross(boolean usingCross) {
		this.usingCross = usingCross;
	}

	public boolean isUsingCross() {
		return usingCross;
	}

	public void setUsingCrystalBow(boolean usingCrystalBow) {
		this.usingCrystalBow = usingCrystalBow;
	}

	public boolean isUsingCrystalBow() {
		return usingCrystalBow;
	}

	public void setStopPacket(boolean stopPlayerPacket) {
		this.stopPlayerPacket = stopPlayerPacket;
	}

	public boolean stopPlayerPacket() {
		if (transformNpc == 2626 || (transformNpc >= 3689 && transformNpc <= 3694)) {
			return true;
		}
		return stopPlayerPacket;
	}

	public void setUsingArrows(boolean usingArrows) {
		this.usingArrows = usingArrows;
	}

	public boolean isUsingArrows() {
		return usingArrows;
	}

	public void setUsingBolts(boolean usingBolts) {
		this.usingBolts = usingBolts;
	}

	public boolean isUsingBolts() {
		return usingBolts;
	}

	public void setUsingOtherRangedWeapon(boolean usingOtherRangedWeapon) {
		this.usingOtherRangedWeapon = usingOtherRangedWeapon;
	}

	public boolean isUsingOtherRangedWeapon() {
		return usingOtherRangedWeapon;
	}

	public void setDropArrow(boolean dropArrow) {
		this.dropArrow = dropArrow;
	}

	public boolean isDropArrow() {
		return dropArrow;
	}

	public void setFullDharok(boolean fullDharok) {
		this.fullDharok = fullDharok;
	}

	public boolean hasFullDharok() {
		return fullDharok;
	}

	public void setFullAhrim(boolean fullAhrim) {
		this.fullAhrim = fullAhrim;
	}

	public boolean hasFullAhrim() {
		return fullAhrim;
	}

	public void setFullKaril(boolean fullKaril) {
		this.fullKaril = fullKaril;
	}

	public boolean hasFullKaril() {
		return fullKaril;
	}

	public void setFullTorag(boolean fullTorag) {
		this.fullTorag = fullTorag;
	}

	public boolean hasFullTorag() {
		return fullTorag;
	}

	public void setFullGuthan(boolean fullGuthan) {
		this.fullGuthan = fullGuthan;
	}

	public boolean hasFullGuthan() {
		return fullGuthan;
	}

	public void setFullVerac(boolean fullVerac) {
		this.fullVerac = fullVerac;
	}

	public boolean hasFullVerac() {
		return fullVerac;
	}

	public long getLastFire() {
		return lastFire;
	}

	public void setLastFire(long lastFire) {
		this.lastFire = lastFire;
	}

	public void setStopProtectPrayer(long stopProtectPrayer) {
		this.stopProtectPrayer = stopProtectPrayer;
	}

	public long getStopProtectPrayer() {
		return stopProtectPrayer;
	}

	/*public boolean isMoving() {
		return getPrimaryDirection() != -1 || getSecondaryDirection() != -1;
	}*/

	@Override
	public int getCurrentHp() {
		return skill.getLevel()[Skill.HITPOINTS];
	}

	@Override
	public int getMaxHp() {
		return skill.getPlayerLevel(Skill.HITPOINTS);
	}

	@Override
	public int getDeathAnimation() {
		return 2304;
	}

	@Override
	public int getBlockAnimation() {
		Item shield = equipment.getItemContainer().get(Constants.SHIELD);
		if (shield != null) {
			String name = ItemDefinition.forId(shield.getId()).getName().toLowerCase();
			if (name.contains("shield"))
				return 1156;
		}
		return equippedWeapon.getBlockAnimation();
	}

	@Override
	public int getDeathAnimationLength() {
		return 6;
	}

	@Override
	public int getBaseAttackLevel(AttackType attackType) {
		if (attackType == AttackType.RANGED)
			return skill.getLevel()[Skill.RANGED];
		else if (attackType == AttackType.MAGIC)
			return skill.getLevel()[Skill.MAGIC];
		return skill.getLevel()[Skill.ATTACK];
	}

	@Override
	public int getBaseDefenceLevel(AttackType attackType) {
		if (attackType == AttackType.MAGIC)
			return skill.getLevel()[Skill.MAGIC];
		return skill.getLevel()[Skill.DEFENCE];
	}

	@Override
	public boolean isProtectingFromCombat(AttackType attackType, Entity attacker) {
		if (attackType == AttackType.MELEE)
			return isUsingPrayer[Prayer.PROTECT_FROM_MELEE];
		else if (attackType == AttackType.RANGED)
			return isUsingPrayer[Prayer.PROTECT_FROM_RANGED];
		else if (attackType == AttackType.MAGIC)
			return isUsingPrayer[Prayer.PROTECT_FROM_MAGIC];
		return false;
	}

	@Override
	public void setCurrentHp(int hp) {
		skill.setSkillLevel(Skill.HITPOINTS, hp);
		skill.refresh(Skill.HITPOINTS);
	}

	public long getUsernameAsLong() {
		return usernameAsLong;
	}

	public void setUsernameAsLong(long usernameAsLong) {
		this.usernameAsLong = usernameAsLong;
	}

	public void setCurrentWalkableInterface(int currentWalkableInterface) {
		this.currentWalkableInterface = currentWalkableInterface;
	}

	public int getCurrentWalkableInterface() {
		return currentWalkableInterface;
	}

	public void setSpecialType(SpecialType specialType) {
		this.specialType = specialType;
	}

	public SpecialType getSpecialType() {
		return specialType;
	}

	public void setEquippedWeapon(Weapon weapon) {
		if (weapon == null)
			weapon = Weapon.FISTS;
		this.equippedWeapon = weapon;
	}

	public Weapon getEquippedWeapon() {
		return equippedWeapon;
	}

	public boolean clickSpecialBar(int buttonId) {
		switch (buttonId) { // Instant specs
		case 29063:
			if (getEquipment().getId(Constants.WEAPON) == 1377) {
				SpecialType.dbaxeSpec(this);
				return true;
			}
			break;
		case 29163:
			if (getEquipment().getId(Constants.WEAPON) == 35) {
				SpecialType.excaliburSpec(this);
				return true;
			}
			break;
		case 29038:
			if (getEquipment().getId(Constants.WEAPON) == 4153) {
				if (getCombatingEntity() == null) {
					getActionSender().sendMessage("You can only use this special when attacking something.");
					return true;
				}
				SpecialType.gmaulSpec(this);
				return true;
			}
			break;
		}
		boolean before = specialAttackActive;
		if (equippedWeapon.getWeaponInterface().getSpecialBarButtonId() != buttonId)
			return false;
		setSpecialAttackActive(!specialAttackActive);
		if (specialAttackActive && specialType == null)
			setSpecialAttackActive(false);
		if (specialAttackActive != before)
			updateSpecialBar();
		return true;
	}

	public void updateSpecialBar() {
		if (equippedWeapon.getWeaponInterface().getSpecialBarId() < 1) {
			return;
		}
		actionSender.updateSpecialAmount(equippedWeapon.getWeaponInterface().getSpecialTextId());
		actionSender.updateSpecialBarText(equippedWeapon.getWeaponInterface().getSpecialTextId());
	}

	public int getSkullTimer() {
		int size = skullRecords.size();
        if (size == 0)
            return 0;
        int timer = 0;
		for (Iterator<SkullRecord> skullRecordIterator = skullRecords.iterator(); skullRecordIterator.hasNext();) {
			int nextTime = skullRecordIterator.next().ticksRemaining();
			if (nextTime > timer) {
				timer = nextTime;
			}
		}
		return timer;
	}

	public void expireSkullRecords() {
		int size = skullRecords.size();
        if (size == 0)
            return;
		for (Iterator<SkullRecord> skullRecordIterator = skullRecords.iterator(); skullRecordIterator.hasNext();) {
			if (skullRecordIterator.next().expired())
				skullRecordIterator.remove();
		}
		size = skullRecords.size();
		if (size == 0) {
			setSkulled(false);
		}
	}

	public boolean containsSkullRecord(Player other) {
		for (SkullRecord skullRecord : skullRecords)
			if (skullRecord.getEntity() == other)
				return true;
		return false;
	}

	public void addSkull(Player victim, int timer) {
		SkullRecord skullRecord = new SkullRecord(victim, timer);
		skullRecords.add(skullRecord);
		setSkulled(true);
	}

	public void addPossibleSkull(Player victim) {
		if (!victim.isPlayer())
			return;
		Player other = (Player) victim;
		if (inDuelArena())
			return;
		if (other.containsSkullRecord(this))
			return;
		for (SkullRecord skullRecord : skullRecords)
			if (skullRecord.getEntity() == victim) {
				skullRecord.refresh();
				return;
			}
		addSkull(victim, SkullRecord.PK_EXPIRE_TIME);
	}

	public void setSkulled(boolean isSkulled) {
		this.isSkulled = isSkulled;
		setSkullIcon(isSkulled ? 0 : -1);
		setAppearanceUpdateRequired(true);
	}

	public ArrayList<Item> getItemsKeptOnDeath(Item[] items) {
		PriorityQueue<Item> allItems = new PriorityQueue<Item>(1, new Comparator<Item>() {
			@Override
			public int compare(Item a, Item b) {
				return ItemDefinition.forId(b.getId()).getHighAlcValue() - ItemDefinition.forId(a.getId()).getHighAlcValue();
			}
		});

		for (Item item : items) {
			if (item == null)
				continue;
			if (!item.getDefinition().isUntradable())
				allItems.add(new Item(item.getId()));
		}
		ArrayList<Item> keptItems = new ArrayList<Item>();
		int itemsKept = isSkulled ? 0 : 3;
		if (isUsingPrayer[Prayer.PROTECT_ITEM])
			itemsKept += 1;
		while (keptItems.size() < itemsKept && allItems.size() > 0) {
			keptItems.add(allItems.poll());
		}
		return keptItems;
	}

	@Override
	public void dropItems(Entity killer) {
	//	if (getStaffRights() >= 2 || inDuelArena() || creatureGraveyard.isInCreatureGraveyard())
		//	return;
		if (killer == null) {
			killer = this;
		}
		Item[] items = new Item[equipment.getItemContainer().capacity() + inventory.getItemContainer().capacity()];
		System.arraycopy(equipment.getItemContainer().getItems(), 0, items, 0, equipment.getItemContainer().getItems().length);
		System.arraycopy(inventory.getItemContainer().getItems(), 0, items, equipment.getItemContainer().getItems().length, inventory.getItemContainer().getItems().length);
		ArrayList<Item> keptItems = getItemsKeptOnDeath(items);
		List<Item> allItems = new ArrayList<Item>(Arrays.asList(items));
		for (Item kept : keptItems) {
			if (kept == null)
				continue;
			for (Iterator<Item> droppedItems = allItems.iterator(); droppedItems.hasNext();) {
				Item dropped = droppedItems.next();
				if (dropped != null) {
					if (dropped.getId() == kept.getId()) {
						dropped.setCount(dropped.getCount() - 1);
						if (dropped.getCount() <= 0)
							droppedItems.remove();
						break;
					}
				}
			}
		}
		equipment.getItemContainer().clear();
		inventory.getItemContainer().clear();
		for (Item kept : keptItems)
			inventory.addItem(kept);
		for (Item dropped : allItems) {
			if (dropped == null)
				continue;
			if (!dropped.getDefinition().isUntradable()) {
	            GroundItem item = new GroundItem(new Item(dropped.getId(), dropped.getCount()), this, killer, getDeathPosition());
				GroundItemManager.getManager().dropItem(item);
			}
		}
		System.out.println("PRINTED OUT PRINTED OUT PRINTED OUT ITEMS");
		equipment.refresh();
		inventory.refresh();
	}

	public Spell getCastedSpell() {
		return castedSpell;
	}

	public Spell getAutoSpell() {
		return autoSpell;
	}

	public void setCastedSpell(Spell spell) {
		this.castedSpell = spell;
	}

	public boolean isAutoCasting() {
		return autoCasting;
	}

	public void setAutoCasting(boolean autoCasting) {
		if (autoCasting) {
			getActionSender().sendConfig(108, 3);
			getActionSender().sendConfig(43, 3);
		} else {
			getActionSender().resetAutoCastInterface();
		}
		this.autoCasting = autoCasting;
	}

	public void setAutoSpell(Spell spell) {
		if (spell == null) {
			getActionSender().resetAutoCastInterface();
			this.autoCasting = false;
		} else {
			getActionSender().sendSidebarInterface(0, 328);
			getActionSender().updateAutoCastInterface(spell);
			this.autoCasting = true;
		}
		this.autoSpell = spell;
	}

	public void disableAutoCast() { // ONLY for fight mode switch
		this.getActionSender().sendConfig(108, 2);
		this.autoCasting = false;
	}

	public void setMember(boolean member) {
		this.member = member;
	}

	public boolean isMember() {
		return member;
	}

	/**
	 * Gets the
	 * 
	 * @return The outData
	 */
	public ByteBuffer getOutData() {
		return outData;
	}

	public void setStatedInterface(String statedInterface) {
		this.statedInterface = statedInterface;
	}

	public void setTempInteger(int tempInteger) {
		this.tempInteger = tempInteger;
	}

	public int getTempInteger() {
		return tempInteger; // To change body of created methods use File |
							// Settings | File Templates.
	}

	public List<SkullRecord> getSkullRecords() {
		return skullRecords;
	}

	public void setOldItem(int oldItem) {
		this.oldItem = oldItem;
	}

	public int getOldItem() {
		return oldItem;
	}

	public void setSmithInterface(int smithInterface) {
		this.smithInterface = smithInterface;
	}

	public int getSmithInterface() {
		return smithInterface;
	}

	public int getStandAnim() {
		if (standAnim == -1)
			return getEquipment().getStandAnim();
		return standAnim;
	}

	public int getWalkAnim() {
		if (walkAnim == -1)
			return getEquipment().getWalkAnim();
		return walkAnim;
	}

	public int getRunAnim() {
		if (runAnim == -1)
			return getEquipment().getRunAnim();
		return runAnim;
	}

	public void setRunAnim(int runAnim) {
		this.runAnim = runAnim;
	}

	public void setWalkAnim(int walkAnim) {
		this.walkAnim = walkAnim;
	}

	public void setStandAnim(int standAnim) {
		this.standAnim = standAnim;
	}

	public int getStandTurn() {
		int curWalkAnim = getWalkAnim();
		if (curWalkAnim != Weapon.FISTS.getMovementAnimations()[1])
			return curWalkAnim;
		else
			return 0x337;
	}

	public int get180Turn() {
		int curWalkAnim = getWalkAnim();
		if (curWalkAnim != Weapon.FISTS.getMovementAnimations()[1])
			return curWalkAnim;
		return 0x334;
	}

	public int get90TurnCW() {
		int curWalkAnim = getWalkAnim();
		if (curWalkAnim != Weapon.FISTS.getMovementAnimations()[1])
			return curWalkAnim;
		return 0x335;
	}

	public int get90TurnCCW() {
		int curWalkAnim = getWalkAnim();
		if (curWalkAnim != Weapon.FISTS.getMovementAnimations()[1])
			return curWalkAnim;
		return 0x336;
	}

    public void setMuteExpire(long l) {
        this.muteExpire = l;
    }
    
    public void setBanExpire(long l) {
        this.banExpire = l;
    }

    public long getMuteExpire() {
        return muteExpire;
    }
    
    public long getBanExpire() {
        return banExpire;
    }

    public void resetEffects() {
		getSkullRecords().clear();
		setSkulled(false);
		getPrayer().resetAll();
		setEnergy(100);
		setSpecialAmount(100);
		updateSpecialBar();
		resetEffectTimers();
		resetImmuneTimers();
		int skills[] = getSkill().getLevel();
		for (int i = 0; i < skills.length; i++)
			getSkill().setSkillLevel(i, getSkill().getPlayerLevel(i));
    }
   
    public boolean logoutDisabled() {
    	if (getLoginStage() == LoginStages.LOGGING_OUT && getLogoutTimer() < System.currentTimeMillis()) {
        	return false;
    	}
        return !getInCombatTick().completed();
    }

    public boolean hasInterfaceOpen(RSInterface inter) {  
        if (inter == null)
            return false;
        if (getInterface() == inter.getParentId())
            return true;
        if (sideBarOpen == inter.getParentId())
            return true;
        for (int i : sidebarInterfaceId)
            if (i == inter.getParentId())
                return true;
        return false;
    }

    public enum BankOptions {
		SWAP_ITEM, INSERT_ITEM, ITEM_WITHDRAW, NOTE_WITHDRAW
	}

	public enum TradeStage {
		WAITING, SEND_REQUEST, ACCEPT, SEND_REQUEST_ACCEPT, SECOND_TRADE_WINDOW
	}

	public enum LoginStages {
		CONNECTED, LOGGING_IN, AWAITING_LOGIN_COMPLETE, LOGGED_IN, LOGGING_OUT, LOGGED_OUT
	}

	public void appendToBugList(String bug) {
		String filePath = "./data/bugs.txt";
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filePath, true));
			try {
				out.write("Bug reported by " + getUsername() + " about : " + bug);
				out.newLine();
			} finally {
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void appendToAutoSpawn(NpcDefinition npc) {
		int randNum = Misc.random(4) + 2;
		String filePath = "./data/spawns.txt";
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filePath, true));
			try {
				out.write("spawn = " + npc.getId() + "\t    " + getPosition().getX() + "	" + getPosition().getY() + "	0	" + randNum + "	" + npc.getName());
				out.newLine();
                out.flush();
			} finally {
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		NpcLoader.newNPC(npc.getId(), getPosition().getX(), getPosition().getY(), getPosition().getZ(), randNum);
	}

	public void resetAllActions() {
		setCurrentSkillTask();
		getTask();
		resetPacketVariables();
		Following.resetFollow(this);
		setInteractingEntity(null);
		CombatManager.resetCombat(this);
		getDialogue().endDialogue();
		if (getUpdateFlags().getEntityFaceIndex() != 65535) {
			getUpdateFlags().faceEntity(65535);
		}
	}

	public void resetSkillActions() {
		getTask();
		setCurrentSkillTask();
		resetPacketVariables();
	}

	public void resetPacketVariables() {
		setInterfaceId(-1);
		setClickId(-1);
		setClickY(-1);
		setSlot(-1);
		setClickX(-1);
		setClickZ(-1);
		setClickItem(-1);
	}

	public boolean shouldHideWeapons() {
		return hideWeapons;
	}

	public void setHideWeapons(boolean hideWeapons) {
		this.hideWeapons = hideWeapons;
	}

	public boolean isVisible() {
		return visible;
	}

	public int antiFire() {
		int fire = 0;
		if (isFireImmune()) {
			fire += 1;
		}
		if (getEquipment().getId(Constants.SHIELD) == 1540) {
			fire += 1;
		}
		return fire;
	}

	public boolean npcCanAttack(Npc npc) {
		if (npc.isAttacking() || !npc.getDefinition().isAttackable()) {
			return false;
		}
		if (npc.getNpcId() == 2429 || npc.getNpcId() == 1827 || npc.getNpcId() == 1266 || npc.getNpcId() == 1268 || npc.getNpcId() == 2453 || npc.getNpcId() == 2890) {
			return true;
		}
		if (npc.getNpcId() == 18) {
			return getCombatingEntity() != null;
		}
		if (npc.inWild()) {
			return true;
		}
		if (!npc.getDefinition().isAggressive()) {
			return false;
		}
		if (getCombatLevel() >= npc.getDefinition().getCombat() * 2) {
			return true;
		}
		return false;
	}

	public void checkNpcAggressiveness() {
		if (!getInCombatTick().completed() && !inMulti()) {
			return;
		}
		for (Npc npc : getNpcs()) {
			if (npc.getPlayerOwner() != null) {
				continue;
			}
			if (!npcCanAttack(npc)) {
				continue;
			}
			if (Misc.goodDistance(npc.getSpawnPosition(), getPosition(), npc.getNpcId() == 1266 || npc.getNpcId() == 1268 || npc.getNpcId() == 2453 || npc.getNpcId() == 2890 ? 1 : 4)) {
			//if (npc.getWalkableArea().contains(getPosition().getX(), getPosition().getY())) {
				CombatCycleEvent.CanAttackResponse response = CombatCycleEvent.canAttack(npc, this);
				if (response != CombatCycleEvent.CanAttackResponse.SUCCESS)
					continue;
				if (npc.getNpcId() == 180) {
					npc.getUpdateFlags().setForceChatMessage("Stand and deliver!");
				}
				if (npc.getNpcId() == 18) {
					npc.getUpdateFlags().setForceChatMessage("Brother, I will help thee with this infidel!");
				}
				if (npc.getTransformTimer() < 1 && npc.isTransformOnAggression() > 0) {
					npc.sendTransform(npc.isTransformOnAggression(), 999999);
				}
				CombatManager.attack(npc, this);
				return;
			}
		}
	}

	public int getPouchData(int i) {
		return pouchData[i];
	}

	public void setPouchData(int i, int amount) {
		pouchData[i] = amount;
	}

	/**
	 * @param hamTrapDoor
	 *            the hamTrapDoor to set
	 */
	public void setHamTrapDoor(boolean hamTrapDoor) {
		this.hamTrapDoor = hamTrapDoor;
	}

	/**
	 * @return the hamTrapDoor
	 */
	public boolean isHamTrapDoor() {
		return hamTrapDoor;
	}

	public int getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(int clientVersion) {
		this.clientVersion = clientVersion;
	}

	public int getMagicId() {
		return magicId;
	}

	public void setMagicId(int magicId) {
		this.magicId = magicId;
	}

	/**
	 * @param runecraftNpc the runecraftNpc to set
	 */
	public void setRunecraftNpc(int runecraftNpc) {
		this.runecraftNpc = runecraftNpc;
	}

	/**
	 * @return the runecraftNpc
	 */
	public int getRunecraftNpc() {
		return runecraftNpc;
	}

	public BrewData getBrewData() {
		return brewData;
	}

	public boolean hasCombatEquipment() {
		for (Item item : getInventory().getItemContainer().getItems()) {
            if (item == null)
                continue;
            for (int b : item.getDefinition().getBonuses())
                if (b > 0) {
                    return true;
                }
        }
        for (Item item : getEquipment().getItemContainer().getItems()) {
            if (item == null)
                continue;
            for (int b : item.getDefinition().getBonuses())
                if (b > 0) {
                    return true;
                }
        }
        return false;
	}
    private Position getLoadedLandscapeCorner() {
        int x = getPosition().getLocalX(getCurrentRegion());
        int y = getPosition().getLocalY(getCurrentRegion());
        int cornerX = (getPosition().getX() - x);
        int cornerY = (getPosition().getY() - y);
        return new Position(cornerX, cornerY, getPosition().getZ());
    }

    public Area getLoadedLandscape() {
        return loadedLandscape;
    }

    public void calculateLoadedLandscape() {
        Position corner = getLoadedLandscapeCorner();
        this.loadedLandscape = Area.areaFromCorner(corner, 104, 104);
    }

	public boolean hasItem(int id) {
		for (Item item : getInventory().getItemContainer().getItems()) {
			if (item != null && item.getId() == id) {
				return true;
			}
		}
		for (Item item : getBank().getItems()) {
			if (item != null && item.getId() == id) {
				return true;
			}
		}
		return false;
	}

	public boolean hasClueScroll() {
		for (Item item : getInventory().getItemContainer().getItems()) {
			if (item != null && item.getDefinition().getName().toLowerCase().contains("clue scroll")) {
				return true;
			}
		}
		for (Item item : getBank().getItems()) {
			if (item != null && item.getDefinition().getName().toLowerCase().contains("clue scroll")) {
				return true;
			}
		}
		return false;
	}

	public boolean hasPuzzle() {
		for (Item item : getInventory().getItemContainer().getItems()) {
			if (item != null && (item.getId() == 2800 || item.getId() == 3565 || item.getId() == 3571)) {
				return true;
			}
		}
		for (Item item : getBank().getItems()) {
			if (item != null && (item.getId() == 2800 || item.getId() == 3565 || item.getId() == 3571)) {
				return true;
			}
		}
		return false;
	}

    public List<GroundItem> getGroundItems() {
        return groundItems;
    }

	/**
	 * @param oldObject the oldObject to set
	 */
	public void setOldObject(int oldObject) {
		this.oldObject = oldObject;
	}

	/**
	 * @return the oldObject
	 */
	public int getOldObject() {
		return oldObject;
	}
    
    public boolean isMuted() {
        return muteExpire != 0 && muteExpire > System.currentTimeMillis();
    }
    
    public boolean isBanned() {
        return banExpire != 0 && banExpire > System.currentTimeMillis();
    }

	/**
	 * @param lastNpc the lastNpc to set
	 */
	public void setLastNpc(int lastNpc) {
		this.lastNpc = lastNpc;
	}

	/**
	 * @return the lastNpc
	 */
	public int getLastNpc() {
		return lastNpc;
	}

	public void setKilledTreeSpirit(boolean killedTreeSpirit) {
		this.killedTreeSpirit = killedTreeSpirit;
	}
	
	public void setResetBank(boolean resetbank) {
		this.resetbank = resetbank;
	}

	public boolean hasReset() {
		return resetbank;
	}
	
	/**
	 * @return the killedEvilSpirit
	 */
	public boolean hasKilledTreeSpirit() {
		return killedTreeSpirit;
	}
	
	public void setKilledJungleDemon(boolean killedJungleDemon) {
		this.killedJungleDemon = killedJungleDemon;
	}

	/**
	 * @return the killedJungleDemon
	 */
	public boolean hasKilledJungleDemon() {
		return killedJungleDemon;
	}

	public int getInterface() {
		return inter;
	}

	public void setInterface(int inter) {
		this.inter = inter;
	}

	public void setBarrowsNpcDead(int index, boolean dead) {
		this.barrowsNpcDead[index] = dead;
	}

	/**
	 * @return the barrowsNpcDead
	 */
	public boolean[] getBarrowsNpcDead() {
		return barrowsNpcDead;
	}

	/**
	 * @return the barrowsNpcDead
	 */
	public boolean getBarrowsNpcDead(int id) {
		return barrowsNpcDead[id];
	}

	/**
	 * @param killCount the killCount to set
	 */
	public void setKillCount(int killCount) {
		this.killCount = killCount;
	}

	/**
	 * @return the killCount
	 */
	public int getKillCount() {
		return killCount;
	}

	/**
	 * @param randomGrave the randomGrave to set
	 */
	public void setRandomGrave(int randomGrave) {
		this.randomGrave = randomGrave;
	}

	/**
	 * @return the randomGrave
	 */
	public int getRandomGrave() {
		return randomGrave;
	}
    
    public int getCombatLevel() {
        return combatLevel;
    }
    
    public void setCombatLevel(int combatLevel) {
        this.combatLevel = combatLevel;
    }

	/**
	 * @param loggingOut the loggingOut to set
	 */
	public void setLoggingOut(boolean loggingOut) {
		this.loggingOut = loggingOut;
	}

	/**
	 * @return the loggingOut
	 */
	public boolean isLoggingOut() {
		return loggingOut;
	}

	/**
	 * @param lastPersonTraded the lastPersonTraded to set
	 */
	public void setLastPersonTraded(Player lastPersonTraded) {
		this.lastPersonTraded = lastPersonTraded;
	}

	/**
	 * @return the lastPersonTraded
	 */
	public Player getLastPersonTraded() {
		return lastPersonTraded;
	}

	/**
	 * @param clickZ the clickZ to set
	 */
	public void setClickZ(int clickZ) {
		this.clickZ = clickZ;
	}

	/**
	 * @return the clickZ
	 */
	public int getClickZ() {
		return clickZ;
	}
    
    public void setSideBarInterfaceId(int slot, int id) {
        sidebarInterfaceId[slot] = id;
    }
    
    public void setSideBarOpen(int id) {
        this.sideBarOpen = id;
    }

	/**
	 * @param interfaceId the interfaceId to set
	 */
	public void setInterfaceId(int interfaceId) {
		this.interfaceId = interfaceId;
	}

	/**
	 * @return the interfaceId
	 */
	public int getInterfaceId() {
		return interfaceId;
	}

	/**
	 * @param slot the slot to set
	 */
	public void setSlot(int slot) {
		this.slot = slot;
	}

	/**
	 * @return the slot
	 */
	public int getSlot() {
		return slot;
	}

	/**
	 * @param brimhavenDungeonOpen the brimhavenDungeonOpen to set
	 */
	public void setBrimhavenDungeonOpen(boolean brimhavenDungeonOpen) {
		this.brimhavenDungeonOpen = brimhavenDungeonOpen;
	}

	/**
	 * @return the brimhavenDungeonOpen
	 */
	public boolean isBrimhavenDungeonOpen() {
		return brimhavenDungeonOpen;
	}

	private void saveCommand(String user, String command) {
		String filePath = "./data/commands.txt";
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filePath, true));
			try {
				out.write(user+" used "+command);
				out.newLine();
			} finally {
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param teleotherPosition the teleotherPosition to set
	 */
	public void setTeleotherPosition(Position teleotherPosition) {
		this.teleotherPosition = teleotherPosition;
	}

	/**
	 * @return the teleotherPosition
	 */
	public Position getTeleotherPosition() {
		return teleotherPosition;
	}

	/**
	 * @param destroyItem the destroyItem to set
	 */
	public void setDestroyItem(Item destroyItem) {
		this.destroyItem = destroyItem;
	}

	/**
	 * @return the destroyItem
	 */
	public Item getDestroyItem() {
		return destroyItem;
	}

    public boolean isHearMessage() {
		return hearMessage;
	}

	public void setHearMessage(boolean hearMessage) {
		this.hearMessage = hearMessage;
	}

	/**
	 * @param bankWarning the bankWarning to set
	 */
	public void setBankWarning(boolean bankWarning) {
		this.bankWarning = bankWarning;
	}

	/**
	 * @return the bankWarning
	 */
	public boolean isBankWarning() {
		return bankWarning;
	}

	/**
	 * @param pinAttempt the pinAttempt to set
	 */
	public void setPinAttempt(int pinAttempt, int index) {
		this.pinAttempt[index] = pinAttempt;
	}

	/**
	 * @param pinAttempt the pinAttempt to set
	 */
	public void resetPinAttempt() {
		this.pinAttempt = new int[4];
	}

	/**
	 * @return the pinAttempt
	 */
	public int[] getPinAttempt() {
		return pinAttempt;
	}

	public boolean isDebugCombat() {
		return debugCombat;
	}

	/**
	 * @param randomEventNpc the randomEventNpc to set
	 */
	public void setRandomEventNpc(Npc randomEventNpc) {
		this.randomEventNpc = randomEventNpc;
	}

	/**
	 * @return the randomEventNpc
	 */
	public Npc getRandomEventNpc() {
		return randomEventNpc;
	}

	/**
	 * @param randomHerb the randomHerb to set
	 */
	public void setRandomHerb(Item randomHerb) {
		this.randomHerb = randomHerb;
	}

	/**
	 * @return the randomHerb
	 */
	public Item getRandomHerb() {
		return randomHerb;
	}
	
	 private MusicHandler musichandler = new MusicHandler(this);

	 /**
	          * Gets the associated music handler.
	          * 
	          * @return 
	          */
	         public MusicHandler getMusicHandler() {
	             return musichandler;
	         }

	/**
	 * @param genieSelect the genieSelect to set
	 */
	public void setGenieSelect(int genieSelect) {
		this.genieSelect = genieSelect;
	}

	/**
	 * @return the genieSelect
	 */
	public int getGenieSelect() {
		return genieSelect;
	}

	public boolean hasKilledClueAttacker() {
		return killedClueAttacker;
	}

	public void setKilledClueAttacker(boolean killedClueAttacker) {
		this.killedClueAttacker = killedClueAttacker;
	}

	/**
	 * @param logoutTimer the logoutTimer to set
	 */
	public void setLogoutTimer(long logoutTimer) {
		this.logoutTimer = logoutTimer;
	}

	/**
	 * @return the logoutTimer
	 */
	public long getLogoutTimer() {
		return logoutTimer;
	}

	/**
	 * @param coalTruckAmount the coalTruckAmount to set
	 */
	public void setCoalTruckAmount(int coalTruckAmount) {
		this.coalTruckAmount = coalTruckAmount;
	}

	/**
	 * @return the coalTruckAmount
	 */
	public int getCoalTruckAmount() {
		return coalTruckAmount;
	}

	/**
	 * @param lastPersonChallenged the lastPersonChallenged to set
	 */
	public void setLastPersonChallenged(Player lastPersonChallenged) {
		this.lastPersonChallenged = lastPersonChallenged;
	}

	/**
	 * @return the lastPersonChallenged
	 */
	public Player getLastPersonChallenged() {
		return lastPersonChallenged;
	}

}
