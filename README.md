# Mine!
A paper based plugin for claiming land in modern versions of Minecraft!

## Commands
Mine! works off of one main command ("/mine" or "/.") with lots of options.

pointer - This command gives you a [pointer](#pointer) item to select an area with.

## Interacting with the plugin

### Pointer:
This item can be obtained by using the "/. pointer" [command](#commands).  
Left-clicking with the pointer will set a corner of your claim. Right-clicking will set the other corner. Clicking again will change the selected block.  
When a block is selected it will change to a marker block & light up. When two or more blocks are selected an outline of Red Glass will be rendered along the edges of the selected area.

## FutureGoals:
- ~~Claim an area.~~
- Config for the max & min amount of blocks claimable.
- Config for the max amount of claims for players (probably will have a choice between num of claims AND/OR max claimable blocks).
- Above config but for players with certain perms only.
- Ability to override config values for certain players.
- See list of claimed land.
- Delete a claim (config for if the player has to be standing in it).
- Resize a claim.
- Command to see claim outlines.
- Unstuck command for to teleport you out of a claim after 10 seconds of nothing happening to a player.
- Permissions for claims.
- Default permissions for any player.
- Ability to give players specific permissions.
- Ability to create/delete/edit a set of permissions & assign it to players.
- Permissions for breaking blocks.
- Permissions for placing blocks.
- Permissions for interacting with blocks.
- Permissions for placing rideable entities (such as a boat, or putting a saddle on a horse).
- Permissions for opening chests (the value set here overrides the permission for interacting with block).
- Settings for explosions in claim.
- Settings for mob griefing in claim.
- Settings for mobs picking up items in claim.
- Settings for liquid flowing in claim.
- Settings for water & lava creating obsidian or cobble in claim.
- Settings for piston push in claim.
- Settings for fire spread in claim.
- Settings for PVP in claim.
- Overrides for permissions and settings in config file.
- Players with a perm can see a list of all claims & info abt them.
- Players with a perm can search though claims based on player name and/or claim name.

## Misc (temporary):
Changing the world names will completely **BREAK** claims with this plugin.  
The players are managed using their uuid, so changing your name **won't** have any impact.  
If you delete the (path to db) then **all** data for clans, & member will **PERMANENTLY** be erased. Do not delete this file. If you wish to delete data see [TODO](https://www.youtube.com/watch?v=dQw4w9WgXcQ).  
The key {newLine} is used in the config files instead of just putting a new line as to keep everything on the same line when editing the config files, which makes the process easier.
