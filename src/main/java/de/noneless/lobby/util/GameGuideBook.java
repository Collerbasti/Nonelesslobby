package de.noneless.lobby.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

/**
 * Erstellt das geschriebene Regelbuch für das NonelessGame Kartenspiel.
 * Wird im Warps-Menü angeboten, sofern das Plugin NonelessGame geladen ist.
 */
public final class GameGuideBook {

    private static final String AUTHOR = "Noneless";
    private static final String TITLE  = "Kartenspiel-Anleitung";

    private GameGuideBook() {}

    /** Erstellt den fertigen ItemStack des Regelbuches. */
    public static ItemStack create() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        if (meta == null) return book;

        meta.author(Component.text(AUTHOR));
        meta.title(Component.text(TITLE));
        meta.pages(buildPages());

        // Lore im Inventar
        meta.displayName(
            Component.text("✦ Kartenspiel-Anleitung", NamedTextColor.GOLD)
                     .decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(List.of(
            Component.text("Alle Grundlagen des", NamedTextColor.GRAY)
                     .decoration(TextDecoration.ITALIC, false),
            Component.text("NonelessGame Kartenspiels", NamedTextColor.GRAY)
                     .decoration(TextDecoration.ITALIC, false)
        ));

        book.setItemMeta(meta);
        return book;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Seiten
    // ─────────────────────────────────────────────────────────────────────────

    private static List<Component> buildPages() {
        return List.of(
            page1_title(),
            page2_goal(),
            page3_mobCards(),
            page4_wizCards(),
            page5_actionPoints(),
            page6_turnFlow(),
            page7_deck(),
            page8_boosters(),
            page9_duel(),
            page10_tips()
        );
    }

    /** Titelseite */
    private static Component page1_title() {
        return Component.text()
            .append(Component.text("\n  ★ NONELESS ★\n", NamedTextColor.GOLD, TextDecoration.BOLD))
            .append(Component.text(" Kartenspiel\n", NamedTextColor.DARK_RED, TextDecoration.BOLD))
            .append(Component.text(" Spielanleitung\n\n", NamedTextColor.DARK_RED))
            .append(Component.text("─────────────\n\n", NamedTextColor.GRAY))
            .append(Component.text("Willkommen!\n", NamedTextColor.BLACK, TextDecoration.BOLD))
            .append(Component.text("Diese Anleitung\nerklärt dir alle\nGrundlagen des\nSpiels.", NamedTextColor.BLACK))
            .build();
    }

    /** Das Spielziel */
    private static Component page2_goal() {
        return Component.text()
            .append(Component.text("─── Das Ziel ───\n\n", NamedTextColor.DARK_RED, TextDecoration.BOLD))
            .append(Component.text("Reduziere die\n", NamedTextColor.BLACK))
            .append(Component.text("30 Lebenspunkte\n", NamedTextColor.DARK_RED, TextDecoration.BOLD))
            .append(Component.text("deines Gegners\nauf 0!\n\n", NamedTextColor.BLACK))
            .append(Component.text("Jeder startet mit:\n", NamedTextColor.BLACK, TextDecoration.BOLD))
            .append(Component.text("❤ 30 LP\n", NamedTextColor.RED))
            .append(Component.text("⚡ 3 AP / Runde\n", NamedTextColor.YELLOW))
            .append(Component.text("✦ 5 Handkarten", NamedTextColor.BLUE))
            .build();
    }

    /** Mob-Karten */
    private static Component page3_mobCards() {
        return Component.text()
            .append(Component.text("── Kartentypen ──\n\n", NamedTextColor.DARK_RED, TextDecoration.BOLD))
            .append(Component.text("◆ MOB-KARTEN\n", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
            .append(Component.text("Beschwöre Monster\nauf dein Spielfeld.\n\n", NamedTextColor.BLACK))
            .append(Component.text("Jeder Mob hat:\n", NamedTextColor.BLACK, TextDecoration.BOLD))
            .append(Component.text("⚔ ATK – Angriff\n", NamedTextColor.RED))
            .append(Component.text("❤ HP  – Leben\n\n", NamedTextColor.DARK_GREEN))
            .append(Component.text("Du hast ", NamedTextColor.BLACK))
            .append(Component.text("5 Slots\n", NamedTextColor.GOLD, TextDecoration.BOLD))
            .append(Component.text("für Mobs auf\ndeinem Feld.", NamedTextColor.BLACK))
            .build();
    }

    /** Zauber-Karten */
    private static Component page4_wizCards() {
        return Component.text()
            .append(Component.text("── Kartentypen ──\n\n", NamedTextColor.DARK_RED, TextDecoration.BOLD))
            .append(Component.text("◆ ZAUBER-KARTEN\n", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD))
            .append(Component.text("Einmalige Effekte\noder Ausrüstung.\n\n", NamedTextColor.BLACK))
            .append(Component.text("Arten:\n", NamedTextColor.BLACK, TextDecoration.BOLD))
            .append(Component.text("⚔ Schwerter\n", NamedTextColor.GRAY))
            .append(Component.text("  (ATK erhöhen)\n", NamedTextColor.DARK_GRAY))
            .append(Component.text("☆ Tränke\n", NamedTextColor.LIGHT_PURPLE))
            .append(Component.text("  (Heilen/Schaden/\n   Gift/Schutz)\n", NamedTextColor.DARK_GRAY))
            .append(Component.text("▦ Wände\n", NamedTextColor.AQUA))
            .append(Component.text("  (Defensiv-Schutz)", NamedTextColor.DARK_GRAY))
            .build();
    }

    /** Aktionspunkte */
    private static Component page5_actionPoints() {
        return Component.text()
            .append(Component.text("─ Aktionspunkte ─\n\n", NamedTextColor.DARK_RED, TextDecoration.BOLD))
            .append(Component.text("Pro Runde erhältst\ndu ", NamedTextColor.BLACK))
            .append(Component.text("3 AP", NamedTextColor.YELLOW, TextDecoration.BOLD))
            .append(Component.text(".\nNicht genutzte AP\nverfallen.\n\n", NamedTextColor.BLACK))
            .append(Component.text("Kosten:\n", NamedTextColor.BLACK, TextDecoration.BOLD))
            .append(Component.text("• Karte spielen ", NamedTextColor.BLACK))
            .append(Component.text("1 AP\n", NamedTextColor.YELLOW, TextDecoration.BOLD))
            .append(Component.text("• Mob angreifen ", NamedTextColor.BLACK))
            .append(Component.text("1 AP\n", NamedTextColor.YELLOW, TextDecoration.BOLD))
            .append(Component.text("• Direktangriff ", NamedTextColor.BLACK))
            .append(Component.text("1 AP", NamedTextColor.YELLOW, TextDecoration.BOLD))
            .build();
    }

    /** Rundenablauf */
    private static Component page6_turnFlow() {
        return Component.text()
            .append(Component.text("─ Rundenablauf ─\n\n", NamedTextColor.DARK_RED, TextDecoration.BOLD))
            .append(Component.text("1. ", NamedTextColor.GOLD, TextDecoration.BOLD))
            .append(Component.text("Karte ziehen\n\n", NamedTextColor.BLACK))
            .append(Component.text("2. ", NamedTextColor.GOLD, TextDecoration.BOLD))
            .append(Component.text("Karten spielen\n   & angreifen\n\n", NamedTextColor.BLACK))
            .append(Component.text("3. ", NamedTextColor.GOLD, TextDecoration.BOLD))
            .append(Component.text("Zug beenden\n\n", NamedTextColor.BLACK))
            .append(Component.text("─────────────\n", NamedTextColor.GRAY))
            .append(Component.text("Das Duell-Menü\nöffnest du mit\ndem ", NamedTextColor.BLACK))
            .append(Component.text("Netherstern\n", NamedTextColor.AQUA, TextDecoration.BOLD))
            .append(Component.text("in Slot 9.", NamedTextColor.BLACK))
            .build();
    }

    /** Deck bauen */
    private static Component page7_deck() {
        return Component.text()
            .append(Component.text("──── Dein Deck ────\n\n", NamedTextColor.DARK_RED, TextDecoration.BOLD))
            .append(Component.text("Baue dein Deck\nmit\n", NamedTextColor.BLACK))
            .append(Component.text("/editDeck\n\n", NamedTextColor.GOLD, TextDecoration.BOLD))
            .append(Component.text("Dein Deck besteht\naus Karten die\ndu gesammelt hast.\n\n", NamedTextColor.BLACK))
            .append(Component.text("Karten bekommst\ndu durch:\n", NamedTextColor.BLACK, TextDecoration.BOLD))
            .append(Component.text("• Boosterpacks\n", NamedTextColor.DARK_GREEN))
            .append(Component.text("• Events &\n  Belohnungen", NamedTextColor.DARK_GREEN))
            .build();
    }

    /** Boosterpacks */
    private static Component page8_boosters() {
        return Component.text()
            .append(Component.text("─ Boosterpacks ─\n\n", NamedTextColor.DARK_RED, TextDecoration.BOLD))
            .append(Component.text("/booster\n\n", NamedTextColor.GOLD, TextDecoration.BOLD))
            .append(Component.text("Jedes Pack enthält\n", NamedTextColor.BLACK))
            .append(Component.text("5 zufällige\nKarten", NamedTextColor.BLACK, TextDecoration.BOLD))
            .append(Component.text(".\n\nVerfügbare Packs:\n", NamedTextColor.BLACK))
            .append(Component.text("• Animal Pack\n", NamedTextColor.GREEN))
            .append(Component.text("• Cave Pack\n", NamedTextColor.DARK_GRAY))
            .append(Component.text("• Nether Pack\n", NamedTextColor.RED))
            .append(Component.text("• End Pack\n", NamedTextColor.DARK_PURPLE))
            .append(Component.text("• Wildlife Pack\n", NamedTextColor.GREEN))
            .append(Component.text("• … und mehr!", NamedTextColor.GRAY))
            .build();
    }

    /** Ein Duell starten */
    private static Component page9_duel() {
        return Component.text()
            .append(Component.text("─ Duellieren ─\n\n", NamedTextColor.DARK_RED, TextDecoration.BOLD))
            .append(Component.text("Gegen Spieler:\n", NamedTextColor.BLACK, TextDecoration.BOLD))
            .append(Component.text("/Duell <Spieler>\n\n", NamedTextColor.GOLD))
            .append(Component.text("Gegen Bot:\n", NamedTextColor.BLACK, TextDecoration.BOLD))
            .append(Component.text("/duellbot\n\n", NamedTextColor.GOLD))
            .append(Component.text("─────────────\n", NamedTextColor.GRAY))
            .append(Component.text("Der Gegner nimmt\ndie Anfrage an.\nBeide werden\ndann zur ", NamedTextColor.BLACK))
            .append(Component.text("Arena\n", NamedTextColor.AQUA, TextDecoration.BOLD))
            .append(Component.text("teleportiert.", NamedTextColor.BLACK))
            .build();
    }

    /** Tipps */
    private static Component page10_tips() {
        return Component.text()
            .append(Component.text("──── Tipps ────\n\n", NamedTextColor.DARK_RED, TextDecoration.BOLD))
            .append(Component.text("★ ", NamedTextColor.GOLD))
            .append(Component.text("Schwache Mobs mit\nZauberkarten\nschützen.\n\n", NamedTextColor.BLACK))
            .append(Component.text("★ ", NamedTextColor.GOLD))
            .append(Component.text("Starke Mobs für\nDirektangriffe\nnutzen.\n\n", NamedTextColor.BLACK))
            .append(Component.text("★ ", NamedTextColor.GOLD))
            .append(Component.text("AP gut einteilen –\nsie verfallen!\n\n", NamedTextColor.BLACK))
            .append(Component.text("       Viel Spaß! ★", NamedTextColor.GOLD, TextDecoration.BOLD))
            .build();
    }
}
