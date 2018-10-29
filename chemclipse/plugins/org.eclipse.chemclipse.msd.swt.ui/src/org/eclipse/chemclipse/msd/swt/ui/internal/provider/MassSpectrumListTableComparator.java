/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.internal.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class MassSpectrumListTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	private TargetExtendedComparator targetExtendedComparator;

	public MassSpectrumListTableComparator() {
		targetExtendedComparator = new TargetExtendedComparator(SortOrder.DESC);
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		/*
		 * SYNCHRONIZE: TargetsLabelProvider
		 */
		int sortOrder = 0;
		if(e1 instanceof IRegularLibraryMassSpectrum && e2 instanceof IRegularLibraryMassSpectrum) {
			IRegularLibraryMassSpectrum massSpectrum1 = (IRegularLibraryMassSpectrum)e1;
			IRegularLibraryMassSpectrum massSpectrum2 = (IRegularLibraryMassSpectrum)e2;
			sortOrder = getSortOrder(massSpectrum1, massSpectrum2, massSpectrum1.getLibraryInformation(), massSpectrum2.getLibraryInformation());
		} else if(e1 instanceof IScanMSD && e2 instanceof IScanMSD) {
			IScanMSD massSpectrum1 = (IScanMSD)e1;
			IScanMSD massSpectrum2 = (IScanMSD)e2;
			//
			ILibraryInformation libraryInformation1 = getLibraryInformation(massSpectrum1.getTargets());
			ILibraryInformation libraryInformation2 = getLibraryInformation(massSpectrum2.getTargets());
			/*
			 * Show the optimized mass spectrum if available.
			 */
			if(massSpectrum1.getOptimizedMassSpectrum() != null) {
				massSpectrum1 = massSpectrum1.getOptimizedMassSpectrum();
			}
			//
			if(massSpectrum2.getOptimizedMassSpectrum() != null) {
				massSpectrum2 = massSpectrum2.getOptimizedMassSpectrum();
			}
			sortOrder = getSortOrder(massSpectrum1, massSpectrum2, libraryInformation1, libraryInformation2);
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}

	private int getSortOrder(IScanMSD massSpectrum1, IScanMSD massSpectrum2, ILibraryInformation libraryInformation1, ILibraryInformation libraryInformation2) {

		int sortOrder = 0;
		switch(getPropertyIndex()) {
			case 0: // Name
				if(libraryInformation1 != null && libraryInformation2 != null) {
					sortOrder = libraryInformation2.getName().compareTo(libraryInformation1.getName());
				}
				break;
			case 1: // RT
				sortOrder = Integer.compare(massSpectrum2.getRetentionTime(), massSpectrum1.getRetentionTime());
				break;
			case 2: // RRT
				sortOrder = Integer.compare(massSpectrum2.getRelativeRetentionTime(), massSpectrum1.getRelativeRetentionTime());
				break;
			case 3: // RI
				sortOrder = Float.compare(massSpectrum2.getRetentionIndex(), massSpectrum1.getRetentionIndex());
				break;
			case 4: // Base Peak
				sortOrder = Double.compare(massSpectrum2.getBasePeak(), massSpectrum1.getBasePeak());
				break;
			case 5: // Base Peak Abundance
				sortOrder = Float.compare(massSpectrum2.getBasePeakAbundance(), massSpectrum1.getBasePeakAbundance());
				break;
			case 6: // Number of Ions
				sortOrder = Integer.compare(massSpectrum2.getNumberOfIons(), massSpectrum1.getNumberOfIons());
				break;
			case 7: // CAS
				if(libraryInformation1 != null && libraryInformation2 != null) {
					sortOrder = libraryInformation2.getCasNumber().compareTo(libraryInformation1.getCasNumber());
				}
				break;
			case 8: // MW
				if(libraryInformation1 != null && libraryInformation2 != null) {
					sortOrder = Double.compare(libraryInformation2.getMolWeight(), libraryInformation1.getMolWeight());
				}
				break;
			case 9: // Formula
				if(libraryInformation1 != null && libraryInformation2 != null) {
					sortOrder = libraryInformation2.getFormula().compareTo(libraryInformation1.getFormula());
				}
				break;
			case 10:
				if(libraryInformation1 != null && libraryInformation2 != null) {
					sortOrder = libraryInformation2.getSmiles().compareTo(libraryInformation1.getSmiles());
				}
				break;
			case 11:
				if(libraryInformation1 != null && libraryInformation2 != null) {
					sortOrder = libraryInformation2.getInChI().compareTo(libraryInformation1.getInChI());
				}
				break;
			case 12: // Reference Identifier
				if(libraryInformation1 != null && libraryInformation2 != null) {
					sortOrder = libraryInformation2.getReferenceIdentifier().compareTo(libraryInformation1.getReferenceIdentifier());
				}
				break;
			case 13:
				if(libraryInformation1 != null && libraryInformation2 != null) {
					sortOrder = libraryInformation2.getComments().compareTo(libraryInformation1.getComments());
				}
				break;
			default:
				sortOrder = 0;
		}
		return sortOrder;
	}

	private ILibraryInformation getLibraryInformation(Set<IIdentificationTarget> targets) {

		ILibraryInformation libraryInformation = null;
		List<IIdentificationTarget> targetsList = new ArrayList<>(targets);
		Collections.sort(targetsList, targetExtendedComparator);
		if(targetsList.size() >= 1) {
			libraryInformation = targetsList.get(0).getLibraryInformation();
		}
		return libraryInformation;
	}
}
