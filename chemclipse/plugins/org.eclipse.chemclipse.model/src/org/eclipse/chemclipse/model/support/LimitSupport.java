/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import java.util.Set;

import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;

public class LimitSupport {

	/**
	 * Returns true if no target is available with >= limit match factor.
	 * 
	 * @param identificationTargets
	 * @param limitMatchFactor
	 * @return boolean
	 */
	public static boolean doIdentify(Set<IIdentificationTarget> identificationTargets, float limitMatchFactor) {

		if(identificationTargets == null) {
			return false;
		}
		//
		for(IIdentificationTarget identificationTarget : identificationTargets) {
			IComparisonResult comparisonResult = identificationTarget.getComparisonResult();
			if(comparisonResult.getMatchFactor() >= limitMatchFactor) {
				return false;
			}
		}
		//
		return true;
	}
}