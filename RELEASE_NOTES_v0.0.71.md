# Darkest Dungeon Save Editor v0.0.71 Release Notes

## 🎉 Major Feature Update

This release adds powerful new search, navigation, and quick editing capabilities to make save file editing much more efficient and user-friendly.

---

## ✨ New Features

### 1. **Advanced Search & Navigation System**

#### Basic Text Search (Ctrl+F)
- Find and replace functionality with regex support
- Match case, whole word, regular expression options
- Find next/previous navigation (F3 / Shift+F3)
- Auto-populate search field with selected text
- Non-modal dialog for simultaneous editing

#### Advanced JSON Search (Ctrl+Shift+F)
- **JSON Path Navigation**: Jump directly to specific paths
  - Example: `roster.heroes[0].name` → Navigate to first hero's name
  - Example: `estate.wallet` → Jump to wallet section

- **Value-Based Filtering**: Find items matching conditions
  - Example: Find all heroes with stress > 80
  - Example: Find heroes with resolve level >= 5
  - Supports operators: `>`, `<`, `>=`, `<=`, `==`, `!=`, `contains`
  - Wildcard support with `[*]` for arrays

#### Bookmark System (Ctrl+D / Ctrl+B)
- Save frequently accessed locations for quick navigation
- Auto-generated context snippets
- Editable bookmark descriptions
- Cross-file navigation support
- Persistent bookmark list per session

### 2. **Quick Edit Presets (Ctrl+Q)**

One-click actions for common save file modifications:

#### Hero Modifications:
- ✅ **Reset all heroes' stress to 0**
- ✅ **Remove ONLY negative quirks (safe)** - NEW SMART MODE!
  - Uses comprehensive quirk database (100+ negative quirks)
  - Preserves ALL positive quirks regardless of lock status
  - Safest option - won't accidentally remove good traits
- ⚠️ **Remove all unlocked quirks** (risky)
  - Removes both positive and negative unlocked quirks
- ⚠️⚠️ **Remove ALL quirks including locked** (dangerous)
  - Complete quirk wipe
- ✅ **Heal all heroes to max HP**

#### Estate Modifications:
- ✅ **Set gold to 999,999**
- ✅ **Set all heirlooms to 999** (bust, portrait, deed, crest)

**Key Features:**
- Multi-select checkboxes for batch operations
- Real-time log showing all changes
- Detailed change count and success messages
- Smart quirk identification using QuirkLibrary database

### 3. **Quirk Library Database**

New comprehensive quirk classification system:
- **100+ negative quirks** identified (diseases, phobias, debuffs, weaknesses)
- **80+ positive quirks** for validation
- Covers base game + Crimson Court + Color of Madness DLC
- Extensible for mod support

**Negative quirks include:**
- Diseases: syphilis, the_red_plague, rabies, scurvy, tapeworm, etc.
- Phobias: fear_of_beasts, claustrophobia, nyctophobia, etc.
- Compulsions: dipsomania, kleptomaniac, gambler, etc.
- Combat debuffs: slow_reflexes, weak_grip, torn_rotator, etc.
- Location phobias: ruins_phobe, warren_phobe, weald_phobe, etc.

---

## 🎨 UI Enhancements

### New "Edit" Menu
```
Edit
├── Find...                     (Ctrl+F)
├── Advanced Search...          (Ctrl+Shift+F)
├── ─────────────────
├── Add Bookmark                (Ctrl+D)
├── Show Bookmarks              (Ctrl+B)
├── ─────────────────
└── Quick Edit Presets...       (Ctrl+Q)
```

### Enhanced Visual Feedback
- Warning emojis (⚠️) for risky operations
- Detailed tooltips explaining each option
- Color-coded search results
- Real-time log output for quick edits

---

## 🛠️ Technical Improvements

### Code Organization
- 4 new dialog classes for modular functionality
- Clean separation of concerns
- Reusable components for future features

