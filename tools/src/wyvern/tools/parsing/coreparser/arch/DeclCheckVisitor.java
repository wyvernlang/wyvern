package wyvern.tools.parsing.coreparser.arch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;

public class DeclCheckVisitor extends ArchParserDefaultVisitor {
    private InterpreterState state;
    private HashSet<String> connectorTypes = new HashSet<>();
    // "componentTypes" maps component type name to the component type declaration AST node
    private HashMap<String, ASTComponentTypeDecl> componentTypes = new HashMap<>();
    // components maps component instance name to the component declaration AST node
    private HashMap<String, ASTComponentDecl> components = new HashMap<>();
    // "connectors" maps connector instance name to the connector type
    private HashMap<String, String> connectors = new HashMap<>();
    // entrypoints maps entrypoint name to the corresponding component instance name
    private HashMap<String, String> entrypoints = new HashMap<>();
    // attachments maps connector instance name to the component ports in the connection
    private HashMap<String, HashSet<String>> attachments = new HashMap<>();
    // portdecls maps port name to the port declaration AST node
    private HashMap<String, ASTPortDecl> portdecls = new HashMap<>();

    public boolean endOfFileCheck() {
        for (String connector : attachments.keySet()) {
            if (!connectors.containsKey(connector)) {
                // connector not declared
                ToolError.reportError(ErrorMessage.MEMBER_NOT_DECLARED,
                        FileLocation.UNKNOWN, "connector", connector);
            }
        }
        return true;
    }

    public List<ASTComponentDecl> generateDependencyGraph() {
        // FIXME
        // Currently doesn't handle cycles correctly between port dependencies
        HashMap<String, DependencyGraphNode> nodes = new HashMap<>();
        ArrayList<DependencyGraphNode> noReqNodes = new ArrayList<>();
        List<ASTComponentDecl> ordered = new LinkedList<>();
        for (String connector : attachments.keySet()) {                                 //for all connector attachments
            HashSet<String> fullports = attachments.get(connector);
            for (String fullport : fullports) {                                         //for each component port in an attachment
                String component = fullport.substring(0, fullport.indexOf("."));        //get component instance name
                ASTComponentDecl compObj = components.get(component);                   //get component instance AST Node
                ASTComponentTypeDecl compType = componentTypes.get(compObj.getType());  //get component instance type
                if (!nodes.containsKey(component)) {                                    //if it's not in the visited list
                    DependencyGraphNode compNode = new DependencyGraphNode(compObj);    //  put it in there
                    nodes.put(component, compNode);
                }
                DependencyGraphNode compNode = nodes.get(component);
                if (compType.getReqs().size() == 0) {                                   //if it doesn't have incoming edges
                    noReqNodes.add(compNode);                                           //  add it to the noReqNodes list
                }
                for (String reqs : compType.getReqs().values()) {                       //for all of its incoming edges; get required interfaces
                    for (String innerfullport : fullports) {                            //look through ports of the attachment
                        String innercomponent = innerfullport.substring(0, innerfullport.indexOf("."));
                        ASTComponentDecl innercompObj = components.get(innercomponent);
                        ASTComponentTypeDecl innercompType = componentTypes.get(innercompObj.getType());
                        if (!nodes.containsKey(innercomponent)) {
                            DependencyGraphNode innercompNode = new DependencyGraphNode(innercompObj);
                            nodes.put(innercomponent, innercompNode);
                        }
                        DependencyGraphNode innercompNode = nodes.get(innercomponent);
                        for (String provs : innercompType.getProvs().values()) {
                            if (provs.equals(reqs)) {
                                compNode.addRequires(innercompNode);
                                innercompNode.addProvides(compNode);
                                nodes.put(innercomponent, innercompNode);
                                nodes.put(component, compNode);
                            }
                        }
                    }
                }
            }
        }

        // Apply topological sort to the graph!
        for (DependencyGraphNode m : noReqNodes) {
            ordered.add(m.getComponentDecl());
            for (DependencyGraphNode n : m.getProvides()) {
                n.getProvides().remove(m);
                if (n.getProvides().size() == 0) {
                    ordered.add(n.getComponentDecl());
                }
            }
        }
        return ordered;
    }

    public HashMap<String, ASTComponentTypeDecl> getComponentTypes() {
        return componentTypes;
    }

