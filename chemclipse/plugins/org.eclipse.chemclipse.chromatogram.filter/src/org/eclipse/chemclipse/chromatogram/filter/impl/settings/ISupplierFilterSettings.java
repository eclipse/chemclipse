/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.impl.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;

public interface ISupplierFilterSettings extends IChromatogramFilterSettings {

	double getStartRetentionTimeMinutes();

	void setStartRetentionTimeMinutes(double startRetentionTimeMinutes);

	double getStopRetentionTimeMinutes();

	void setStopRetentionTimeMinutes(double stopRetentionTimeMinutes);
}
