/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Daniel Mariano, Rafael Aguayo - additional functionality and UI improvements
 * Jan Holy - initial API and implementation
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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.SamplesSelectionDialog;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class PcaEditor extends AbstractPcaEditor {

	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui/org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors.PcaEditor";
	public static final String ICON_URI = "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/chromatogram.gif";
	public static final String ID = "org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.pcaEditor";
	//
	private static final Logger logger = Logger.getLogger(PcaEditor.class);
	public static final String TOOLTIP = "PCA Editor";
	@Inject
	private MApplication application;
	@Inject
	private MDirtyable dirtyable;
	private ErrorResiduePage errorResiduePage;
	//
	private FiltersPage filtersPage;
	private FormToolkit formToolkit;
	private LoadingPlotPage loadingPlotPage;
	@Inject
	private EModelService modelService;
	private OverviewPage overviewPage;
	private List<Object> pages;
	/*
	 * Injected member in constructor
	 */
	@Inject
	private MPart part;
	private PeakListIntensityTablePage peakListIntensityTablePage;
	/*
	 * Pages
	 */
	private PreprocessingPage preprocessingPage;
	private SamplesOverviewPage samplesOverviewPage;
	private SamplesSelectionDialog samplesSelectionDialog;
	private ScorePlot3dPage scorePlot3dPage;
	private ScorePlotPage scorePlotPage;
	/*
	 * Showing additional info in tabs.
	 */
	private TabFolder tabFolder;

	public PcaEditor() {
		//
		pages = new ArrayList<Object>();
		samplesSelectionDialog = new SamplesSelectionDialog(this);
	}

	@PostConstruct
	private void createControl(Composite parent) {

		createPages(parent);
		Object object = part.getObject();
		if(object instanceof ISamples) {
			ISamples samples = (ISamples)object;
			try {
				setSamples(samples);
				updateSamples();
				evaluateSamples(samples);
				updateResults();
				showScorePlotPage();
				dirtyable.setDirty(true);
			} catch(InvocationTargetException e) {
				logger.warn(e);
				logger.warn(e.getCause());
			} catch(InterruptedException e) {
				logger.warn(e);
			}
		}
	}

	private void createPages(Composite parent) {

		part.setLabel("PCA");
		tabFolder = new TabFolder(parent, SWT.BOTTOM);
		//
		pages.add(overviewPage = new OverviewPage(this, tabFolder, formToolkit));
		pages.add(samplesOverviewPage = new SamplesOverviewPage(this, tabFolder, formToolkit));
		pages.add(preprocessingPage = new PreprocessingPage(this, tabFolder, formToolkit));
		pages.add(filtersPage = new FiltersPage(this, tabFolder, formToolkit));
		pages.add(peakListIntensityTablePage = new PeakListIntensityTablePage(this, tabFolder, formToolkit));
		pages.add(scorePlotPage = new ScorePlotPage(this, tabFolder, formToolkit));
		pages.add(errorResiduePage = new ErrorResiduePage(this, tabFolder, formToolkit));
		pages.add(scorePlot3dPage = new ScorePlot3dPage(this, tabFolder, formToolkit));
		pages.add(loadingPlotPage = new LoadingPlotPage(this, tabFolder, formToolkit));
	}

	@Override
	protected void evaluateSamples(ISamples samples) throws InvocationTargetException, InterruptedException {

		super.evaluateSamples(samples);
	}

	@Override
	public void modifyData() {

		super.modifyData();
		updateSamples();
	}

	public void openSamplesSelectionDialog() {

		samplesSelectionDialog.open();
	}

	@Override
	public int openWizardPcaPeaksInput() {

		int status = Window.CANCEL;
		try {
			status = super.openWizardPcaPeaksInput();
			if(status == Window.OK) {
				updateSamples();
				updateResults();
				showScorePlotPage();
				dirtyable.setDirty(true);
			}
		} catch(InvocationTargetException e) {
			logger.warn(e);
			logger.warn(e.getCause());
		} catch(InterruptedException e) {
			logger.warn(e);
		}
		return status;
	}

	@Override
	public int openWizardPcaScansInput() {

		int status = Window.CANCEL;
		try {
			status = super.openWizardPcaScansInput();
			if(status == Window.OK) {
				updateSamples();
				updateResults();
				showScorePlotPage();
				dirtyable.setDirty(true);
			}
		} catch(InvocationTargetException e) {
			logger.warn(e);
			logger.warn(e.getCause());
		} catch(InterruptedException e) {
			logger.warn(e);
		}
		return status;
	}

	@PreDestroy
	private void preDestroy() {

		/*
		 * Remove the editor from the listed parts.
		 */
		if(modelService != null) {
			MPartStack partStack = (MPartStack)modelService.find(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, application);
			part.setToBeRendered(false);
			part.setVisible(false);
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {

					partStack.getChildren().remove(part);
				}
			});
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

	@Override
	public void reEvaluatePcaCalculation() {

		try {
			super.reEvaluatePcaCalculation();
			updateResults();
			showScorePlotPage();
			dirtyable.setDirty(true);
		} catch(InvocationTargetException e) {
			logger.warn(e);
			logger.warn(e.getCause());
		} catch(InterruptedException e) {
			logger.warn(e);
		}
	}

	@Override
	public void reFiltrationData() {

		super.reFiltrationData();
		updateSamples();
	}

	@Persist
	public void save() {

		File exportFile = null;
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
		/*
		 * Check that there is a valid file.
		 */
		if(exportFile != null) {
			try {
				ResultExport resultExport = new ResultExport();
				resultExport.exportToTextFile(exportFile, getPcaResults().get(), getDataInputEntries());
				dirtyable.setDirty(false);
			} catch(FileNotFoundException e) {
				logger.warn(e);
			}
		}
	}

	@Focus
	public void setFocus() {

		tabFolder.setFocus();
	}

	@Override
	public void setSelectAllData(boolean selection) {

		super.setSelectAllData(selection);
		updateSamples();
	}

	public void showSamplesOverviewPagePage() {

		int pageIndex = 0;
		for(int index = 0; index < pages.size(); index++) {
			if(pages.get(index) == samplesOverviewPage) {
				pageIndex = index;
			}
		}
		tabFolder.setSelection(pageIndex);
	}

	public void showScorePlotPage() {

		int pageIndex = 0;
		for(int index = 0; index < pages.size(); index++) {
			if(pages.get(index) == scorePlotPage) {
				pageIndex = index;
			}
		}
		tabFolder.setSelection(pageIndex);
	}

	private void updateResults() {

		samplesOverviewPage.updateResult();
		scorePlotPage.update();
		errorResiduePage.update();
		scorePlot3dPage.update();
		samplesSelectionDialog.update();
		loadingPlotPage.update();
	}

	public void updateSamples() {

		super.updataGroupNames();
		overviewPage.update();
		samplesOverviewPage.updateSamples();
		preprocessingPage.update();
		filtersPage.update();
		peakListIntensityTablePage.update();
	}

	public void updateSelection() {

		scorePlotPage.updateSelection();
		errorResiduePage.updateSelection();
		scorePlot3dPage.updateSelection();
	}
}
