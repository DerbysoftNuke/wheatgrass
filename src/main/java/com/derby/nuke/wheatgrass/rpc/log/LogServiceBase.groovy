package com.derby.nuke.wheatgrass.rpc.log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * LogServiceBase
 *
 * @author Drizzt Yang
 */
public abstract class LogServiceBase implements LogService {
	private String directory;

	protected LogServiceBase(String directory) {
		this.directory = directory;
	}

	@Override
	public List<String> listLogs() {
		List<String> fileNames = new ArrayList<String>();
		for (File file : new File(directory).listFiles()) {
			if (file.getName().endsWith(".log") || file.getName().endsWith("_log") || file.getName().equals("catalina.out")) {
				fileNames.add(file.getName());
			}
		}
		return fileNames;
	}

	public String getDirectory() {
		return directory;
	}

	public File getLogFile(String fileName) {
		File file;
		if (!directory.endsWith(File.separator)) {
			file = new File(directory + File.separator + fileName);
		} else {
			file = new File(directory + fileName);
		}
		if (!file.exists()) {
			return null;
		}
		return file;
	}
}
