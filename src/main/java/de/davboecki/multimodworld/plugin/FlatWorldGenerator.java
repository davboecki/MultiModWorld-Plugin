package de.davboecki.multimodworld.plugin;

import org.bukkit.Material;
import org.bukkit.World;

import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class FlatWorldGenerator extends ChunkGenerator {
    //This is where you stick your populators - these will be covered in another tutorial
    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Arrays.asList((BlockPopulator) new BlankPopulator());
    }

    //This needs to be set to return true to override minecraft's default behaviour
    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    //This converts relative chunk locations to bytes that can be written to the chunk
    public int xyzToByte(int x, int y, int z) {
        return (((x * 16) + z) * 128) + y;
    }

    @Override
    public byte[] generate(World world, Random rand, int chunkx, int chunkz) {
        byte[] result = new byte[32768];
        int y = 0;

        //This will set the floor of each chunk at bedrock level to bedrock
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                //result[xyzToByte(x, y, z)] = (byte) Material.BEDROCK.getId();
            }
        }

        if ((chunkx == 0) && (chunkz == 0)) {
            //Stein
            for (int x = 0; x < 14; x++) {
                for (y = 0; y < 11; y++) {
                    for (int z = 0; z < 14; z++) {
                        result[xyzToByte(x, y + 5, z)] = 1;
                    }
                }
            }

            //Lava
            for (int x = 0; x < 12; x++) {
                for (y = 0; y < 9; y++) {
                    for (int z = 0; z < 12; z++) {
                        result[xyzToByte(x + 1, y + 6, z + 1)] = (byte) Material.LAVA.getId();
                    }
                }
            }

            //Glass
            for (int x = 0; x < 10; x++) {
                for (y = 0; y < 7; y++) {
                    for (int z = 0; z < 10; z++) {
                        result[xyzToByte(x + 2, y + 7, z + 2)] = (byte) Material.GLASS.getId();
                    }
                }
            }

            //Luft
            for (int x = 0; x < 8; x++) {
                for (y = 0; y < 5; y++) {
                    for (int z = 0; z < 8; z++) {
                        result[xyzToByte(x + 3, y + 8, z + 3)] = 0;
                    }
                }
            }

            //Tor 1
            result[xyzToByte(3, 8, 5)] = (byte) Material.OBSIDIAN.getId();
            result[xyzToByte(3, 9, 5)] = (byte) Material.OBSIDIAN.getId();
            result[xyzToByte(3, 10, 5)] = (byte) Material.OBSIDIAN.getId();
            result[xyzToByte(3, 10, 6)] = (byte) Material.OBSIDIAN.getId();
            result[xyzToByte(3, 10, 7)] = (byte) Material.OBSIDIAN.getId();
            result[xyzToByte(3, 9, 7)] = (byte) Material.OBSIDIAN.getId();
            result[xyzToByte(3, 8, 7)] = (byte) Material.OBSIDIAN.getId();
            //Tor 2
            result[xyzToByte(10, 8, 5)] = (byte) Material.STONE.getId();
            result[xyzToByte(10, 9, 5)] = (byte) Material.STONE.getId();
            result[xyzToByte(10, 10, 5)] = (byte) Material.STONE.getId();
            result[xyzToByte(10, 10, 6)] = (byte) Material.STONE.getId();
            result[xyzToByte(10, 10, 7)] = (byte) Material.STONE.getId();
            result[xyzToByte(10, 9, 7)] = (byte) Material.STONE.getId();
            result[xyzToByte(10, 8, 7)] = (byte) Material.STONE.getId();
        }

        if ((chunkx == 1) && (chunkz == 0)) {
            //Stein
            for (int x = 0; x < 14; x++) {
                for (y = 0; y < 11; y++) {
                    for (int z = 0; z < 14; z++) {
                        result[xyzToByte(x, y + 5, z)] = 1;
                    }
                }
            }

            //Lava
            for (int x = 0; x < 12; x++) {
                for (y = 0; y < 9; y++) {
                    for (int z = 0; z < 12; z++) {
                        result[xyzToByte(x + 1, y + 6, z + 1)] = (byte) Material.LAVA.getId();
                    }
                }
            }

            //Glass
            for (int x = 0; x < 10; x++) {
                for (y = 0; y < 7; y++) {
                    for (int z = 0; z < 10; z++) {
                        result[xyzToByte(x + 2, y + 7, z + 2)] = (byte) Material.GLASS.getId();
                    }
                }
            }

            //Luft
            for (int x = 0; x < 8; x++) {
                for (y = 0; y < 5; y++) {
                    for (int z = 0; z < 8; z++) {
                        result[xyzToByte(x + 3, y + 8, z + 3)] = 0;
                    }
                }
            }

            //Tor 1
            result[xyzToByte(3, 8, 5)] = (byte) Material.OBSIDIAN.getId();
            result[xyzToByte(3, 9, 5)] = (byte) Material.OBSIDIAN.getId();
            result[xyzToByte(3, 10, 5)] = (byte) Material.OBSIDIAN.getId();
            result[xyzToByte(3, 10, 6)] = (byte) Material.OBSIDIAN.getId();
            result[xyzToByte(3, 10, 7)] = (byte) Material.OBSIDIAN.getId();
            result[xyzToByte(3, 9, 7)] = (byte) Material.OBSIDIAN.getId();
            result[xyzToByte(3, 8, 7)] = (byte) Material.OBSIDIAN.getId();
            //Tor 2
            result[xyzToByte(10, 8, 5)] = (byte) Material.STONE.getId();
            result[xyzToByte(10, 9, 5)] = (byte) Material.STONE.getId();
            result[xyzToByte(10, 10, 5)] = (byte) Material.STONE.getId();
            result[xyzToByte(10, 10, 6)] = (byte) Material.STONE.getId();
            result[xyzToByte(10, 10, 7)] = (byte) Material.STONE.getId();
            result[xyzToByte(10, 9, 7)] = (byte) Material.STONE.getId();
            result[xyzToByte(10, 8, 7)] = (byte) Material.STONE.getId();
        }

        return result;
    }
}
