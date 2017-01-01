/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.targets;

import java.io.Serializable;
import java.util.List;

/**
 * This interfaces declares some methods to add and remove respectively get the
 * targets of a peak.
 * 
 * @author eselmeister
 */
public interface IPeakTargets extends Serializable {

	/**
	 * Add a target to the peak.
	 * 
	 * @param peakTarget
	 */
	void addTarget(IPeakTarget peakTarget);

	/**
	 * Remove a target from the peak.
	 * 
	 * @param peakTarget
	 */
	void removeTarget(IPeakTarget peakTarget);

	/**
	 * Remove the targets from the peak.
	 * 
	 * @param peakTarget
	 */
	void removeTargets(List<IPeakTarget> targetsToDelete);

	/**
	 * Removes all targets from the peak.
	 */
	void removeAllTargets();

	/**
	 * Returns a list of all targets from the actual peak.
	 * The target could be an instance of IPeakIdentificationEntry.
	 * 
	 * @return List<ITarget>
	 */
	List<IPeakTarget> getTargets();
}
