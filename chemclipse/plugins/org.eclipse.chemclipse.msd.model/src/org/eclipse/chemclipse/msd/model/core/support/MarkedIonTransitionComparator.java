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
package org.eclipse.chemclipse.msd.model.core.support;

import java.util.Comparator;

import org.eclipse.chemclipse.support.comparator.SortOrder;

public class MarkedIonTransitionComparator implements Comparator<IMarkedIonTransition> {

	private SortOrder sortOrder = SortOrder.ASC;

	public MarkedIonTransitionComparator(SortOrder sortOrder) {
		if(sortOrder != null) {
			this.sortOrder = sortOrder;
		}
	}

	@Override
	public int compare(IMarkedIonTransition transition1, IMarkedIonTransition transition2) {

		int result = 0;
		if(transition1 == null || transition2 == null) {
			return 0;
		}
		if(transition1.getIonTransition() == transition2.getIonTransition()) {
			result = 0;
		}
		switch(sortOrder) {
			case ASC:
				result = (int)(transition2.getIonTransition().getQ1StartIon() - transition1.getIonTransition().getQ1StartIon());
				break;
			case DESC:
				result = (int)(transition1.getIonTransition().getQ1StartIon() - transition2.getIonTransition().getQ1StartIon());
				break;
			default:
				result = (int)(transition2.getIonTransition().getQ1StartIon() - transition1.getIonTransition().getQ1StartIon());
		}
		return result;
	}
}
