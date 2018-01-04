/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.notifier;

import org.eclipse.chemclipse.csd.model.core.IPeakCSD;

public interface IPeakCSDSelectionUpdateNotifier {

	/**
	 * If some changes have been occurred, this method will inform about the
	 * changed peak selection instance.<br/>
	 * Each implementing class has to decide, which information of the selection
	 * should be used (draw the chromatogram?, draw the scan? ...).<br/>
	 * Use forceUpdate to inform the listeners that substantial information has
	 * been changed.<br/>
	 * Why and when use it? Think of GUI elements which store representations of
	 * the chromatogram model.<br/>
	 * In a normal retention time selection change, the model do not need to be
	 * reloaded.<br/>
	 * But if the chromatogram itself has been modified (background remove), the
	 * GUI model needs to be reloaded to show correct values.
	 * 
	 * @param IPeakMSD
	 * @param forceReload
	 */
	void update(IPeakCSD peakFID, boolean forceReload);
}
