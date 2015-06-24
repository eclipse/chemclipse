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
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.provider;

import org.eclipse.jface.viewers.Viewer;

import org.eclipse.chemclipse.database.model.IDatabaseProxy;
import org.eclipse.chemclipse.swt.ui.viewers.AbstractRecordTableComparator;
import org.eclipse.chemclipse.swt.ui.viewers.IRecordTableComparator;

public class DatabaseTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IDatabaseProxy && e2 instanceof IDatabaseProxy) {
			IDatabaseProxy proxy1 = (IDatabaseProxy)e1;
			IDatabaseProxy proxy2 = (IDatabaseProxy)e2;
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
