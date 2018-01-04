/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.comparator;

import java.util.Comparator;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.support.comparator.SortOrder;

public class TargetManuallyVerifiedComparator implements Comparator<IIdentificationTarget> {

	private SortOrder sortOrder;

	public TargetManuallyVerifiedComparator() {
		sortOrder = SortOrder.ASC;
	}

	public TargetManuallyVerifiedComparator(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public int compare(IIdentificationTarget identificationTarget1, IIdentificationTarget identificationTarget2) {

		if(identificationTarget1 == null || identificationTarget2 == null) {
			return 0;
		}
		//
		boolean isManuallyVerified1 = identificationTarget1.isManuallyVerified();
		boolean isManuallyVerified2 = identificationTarget2.isManuallyVerified();
		//
		int returnValue;
		switch(sortOrder) {
			case ASC:
				returnValue = Boolean.compare(isManuallyVerified1, isManuallyVerified2);
				break;
			case DESC:
				returnValue = Boolean.compare(isManuallyVerified2, isManuallyVerified1);
				break;
			default:
				returnValue = 0;
				break;
		}
		return returnValue;
	}
}
