/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
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

import java.util.List;

import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IMassSpectrumTarget;
import org.eclipse.chemclipse.support.ui.swt.viewers.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.viewers.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class MassSpectrumListTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

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
			case 0: // RT
				sortOrder = massSpectrum2.getRetentionTime() - massSpectrum1.getRetentionTime();
				break;
			case 1: // RI
				sortOrder = Float.compare(massSpectrum2.getRetentionIndex(), massSpectrum1.getRetentionIndex());
				break;
			case 2: // Base Peak
				sortOrder = Double.compare(massSpectrum2.getBasePeak(), massSpectrum1.getBasePeak());
				break;
			case 3: // Base Peak Abundance
				sortOrder = Float.compare(massSpectrum2.getBasePeakAbundance(), massSpectrum1.getBasePeakAbundance());
				break;
			case 4: // Number of Ions
				sortOrder = massSpectrum2.getNumberOfIons() - massSpectrum1.getNumberOfIons();
				break;
			case 5: // Name
				if(libraryInformation1 != null && libraryInformation2 != null) {
					sortOrder = libraryInformation2.getName().compareTo(libraryInformation1.getName());
				}
				break;
			case 6: // CAS
				if(libraryInformation1 != null && libraryInformation2 != null) {
					sortOrder = libraryInformation2.getCasNumber().compareTo(libraryInformation1.getCasNumber());
				}
				break;
			case 7: // MW
				if(libraryInformation1 != null && libraryInformation2 != null) {
					sortOrder = Double.compare(libraryInformation2.getMolWeight(), libraryInformation1.getMolWeight());
				}
				break;
			case 8: // Formula
				if(libraryInformation1 != null && libraryInformation2 != null) {
					sortOrder = libraryInformation2.getFormula().compareTo(libraryInformation1.getFormula());
				}
				break;
			default:
				sortOrder = 0;
		}
		return sortOrder;
	}

	private ILibraryInformation getLibraryInformation(List<IMassSpectrumTarget> targets) {

		ILibraryInformation libraryInformation = null;
		float matchFactor = Float.MIN_VALUE;
		for(IMassSpectrumTarget target : targets) {
			if(target.getComparisonResult().getMatchFactor() > matchFactor) {
				matchFactor = target.getComparisonResult().getMatchFactor();
				libraryInformation = target.getLibraryInformation();
			}
		}
		return libraryInformation;
	}
}
