package me.matl114.matlib.utils.reflect.wrapper;

import com.google.common.base.Preconditions;
import lombok.Getter;
import me.matl114.matlib.algorithms.dataStructures.frames.initBuidler.InitializeProvider;
import me.matl114.matlib.common.functions.reflect.FieldGetter;
import me.matl114.matlib.common.functions.reflect.FieldSetter;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FieldAccess {
    private boolean printError = Debug.isDebugMod();
    @Getter
    private boolean failInitialization=false;
    private Function<Object, Field> lazilyInitializationFunction;
    Field field;
    private boolean failHandle=false;
    private VarHandle handle;
    @Getter
    boolean staticField =false;
    @Getter
    boolean finalField = false;
    @Getter
    boolean publicField = false;
    boolean isPrivate = false;
    boolean createSnapshot = true;
    public FieldAccess noSnapShot(){
        this.createSnapshot = false;
        return this;
    }
    FieldGetter getter = null;
    FieldSetter setter = null;
    public static FieldAccess reflect(String fieldname, Class<?> tar){
        return ofName(tar, fieldname).noSnapShot().printError(true).initWithNull();
    }
    public static FieldAccess ofName(String fieldName){
        return new FieldAccess((obj)->{
            var result= ReflectUtils.getFieldsRecursively(obj.getClass(),fieldName);
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
    public boolean successfullyInitialized(){
        return this.field != null;
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
                this.finalField =Modifier.isFinal(modifiers);
                this.staticField = Modifier.isStatic(modifiers);
                if(createSnapshot){
                    this.publicField = Modifier.isPublic(modifiers);
                    this.isPrivate = Modifier.isPrivate(modifiers);
                    //only the field who has full access can create fast Access throw FieldAccess

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
    public Field getFieldOrDefault(Supplier<Field> defa){
        init(null);
       // Debug.logger("getField called" + field);
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

    public FieldAccess initWithNull(){
        init(null);
        return this;
    }
    private Object getInternal(Object obj) throws Throwable {
        return getter.getField(obj);
    }
    private FieldGetter getterInternal(){
        //no snapshot ,direct get
        if(!this.createSnapshot){
            return (obj)->{
                try{
                    return this.field.get(obj);
                }catch (Throwable e){
                    throw new RuntimeException(e);
                }
            };
        }
         return  (obj)->{
            try{
                return this.field.get(obj);
            }catch (Throwable ex){
                throw new RuntimeException(ex);
            }
        };
    }
    private FieldSetter setterInternal() {
        if(staticField && finalField){
            return (o,v)-> {throw new RuntimeException("Static final field can only be set using setUnsafe! Field:"+this.field);};
        }else {
            return (o,v)->{
                try{
                    this.field.set(o,v);
                }catch (IllegalAccessException ex){
                    throw  new RuntimeException(ex);
                }
            };
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
        setter.setField(obj,value);
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
            if(!hasTried|| staticField){
                hasTried=true;
                try{
                    re=(T)getInternal(value);
                }catch(Throwable e){
                    if(printError){
                        Debug.logger(e,"Access with object",value,"occurred an error: get");
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
                }catch (Throwable ignore){
                    if(printError){
                        Debug.logger(ignore,"Access with object",value,"occurred an error: set");
                    }
                    failSet=true;
                }
            }
            return false;
        }
        public boolean setUnsafe(T value1){
            if(!failSet){
                if(!staticField ||!finalField){
                    return set(value1);
                }else{
                    try{
                        Unsafe unsafe = ReflectUtils.getUnsafe();
                        Object staticFieldBase = unsafe.staticFieldBase(field);
                        long fieldOffset = unsafe.staticFieldOffset(field);
                        unsafe.putObject(staticFieldBase, fieldOffset, value1);
                        re = value1;
                        return true;
                    }catch (Throwable e){
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
