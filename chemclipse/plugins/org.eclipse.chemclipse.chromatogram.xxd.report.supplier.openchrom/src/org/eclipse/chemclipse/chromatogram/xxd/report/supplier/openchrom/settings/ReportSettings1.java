/*******************************************************************************
 * Copyright (c) 2012, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - extend configuration
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.settings;

import org.eclipse.chemclipse.chromatogram.xxd.report.settings.AbstractChromatogramReportSettings;

public class ReportSettings1 extends AbstractChromatogramReportSettings {

	@Override
	protected String getDefaultFolder() {

		return org.eclipse.chemclipse.xxd.process.preferences.PreferenceSupplier.getReportExportFolder();
	}
}
