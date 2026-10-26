package com.zaremate.ftb_teams_util;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class TeamCommandParserTest {
    private static final UUID TEAM_ID = UUID.fromString("261bfebf-1234-4567-89ab-112233445566");

    @Test void acceptsTeamNameAndHashSuffix() {
        assertTrue(TeamCommandParser.isTeamIdentifier("The_Hidden_Hand_Mafia#261bfebf"));
        assertTrue(TeamCommandParser.matchesTeam("The_Hidden_Hand_Mafia#261bfebf", "The_Hidden_Hand_Mafia", TEAM_ID));
        assertTrue(TeamCommandParser.matchesTeam("the_hidden_hand_mafia#261BFEBF", "The_Hidden_Hand_Mafia", TEAM_ID));
    }

    @Test void rejectsIncorrectTeamIdentifiers() {
        assertFalse(TeamCommandParser.isTeamIdentifier("The_Hidden_Hand_Mafia"));
        assertFalse(TeamCommandParser.isTeamIdentifier("The_Hidden_Hand_Mafia#"));
        assertFalse(TeamCommandParser.isTeamIdentifier("The_Hidden_Hand_Mafia#nothex"));
        assertFalse(TeamCommandParser.isTeamIdentifier("The_Hidden_Hand_Mafia#261bfeb"));
        assertFalse(TeamCommandParser.isTeamIdentifier("#261bfebf"));
        assertFalse(TeamCommandParser.matchesTeam("Different#261bfebf", "The_Hidden_Hand_Mafia", TEAM_ID));
        assertFalse(TeamCommandParser.matchesTeam("The_Hidden_Hand_Mafia#ffffffff", "The_Hidden_Hand_Mafia", TEAM_ID));
    }

    @Test void routesPlainNamesAndUuidsToPlayersOnly() {
        assertTrue(TeamCommandParser.isPlayerLookup("ZareMate"));
        assertTrue(TeamCommandParser.isPlayerLookup("44eab6dc-5704-40db-94cd-d75867257de0"));
        assertTrue(TeamCommandParser.isPlayerLookup("The_Hidden_Hand_Mafia"));
        assertFalse(TeamCommandParser.isPlayerLookup("The_Hidden_Hand_Mafia#261bfebf"));
        assertFalse(TeamCommandParser.isPlayerLookup("Name#bad-id"));
    }

    @Test void keepsWholeAdminMessageIncludingSpecialCharacters() {
        var parsed = TeamCommandParser.parseAdminMessage("The_Hidden_Hand_Mafia#261bfebf Hello #everyone! /test \"quoted\"");
        assertNotNull(parsed);
        assertEquals("The_Hidden_Hand_Mafia#261bfebf", parsed.teamIdentifier());
        assertEquals("Hello #everyone! /test \"quoted\"", parsed.message());
    }

    @Test void rejectsMissingAdminArguments() {
        assertNull(TeamCommandParser.parseAdminMessage("Team#261bfebf"));
        assertNull(TeamCommandParser.parseAdminMessage("Team#261bfebf "));
        assertNull(TeamCommandParser.parseAdminMessage(" message"));
    }

    @Test void retainsMessageSpacesExactly() {
        var parsed = TeamCommandParser.parseAdminMessage("Team#261bfebf  two spaces");
        assertNotNull(parsed);
        assertEquals(" two spaces", parsed.message());
    }
}
