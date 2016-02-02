package com.rs2.util;

import com.rs2.model.Position;
import com.rs2.model.players.item.Item;
import com.rs2.model.players.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Boomer
 */
public class PlayerEditor extends JPanel {

    /**
     * CAN MODIFY
     */

    //The tabs on player editing
    private enum DisplayCategory {
        GENERAL, APPEARANCE, SKILLS, ITEMS, CONTACTS
    }

    //The input areas for each tab
    private void fillValueInputMap() {
        java.util.List<ValueInput> list = valueInputMap.get(DisplayCategory.GENERAL);
        list.add(new ValueInput("Username") {
            @Override
            public String getCurrentValue(Player player) {
                return player.getUsername();
            }

            @Override
            public boolean onSave(Player player, String input) {
                if (input.length() > 0 && input.length() <= 12) {
                    player.setUsername(input);
                    return true;
                }
                return false;
            }
        });
        list.add(new ValueInput("Password") {
            @Override
            public String getCurrentValue(Player player) {
                return player.getPassword();
            }

            @Override
            public boolean onSave(Player player, String input) {
                player.setPassword(input);
                return true;
            }
        });
        list.add(new ValueInput("Position") {
            @Override
            public String getCurrentValue(Player player) {
                return player.getPosition().getX() + " " + player.getPosition().getY() + " " + player.getPosition().getZ() + "";
            }

            @Override
            public boolean onSave(Player player, String input) {
                String pos[] = input.split(" ");
                if (pos.length == 3) {
                    player.setPosition(new Position(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]), Integer.parseInt(pos[2])));
                    return true;
                }
                return false;
            }
        });
        list.add(new ValueInput("Rights") {
            @Override
            public String getCurrentValue(Player player) {
                return player.getStaffRights() + "";
            }

            @Override
            public boolean onSave(Player player, String input) {
                try {
                    player.setStaffRights(Integer.parseInt(input));
                    return true;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        list = valueInputMap.get(DisplayCategory.APPEARANCE);
        list.add(new ValueInput("***") {
            @Override
            public String getCurrentValue(Player player) {
                return player.getGender() + "";
            }

            @Override
            public boolean onSave(Player player, String input) {
                try {
                    player.setGender(Integer.parseInt(input));
                    return true;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        list.add(new ValueInput("Has Designed") {

        //list.add(new ValueInput("Appearance") {
            @Override
            public String getCurrentValue(Player player) {
                return Arrays.toString(player.getAppearance());
            }

            @Override
            public boolean onSave(Player player, String input) {
                String[] stringValues = parseArray(input);
                if (stringValues.length == player.getAppearance().length) {
                    for (int i = 0; i < stringValues.length; i++)
                        player.getAppearance()[i] = Integer.parseInt(stringValues[i]);
                    return true;
                }
                return false;
            }
        });
        list.add(new ValueInput("Colors") {
            @Override
            public String getCurrentValue(Player player) {
                return Arrays.toString(player.getColors());
            }

            @Override
            public boolean onSave(Player player, String input) {
                String[] stringValues = parseArray(input);
                if (stringValues.length == player.getColors().length) {
                    for (int i = 0; i < stringValues.length; i++)
                        player.getColors()[i] = Integer.parseInt(stringValues[i]);
                    return true;
                }
                return false;
            }
        });
        list = valueInputMap.get(DisplayCategory.SKILLS);
        list.add(new ValueInput("Levels") {
            @Override
            public String getCurrentValue(Player player) {
                return Arrays.toString(player.getSkill().getLevel());
            }

            @Override
            public boolean onSave(Player player, String input) {
                String[] stringValues = parseArray(input);
                if (stringValues.length == player.getSkill().getLevel().length) {
                    int levels[] = new int[stringValues.length];
                    for (int i = 0; i < stringValues.length; i++)
                        levels[i] = Integer.parseInt(stringValues[i]);
                    player.getSkill().setLevel(levels);
                    return true;
                }
                return false;
            }
        });
        list.add(new ValueInput("Experience") {
            @Override
            public String getCurrentValue(Player player) {
                return Arrays.toString(player.getSkill().getExp());
            }

            @Override
            public boolean onSave(Player player, String input) {
                String[] stringValues = parseArray(input);
                if (stringValues.length == player.getSkill().getLevel().length) {
                    double exp[] = new double[stringValues.length];
                    for (int i = 0; i < stringValues.length; i++)
                        exp[i] = Double.parseDouble(stringValues[i]);
                    player.getSkill().setExp(exp);
                    return true;
                }
                return false;
            }
        });
        list = valueInputMap.get(DisplayCategory.ITEMS);
        list.add(new ValueInput("Inv Items") {
            @Override
            public String getCurrentValue(Player player) {
                int[] itemIds = new int[player.getInventory().getItemContainer().capacity()];
                Arrays.fill(itemIds, -1);
                Item[] items = player.getInventory().getItemContainer().toArray();
                for (int i = 0; i < items.length; i++) {
                    Item item = items[i];
                    if (item == null)
                        continue;
                    itemIds[i] = item.getId();
                }
                return Arrays.toString(itemIds);
            }

            @Override
            public boolean onSave(Player player, String input) {
                String[] stringValues = parseArray(input);
                if (stringValues.length == player.getInventory().getItemContainer().capacity()) {
                    for (int i = 0; i < stringValues.length; i++) {
                        int itemId = Integer.parseInt(stringValues[i]);
                        if (itemId != -1)
                            player.getInventory().getItemContainer().add(new Item(itemId, 0), i);
                    }
                    return true;
                }
                return false;

            }
        });
        list.add(new ValueInput("Inv Amts") {
            @Override
            public String getCurrentValue(Player player) {
                int[] itemAmts = new int[player.getInventory().getItemContainer().capacity()];
                Arrays.fill(itemAmts, 0);
                Item[] items = player.getInventory().getItemContainer().toArray();
                for (int i = 0; i < items.length; i++) {
                    Item item = items[i];
                    if (item == null)
                        continue;
                    itemAmts[i] = item.getCount();
                }
                return Arrays.toString(itemAmts);
            }

            @Override
            public boolean onSave(Player player, String input) {
                String[] stringValues = parseArray(input);
                if (stringValues.length == player.getInventory().getItemContainer().capacity()) {
                    for (int i = 0; i < stringValues.length; i++) {
                        int itemAmt = Integer.parseInt(stringValues[i]);
                        Item item = player.getInventory().getItemContainer().get(i);
                        if (item != null)
                            item.setCount(itemAmt);
                    }
                    return true;
                }
                return false;
            }
        });
        list.add(new ValueInput("Bank Items") {
            @Override
            public String getCurrentValue(Player player) {
                int[] itemIds = new int[player.getBank().capacity()];
                Arrays.fill(itemIds, -1);
                Item[] items = player.getBank().toArray();
                for (int i = 0; i < items.length; i++) {
                    Item item = items[i];
                    if (item == null)
                        continue;
                    itemIds[i] = item.getId();
                }
                return Arrays.toString(itemIds);
            }

            @Override
            public boolean onSave(Player player, String input) {
                String[] stringValues = parseArray(input);
                if (stringValues.length == player.getBank().capacity()) {
                    for (int i = 0; i < stringValues.length; i++) {
                        int itemId = Integer.parseInt(stringValues[i]);
                        if (itemId != -1)
                            player.getBank().add(new Item(itemId, 0), i);
                    }
                    return true;
                }
                return false;

            }
        });
        list.add(new ValueInput("Bank Amts") {
            @Override
            public String getCurrentValue(Player player) {
                int[] itemAmts = new int[player.getBank().capacity()];
                Arrays.fill(itemAmts, 0);
                Item[] items = player.getBank().toArray();
                for (int i = 0; i < items.length; i++) {
                    Item item = items[i];
                    if (item == null)
                        continue;
                    itemAmts[i] = item.getCount();
                }
                return Arrays.toString(itemAmts);
            }

            @Override
            public boolean onSave(Player player, String input) {
                String[] stringValues = parseArray(input);
                if (stringValues.length == player.getBank().capacity()) {
                    for (int i = 0; i < stringValues.length; i++) {
                        int itemAmt = Integer.parseInt(stringValues[i]);
                        Item item = player.getBank().get(i);
                        if (item != null)
                            item.setCount(itemAmt);
                    }
                    return true;
                }
                return false;
            }
        });

        list.add(new ValueInput("Equip Items") {
            @Override
            public String getCurrentValue(Player player) {
                int[] itemIds = new int[player.getEquipment().getItemContainer().capacity()];
                Arrays.fill(itemIds, -1);
                Item[] items = player.getEquipment().getItemContainer().toArray();
                for (int i = 0; i < items.length; i++) {
                    Item item = items[i];
                    if (item == null)
                        continue;
                    itemIds[i] = item.getId();
                }
                return Arrays.toString(itemIds);
            }

            @Override
            public boolean onSave(Player player, String input) {
                String[] stringValues = parseArray(input);
                if (stringValues.length == player.getEquipment().getItemContainer().capacity()) {
                    for (int i = 0; i < stringValues.length; i++) {
                        int itemId = Integer.parseInt(stringValues[i]);
                        if (itemId != -1)
                            player.getEquipment().getItemContainer().add(new Item(itemId, 0), i);
                    }
                    return true;
                }
                return false;

            }
        });
        list.add(new ValueInput("Equip Amts") {
            @Override
            public String getCurrentValue(Player player) {
                int[] itemAmts = new int[player.getEquipment().getItemContainer().capacity()];
                Arrays.fill(itemAmts, 0);
                Item[] items = player.getEquipment().getItemContainer().toArray();
                for (int i = 0; i < items.length; i++) {
                    Item item = items[i];
                    if (item == null)
                        continue;
                    itemAmts[i] = item.getCount();
                }
                return Arrays.toString(itemAmts);
            }

            @Override
            public boolean onSave(Player player, String input) {
                String[] stringValues = parseArray(input);
                if (stringValues.length == player.getEquipment().getItemContainer().capacity()) {
                    for (int i = 0; i < stringValues.length; i++) {
                        int itemAmt = Integer.parseInt(stringValues[i]);
                        Item item = player.getEquipment().getItemContainer().get(i);
                        if (item != null)
                            item.setCount(itemAmt);
                    }
                    return true;
                }
                return false;
            }
        });
        list = valueInputMap.get(DisplayCategory.CONTACTS);
        list.add(new ValueInput("Friends") {

            @Override
            public String getCurrentValue(Player player) {
                return Arrays.toString(player.getFriends());
            }

            @Override
            public boolean onSave(Player player, String input) {
                String[] stringValues = parseArray(input);
                long[] friends = new long[stringValues.length];
                for (int i = 0; i < stringValues.length; i++)
                    friends[i] = Long.parseLong(stringValues[i]);
                player.setFriends(friends);
                return true;
            }
        });

        list.add(new ValueInput("Ignores") {
            @Override
            public String getCurrentValue(Player player) {
                return Arrays.toString(player.getIgnores());
            }

            @Override
            public boolean onSave(Player player, String input) {
                String[] stringValues = parseArray(input);
                long[] ignores = new long[stringValues.length];
                for (int i = 0; i < stringValues.length; i++)
                    ignores[i] = Long.parseLong(stringValues[i]);
               player.setIgnores(ignores);
                return true;
            }
        });
    }


    /**
     * DON'T MODIFY
     */

    private Map<DisplayCategory, LinkedList<ValueInput>> valueInputMap = new LinkedHashMap<DisplayCategory, LinkedList<ValueInput>>();
    private final PlayerSelector playerSelector = new PlayerSelector(this);
    private JFrame frame;

    public PlayerEditor() {
        showEditor();
        setLayout(new BorderLayout());
        showPlayerSelector();
    }

    public void showPlayerSelector() {
        removeAll();
        setPreferredSize(new Dimension(400, 100));
        frame.pack();
        add(playerSelector, BorderLayout.CENTER);
    }

    private void showPlayerModifier(Player fileKey) {
        removeAll();
        setPreferredSize(new Dimension(600, 600));
        frame.pack();
        add(new PlayerModifier(this, fileKey));
    }

    private void showEditor() {
        frame = new JFrame("Player Editor by Boomer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setOpaque(true);
        frame.setContentPane(this);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private abstract class ValueInput extends JPanel {

        private JTextField inputField;
        private String valueString;

        public ValueInput(String valueString) {
            setLayout(new GridLayout(1, 2));
            add(new JLabel(valueString));
            inputField = new JTextField();
            add(inputField);
            this.valueString = valueString;
        }

        public JTextField getInputField() {
            return inputField;
        }

        public String getValueString() {
            return valueString;
        }

        public abstract String getCurrentValue(Player player);

        public abstract boolean onSave(Player player, String input);

    }

    private class PlayerModifier extends JPanel implements ActionListener {

        private Player loaded;
        private JButton backButton, saveButton;
        private PlayerEditor editor;

        public PlayerModifier(PlayerEditor editor, Player player) {
            this.editor = editor;
            for (DisplayCategory category : DisplayCategory.values())
                editor.valueInputMap.put(category, new LinkedList<ValueInput>());
            fillValueInputMap();
            try {
                PlayerSave.load(player);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.loaded = player;
            setLayout(new BorderLayout());
            add(mainDisplay(), BorderLayout.CENTER);
            JPanel buttons = new JPanel(new FlowLayout());
            backButton = new JButton("BACK");
            saveButton = new JButton("SAVE");
            backButton.addActionListener(this);
            saveButton.addActionListener(this);
            buttons.add(backButton);
            buttons.add(saveButton);
            add(buttons, BorderLayout.PAGE_END);
        }

        private JTabbedPane mainDisplay() {
            JTabbedPane pane = new JTabbedPane();
            for (DisplayCategory category : valueInputMap.keySet())
                pane.addTab(category.name(), generateTab(category));
            return pane;
        }

        private JPanel generateTab(DisplayCategory category) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            java.util.List<ValueInput> valueInputs = editor.valueInputMap.get(category);
            for (ValueInput valueInput : valueInputs) {
                valueInput.getInputField().setText(valueInput.getCurrentValue(this.loaded));
                panel.add(valueInput);
            }
            return panel;

        }

        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getSource() == saveButton) {
                for (DisplayCategory category : DisplayCategory.values()) {
                    java.util.List<ValueInput> values = editor.valueInputMap.get(category);
                    for (ValueInput input : values)
                        if (!input.onSave(loaded, input.getInputField().getText()))
                            System.out.println("Failed to save "+category.name()+" "+input.getValueString());
                }
                try {
                    PlayerSave.save(loaded);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            editor.showPlayerSelector();
        }

    }

    private class PlayerSelector extends JPanel implements ActionListener {

        private JTextField playerNameField;
        private PlayerEditor playerEditor;
        private JLabel selectorStatus;


        public PlayerSelector(PlayerEditor editor) {
            this.playerEditor = editor;
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            add(new JLabel("Input Player's Name"));
            playerNameField = new JTextField();
            playerNameField.addActionListener(this);
            add(playerNameField);
            JButton findPlayer = new JButton("FIND");
            findPlayer.addActionListener(this);
            add(findPlayer);
            selectorStatus = new JLabel("");
            add(selectorStatus);
            repaint();
        }

        public void actionPerformed(ActionEvent actionEvent) {
            String playerName = playerNameField.getText();
            if (playerName.length() == 0 || playerName.length() > 12) {
                showStatusMessage("The name must be between 1 and 12 characters");
                return;
            }
            Player fileKey = getFileKey(playerNameField.getText());
            if (fileKey == null) {
                showStatusMessage("Player could not be found");
                return;
            }

            playerEditor.showPlayerModifier(fileKey);

        }

        public void showStatusMessage(String message) {
            setPreferredSize(new Dimension(400, 500));
            remove(selectorStatus);
            playerNameField.setText("");
            repaint();
            selectorStatus = new JLabel(message);
            add(selectorStatus);
            frame.pack();
        }
    }

    public static Player getFileKey(String playerName) {
        File file = new File(PlayerSave.directory + playerName + ".dat");
        if (!file.exists()) {
            return null;
        }

        FileInputStream inFile;
        try {
            inFile = new FileInputStream(file);
            DataInputStream load = new DataInputStream(inFile);
            Player player = new Player(null);
            player.setUsername(load.readUTF());
            player.setPassword(load.readUTF());
            return player;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String[] parseArray(String src) {
        String valueString = src.substring(src.indexOf("[") + 1, src.indexOf("]"));
        return valueString.split(", ");
    }

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PlayerEditor();
            }
        });
    }

}