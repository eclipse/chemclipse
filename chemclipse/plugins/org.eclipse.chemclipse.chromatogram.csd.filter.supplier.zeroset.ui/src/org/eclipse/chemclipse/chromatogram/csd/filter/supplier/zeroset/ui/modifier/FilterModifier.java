/*******************************************************************************
 * Copyright (c) 2014, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.csd.filter.supplier.zeroset.ui.modifier;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;

public class FilterModifier implements IRunnableWithProgress {

	private IChromatogramSelectionCSD chromatogramSelection;

	public FilterModifier(IChromatogramSelectionCSD chromatogramSelection) {
		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		/*
		 * THIS IS A TEST CASE, PLEASE COMBINE WITH FILTER EXTENSION POINT!
		 */
		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		float minSignal = chromatogram.getMinSignal();
		if(minSignal < 0) {
			float delta = minSignal * -1;
			for(IScan scan : chromatogram.getScans()) {
				float adjustedIntensity = scan.getTotalSignal() + delta;
				scan.adjustTotalSignal(adjustedIntensity);
			}
		}
		/*
		 * Update the retention times.
		 */
		chromatogramSelection.reset();
		chromatogramSelection.update(true);
	}
}
