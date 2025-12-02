package me.matl114.matlib.utils.reflect.internel;

import com.google.common.collect.ImmutableMap;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.ByteCodeUtils;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleObfManagerImpl implements SimpleObfManager {
    final Class<?> obfClass ;
    final ClassMapperHelper classMapperHelper ;
    final Map<String, ?> mappingsByObfName;
    final Map<String, ?> mappingsByMojangName;
    static final String originCraftbukkitPackageName = "org.bukkit.craftbukkit";
    final String craftbukkitPackageName;
    //map from higher version to lower version
    //we all use high version mojang name here
    //
    private static Map<String,String> build(String... values){
        Iterator<String> value = Arrays.stream(values).iterator();
        ImmutableMap.Builder<String,String> builder = ImmutableMap.builder();
        while (value.hasNext()){
             builder.put(value.next(), value.next());
        }
        return builder.build();
    }
    final Map<String,String> mojangVersionedPath = build(
        "net.minecraft.world.level.chunk.status.ChunkStatus", "net.minecraft.world.level.chunk.ChunkStatus",
        "net.minecraft.network.protocol.login.ClientboundLoginFinishedPacket", "net.minecraft.network.protocol.login.ClientboundGameProfilePacket",
        "net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket", "net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket",
        "net.minecraft.network.protocol.game.ServerboundChatCommandSignedPacket","net.minecraft.network.protocol.game.ServerboundChatCommandPacket",
        "net.minecraft.network.chat.contents.PlainTextContents$LiteralContents","net.minecraft.network.chat.contents.LiteralContents",
        "net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket","net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket",
        "net.minecraft.network.protocol.common.ServerboundKeepAlivePacket","net.minecraft.network.protocol.game.ServerboundKeepAlivePacket",
        "net.minecraft.network.protocol.common.ServerboundPongPacket","net.minecraft.network.protocol.game.ServerboundPongPacket",
        "net.minecraft.network.protocol.common.ServerboundResourcePackPacket","net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket",
        "net.minecraft.network.protocol.common.ServerboundClientInformationPacket","net.minecraft.network.protocol.game.ServerboundClientInformationPacket",
        "net.minecraft.world.item.equipment.trim.ArmorTrim","net.minecraft.world.item.armortrim.ArmorTrim",
        "net.minecraft.network.chat.contents.data.DataSource", "net.minecraft.network.chat.contents.DataSource"
    );
    final Map<String, String> mojangVersionedPathMapper;
    final Map<String, String> mojangVersionedPathMapperInverse;
    SimpleObfManagerImpl(){
        String[] path= Bukkit.getServer().getClass().getPackage().getName().split("\\.");
        if(path.length >= 4){
            craftbukkitPackageName = Bukkit.getServer().getClass().getPackage().getName();
        }else {
            //upper than 1_20_v4, paper no longer relocation craftbukkit package , at least through reflection
            craftbukkitPackageName = originCraftbukkitPackageName;
        }
        try{
            obfClass = Class.forName("io.papermc.paper.util.ObfHelper");
            classMapperHelper = new ClassMapperHelperImpl();
            Object obfIns = obfClass.getEnumConstants()[0];
            var f1 = obfClass.getDeclaredField("mappingsByObfName");
            f1.setAccessible(true);
            mappingsByObfName = (Map<String, ?>) f1.get(obfIns);
            var f2 = obfClass.getDeclaredField("mappingsByMojangName");
            f2.setAccessible(true);
            mappingsByMojangName = (Map<String, ?>) f2.get(obfIns);
            ClassMapperHelperImpl classMapperHelper1 = (ClassMapperHelperImpl) classMapperHelper;
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
        Map<String,String> pathValidation = new HashMap<>();
        b(pathValidation);
        mojangVersionedPathMapper = Collections.unmodifiableMap(pathValidation);
        mojangVersionedPathMapperInverse = Collections.unmodifiableMap(
            pathValidation.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(entry-> entry.getValue(), entry->entry.getKey(), (oldvalue, newValue)->oldvalue))
        );
    }
    private void b(Map<String,String> map){
        for (var pathPair :mojangVersionedPath.entrySet()){
            Object reobfName0 = this.mappingsByMojangName.get(pathPair.getKey());
            String realPathKey = reobfName0 == null ? pathPair.getKey() : classMapperHelper.obfNameGetter(reobfName0);
            try{
                Class.forName(realPathKey);
                //if key occurs, we should not register this
                continue;
            }catch (Throwable e){

            }
            Object reobfName = this.mappingsByMojangName.get(pathPair.getValue());
            String realPath = reobfName == null ? pathPair.getValue() : classMapperHelper.obfNameGetter(reobfName);
            try{
                Class.forName(realPath);
            }catch (Throwable e){
               // Debug.logger("Error pair",pathPair);
                continue;
            }
           // Debug.logger("Accept pair",pathPair);
            //valid versioned path
            map.put(pathPair.getKey(), pathPair.getValue());
        }
    }

    public String demapCraftBukkitAndMojangVersionedPath(String currentName){
        if(currentName.startsWith(craftbukkitPackageName)){
            return currentName.replaceFirst(craftbukkitPackageName, originCraftbukkitPackageName);
        }
        return mojangVersionedPathMapperInverse.getOrDefault(currentName, currentName);
    }
    public String remapCraftBukkitAndMojangVersionedPath(String currentName){
        if(currentName.startsWith(originCraftbukkitPackageName)){
            return currentName.replaceFirst(originCraftbukkitPackageName, craftbukkitPackageName);
        }

        return mojangVersionedPathMapper.getOrDefault(currentName, currentName);
    }

    @Override
    public String deobfClassName(String currentName) {
        if (this.mappingsByObfName == null) {
            return demapCraftBukkitAndMojangVersionedPath(currentName);
        }

        final Object map = this.mappingsByObfName.get(currentName);
        if (map == null) {
            return demapCraftBukkitAndMojangVersionedPath(currentName);
        }

        return demapCraftBukkitAndMojangVersionedPath( classMapperHelper.mojangNameGetter(map));
    }

    @Override
    public String reobfClassName(String mojangName) {
        if (this.mappingsByMojangName == null) {
            return remapCraftBukkitAndMojangVersionedPath(mojangName);
        }
        String remappedMojangName = remapCraftBukkitAndMojangVersionedPath(mojangName);
        final Object map = this.mappingsByMojangName.get(remappedMojangName);
        if (map == null) {
            return remappedMojangName;
        }

        return classMapperHelper.obfNameGetter(map);
    }

    @Override
    public String deobfMethodInClass(String reobfClassName, String methodDescriptor) {
        String methodName = ByteCodeUtils.parseMethodNameFromDescriptor(methodDescriptor);
        if (this.mappingsByMojangName == null) {
            return methodName;
        }
        //using versioned path of mojang name
        final Object map = this.mappingsByMojangName.get(remapCraftBukkitAndMojangVersionedPath( reobfClassName) );
        if(map == null){
            //no obf,
            return methodName;
        }
        final Map<String,String> methodMapping = classMapperHelper.methodsByObf(map);
        return methodMapping.getOrDefault(methodDescriptor, methodName);
    }

}
