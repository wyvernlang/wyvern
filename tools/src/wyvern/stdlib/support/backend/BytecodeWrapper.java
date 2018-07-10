package wyvern.stdlib.support.backend;

import com.google.protobuf.ByteString;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public class BytecodeWrapper {
    public static final BytecodeWrapper bytecode = new BytecodeWrapper();
    public Object loadBytecode(String filename) {
        BytecodeOuterClass.Bytecode bytecode = null;
        try {
            bytecode =  BytecodeOuterClass.Bytecode.parseFrom(new FileInputStream(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytecode;
    }

    public BigInteger byteStringToInt(ByteString byteString) {
        byte[] b = byteString.toByteArray();

        return new BigInteger(b);
    }

    public List<Object> dynToList(Object object) {
        return (List<Object>) object;
    }
}
