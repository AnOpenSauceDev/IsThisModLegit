package com.github.anopensaucedev.isthismodlegit;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModOrigin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class IsThisModLegit implements ModInitializer {



    public static String VERSION_FILE_HASH_URL = "https://api.modrinth.com/v2/version_file/";

    public static Logger logger = LoggerFactory.getLogger("Is This Mod Legit?");

    @Override
    public void onInitialize() {
        CheckModAuthenticity();
    }

    //TODO: Optimize a bit more
    public static void CheckModAuthenticity() {
        for (ModContainer mod: FabricLoader.getInstance().getAllMods()){
                // don't bother looking for Jar-In-Jar'ed mods (since they're baked, and therefore in the modrinth version)
                if(mod.getOrigin().getKind() == ModOrigin.Kind.PATH){
                    for(Path path: mod.getOrigin().getPaths()){
                        try {
                            if(path.toFile().isFile()){

                                // ugly code
                            String hash = getSHA1(path.toFile()); // we use SHA1 because it's a bit faster.
                            String modrinthCheck = VERSION_FILE_HASH_URL + hash + "?algorithm=sha1";
                            URL url = new URL(modrinthCheck);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            // set a nice User Agent, and set the version as whatever the mod version is.
                            conn.setRequestProperty("User-Agent","AnOpenSauceDev/IsThisModLegit/" + FabricLoader.getInstance().getModContainer("isthismodlegit").get().getMetadata().getVersion() + " (https://github.com/AnOpenSauceDev/IsThisModLegit/issues)");
                            conn.connect();
                            int responseCode = conn.getResponseCode();
                            conn.disconnect();

                                // if a 410 is returned, that means the API is now dead.
                                if(responseCode == 410){
                                    logger.error("Modrinth has seemingly stopped supporting the backend used by this mod. This mod will now abort.");
                                    break;
                                }

                                // the only responses that exist (as of now) are either 200 or 404.
                            if(responseCode == 200){
                                logger.info("An exact hash of mod " + mod.getMetadata().getName() + " was located on Modrinth! This mod comes from a legitimate source.");
                            }else {
                                // if the mod isn't an edge-case like MC or Fabric itself...
                                if(!mod.getMetadata().getName().equals("Fabric Loader") && !mod.getMetadata().getName().equals("Minecraft")){
                                logger.warn("The mod hash of " + mod.getMetadata().getName() + " was NOT found on Modrinth! This mod may not be legitimate!");
                                }else {
                                    // if the mod is something core that obviously isn't on modrinth, but we aren't 100% sure.
                                    logger.warn("The mod hash of " + mod.getMetadata().getName() + " wasn't on modrinth, however it's probably nothing to worry about, unless you see this message twice for the same mod.");
                                }
                            }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

        }
    }


    public static String getSHA1(File file) throws
            IOException, NoSuchAlgorithmException {

        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        try (InputStream input = new FileInputStream(file)) {

            byte[] buffer = new byte[8192];
            int len = input.read(buffer);

            while (len != -1) {
                sha1.update(buffer, 0, len);
                len = input.read(buffer);
            }

            byte[] digest = sha1.digest();
            StringBuilder hexString = new StringBuilder();

            for (byte b : digest) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        }

    }

}
