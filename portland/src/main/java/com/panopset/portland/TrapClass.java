package com.panopset.portland;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrapClass {

	void processPotentialAttackValue(String insec) {
		if (insec == null) {
			return;
		}
		if (insec.length() < 1) {
			return;
		}
		processPopulatedPotentialAttackValue(insec);
	}

	/**
	 * Unlike the VulnerableClass in the Ashland server, this class
	 * uses our modified Log4J to capture information about what the
	 * attacker is trying to do, without actually doing it.
	 * 
	 * @param insec Insecure parameter from the form.
	 */
	private void processPopulatedPotentialAttackValue(String insec) {
		Logger logger = LogManager.getLogger(this.getClass());
		logger.info(insec);
	}
}
