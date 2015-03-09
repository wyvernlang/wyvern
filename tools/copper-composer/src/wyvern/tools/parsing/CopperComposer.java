package wyvern.tools.parsing;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.PrintCompilerLogHandler;
import edu.umn.cs.melt.copper.compiletime.skins.cup.CupSkinParser;
import edu.umn.cs.melt.copper.compiletime.skins.xml.XMLSkinParser;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.*;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.ParserSpecProcessor;
import edu.umn.cs.melt.copper.main.CopperIOType;
import edu.umn.cs.melt.copper.main.ParserCompiler;
import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CopperComposer extends Task {
	private File hostGrammar;
	private String hostGrammarName;
	private File extensionGrammar;
	private File output;
	private Pattern terminalRegexp = Pattern.compile("Terminals\\.([a-zA-Z0-9_]+)");

	public void setExtensiongrammar(File extensionGrammar) {
		this.extensionGrammar = extensionGrammar;
	}

	public void setHostgrammar(File hostGrammar) {
		this.hostGrammar = hostGrammar;
	}

	public void setHostgrammarname(String hostGrammarName) {
		this.hostGrammarName = hostGrammarName;
	}

	public void setOutput(File output) { this.output = output; }

	public static void main(String[] args) {

	}
	private class NullOutputStream extends OutputStream {
		@Override
		public void write(int b) throws IOException {
		}
	}
	@Override
	public void execute() throws BuildException {
		ParserBean host;
		try (InputStream is = new FileInputStream(hostGrammar)) {
			host = new CupSkinParser().parse(new InputStreamReader(is), hostGrammar.getName());
		} catch (Exception e) {
			throw new BuildException(e);
		}

		ParserBean extension;
		try (InputStream is = new FileInputStream(extensionGrammar)) {
			ArrayList<Pair<String,Reader>> files = new ArrayList<>();
			files.add(new Pair<String, Reader>(extensionGrammar.getName(), new InputStreamReader(is)));
			XMLSkinParser xmlSkinParser = new XMLSkinParser(files,
					new CompilerLogger(new PrintCompilerLogHandler(new PrintStream(new OutputStream() {
						public void write(int b) {
							//Nothing - suppress all messages
						}
					}))));
			xmlSkinParser.parse();
			Field parserField = xmlSkinParser.getClass().getDeclaredField("currentParser");
			parserField.setAccessible(true);
			extension = (ParserBean)parserField.get(xmlSkinParser);
		} catch (Exception e) {
			throw new BuildException(e);
		}

		try {
			CopperElementName grammarName = CopperElementName.newName("_" + hostGrammarName);
			((ExtendedParserBean)extension).setHostGrammar(host.getGrammar(grammarName));
			Field grammarField = extension.getClass().getSuperclass().getDeclaredField("grammars");
			grammarField.setAccessible(true);
			Hashtable<CopperElementName,Grammar> grammars =
					(Hashtable<CopperElementName, Grammar>) grammarField.get(extension);
			grammars.put(grammarName,host.getGrammar(grammarName));
		} catch (Exception e) {
			throw new BuildException(e);
		}

		extension.setParserClassAuxCode(host.getParserClassAuxCode());
		extension.setParserInitCode(host.getParserInitCode());

		Function<CopperElementReference, String> nameGetter = cen -> cen.getGrammarName() +"$"+cen.getName();

		Stream<Stream<GrammarElement>> elements = host.getGrammars().stream().map(host::getGrammar)
				.map(grammar -> grammar.getGrammarElements().stream().map(grammar::getGrammarElement));

		List<DisambiguationFunction> dfl = elements
				.map(ielements -> ielements.filter(el -> el instanceof DisambiguationFunction))
				.flatMap(e->e.map(el->(DisambiguationFunction)el)).collect(Collectors.toList());
		dfl.stream().forEach(df ->
				df.setCode(df.getMembers().stream()
						.map(cen -> "\t\t\tfinal int " + cen.getName() + " = " + nameGetter.apply(cen) + ";")
						.reduce((l, r) -> l + "\n" + r).orElseGet(() -> "") + "\n" + df.getCode()));



		host.getGrammars().stream().map(host::getGrammar)
				.map(grammar -> grammar.getGrammarElements().stream().map(grammar::getGrammarElement))
				.flatMap(els -> els.filter(el -> el instanceof Production).map(el -> (Production) el))
				.forEach(this::rewriteTerminals);

		host.getGrammars().stream().map(host::getGrammar)
				.map(grammar -> grammar.getGrammarElements().stream().map(grammar::getGrammarElement))
				.flatMap(els -> els.filter(el -> el instanceof Terminal).map(el -> (Terminal) el))
				.forEach(this::rewriteTerminals);
		try {
			CompilerLogger logger = new CompilerLogger(new PrintCompilerLogHandler(System.out));
			ParserSpecProcessor.normalizeParser(extension, logger);
		} catch (CopperException e) {
			throw new BuildException(e);
		}
		ParserCompilerParameters pcp = new ParserCompilerParameters();

		ByteArrayOutputStream target = new ByteArrayOutputStream();
		pcp.setOutputStream(new PrintStream(target));
		pcp.setOutputType(CopperIOType.STREAM);


		try {
			ParserCompiler.compile(extension, pcp);
		} catch (CopperException e) {
			throw new BuildException(e);
		}

		try (FileOutputStream fos = new FileOutputStream(output)) {
			fos.write(target.toByteArray());
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}

	private void rewriteTerminals(Production prod) {
		if (prod.getCode() == null)
			return;
		Matcher m = terminalRegexp.matcher(prod.getCode());
		StringBuffer ob = new StringBuffer();
		while (m.find())
			m.appendReplacement(ob, "Terminals._"+hostGrammarName+"\\$$1");
		m.appendTail(ob);
		prod.setCode(ob.toString());
	}

	private void rewriteTerminals(Terminal term) {
		if (term.getCode() == null)
			return;
		Matcher m = terminalRegexp.matcher(term.getCode());
		StringBuffer ob = new StringBuffer();
		while (m.find())
			m.appendReplacement(ob, "Terminals._"+hostGrammarName+"\\$$1");
		m.appendTail(ob);
		term.setCode(ob.toString());
	}

}
