/*******************************************************************************
 * Copyright (c) 2010, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Philip
 * (eselmeister) Wenig - initial API and implementation
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
	void addTarget(IMassSpectrumTarget massSpectrumTarget);

	/**
	 * Remove a target from the mass spectrum.
	 * 
	 * @param peakTarget
	 */
	void removeTarget(IMassSpectrumTarget massSpectrumTarget);

	/**
	 * Remove the targets from the mass spectrum
	 * 
	 * @param peakTarget
	 */
	void removeTargets(List<IMassSpectrumTarget> targetsToDelete);

	/**
	 * Removes all targets from the mass spectrum.
	 */
	void removeAllTargets();

	/**
	 * Returns a list of all targets from the actual mass spectrum.
	 * 
	 * @return List<IMassSpectrumTarget>
	 */
	List<IMassSpectrumTarget> getTargets();
}
