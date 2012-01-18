package de.davboecki.multimodworld.plugin.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.reader.UnicodeReader;

import de.davboecki.multimodworld.plugin.PrivatChest;
import de.davboecki.multimodworld.plugin.RoomLocation;
import de.davboecki.multimodworld.plugin.yaml.MyYamlConstructor;

public class Settings {
	//Settings
	public HashMap<String,Object> config = new HashMap<String,Object>();
	public HashMap<String,WorldSetting> WorldSettings = new HashMap<String,WorldSetting>();
	public HashMap<String,ArrayList<Long>> GroupItemLists = new HashMap<String,ArrayList<Long>>();
	public HashMap<String,ArrayList<String>> GroupEntityList = new HashMap<String,ArrayList<String>>();
	public ArrayList<String> WorldTagList = new ArrayList<String>();
	public ArrayList<Integer> KnownItems = new ArrayList<Integer>();
	public ArrayList<String> KnownEntities = new ArrayList<String>();
	public HashMap<String,ExchangeWorldSetting> ExchangeWorlds = new HashMap<String,ExchangeWorldSetting>();
	
	//Local Variables
	private Yaml yamlExchangeWorlds;
	private Yaml yamlconfig;
	private Yaml yamlWorldSettings;
	private Yaml yamlGroupItemLists;
	private Yaml yamlGroupEntityList;
	private Yaml yamlWorldTagList;
	private Yaml yamlKnownItems;
	private Yaml yamlKnownEntities;
	private PrivatChest plugin;
	private static Settings instance = null;
	
	public Settings(PrivatChest instance){
		plugin = instance;
		createyamlExchangeWorlds();
		createyamlconfig();
		createyamlWorldSettings();
		createyamlGroupItemLists();
		createyamlGroupEntityList();
		createyamlWorldTagList();
		createyamlKnownItems();
		createyamlKnownEntities();
		this.instance = this;
	}
	
	public static Settings getInstance(){
		return instance;
	}
	
	public static void ListItem(int id){
		if(instance.KnownItems == null) instance.KnownItems = new ArrayList<Integer>();
		if(!instance.KnownItems.contains(id)){
			instance.KnownItems.add(id);
			Collections.sort(instance.KnownItems);
			instance.saveKnownItems();
		}
	}

	
	public static void ListEntity(String Class){
		if(instance.KnownEntities == null) instance.KnownEntities = new ArrayList<String>();
		if(!instance.KnownEntities.contains(Class)){
			instance.KnownEntities.add(Class);
			Collections.sort(instance.KnownEntities);
			instance.saveKnownEntities();
		}
	}
	
	public static ArrayList<Long> getItemList(String WorldName){
		if(instance.WorldSettings.containsKey(WorldName)){
			String ListName = instance.WorldSettings.get(WorldName).ItemList;
			if(!ListName.toLowerCase().equals(ListName)){
				ListName = instance.ExchangeWorlds.get(WorldName).ItemList = ListName.toLowerCase();
				instance.saveWorldSettings();
			}
			if(!instance.GroupItemLists.containsKey(ListName)){
				instance.GroupItemLists.put(ListName,Default.ItemList());
				instance.saveGroupItemLists();
			}
			return instance.GroupItemLists.get(ListName);
		} else {
			return Default.ItemList();
		}
	}
	
	public static ArrayList<Long> getItemListExchangeWorld(String WorldName){
		if(instance.ExchangeWorlds.containsKey(WorldName)){
			String ListName = instance.ExchangeWorlds.get(WorldName).ItemList;
			if(!ListName.toLowerCase().equals(ListName)){
				ListName = instance.ExchangeWorlds.get(WorldName).ItemList = ListName.toLowerCase();
				instance.saveWorldSettings();
			}
			if(!instance.GroupItemLists.containsKey(ListName)){
				instance.GroupItemLists.put(ListName,Default.ItemList());
				instance.saveGroupItemLists();
			}
			return instance.GroupItemLists.get(ListName);
		} else {
			return Default.ItemList();
		}
	}

	public static WorldSetting getWorldSetting(String WorldName){
		if(instance.WorldSettings.containsKey(WorldName)){
			return instance.WorldSettings.get(WorldName);
		} else {
			return new WorldSetting();
		}
	}
	
