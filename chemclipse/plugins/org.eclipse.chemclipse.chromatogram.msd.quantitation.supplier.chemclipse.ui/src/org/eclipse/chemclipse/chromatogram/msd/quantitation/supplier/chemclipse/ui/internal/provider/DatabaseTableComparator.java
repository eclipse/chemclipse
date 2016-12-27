/*******************************************************************************
 * Copyright (c) 2013, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.provider;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabaseProxy;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class DatabaseTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IQuantDatabaseProxy && e2 instanceof IQuantDatabaseProxy) {
			IQuantDatabaseProxy proxy1 = (IQuantDatabaseProxy)e1;
			IQuantDatabaseProxy proxy2 = (IQuantDatabaseProxy)e2;
			switch(getPropertyIndex()) {
				case 0: // Name
					sortOrder = proxy2.getDatabaseName().compareTo(proxy1.getDatabaseName());
					break;
				case 1: // URL
					sortOrder = proxy2.getDatabaseUrl().compareTo(proxy1.getDatabaseUrl());
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
