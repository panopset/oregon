package com.panopset.ashland;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class VulnerableClass {

	void processInsecureValue(String insec) {
		if (insec == null) {
			return;
		}
		if (insec.length() < 1) {
			return;
		}
		processPopulatedInsecureValue(insec);
	}

	/**
	 * Here is where we do what is most feared, use a pre 2.15.0 version of Log4j to
	 * log the contents of form data.
	 * 
	 * @param insec Insecure parameter from the form.
	 */
	private void processPopulatedInsecureValue(String insec) {
		Logger logger = LogManager.getLogger(this.getClass());
		logger.info(insec);
	}
}
