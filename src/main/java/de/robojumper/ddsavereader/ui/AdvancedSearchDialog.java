package de.robojumper.ddsavereader.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Advanced search dialog for JSON path navigation and value-based filtering
 *
 * Features:
 * - JSON Path Search: Navigate to specific paths like "roster.heroes[0].name"
 * - Value-based Filtering: Find heroes with stress > 80, level >= 5, etc.
 * - Supports wildcards: heroes[*].stress will show all hero stress values
 */
public class AdvancedSearchDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    // JSON Path tab
    private JTextField pathField;
    private JButton navigateButton;
    private JLabel pathStatusLabel;

    // Value Filter tab
    private JTextField filterPathField;
    private JTextField filterOperatorField;
    private JTextField filterValueField;
    private JButton filterButton;
    private DefaultListModel<SearchResult> resultListModel;
    private JList<SearchResult> resultList;

    private RSyntaxTextArea currentTextArea;
    private Gson gson;

    public AdvancedSearchDialog(JFrame parent) {
        super(parent, "Advanced Search", false);
        this.gson = new Gson();
        initComponents();
        setupKeyBindings();
        pack();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                pathField.requestFocusInWindow();
            }
        });
    }

    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // JSON Path tab
        JPanel pathPanel = createPathPanel();
        tabbedPane.addTab("JSON Path", pathPanel);

        // Value Filter tab
        JPanel filterPanel = createFilterPanel();
        tabbedPane.addTab("Value Filter", filterPanel);

        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // Close button at bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> setVisible(false));
        bottomPanel.add(closeButton);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createPathPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Instructions
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        JLabel instructionLabel = new JLabel("<html><b>JSON Path Examples:</b><br>" +
                "roster.heroes[0].name - Navigate to first hero's name<br>" +
                "estate.wallet - Navigate to wallet section<br>" +
                "roster.heroes[2].quirks - Navigate to third hero's quirks</html>");
        panel.add(instructionLabel, gbc);

        // Path field
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Path:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        pathField = new JTextField(30);
        pathField.setToolTipText("Enter JSON path (e.g., roster.heroes[0].name)");
        panel.add(pathField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        navigateButton = new JButton("Navigate");
        navigateButton.addActionListener(e -> navigateToPath());
        panel.add(navigateButton, gbc);

        // Status label
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        pathStatusLabel = new JLabel(" ");
        panel.add(pathStatusLabel, gbc);

        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Instructions
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        JLabel instructionLabel = new JLabel("<html><b>Value Filter Examples:</b><br>" +
                "Path: roster.heroes[*].stress.current_value, Operator: >, Value: 80<br>" +
                "Path: roster.heroes[*].resolve_level, Operator: >=, Value: 5<br>" +
                "Path: roster.heroes[*].name, Operator: contains, Value: Dismas</html>");
        panel.add(instructionLabel, gbc);

        // Filter path
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Path:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        filterPathField = new JTextField(30);
        filterPathField.setToolTipText("Enter JSON path with [*] for wildcards");
        panel.add(filterPathField, gbc);

        // Operator
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Operator:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.3;
        filterOperatorField = new JTextField(10);
        filterOperatorField.setToolTipText("Operators: >, <, >=, <=, ==, !=, contains");
        filterOperatorField.setText(">");
        panel.add(filterOperatorField, gbc);

        // Value
        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Value:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.7;
        filterValueField = new JTextField(10);
        panel.add(filterValueField, gbc);

        // Search button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterButton = new JButton("Search");
        filterButton.addActionListener(e -> performValueFilter());
        buttonPanel.add(filterButton);
        panel.add(buttonPanel, gbc);

        // Results list
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        resultListModel = new DefaultListModel<>();
        resultList = new JList<>(resultListModel);
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                SearchResult selected = resultList.getSelectedValue();
                if (selected != null) {
                    highlightInEditor(selected.fullPath);
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(resultList);
        panel.add(scrollPane, gbc);

        return panel;
    }

    private void setupKeyBindings() {
        // ESC key = Close dialog
        getRootPane().registerKeyboardAction(
            e -> setVisible(false),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // Enter in path field = Navigate
        pathField.addActionListener(e -> navigateToPath());
    }

    public void setTextArea(RSyntaxTextArea textArea) {
        this.currentTextArea = textArea;
    }

    private void navigateToPath() {
        if (currentTextArea == null) {
            pathStatusLabel.setText("No editor tab selected");
            return;
        }

        String path = pathField.getText().trim();
        if (path.isEmpty()) {
            pathStatusLabel.setText("Please enter a JSON path");
            return;
        }

        try {
            String jsonText = currentTextArea.getText();
            JsonElement root = gson.fromJson(jsonText, JsonElement.class);

            String searchText = navigateJsonPath(root, path);
            if (searchText != null) {
                highlightInEditor(searchText);
                pathStatusLabel.setText("Navigated to: " + path);
            } else {
                pathStatusLabel.setText("Path not found: " + path);
            }
        } catch (Exception e) {
            pathStatusLabel.setText("Error: " + e.getMessage());
        }
    }

    private String navigateJsonPath(JsonElement element, String path) {
        String[] parts = path.split("\\.");
        JsonElement current = element;

        StringBuilder fullPath = new StringBuilder();

        for (String part : parts) {
            if (part.contains("[")) {
                // Array access: heroes[0]
                String fieldName = part.substring(0, part.indexOf('['));
                String indexStr = part.substring(part.indexOf('[') + 1, part.indexOf(']'));

                if (current.isJsonObject()) {
                    current = current.getAsJsonObject().get(fieldName);
                    if (current == null) return null;
                }

                if (current.isJsonArray()) {
                    try {
                        int index = Integer.parseInt(indexStr);
                        JsonArray array = current.getAsJsonArray();
                        if (index >= 0 && index < array.size()) {
                            current = array.get(index);
                            fullPath.append(fieldName).append("[").append(index).append("]");
                        } else {
                            return null;
                        }
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            } else {
                // Simple field access
                if (current.isJsonObject()) {
                    current = current.getAsJsonObject().get(part);
                    if (current == null) return null;
                    if (fullPath.length() > 0) fullPath.append(".");
                    fullPath.append(part);
                } else {
                    return null;
                }
            }
        }

        // Return the field name to search for
        return "\"" + parts[parts.length - 1].replaceAll("\\[.*\\]", "") + "\"";
    }

    private void performValueFilter() {
        if (currentTextArea == null) {
            return;
        }

        resultListModel.clear();
        String path = filterPathField.getText().trim();
        String operator = filterOperatorField.getText().trim();
        String value = filterValueField.getText().trim();

        if (path.isEmpty() || operator.isEmpty() || value.isEmpty()) {
            return;
        }

        try {
            String jsonText = currentTextArea.getText();
            JsonElement root = gson.fromJson(jsonText, JsonElement.class);

            List<SearchResult> results = filterByValue(root, path, operator, value);

            for (SearchResult result : results) {
                resultListModel.addElement(result);
            }

            if (results.isEmpty()) {
                resultListModel.addElement(new SearchResult("No results found", "", ""));
            }
        } catch (Exception e) {
            resultListModel.addElement(new SearchResult("Error: " + e.getMessage(), "", ""));
        }
    }

    private List<SearchResult> filterByValue(JsonElement root, String path, String operator, String filterValue) {
        List<SearchResult> results = new ArrayList<>();

        if (!path.contains("[*]")) {
            return results;
        }

        // Split path into before and after wildcard
        String[] pathParts = path.split("\\[\\*\\]", 2);
        String beforeWildcard = pathParts[0];
        String afterWildcard = pathParts.length > 1 ? pathParts[1] : "";
        if (afterWildcard.startsWith(".")) {
            afterWildcard = afterWildcard.substring(1);
        }

        // Navigate to the array
        JsonElement arrayElement = navigateToElement(root, beforeWildcard);
        if (arrayElement == null || !arrayElement.isJsonArray()) {
            return results;
        }

        JsonArray array = arrayElement.getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
            JsonElement item = array.get(i);
            JsonElement targetValue = afterWildcard.isEmpty() ? item : navigateToElement(item, afterWildcard);

            if (targetValue != null && matchesFilter(targetValue, operator, filterValue)) {
                String displayPath = beforeWildcard + "[" + i + "]" +
                                   (afterWildcard.isEmpty() ? "" : "." + afterWildcard);
                String displayValue = targetValue.toString();
                results.add(new SearchResult(displayPath + " = " + displayValue, displayPath, displayValue));
            }
        }

        return results;
    }

    private JsonElement navigateToElement(JsonElement element, String path) {
        if (path.isEmpty()) return element;

        String[] parts = path.split("\\.");
        JsonElement current = element;

        for (String part : parts) {
            if (part.contains("[")) {
                String fieldName = part.substring(0, part.indexOf('['));
                String indexStr = part.substring(part.indexOf('[') + 1, part.indexOf(']'));

                if (current.isJsonObject()) {
                    current = current.getAsJsonObject().get(fieldName);
                    if (current == null) return null;
                }

                if (current.isJsonArray()) {
                    try {
                        int index = Integer.parseInt(indexStr);
                        current = current.getAsJsonArray().get(index);
                    } catch (Exception e) {
                        return null;
                    }
                }
            } else {
                if (current.isJsonObject()) {
                    current = current.getAsJsonObject().get(part);
                    if (current == null) return null;
                } else {
                    return null;
                }
            }
        }

        return current;
    }

    private boolean matchesFilter(JsonElement element, String operator, String filterValue) {
        try {
            if (element.isJsonPrimitive()) {
                JsonPrimitive primitive = element.getAsJsonPrimitive();

                if (operator.equals("contains") && primitive.isString()) {
                    return primitive.getAsString().toLowerCase().contains(filterValue.toLowerCase());
                }

                if (primitive.isNumber()) {
                    double numValue = primitive.getAsDouble();
                    double filterNum = Double.parseDouble(filterValue);

                    switch (operator) {
                        case ">": return numValue > filterNum;
                        case "<": return numValue < filterNum;
                        case ">=": return numValue >= filterNum;
                        case "<=": return numValue <= filterNum;
                        case "==": return numValue == filterNum;
                        case "!=": return numValue != filterNum;
                    }
                }

                if (primitive.isString()) {
                    String strValue = primitive.getAsString();
                    switch (operator) {
                        case "==": return strValue.equals(filterValue);
                        case "!=": return !strValue.equals(filterValue);
                        case "contains": return strValue.toLowerCase().contains(filterValue.toLowerCase());
                    }
                }
            }
        } catch (Exception e) {
            // Ignore parse errors
        }
        return false;
    }

    private void highlightInEditor(String searchText) {
        if (currentTextArea == null || searchText == null || searchText.isEmpty()) {
            return;
        }

        String text = currentTextArea.getText();
        int index = text.indexOf(searchText);

        if (index >= 0) {
            currentTextArea.setCaretPosition(index);
            currentTextArea.select(index, index + searchText.length());
            currentTextArea.requestFocusInWindow();
        }
    }

    public void showDialog() {
        setVisible(true);
        pathField.requestFocusInWindow();
    }

    private static class SearchResult {
        String display;
        String fullPath;
        String value;

        SearchResult(String display, String fullPath, String value) {
            this.display = display;
            this.fullPath = fullPath;
            this.value = value;
        }

        @Override
        public String toString() {
            return display;
        }
    }
}
