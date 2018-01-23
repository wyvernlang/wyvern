package wyvern.tools.types;

import java.util.List;

public interface ParameterizableType {
    Type checkParameters(List<Type> params);
}
