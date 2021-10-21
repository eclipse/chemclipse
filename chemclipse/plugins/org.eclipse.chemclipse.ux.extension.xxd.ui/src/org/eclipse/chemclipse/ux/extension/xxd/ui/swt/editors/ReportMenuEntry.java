/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
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

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.xxd.report.core.ChromatogramReports;
import org.eclipse.chemclipse.chromatogram.xxd.report.core.IChromatogramReportSupplier;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.menu.AbstractChartMenuEntry;
import org.eclipse.swtchart.extensions.menu.IChartMenuEntry;

@SuppressWarnings("rawtypes")
public class ReportMenuEntry extends AbstractChartMenuEntry implements IChartMenuEntry {

	private ExtendedChromatogramUI extendedChromatogramUI;
	private IChromatogramReportSupplier chromatogramReportSupplier;
	private String type;
	//

	public ReportMenuEntry(ExtendedChromatogramUI extendedChromatogramUI, IChromatogramReportSupplier chromatogramReportSupplier, String type) {

		this.extendedChromatogramUI = extendedChromatogramUI;
		this.chromatogramReportSupplier = chromatogramReportSupplier;
		this.type = type;
	}

	@Override
	public String getCategory() {

		return "Reports";
	}

	@Override
	public String getName() {

		return chromatogramReportSupplier.getReportName();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(Shell shell, ScrollableChart scrollableChart) {

		IChromatogramSelection chromatogramSelection = extendedChromatogramUI.getChromatogramSelection();
		if(chromatogramSelection != null) {
			/*
			 * Create the runnable.
			 */
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			File file = getFileFromFileDialog(chromatogram.getName(), chromatogramReportSupplier);
			//
			if(file != null) {
				IRunnableWithProgress runnable = new IRunnableWithProgress() {

					@SuppressWarnings("unchecked")
					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

						switch(type) {
							case ExtendedChromatogramUI.TYPE_GENERIC:
								ChromatogramReports.generate(file, false, chromatogram, chromatogramReportSupplier.getId(), monitor);
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

	private File getFileFromFileDialog(String defaultFileName, IChromatogramReportSupplier chromatogramReportSupplier) {

		FileDialog fileDialog = new FileDialog(DisplayUtils.getShell(), SWT.SAVE);
		fileDialog.setOverwrite(true);
		fileDialog.setText("Report");
		fileDialog.setFileName(defaultFileName);
		fileDialog.setFilterExtensions(new String[]{"*" + chromatogramReportSupplier.getFileExtension()});
		fileDialog.setFilterNames(new String[]{chromatogramReportSupplier.getReportName()});
		String fileName = fileDialog.open();
		if(fileName == null || fileName.equals("")) {
			return null;
		} else {
			return new File(fileName);
		}
	}
}