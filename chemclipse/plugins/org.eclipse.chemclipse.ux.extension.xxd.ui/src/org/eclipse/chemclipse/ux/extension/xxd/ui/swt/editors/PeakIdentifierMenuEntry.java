/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.csd.identifier.peak.PeakIdentifierCSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.PeakIdentifierMSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.menu.AbstractChartMenuEntry;
import org.eclipse.swtchart.extensions.menu.IChartMenuEntry;

public class PeakIdentifierMenuEntry extends AbstractChartMenuEntry implements IChartMenuEntry {

	private ExtendedChromatogramUI extendedChromatogramUI;
	private String name;
	private String peakIdentifierId;
	private String type;
	@SuppressWarnings("rawtypes")
	private IChromatogramSelection chromatogramSelection;

	@SuppressWarnings("rawtypes")
	public PeakIdentifierMenuEntry(ExtendedChromatogramUI extendedChromatogramUI, String name, String peakIdentifierId, String type, IChromatogramSelection chromatogramSelection) {
		this.extendedChromatogramUI = extendedChromatogramUI;
		this.name = name;
		this.peakIdentifierId = peakIdentifierId;
		this.type = type;
		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public String getCategory() {

		return "Peak Identifier";
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

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

					switch(type) {
						case ExtendedChromatogramUI.TYPE_MSD:
							if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
								IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
								PeakIdentifierMSD.identify(chromatogramSelectionMSD, peakIdentifierId, monitor);
							}
							break;
						case ExtendedChromatogramUI.TYPE_CSD:
							if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
								IChromatogramSelectionCSD chromatogramSelectionCSD = (IChromatogramSelectionCSD)chromatogramSelection;
								PeakIdentifierCSD.identify(chromatogramSelectionCSD, peakIdentifierId, monitor);
							}
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
}
