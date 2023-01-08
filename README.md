
![image](https://user-images.githubusercontent.com/70060472/211222840-2b83142d-8252-4959-a5b2-b4ca462b22a7.png)



Staff Spectate is a Minecraft server plugin designed to allow your staff to check on players without them losing their progress in survival.
This plugin helps eliminate the abuse of spectator mode and changing game modes.
On issuing the command /spectate <player-name> the staff member will be put into Spectator mode and teleported to the player. The plugin will save the location they issued the command and send them back to that location upon ending their spectator session.

The session will be ended when the user:
- Leave the game
- Issue /spectate leave
- Start to spectate another player.

Commands

    /spectate <player>
    /spectate leave
    /spectate exit-here

Admin Commands

    /spectateadmin info
    /spectateadmin reload

Permissions: (OP has all permissions)

staffspectate.use:
   - staffspectate.spectate
   - staffspectate.leave
staffspectate.admin:
   - staffspectate.notify
   - staffspectate.leavebypass

staffspectate.spectate
staffspectate.leave
staffspectate.notify
staffspectate.leavebypass
