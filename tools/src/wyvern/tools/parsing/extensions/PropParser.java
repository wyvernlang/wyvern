package wyvern.tools.parsing.extensions;

import static wyvern.tools.parsing.ParseUtils.parseSymbol;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.ContParser;
import wyvern.tools.parsing.DeclParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.FunDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Pair;

/**
 * A property in a type is written "prop f : T."  It is syntactic sugar for the two declarations "fun f : T" and "fun setF (x : T)"
 * Note that in the sugar, if f starts with a lowercase letter it is capitalized in the setF function; if f does not start with a
 * lowercase letter, set is just appended.
 */
public class PropParser implements DeclParser {
	private PropParser() { }
	private static PropParser instance = new PropParser();
	public static PropParser getInstance() { return instance; }
	
	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence, Environment> ctx) {
		Pair<Environment, ContParser> p = parseDeferred(first,  ctx);
		return p.second.parse(new ContParser.SimpleResolver(p.first.extend(ctx.second)));
	}

	@Override
	public Pair<Environment, ContParser> parseDeferred(TypedAST first,
			Pair<ExpressionSequence, Environment> ctx) {
		Symbol s = ParseUtils.parseSymbol(ctx);
		final String propName = s.name;
		FileLocation line = s.getLocation();
		final Type type;
		if (ParseUtils.checkFirst(":", ctx)) {
			parseSymbol(":", ctx);
			
			type = ParseUtils.parseType(ctx); // parseSymbol(ctx).name;
			if (ParseUtils.checkFirst("?", ctx)) {
				// typeName = typeName + "?"; // FIXME: Just hack for now until NULL/NON-NULL types done.
				ParseUtils.parseSymbol("?", ctx); 
			}
		} else {
			throw new RuntimeException("Error parsing prop expression : expected.");
		}
		
		// Need to return a DeclSequence with two FunDeclaration's.
		FunDeclaration getter = new FunDeclaration(propName, new LinkedList<NameBinding>(), type, null, false, line);
		
		List<NameBinding> args = new ArrayList<NameBinding>();
		args.add(new NameBindingImpl("new" + propName.substring(0,1).toUpperCase() + propName.substring(1), type));
		FunDeclaration setter = new FunDeclaration("set" + propName.substring(0,1).toUpperCase() + propName.substring(1),
			args, Unit.getInstance(), null, false, line);
		
		LinkedList<FunDeclaration> seq = new LinkedList<>();
		seq.add(getter);
		seq.add(setter);
		
		DeclSequence parsed = new DeclSequence(seq);
		// PropDeclaration parsed = new PropDeclaration(varName, type, line);
		
		return new Pair<Environment, ContParser>(parsed.extend(Environment.getEmptyEnvironment()), new ContParser.EmptyWithAST(parsed));		
	}
}