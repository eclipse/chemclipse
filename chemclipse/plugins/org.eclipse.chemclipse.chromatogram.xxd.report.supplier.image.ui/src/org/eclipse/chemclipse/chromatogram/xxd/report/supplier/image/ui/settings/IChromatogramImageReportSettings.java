/*******************************************************************************
 * Copyright (c) 2012, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.image.ui.settings;

import org.eclipse.chemclipse.chromatogram.xxd.report.settings.IChromatogramReportSettings;

public interface IChromatogramImageReportSettings extends IChromatogramReportSettings {

	int getWidth();

	int getHeight();

	boolean isPeaks();

	boolean isScans();

	ImageFormat getFormat();
}
