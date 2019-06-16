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

    public Serializer() { }

    public HashMap<String, Object> makeJSONMap(Object obj) {
        HashMap<String, Object> jsonmap = new HashMap<>();
        if (obj instanceof ObjectValue) {
            jsonmap.put("", recMakeJSONMap((ObjectValue) obj));
        } else {
            jsonmap.put("", obj);
        }
        return jsonmap;
    }

    private HashMap<String, Object> recMakeJSONMap(ObjectValue obj) {
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
                jsonmap.put(name, recMakeJSONMap((ObjectValue) expr));
            } else {
                ToolError.reportError(ErrorMessage.QUALIFIED_TYPES_ONLY_FIELDS,
                        FileLocation.UNKNOWN);
            }
        }
        return jsonmap;
    }

    public Object stringToJSONMap(String str) {
        HashMap<String, Object> jmap = new HashMap<>();
        HashMap<String, Object> inner = new HashMap<>();
        str = str.substring(2, str.length() - 2).trim();
        String[] pair = str.split(":\\s", 2);
        String key = pair[0].substring(1, pair[0].length() - 1);
        String val = pair[1];
        recStringToJSONMap(key, val, inner);
        if (inner.keySet().size() == 1 && inner.containsKey("")) {
            jmap = inner;
        } else {
            jmap.put("", inner);
        }
        return jmap;
    }

    private void recStringToJSONMap(String key, String val,
                                    HashMap<String, Object> jmap) {
        key = key.trim();
        val = val.trim();
        if (val.indexOf("{") == 0 && val.lastIndexOf("}") == val.length() - 1) {
            val = val.substring(1, val.length() - 1);
            if (!val.contains("{") || !val.contains("}")) {
                recStringToJSONMap(key, val, jmap);
            } else {
                int openBrace = val.indexOf("{");
                int closeBrace = val.lastIndexOf("}");

                String before = val.substring(0, openBrace - 3);
                int quote = before.lastIndexOf("\"");
                String k = before.substring(quote + 1);
                before = before.substring(0, quote);
                recStringToJSONMap(k, before, jmap);

                String v = val.substring(openBrace, closeBrace + 1).trim();
                HashMap<String, Object> inner = new HashMap<>();
                recStringToJSONMap(k, v, inner);
                jmap.put(k, inner);

                String after = val.substring(closeBrace + 1);
                after = (after.charAt(0) == ',') ? after.substring(1) : after;
                recStringToJSONMap(k, after, jmap);
            }
        } else {
            String[] entries = val.split(",\n");
            for (String entry : entries) {
                String[] pair = entry.split(":\\s");
                if (pair.length == 2) {
                    String k = pair[0].trim();
                    k = k.substring(1, k.length() - 1);
                    String v = (pair[1].charAt(pair[1].length() - 1) == ',')
                            ? pair[1].substring(0, pair[1].length() - 1)
                            : pair[1];
                    v = v.trim();
                    jmap.put(k, matchPrimValue(v));
                } else {
                    jmap.put("", matchPrimValue(pair[0]));
                }
            }
        }
    }

    private Object matchPrimValue(String value) {
        if (value.equals("true")) {
            return true;
        } else if (value.equals("false")) {
            return false;
        } else if (value.matches("[0-9]+")) {
            return new BigInteger(value);
        } else if (value.matches(
                "([0].[0-9]*)|([1-9][0-9]*.[0-9]+)|(.[0-9]+)|([1-9][0-9]*.)")) {
            return (Double) Double.parseDouble(value);
        } else {
            return value.substring(1, value.length() - 1);
        }
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
    public Object deserializeFromJSON(HashMap<String, Object> hmap) {
        Object jsonmap = hmap.get("");
        if (jsonmap instanceof HashMap) {
            return recDeserializeFromJSON((HashMap<String, Object>) jsonmap);
        } else {
            return jsonmap;
        }
    }

    @SuppressWarnings("unchecked")
    private ObjectValue recDeserializeFromJSON(
            HashMap<String, Object> jsonobj) {
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
                def = recDeserializeFromJSON((HashMap<String, Object>) value);
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
