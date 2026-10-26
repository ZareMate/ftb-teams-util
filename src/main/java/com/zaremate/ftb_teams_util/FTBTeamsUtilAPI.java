package com.zaremate.ftb_teams_util;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collections;
import java.util.Optional;
import java.util.OptionalInt;
import dev.ftb.mods.ftbteams.api.property.TeamProperties;
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
     * Returns the effective team's color as a 24-bit RGB integer (0xRRGGBB).
     * Empty for a null UUID, unloaded team manager, or missing team/color.
     * Includes personal teams; callers decide whether to display them.
     */
    public static OptionalInt getTeamColor(UUID playerUuid) {
        var color = getTeam(playerUuid).map(team -> team.getProperty(TeamProperties.COLOR));
        return color.isPresent() ? OptionalInt.of(color.get().rgb() & 0xFFFFFF) : OptionalInt.empty();
    }

    /**
     * Gets all members of the player's current effective FTB Team.
     *
     * @param playerUuid player UUID
     * @return an immutable set of member UUIDs, or an empty set if no team exists
     */
    public static Set<UUID> getTeamMembers(UUID playerUuid) {
        return getTeam(playerUuid)
                .map(team -> Set.copyOf(team.getMembers()))
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
