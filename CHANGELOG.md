- Fixed modded keybinds resetting to their original default on Forge and NeoForge
- Fixed options being unnecessarily saved even if no keys were changed
- Added some additional logging

---

- Changed the way keys are remapped to improve stability
  - `knownkeys.txt` has been removed, instead `options.txt` is now used as basis on whether a key has already been modified by the user.
  - Defaults will apply as previously.
  - Bound key mappings will now only be overridden if the key does not already exist in `options.txt`, and matches whatever the original mod/vanilla default was.
- Added improved logging for better debuggability