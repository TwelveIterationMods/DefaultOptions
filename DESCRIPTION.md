This mod can be used by modpack developers to distribute default options and keybindings without overriding user changes on every modpack update.

Instead of shipping the options.txt directly, you will ship a "defaultoptions" folder in your modpack which contains the defaults previously saved through the /defaultoptions command.

This mod requires [Balm](https://www.curseforge.com/minecraft/mc-mods/balm), which must be installed alongside the mod.

### ![Features](https://mods.twelveiterations.com/img/features-header.png)

- On the first run, users will start out with the options you configured - and future changes won't be overridden on updated
- Keybinds configured by the modpack will be set as new defaults, meaning changes made by users will persist even on updates
- The server list will be pre-populated with the server list provided by the modpack (and user changes will persist on updates, still)

### ![Usage](https://mods.twelveiterations.com/img/usage-header.png)

1. Configure the options, keybinds and server list as you would like them to be shipped in the modpack
2. Join any world and run "/defaultoptions saveAll" to save your current options in the defaultoptions folder (located in the config folder where it should remain)
3. Do not include the options.txt and servers.dat from the root directory in your modpack
4. Fresh installations (meaning no existing options.txt) will now start out with your default options
