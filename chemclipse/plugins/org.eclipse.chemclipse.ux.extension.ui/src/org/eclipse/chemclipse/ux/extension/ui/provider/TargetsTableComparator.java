/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.provider;

import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.support.ui.swt.viewers.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.viewers.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class TargetsTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		/*
		 * SYNCHRONIZE: TargetsLabelProvider
		 */
		int sortOrder = 0;
		if(e1 instanceof IIdentificationTarget && e2 instanceof IIdentificationTarget) {
			IIdentificationTarget entry1 = (IIdentificationTarget)e1;
			IIdentificationTarget entry2 = (IIdentificationTarget)e2;
			ILibraryInformation libraryInformation1 = entry1.getLibraryInformation();
			IComparisonResult comparisonResult1 = entry1.getComparisonResult();
			ILibraryInformation libraryInformation2 = entry2.getLibraryInformation();
			IComparisonResult comparisonResult2 = entry2.getComparisonResult();
			switch(getPropertyIndex()) {
				case 0: // Name
					sortOrder = libraryInformation2.getName().compareTo(libraryInformation1.getName());
					break;
				case 1: // CAS
					sortOrder = libraryInformation2.getCasNumber().compareTo(libraryInformation1.getCasNumber());
					break;
				case 2: // Match Factor
					sortOrder = Float.compare(comparisonResult2.getMatchFactor(), comparisonResult1.getMatchFactor());
					break;
				case 3: // Reverse Match Factor
					sortOrder = Float.compare(comparisonResult2.getReverseMatchFactor(), comparisonResult1.getReverseMatchFactor());
					break;
				case 4: // Formula
					sortOrder = libraryInformation2.getFormula().compareTo(libraryInformation1.getFormula());
					break;
				case 5: // Mol Weight
					sortOrder = Double.compare(libraryInformation2.getMolWeight(), libraryInformation1.getMolWeight());
					break;
				case 6: // Probability
					sortOrder = Float.compare(comparisonResult2.getProbability(), comparisonResult1.getProbability());
					break;
				case 7: // Advise
					String advise2 = comparisonResult2.getAdvise();
					String advise1 = comparisonResult1.getAdvise();
					if(advise2 != null && advise1 != null) {
						sortOrder = comparisonResult2.getAdvise().compareTo(comparisonResult1.getAdvise());
					}
					break;
				case 8: // Identifier
					sortOrder = entry2.getIdentifier().compareTo(entry1.getIdentifier());
					break;
				case 9: // Miscellaneous
					sortOrder = libraryInformation2.getMiscellaneous().compareTo(libraryInformation1.getMiscellaneous());
					break;
				case 10: // Comments
					sortOrder = libraryInformation2.getComments().compareTo(libraryInformation1.getComments());
					break;
				default:
					sortOrder = 0;
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}
