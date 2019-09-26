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
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.io.IOException;

import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

public interface SettingsUIProvider<SettingType> {

	SettingsUIControl createUI(Composite parent, ProcessorPreferences<SettingType> preferences) throws IOException;

	interface SettingsUIControl {

		void setEnabled(boolean enabled);

		IStatus validate();

		String getSettings() throws IOException;

		void addChangeListener(Listener listener);
	}
}
