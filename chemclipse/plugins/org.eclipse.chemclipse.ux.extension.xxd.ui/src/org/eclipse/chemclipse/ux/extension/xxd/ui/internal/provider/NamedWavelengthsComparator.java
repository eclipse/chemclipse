/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Matthias Mailänder - adapted for DAD
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.model.wavelengths.NamedWavelength;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class NamedWavelengthsComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof NamedWavelength && e2 instanceof NamedWavelength) {
			//
			NamedWavelength namedWavelength1 = (NamedWavelength)e1;
			NamedWavelength namedWavelength2 = (NamedWavelength)e2;
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = namedWavelength2.getIdentifier().compareTo(namedWavelength1.getIdentifier());
					break;
				case 1:
					sortOrder = namedWavelength2.getWavelengths().compareTo(namedWavelength1.getWavelengths());
					break;
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}
