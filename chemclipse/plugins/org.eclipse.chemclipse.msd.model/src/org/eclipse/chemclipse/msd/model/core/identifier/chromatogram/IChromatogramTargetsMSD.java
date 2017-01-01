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
package org.eclipse.chemclipse.msd.model.core.identifier.chromatogram;

import java.util.List;

/**
 * This interfaces declares some method to get and set identities of a
 * chromatogram.
 * 
 * @author eselmeister
 */
public interface IChromatogramTargetsMSD {

	/**
	 * Add a target to the chromatogram.
	 * 
	 * @param chromatogramTarget
	 */
	void addTarget(IChromatogramTargetMSD chromatogramTarget);

	/**
	 * Remove a target from the chromatogram.
	 * 
	 * @param chromatogramTarget
	 */
	void removeTarget(IChromatogramTargetMSD chromatogramTarget);

	/**
	 * Remove the targets from the chromatogram.
	 * 
	 * @param targetsToRemove
	 */
	void removeTargets(List<IChromatogramTargetMSD> targetsToRemove);

	/**
	 * Returns a list of all targets from the actual chromatogram.
	 * 
	 * @return List<IChromatogram>
	 */
	List<IChromatogramTargetMSD> getTargets();
}
