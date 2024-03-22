/*******************************************************************************
 * Copyright (c) 2010, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - add default delegate methods
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.comparator.IdentificationTargetComparator;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.model.targets.ITarget;
import org.eclipse.chemclipse.support.comparator.SortOrder;

public interface IIdentificationTarget extends ITarget {

	/**
	 * This method may return null.
	 * 
	 * @param peak
	 * @return {@link ILibraryInformation}
	 */
	static ILibraryInformation getLibraryInformation(IPeak peak) {

		IIdentificationTarget identificationTarget = getIdentificationTarget(peak);
		return (identificationTarget != null) ? identificationTarget.getLibraryInformation() : null;
	}

	/**
	 * This method may return null.
	 * 
	 * @param peak
	 * @return {@link IIdentificationTarget}
	 */
	static IIdentificationTarget getIdentificationTarget(IPeak peak) {

		if(peak != null) {
			return getIdentificationTarget(peak.getPeakModel().getPeakMaximum());
		}
		//
		return null;
	}

	/**
	 * This method may return null.
	 * 
	 * @param scan
	 * @return {@link ILibraryInformation}
	 */
	static ILibraryInformation getLibraryInformation(IScan scan) {

		IIdentificationTarget identificationTarget = getIdentificationTarget(scan);
		return (identificationTarget != null) ? identificationTarget.getLibraryInformation() : null;
	}

	/**
	 * This method may return null.
	 * 
	 * @param scan
	 * @return {@link IdentificationTarget}
	 */
	static IIdentificationTarget getIdentificationTarget(IScan scan) {

		if(scan != null) {
			return getIdentificationTarget(scan.getTargets(), scan.getRetentionIndex());
		}
		//
		return null;
	}

	/**
	 * Returns a sorted list of the targets.
	 * 
	 * @param targets
	 * @param retentionIndex
	 * @return {@link List}
	 */
	static List<IIdentificationTarget> getTargetsSorted(Collection<IIdentificationTarget> targets, float retentionIndex) {

		List<IIdentificationTarget> identificationTargets = new ArrayList<>();
		if(targets != null) {
			identificationTargets.addAll(targets);
			IdentificationTargetComparator identificationTargetComparator = new IdentificationTargetComparator(SortOrder.DESC, PreferenceSupplier.isUseRetentionIndexQC() ? retentionIndex : 0.0f);
			Collections.sort(identificationTargets, identificationTargetComparator);
		}
		//
		return identificationTargets;
	}

	/**
	 * This method may return null.
	 * 
	 * @param targets
	 * @param retentionIndex
	 * @return {@link ILibraryInformation}
	 */
	static ILibraryInformation getLibraryInformation(Set<IIdentificationTarget> targets, float retentionIndex) {

		IIdentificationTarget identificationTarget = getIdentificationTarget(targets, retentionIndex);
		if(identificationTarget != null) {
			return identificationTarget.getLibraryInformation();
		}
		//
		return null;
	}

	/**
	 * This method may return null.
	 * 
	 * @param targets
	 * @param retentionIndex
	 * @return {@link IIdentificationTarget}
	 */
	static IIdentificationTarget getIdentificationTarget(Set<IIdentificationTarget> targets, float retentionIndex) {

		if(targets != null && !targets.isEmpty()) {
			IdentificationTargetComparator comparator = new IdentificationTargetComparator(SortOrder.DESC, PreferenceSupplier.isUseRetentionIndexQC() ? retentionIndex : 0.0f);
			return IIdentificationTarget.getIdentificationTarget(targets, comparator);
		}
		//
		return null;
	}

	/**
	 * This method may return null.
	 * 
	 * @param targets
	 * @param comparator
	 * @return {@link IIdentificationTarget}
	 */
	static IIdentificationTarget getIdentificationTarget(Set<IIdentificationTarget> targets, Comparator<IIdentificationTarget> comparator) {

		IIdentificationTarget identificationTarget = null;
		if(targets != null && !targets.isEmpty()) {
			if(targets.size() == 1) {
				identificationTarget = targets.iterator().next();
			} else {
				List<IIdentificationTarget> targetsList = new ArrayList<>(targets);
				if(comparator != null) {
					Collections.sort(targetsList, comparator);
				}
				identificationTarget = targetsList.get(0);
			}
		}
		//
		return identificationTarget;
	}

	static IIdentificationTarget createDefaultTarget(String name, String casNumber, String identifier) {

		return createDefaultTarget(name, casNumber, identifier, IComparisonResult.FACTOR_BEST_MATCH);
	}

	static IIdentificationTarget createDefaultTarget(String name, String casNumber, String identifier, float matchFactor) {

		ILibraryInformation libraryInformation = new LibraryInformation();
		libraryInformation.setName(name);
		libraryInformation.setCasNumber(casNumber);
		libraryInformation.setComments("");
		libraryInformation.setContributor("");
		libraryInformation.setReferenceIdentifier("");
		//
		IComparisonResult comparisonResult = new ComparisonResult(matchFactor, matchFactor, matchFactor, matchFactor);
		IIdentificationTarget identificationTarget = new IdentificationTarget(libraryInformation, comparisonResult);
		identificationTarget.setIdentifier(identifier);
		//
		return identificationTarget;
	}

	/**
	 * Returns the library information instance.
	 * 
	 * @return {@link ILibraryInformation}
	 */
	ILibraryInformation getLibraryInformation();

	/**
	 * Returns the comparison result.
	 * 
	 * @return {@link IComparisonResult}
	 */
	IComparisonResult getComparisonResult();

	/**
	 * Get the identifier, e.g.: 'NIST (extern)'
	 * 
	 * @return String
	 */
	String getIdentifier();

	/**
	 * Caution
	 * ---
	 * The identifier must not be translated. Otherwise, the ILibraryService is
	 * not able to locate the reference mass spectrum for displaying the
	 * unknown/reference mass spectrum in the comparison view.
	 * 
	 * @param identifier
	 */
	void setIdentifier(String identifier);

	/**
	 * Returns if this result has been verified.
	 * 
	 * @return boolean
	 */
	boolean isVerified();

	/**
	 * Set to verify this result.
	 * 
	 * @param verified
	 */
	void setVerified(boolean verified);

	IIdentificationTarget makeDeepCopy();
}