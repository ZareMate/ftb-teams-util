# FTB Teams Util

NeoForge 1.21.1 utility mod for FTB Teams.

## Commands

- `/ftbteams-util team <player-nickname|player-uuid|team-name#8char-id>`
- `/ftbteams-util adm-msg <team-name#8char-id> <message>`

`team` accepts player nicknames (including cached offline names) or player UUIDs without `#`. To target a team directly, use its `name#8-character-id` identifier, for example `The_Hidden_Hand_Mafia#261bfebf`. A plain team name is not treated as a team identifier. The command displays the resolved team ID, name, type, owner, and members with ranks.

`adm-msg` sends `[ADM] <message>` through FTB Teams team chat, rendered red.

## LuckPerms

- `ftbteamsutil.command` — root access
- `ftbteamsutil.team` — `/ftbteams-util team`
- `ftbteamsutil.adm_msg` — `/ftbteams-util adm-msg`

Operator level 3 is also allowed. If LuckPerms is unavailable, non-operators are denied.

## Dependencies

Requires FTB Teams for NeoForge 1.21.1. The implementation uses the public FTB Teams API (`FTBTeamsAPI`, `TeamManager`, `Team`, and `TeamRank`) rather than FTB Teams implementation classes.

## Java API

Other mods can use the simple `FTBTeamsUtilAPI` helper:

```java
FTBTeamsUtilAPI.getTeamMembers(playerUuid);
```

This returns an immutable `Set<UUID>` containing the members of the player's current effective FTB Team. It returns an empty set when no team is available.

Additional helpers are available:

```java
FTBTeamsUtilAPI.getTeam(playerUuid);
FTBTeamsUtilAPI.getOnlineTeamMembers(playerUuid);
```

### Team color API (1.0.1)

`FTBTeamsUtilAPI.getTeamColor(UUID)` returns `OptionalInt` containing the effective
team's 24-bit RGB color (`0xRRGGBB`). Null UUIDs, an unloaded manager, missing teams
and missing color values return empty. Personal-team colors are included.
