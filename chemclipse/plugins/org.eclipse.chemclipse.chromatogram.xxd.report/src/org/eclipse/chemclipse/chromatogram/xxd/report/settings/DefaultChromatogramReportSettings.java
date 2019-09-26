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
package org.eclipse.chemclipse.chromatogram.xxd.report.settings;

import org.eclipse.chemclipse.chromatogram.xxd.report.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.SystemSettings;
import org.eclipse.chemclipse.support.settings.SystemSettingsStrategy;

@SystemSettings(value = SystemSettingsStrategy.DYNAMIC, dynamicCheckMethod = "getSystemSettingsStrategy")
public class DefaultChromatogramReportSettings extends AbstractChromatogramReportSettings {

	@Override
	protected String getDefaultFolder() {

		return PreferenceSupplier.getReportExportFolder();
	}

	/**
	 * Method that check if systemsettings are available, this is used in conjunction with the SystemSettings annotation but can also be called by user code
	 * 
	 * @return
	 */
	public static SystemSettingsStrategy getSystemSettingsStrategy() {

		String folder = PreferenceSupplier.getReportExportFolder();
		if(folder != null && !folder.isEmpty()) {
			return SystemSettingsStrategy.NEW_INSTANCE;
		} else {
			return SystemSettingsStrategy.NONE;
		}
	}
}
