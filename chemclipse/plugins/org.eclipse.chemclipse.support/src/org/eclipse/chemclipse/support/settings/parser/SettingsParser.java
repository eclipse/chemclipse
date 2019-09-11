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
package org.eclipse.chemclipse.support.settings.parser;

import java.util.List;

import org.eclipse.chemclipse.support.settings.SystemSettingsStrategy;

public interface SettingsParser {

	SystemSettingsStrategy getSystemSettingsStrategy();

	List<InputValue> getInputValues();

	boolean requiresUserSettings();
}
