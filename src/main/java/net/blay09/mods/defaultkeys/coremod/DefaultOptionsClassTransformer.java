package net.blay09.mods.defaultkeys.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;

public class DefaultOptionsClassTransformer implements IClassTransformer {

    public static final String OBF_CLASS = "bao";
    public static final String MCP_CLASS = "net.minecraft.client.Minecraft";
    public static final String OBF_METHOD = "func_71384_a";
    public static final String MCP_METHOD = "startGame";
    private static final String METHOD_DESC = "()V";

    @Override
    public byte[] transform(String className, String transformedClassName, byte[] bytes) {
        String methodName;
        if(className.equals(OBF_CLASS)) {
            methodName = OBF_METHOD;
        } else if(className.equals(MCP_CLASS)) {
            methodName = MCP_METHOD;
        } else {
            return bytes;
        }
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
        Iterator<MethodNode> methods = classNode.methods.iterator();
        while(methods.hasNext()) {
            MethodNode method = methods.next();
            if(method.name.equals(methodName) && method.desc.equals(METHOD_DESC)) {
                AbstractInsnNode node = method.instructions.get(0);
                method.instructions.insertBefore(node, new MethodInsnNode(Opcodes.INVOKESTATIC, "net/blay09/mods/defaultkeys/DefaultKeys", "preStartGame", "()V", false));
            }
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

}
