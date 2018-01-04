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

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.support.comparator.SortOrder;

public class TargetCombinedComparator implements Comparator<IIdentificationTarget>, Serializable {

	private static final long serialVersionUID = -6174844361574013014L;
	//
	private final Comparator<IIdentificationTarget> firstComparator;
	private final Comparator<IIdentificationTarget> secondComparator;

	/**
	 * The sort order is per default value ascending.
	 */
	public TargetCombinedComparator() {
		this(SortOrder.ASC);
	}

	public TargetCombinedComparator(SortOrder sortOrder) {
		firstComparator = new TargetMatchFactorComparator(sortOrder);
		secondComparator = new TargetReverseMatchFactorComparator(sortOrder);
	}

	@Override
	public int compare(IIdentificationTarget identificationTarget1, IIdentificationTarget identificationTarget2) {

		int result = firstComparator.compare(identificationTarget1, identificationTarget2);
		if(result != 0) {
			return result;
		}
		result = secondComparator.compare(identificationTarget1, identificationTarget2);
		return result;
	}
}
