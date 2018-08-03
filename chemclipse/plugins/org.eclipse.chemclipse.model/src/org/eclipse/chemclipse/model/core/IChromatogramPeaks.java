/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.util.List;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public interface IChromatogramPeaks<T extends IPeak> {

	void removeAllPeaks();

	int getNumberOfPeaks();

	void addPeak(T peak);

	void removePeak(T peak);

	void removePeaks(List<T> peaksToDelete);

	T getPeak(int retentionTime);

	/**
	 * Returns a list.
	 * Modification does not change the chromatogram peak list.
	 * 
	 * @return List<T>
	 */
	List<T> getPeaks();

	/**
	 * Returns a list.
	 * Modification does not change the chromatogram peak list.
	 * 
	 * @return List<T>
	 */
	List<T> getPeaks(IChromatogramSelection chromatogramSelection);
}
