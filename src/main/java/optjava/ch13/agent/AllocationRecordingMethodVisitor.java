package optjava.ch13.agent;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

/**
 *
 * @author kittylyst
 */
public final class AllocationRecordingMethodVisitor extends GeneratorAdapter {
    private final String runtimeAccounterTypeName = "optjava/agent/RuntimeCostAccounter";

    public AllocationRecordingMethodVisitor(MethodVisitor methodVisitor, int access, String name, String desc) {
        super(Opcodes.ASM5, methodVisitor, access, name, desc);
    }

    /**
     * 이 메서드는 정수형 오퍼랜드가 1개인 옵코드 방문 시 호출된다.
     * 여기서 옵코드는 NEWARRAY다.
     *
     * @param opcode
     * @param operand 
     */
    @Override
    public void visitIntInsn(final int opcode, final int operand) {
        if (opcode != Opcodes.NEWARRAY) {
            super.visitIntInsn(opcode, operand);
            return;
        }

        // 옵코드는 NEWARRAY - recordArrayAllocation:(Ljava/lang/String;I)V
        // 오퍼랜드 값은 다음 중 하나여야 한다.
        // Opcodes.T_BOOLEAN, Opcodes.T_CHAR, Opcodes.T_FLOAT, Opcodes.T_DOUBLE,
        // Opcodes.T_BYTE, Opcodes.T_SHORT, Opcodes.T_INT or Opcodes.T_LONG.
        final int typeSize;
        switch (operand) {
            case Opcodes.T_BOOLEAN:
            case Opcodes.T_BYTE:
                typeSize = 1;
                break;
            case Opcodes.T_SHORT:
            case Opcodes.T_CHAR:
                typeSize = 2;
                break;
            case Opcodes.T_INT:
            case Opcodes.T_FLOAT:
                typeSize = 4;
                break;
            case Opcodes.T_LONG:
            case Opcodes.T_DOUBLE:
                typeSize = 8;
                break;
            default:
                throw new IllegalStateException("Illegal operand to NEWARRAY seen: " + operand);
        }
        super.visitInsn(Opcodes.DUP);
        super.visitLdcInsn(typeSize);
        super.visitMethodInsn(Opcodes.INVOKESTATIC, runtimeAccounterTypeName, "recordArrayAllocation", "(II)V", true);
        super.visitIntInsn(opcode, operand);
    }

    /**
     * 이 메서드는 참조형(여기서는 String) 오퍼랜드가 1개인 옵코드 방문 시 호출된다.
     * 여기서 옵코드는 NEW 또는 ANEWARRAY다.
     *
     * @param opcode 
     * @param type 
     */
    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        // 옵코드는 NEW - recordAllocation:(Ljava/lang/String;)V 또는
        // ANEWARRAY - recordArrayAllocation:(Ljava/lang/String;I)V다.
        switch (opcode) {
            case Opcodes.NEW:
                super.visitLdcInsn(type);
                super.visitMethodInsn(Opcodes.INVOKESTATIC, runtimeAccounterTypeName, "recordAllocation", "(Ljava/lang/String;)V", true);
                break;
            case Opcodes.ANEWARRAY:
                super.visitInsn(Opcodes.DUP);
                super.visitLdcInsn(8);
                super.visitMethodInsn(Opcodes.INVOKESTATIC, runtimeAccounterTypeName, "recordArrayAllocation", "(II)V", true);
                break;
        }

        super.visitTypeInsn(opcode, type);
    }
}