/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.support;

/**
 * Represents user preference for a processor
 * 
 * @author Christoph Läubrich
 *
 */
public interface ProcessorPreferences {

	/**
	 * 
	 * @return <code>true</code> if the user should be queried for the settings each time
	 */
	boolean isAskForSettings();

	void setAskForSettings(boolean askForSettings);

	/**
	 * 
	 * @return the currently stored usersettings for this processor
	 */
	String getUserSettings();

	void setUserSettings(String settings);

	/**
	 * 
	 * @return <code>true</code> if this processor want to use system settings by default
	 */
	boolean isUseSystemDefaults();

	void setUseSystemDefaults(boolean useSystemDefaults);

	/**
	 * reset this preferences deleting all saved values
	 */
	void reset();
}
