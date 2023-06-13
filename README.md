# Default Options

Minecraft Mod. A way for modpacks to ship a default (key) configuration without having to include an options.txt file.

- [Modpack Permissions](https://mods.twelveiterations.com/permissions)

#### Forge

[![Versions](http://cf.way2muchnoise.eu/versions/232131_latest.svg)](https://www.curseforge.com/minecraft/mc-mods/default-options)
[![Downloads](http://cf.way2muchnoise.eu/full_232131_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/default-options)

#### Fabric

[![Versions](http://cf.way2muchnoise.eu/versions/547694_latest.svg)](https://www.curseforge.com/minecraft/mc-mods/default-options-fabric)
[![Downloads](http://cf.way2muchnoise.eu/full_547694_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/default-options-fabric)

## Contributing

If you're interested in contributing to the mod, you can check out [issues labelled as "help wanted"](https://github.com/TwelveIterationMods/DefaultOptions/issues?q=is%3Aopen+is%3Aissue+label%3A%22help+wanted%22).

When it comes to new features, it's best to confer with me first to ensure we share the same vision. You can join us on [Discord](https://discord.gg/VAfZ2Nau6j) if you'd like to talk.

Contributions must be done through pull requests. I will not be able to accept translations, code or other assets through any other channels.

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

Starting in Minecraft 1.18, Default Options provides an API for other mods to register their own default option files.

To use this API, you must specify Default Options as a build dependency in your Gradle file (see next section). You should only use classes
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

## Adding Default Options to a development environment

Note that you will also need to add Balm if you want to test your integration in your environment.

### Using CurseMaven

Add the following to your `build.gradle`:

```groovy
repositories {
    maven { url "https://www.cursemaven.com" }
}

dependencies {
    // Replace ${default_options_file_id} and ${balm_file_id} with the id of the file you want to depend on.
    // You can find it in the URL of the file on CurseForge (e.g. 3914527).
    // Forge: implementation fg.deobf("curse.maven:balm-531761:${balm_file_id}")
    // Fabric: modImplementation "curse.maven:balm-fabric-500525:${balm_file_id}"
    
    // Forge: implementation fg.deobf("curse.maven:default-options-232131:${default_options_file_id}")
    // Fabric: modImplementation "curse.maven:default-options-fabric-547694:${default_options_file_id}"
}
```

### Using Twelve Iterations Maven (includes snapshot and mojmap versions)

Add the following to your `build.gradle`:

```groovy
repositories {
    maven { 
        url "https://maven.twelveiterations.com/repository/maven-public/" 
        
        content {
            includeGroup "net.blay09.mods"
        }
    }
}

dependencies {
    // Replace ${default_options_version} and ${balm_version} with the version you want to depend on. 
    // You can find the latest version for a given Minecraft version at https://maven.twelveiterations.com/service/rest/repository/browse/maven-public/net/blay09/mods/balm-common/ and https://maven.twelveiterations.com/service/rest/repository/browse/maven-public/net/blay09/mods/defaultoptions-common/
    // Common (mojmap): implementation "net.blay09.mods:balm-common:${balm_version}"
    // Forge: implementation fg.deobf("net.blay09.mods:balm-forge:${balm_version}")
    // Fabric: modImplementation "net.blay09.mods:balm-fabric:${balm_version}"
    
    // Common (mojmap): implementation "net.blay09.mods:defaultoptions-common:${default_options_version}"
    // Forge: implementation fg.deobf("net.blay09.mods:defaultoptions-forge:${default_options_version}")
    // Fabric: modImplementation "net.blay09.mods:defaultoptions-fabric:${default_options_version}"
}
```
