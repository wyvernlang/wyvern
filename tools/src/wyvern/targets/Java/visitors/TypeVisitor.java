package wyvern.targets.Java.visitors;

import org.objectweb.asm.ClassWriter;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.visitors.BaseASTVisitor;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.V1_7;

public class TypeVisitor extends BaseASTVisitor {
    private final ClassStore store;
    private ClassWriter cw = null;

    public TypeVisitor(ClassStore store) {
        this.store = store;
    }

    @Override
    public void visit(TypeDeclaration td) {
        store.registerClass(td.getType());
        cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cw.visit(V1_7,
                ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,
                store.mangleTypeName(td.getType()),
                null,
                "java/lang/Object",
                null);
        super.visit(td);
        cw.visitEnd();
        store.registerClass(td.getType(), cw.toByteArray());
    }

    @Override
    public void visit(DefDeclaration md) {
        cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, md.getName(), store.getTypeName(md.getType(), true), null, null).visitEnd();
    }
}
