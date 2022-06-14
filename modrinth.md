<p>
    <a style="text-decoration: none;" href="https://modrinth.com/mod/balm"> 
        <img src="https://blay09.net/files/brand/requires_balm.png" alt="Requires Balm" width="217" height="51" /> 
    </a>
    <img src="https://blay09.net/files/brand/spacer.png" alt="" width="20" height="51" />
    <a style="text-decoration: none;" href="https://www.patreon.com/blay09"> 
        <img src="https://blay09.net/files/brand/patreon.png" alt="Become a Patron" width="217" height="51" /> 
    </a> 
    <img src="https://blay09.net/files/brand/spacer.png" alt="" width="21" height="51" /> 
    <a style="text-decoration: none;" href="https://twitter.com/BlayTheNinth">
        <img src="https://blay09.net/files/brand/twitter.png" alt="Follow me on Twitter" width="51" height="51" />
    </a>
    <a style="text-decoration: none;" href="https://discord.gg/scGAfXC">
        <img src="https://blay09.net/files/brand/discord.png" alt="Join our Discord" width="51" height="51" />
    </a>
</p>

![](https://blay09.net/files/brand/defaultoptions.png)

This mod can be used by Modpack developers to distribute default options and keybindings without overriding user changes on every modpack update.

Instead of shipping the options.txt directly, you will ship a "defaultoptions" folder in your modpack which contains the defaults previously saved through the /defaultoptions command.

## Features

- On the first run, users will start out with the options you configured - and future changes won't be overridden on updated
- Keybinds configured by the modpack will be set as new defaults, meaning changes made by users will persist even on updates
- The server list will be pre-populated with the server list provided by the modpack (and user changes will persist on updates, still)

## Instructions

1. Configure the options, keybinds and server list as you would like them to be shipped in the modpack
2. Join any world and run "/defaultoptions saveAll" to save your current options in the defaultoptions folder (located in the config folder where it should remain)
3. Do not include the options.txt and servers.dat from the root directory in your modpack
4. Fresh installations (meaning no existing options.txt) will now start out with your default options

**If you are using FoamFix Anarchy Version**, disable `B:initOptions` in the FoamFix config or else Default Options will not work. See this [GitHub issue](https://github.com/blay09/DefaultOptions/issues/25) for more info.