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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface ISettingsMigrationHandler<T> {

	default ObjectMapper getObjectMapper() {

		/*
		 * Enable 'fail on unknown properties' to prevent wrong settings to be mapped.
		 */
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		//
		return objectMapper;
	}

	/**
	 * Migrate the JSON settings provided in content to
	 * the supplied settings instance of type T.
	 * If the settings are of wrong version or if it fails return false.
	 * 
	 * @param settings
	 * @param content
	 * @return {@link Boolean}
	 */
	boolean migrateSettings(T settings, String content);
}