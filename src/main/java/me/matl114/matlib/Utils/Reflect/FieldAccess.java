package me.matl114.matlib.Utils.Reflect;

import com.google.common.base.Preconditions;
import me.matl114.matlib.Algorithms.DataStructures.Frames.InitializeProvider;
import me.matl114.matlib.Algorithms.DataStructures.Struct.Pair;
import me.matl114.matlib.Utils.Debug;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FieldAccess {
    private boolean printError = Debug.isDebugMod();
    private boolean failInitialization=false;
    private Function<Object, Field> lazilyInitializationFunction;
    private Field field;
    private boolean failHandle=false;
    private VarHandle handle;
    private boolean isStatic=false;
    private boolean isFinal = false;
    private boolean isPublic = false;
    private boolean isPrivate = false;
    private boolean createSnapshot = true;
    private static HashMap<Class<?>, com.esotericsoftware.reflectasm.FieldAccess> cachedAccess = new HashMap<>();
    public static com.esotericsoftware.reflectasm.FieldAccess getOrCreateAccess(Class<?> targetClass){
        return cachedAccess.computeIfAbsent(targetClass, (clz)->Debug.interceptAllOutputs(()-> com.esotericsoftware.reflectasm.FieldAccess.get(clz),(output)->{
            if (output !=null && !output.isEmpty()){
                Debug.warn("Console output is intercepted:",output);
                Debug.warn("It is not a BUG and you can ignore it");
            }
        }))
                ;
    }
    private com.esotericsoftware.reflectasm.FieldAccess fastAccessInternal;
    private int fastAccessIndex;
    private boolean failPublicAccess=true;
    private Class<?> definedType=null;
    private static final boolean useHandle=true;
    private FieldGetter getter = null;
    private FieldSetter setter = null;
    public static FieldAccess ofName(String fieldName){
        return new FieldAccess((obj)->{
            var result=ReflectUtils.getFieldsRecursively(obj.getClass(),fieldName);
            return result==null?null:result.getA();
        });
    }
    public static FieldAccess of(Field field){
        return new FieldAccess((obj)->field);
    }
    public static FieldAccess ofName(Class<?> clazz,String fieldName){
        return new FieldAccess((obj)->{
            var result=ReflectUtils.getFieldsRecursively(clazz,fieldName);
            return result==null?null:result.getA();
        });
    }
    private static final FieldAccess FAIL= new InitializeProvider<>(()->{
        FieldAccess access = new FieldAccess(null);
        access.failInitialization = true;
        return access;
    }).v();
    public static FieldAccess ofFailure(){
        return FAIL;
    }
    public FieldAccess(Function<Object, Field> initFunction) {
        this.lazilyInitializationFunction =initFunction;
    }
    public FieldAccess printError(boolean printError) {
        this.printError = printError;
        return this;
    }
    private Field getFieldInternal(Object obj) throws Throwable {
        Field field=lazilyInitializationFunction.apply(obj);
        Preconditions.checkArgument(field!=null,"FieldAccess init field failed: field is null! using argument: "+(obj==null?"null":obj.toString()));
        field.setAccessible(true);
        return field;
    }
    public FieldAccess init(Object obj){
        if(this.field==null&&!failInitialization){
            try{
                this.field=getFieldInternal(obj);
                int modifiers = field.getModifiers();
                this.isFinal=Modifier.isFinal(modifiers);
                this.isStatic= Modifier.isStatic(modifiers);
                if(createSnapshot){
                    this.isPublic = Modifier.isPublic(modifiers);
                    this.isPrivate = Modifier.isPrivate(modifiers);
                    this.definedType=this.field.getType();
                    //only the field who has full access can create fast Access throw FieldAccess
                    if(!isStatic && isPublic){
                        initFastAccess();
                    }else {
                        this.failPublicAccess = true;
                    }
                    try{
                        this.handle=MethodHandles.privateLookupIn(this.field.getDeclaringClass(),MethodHandles.lookup()).unreflectVarHandle(this.field);
                        this.failHandle=false;
                    }catch(IllegalAccessException e){
                        this.failHandle=true;
                        if(printError){
                            Debug.logger("Failed to create field handle for Field :",field);
                            e.printStackTrace();
                        }
                    }

                }
                this.getter = getterInternal();
                this.setter = setterInternal();
            }catch (Throwable e){
                failInitialization=true;
                if(printError){
                    e.printStackTrace();
                }
            }
        }
        return this;
    }
    private void initFastAccess(){
        try{
            this.fastAccessInternal = getOrCreateAccess(field.getDeclaringClass());
            this.fastAccessIndex = this.fastAccessInternal.getIndex(this.field);
            this.failPublicAccess = !this.isPublic;
        }catch (Throwable e){
            this.failPublicAccess = true;
            if (printError){
                Debug.logger("Failed to create fast Access for Field :",field);
                Debug.logger(e);
            }
        }
    }
    public Field getFieldOrDefault(Supplier<Field> defa){
        init(null);
        return failInitialization?defa.get():field;
    }
    public Field finalizeFieldOrDefault(Object initializeObject,Supplier<Field> defa){
        init(initializeObject);
        return failInitialization?defa.get():field;
    }
    public VarHandle getVarHandleOrDefault(Supplier<VarHandle> defa){
        init(null);
        return failHandle?defa.get():handle;
    }
    public VarHandle finalizeHandleOrDefault(Object initializeObject,Supplier<VarHandle> defa){
        init(initializeObject);
        return failHandle?defa.get():handle;
    }
    public Pair<com.esotericsoftware.reflectasm.FieldAccess,Integer> getReflectAsm(){
        return getReflectAsm(null);
    }
    public Pair<com.esotericsoftware.reflectasm.FieldAccess,Integer> getReflectAsm(Object initializeObject){
        init(initializeObject);
        Preconditions.checkArgument(!isStatic&&!isPrivate,"Private and Static field can not be accessed from FieldAccess");
        if(!isPublic){
            //only public create automatically
            initFastAccess();
        }
        return Pair.of(this.fastAccessInternal,this.fastAccessIndex);
    }

    public FieldAccess initWithNull(){
        init(null);
        return this;
    }
    private Object getInternal(Object obj) throws Throwable {
//        if(useHandle&& !failHandle){
//            if(isStatic){
//                return this.handle.get();
//            }else {
//                return this.handle.get(obj);
//            }
//        }
//        if( !this.failPublicAccess){
//            return this.fastAccessInternal.get(obj,fastAccessIndex);
//        }
//        return this.field.get(obj);
        return getter.apply(obj);
    }
    private FieldGetter getterInternal(){
        //no snapshot ,direct get
        if(!this.createSnapshot){
            return this.field::get;
        }
        if(useHandle && !failHandle){
            if(isStatic){
                return (o)->this.handle.get();
            }else {
                return (o)->this.handle.get(o);
            }
        }
        if(!this.failPublicAccess){

            return (obj)->{return this.fastAccessInternal.get(obj,fastAccessIndex);};
        }
        return this.field::get;
    }
    private FieldSetter setterInternal() {
        if(isStatic&&isFinal){
            return (o,v)-> {throw new IllegalAccessException("Static final field can only be set using setUnsafe! Field:"+this.field);};
        }else {
            if(!this.createSnapshot){
                return this.field::set;
            }
            if(useHandle&& !failHandle&&!isFinal){
                if(isStatic){
                   return (o,value)-> this.handle.set(value);
                }else {
                   return (obj,value)-> this.handle.set(obj,value);
                }
            }else {
                if(!this.failPublicAccess){
                    return (obj,value)->{ this.fastAccessInternal.set(obj,fastAccessIndex,value);};
                }
                return this.field::set;
            }
        }
    }
    public FieldGetter getter(Object initialValue){
        init(initialValue);
        return getter;
    }
    public FieldSetter setter(Object initialValue){
        init(initialValue);
        return setter;
    }

    private void setInternal(Object obj, Object value) throws Throwable {
        setter.consume(obj,value);
//        if(isStatic&&isFinal){
//            throw new IllegalAccessException("Static final field can only be set using setUnsafe! Field:"+this.field);
//        }else {
//            if(useHandle&& !failHandle&&!isFinal){
//                if(isStatic){
//                    this.handle.set(value);
//                }else {
//                    this.handle.set(obj,value);
//                }
//            }else {
//                this.field.set(obj,value);
//            }
//        }
    }
    public Object getValue(Object obj) throws Throwable{
        if(this.getter == null){
            init(obj);
        }
        return getInternal(obj);
    }
    public <W extends Object> AccessWithObject<W> ofAccess(Object obj,Supplier<AccessWithObject<W>> supplier){
        init(obj);
        AccessWithObject<W> ob=supplier.get();
        ob.value=obj;
        if(FieldAccess.this.failInitialization){
            ob.failGet=true;
            ob.failSet=true;
        }
        return ob;
    }
    public <W extends Object> AccessWithObject<W> ofAccess(Object obj){
        return ofAccess(obj,AccessWithObject<W>::new);
    }
    public boolean compareFieldOrDefault(Object a1,Object a2,Supplier<Boolean> defaultVal){
        if(failInitialization){
            return defaultVal.get();
        }
        try{
            Object x1=getValue(a1);
            Object x2=getValue(a2);
            return Objects.equals(x1,x2);
        }catch (Throwable e){

            return defaultVal.get();
        }
    }
    public class AccessWithObject<T>{
        private boolean failGet=false;
        private boolean failSet=false;
        private boolean hasTried=false;
        private Object value;
        private T re;
        public boolean hasFailGet(){
            return hasTried&&failGet;
        }
        public boolean hasFailSet(){
            return hasTried&&failSet;
        }
        public AccessWithObject<T> get(Consumer<T> callback){
            if(!hasTried||isStatic){
                hasTried=true;
                try{
                    re=(T)getInternal(value);
                }catch(Throwable e){
                    if(printError){
                        Debug.logger("Access with object",value,"occurred an error: get");
                        Debug.logger(e);
                    }
                    failGet=true;
                    return this;
                }
            }
            if(failGet){
                re=null;
                return this;
            }
            callback.accept(re);
            return this;
        }
        private static final Consumer<?> NONE=(ignored)->{};
        public T getRaw(){
            if(!hasTried){
                get((Consumer<T>) NONE);
            }
            return re;
        }
        public T getRawOrDefault(T defaultValue){
            return  getRawOrDefault(()->defaultValue);
        }
        public T getRawOrDefault(Supplier<T> supplier){
            if(!hasTried){
                get((e)->{});
            }
            if(failGet){
                return supplier.get();
            }
            return re;
        }
        public <W extends Object> W computeIf(Function<T,W> map,Supplier<W> supplier){
            if(!hasTried){
                get((e)->{});
            }
            if(failGet){
                return supplier.get();
            }
            return map.apply(re);
        }
        public AccessWithObject<T> ifFailed(Consumer<Object> callback){
            if(failGet){
                callback.accept(value);
            }
            return this;
        }
        public boolean set(T value1){
            if(!failSet){
                try{
                    setInternal(value, value1);
                    re=value1;
                    return true;
                }catch (Throwable ignored){
                    if(printError){
                        Debug.logger("Access with object",value,"occurred an error: set");
                        Debug.logger(ignored);
                    }
                    failSet=true;
                }
            }
            return false;
        }
        public boolean setUnsafe(T value1){
            if(!failSet){
                if(!isStatic||!isFinal){
                    return set(value1);
                }else{
                    AtomicBoolean result=new AtomicBoolean(false);
                    ReflectUtils.getUnsafeSetter(FieldAccess.this.field,((unsafe,staticFieldBase, fieldOffset, field1) -> {
                        unsafe.putObject(staticFieldBase, fieldOffset,value1);
                        re=value1;
                        result.set(true);
                    }));
                    if(result.get()){
                        re=value1;
                        return true;
                    }else {
                        if(printError){
                            Debug.logger("Access with object",value,"occurred an error: setUnsafe");
                        }
                        failSet=true;
                        return false;
                    }
                }
            }
            return false;
        }
    }
    public String toString(){
        return "FieldAccess{ "+ (failInitialization?"failed":(field==null?"null":field))+" }";
    }

}
