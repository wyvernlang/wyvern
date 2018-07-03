package wyvern.stdlib.support;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.ValDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.expression.BooleanLiteral;
import wyvern.target.corewyvernIL.expression.FloatLiteral;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.support.VarEvalContext;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;

public class Serializer {
  public static final Serializer serializer = new Serializer();

  public Serializer() {
  }

  public HashMap<String, Object> makeJSONMap(ObjectValue obj) {
    HashMap<String, Object> jsonmap = new HashMap<>();
    for (Declaration d : obj.getDecls()) {
      if (!(d instanceof ValDeclaration)) {
        ToolError.reportError(ErrorMessage.QUALIFIED_TYPES_ONLY_FIELDS,
            FileLocation.UNKNOWN);
      }
      String name = d.getName();
      IExpr expr = ((ValDeclaration) d).getDefinition();
      if (expr instanceof BooleanLiteral) {
        jsonmap.put(name, ((BooleanLiteral) expr).getValue());
      } else if (expr instanceof StringLiteral) {
        jsonmap.put(name, ((StringLiteral) expr).getValue());
      } else if (expr instanceof FloatLiteral) {
        jsonmap.put(name, ((FloatLiteral) expr).getFullValue());
      } else if (expr instanceof IntegerLiteral) {
        jsonmap.put(name, ((IntegerLiteral) expr).getFullValue());
      } else if (expr instanceof ObjectValue) {
        jsonmap.put(name, makeJSONMap((ObjectValue) expr));
      } else {
        ToolError.reportError(ErrorMessage.QUALIFIED_TYPES_ONLY_FIELDS,
            FileLocation.UNKNOWN);
      }
    }
    return jsonmap;
  }

  @SuppressWarnings("unchecked")
  public String toJSONString(Object obj) {
    HashMap<String, Object> hashmap = (HashMap<String, Object>) obj;
    StringBuffer json = new StringBuffer();
    int indent = 0;
    recToJSONString(hashmap, json, indent);
    return json.toString();
  }

  private void recToJSONString(HashMap<String, Object> hashmap,
      StringBuffer json, int indent) {
    json.append("{");
    int size = hashmap.size();
    int count = 1;
    for (String key : hashmap.keySet()) {
      json.append("\n");
      addTabs(json, indent + 1);
      json.append("\"" + key + "\": ");
      Object value = hashmap.get(key);
      if (value instanceof String) {
        json.append("\"" + value + "\"");
      } else if (value instanceof BigInteger) {
        json.append(value.toString());
      } else if (value instanceof Double) {
        json.append(value.toString());
      } else if (value instanceof Boolean) {
        json.append(value.toString());
      } else if (value instanceof HashMap) {
        recToJSONString((HashMap) value, json, indent + 1);
      } else {
        ToolError.reportError(ErrorMessage.QUALIFIED_TYPES_ONLY_FIELDS,
            FileLocation.UNKNOWN);
      }
      if (count < size) {
        count++;
        json.append(",");
      }
    }
    json.append("\n");
    addTabs(json, indent);
    json.append("}");
  }

  private void addTabs(StringBuffer json, int indent) {
    for (int i = 0; i < indent; i++) {
      json.append("\t");
    }
  }

  @SuppressWarnings("unchecked")
  public ObjectValue deserializeFromJSON(HashMap<String, Object> jsonobj) {
    BindingSite selfSite = new BindingSite("this");
    List<Declaration> decls = new LinkedList<>();
    List<DeclType> declTypes = new LinkedList<>();

    for (String key : jsonobj.keySet()) {
      Object value = jsonobj.get(key);
      IExpr def = null;
      ValueType t = null;
      DeclType dt = null;

      if (value instanceof BigInteger) {
        def = new IntegerLiteral((BigInteger) value);
        t = new NominalType("system", "Int");
        dt = new ValDeclType(key, t);
      } else if (value instanceof Double) {
        def = new FloatLiteral((Double) value);
        t = new NominalType("system", "Float");
        dt = new ValDeclType(key, t);
      } else if (value instanceof String) {
        def = new StringLiteral((String) value);
        t = new NominalType("system", "String");
        dt = new ValDeclType(key, t);
      } else if (value instanceof Boolean) {
        def = new BooleanLiteral((Boolean) value);
        t = new NominalType("system", "Boolean");
        dt = new ValDeclType(key, t);
      } else if (value instanceof HashMap) {
        def = deserializeFromJSON((HashMap<String, Object>) value);
        t = ((ObjectValue) def).getType();
      }

      ValDeclaration val = new ValDeclaration(key, t, def,
          FileLocation.UNKNOWN);
      decls.add(val);
      declTypes.add(dt);
    }

    ValueType exprType = new StructuralType("this", declTypes);
    ObjectValue top = new ObjectValue(decls, selfSite, exprType, null,
        FileLocation.UNKNOWN, new VarEvalContext(selfSite, null, null));
    return top;
  }
}
