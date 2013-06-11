package wyvern.DSL.deploy;

import wyvern.DSL.DSL;
import wyvern.DSL.deploy.parsers.architecture.ArchitectureParser;
import wyvern.DSL.deploy.parsers.architecture.EndpointParser;
import wyvern.DSL.deploy.parsers.architecture.connections.ConnectionParser;
import wyvern.DSL.deploy.parsers.architecture.connections.properties.DomainParser;
import wyvern.DSL.deploy.parsers.architecture.connections.properties.RequiresParser;
import wyvern.DSL.deploy.parsers.architecture.connections.properties.ViaParser;
import wyvern.tools.typedAST.core.Keyword;
import wyvern.tools.typedAST.core.binding.KeywordNameBinding;
import wyvern.tools.types.Environment;

public class Deploy implements DSL {
	@Override
	public Environment addToEnv(Environment in) {
		return in.extend(new KeywordNameBinding("architecture", new Keyword(new ArchitectureParser())));
	}

	public static Environment getArchInnerEnv(ArchitectureParser architectureParser) {
		return Environment.getEmptyEnvironment()
				.extend(new KeywordNameBinding("endpoint", new Keyword(new EndpointParser(architectureParser))))
				.extend(new KeywordNameBinding("via", new Keyword(new ViaParser())))
				.extend(new KeywordNameBinding("requires", new Keyword(new RequiresParser())))
				.extend(new KeywordNameBinding("connection", new Keyword(new ConnectionParser())))
				.extend(new KeywordNameBinding("domain", new Keyword(new DomainParser())));
	}
}
