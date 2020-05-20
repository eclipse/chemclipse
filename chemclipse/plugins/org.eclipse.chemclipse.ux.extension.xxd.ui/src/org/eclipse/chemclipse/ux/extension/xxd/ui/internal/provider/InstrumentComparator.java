/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.model.instruments.Instrument;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class InstrumentComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof Instrument && e2 instanceof Instrument) {
			//
			Instrument instrument1 = (Instrument)e1;
			Instrument instrument2 = (Instrument)e2;
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = instrument2.getIdentifier().compareTo(instrument1.getIdentifier());
					break;
				case 1:
					sortOrder = instrument2.getName().compareTo(instrument1.getName());
					break;
				case 2:
					sortOrder = instrument2.getDescription().compareTo(instrument1.getDescription());
					break;
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}
