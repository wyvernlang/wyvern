package wyvern.DSL.deploy.parsers.architecture.connections;

import wyvern.DSL.deploy.typedAST.architecture.Connection;
import wyvern.DSL.deploy.types.DomainType;
import wyvern.DSL.deploy.types.ViaType;
import wyvern.tools.parsing.*;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.Pair;

import java.util.List;

public class ConnectionParser implements DeclParser {
	@Override
	public TypedAST parse(TypedAST first, CompilationContext ctx) {
		return null;
	}

	@Override
	public Pair<Environment, ContParser> parseDeferred(TypedAST first, CompilationContext ctx) {
		String name = ParseUtils.parseSymbol(ctx).name;
		CompilationContext innerCtx = new CompilationContext(ctx.first,ctx.second.getExternalEnv());
		List<NameBinding> args = ParseUtils.getNameBindings(innerCtx);
		Type returnType = ParseUtils.parseReturnType(innerCtx);
		ctx.first = innerCtx.first;
		final Connection conn = new Connection(name, args, returnType, null);
		final ExpressionSequence modiferExprs = ctx.first;
		ctx.first = null;

		return new Pair<Environment, ContParser>(conn.extend(ctx.second),
				new ContParser() {
                    @Override
					public TypedAST parse(EnvironmentResolver r) {
						Environment intEnv = r.getEnv(conn);

						TypeBinding domainBinding = intEnv.lookupType("domain");
						TypeBinding viaBinding = intEnv.lookupType("via");
						TypeBinding requiresBinding = intEnv.lookupType("requires");

						if (domainBinding == null || viaBinding == null)
							throw new RuntimeException();

						conn.setProperties((DomainType)domainBinding.getType(), (ViaType)viaBinding.getType());

						if (modiferExprs != null)
							conn.setModifiers(BodyParser.getInstance().visit(modiferExprs, intEnv));

						((DomainType)domainBinding.getType())
								.getDomain().getFinalEndpoint().getEndpoint().addConnection(conn);
						return conn;  //To change body of implemented methods use File | Settings | File Templates.
					}
				});
	}
}
