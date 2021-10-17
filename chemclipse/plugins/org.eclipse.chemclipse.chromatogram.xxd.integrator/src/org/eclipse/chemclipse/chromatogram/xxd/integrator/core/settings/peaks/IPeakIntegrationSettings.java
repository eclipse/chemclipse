/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.IIntegrationSettings;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;

public interface IPeakIntegrationSettings extends IIntegrationSettings {

	/**
	 * Adds a report decider. A {@link IReportDecider} can set values at which a
	 * peak will not be reported.
	 * 
	 * @param reportDecider
	 */
	void addReportDecider(IReportDecider reportDecider);

	/**
	 * Removes a report decider.
	 * 
	 * @param reportDecider
	 */
	void removeReportDecider(IReportDecider reportDecider);

	/**
	 * Returns the selected ions.<br/>
	 * If the list is empty or a signal IIon.TIC_Ion is stored the TIC
	 * (total ion chromatogram will be integrated).<br/>
	 * If the IIon.TIC_Ion is stored, the TIC signal will be integrated,
	 * independently if there are other ions stored.
	 * 
	 * @return {@link IMarkedIons}
	 */
	IMarkedIons getSelectedIons();

	/**
	 * Returns an IAreaSupport instance.<br/>
	 * You can edit in the area support, which parts of the integrated peak
	 * areas should be summed.
	 * 
	 * @return IAreaSupport
	 */
	IAreaSupport getAreaSupport();

	/**
	 * Returns the {@link IIntegrationSupport} instance.<br/>
	 * You can edit here, which parts should not be integrated.
	 * 
	 * @return IIntegrationSupport
	 */
	IIntegrationSupport getIntegrationSupport();

	/**
	 * Returns an object which gives information about the setting status of a
	 * particular peak.<br/>
	 * E.g. should the peak be integrated, should its area be summed on.
	 * 
	 * @param peak
	 * @return ISettingStatus
	 */
	ISettingStatus getSettingStatus(IPeak peak);
}
