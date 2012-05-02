package de.davboecki.multimodworld.plugin.settings;

import java.util.ArrayList;

public class Default {
	public static ArrayList<Long> ItemList(){
		ArrayList<Long> tmp = new ArrayList<Long>();
		for(long i=0;i<=124;i++) {
			tmp.add(i);
		}
		for(long i=256;i<=385;i++) {
			tmp.add(i);
		}
		for(long i=2256;i<=2266;i++) {
			tmp.add(i);
		}
		return tmp;
	}
	
	public static ArrayList<String> EntityList(){
		ArrayList<String> tmp = new ArrayList<String>();
		/*
		tmp.add("net.minecraft.server.EntityCreeper");
		tmp.add("net.minecraft.server.EntitySpider");
		tmp.add("net.minecraft.server.EntitySkeleton");
		tmp.add("net.minecraft.server.EntityFallingSand");
		tmp.add("net.minecraft.server.EntityZombie");
		tmp.add("net.minecraft.server.EntitySpider");
		tmp.add("net.minecraft.server.EntityChicken");
		tmp.add("net.minecraft.server.EntityItem");
		tmp.add("net.minecraft.server.EntityPigZombie");
		tmp.add("net.minecraft.server.EntityEnderman");
		tmp.add("net.minecraft.server.EntityCow");
		tmp.add("net.minecraft.server.EntityWolf");
		tmp.add("net.minecraft.server.EntityPig");
		tmp.add("net.minecraft.server.EntitySheep");
		tmp.add("net.minecraft.server.EntitySquid");
		tmp.add("net.minecraft.server.EntityTNTPrimed");
		tmp.add("net.minecraft.server.EntityFireball");
		tmp.add("net.minecraft.server.EntityGhast");
		tmp.add("net.minecraft.server.abc");
		*/
		tmp.add("net.minecraft.server.Entity");
		tmp.add("net.minecraft.server.EntityCreature");
		tmp.add("net.minecraft.server.EntityAnimal");
		tmp.add("net.minecraft.server.EntityArrow");
		tmp.add("net.minecraft.server.EntityBlaze");
		tmp.add("net.minecraft.server.EntityBoat");
		tmp.add("net.minecraft.server.EntityCaveSpider");
		tmp.add("net.minecraft.server.EntityChicken");
		tmp.add("net.minecraft.server.EntityComplex");
		tmp.add("net.minecraft.server.EntityComplexPart");
		tmp.add("net.minecraft.server.EntityCow");
		tmp.add("net.minecraft.server.EntityCreature");
		tmp.add("net.minecraft.server.EntityCreeper");
		tmp.add("net.minecraft.server.EntityDamageSource");
		tmp.add("net.minecraft.server.EntityDamageSourceIndirect");
		tmp.add("net.minecraft.server.EntityEgg");
		tmp.add("net.minecraft.server.EntityEnderCrystal");
		tmp.add("net.minecraft.server.EntityEnderDragon");
		tmp.add("net.minecraft.server.EntityEnderPearl");
		tmp.add("net.minecraft.server.EntityEnderSignal");
		tmp.add("net.minecraft.server.EntityEnderman");
		tmp.add("net.minecraft.server.EntityExperienceOrb");
		tmp.add("net.minecraft.server.EntityFallingBlock");
		tmp.add("net.minecraft.server.EntityFireball");
		tmp.add("net.minecraft.server.EntityFishingHook");
		tmp.add("net.minecraft.server.EntityFlying");
		tmp.add("net.minecraft.server.EntityGhast");
		tmp.add("net.minecraft.server.EntityGiantZombie");
		tmp.add("net.minecraft.server.EntityGolem");
		tmp.add("net.minecraft.server.EntityHuman");
		tmp.add("net.minecraft.server.EntityIronGolem");
		tmp.add("net.minecraft.server.EntityItem");
		tmp.add("net.minecraft.server.EntityLiving");
		tmp.add("net.minecraft.server.EntityMagmaCube");
		tmp.add("net.minecraft.server.EntityMinecart");
		tmp.add("net.minecraft.server.EntityMonster");
		tmp.add("net.minecraft.server.EntityMushroomCow");
		tmp.add("net.minecraft.server.EntityPainting");
		tmp.add("net.minecraft.server.EntityPig");
		tmp.add("net.minecraft.server.EntityPigZombie");
		tmp.add("net.minecraft.server.EntityPlayer");
		tmp.add("net.minecraft.server.EntityPotion");
		tmp.add("net.minecraft.server.EntityProjectile");
		tmp.add("net.minecraft.server.EntitySheep");
		tmp.add("net.minecraft.server.EntitySilverfish");
		tmp.add("net.minecraft.server.EntitySkeleton");
		tmp.add("net.minecraft.server.EntitySlime");
		tmp.add("net.minecraft.server.EntitySmallFireball");
		tmp.add("net.minecraft.server.EntitySnowball");
		tmp.add("net.minecraft.server.EntitySnowman");
		tmp.add("net.minecraft.server.EntitySpider");
		tmp.add("net.minecraft.server.EntitySquid");
		tmp.add("net.minecraft.server.EntityTNTPrimed");
		tmp.add("net.minecraft.server.EntityTracker");
		tmp.add("net.minecraft.server.EntityTrackerEntry");
		tmp.add("net.minecraft.server.EntityThrownExpBottle");
		tmp.add("net.minecraft.server.EntityTypes");
		tmp.add("net.minecraft.server.EntityVillager");
		tmp.add("net.minecraft.server.EntityWaterAnimal");
		tmp.add("net.minecraft.server.EntityWeather");
		tmp.add("net.minecraft.server.EntityWeatherLighting");
		tmp.add("net.minecraft.server.EntityWolf");
		tmp.add("net.minecraft.server.EntityZombie");
		tmp.add("net.minecraft.server.EntityOcelot");
		return tmp;
	}
}
