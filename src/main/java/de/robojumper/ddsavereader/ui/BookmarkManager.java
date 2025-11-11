package de.robojumper.ddsavereader.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

/**
 * Bookmark manager for saving and navigating to specific locations in save files
 *
 * Features:
 * - Add bookmarks at current cursor position
 * - Navigate to bookmarks with double-click
 * - Edit bookmark descriptions
 * - Delete bookmarks
 * - Persistent storage (in memory for current session)
 */
public class BookmarkManager extends JDialog {
    private static final long serialVersionUID = 1L;

    private DefaultListModel<Bookmark> bookmarkListModel;
    private JList<Bookmark> bookmarkList;
    private JButton addButton;
    private JButton deleteButton;
    private JButton goToButton;
    private JButton editButton;

    private RSyntaxTextArea currentTextArea;
    private String currentFileName;
    private BookmarkNavigationCallback navigationCallback;

    public interface BookmarkNavigationCallback {
        void navigateToBookmark(String fileName, int lineNumber, int offset);
    }

    public BookmarkManager(JFrame parent) {
        super(parent, "Bookmarks", false);
        initComponents();
        setupKeyBindings();
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));

        // Instructions panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Double-click a bookmark to navigate to it"));
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Bookmark list
        bookmarkListModel = new DefaultListModel<>();
        bookmarkList = new JList<>(bookmarkListModel);
        bookmarkList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookmarkList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    goToSelectedBookmark();
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(bookmarkList);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        addButton = new JButton("Add Bookmark");
        addButton.setToolTipText("Add bookmark at current cursor position");
        addButton.addActionListener(e -> addBookmark());

        editButton = new JButton("Edit");
        editButton.addActionListener(e -> editSelectedBookmark());

        goToButton = new JButton("Go To");
        goToButton.addActionListener(e -> goToSelectedBookmark());

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedBookmark());

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> setVisible(false));

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(goToButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
    }

    private void setupKeyBindings() {
        // ESC key = Close dialog
        getRootPane().registerKeyboardAction(
            e -> setVisible(false),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // Delete key = Delete bookmark
        bookmarkList.registerKeyboardAction(
            e -> deleteSelectedBookmark(),
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
            JComponent.WHEN_FOCUSED
        );

        // Enter key = Go to bookmark
        bookmarkList.registerKeyboardAction(
            e -> goToSelectedBookmark(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            JComponent.WHEN_FOCUSED
        );
    }

    public void setCurrentContext(RSyntaxTextArea textArea, String fileName) {
        this.currentTextArea = textArea;
        this.currentFileName = fileName;
    }

    public void setNavigationCallback(BookmarkNavigationCallback callback) {
        this.navigationCallback = callback;
    }

    private void addBookmark() {
        addBookmarkInternal(this);
    }

    /**
     * Public method to add a bookmark from external callers (e.g., MainWindow)
     * Returns true if bookmark was added, false if cancelled
     */
    public boolean addBookmarkFromCurrent() {
        return addBookmarkInternal(null);
    }

    private boolean addBookmarkInternal(java.awt.Component parentComponent) {
        if (currentTextArea == null || currentFileName == null) {
            if (parentComponent != null) {
                JOptionPane.showMessageDialog(parentComponent,
                    "No file is currently open",
                    "Cannot Add Bookmark",
                    JOptionPane.WARNING_MESSAGE);
            }
            return false;
        }

        int caretPosition = currentTextArea.getCaretPosition();
        int lineNumber = currentTextArea.getLineOfOffset(caretPosition);

        // Get a snippet of text around the cursor for context
        String snippet = getTextSnippet(lineNumber);

        // Ask user for a description
        String description = (String) JOptionPane.showInputDialog(
            parentComponent != null ? parentComponent : this,
            "Enter bookmark description:",
            "Add Bookmark",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            snippet);

        if (description != null && !description.trim().isEmpty()) {
            Bookmark bookmark = new Bookmark(
                currentFileName,
                lineNumber + 1, // Display line numbers starting from 1
                caretPosition,
                description.trim()
            );
            bookmarkListModel.addElement(bookmark);
            return true;
        }
        return false;
    }

    private String getTextSnippet(int lineNumber) {
        try {
            int lineStart = currentTextArea.getLineStartOffset(lineNumber);
            int lineEnd = currentTextArea.getLineEndOffset(lineNumber);
            String lineText = currentTextArea.getText(lineStart, lineEnd - lineStart);

            // Trim and limit length
            lineText = lineText.trim();
            if (lineText.length() > 50) {
                lineText = lineText.substring(0, 47) + "...";
            }
            return lineText;
        } catch (Exception e) {
            return "";
        }
    }

    private void editSelectedBookmark() {
        Bookmark selected = bookmarkList.getSelectedValue();
        if (selected == null) {
            return;
        }

        String newDescription = (String) JOptionPane.showInputDialog(
            this,
            "Edit bookmark description:",
            "Edit Bookmark",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            selected.description);

        if (newDescription != null && !newDescription.trim().isEmpty()) {
            selected.description = newDescription.trim();
            bookmarkList.repaint();
        }
    }

    private void deleteSelectedBookmark() {
        int selectedIndex = bookmarkList.getSelectedIndex();
        if (selectedIndex >= 0) {
            bookmarkListModel.remove(selectedIndex);
        }
    }

    private void goToSelectedBookmark() {
        Bookmark selected = bookmarkList.getSelectedValue();
        if (selected != null && navigationCallback != null) {
            navigationCallback.navigateToBookmark(
                selected.fileName,
                selected.lineNumber,
                selected.caretPosition
            );
        }
    }

    public void showDialog() {
        setVisible(true);
    }

    public int getBookmarkCount() {
        return bookmarkListModel.getSize();
    }

    public List<Bookmark> getBookmarks() {
        List<Bookmark> bookmarks = new ArrayList<>();
        for (int i = 0; i < bookmarkListModel.getSize(); i++) {
            bookmarks.add(bookmarkListModel.getElementAt(i));
        }
        return bookmarks;
    }

    public void clearBookmarks() {
        bookmarkListModel.clear();
    }

    /**
     * Represents a bookmark in a save file
     */
    public static class Bookmark {
        String fileName;
        int lineNumber; // 1-based for display
        int caretPosition; // Actual character offset
        String description;

        public Bookmark(String fileName, int lineNumber, int caretPosition, String description) {
            this.fileName = fileName;
            this.lineNumber = lineNumber;
            this.caretPosition = caretPosition;
            this.description = description;
        }

        @Override
        public String toString() {
            return String.format("[%s:%d] %s", fileName, lineNumber, description);
        }

        public String getFileName() {
            return fileName;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public int getCaretPosition() {
            return caretPosition;
        }

        public String getDescription() {
            return description;
        }
    }
}
