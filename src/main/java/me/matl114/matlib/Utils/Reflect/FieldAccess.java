package me.matl114.matlib.Utils.Reflect;

import com.google.common.base.Preconditions;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import lombok.Getter;
import me.matl114.matlib.Utils.Debug;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FieldAccess {
    private boolean printError = false;
    private boolean failInitialization=false;
    private Function<Object, Field> lazilyInitializationFunction;
    private Field field;
    private boolean failHandle=false;
    private VarHandle handle;
    private boolean isStatic=false;
    private boolean isFinal=false;
    private Class<?> definedType=null;
    private static final boolean useHandle=false;
    public static FieldAccess ofName(String fieldName){
        return new FieldAccess((obj)->{
            var result=ReflectUtils.getFieldsRecursively(obj.getClass(),fieldName);
            return result==null?null:result.getA();
        }).printError(true);
    }
    public static FieldAccess of(Field field){
        return new FieldAccess((obj)->field).printError(true);
    }
    public static FieldAccess ofName(Class<?> clazz,String fieldName){
        return new FieldAccess((obj)->{
            var result=ReflectUtils.getFieldsRecursively(clazz,fieldName);
            return result==null?null:result.getA();
        }).printError(true);
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
                this.isFinal=Modifier.isFinal(this.field.getModifiers());
                this.isStatic= Modifier.isStatic(this.field.getModifiers());
                this.definedType=this.field.getType();
                try{
                    this.handle=MethodHandles.privateLookupIn(this.field.getDeclaringClass(),MethodHandles.lookup()).unreflectVarHandle(this.field);
                }catch(IllegalAccessException e){
                    this.failHandle=true;
                    if(printError){
                        Debug.logger("Failed to create field handle for Field :",field);
                        e.printStackTrace();
                    }
                }
            }catch (Throwable e){
                failInitialization=true;
                if(printError){
                    e.printStackTrace();
                }
            }
        }
        return this;
    }
    private Class getFieldType(){
        Preconditions.checkArgument(!failInitialization,"FieldAccess initialization failed!");
        Preconditions.checkArgument(definedType!=null,"FieldAccess field not initialized!");
        return definedType;
    }
    private Class getDeclareClass(){
        Preconditions.checkArgument(!failInitialization,"FieldAccess initialization failed!");
        Preconditions.checkArgument(field!=null,"FieldAccess field not initialized!");
        return field.getDeclaringClass();
    }
    public FieldAccess initWithNull(){
        init(null);
        return this;
    }
    private Object getInternal(Object obj) throws Throwable {
        if(useHandle&& !failHandle){
            if(isStatic){
                return this.handle.get();
            }else {
                return this.handle.get(obj);
            }
        }
        return this.field.get(obj);
    }
    private void setInternal(Object obj, Object value) throws Throwable {
        if(isStatic&&isFinal){
            throw new IllegalAccessException("Static final field can only be set using setUnsafe! Field:"+this.field);
        }else {
            if(useHandle&& !failHandle&&!isFinal){
                if(isStatic){
                    this.handle.set(value);
                }else {
                    this.handle.set(obj,value);
                }
            }else {
                this.field.set(obj,value);
            }
        }
    }
    public Object getValue(Object obj) throws Throwable{
        init(obj);
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


}