    public HashSet<String> getConnectorTypes() {
        return connectorTypes;
    }

    public HashMap<String, ASTComponentDecl> getComponents() {
        return components;
    }

    public HashMap<String, String> getConnectors() {
        return connectors;
    }

    public HashMap<String, String> getEntrypoints() {
        return entrypoints;
    }

    public HashMap<String, HashSet<String>> getAttachments() {
        return attachments;
    }

    public HashMap<String, ASTPortDecl> getPortDecls() {
        return portdecls;
    }

    public DeclCheckVisitor(InterpreterState state) {
        super();
        this.state = state;
    }

    public Object visit(ASTAttachmentDecl node, Object data) {
        String connector = node.getConnector();
        HashSet<String> ports = node.getAllPorts();
        if (attachments.putIfAbsent(connector, ports) != null) {
            // duplicate connector use
            ToolError.reportError(ErrorMessage.DUPLICATE_CONNECTOR_USE,
                    node.getLocation(), connector);
        }
        /* Check if ports are improperly declared */
        for (String portFull : ports) {
            String[] portParts = portFull.split("\\.", 2);
            String component = portParts[0];
            String port = portParts[1];
            // check component declaration exists
            // get component type
            ASTComponentDecl comp = components.get(component);
            if (comp == null) {
                ToolError.reportError(ErrorMessage.MEMBER_NOT_DECLARED,
                        node.getLocation(), "component", component);
            }
            // check component of that type has port
            ASTComponentTypeDecl typeDecl = componentTypes
                    .get(comp.getType());
            if (!typeDecl.getReqs().containsKey(port)
                    && !typeDecl.getProvs().containsKey(port)) {
                ToolError.reportError(ErrorMessage.MEMBER_NOT_DECLARED,
                        node.getLocation(), "port", port);
            }

        }
        return super.visit(node, data);
    }

    public Object visit(ASTPortDecl node, Object data) {
        portdecls.put(node.getPort(), node);
        return super.visit(node, data);
    }

    public Object visit(ASTEntryPointDecl node, Object data) {
        entrypoints.put(node.getName(), node.getAction());
        return super.visit(node, data);
    }

    public Object visit(ASTComponentDecl node, Object data) {
        String name = node.getName();
        String type = node.getType();
        if (!componentTypes.containsKey(type)) {
            // component type not declared
            ToolError.reportError(ErrorMessage.ARCH_TYPE_NOT_DEFINED,
                    node.getLocation(), "component", type);
        }
        if (components.putIfAbsent(name, node) != null) {
            // duplicate members
            ToolError.reportError(ErrorMessage.DUPLICATE_MEMBER_NAMES,
                    node.getLocation(), name);
        }
        return super.visit(node, data);
    }

    public Object visit(ASTConnectorDecl node, Object data) {
        String name = node.getName();
        String type = node.getType();
        if (!connectorTypes.contains(type)) {
            // connector type not declared
            ToolError.reportError(ErrorMessage.ARCH_TYPE_NOT_DEFINED,
                    node.getLocation(), "connector", type);
        }
        if (connectors.putIfAbsent(name, type) != null) {
            // duplicate members
            ToolError.reportError(ErrorMessage.DUPLICATE_MEMBER_NAMES,
                    node.getLocation(), name);
        }
        return super.visit(node, data);
    }

    public Object visit(ASTComponentTypeDecl node, Object data) {
        if (componentTypes.containsKey(node.getTypeName())) {
            // type of this name has already been declared
            ToolError.reportError(ErrorMessage.DUPLICATE_TYPE_DEFINITIONS,
                    node.getLocation(), node.getTypeName());
        }
        node.collectPorts();
        if (node.checkModule(state)) {
            componentTypes.put(node.getTypeName(), node);
        }
        return super.visit(node, data);
    }

    public Object visit(ASTConnectorTypeDecl node, Object data) {
        if (connectorTypes.contains(node.getTypeName())) {
            // type of this name has already been declared
            ToolError.reportError(ErrorMessage.DUPLICATE_TYPE_DEFINITIONS,
                    node.getLocation(), node.getTypeName());
        }
        node.collectVals();
        if (node.checkModule(state)) {
            connectorTypes.add(node.getTypeName());
        }
        return super.visit(node, data);
    }

}
