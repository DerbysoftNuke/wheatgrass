package com.derby.nuke.wheatgrass.rpc.log;

import com.googlecode.jsonrpc4j.JsonRpcService

/**
 * LogService
 *
 * @author Drizzt Yang
 */
@JsonRpcService("/log.ci")
interface LogService {
	List<String> listLogs();

	List<String> tail(String logName, Integer size);

	int lines(String logName);

	boolean isAppend(String logName, Integer lineNumber);

	List<String> next(String logName, Integer lineNumber, Integer size);

	List<String> find(String logName, String pattern, Integer size);

	int findLines(String logName, String pattern);

	List<String> findNext(String logName, String pattern, Integer lineNumber, Integer size);

	int findLines(String logName, List<String> pattern);

	List<String> findNext(String logName, List<String> pattern, Integer lineNumber, Integer size);
}
