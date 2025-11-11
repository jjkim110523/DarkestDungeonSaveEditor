package de.robojumper.ddsavereader.ui;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for identifying negative quirks in Darkest Dungeon
 *
 * Based on the game's quirk_library.json and community knowledge.
 * Negative quirks are those that harm the hero's performance.
 */
public class QuirkLibrary {

    /**
     * Known negative quirks in Darkest Dungeon (base game + DLC)
     * This list includes diseases, phobias, compulsions, and other negative traits.
     */
    private static final Set<String> NEGATIVE_QUIRKS = new HashSet<>(Arrays.asList(
        // Diseases
        "syphilis",
        "the_red_plague",
        "rabies",
        "black_plague",
        "scurvy",
        "tapeworm",
        "fits",
        "tetanus",
        "wasting_sickness",

        // Phobias (Fear of...)
        "fear_of_beasts",
        "fear_of_eldritch",
        "fear_of_unholy",
        "fear_of_mankind",
        "claustrophobia",
        "nyctophobia",
        "hemophobia",

        // Hatred (anti-synergy with other classes)
        "hatred_of_beasts",
        "hatred_of_eldritch",
        "hatred_of_unholy",
        "hatred_of_mankind",

        // Compulsions and addictions
        "known_cheat",
        "kleptomaniac",
        "dipsomania",
        "compulsive",
        "gambler",
        "nervous",
        "hopeless",
        "irrational",
        "paranoia",
        "masochism",
        "selfish",
        "guilty_conscience",

        // Combat debuffs
        "slow_reflexes",
        "bad_gambler",
        "slowdraw",
        "dud_hitter",
        "lazy_eye",
        "squeamish",
        "weak_grip_on_life",
        "fragile",
        "torn_rotator",
        "sprained_ankle",
        "leper_lash",

        // Mental debuffs
        "fearful",
        "soft",
        "weak",
        "meek",
        "cowardly",
        "unquiet_mind",

        // Negative combat behaviors
        "death_obsessed",
        "fear_of_death",
        "morbid",
        "phengophobia", // fear of daylight

        // Location-specific debuffs
        "ruins_phobe",
        "ruins_scratcher",
        "warren_phobe",
        "warren_scratcher",
        "weald_phobe",
        "weald_scratcher",
        "cove_phobe",
        "cove_scratcher",
        "courtyard_phobe", // Crimson Court DLC
        "courtyard_scratcher", // Crimson Court DLC
        "farmstead_phobe", // Color of Madness DLC
        "farmstead_scratcher", // Color of Madness DLC

        // Other negative traits
        "resolution",
        "histrionic",
        "off_guard",
        "slowdraw",
        "stress_eater",
        "night_blindness",
        "nocturnal",
        "light_sensitive",
        "dark_temptation",
        "demonomania",
        "bloodthirsty",
        "on_guard",
        "bloodthisty", // typo in game data

        // Negative religious/moral traits
        "god_fearing",
        "faithless",
        "deviant_tastes",
        "love_interest",
        "calm",

        // Physical weaknesses
        "thin_blooded",
        "anemic",
        "slow",
        "clumsy",

        // Social debuffs
        "quiet",
        "introvert",
        "calm",
        "peaceful",

        // Activity-specific debuffs
        "no_healing",
        "no_stress_healing",
        "ablutomania",
        "egomania",
        "plutomania",
        "experience_draining",

        // Misc negative
        "curious",
        "on_guard",
        "dark_temptation",
        "sickly"
    ));

    /**
     * Known positive quirks - for reference and validation
     * These should NOT be removed when removing negative quirks
     */
    private static final Set<String> POSITIVE_QUIRKS = new HashSet<>(Arrays.asList(
        // Slayers
        "beast_slayer",
        "unholy_slayer",
        "eldritch_slayer",
        "man_slayer",

        // Physical bonuses
        "quick_reflexes",
        "hard_noggin",
        "hard_skinned",
        "tough",
        "eagle_eye",
        "unerring",
        "precise_striker",
        "natural_swing",
        "natural_eye",
        "natural",
        "steady",
        "thick_blooded",

        // Mental bonuses
        "courageous",
        "focused",
        "resilient",
        "warrior_of_light",
        "enlightened",
        "gifted",

        // Combat bonuses
        "quick_draw",
        "quickdraw",
        "powerful",
        "deadly",
        "skilled_gambler",
        "lurker",
        "on_guard",

        // Location bonuses
        "ruins_adventurer",
        "ruins_tactician",
        "warren_adventurer",
        "warren_tactician",
        "weald_adventurer",
        "weald_tactician",
        "cove_adventurer",
        "cove_tactician",
        "courtyard_adventurer", // Crimson Court DLC
        "courtyard_tactician", // Crimson Court DLC
        "farmstead_adventurer", // Color of Madness DLC
        "farmstead_tactician", // Color of Madness DLC

        // Recovery bonuses
        "fast_healer",
        "photomania",
        "clotter",
        "second_wind",
        "hard_noggin",

        // Utility bonuses
        "natural_eye",
        "natural_swing",
        "mankind_hater", // actually can be positive in some contexts
        "eldritch_hater",
        "beast_hater",
        "unholy_hater",

        // Stress management
        "steady",
        "courageous",
        "stalwart",

        // Activity bonuses
        "balanced",
        "clutch_hitter",
        "hard_noggin",
        "improved_balance",
        "irrepressible",
        "last_gasp",
        "natural",
        "robust",
        "unyielding",
        "vigorous",
        "warrens_explorer",
        "weald_explorer",
        "ruins_explorer",
        "cove_explorer",

        // Misc positive
        "corvids_grace",
        "corvids_eye",
        "corvids_resilience",
        "corvids_appetite",
        "moonlight_dancer", // Color of Madness DLC
        "witness" // Color of Madness DLC
    ));

    /**
     * Checks if a quirk is known to be negative
     *
     * @param quirkName The name of the quirk (lowercase, as stored in save file)
     * @return true if the quirk is negative, false otherwise
     */
    public static boolean isNegativeQuirk(String quirkName) {
        if (quirkName == null || quirkName.isEmpty()) {
            return false;
        }
        return NEGATIVE_QUIRKS.contains(quirkName.toLowerCase());
    }

    /**
     * Checks if a quirk is known to be positive
     *
     * @param quirkName The name of the quirk (lowercase, as stored in save file)
     * @return true if the quirk is positive, false otherwise
     */
    public static boolean isPositiveQuirk(String quirkName) {
        if (quirkName == null || quirkName.isEmpty()) {
            return false;
        }
        return POSITIVE_QUIRKS.contains(quirkName.toLowerCase());
    }

    /**
     * Gets the total number of known negative quirks
     */
    public static int getNegativeQuirkCount() {
        return NEGATIVE_QUIRKS.size();
    }

    /**
     * Gets the total number of known positive quirks
     */
    public static int getPositiveQuirkCount() {
        return POSITIVE_QUIRKS.size();
    }
}
