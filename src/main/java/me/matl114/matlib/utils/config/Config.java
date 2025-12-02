package me.matl114.matlib.utils.config;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.matl114.matlib.utils.Debug;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public abstract class Config {
    //for load when startup

    @Getter
    @Setter
    private boolean updateDataWhenModified = true;
    @Getter
    @Setter
    private boolean saveDataWhenModified = false;
    HashMap<String,Object> data;
//    @Getter
//    Logger logger;
    RootNode root;
    //todo keep comment blocks, use model instead
    public Config(HashMap<String,Object> data) {
        this.data = data;
        this.root= new RootNode();

    }
    //get the HashMap s.t. map.get(path[path.length-1])=the needed Object
    private HashMap<String, Object> getOrCreateParentPathInRawData(String... targetPath){
        HashMap<String,Object> value=this.data;

        for(int i=0;i<targetPath.length-1;i++){
            value=(HashMap<String, Object>)(Object) value.compute(targetPath[i],(k, ob)->{
                if(! (ob instanceof HashMap<?,?>)){
                    return new LinkedHashMap<>();
                }
                return ob;
            });
        }

        return value;
      // value.put(path[path.length-1],val);
    }
    protected void updateRawData(){
        if(!this.isUpdateDataWhenModified()){
            this.data= dumpToMap(this.root);
        }
    }
    private HashMap<String,Object> dumpToMap(InnerNode node){
        Collection<String> keys=node.getKeys();
        HashMap<String,Object> value=new LinkedHashMap<>();
        for(String key:keys){
            Node childe=node.getChild(key);
            if(childe instanceof InnerNode inner){
                value.put(key, dumpToMap(inner));
            }else if(childe instanceof LeafNode inner){
                value.put(key,inner.data.get());
            }
        }
        return value;
    }
    public static Map<String, Object> flattenPathMap(@Nonnull Map<String, Object> data){
        Map<String ,Object > result = new LinkedHashMap<>();
        for (var entry: data.entrySet()){
            String key = entry.getKey();
            Map<String, Object> currentMap = result;
            String[] keySplit = entry.getKey().split("\\.");
            for (int i=0; i< keySplit.length - 1; ++i){
                String key0 = keySplit[i];
                currentMap = (Map<String, Object>) currentMap.compute(key0, (k,v)->{
                    if(v instanceof Map map0){
                        return map0;
                    }
                    return new LinkedHashMap<>();
                });
            }
            currentMap.put(keySplit[keySplit.length-1], entry.getValue());
        }
        return result;
    }

    //for save
    protected final HashMap<String,Object> getRawData(){
        return data;
    }
    public Map<String,Object> getData(){
        updateRawData();
        return Collections.unmodifiableMap(this.data);
    }
    //配置文件节点
    protected abstract class Node{
        //abstract Collection<String> getKeys();
        @Getter
        @Setter(AccessLevel.PROTECTED)
        private Node parent;
        @Getter
        @Setter(AccessLevel.PROTECTED)
        private String pkey;
        @Getter
        @Setter(AccessLevel.PROTECTED)
        private boolean valid;
        protected NodeReference<?> data;
        public Node(){

        }
        public void markDirty(){
            if(this.isValid()){
                Config.this.makeDirtyInternal(this);
            }
        }

        public void setValid(boolean flag){
            this.valid = flag;
            if(!flag && this instanceof InnerNode innerNode){
                for(String key : innerNode.getKeys()){
                    Node child = innerNode.getChild(key);
                    child.setValid(false);
                }
            }
        }
    }

    protected class InnerNode extends Node {
        public InnerNode(){
            super();
            this.data = NodeReference.of(children, (w)->this.markDirty(), this);
        }
        private final HashMap<String,Node> children = new LinkedHashMap<>();

        /**
         * no copy
         * @return
         */
        public Collection<String> getKeys(){
            return  children.keySet();
        }
        public Collection<String> getKeysCopy(){
            return  new LinkedHashSet<>(getKeys());
        }
        public Node getChild(String key) {
            return children.get(key);
        }
        public <T extends Node> T addOrGetChild(String key, Class<T> clazz, Supplier<T> newNode){
             if(children.containsKey(key)){
                 Node node = children.get(key);
                 if(clazz.isInstance(node)){
                     return clazz.cast(node);
                 }else {
                     //Debug.logger("Error Node Type while expanding", key);
                     removeChild(key);
                 }
             }
             T value = newNode.get();
             addChild(key, value);
             return value;
        }
        public void addChild(String key, Node child) {
            child.setParent(this);
            child.setPkey(key);
            child.setValid(true);
            if(children.containsKey(key)){
                removeChild(key);
            }
            children.put(key, child);
            markDirty();
        }
        public Node removeChild(String key) {
            Node node= children.remove(key) ;
            if(node != null){
                node.setParent(null);
                node.setValid(false);
            }
            markDirty();
            return node;
        }
        public void clearChild(){
            this.children.clear();
            markDirty();
        }

        protected void loadInternal(Map<String, Object> value){
            for(Map.Entry<String,Object> entry: value.entrySet()){
                String key = entry.getKey();
                Object val = entry.getValue();
                String[] keySplit = entry.getKey().split("\\.");
                InnerNode node = this   ;
                //fixme: should be common at any InnerNode
                //fixme: loading node in other places should use this method
                for (int i=0; i< keySplit.length - 1; ++i){
                    String key0 = keySplit[i];
                    node = node.addOrGetChild(key0, InnerNode.class, InnerNode::new);
                }
                if(val instanceof Map map){
                    // Handle nested maps properly
                    InnerNode newNode = node.addOrGetChild(keySplit[keySplit.length-1], InnerNode.class, InnerNode::new);
                    newNode.loadInternal((Map<String, Object>) map);
                } else {
                    // Handle leaf values
                    LeafNode<?> leafNode = new LeafNode<>(val);
                    node.addChild(keySplit[keySplit.length-1], leafNode);
                }
            }
        }
        //
        protected void reloadNodeInternal(Map<String,Object> flattenPath){
            // Track existing nodes to preserve references
            Map<String, Node> existingNodes = new HashMap<>(this.children);

            // Process new data
            for(Map.Entry<String,Object> entry: flattenPath.entrySet()){
                String key = entry.getKey();
                Object value = entry.getValue();
                Node existingNode = existingNodes.remove(key);
                if(value instanceof Map map){
                    // Handle nested maps
                    if(existingNode instanceof InnerNode existingInner){
                        // Reuse existing inner node
                        existingInner.reloadNodeInternal((Map<String, Object>) map);
                    } else {
                        // Create new inner node
                        InnerNode newInner = new InnerNode();
                        this.addChild(key, newInner);
                        newInner.loadInternal((Map<String, Object>) map);
                    }
                } else {
                    // Handle leaf values
                    if(existingNode instanceof LeafNode<?> existingLeaf){
                        // Update existing leaf value without replacing the NodeReference
                        NodeReference<Object> ref = (NodeReference<Object>) existingLeaf.data;
                        ref.set(value);
                    } else {
                        // Create new leaf
                        LeafNode<?> newLeaf = new LeafNode<>(value);
                        this.addChild(key, newLeaf);
                    }
                }

                // Remove from existing nodes to track what's been processed
                // existingNodes.remove(key);
            }

            // Remove nodes that no longer exist in the new data
            for(String keyToRemove : existingNodes.keySet()){
                this.removeChild(keyToRemove);
            }
        }
    }
    protected final class RootNode extends InnerNode{
        public RootNode(){
            super();
            Preconditions.checkArgument(Config.this.root == null||!Config.this.root.isValid(),"Node Error in Config! this Config Tree already has a Root");
            Config.this.root = this;
            HashMap<String,Object> loaded= Config.this.data;
            this.loadInternal(loaded);
            setPkey(null);
            setParent(null);
            setValid(true);
        }

        public void reloadInternal(Map<String ,Object> map0){
            //flatten map
            Map<String, Object> flattenMap = flattenPathMap(map0);
            this.reloadNodeInternal(flattenMap);
        }
    }
    @Getter
    protected class LeafNode<W> extends Node {
        public LeafNode(W value) {
            super();

            this.data= NodeReference.of(value,(w)->{
                this.markDirty();
            }, this);
        }
    }
    //todo  left as not done
    private void makeDirtyInternal(Node node){
        if(isUpdateDataWhenModified()){
            Node modifiedNode=node;
            List<String> modifiedPath=new ArrayList<>();
            while(node.getPkey() != null&&node.getParent() != null){
                modifiedPath.add(node.getPkey());
                node=node.getParent();
            }
            if(node==root){
                if(root.isValid()){
                    //如果可以回溯到root 说明没断
                    String[] path=modifiedPath.toArray(String[]::new);
                    HashMap<String,Object> parentNode= getOrCreateParentPathInRawData(path);
                    if(modifiedNode instanceof LeafNode<?> leaf){
                        parentNode.put(path[path.length-1], leaf.data.get());
                    }else if(modifiedNode instanceof InnerNode inner){
                        var iter=parentNode.keySet().iterator();
                        HashSet<String> newPaths=new LinkedHashSet<>( inner.getKeys());
                        //删除的key
                        while(iter.hasNext()){
                            String pkey=iter.next();
                            if(!newPaths.contains(pkey)){
                                iter.remove();
                            }else {
                                newPaths.remove(pkey);
                            }
                        }
                        //新出现的key
                        for(String left:newPaths){
                            Node child=inner.getChild(left);
                            if(child instanceof InnerNode leaf){
                                parentNode.put(left, dumpToMap(leaf));
                            }else if(child instanceof LeafNode<?> leaf){
                                parentNode.put(left, leaf.data.get());
                            }
                        }
                    }
                }else{
                    return ;
                }
            }else{
                //并不是这个root下的节点,标记为已经废弃的节点,将不会继续markDirty
                modifiedNode.setValid(false);
                return;
            }
        }
        if(isSaveDataWhenModified()){
            save();
        }
    }
    @Nullable
    private InnerNode getNode(String[] path){
        InnerNode parent=this.root;
        for (int i=0; i<path.length; ++i){
            Node nextNode=parent.getChild(path[i]);
            if(nextNode instanceof InnerNode innerNode){
                parent=innerNode;
            }else {
                return null;
            }
        }
        return parent;
    }
    @Nonnull
    private InnerNode getOrCreateNode(String[] path){
        InnerNode parent=this.root;
        for (int i=0;i<path.length;++i){
            Node nextNode=parent.getChild(path[i]);
            if(nextNode instanceof InnerNode innerNode){
                parent=innerNode;
            }else {
                var re=new InnerNode();
                parent.addChild(path[i],re);
                parent=re;
            }
        }
        return parent;
    }
    @Nullable
    private InnerNode getParentNode(String[] path){
        return getNode(Arrays.copyOfRange(path,0,path.length-1));
    }
    @Nonnull
    private InnerNode getOrCreateParentNode(String[] path){
        return getOrCreateNode(Arrays.copyOfRange(path,0,path.length-1));
    }
    @Nullable
    public <T> T getValue(String path){
        return (T) getLeaf(path).get();
    }
    @Nullable
    public <T> T getValue(String[] path){
        return (T)getLeaf(path).get();
    }

    @Nonnull
    public <T> NodeReference<T> getLeaf(String path){
        return getLeaf(path.split("\\."));
    }
    @Nonnull
    public <T> NodeReference<T> getLeaf(String[] path){
        InnerNode parentNode= getParentNode(path);
        if(parentNode != null && parentNode.getChild(path[path.length-1]) instanceof LeafNode<?> leaf){
            return (NodeReference<T>) leaf.data;
        }else {
            return (NodeReference<T>) NodeReference.NULL;
        }
    }
    @Nonnull
    public <T> NodeReference<T> getOrCreateLeaf(String path){
        return getOrCreateLeaf(path.split("\\."));
    }
    @Nonnull
    public <T> NodeReference<T> getOrCreateLeaf(String[] path){
        InnerNode parentNode= getOrCreateParentNode(path);
        String leafKey = path[path.length-1];
        
        Node existingNode = parentNode.getChild(leafKey);
        if(existingNode instanceof LeafNode<?> leaf){
            return (NodeReference<T>) leaf.data;
        } else {
            // Create new leaf with default value
            LeafNode<T> newLeaf = new LeafNode<>(null);
            parentNode.addChild(leafKey, newLeaf);
            return (NodeReference<T>) newLeaf.data;
        }
    }
    @Nonnull
    public  <T> NodeReference<T> getLeafOrSetDefault(@Nullable T value, String path){
        return getLeafOrSetDefault(value, path.split("\\."));
    }

    /**
     * use value.getClass check the recorded value
     * @param value
     * @param path
     * @return
     * @param <T>
     */
    @Nonnull
    public  <T> NodeReference<T> getLeafOrSetDefault(@Nullable T value, String[] path){
        InnerNode parentNode= getOrCreateParentNode(path);
        if(parentNode.getChild(path[path.length-1]) instanceof LeafNode<?> leaf){
            var re=leaf.data.get();
            if(value==null || value.getClass().isInstance(re)){
                return (NodeReference<T>) leaf.data;
            }
        }
        LeafNode<T> newNodeToReplace=new LeafNode<>(value);
        parentNode.addChild(path[path.length-1],newNodeToReplace);
        return (NodeReference<T>) newNodeToReplace.data;
    }

    /**
     * set the config's default value(null->value)
     * @param defaultVal
     * @param path
     * @return
     * @param <T>
     * @param <W>
     */
    public <T extends Object,W extends Config> W defaultValue(@Nullable T defaultVal,String... path){
        getLeafOrSetDefault(defaultVal,path);
        return (W)this;
    }
    public <T extends Object> void setValue(T value,String... path){
        InnerNode parentNode= getOrCreateParentNode(path);
        if(parentNode.getChild(path[path.length-1]) instanceof LeafNode leaf){
            ((NodeReference<T>)leaf.data).set(value);
            return;
        }
        parentNode.addChild(path[path.length-1],new LeafNode<>(value));
    }
    public final void createLeaf(Object value,String... path){
        getOrCreateParentNode(path).addChild(path[path.length-1],new LeafNode<>(value));
    }
    public final void clear(){
        this.root.clearChild();
    }


    protected final void reloadInternal(HashMap<String,Object> data){
        // Store the old root to preserve references during reload
        this.root.reloadInternal(data);
    }


    public final  <T extends Config> T save(){
        if(this.getFile()!=null){
            save(this.getFile());
        }
        return (T)this;
    }
    public abstract File getFile();
    public abstract void save(File file);

//    /**
//     * cast from hashmap to internal config tree: will cast non-map value to leaf-node value
//     * @param val
//     * @return
//     */
//    public abstract Object castWhenLoad(Object val);
//
//    /**
//     * cast from internal config tree to hashmap: will cast leaf node value to this
//     * @param val
//     * @return
//     */
//    public abstract Object castWhenDump(Object val);
//



    public abstract void reload();

    public boolean contains(@Nonnull String... path) {
        return getLeaf(path)!=null;
    }
    public static boolean createFile(File file) {
        try {
            return file.createNewFile();
        } catch (IOException var2) {
            IOException e = var2;
            Debug.severe("Exception while creating a Config file",e.getMessage());
            //this.logger.log(Level.SEVERE, "Exception while creating a Config file", e);
            return false;
        }
    }
    public final Collection<String> getKeys() {
        return this.root.getKeys();
    }
    @Nonnull
    public final Collection<String> getKeys(@Nonnull String... path) {
        return getOrCreateNode(path).getKeys();
    }
    public final Set<String> getPaths(){
        HashSet<String> paths=new LinkedHashSet<>();
        for(String val:this.root.getKeys()){
            if(this.root.getChild(val) instanceof InnerNode map2){
                HashSet<String> p=getPaths(map2,val);
                paths.addAll(p);
            }else{
                paths.add(val);
            }
        }
        return paths;
    }
    private static HashSet<String> getPaths(InnerNode map,String parent){
        HashSet<String> paths=new LinkedHashSet<>();
        for(String val:map.getKeys()){
            if(map.getChild(val) instanceof InnerNode map2){
                HashSet<String> p=getPaths(map2,val);
                paths.addAll(p.stream().map(str->(String)parent+"."+str).collect(Collectors.toSet()));
            }else{
                paths.add(parent+"."+val);
            }
        }
        return paths;
    }
    public static String[] cutToPath(String rawPath){
        return rawPath.split("\\.");
    }
    public static Map<String,Object> deepCopyTree(Map<String,Object> origin){
        //todo left as not completed
        return null;
    }

    // /**
    //  * Get or create a leaf node with better reference management.
    //  * This ensures that the same path always returns the same NodeReference.
    //  */
    // @Nonnull
    // public <T> NodeReference<T> getOrCreateLeafWithReference(String[] path){
    //     InnerNode parentNode = getOrCreateParentNode(path);
    //     String leafKey = path[path.length-1];
        
    //     Node existingNode = parentNode.getChild(leafKey);
    //     if(existingNode instanceof LeafNode<?> leaf){
    //         return (NodeReference<T>) leaf.data;
    //     } else {
    //         // Create new leaf with null value
    //         LeafNode<T> newLeaf = new LeafNode<>(null);
    //         parentNode.addChild(leafKey, newLeaf);
    //         return (NodeReference<T>) newLeaf.data;
    //     }
    // }
    
    
    /**
     * Check if a NodeReference is still valid (not invalidated by reloading)
     */
    public boolean isReferenceValid(NodeReference<?> reference){
        if(reference == null || reference == NodeReference.NULL){
            return false;
        }
        // Check if the node is still valid
        if(reference.nodeRef != null){
            return reference.nodeRef.isValid();
        }
        return true;
    }
}
