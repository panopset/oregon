package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.LogEvent;

public class JndiLookup extends AbstractLookup {
    static final String CONTAINER_JNDI_RESOURCE_PATH_PREFIX = "java:comp/env/";

	@Override
	public String lookup(LogEvent event, String key) {
        if (key == null) {
            return null;
        }
        final String jndiName = convertJndiName(key);
        return String.format("Honeypot has detected a potential attack, overridden and bypassed: %s", jndiName); 
	}


    /**
     * Convert the given JNDI name to the actual JNDI name to use.
     * Default implementation applies the "java:comp/env/" prefix
     * unless other scheme like "java:" is given.
     * @param jndiName The name of the resource.
     * @return The fully qualified name to look up.
     */
    private String convertJndiName(final String jndiName) {
        if (!jndiName.startsWith(CONTAINER_JNDI_RESOURCE_PATH_PREFIX) && jndiName.indexOf(':') == -1) {
            return CONTAINER_JNDI_RESOURCE_PATH_PREFIX + jndiName;
        }
        return jndiName;
    }
}
