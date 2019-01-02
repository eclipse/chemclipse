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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.model.quantitation.IResponseSignal;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class QuantResponseTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IResponseSignal && e2 instanceof IResponseSignal) {
			IResponseSignal entry1 = (IResponseSignal)e1;
			IResponseSignal entry2 = (IResponseSignal)e2;
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = Double.compare(entry2.getSignal(), entry1.getSignal());
					break;
				case 1:
					sortOrder = Double.compare(entry2.getConcentration(), entry1.getConcentration());
					break;
				case 2:
					sortOrder = Double.compare(entry2.getResponse(), entry1.getResponse());
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
