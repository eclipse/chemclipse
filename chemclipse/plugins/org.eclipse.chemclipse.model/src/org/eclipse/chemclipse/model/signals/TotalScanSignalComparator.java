/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.signals;

import java.util.Comparator;

public class TotalScanSignalComparator implements Comparator<ITotalScanSignal> {

	@Override
	public int compare(ITotalScanSignal totalIonSignal1, ITotalScanSignal totalIonSignal2) {

		/*
		 * Values must not be null.
		 */
		if(totalIonSignal1 == null || totalIonSignal2 == null) {
			return 0;
		}
		/*
		 * If they are equal return 0.
		 */
		return Float.compare(totalIonSignal1.getTotalSignal(), totalIonSignal2.getTotalSignal());
	}
}