	public static ArrayList<String> getEntityList(String WorldName){
		if(instance.WorldSettings.containsKey(WorldName)){
			String ListName = instance.WorldSettings.get(WorldName).EntityList;
			if(!ListName.toLowerCase().equals(ListName)){
				ListName = instance.WorldSettings.get(WorldName).EntityList = ListName.toLowerCase();
				instance.saveWorldSettings();
			}
			if(!instance.GroupEntityList.containsKey(ListName)){
				instance.GroupEntityList.put(ListName,Default.EntityList());
				instance.saveGroupEntityList();
			}
			return instance.GroupEntityList.get(ListName);
		} else {
			return Default.EntityList();
		}
	}

	public static boolean getTag(String WorldName,String Setting){
		if(instance.WorldSettings.containsKey(WorldName)){
			HashMap<String,Boolean> Tags = instance.WorldSettings.get(WorldName).Tags;
			if(!instance.WorldTagList.contains(Setting) || !Tags.containsKey(Setting)){
				if(!instance.WorldTagList.contains(Setting)){
					instance.WorldTagList.add(Setting);
					instance.saveWorldTagList();
				}
				for(WorldSetting Worldsetting:instance.WorldSettings.values()){
					if(!Worldsetting.Tags.containsKey(Setting)){
						Worldsetting.Tags.put(Setting, false);
					}
				}
			}
			return Tags.get(Setting);
		} else {
			return false;
		}
	}
	
	public void load(){
		loadExchangeWorlds();
		loadconfig();
		loadGroupItemLists();
		loadGroupEntityList();
		loadWorldSettings();
		loadWorldTagList();
		loadKnownItems();
		loadKnownEntities();
	}

	public void save(){
		saveExchangeWorlds();
		saveconfig();
		saveWorldSettings();
		saveGroupItemLists();
		saveGroupEntityList();
		saveWorldTagList();
		saveKnownItems();
		saveKnownEntities();
	}
	
	
	private void createyamlExchangeWorlds(){
		MyYamlConstructor cstr = new MyYamlConstructor(ExchangeWorldSetting.class);
        TypeDescription pDesc = new TypeDescription(ExchangeWorldSetting.class,"ExchangeWorldSetting");
        cstr.addTypeDescription(pDesc);
        pDesc = new TypeDescription(RoomLocation.class,"ChestRooms");
        pDesc.putListPropertyType("ChestRooms", RoomLocation.class);
        cstr.addTypeDescription(pDesc);
        this.yamlExchangeWorlds = new Yaml(cstr);
	}

	private void createyamlconfig(){
        this.yamlconfig = new Yaml();
	}

	private void createyamlWorldSettings(){
		MyYamlConstructor cstr = new MyYamlConstructor(WorldSetting.class);
        TypeDescription pDesc = new TypeDescription(WorldSetting.class,"WorldSetting");
        cstr.addTypeDescription(pDesc);
        pDesc = new TypeDescription(RoomLocation.class,"ChestRooms");
        pDesc.putListPropertyType("ChestRooms", RoomLocation.class);
        cstr.addTypeDescription(pDesc);
        this.yamlWorldSettings = new Yaml(cstr);
	}

	private void createyamlGroupItemLists(){
        this.yamlGroupItemLists = new Yaml();
	}

	private void createyamlGroupEntityList(){
        this.yamlGroupEntityList = new Yaml();
	}

	private void createyamlWorldTagList(){
        this.yamlWorldTagList = new Yaml();
	}

	private void createyamlKnownItems(){
        this.yamlKnownItems = new Yaml();
	}

	private void createyamlKnownEntities(){
        this.yamlKnownEntities = new Yaml();
	}
	
