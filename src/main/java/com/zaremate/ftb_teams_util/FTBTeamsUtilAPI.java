package com.zaremate.ftb_teams_util;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Small convenience API for querying FTB Teams.
 *
 * <p>This class intentionally exposes UUIDs so callers do not need to depend
 * on FTB Teams implementation classes.</p>
 */
public final class FTBTeamsUtilAPI {
    private FTBTeamsUtilAPI() {}

    /**
     * Gets the player's current effective FTB Team.
     *
     * @param playerUuid player UUID
     * @return the team, or empty if the player has no known team
     */
    public static Optional<Team> getTeam(UUID playerUuid) {
        if (playerUuid == null || !FTBTeamsAPI.api().isManagerLoaded()) {
            return Optional.empty();
        }

        return FTBTeamsAPI.api().getManager().getTeamForPlayerID(playerUuid);
    }

    /**
     * Gets all members of the player's current effective FTB Team.
     *
     * @param playerUuid player UUID
     * @return an immutable set of member UUIDs, or an empty set if no team exists
     */
    public static Set<UUID> getTeamMembers(UUID playerUuid) {
        return getTeam(playerUuid)
                .map(team -> Collections.unmodifiableSet(team.getMembers()))
                .orElseGet(Collections::emptySet);
    }

    /**
     * Gets all online members of the player's current effective FTB Team.
     *
     * @param playerUuid player UUID
     * @return an immutable set of online team members
     */
    public static Set<ServerPlayer> getOnlineTeamMembers(UUID playerUuid) {
        return getTeam(playerUuid)
                .map(team -> Collections.unmodifiableSet(
                        Set.copyOf(team.getOnlineMembers())
                ))
                .orElseGet(Collections::emptySet);
    }
}
