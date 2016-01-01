/*******************************************************************************
 * Copyright (c) 2015, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.core.identifier.chromatogram;

import java.util.List;

/**
 * This interfaces declares some method to get and set identities of a
 * chromatogram.
 * 
 * @author eselmeister
 */
public interface IChromatogramTargetsCSD {

	/**
	 * Add a target to the chromatogram.
	 * 
	 * @param chromatogramTarget
	 */
	void addTarget(IChromatogramTargetCSD chromatogramTarget);

	/**
	 * Remove a target from the chromatogram.
	 * 
	 * @param chromatogramTarget
	 */
	void removeTarget(IChromatogramTargetCSD chromatogramTarget);

	/**
	 * Remove the targets from the chromatogram.
	 * 
	 * @param targetsToRemove
	 */
	void removeTargets(List<IChromatogramTargetCSD> targetsToRemove);

	/**
	 * Returns a list of all targets from the actual chromatogram.
	 * 
	 * @return List<IChromatogram>
	 */
	List<IChromatogramTargetCSD> getTargets();
}