	private void loadExchangeWorlds(){
        FileInputStream pFile;
        File dir = new File(plugin.getDataFolder().getPath() + "/ExchangeWorlds/");
        
        dir.mkdirs();
        String[] children = dir.list();
        if(children == null) return;
        
        for(String child : children){
        	if(child == null) continue;
        	File file = new File(plugin.getDataFolder().getPath() + "/ExchangeWorlds/" + child);
        	if(!child.endsWith(".yml")) continue;
        	String Name = child.substring(0, child.indexOf("."));
        	try {
            	pFile = new FileInputStream(file);
            	ExchangeWorlds.put(Name,ExchangeWorldSetting.parse(yamlExchangeWorlds.load(new UnicodeReader(pFile))));
        	} catch (FileNotFoundException e) {
        		//plugin.log.severe("[PrivatChest] Could not load PrivatChest Config. File Not Found.");
        	} catch (Exception ex) {
        		ex.printStackTrace();
        		plugin.log.severe("[PrivatChest] Could not load PrivatChest Config.");
        	}
        }
	}
	
    private void saveExchangeWorlds(){
        FileOutputStream stream = null;
        File parent = new File(plugin.getDataFolder().getPath() + "/ExchangeWorlds/world.yml").getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        for(String WorldName: ExchangeWorlds.keySet()){
	        try {
	            stream = new FileOutputStream(new File(plugin.getDataFolder().getPath() + "/ExchangeWorlds/" + WorldName + ".yml"));
	            OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8");
	            this.yamlExchangeWorlds.dump(ExchangeWorlds.get(WorldName), writer);
	        } catch (IOException e) {
	        	plugin.log.severe("[PrivatChest] Could not save PrivatChest Config.");
	        } finally {
	            try {
	                if (stream != null) {
	                    stream.close();
	                }
	            } catch (IOException e) {}
	        }
	    }
    }
	
	private void loadconfig(){
        FileInputStream pFile;
        try {
           	pFile = new FileInputStream(new File(plugin.getDataFolder().getPath() + "/config.yml"));
           	config = (HashMap<String,Object>) yamlconfig.load(new UnicodeReader(pFile));
        } catch (FileNotFoundException e) {
        	plugin.log.severe("[PrivatChest] Could not load PrivatChest Config. File Not Found.");
        } catch (Exception ex) {
        	ex.printStackTrace();
        	plugin.log.severe("[PrivatChest] Could not load PrivatChest Config.");
        }
        //Parse
        if(!config.containsKey("debug")){
        	config.put("debug", false);
        	saveconfig();
        } else {
        	Object fobject = config.get("debug");
        	if(fobject instanceof Boolean){
        		if((Boolean)fobject){
        			PrivatChest.setdebug(true);
        		}
        	}
        }
	}
	
    private void saveconfig(){
        FileOutputStream stream = null;
        File parent = new File(plugin.getDataFolder().getPath() + "/config.yml").getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        try {
        	stream = new FileOutputStream(new File(plugin.getDataFolder().getPath() + "/config.yml"));
	        OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8");
	        this.yamlconfig.dump(config, writer);
        } catch (IOException e) {
        	plugin.log.severe("[PrivatChest] Could not save PrivatChest Config.");
        } finally {
        	try {
        		if (stream != null) {
        			stream.close();
        		}
        	} catch (IOException e) {}
	    }
    }
    
	private void loadWorldSettings(){
        FileInputStream pFile;
        File dir = new File(plugin.getDataFolder().getPath() + "/WorldSettings/");
        
        dir.mkdirs();
        String[] children = dir.list();
        if(children == null) return;
        
        for(String child : children){
        	if(child == null) continue;
        	File file = new File(plugin.getDataFolder().getPath() + "/WorldSettings/" + child);
        	if(!child.endsWith(".yml")) continue;
        	String Name = child.substring(0, child.indexOf("."));
        	try {
            	pFile = new FileInputStream(file);
            	WorldSettings.put(Name,(WorldSetting)yamlWorldSettings.load(new UnicodeReader(pFile)));
            	getItemList(Name);
            	getEntityList(Name);
        	} catch (FileNotFoundException e) {
        		//plugin.log.severe("[PrivatChest] Could not load PrivatChest Config. File Not Found.");
        	} catch (Exception ex) {
        		ex.printStackTrace();
        		plugin.log.severe("[PrivatChest] Could not load PrivatChest Config.");
        	}
        }
	}
	
