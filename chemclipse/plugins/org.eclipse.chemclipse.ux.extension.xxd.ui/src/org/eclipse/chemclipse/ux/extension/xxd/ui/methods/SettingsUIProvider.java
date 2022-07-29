/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - enable profiles
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.io.IOException;

import org.eclipse.chemclipse.processing.supplier.IProcessorPreferences;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

public interface SettingsUIProvider<SettingType> {

	SettingsUIControl createUI(Composite parent, IProcessorPreferences<SettingType> preferences, boolean showProfileToolbar) throws IOException;

	interface SettingsUIControl {

		void setEnabled(boolean enabled);

		IStatus validate();

		String getSettings() throws IOException;

		void addChangeListener(Listener listener);

		Control getControl();
	}
}
