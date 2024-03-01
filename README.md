# Is This Mod Legit?

[Modrinth Link](https://modrinth.com/mod/is-this-mod-legit) | [Web Client](https://isthismodlegit.anopensauce.dev/) [(source)](https://github.com/AnOpenSauceDev/IsThisModLegit-Web)

**Note: This mod has been superceeded by the much safer Web Client.** No further updates are planned for the mod version.

A pretty tiny mod that checks the hashes of every mod in your mods folder against Modrinth to see if the mod you're using is tamper-free.

Works as long **as you have less than 300 _individual (.jar file)_ mods** (because you'll get rate-limited going past that, also assuming this is the first run.)

Due to this mod using the Modrinth API, this will fail checks on binaries from CurseForge/Github that aren't identical to what's on Modrinth.

## Caching System

As of 0.2.0, the ITML will resolve to one of two hash "databases"
- `~/.minecraft/config/ITML.cache`
- Modrinth's "version from hash" API.
  
After verifying a hash with the Modrinth API, the mod's hash will be stored over in `ITML.cache`, of which any hashes located in there will be whitelisted (with a notice in the logger itself.)
This essentially means that there should only be a noticable increase in load times when opening a **large** modpack for the first time.

This also means that you only need to connect to the internet once at startup to verify your mods, and to recconnect when you add/update any mods.

To rebuild the cache, just delete the file, nothing will break.
