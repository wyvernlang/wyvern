package wyvern.DSL.deploy.parsers.architecture.connections.properties;

import wyvern.DSL.deploy.parsers.architecture.connections.ConnectionPropertyParser;
import wyvern.DSL.deploy.typedAST.architecture.properties.DomainProperty;
import wyvern.DSL.deploy.types.EndpointType;
import wyvern.tools.parsing.ContParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.parsing.TypeParser;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.util.Pair;

public class DomainParser extends ConnectionPropertyParser {

	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence, Environment> ctx) {
		return null;
	}

	@Override
	public Pair<Environment, ContParser> parseDeferred(final TypedAST first, final Pair<ExpressionSequence, Environment> ctx) {
		final DomainProperty dp = new DomainProperty(null, null, null);
		final Pair<ExpressionSequence, Environment> ext =
				new Pair<>(ctx.first, dp.extend(ctx.second));

		//final Arrow domainT = (Arrow) ParseUtils.parseType(ctx);
		final ParseUtils.LazyEval<Type> lazyType = TypeParser.parsePartialType(ctx);
		final Pair<Environment, ContParser> body = DomainParser.super.iParse(ctx);

		ctx.first = null;
		return new Pair<Environment, ContParser>(
			body.first.extend(Environment.getEmptyEnvironment()),
			new ContParser() {

                @Override
				public TypedAST parse(EnvironmentResolver r) {
					Environment iEnv = r.getEnv(dp);
					Arrow domain = (Arrow)lazyType.eval(iEnv);
					dp.setVals((EndpointType)domain.getArgument(), (EndpointType)domain.getResult(), null);
					TypedAST bodyAST = body.second.parse(new ExtensionResolver(r, dp.getBinding()));
					return bodyAST;
				}
			}
		);
	}
}
