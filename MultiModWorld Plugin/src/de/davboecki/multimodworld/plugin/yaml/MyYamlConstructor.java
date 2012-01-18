package de.davboecki.multimodworld.plugin.yaml;

import java.util.HashMap;

import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;

import de.davboecki.multimodworld.plugin.RoomLocation;
import de.davboecki.multimodworld.plugin.settings.ExchangeWorldSetting;
import de.davboecki.multimodworld.plugin.settings.WorldSetting;


public class MyYamlConstructor extends Constructor {
    private HashMap<String,Class<?>> classMap = new HashMap<String,Class<?>>();

       public MyYamlConstructor(Class<? extends Object> theRoot) {
           super( theRoot );
           classMap.put( RoomLocation.class.getName(), RoomLocation.class );
           classMap.put( ExchangeWorldSetting.class.getName(), ExchangeWorldSetting.class );
           classMap.put( WorldSetting.class.getName(), WorldSetting.class );
       }


       /*
        * This is a modified version of the Constructor. Rather than using a class loader to
        * get external classes, they are already predefined above. This approach works similar to
        * the typeTags structure in the original constructor, except that class information is
        * pre-populated during initialization rather than runtime.
        *
        * @see org.yaml.snakeyaml.constructor.Constructor#getClassForNode(org.yaml.snakeyaml.nodes.Node)
        */
        protected Class<?> getClassForNode(Node node) {
            String name = node.getTag().getClassName();
            Class<?> cl = classMap.get( name );
            if ( cl == null )
                throw new YAMLException( "Class not found: " + name );
            else
                return cl;
        }
}