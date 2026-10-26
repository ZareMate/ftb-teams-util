# FTB Teams Util

NeoForge 1.21.1 utility mod for FTB Teams.

## Commands

- `/ftbteams-util team <player|offline-player|uuid|team-name|team-name#8char-id|team-uuid>`
- `/ftbteams-util adm-msg <team-name|team-name#8char-id|team-uuid> <message>`

`team` displays the team ID, name, type, owner UUID, and every member with their FTB Teams rank and UUID. Player names are resolved from online players and the Minecraft GameProfile cache, so offline names are supported. UUIDs are accepted for both players and teams. Team names can be used alone if unique. When multiple teams share a name, use the `name#8-character-UUID-prefix` form, for example `The_Hidden_Hand_Mafia#261bfebf`. Team names containing `#` are supported via the same identifier syntax.

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
