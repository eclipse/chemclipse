/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.core.identifier.scan;

import java.util.List;

/**
 * This interfaces declares some method to get and set identities of a
 * scan.
 *
 */
public interface IScanTargetsCSD {

	/**
	 * Add a target to the scan.
	 * 
	 * @param scanTarget
	 */
	void addTarget(IScanTargetCSD scanTarget);

	/**
	 * Remove a target from the scan.
	 * 
	 * @param scanTarget
	 */
	void removeTarget(IScanTargetCSD scanTarget);

	/**
	 * Remove the targets from the scan.
	 * 
	 * @param targetsToRemove
	 */
	void removeTargets(List<IScanTargetCSD> targetsToRemove);

	/**
	 * Returns a list of all targets from the actual scan.
	 * 
	 * @return List<IChromatogram>
	 */
	List<IScanTargetCSD> getTargets();
}
