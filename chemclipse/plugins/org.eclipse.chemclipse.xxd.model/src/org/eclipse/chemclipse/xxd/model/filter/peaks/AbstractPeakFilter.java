/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.filter.peaks;

import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public abstract class AbstractPeakFilter<ConfigType> implements IPeakFilter<ConfigType> {

	protected void resetPeakSelection(Object dataContainer) {

		if(dataContainer instanceof IChromatogramSelection<?, ?>) {
			IChromatogramSelection<?, ?> chromatogramSelection = (IChromatogramSelection<?, ?>)dataContainer;
			chromatogramSelection.setSelectedPeak(null);
		}
	}
}