    private void saveWorldSettings(){
        FileOutputStream stream = null;
        File parent = new File(plugin.getDataFolder().getPath() + "/WorldSettings/world.yml").getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        for(String WorldName: WorldSettings.keySet()){
	        try {
	            stream = new FileOutputStream(new File(plugin.getDataFolder().getPath() + "/WorldSettings/" + WorldName + ".yml"));
	            OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8");
	            this.yamlWorldSettings.dump(WorldSettings.get(WorldName), writer);
	        } catch (IOException e) {
	        	plugin.log.severe("[PrivatChest] Could not save PrivatChest Config.");
	        } finally {
	            try {
	                if (stream != null) {
	                    stream.close();
	                }
	            } catch (IOException e) {}
	        }
	    }
    }

	private void loadGroupItemLists(){
        FileInputStream pFile;
        File dir = new File(plugin.getDataFolder().getPath() + "/GroupItemLists/");
        
        dir.mkdirs();
        String[] children = dir.list();
        if(children == null) return;
        
        for(String child : children){
        	if(child == null) continue;
        	File file = new File(plugin.getDataFolder().getPath() + "/GroupItemLists/" + child);
        	if(!child.endsWith(".yml")) continue;
        	String Name = child.substring(0, child.indexOf("."));
        	try {
            	pFile = new FileInputStream(file);
            	GroupItemLists.put(Name.toLowerCase(),(ArrayList<Long>)yamlGroupItemLists.load(new UnicodeReader(pFile)));
        	} catch (FileNotFoundException e) {
        		//plugin.log.severe("[PrivatChest] Could not load PrivatChest Config. File Not Found.");
        	} catch (Exception ex) {
        		ex.printStackTrace();
        		plugin.log.severe("[PrivatChest] Could not load PrivatChest GroupItemLists: "+child+".");
        	}
        }
	}
	
    private void saveGroupItemLists(){
        FileOutputStream stream = null;
        File parent = new File(plugin.getDataFolder().getPath() + "/GroupItemLists/world.yml").getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        for(String WorldName: GroupItemLists.keySet()){
	        try {
	            stream = new FileOutputStream(new File(plugin.getDataFolder().getPath() + "/GroupItemLists/" + WorldName + ".yml"));
	            OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8");
	            this.yamlGroupItemLists.dump(GroupItemLists.get(WorldName), writer);
	        } catch (IOException e) {
	        	plugin.log.severe("[PrivatChest] Could not save PrivatChest GroupItemLists: "+WorldName+".");
	        } finally {
	            try {
	                if (stream != null) {
	                    stream.close();
	                }
	            } catch (IOException e) {}
	        }
	    }
    }

	private void loadGroupEntityList(){
        FileInputStream pFile;
        File dir = new File(plugin.getDataFolder().getPath() + "/GroupEntityList/");
        
        dir.mkdirs();
        String[] children = dir.list();
        if(children == null) return;
        
        for(String child : children){
        	if(child == null) continue;
        	File file = new File(plugin.getDataFolder().getPath() + "/GroupEntityList/" + child);
        	if(!child.endsWith(".yml")) continue;
        	String Name = child.substring(0, child.indexOf("."));
        	try {
            	pFile = new FileInputStream(file);
            	GroupEntityList.put(Name.toLowerCase(),(ArrayList<String>)yamlGroupEntityList.load(new UnicodeReader(pFile)));
        	} catch (FileNotFoundException e) {
        		//plugin.log.severe("[PrivatChest] Could not load PrivatChest Config. File Not Found.");
        	} catch (Exception ex) {
        		ex.printStackTrace();
        		plugin.log.severe("[PrivatChest] Could not load PrivatChest GroupEntityList: "+child+".");
        	}
        }
	}
	
    private void saveGroupEntityList(){
        FileOutputStream stream = null;
        File parent = new File(plugin.getDataFolder().getPath() + "/GroupEntityList/world.yml").getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        for(String WorldName: GroupEntityList.keySet()){
	        try {
	            stream = new FileOutputStream(new File(plugin.getDataFolder().getPath() + "/GroupEntityList/" + WorldName + ".yml"));
	            OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8");
	            this.yamlGroupEntityList.dump(GroupEntityList.get(WorldName), writer);
	        } catch (IOException e) {
	        	plugin.log.severe("[PrivatChest] Could not save PrivatChest GroupEntityList: "+WorldName+".");
	        } finally {
	            try {
	                if (stream != null) {
	                    stream.close();
	                }
	            } catch (IOException e) {}
	        }
	    }
    }

