package net.blay09.mods.defaultoptions.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class DefaultOptionsClassTransformer implements IClassTransformer {

    public static final String MCP_CLASS = "net.minecraft.client.Minecraft";
    public static final String OBF_METHOD = "func_71384_a";
    public static final String MCP_METHOD = "startGame";
    private static final String METHOD_DESC = "()V";

    @Override
    public byte[] transform(String className, String transformedClassName, byte[] bytes) {
        if(!transformedClassName.equals(MCP_CLASS)) {
            return bytes;
        }
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
        for (MethodNode method : classNode.methods) {
            if ((method.name.equals(OBF_METHOD) || method.name.equals(MCP_METHOD)) && method.desc.equals(METHOD_DESC)) {
                AbstractInsnNode node = method.instructions.get(0);
                method.instructions.insertBefore(node, new MethodInsnNode(Opcodes.INVOKESTATIC, "net/blay09/mods/defaultoptions/DefaultOptions", "preStartGame", "()V", false));
                break;
            }
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

}
