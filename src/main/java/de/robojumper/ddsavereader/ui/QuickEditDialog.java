package de.robojumper.ddsavereader.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Quick Edit Dialog for common save file modifications
 *
 * Provides preset actions like:
 * - Reset all heroes' stress to 0
 * - Remove negative quirks
 * - Heal all heroes
 * - Max out resources
 */
public class QuickEditDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private RSyntaxTextArea currentTextArea;
    private String currentFileName;
    private Gson gson;

    private JCheckBox chkResetStress;
    private JCheckBox chkRemoveNegativeQuirks;
    private JCheckBox chkRemoveAllUnlocked;
    private JCheckBox chkRemoveAllQuirks;
    private JCheckBox chkHealHeroes;
    private JCheckBox chkMaxGold;
    private JCheckBox chkMaxHeirlooms;

    private JTextArea logArea;
    private JButton applyButton;
    private JButton closeButton;

    private QuickEditCallback callback;

    public interface QuickEditCallback {
        void onTextModified(String fileName, String newText);
    }

    public QuickEditDialog(JFrame parent) {
        super(parent, "Quick Edit Presets", false);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        initComponents();
        setupKeyBindings();
        setSize(500, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Instructions
        JLabel instructionLabel = new JLabel("<html><b>Select quick edit actions to apply:</b><br>" +
                "These will modify the current save file when you click Apply.</html>");
        mainPanel.add(instructionLabel, BorderLayout.NORTH);

        // Options panel
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Hero modifications section
        JLabel heroLabel = new JLabel("<html><b>Hero Modifications:</b></html>");
        optionsPanel.add(heroLabel, gbc);

        gbc.gridy++;
        chkResetStress = new JCheckBox("Reset all heroes' stress to 0");
        chkResetStress.setToolTipText("Sets m_Stress to 0 for all heroes in roster");
        optionsPanel.add(chkResetStress, gbc);

        gbc.gridy++;
        chkRemoveNegativeQuirks = new JCheckBox("Remove ONLY negative quirks (safe)");
        chkRemoveNegativeQuirks.setToolTipText("<html>Removes only known negative quirks (diseases, phobias, etc.)<br>" +
                "Positive quirks are preserved regardless of lock status<br>" +
                "Database: " + QuirkLibrary.getNegativeQuirkCount() + " known negative quirks</html>");
        optionsPanel.add(chkRemoveNegativeQuirks, gbc);

        gbc.gridy++;
        chkRemoveAllUnlocked = new JCheckBox("Remove all unlocked quirks (⚠️ risky)");
        chkRemoveAllUnlocked.setToolTipText("<html>Removes ALL unlocked quirks (both positive and negative)<br>" +
                "Locked positive quirks will be preserved<br>" +
                "WARNING: Unlocked positive quirks will also be removed!</html>");
        optionsPanel.add(chkRemoveAllUnlocked, gbc);

        gbc.gridy++;
        chkRemoveAllQuirks = new JCheckBox("Remove ALL quirks including locked (⚠️⚠️ dangerous)");
        chkRemoveAllQuirks.setToolTipText("<html>Removes EVERY quirk regardless of type or lock status<br>" +
                "WARNING: All positive quirks will also be removed, even if locked!</html>");
        optionsPanel.add(chkRemoveAllQuirks, gbc);

        gbc.gridy++;
        chkHealHeroes = new JCheckBox("Heal all heroes to max HP");
        chkHealHeroes.setToolTipText("Sets current_hp to max_hp for all heroes");
        optionsPanel.add(chkHealHeroes, gbc);

        // Estate modifications section
        gbc.gridy++;
        gbc.insets = new Insets(15, 5, 5, 5);
        JLabel estateLabel = new JLabel("<html><b>Estate Modifications:</b></html>");
        optionsPanel.add(estateLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 5, 5, 5);
        chkMaxGold = new JCheckBox("Set gold to 999,999");
        chkMaxGold.setToolTipText("Sets gold amount to maximum (only works with persist.estate.json)");
        optionsPanel.add(chkMaxGold, gbc);

        gbc.gridy++;
        chkMaxHeirlooms = new JCheckBox("Set all heirlooms to 999");
        chkMaxHeirlooms.setToolTipText("Sets all heirloom types to 999 (only works with persist.estate.json)");
        optionsPanel.add(chkMaxHeirlooms, gbc);

        mainPanel.add(optionsPanel, BorderLayout.CENTER);

        // Log area
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Log"));
        logArea = new JTextArea(8, 40);
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(logArea);
        logPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(logPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        applyButton = new JButton("Apply Changes");
        applyButton.addActionListener(e -> applyChanges());
        closeButton = new JButton("Close");
        closeButton.addActionListener(e -> setVisible(false));
        buttonPanel.add(applyButton);
        buttonPanel.add(closeButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupKeyBindings() {
        // ESC key = Close dialog
        getRootPane().registerKeyboardAction(
            e -> setVisible(false),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    public void setCurrentContext(RSyntaxTextArea textArea, String fileName) {
        this.currentTextArea = textArea;
        this.currentFileName = fileName;
    }

    public void setCallback(QuickEditCallback callback) {
        this.callback = callback;
    }

    private void applyChanges() {
        if (currentTextArea == null || currentFileName == null) {
            JOptionPane.showMessageDialog(this,
                "No file is currently open",
                "Cannot Apply Changes",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if any option is selected
        if (!chkResetStress.isSelected() &&
            !chkRemoveNegativeQuirks.isSelected() &&
            !chkRemoveAllUnlocked.isSelected() &&
            !chkRemoveAllQuirks.isSelected() &&
            !chkHealHeroes.isSelected() &&
            !chkMaxGold.isSelected() &&
            !chkMaxHeirlooms.isSelected()) {
            JOptionPane.showMessageDialog(this,
                "Please select at least one option to apply",
                "No Options Selected",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        logArea.setText("");
        int totalChanges = 0;

        try {
            String jsonText = currentTextArea.getText();
            JsonElement root = gson.fromJson(jsonText, JsonElement.class);

            if (!root.isJsonObject()) {
                log("Error: Root element is not a JSON object");
                return;
            }

            JsonObject rootObj = root.getAsJsonObject();

            // Apply changes based on file type
            if (currentFileName.contains("roster")) {
                totalChanges += applyRosterChanges(rootObj);
            } else if (currentFileName.contains("estate")) {
                totalChanges += applyEstateChanges(rootObj);
            } else {
                log("Note: Some options only work with specific files (roster or estate)");
                // Try to apply anyway
                totalChanges += applyRosterChanges(rootObj);
                totalChanges += applyEstateChanges(rootObj);
            }

            if (totalChanges > 0) {
                // Convert back to JSON
                String newJson = gson.toJson(root);

                // Update the text area
                currentTextArea.setText(newJson);
                currentTextArea.setCaretPosition(0);

                log("\n=== SUCCESS ===");
                log("Total changes applied: " + totalChanges);
                log("Please save the file to persist changes.");

                if (callback != null) {
                    callback.onTextModified(currentFileName, newJson);
                }

                JOptionPane.showMessageDialog(this,
                    "Successfully applied " + totalChanges + " changes!\nDon't forget to save the file.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                log("No changes were made. File may not have applicable data.");
            }

        } catch (Exception e) {
            log("Error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error applying changes: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private int applyRosterChanges(JsonObject rootObj) {
        int changes = 0;

        // Look for heroes array
        JsonElement heroesElement = rootObj.get("heroes");
        if (heroesElement == null || !heroesElement.isJsonArray()) {
            log("Note: No 'heroes' array found in this file");
            return changes;
        }

        JsonArray heroes = heroesElement.getAsJsonArray();
        log("Found " + heroes.size() + " heroes");

        for (int i = 0; i < heroes.size(); i++) {
            JsonElement heroElement = heroes.get(i);
            if (!heroElement.isJsonObject()) continue;

            JsonObject hero = heroElement.getAsJsonObject();
            String heroName = getHeroName(hero);
            log("\nProcessing hero " + (i + 1) + ": " + heroName);

            // Reset stress
            if (chkResetStress.isSelected()) {
                if (hero.has("m_Stress")) {
                    hero.addProperty("m_Stress", 0);
                    changes++;
                    log("  - Reset stress to 0");
                }
            }

            // Remove quirks (three different modes)
            if (chkRemoveNegativeQuirks.isSelected() ||
                chkRemoveAllUnlocked.isSelected() ||
                chkRemoveAllQuirks.isSelected()) {

                if (hero.has("quirks")) {
                    JsonElement quirksElement = hero.get("quirks");
                    if (quirksElement.isJsonObject()) {
                        JsonObject quirks = quirksElement.getAsJsonObject();
                        int quirkCount = quirks.size();

                        if (chkRemoveAllQuirks.isSelected()) {
                            // Mode 3: Remove ALL quirks (most dangerous)
                            quirks.entrySet().clear();
                            changes += quirkCount;
                            log("  - Removed ALL " + quirkCount + " quirks (including locked positive ones)");

                        } else if (chkRemoveAllUnlocked.isSelected()) {
                            // Mode 2: Remove all unlocked quirks (risky)
                            int removed = 0;
                            java.util.Iterator<java.util.Map.Entry<String, JsonElement>> iterator =
                                quirks.entrySet().iterator();
                            while (iterator.hasNext()) {
                                java.util.Map.Entry<String, JsonElement> entry = iterator.next();
                                JsonElement quirkData = entry.getValue();
                                if (quirkData.isJsonObject()) {
                                    JsonObject quirkObj = quirkData.getAsJsonObject();
                                    boolean isLocked = false;
                                    if (quirkObj.has("is_locked")) {
                                        isLocked = quirkObj.get("is_locked").getAsBoolean();
                                    }
                                    if (!isLocked) {
                                        iterator.remove();
                                        removed++;
                                    }
                                }
                            }
                            changes += removed;
                            log("  - Removed " + removed + " unlocked quirks (kept " + (quirkCount - removed) + " locked)");

                        } else if (chkRemoveNegativeQuirks.isSelected()) {
                            // Mode 1: Remove ONLY negative quirks (safest)
                            int removed = 0;
                            int positiveKept = 0;
                            java.util.Iterator<java.util.Map.Entry<String, JsonElement>> iterator =
                                quirks.entrySet().iterator();
                            while (iterator.hasNext()) {
                                java.util.Map.Entry<String, JsonElement> entry = iterator.next();
                                String quirkName = entry.getKey();

                                // Check if this is a negative quirk using our database
                                if (QuirkLibrary.isNegativeQuirk(quirkName)) {
                                    iterator.remove();
                                    removed++;
                                } else {
                                    // Keep all non-negative quirks (positive or unknown)
                                    positiveKept++;
                                }
                            }
                            changes += removed;
                            log("  - Removed " + removed + " negative quirks, kept " + positiveKept + " positive/unknown quirks");
                        }
                    }
                }
            }

            // Heal heroes
            if (chkHealHeroes.isSelected()) {
                if (hero.has("actor")) {
                    JsonElement actorElement = hero.get("actor");
                    if (actorElement.isJsonObject()) {
                        JsonObject actor = actorElement.getAsJsonObject();
                        if (actor.has("max_hp")) {
                            float maxHp = actor.get("max_hp").getAsFloat();
                            actor.addProperty("current_hp", maxHp);
                            changes++;
                            log("  - Healed to max HP: " + maxHp);
                        }
                    }
                }
            }
        }

        return changes;
    }

    private int applyEstateChanges(JsonObject rootObj) {
        int changes = 0;

        // Max gold
        if (chkMaxGold.isSelected()) {
            if (rootObj.has("wallet")) {
                JsonElement walletElement = rootObj.get("wallet");
                if (walletElement.isJsonObject()) {
                    JsonObject wallet = walletElement.getAsJsonObject();
                    if (wallet.has("gold")) {
                        wallet.addProperty("gold", 999999);
                        changes++;
                        log("Set gold to 999,999");
                    }
                }
            } else {
                log("Note: 'wallet' not found (not an estate file?)");
            }
        }

        // Max heirlooms
        if (chkMaxHeirlooms.isSelected()) {
            if (rootObj.has("wallet")) {
                JsonElement walletElement = rootObj.get("wallet");
                if (walletElement.isJsonObject()) {
                    JsonObject wallet = walletElement.getAsJsonObject();

                    String[] heirlooms = {"bust", "portrait", "deed", "crest"};
                    for (String heirloom : heirlooms) {
                        if (wallet.has(heirloom)) {
                            wallet.addProperty(heirloom, 999);
                            changes++;
                            log("Set " + heirloom + " to 999");
                        }
                    }
                }
            } else {
                log("Note: 'wallet' not found (not an estate file?)");
            }
        }

        return changes;
    }

    private String getHeroName(JsonObject hero) {
        if (hero.has("actor")) {
            JsonElement actorElement = hero.get("actor");
            if (actorElement.isJsonObject()) {
                JsonObject actor = actorElement.getAsJsonObject();
                if (actor.has("name")) {
                    return actor.get("name").getAsString();
                }
            }
        }
        return "Unknown";
    }

    private void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public void showDialog() {
        logArea.setText("");
        setVisible(true);
    }
}
