/*******************************************************************************
 * Copyright (c) 2010, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.identifier.massspectrum;

import java.util.List;

/**
 * This interfaces declares some methods to add and remove respectively get the
 * targets of a peak.
 * 
 * @author eselmeister
 */
public interface IMassSpectrumTargets {

	/**
	 * Add a target to the mass spectrum.
	 * 
	 * @param peakTarget
	 */
	void addTarget(IScanTargetMSD massSpectrumTarget);

	/**
	 * Remove a target from the mass spectrum.
	 * 
	 * @param peakTarget
	 */
	void removeTarget(IScanTargetMSD massSpectrumTarget);

	/**
	 * Remove the targets from the mass spectrum
	 * 
	 * @param peakTarget
	 */
	void removeTargets(List<IScanTargetMSD> targetsToDelete);

	/**
	 * Removes all targets from the mass spectrum.
	 */
	void removeAllTargets();

	/**
	 * Returns a list of all targets from the actual mass spectrum.
	 * 
	 * @return List<IMassSpectrumTarget>
	 */
	List<IScanTargetMSD> getTargets();
}
