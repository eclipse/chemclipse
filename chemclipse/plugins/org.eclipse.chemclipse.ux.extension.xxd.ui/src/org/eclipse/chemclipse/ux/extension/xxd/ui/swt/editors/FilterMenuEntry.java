/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.csd.filter.core.chromatogram.ChromatogramFilterCSD;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.ChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.ChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.wsd.filter.core.chromatogram.ChromatogramFilterWSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.menu.AbstractChartMenuEntry;
import org.eclipse.swtchart.extensions.menu.IChartMenuEntry;

public class FilterMenuEntry extends AbstractChartMenuEntry implements IChartMenuEntry {

	private ExtendedChromatogramUI extendedChromatogramUI;
	private String name;
	private String filterId;
	private String type;
	@SuppressWarnings("rawtypes")
	private IChromatogramSelection chromatogramSelection;

	@SuppressWarnings("rawtypes")
	public FilterMenuEntry(ExtendedChromatogramUI extendedChromatogramUI, String name, String filterId, String type, IChromatogramSelection chromatogramSelection) {

		this.extendedChromatogramUI = extendedChromatogramUI;
		this.name = name;
		this.filterId = filterId;
		this.type = type;
		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public String getCategory() {

		return "Filter";
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

					IProcessingInfo<?> processingInfo = null;
					switch(type) {
						case ExtendedChromatogramUI.TYPE_GENERIC:
							processingInfo = ChromatogramFilter.applyFilter(chromatogramSelection, filterId, monitor);
							break;
						case ExtendedChromatogramUI.TYPE_MSD:
							if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
								IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
								processingInfo = ChromatogramFilterMSD.applyFilter(chromatogramSelectionMSD, filterId, monitor);
							}
							break;
						case ExtendedChromatogramUI.TYPE_CSD:
							if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
								IChromatogramSelectionCSD chromatogramSelectionCSD = (IChromatogramSelectionCSD)chromatogramSelection;
								processingInfo = ChromatogramFilterCSD.applyFilter(chromatogramSelectionCSD, filterId, monitor);
							}
							break;
						case ExtendedChromatogramUI.TYPE_WSD:
							if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
								IChromatogramSelectionWSD chromatogramSelectionWSD = (IChromatogramSelectionWSD)chromatogramSelection;
								processingInfo = ChromatogramFilterWSD.applyFilter(chromatogramSelectionWSD, filterId, monitor);
							}
							break;
					}
					if(processingInfo != null && !processingInfo.getMessages().isEmpty()) {
						ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, false);
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