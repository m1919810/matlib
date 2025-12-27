package me.matl114.matlib.utils.reflect.asm;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import lombok.Getter;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

@Getter
public class DebugClassReader extends ClassVisitor {
    Map<Pair<String, String>, MethodNode> methodInfo = new LinkedHashMap<>();
    Map<String, FieldNode> fieldInfo = new LinkedHashMap<>();

    public DebugClassReader(int api) {
        super(api);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        FieldNode node = new FieldNode(access, name, descriptor, signature, value);
        fieldInfo.put(name, node);
        return node;
    }

    @Override
    public MethodVisitor visitMethod(
            int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodNode node = new MethodNode(Opcodes.ASM9, access, name, descriptor, signature, exceptions);
        methodInfo.put(Pair.of(name, descriptor), node);
        return node;
    }

    public static void printInfo(InsnList list, Consumer<String> print) {
        for (var re : list) {
            print.accept(re.getClass().getSimpleName() + ": type:" + re.getType() + "  Opcode:" + re.getOpcode() + " ");
        }
    }
}
