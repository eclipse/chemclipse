/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ScanDataSupport;
import org.eclipse.jface.viewers.Viewer;

public class ScanListTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	private ScanDataSupport scanDataSupport = new ScanDataSupport();

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IScan && e2 instanceof IScan) {
			IScan scan1 = (IScan)e1;
			IScan scan2 = (IScan)e2;
			ILibraryInformation libraryInformation1 = scanDataSupport.getLibraryInformation(scan1);
			ILibraryInformation libraryInformation2 = scanDataSupport.getLibraryInformation(scan2);
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = (libraryInformation1 != null && libraryInformation2 != null) ? libraryInformation2.getName().compareTo(libraryInformation1.getName()) : 0;
					break;
				case 1:
					sortOrder = (libraryInformation1 != null && libraryInformation2 != null) ? Integer.compare(scan2.getRetentionTime(), scan1.getRetentionTime()) : 0;
					break;
				case 2:
					sortOrder = (libraryInformation1 != null && libraryInformation2 != null) ? Integer.compare(scan2.getRelativeRetentionTime(), scan1.getRelativeRetentionTime()) : 0;
					break;
				case 3:
					sortOrder = (libraryInformation1 != null && libraryInformation2 != null) ? Float.compare(scan2.getRetentionIndex(), scan1.getRetentionIndex()) : 0;
					break;
				case 4:
					sortOrder = (libraryInformation1 != null && libraryInformation2 != null) ? libraryInformation2.getCasNumber().compareTo(libraryInformation1.getCasNumber()) : 0;
					break;
				case 5:
					sortOrder = (libraryInformation1 != null && libraryInformation2 != null) ? Double.compare(libraryInformation2.getMolWeight(), libraryInformation1.getMolWeight()) : 0;
					break;
				case 6:
					sortOrder = (libraryInformation1 != null && libraryInformation2 != null) ? libraryInformation2.getFormula().compareTo(libraryInformation1.getFormula()) : 0;
					break;
				case 7:
					sortOrder = (libraryInformation1 != null && libraryInformation2 != null) ? libraryInformation2.getSmiles().compareTo(libraryInformation1.getSmiles()) : 0;
					break;
				case 8:
					sortOrder = (libraryInformation1 != null && libraryInformation2 != null) ? libraryInformation2.getInChI().compareTo(libraryInformation1.getInChI()) : 0;
					break;
				case 9:
					sortOrder = (libraryInformation1 != null && libraryInformation2 != null) ? libraryInformation2.getReferenceIdentifier().compareTo(libraryInformation1.getReferenceIdentifier()) : 0;
					break;
				case 10:
					sortOrder = (libraryInformation1 != null && libraryInformation2 != null) ? libraryInformation2.getComments().compareTo(libraryInformation1.getComments()) : 0;
					break;
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}
