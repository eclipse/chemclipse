/*******************************************************************************
 * Copyright (c) 2016 Lablicate UG (haftungsbeschränkt).
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

public class IdentificationTargetComparator implements Comparator<IIdentificationTarget> {

	private SortOrder sortOrder;

	public IdentificationTargetComparator() {
		sortOrder = SortOrder.ASC;
	}

	public IdentificationTargetComparator(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public int compare(IIdentificationTarget identificationTarget1, IIdentificationTarget identificationTarget2) {

		if(identificationTarget1 == null || identificationTarget2 == null) {
			return 0;
		}
		//
		float matchFactor1 = identificationTarget1.getComparisonResult().getMatchFactor();
		float matchFactor2 = identificationTarget2.getComparisonResult().getMatchFactor();
		/*
		 * MatchFactor
		 */
		int result = 0;
		int comparisonResult = Float.compare(matchFactor1, matchFactor2);
		if(comparisonResult == 0) {
			/*
			 * ReverseMatchFactor
			 */
			result = compareReverseMatchFactor(identificationTarget1, identificationTarget2);
		} else {
			result = comparisonResult;
		}
		/*
		 * ASC or DESC
		 */
		if(sortOrder.equals(SortOrder.DESC)) {
			result *= -1;
		}
		//
		return result;
	}

	private int compareReverseMatchFactor(IIdentificationTarget identificationTarget1, IIdentificationTarget identificationTarget2) {

		float reverseMatchFactor1 = identificationTarget1.getComparisonResult().getReverseMatchFactor();
		float reverseMatchFactor2 = identificationTarget2.getComparisonResult().getReverseMatchFactor();
		/*
		 * Compare
		 */
		return Float.compare(reverseMatchFactor1, reverseMatchFactor2);
	}
}
