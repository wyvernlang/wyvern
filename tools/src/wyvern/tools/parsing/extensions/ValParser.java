package wyvern.tools.parsing.extensions;

import static wyvern.tools.parsing.ParseUtils.parseSymbol;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.parsing.ContParser;
import wyvern.tools.parsing.DeclParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.typedAST.core.binding.LateNameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.UnresolvedType;
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.Pair;

public class ValParser implements DeclParser {
    private ValParser() {
    }

    private static ValParser instance = new ValParser();

    public static ValParser getInstance() {
        return instance;
    }


    @Override
    public TypedAST parse(TypedAST first, CompilationContext ctx) {
        Pair<Environment, ContParser> p = parseDeferred(first, ctx);
        return p.second.parse(new ContParser.SimpleResolver(p.first.extend(ctx.getEnv())));
    }


    //@Override
    public Pair<Environment, ContParser> parseDeferred(TypedAST first,
                                                       final CompilationContext ctx) {
        Symbol s = ParseUtils.parseSymbol(ctx);
        final String valName = s.name;
        final FileLocation valNameLocation = s.getLocation();

        if (ParseUtils.checkFirst("=", ctx)) {
            //ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
            //return null;
        }
        Type type = null;
        if (ParseUtils.checkFirst(":", ctx)) {
            parseSymbol(":", ctx);
            type = ParseUtils.parseType(ctx);
        }

        ctx.setExpected(type);

        final CompilationContext restctx = ctx.copyAndClear();

        ValDeclaration nc = null;
        if (restctx.getTokens() == null)
            nc = new ValDeclaration(valName, type, null, valNameLocation);
        else if (ParseUtils.checkFirst("=", restctx)) {
            ParseUtils.parseSymbol("=", restctx);
            nc = new ValDeclaration(valName, new UnresolvedType("Dummy"), null, valNameLocation);
        } else {
            ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, restctx.getTokens());
            nc = null;
        }
        final Type parsedType = type;

        final ValDeclaration intermvd = nc;
        return new Pair<Environment, ContParser>(
                Environment.getEmptyEnvironment().extend(new NameBindingImpl(valName, parsedType)),
                new ContParser() {
                    @Override
                    public TypedAST parse(EnvironmentResolver r) {
                        TypedAST definition = null;
                        Type type = null;
                        if (restctx.getTokens() != null) {
                            definition = ParseUtils.parseExpr(restctx);
                            type = definition.typecheck(ctx.getEnv());
                        } else {
                            type = parsedType;
                        }
                        return new ValDeclaration(valName, type, definition, valNameLocation);
                    }
                });
    }
}