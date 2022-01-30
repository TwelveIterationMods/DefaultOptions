# Default Options

Minecraft Mod. A way for modpacks to ship a default (key) configuration without having to include an options.txt file.
Also allows local options from any mod .cfg file.

See [the license](LICENSE) for modpack permissions etc.

This mod is available for both Forge and Fabric (starting Minecraft 1.17).

#### Forge

[![Versions](http://cf.way2muchnoise.eu/versions/232131_latest.svg)](https://www.curseforge.com/minecraft/mc-mods/default-options)
[![Downloads](http://cf.way2muchnoise.eu/full_232131_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/default-options)

#### Fabric

[![Versions](http://cf.way2muchnoise.eu/versions/547694_latest.svg)](https://www.curseforge.com/minecraft/mc-mods/default-options-fabric)
[![Downloads](http://cf.way2muchnoise.eu/full_547694_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/default-options-fabric)

## Contributing

If you're interested in contributing to the mod, you can check
out [issues labelled as "help wanted"](https://github.com/ModdingForBlockheads/DefaultOptions/issues?q=is%3Aopen+is%3Aissue+label%3A%22help+wanted%22)
. These should be ready to be implemented as they are.

If you need help, feel free to join us on [Discord](https://discord.gg/scGAfXC).

## Supported Mods

By default, Default Options comes with support for the following mods:

- OptiFine
- ViveCraft

This means that commands such as `/defaultoptions saveOptions` will include options files from those mods and the
defaults will be loaded from the main `config/defaultoptions` folder.

Other mods may be supported through the use of extra default options (see below).

## Extra Default Options

The default options folder includes an `config/defaultoptions/extra` folder. Upon starting Minecraft, all files within
this folder will be copied into the Minecraft folder while retaining their folder structure (only if the file does not
exist in the Minecraft folder yet, i.e. just like the other default options, they only apply on the first run).

This allows support for more complex use cases or mods with custom options files that are not natively supported, such
as JourneyMap.

Note that this folder is not automatically populated when using the `/defaultoptions saveOptions` command. You are
required to manually copy the folders/files into the `extra` folder after you've configured defaults.

For example, to create default options for JourneyMap, you would:

1. Configure JourneyMap as you like
2. Copy the whole JourneyMap folder into the `config/defaultoptions/extra` so the resulting path
   is `config/defaultoptions/extra/journeymap`
3. Within that new folder, delete all files that should not be defaulted (e.g. `journeymap.log` and the `data` folder)
4. When running a fresh instance, the JourneyMap options will now be copied into the Minecraft folder before JourneyMap
   loads, making them the new default

## API

Default Options starting in Minecraft 1.18 provides an API for other mods to register their own default option files.

To use this API, you must specify Default Options as a build dependency in your Gradle file. You should only use classes
from within the `net.blay09.mods.defaultoptions.api` package, as other classes may have unexpected breaking changes.

To plug into Default Options, create a class implementing `DefaultOptionsPlugin`. This class will be loaded by a Service
Loader, so you must also create a new file
under `META-INF/services/net.blay09.mods.defaultoptions.api.DefaultOptionsPlugin` and specify the full package & class name of
your plugin in there. This is necessary because the normal mod loading hooks run too late for most default options to apply.

Within that class you can use `DefaultOptionsAPI.registerOptionsFile` for simple use-cases,
or `DefaultOptionsAPI.registerOptionsHandler` for more complex use cases where you need more control over the underlying
implementation.

### Example:

```java
package com.example;

public class ExamplePlugin implements DefaultOptionsPlugin {

    @Override
    public void initialize() {
        DefaultOptionsAPI.registerOptionsFile(new File(DefaultOptions.getMinecraftDataDir(), "options.txt"))
                .withLinePredicate(line -> !line.startsWith("key_"))
                .withSaveHandler(() -> Minecraft.getInstance().options.save());
    }
}
```

`META-INF/services/net.blay09.mods.defaultoptions.api.DefaultOptionsPlugin`

```
com.example.ExamplePlugin
```