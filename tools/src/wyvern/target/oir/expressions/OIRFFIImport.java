package wyvern.target.oir.expressions;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.declarations.OIRType;


public class OIRFFIImport extends OIRExpression {
    private FFIType ffiType;
    private String module;

    public OIRFFIImport(FFIType type, String module) {
        super();
        this.ffiType = type;
        this.module = module;
    }

    public FFIType getFFIType() {
        return this.ffiType;
    }

    public void setFFIType(FFIType type) {
        this.ffiType = type;
    }

    public String getModule() {
        return this.module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    @Override
    public OIRType typeCheck(OIREnvironment oirEnv) {
        return null;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }
}
