/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.comparator;

import java.util.Comparator;

import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.comparator.SortOrder;

public class IdentificationTargetComparator implements Comparator<IIdentificationTarget> {

	private SortOrder sortOrder;
	private float retentionIndexSource = 0.0f;

	public IdentificationTargetComparator() {

		this(0.0f);
	}

	public IdentificationTargetComparator(SortOrder sortOrder) {

		this(sortOrder, 0.0f);
	}

	public IdentificationTargetComparator(float retentionIndexSource) {

		this(SortOrder.DESC, retentionIndexSource);
	}

	public IdentificationTargetComparator(SortOrder sortOrder, float retentionIndexSource) {

		this.sortOrder = sortOrder;
		this.retentionIndexSource = retentionIndexSource;
	}

	@Override
	public int compare(IIdentificationTarget identificationTarget1, IIdentificationTarget identificationTarget2) {

		/*
		 * WATCH OUT!
		 * OPPOSITE DIRECTION - DELTA RETENTION INDEX
		 * ---
		 * The value needs to be sorted in the opposite direction, as the lowest deviation is the best.
		 * Hence, it behaves completely different than the other values.
		 */
		int sortIndex = 0;
		if(identificationTarget1 != null && identificationTarget2 != null) {
			/*
			 * Manually verified
			 */
			sortIndex = Boolean.compare(identificationTarget1.isManuallyVerified(), identificationTarget2.isManuallyVerified());
			if(sortIndex == 0) {
				/*
				 * Delta Retention Index
				 */
				ILibraryInformation libraryInformation1 = identificationTarget1.getLibraryInformation();
				ILibraryInformation libraryInformation2 = identificationTarget2.getLibraryInformation();
				float deltaRetentionIndex1 = calculateDeltaRetentionIndex(libraryInformation1);
				float deltaRetentionIndex2 = calculateDeltaRetentionIndex(libraryInformation2);
				sortIndex = Float.compare(deltaRetentionIndex2, deltaRetentionIndex1); // <- see Info
				if(sortIndex == 0) {
					/*
					 * Match Factor
					 */
					IComparisonResult comparisonResult1 = identificationTarget1.getComparisonResult();
					IComparisonResult comparisonResult2 = identificationTarget2.getComparisonResult();
					sortIndex = Float.compare(comparisonResult1.getMatchFactor(), comparisonResult2.getMatchFactor());
					if(sortIndex == 0) {
						/*
						 * Reverse Match Factor
						 */
						sortIndex = Float.compare(comparisonResult1.getReverseMatchFactor(), comparisonResult2.getReverseMatchFactor());
						if(sortIndex == 0) {
							/*
							 * Match Factor Direct
							 */
							sortIndex = Float.compare(comparisonResult1.getMatchFactorDirect(), comparisonResult2.getMatchFactorDirect());
							if(sortIndex == 0) {
								/*
								 * Reverse Match Factor Direct
								 */
								sortIndex = Float.compare(comparisonResult1.getReverseMatchFactorDirect(), comparisonResult2.getReverseMatchFactorDirect());
								if(sortIndex == 0) {
									/*
									 * Probability
									 */
									sortIndex = Float.compare(comparisonResult1.getProbability(), comparisonResult2.getProbability());
								}
							}
						}
					}
				}
			}
		}
		/*
		 * Sort
		 */
		if(SortOrder.DESC.equals(sortOrder) && sortIndex != 0) {
			sortIndex *= -1;
		}
		//
		return sortIndex;
	}

	private float calculateDeltaRetentionIndex(ILibraryInformation libraryInformation) {

		float deltaRI = 0.0f;
		//
		if(PreferenceSupplier.isUseRetentionIndexQC()) {
			if(retentionIndexSource != 0.0f) {
				deltaRI = Math.abs(retentionIndexSource - libraryInformation.getRetentionIndex());
			}
		}
		//
		return deltaRI;
	}
}