### New Files Added:
- `SearchDialog.java` (294 lines) - Basic text search
- `AdvancedSearchDialog.java` (504 lines) - JSON path & value filtering
- `BookmarkManager.java` (317 lines) - Bookmark system
- `QuickEditDialog.java` (419 lines) - Preset actions
- `QuirkLibrary.java` (290 lines) - Quirk database

**Total:** 1,824 lines of new functionality

### Build System
- New build scripts for easy packaging: `build-release.bat` & `build-release.sh`
- Automated fat JAR creation with all dependencies
- Distribution ZIP generation

---

## 📋 Usage Examples

### Example 1: Find Heroes with High Stress
1. Open `persist.roster.json`
2. Press `Ctrl+Shift+F`
3. Advanced Search → Value Filter tab
4. Path: `roster.heroes[*].stress.current_value`
5. Operator: `>`
6. Value: `80`
7. Click Search → See all stressed heroes

### Example 2: Safe Quirk Removal
1. Open `persist.roster.json`
2. Press `Ctrl+Q`
3. Check: ✅ "Remove ONLY negative quirks (safe)"
4. Check: ✅ "Reset all heroes' stress to 0"
5. Click Apply → All negative traits removed, positive ones kept!

### Example 3: Quick Bookmark Navigation
1. Navigate to important location (e.g., gold amount)
2. Press `Ctrl+D` → Add bookmark "Player Gold"
3. Later: Press `Ctrl+B` → Double-click bookmark → Instant navigation!

---

## 🚀 Getting Started

### Running the Application
```bash
java -jar DDSaveEditor.jar
```

### Building from Source
**Windows:**
```cmd
build-release.bat
```

**Linux/Mac:**
```bash
chmod +x build-release.sh
./build-release.sh
```

The JAR file will be in `build/libs/DDSaveEditor.jar`

---

## ⚠️ Important Notes

### Quirk Removal Safety
- **"Remove ONLY negative quirks"** is the recommended safe option
- It uses a comprehensive database to identify negative traits
- Positive quirks are preserved regardless of lock status
- Unknown quirks are kept to prevent accidental removal

### Backup Reminder
Always use the built-in backup feature before making major changes:
- **File → Make Backup** before editing
- Backups are timestamped for easy restoration

---

## 🐛 Bug Fixes
- Improved quirk removal logic to prevent accidental positive trait deletion
- Better error handling in JSON parsing
- Enhanced file state management

---

## 📈 Statistics

**Code Changes:**
- 5 new classes added
- 1,824 lines of new code
- 3 major feature additions
- 6 new keyboard shortcuts
- 100+ quirks catalogued

---

## 🙏 Acknowledgments

Special thanks to the Darkest Dungeon community for feedback on quirk classification and feature requests.

---

## 📝 Full Changelog

### Features Added:
- Advanced search system with JSON path navigation
- Value-based filtering with wildcard support
- Bookmark management system
- Quick edit presets with smart quirk removal
- Comprehensive quirk library database
- Build automation scripts

### UI Improvements:
- New Edit menu with search and bookmark options
- Enhanced tooltips with detailed explanations
- Real-time operation logs
- Warning indicators for dangerous operations

### Developer Improvements:
- Modular dialog architecture
- Reusable search components
- Extensible quirk database
- Improved build process

---

## 🔗 Links

- **GitHub Repository**: https://github.com/jjkim110523/DarkestDungeonSaveEditor
- **Forked from**: https://github.com/robojumper/DarkestDungeonSaveEditor (Original Author: robojumper)
- **License**: See LICENSE file

---

## 📦 Distribution Files

This release includes:
- `DDSaveEditor.jar` - Main executable
- `README.md` - User guide
- `LICENSE` - License information
- `docs/` - Format documentation
- `Licenses/` - Third-party licenses

---

**Enjoy the new features and happy save editing!** 🎮
