/**
 * 
 */
package translation;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * @author Timo Freitag
 *
 */
public class DirectoryHandler {
	private File project;
	private File ecore;
	private File xmi;
	private File bin;
	private File cpn;
	
	public DirectoryHandler(File project) {
		this.project = project;
	}
	
	public void initialize() {
		ecore = new File(project.getAbsolutePath() + File.separatorChar + "model" + File.separatorChar + project.getName() + ".ecore");
		bin = new File(project.getAbsolutePath() + File.separatorChar + "bin" + File.separator);
		xmi = new File(project.getAbsolutePath() + File.separatorChar + "instances");
		cpn = new File(project.getAbsolutePath() + File.separator + "cpn" + File.separator + project.getName() +".cpn");
	}

	public File getProject() {
		return project;
	}

	public File getEcore() {
		return ecore;
	}

	public File getXmi() {
		return xmi;
	}
	
	public File getCpn() {
		return cpn;
	}

	public File getBin() {
		return bin;
	}
	
	public String getFullClassName(String name) {
		if(name.endsWith("Impl"))
			return project.getName() + ".impl." + name;
		return project.getName() + "." + name;
	}
	
	public File getLogFile(String name) {
		DateFormat dateFormat = new SimpleDateFormat("_dd_MM_yyyy_HH_mm");
		Date date = new Date();
		File log = new File(project.getAbsolutePath() + File.separator + "cpn" + File.separator
				+ "simulationLog" + File.separatorChar + name + dateFormat.format(date) + ".txt");
		return log;
	}
}
