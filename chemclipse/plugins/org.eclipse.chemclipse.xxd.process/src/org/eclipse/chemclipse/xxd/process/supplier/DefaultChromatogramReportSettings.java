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
package org.eclipse.chemclipse.xxd.process.supplier;

import org.eclipse.chemclipse.chromatogram.xxd.report.settings.AbstractChromatogramReportSettings;
import org.eclipse.chemclipse.xxd.process.preferences.PreferenceSupplier;

public class DefaultChromatogramReportSettings extends AbstractChromatogramReportSettings {

	@Override
	protected String getDefaultFolder() {

		return PreferenceSupplier.getReportExportFolder();
	}
}
