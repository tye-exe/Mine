package me.tye.mine.utils;

import org.bukkit.Material;

import java.io.File;

import static me.tye.mine.utils.Util.dataFolder;

/**
 Class to temporally stores the configurable vars until this feature is implemented fully.
 */
public class TempConfigsStore {
/**
 The material the pointer item should be.
 */
public static final Material pointer = Material.WOODEN_SWORD;
/**
 How quickly the pointer has to be dropped in succession for a confirmation of a selection. (In milliseconds).
 */
public static final Long dropRetryInterval = 500L;
public static final Material firstSelectedMaterial = Material.MAGENTA_GLAZED_TERRACOTTA;
public static final Material lastSelectedMaterial = Material.ORANGE_GLAZED_TERRACOTTA;
public static final Material outlineMaterial = Material.RED_STAINED_GLASS;
public static final File database = new File(dataFolder.getAbsolutePath() + File.separator + "database");
}
