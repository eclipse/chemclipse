/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Daniel Mariano, Rafael Aguayo - additional functionality and UI improvements
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.ResultExport;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.runnable.PcaRunnable;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MInputPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.forms.widgets.FormToolkit;

@SuppressWarnings("deprecation")
public class PcaEditor {

	public static final String ID = "org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.pcaEditor";
	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui/org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors.PcaEditor";
	public static final String ICON_URI = "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/chromatogram.gif";
	public static final String TOOLTIP = "PCA Editor";
	/*
	 * TODO protected
	 */
	protected List<IDataInputEntry> dataInputEntries;
	protected PcaResults pcaResults;
	//
	private static final Logger logger = Logger.getLogger(PcaEditor.class);
	/*
	 * Injected member in constructor
	 */
	@Inject
	private MInputPart inputPart;
	@Inject
	private MDirtyable dirtyable;
	@Inject
	private MApplication application;
	@Inject
	private EModelService modelService;
	/*
	 * Showing additional info in tabs.
	 */
	private TabFolder tabFolder;
	private FormToolkit formToolkit;
	/*
	 * Indices of the pages.
	 */
	private OverviewPage overviewPage;
	@SuppressWarnings("unused")
	private InputFilesPage inputFilesPage;
	private PeakListIntensityTablePage peakListIntensityTablePage;
	private ScorePlotPage scorePlotPage;
	private ErrorResiduePage errorResiduePage;
	@SuppressWarnings("unused")
	private ScorePlot3dPage scorePlot3dPage;
	//
	private int scorePlotPageIndex;
	//
	private File exportFile;

	public PcaEditor() {
		//
		dataInputEntries = new ArrayList<IDataInputEntry>();
	}

	@PostConstruct
	private void createControl(Composite parent) {

		createPages(parent);
	}

	@Focus
	public void setFocus() {

	}

	@PreDestroy
	private void preDestroy() {

		/*
		 * Remove the editor from the listed parts.
		 */
		if(modelService != null) {
			MPartStack partStack = (MPartStack)modelService.find(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, application);
			inputPart.setToBeRendered(false);
			inputPart.setVisible(false);
			partStack.getChildren().remove(inputPart);
		}
		/*
		 * Dispose the form toolkit.
		 */
		if(formToolkit != null) {
			formToolkit.dispose();
		}
		/*
		 * Run the garbage collector.
		 */
		System.gc();
	}

	@Persist
	public void save() {

		if(exportFile == null) {
			FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
			fileDialog.setText("Save PCA results");
			fileDialog.setOverwrite(true);
			fileDialog.setFileName("PCA-Results.txt");
			fileDialog.setFilterExtensions(new String[]{"*.txt"});
			fileDialog.setFilterNames(new String[]{"ASCII PCA reports"});
			String pathname = fileDialog.open();
			if(pathname != null) {
				exportFile = new File(pathname);
			}
		}
		/*
		 * Check that there is a valid file.
		 */
		if(exportFile != null) {
			try {
				ResultExport resultExport = new ResultExport();
				resultExport.exportToTextFile(exportFile, pcaResults);
				dirtyable.setDirty(false);
			} catch(FileNotFoundException e) {
				logger.warn(e);
			}
		}
	}

	private void createPages(Composite parent) {

		inputPart.setLabel("PCA");
		tabFolder = new TabFolder(parent, SWT.BOTTOM);
		//
		overviewPage = new OverviewPage(this, tabFolder, formToolkit, 2); // 1
		inputFilesPage = new InputFilesPage(this, tabFolder, formToolkit); // 2
		peakListIntensityTablePage = new PeakListIntensityTablePage(this, tabFolder, formToolkit);
		scorePlotPage = new ScorePlotPage(this, tabFolder, formToolkit);
		errorResiduePage = new ErrorResiduePage(this, tabFolder, formToolkit);
		scorePlot3dPage = new ScorePlot3dPage(this, tabFolder, formToolkit);
	}

	/*
	 * TODO protected
	 */
	protected void runPcaCalculation() {

		dirtyable.setDirty(true);
		/*
		 * Get the settings.
		 */
		int retentionTimeWindow = overviewPage.getRetentionTimeWindow();
		int numberOfPrincipleComponents = overviewPage.getNumberOfPrincipleComponents();
		/*
		 * Run the process.
		 */
		PcaRunnable runnable = new PcaRunnable(dataInputEntries, retentionTimeWindow, numberOfPrincipleComponents, overviewPage.getExtractionType());
		ProgressMonitorDialog monitor = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
		try {
			/*
			 * Calculate the results
			 */
			monitor.run(true, true, runnable);
			pcaResults = runnable.getPcaResults();
			/*
			 * Reload the tables and charts.
			 */
			peakListIntensityTablePage.reloadPeakListIntensityTable();
			scorePlotPage.updateSpinnerPCMaxima();
			scorePlotPage.reloadScorePlotChart();
			errorResiduePage.reloadErrorResidueChart();
			/*
			 * Activate the score plot chart.
			 */
			tabFolder.setSelection(scorePlotPageIndex);
		} catch(InvocationTargetException e) {
			logger.warn(e);
			logger.warn(e.getCause());
		} catch(InterruptedException e) {
			logger.warn(e);
		}
	}
}
