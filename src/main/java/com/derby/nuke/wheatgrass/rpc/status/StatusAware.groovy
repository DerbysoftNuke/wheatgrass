package com.derby.nuke.wheatgrass.rpc.status;

import java.util.Map;
import java.util.Set;

/**
 * StatusAware
 *
 * @author Drizzt Yang
 */
interface StatusAware {
	String getStatusPrefix();

	Set<String> getStatusKeys();

	Map<String, String> status();
}
