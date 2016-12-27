/*******************************************************************************
 * Copyright (c) 2008, 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IPeakIntegrationSettings;

/**
 * @author eselmeister
 */
public interface ITrapezoidPeakIntegrationSettings extends IPeakIntegrationSettings {

	/**
	 * Tells whether to include the background in the peak area.
	 * 
	 * @return boolean
	 */
	boolean isIncludeBackground();

	/**
	 * Include the background under peak in the peak area.
	 * This value shall be false by default.
	 * 
	 * @param includeBackground
	 */
	void setIncludeBackground(boolean includeBackground);
}
