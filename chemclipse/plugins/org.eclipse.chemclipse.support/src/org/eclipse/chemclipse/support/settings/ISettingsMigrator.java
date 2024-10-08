/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.settings;

public interface ISettingsMigrator {

	/**
	 * Transfer the JSON content from a saved session
	 * to the currently used settings set.
	 * 
	 * @param content
	 */
	void transferToLatestVersion(String content);
}