	private void loadWorldTagList(){
        FileInputStream pFile;
        try {
           	pFile = new FileInputStream(new File(plugin.getDataFolder().getPath() + "/WorldTagList.yml"));
           	WorldTagList = (ArrayList<String>) yamlWorldTagList.load(new UnicodeReader(pFile));
        } catch (FileNotFoundException e) {
        	plugin.log.severe("[PrivatChest] Could not load PrivatChest WorldTagList. File Not Found.");
        } catch (Exception ex) {
        	ex.printStackTrace();
        	plugin.log.severe("[PrivatChest] Could not load PrivatChest WorldTagList.");
        }
	}
	
    private void saveWorldTagList(){
        FileOutputStream stream = null;
        File parent = new File(plugin.getDataFolder().getPath() + "/WorldTagList.yml").getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        try {
        	stream = new FileOutputStream(new File(plugin.getDataFolder().getPath() + "/WorldTagList.yml"));
	        OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8");
	        this.yamlWorldTagList.dump(WorldTagList, writer);
        } catch (IOException e) {
        	plugin.log.severe("[PrivatChest] Could not save PrivatChest WorldTagList.");
        } finally {
        	try {
        		if (stream != null) {
        			stream.close();
        		}
        	} catch (IOException e) {}
	    }
    }

	private void loadKnownItems(){
        FileInputStream pFile;
        try {
           	pFile = new FileInputStream(new File(plugin.getDataFolder().getPath() + "/KnownItems.yml"));
           	KnownItems = (ArrayList<Integer>) yamlKnownItems.load(new UnicodeReader(pFile));
        } catch (FileNotFoundException e) {
        	plugin.log.severe("[PrivatChest] Could not load PrivatChest WorldTagList. File Not Found.");
        } catch (Exception ex) {
        	ex.printStackTrace();
        	plugin.log.severe("[PrivatChest] Could not load PrivatChest's KnownItems.");
        }
	}
	
    private void saveKnownItems(){
        FileOutputStream stream = null;
        File parent = new File(plugin.getDataFolder().getPath() + "/KnownItems.yml").getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        try {
        	stream = new FileOutputStream(new File(plugin.getDataFolder().getPath() + "/KnownItems.yml"));
	        OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8");
	        this.yamlKnownItems.dump(KnownItems, writer);
        } catch (IOException e) {
        	plugin.log.severe("[PrivatChest] Could not save PrivatChest's KnownItems.");
        } finally {
        	try {
        		if (stream != null) {
        			stream.close();
        		}
        	} catch (IOException e) {}
	    }
    }

	private void loadKnownEntities(){
        FileInputStream pFile;
        try {
           	pFile = new FileInputStream(new File(plugin.getDataFolder().getPath() + "/KnownEntities.yml"));
           	KnownEntities = (ArrayList<String>) yamlKnownEntities.load(new UnicodeReader(pFile));
        } catch (FileNotFoundException e) {
        	plugin.log.severe("[PrivatChest] Could not load PrivatChest WorldTagList. File Not Found.");
        } catch (Exception ex) {
        	ex.printStackTrace();
        	plugin.log.severe("[PrivatChest] Could not load PrivatChest's KnownEntities.");
        }
	}
	
    private void saveKnownEntities(){
        FileOutputStream stream = null;
        File parent = new File(plugin.getDataFolder().getPath() + "/KnownEntities.yml").getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        try {
        	stream = new FileOutputStream(new File(plugin.getDataFolder().getPath() + "/KnownEntities.yml"));
	        OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8");
	        this.yamlKnownEntities.dump(KnownEntities, writer);
        } catch (IOException e) {
        	plugin.log.severe("[PrivatChest] Could not save PrivatChest's KnownEntities.");
        } finally {
        	try {
        		if (stream != null) {
        			stream.close();
        		}
        	} catch (IOException e) {}
	    }
    }
    
}
