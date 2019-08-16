/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.core.PeakQuantifier;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.menu.AbstractChartMenuEntry;
import org.eclipse.swtchart.extensions.menu.IChartMenuEntry;

public class PeakQuantifierMenuEntry extends AbstractChartMenuEntry implements IChartMenuEntry {

	private ExtendedChromatogramUI extendedChromatogramUI;
	private String name;
	private String peakQuantifierId;
	private String type;
	@SuppressWarnings("rawtypes")
	private IChromatogramSelection chromatogramSelection;

	@SuppressWarnings("rawtypes")
	public PeakQuantifierMenuEntry(ExtendedChromatogramUI extendedChromatogramUI, String name, String peakQuantifierId, String type, IChromatogramSelection chromatogramSelection) {

		this.extendedChromatogramUI = extendedChromatogramUI;
		this.name = name;
		this.peakQuantifierId = peakQuantifierId;
		this.type = type;
		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public String getCategory() {

		return "Peak Quantifier";
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public void execute(Shell shell, ScrollableChart scrollableChart) {

		if(chromatogramSelection != null) {
			/*
			 * Create the runnable.
			 */
			IRunnableWithProgress runnable = new IRunnableWithProgress() {

				@SuppressWarnings("unchecked")
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

					switch(type) {
						case ExtendedChromatogramUI.TYPE_GENERIC:
							List<IPeak> peaks = extractPeaks(chromatogramSelection);
							PeakQuantifier.quantify(peaks, peakQuantifierId, monitor);
							break;
					}
				}
			};
			/*
			 * Execute
			 */
			extendedChromatogramUI.processChromatogram(runnable);
		}
	}

	private List<IPeak> extractPeaks(IChromatogramSelection<? extends IPeak, ?> chromatogramSelection) {

		IChromatogram<? extends IPeak> chromatogram = chromatogramSelection.getChromatogram();
		List<IPeak> peaks = new ArrayList<>();
		for(IPeak peak : chromatogram.getPeaks(chromatogramSelection)) {
			peaks.add(peak);
		}
		return peaks;
	}
}
