package com.zaremate.ftb_teams_util;

import java.util.Locale;
import java.util.UUID;

/** Side-effect-free command parsing helpers, also used by the Gradle unit tests. */
final class TeamCommandParser {
    private TeamCommandParser() {}

    record AdminMessage(String teamIdentifier, String message) {}

    static AdminMessage parseAdminMessage(String input) {
        int separator = input.indexOf(' ');
        if (separator <= 0 || separator == input.length() - 1) return null;
        return new AdminMessage(input.substring(0, separator), input.substring(separator + 1));
    }

    static boolean isTeamIdentifier(String input) {
        int hash = input.lastIndexOf('#');
        if (hash <= 0) return false;
        return input.substring(hash + 1).matches("[0-9a-fA-F]{8}");
    }

    static boolean matchesTeam(String identifier, String name, UUID id) {
        if (!isTeamIdentifier(identifier)) return false;
        int hash = identifier.lastIndexOf('#');
        return identifier.substring(0, hash).equalsIgnoreCase(name)
            && id.toString().replace("-", "").regionMatches(true, 0,
                identifier.substring(hash + 1), 0, 8);
    }

    static boolean isPlayerLookup(String input) {
        return !input.contains("#");
    }
}
