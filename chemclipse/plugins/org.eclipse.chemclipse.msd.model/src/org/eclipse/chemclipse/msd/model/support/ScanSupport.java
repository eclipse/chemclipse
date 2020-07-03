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
package org.eclipse.chemclipse.msd.model.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class ScanSupport {

	public static String getSortedTraces(IScanMSD scanMSD) {

		StringBuilder builder = new StringBuilder();
		/*
		 * Get the ions.
		 */
		List<Integer> ions = new ArrayList<Integer>();
		for(IIon ion : scanMSD.getIons()) {
			ions.add((int)Math.round(ion.getIon()));
		}
		/*
		 * Get the list.
		 */
		Collections.sort(ions);
		Iterator<Integer> iterator = ions.iterator();
		while(iterator.hasNext()) {
			builder.append(Integer.toString(iterator.next()));
			if(iterator.hasNext()) {
				builder.append(" ");
			}
		}
		//
		return builder.toString();
	}
}
