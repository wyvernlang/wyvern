package wyvern.tools.parsing;

import edu.umn.cs.melt.copper.compiletime.skins.cup.CupSkinParser;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperElementName;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperElementReference;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.main.CopperIOType;
import edu.umn.cs.melt.copper.main.ParserCompiler;
import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.engines.semantics.VirtualLocation;
import edu.umn.cs.melt.copper.runtime.io.Location;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;

public class FirstChanger extends Task {
	private File original;
	private File target;
	private String newFC;
	private String hostGrammar;

	private String newPackage;
	private String newName;
	private String extraPackage;

	public void setOriginal(File file) {
		original = file;
	}

	public void setTarget(File file) {
		target = file;
	}

	public void setNewfc(String fc) {
		this.newFC = fc;
	}

	public void setHostgrammar(String hostGrammar) {
		this.hostGrammar = hostGrammar;
	}

	public void setNewpackage(String newPackage) {
		this.newPackage = newPackage;
	}

	public void setNewname(String newName) {
		this.newName = newName;
	}

	public void setExtrapackage(String extraPackage) {
		this.extraPackage = extraPackage;
	}

	@Override
	public void execute() throws BuildException {
		ParserBean host;
		try (InputStream is = new FileInputStream(original)) {
			host = new CupSkinParser().parse(new InputStreamReader(is), original.getName());
		} catch (Exception e) {
			throw new BuildException(e);
		}

		Location newFCLoc = null;
		try {
			newFCLoc = host.getGrammar(CopperElementName.newName("_" + hostGrammar))
					.getGrammarElement(CopperElementName.newName(newFC)).getLocation();
			host.setStartSymbol(CopperElementReference.ref(CopperElementName.newName("_" + hostGrammar), newFC, newFCLoc));
		} catch (ParseException e) {
			throw new BuildException(e);
		}

		host.setClassName(newName);
		host.setPackageDecl(newPackage);

		if (!extraPackage.isEmpty())
			host.setPreambleCode("import "+extraPackage+";"+host.getPreambleCode());

		ParserCompilerParameters pcp = new ParserCompilerParameters();
		pcp.setOutputFile(target);
		pcp.setOutputType(CopperIOType.FILE);

		try {
			ParserCompiler.compile(host, pcp);
		} catch (CopperException e) {
			throw new BuildException(e);
		}
	}

}
