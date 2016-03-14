package com.derby.nuke.wheatgrass.rpc.log;

import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * LinuxLogService
 *
 * @author Drizzt Yang
 */
@Service
class LinuxLogService extends LogServiceBase implements LogService {

	private CommandExecutor commandExecutor = new CommandExecutor();

	@Autowired
	public LinuxLogService(@Value('${log.file.path:}') String logFilePath) {
		super(new File(logFilePath).getParent());
	}

	@Override
	public List<String> tail(String logName, Integer size) {
		File logFile = getLogFile(logName);
		if (logFile == null) {
			return null;
		}
		String logFilePath = getLogFile(logName).getPath();
		String result = commandExecutor.execute("tail -" + size + " " + logFilePath);
		return commandExecutor.result2Lines(result, null);
	}

	@Override
	public int lines(String logName) {
		File logFile = getLogFile(logName);
		if (logFile == null) {
			return 0;
		}
		return lines4File(logFile.getPath());
	}

	@Override
	public boolean isAppend(String logName, Integer lineNumber) {
		File logFile = getLogFile(logName);
		if (logFile == null) {
			return false;
		}
		int lines = lines4File(logFile.getPath());
		return lineNumber < lines;
	}

	@Override
	public List<String> next(String logName, Integer lineNumber, Integer size) {
		File logFile = getLogFile(logName);
		if (logFile == null) {
			return null;
		}
		String logFilePath = getLogFile(logName).getPath();
		int start = lineNumber + 1;
		int end = lineNumber + size;
		String result = commandExecutor.execute("sed -n '" + start + "," + end + "p' " + logFilePath);
		return commandExecutor.result2Lines(result, null);
	}

	@Override
	public List<String> find(String logName, String pattern, Integer size) {
		File logFile = getLogFile(logName);
		if (logFile == null) {
			return null;
		}
		String logFilePath = getLogFile(logName).getPath();
		String result = commandExecutor.execute("grep -n '" + pattern + "' " + logFilePath + " | head -" + size);
		return commandExecutor.result2Lines(result, null);
	}

	@Override
	public List<String> findNext(String logName, String pattern, Integer lineNumber, Integer size) {
		File logFile = getLogFile(logName);
		if (logFile == null) {
			return null;
		}
		String logFilePath = getLogFile(logName).getPath();
		int start = lineNumber + 1;
		int end = lineNumber + size;
		String result = commandExecutor.execute("grep -n '" + pattern + "' " + logFilePath + " | " + "sed -n '" + start + "," + end + "p' ");
		return commandExecutor.result2Lines(result, null);
	}

	@Override
	public int findLines(String logName, String pattern) {
		File logFile = getLogFile(logName);
		if (logFile == null) {
			return 0;
		}
		String logFilePath = getLogFile(logName).getPath();
		String result = commandExecutor.execute("grep '" + pattern + "' " + logFilePath + " | wc -l");
		return Integer.parseInt(result.trim());
	}

	@Override
	public int findLines(String logName, List<String> patterns) {
		File logFile = getLogFile(logName);
		if (logFile == null) {
			return 0;
		}
		if(patterns.size() < 1) {
			return 0;
		}
		if(patterns.size() < 2) {
			return findLines(logName, patterns.get(0));
		}
		String logFilePath = getLogFile(logName).getPath();

		String result = commandExecutor.execute("grep '" + patterns.get(0) + "' " +  logFilePath
				+ " | " + "grep '" + StringUtils.join(patterns.subList(1, patterns.size()), "' | grep '")  + "' "
				+ "| " + "wc -l");
		return Integer.parseInt(result.trim());
	}

	@Override
	public List<String> findNext(String logName, List<String> patterns, Integer lineNumber, Integer size) {
		File logFile = getLogFile(logName);
		if (logFile == null) {
			return null;
		}
		if(patterns.size() < 1) {
			return new ArrayList<String>();
		}
		if(patterns.size() < 2) {
			return findNext(logName, patterns.get(0), lineNumber, size);
		}
		String logFilePath = getLogFile(logName).getPath();
		int start = lineNumber + 1;
		int end = lineNumber + size;
		String result = commandExecutor.execute("grep -n '" + patterns.get(0) + "' " +  logFilePath
				+ " | " + "grep '" + StringUtils.join(patterns.subList(1, patterns.size()), "' | grep '")  + "' "
				+ " | " + "sed -n '" + start + "," + end + "p' ");
		return commandExecutor.result2Lines(result, null);
	}

	private int lines4File(String logFilePath) {
		String result = commandExecutor.execute("wc -lcw " + logFilePath);
		List<List<String>> datas = commandExecutor.result2Cols(result, null, 4);
		return Integer.parseInt(datas.get(0).get(0));
	}

	public void setCommandExecutor(CommandExecutor commandExecutor) {
		this.commandExecutor = commandExecutor;
	}
}
