package de.robojumper.ddsavereader.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

/**
 * Search dialog for finding text in the current editor tab
 * Supports: Find Next, Find Previous, Replace, Replace All
 * Options: Match Case, Whole Word, Regular Expression
 */
public class SearchDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private JTextField searchField;
    private JTextField replaceField;
    private JCheckBox matchCaseCheckBox;
    private JCheckBox wholeWordCheckBox;
    private JCheckBox regexCheckBox;

    private JButton findNextButton;
    private JButton findPreviousButton;
    private JButton replaceButton;
    private JButton replaceAllButton;
    private JButton closeButton;

    private JLabel statusLabel;

    private RSyntaxTextArea currentTextArea;

    public SearchDialog(JFrame parent) {
        super(parent, "Find and Replace", false); // Non-modal
        initComponents();
        setupKeyBindings();
        pack();
        setLocationRelativeTo(parent);

        // When dialog closes, don't destroy it, just hide it
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

        // Focus search field when shown
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                searchField.requestFocusInWindow();
                searchField.selectAll();
            }
        });
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Search field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Find:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        searchField = new JTextField(30);
        mainPanel.add(searchField, gbc);

        // Replace field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Replace:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        replaceField = new JTextField(30);
        mainPanel.add(replaceField, gbc);

        // Options
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        matchCaseCheckBox = new JCheckBox("Match Case");
        wholeWordCheckBox = new JCheckBox("Whole Word");
        regexCheckBox = new JCheckBox("Regular Expression");
        optionsPanel.add(matchCaseCheckBox);
        optionsPanel.add(wholeWordCheckBox);
        optionsPanel.add(regexCheckBox);
        mainPanel.add(optionsPanel, gbc);

        // Buttons panel
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        findNextButton = new JButton("Find Next");
        findNextButton.addActionListener(e -> findNext());

        findPreviousButton = new JButton("Find Previous");
        findPreviousButton.addActionListener(e -> findPrevious());

        replaceButton = new JButton("Replace");
        replaceButton.addActionListener(e -> replace());

        replaceAllButton = new JButton("Replace All");
        replaceAllButton.addActionListener(e -> replaceAll());

        closeButton = new JButton("Close");
        closeButton.addActionListener(e -> setVisible(false));

        buttonPanel.add(findPreviousButton);
        buttonPanel.add(findNextButton);
        buttonPanel.add(replaceButton);
        buttonPanel.add(replaceAllButton);
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, gbc);

        // Status label
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        statusLabel = new JLabel(" ");
        mainPanel.add(statusLabel, gbc);

        getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

    private void setupKeyBindings() {
        // Enter key in search field = Find Next
        searchField.addActionListener(e -> findNext());

        // ESC key = Close dialog
        getRootPane().registerKeyboardAction(
            e -> setVisible(false),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // F3 = Find Next
        getRootPane().registerKeyboardAction(
            e -> findNext(),
            KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // Shift+F3 = Find Previous
        getRootPane().registerKeyboardAction(
            e -> findPrevious(),
            KeyStroke.getKeyStroke(KeyEvent.VK_F3, KeyEvent.SHIFT_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    public void setTextArea(RSyntaxTextArea textArea) {
        this.currentTextArea = textArea;
    }

    private SearchContext createSearchContext(boolean forward) {
        SearchContext context = new SearchContext();
        context.setSearchFor(searchField.getText());
        context.setMatchCase(matchCaseCheckBox.isSelected());
        context.setWholeWord(wholeWordCheckBox.isSelected());
        context.setRegularExpression(regexCheckBox.isSelected());
        context.setSearchForward(forward);
        return context;
    }

    private void findNext() {
        if (currentTextArea == null) {
            statusLabel.setText("No editor tab selected");
            return;
        }

        if (searchField.getText().isEmpty()) {
            statusLabel.setText("Please enter search text");
            return;
        }

        SearchContext context = createSearchContext(true);
        SearchResult result = SearchEngine.find(currentTextArea, context);

        if (result.wasFound()) {
            statusLabel.setText("Found at position " + result.getMarkedCount());
        } else {
            statusLabel.setText("Not found");
        }
    }

    private void findPrevious() {
        if (currentTextArea == null) {
            statusLabel.setText("No editor tab selected");
            return;
        }

        if (searchField.getText().isEmpty()) {
            statusLabel.setText("Please enter search text");
            return;
        }

        SearchContext context = createSearchContext(false);
        SearchResult result = SearchEngine.find(currentTextArea, context);

        if (result.wasFound()) {
            statusLabel.setText("Found at position " + result.getMarkedCount());
        } else {
            statusLabel.setText("Not found");
        }
    }

    private void replace() {
        if (currentTextArea == null) {
            statusLabel.setText("No editor tab selected");
            return;
        }

        if (searchField.getText().isEmpty()) {
            statusLabel.setText("Please enter search text");
            return;
        }

        SearchContext context = createSearchContext(true);
        context.setReplaceWith(replaceField.getText());
        SearchResult result = SearchEngine.replace(currentTextArea, context);

        if (result.wasFound()) {
            statusLabel.setText("Replaced 1 occurrence");
        } else {
            statusLabel.setText("Not found");
        }
    }

    private void replaceAll() {
        if (currentTextArea == null) {
            statusLabel.setText("No editor tab selected");
            return;
        }

        if (searchField.getText().isEmpty()) {
            statusLabel.setText("Please enter search text");
            return;
        }

        SearchContext context = createSearchContext(true);
        context.setReplaceWith(replaceField.getText());
        SearchResult result = SearchEngine.replaceAll(currentTextArea, context);

        int count = result.getCount();
        if (count > 0) {
            statusLabel.setText("Replaced " + count + " occurrence(s)");
        } else {
            statusLabel.setText("No occurrences found");
        }
    }

    public void showDialog() {
        setVisible(true);
        searchField.requestFocusInWindow();
        searchField.selectAll();
    }

    public void setSearchText(String text) {
        searchField.setText(text);
    }
}
