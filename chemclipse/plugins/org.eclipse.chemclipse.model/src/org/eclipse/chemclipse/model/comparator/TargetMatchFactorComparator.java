/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.comparator;

import java.util.Comparator;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.support.comparator.SortOrder;

public class TargetMatchFactorComparator implements Comparator<IIdentificationTarget> {

	private SortOrder sortOrder;

	public TargetMatchFactorComparator() {
		sortOrder = SortOrder.ASC;
	}

	public TargetMatchFactorComparator(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public int compare(IIdentificationTarget identificationTarget1, IIdentificationTarget identificationTarget2) {

		if(identificationTarget1 == null || identificationTarget1.getComparisonResult() == null) {
			return 0;
		}
		//
		if(identificationTarget2 == null || identificationTarget2.getComparisonResult() == null) {
			return 0;
		}
		//
		float matchFactor1 = identificationTarget1.getComparisonResult().getMatchFactor();
		float matchFactor2 = identificationTarget2.getComparisonResult().getMatchFactor();
		//
		int returnValue;
		switch(sortOrder) {
			case ASC:
				returnValue = Float.compare(matchFactor1, matchFactor2);
				break;
			case DESC:
				returnValue = Float.compare(matchFactor2, matchFactor1);
				break;
			default:
				returnValue = 0;
				break;
		}
		return returnValue;
	}
}
