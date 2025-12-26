package me.matl114.matlib.utils.entity.display.Implementation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.PairList;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.utils.ThreadUtils;
import me.matl114.matlib.utils.entity.display.Joint;
import me.matl114.matlib.utils.entity.display.LinkBody;
import me.matl114.matlib.utils.entity.groups.implement.FixedEntityGroup;
import me.matl114.matlib.utils.persistentDataContainer.PdcUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Marker;
import org.jetbrains.annotations.NotNull;

public class LinkDisplayGroup extends DisplayGroup implements LinkBody {
    public LinkDisplayGroup(String namespace, @NotNull Marker entityParent, String linkId) {
        super(namespace, entityParent);
        this.childLinkage = new LinkedHashMap<>();
        this.linkId = linkId;
        this.linkageRecord = new NamespacedKey(namespace, linkId);
        this.childLinkUUIDMap = new LinkedHashMap<>();
    }

    final String linkId;
    final NamespacedKey linkageRecord;
    LinkBody parentLink = null;
    final Map<String, Pair<Joint, LinkBody>> childLinkage;
    final Map<UUID, String> childLinkUUIDMap;

    @Override
    public LinkBody getParentLink() {
        return parentLink;
    }

    public void killGroup() {
        super.killGroup();
        childLinkage.values().forEach(p -> p.getB().killGroup());
    }

    public void killGroupAsync() {
        ThreadUtils.executeSync(this::killGroup);
    }

    private List<String> genChildLinkList() {
        return childLinkUUIDMap.entrySet().stream()
                .map(entry -> entry.getKey().toString() + FixedEntityGroup.PLACEHOLDER + entry.getValue())
                .toList();
    }

    private static PairList<String, UUID> genChildFromList(List<String> childs) {
        PairList<String, UUID> list = new PairList<>(childs.size() + 1);
        for (String it : childs) {
            try {
                String[] args = it.split(PLACEHOLDER);
                UUID uuid = UUID.fromString(args[0]);
                if (uuid != null) {
                    list.put(args[1], uuid);
                }
            } catch (Throwable e) {
            }
        }
        return list;
    }

    public LinkDisplayGroup addChildLink(Joint joint, LinkDisplayGroup child) {
        childLinkage.put(child.getId(), new Pair<>(joint, child));
        // todo redesign how to record relations
        // childLinkUUIDMap.put(child.getParent().getUuid(), child.getId());
        child.parentLink = this;
        var parent = this.getParent();
        if (parent != null && isDataRecordEnabled()) {
            parent.getOrLoadEntity().ifPresent(e -> {
                PdcUtils.setOrRemove(e.getPersistentDataContainer(), linkageRecord, listPdcType, genChildLinkList());
            });
        }
        return this;
    }
    // todo should need a link builder type to deal with this building staff
    //    public LinkDisplayGroup tryBuild(Map<String,Joint> connection){
    //        //deserializing data in parent, build links
    //        var re = this.getParent().forceGetOrLoadEntity().getPersistentDataContainer();
    //        if(re.has(linkageRecord,listPdcType)){
    //            var sons = genChildFromList(PdcUtils.getOrDefault(re,linkageRecord,listPdcType,List.of()));
    //            for(var it:sons){
    //                EntityRecord<?> record = FixedEntityRecord.ofFixedEntity(it.getB());
    //                var joint = connection.get(it.getA());
    //                childLinkage.put(it.getA(), new Pair<>(joint, ));
    //                childLinkUUIDMap.put(child.getParent().getUuid(), child.getId());
    //                child.parentLink = this;
    //                if(record !=null){
    //                    putChildInternal(it.getA(), (EntityRecord<T>) record);
    //                }
    //            }
    //        }
    //        return this;
    //    }

    @Override
    public Iterable<Pair<Joint, LinkBody>> getChildJoints() {
        return childLinkage.values();
    }

    @Override
    public String getId() {
        return linkId;
    }
}
