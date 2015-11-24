/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.ResultExport;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.DataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.runnable.PcaRunnable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.BatchProcessWizardDialog;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.PeakInputFilesWizard;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.TimeRangeWizard;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.thirdpartylibraries.swtchart.ext.InteractiveChartExtended;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MInputPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.swtchart.ICustomPaintListener;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.IPlotArea;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.ISeriesSet;
import org.swtchart.LineStyle;
import org.swtchart.Range;

@SuppressWarnings("deprecation")
public class PcaEditor {

	public static final String ID = "org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.pcaEditor";
	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui/org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors.PcaEditor";
	public static final String ICON_URI = "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/chromatogram.gif";
	public static final String TOOLTIP = "PCA Editor";
	//
	private Color COLOR_BLACK = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	private Color COLOR_WHITE = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	private Color COLOR_RED = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	private Color COLOR_BLUE = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
	private Color COLOR_MAGENTA = Display.getCurrent().getSystemColor(SWT.COLOR_MAGENTA);
	private Color COLOR_CYAN = Display.getCurrent().getSystemColor(SWT.COLOR_CYAN);
	private Color COLOR_GRAY = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
	private int SYMBOL_SIZE = 8;
	//
	private static final int DEFAULT_RETENTION_TIME_WINDOW = 200;
	//
	private static final Logger logger = Logger.getLogger(PcaEditor.class);
	private static final String FILES = "Input Files: ";
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
	private Table inputFilesTable;
	private Label countFiles;
	private Label tableHeader;
	private Text retentionTimeWindowText;
	private Spinner principleComponentSpinner;
	private Table peakListIntensityTable;
	private InteractiveChartExtended scorePlotChart;
	private InteractiveChartExtended errorResidueChart;
	private Spinner spinnerPCx;
	private Spinner spinnerPCy;
	/*
	 * Indices of the pages.
	 */
	private int inputFilesPageIndex;
	private int scorePlotPageIndex;
	//
	private List<IDataInputEntry> dataInputEntries;
	private PcaResults pcaResults;
	private File exportFile;
	/*
	 * Number format
	 */
	private NumberFormat numberFormat;
	private static final int FRACTION_DIGITS = 3;
	/*
	 * Peak Intensity Table Data
	 */
	private int currentNumberOfPeaks;
	/*
	 * ExtractionType - 0 for peaks, 1 for scans
	 */
	private int extractionType;

	public PcaEditor() {

		dataInputEntries = new ArrayList<IDataInputEntry>();
		/*
		 * Number format of retention times.
		 */
		numberFormat = NumberFormat.getInstance();
		numberFormat.setMinimumFractionDigits(FRACTION_DIGITS);
		numberFormat.setMaximumFractionDigits(FRACTION_DIGITS);
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
		// 0
		createOverviewPage();
		// 1
		inputFilesPageIndex = 1;
		createInputFilesPage();
		// 2
		createPeakListIntensityTablePage();
		// 3
		scorePlotPageIndex = 3;
		createScorePlotPage();
		// 4
		createErrorResiduePage();
	}

	// --------------------------------------------------------------------------------------------------------------Overview Page
	private void createOverviewPage() {

		/*
		 * Miscellaneous Page
		 */
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Overview");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		/*
		 * Forms API
		 */
		formToolkit = new FormToolkit(composite.getDisplay());
		ScrolledForm scrolledForm = formToolkit.createScrolledForm(composite);
		Composite scrolledFormComposite = scrolledForm.getBody();
		formToolkit.decorateFormHeading(scrolledForm.getForm());
		scrolledFormComposite.setLayout(new TableWrapLayout());
		scrolledForm.setText("Principle Component Analysis");
		/*
		 * Add the sections
		 */
		createPropertiesSection(scrolledFormComposite);
		createExecuteSection(scrolledFormComposite);
		//
		tabItem.setControl(composite);
	}

	/**
	 * Creates the properties section.
	 */
	private void createPropertiesSection(Composite parent) {

		/*
		 * Section
		 */
		Section section = formToolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText("Properties");
		section.setDescription("Use the properties to define the retention time window and the number of components.");
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		/*
		 * Client
		 */
		Composite client = formToolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		Label label = formToolkit.createLabel(client, "Select the PCA settings:");
		label.setLayoutData(gridData);
		/*
		 * Settings
		 */
		createRetentionTimeWindowText(client);
		createPrincipleComponentSpinner(client);
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		formToolkit.paintBordersFor(client);
	}

	private void createRetentionTimeWindowText(Composite client) {

		formToolkit.createLabel(client, "Retention Time Window (milliseconds)");
		//
		retentionTimeWindowText = formToolkit.createText(client, Integer.toString(DEFAULT_RETENTION_TIME_WINDOW), SWT.NONE);
		//
		GridData gridData = new GridData();
		gridData.widthHint = 300;
		retentionTimeWindowText.setLayoutData(gridData);
	}

	private void createPrincipleComponentSpinner(Composite client) {

		formToolkit.createLabel(client, "Number of Principle Components");
		//
		principleComponentSpinner = new Spinner(client, SWT.NONE);
		principleComponentSpinner.setMinimum(3);
		principleComponentSpinner.setMaximum(10);
		principleComponentSpinner.setIncrement(1);
		//
		GridData gridData = new GridData();
		gridData.widthHint = 50;
		gridData.heightHint = 20;
		principleComponentSpinner.setLayoutData(gridData);
	}

	/**
	 * Creates the run section.
	 * 
	 * @param parent
	 */
	private void createExecuteSection(Composite parent) {

		Label label;
		/*
		 * Section
		 */
		Section section = formToolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText("Evaluation");
		section.setDescription("Run the PCA evaluation after the entries have been edited.");
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		/*
		 * Client
		 */
		Composite client = formToolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalIndent = 20;
		gridData.heightHint = 30;
		/*
		 * Input files section.
		 */
		label = formToolkit.createLabel(client, "Select the input chromatograms.");
		label.setLayoutData(gridData);
		createInputFilesPageHyperlink(client, gridData);
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		formToolkit.paintBordersFor(client);
	}

	private void createInputFilesPageHyperlink(Composite client, GridData gridData) {

		ImageHyperlink imageHyperlink;
		/*
		 * Settings
		 */
		imageHyperlink = formToolkit.createImageHyperlink(client, SWT.NONE);
		imageHyperlink.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		imageHyperlink.setText("Data Input Files");
		imageHyperlink.setLayoutData(gridData);
		imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			public void linkActivated(HyperlinkEvent e) {

				tabFolder.setSelection(inputFilesPageIndex);
			}
		});
	}

	// --------------------------------------------------------------------------------------------------------------Input Files Page
	/**
	 * Creates the page.
	 * 
	 */
	private void createInputFilesPage() {

		/*
		 * Input Files
		 */
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Data Input Files");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		/*
		 * Forms API
		 */
		formToolkit = new FormToolkit(composite.getDisplay());
		ScrolledForm scrolledForm = formToolkit.createScrolledForm(composite);
		Composite scrolledFormComposite = scrolledForm.getBody();
		scrolledFormComposite.setLayout(new TableWrapLayout());
		scrolledForm.setText("Input File Editor");
		/*
		 * Create the section.
		 */
		createInputFilesSection(scrolledFormComposite);
		//
		tabItem.setControl(composite);
	}

	private void createInputFilesSection(Composite parent) {

		Section section;
		Composite client;
		GridLayout layout;
		/*
		 * Section
		 */
		section = formToolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText("Input files");
		section.setDescription("Select the files to process. Use the add and remove buttons as needed. Click Peaks or Scans to pick appropriate extraction type. Default is set to Peaks. Click Run PCA to process the files. ");
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		/*
		 * Set the layout for the client.
		 */
		client = formToolkit.createComposite(section, SWT.WRAP);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		Label label;
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalIndent = 20;
		gridData.heightHint = 30;
		gridData.horizontalSpan = 2;
		/*
		 * Label II
		 */
		label = formToolkit.createLabel(client, "");
		label.setLayoutData(gridData);
		/*
		 * Creates the table and the action buttons.
		 */
		createTable(client);
		createButtons(client);
		createLabels(client);
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		formToolkit.paintBordersFor(client);
	}

	/**
	 * Creates the table.
	 * 
	 * @param client
	 */
	private void createTable(Composite client) {

		GridData gridData;
		inputFilesTable = formToolkit.createTable(client, SWT.MULTI);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 400;
		// gridData.widthHint = 150;
		gridData.widthHint = 100;
		gridData.verticalSpan = 5;
		// gridData.verticalSpan = 3;
		inputFilesTable.setLayoutData(gridData);
		inputFilesTable.setHeaderVisible(true);
		inputFilesTable.setLinesVisible(true);
	}

	/**
	 * Create the action buttons.
	 * 
	 * @param client
	 */
	private void createButtons(Composite client) {

		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		//
		createPeaksTypeButton(client, gridData);
		createScansTypeButton(client, gridData);
		createAddButton(client, gridData);
		createRemoveButton(client, gridData);
		createProcessButton(client, gridData);
	}

	/**
	 * Creates the add button.
	 * 
	 * @param client
	 * @param editorPart
	 */
	private void createAddButton(Composite client, GridData gridData) {

		Button add;
		add = formToolkit.createButton(client, "Add", SWT.PUSH);
		add.setLayoutData(gridData);
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				PeakInputFilesWizard inputWizard = new PeakInputFilesWizard();
				BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(Display.getCurrent().getActiveShell(), inputWizard);
				wizardDialog.create();
				int returnCode = wizardDialog.open();
				/*
				 * If OK
				 */
				if(returnCode == WizardDialog.OK) {
					/*
					 * Get the list of selected chromatograms.
					 */
					List<String> selectedPeakFiles = inputWizard.getSelectedPeakFiles();
					if(selectedPeakFiles.size() > 0) {
						/*
						 * If it contains at least 1 element, add it to the input files list.
						 */
						addEntries(selectedPeakFiles);
						reloadInputFilesTable();
					}
				}
			}
		});
	}

	/**
	 * Create the remove button.
	 * 
	 * @param client
	 * @param editorPart
	 */
	private void createRemoveButton(Composite client, GridData gridData) {

		Button remove;
		remove = formToolkit.createButton(client, "Remove", SWT.PUSH);
		remove.setLayoutData(gridData);
		remove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				removeEntries(inputFilesTable.getSelectionIndices());
			}
		});
	}

	private void createProcessButton(Composite client, GridData gridData) {

		Button process;
		process = formToolkit.createButton(client, "Run PCA", SWT.PUSH);
		process.setLayoutData(gridData);
		process.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		process.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				runPcaCalculation();
			}
		});
	}

	private void createPeaksTypeButton(Composite client, GridData gridData) {

		Button scans;
		scans = formToolkit.createButton(client, "Peaks", SWT.PUSH);
		scans.setLayoutData(gridData);
		scans.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				extractionType = 0;
			}
		});
	}

	private void createScansTypeButton(Composite client, GridData gridData) {

		Button scans;
		scans = formToolkit.createButton(client, "Scans", SWT.PUSH);
		scans.setLayoutData(gridData);
		scans.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				extractionType = 1;
			}
		});
	}

	private void runPcaCalculation() {

		dirtyable.setDirty(true);
		/*
		 * Get the settings.
		 */
		int retentionTimeWindow = DEFAULT_RETENTION_TIME_WINDOW;
		try {
			retentionTimeWindow = Integer.parseInt(retentionTimeWindowText.getText().trim());
		} catch(NumberFormatException e) {
			logger.warn(e);
		}
		int numberOfPrincipleComponents = principleComponentSpinner.getSelection();
		/*
		 * Run the process.
		 */
		PcaRunnable runnable = new PcaRunnable(dataInputEntries, retentionTimeWindow, numberOfPrincipleComponents, extractionType);
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
			reloadPeakListIntensityTable();
			updateSpinnerPCMaxima();
			reloadScorePlotChart();
			reloadErrorResidueChart();
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

	/**
	 * Creates the file count labels.
	 * 
	 * @param client
	 */
	private void createLabels(Composite client) {

		countFiles = formToolkit.createLabel(client, FILES + "0", SWT.NONE);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		countFiles.setLayoutData(gridData);
	}

	/**
	 * Creates the peak intensity table labels.
	 * 
	 * @param client
	 */
	private void createPeakIntensityTableLabels(Composite client) {

		tableHeader = formToolkit.createLabel(client, FILES + " " + "\t\tPeaks: " + " \tStart Peak: " + " \t End Peak: ", SWT.NONE);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		tableHeader.setLayoutData(gridData);
	}

	/**
	 * Add the selected peak files to the input files list.
	 * 
	 * @param selectedChromatograms
	 */
	private void addEntries(List<String> selectedFiles) {

		IDataInputEntry inputEntry;
		for(String inputFile : selectedFiles) {
			inputEntry = new DataInputEntry(inputFile);
			dataInputEntries.add(inputEntry);
		}
	}

	/**
	 * Remove the given entries.
	 * The table need not to be reloaded.
	 * 
	 * @param indices
	 */
	private void removeEntries(int[] indices) {

		if(indices == null || indices.length == 0) {
			return;
		}
		/*
		 * Remove the entries from the table.
		 */
		inputFilesTable.remove(indices);
		/*
		 * Remove the entries from the batchProcessJob instance.
		 */
		int counter = 0;
		for(int index : indices) {
			/*
			 * Decrease the index and increase the counter to remove the correct entries.
			 */
			index -= counter;
			dataInputEntries.remove(index);
			counter++;
		}
		redrawCountFiles(dataInputEntries);
	}

	/**
	 * Reload the table.
	 */
	private void reloadInputFilesTable() {

		if(inputFilesTable != null) {
			/*
			 * Remove all entries.
			 */
			inputFilesTable.removeAll();
			/*
			 * Header
			 */
			String[] titles = {"Filename", "Path"};
			for(int i = 0; i < titles.length; i++) {
				TableColumn column = new TableColumn(inputFilesTable, SWT.NONE);
				column.setText(titles[i]);
			}
			/*
			 * Data
			 */
			for(IDataInputEntry entry : dataInputEntries) {
				TableItem item = new TableItem(inputFilesTable, SWT.NONE);
				item.setText(0, entry.getName());
				item.setText(1, entry.getInputFile());
			}
			/*
			 * Pack to make the entries visible.
			 */
			for(int i = 0; i < titles.length; i++) {
				inputFilesTable.getColumn(i).pack();
			}
			/*
			 * Set the count label information.
			 */
			redrawCountFiles(dataInputEntries);
		}
	}

	private void redrawCountFiles(List<IDataInputEntry> inputEntries) {

		countFiles.setText(FILES + Integer.toString(inputEntries.size()));
	}

	private void redrawTableHeader(List<IDataInputEntry> inputEntries, int numPeaks, String startPoint, String endPoint) {

		tableHeader.setText(FILES + Integer.toString(inputEntries.size()) + "\t\tPeaks: " + numPeaks + " \t\tStart Peak: " + startPoint + "\t\tEnd Peak: " + endPoint);
	}

	// --------------------------------------------------------------------------------------------------------------Peak Intensity Table Page
	private void createPeakListIntensityTablePage() {

		/*
		 * Create the peak intensity table.
		 */
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Data Table");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		/*
		 * Forms API
		 */
		formToolkit = new FormToolkit(composite.getDisplay());
		ScrolledForm scrolledForm = formToolkit.createScrolledForm(composite);
		Composite scrolledFormComposite = scrolledForm.getBody();
		scrolledFormComposite.setLayout(new TableWrapLayout());
		scrolledForm.setText("Peak Intensity Table Editor");
		createPeakListIntensityTableSection(scrolledFormComposite);
		tabItem.setControl(composite);
	}

	private void createPeakListIntensityTableSection(Composite parent) {

		Section section;
		Composite client;
		GridLayout layout;
		/*
		 * Section
		 */
		section = formToolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText("Peak Intensity Table");
		section.setDescription("Click on the filename box to display a certain timerange.\n" + "Click on any peak column header to delete the corresponding column.\n");
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		/*
		 * Set the layout for the client.
		 */
		client = formToolkit.createComposite(section, SWT.WRAP);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		// Check if this works
		createPeakIntensityTableLabels(client);
		GridData gridData;
		peakListIntensityTable = formToolkit.createTable(client, SWT.MULTI | SWT.VIRTUAL | SWT.CHECK);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 300;
		gridData.widthHint = 100;
		gridData.verticalSpan = 3;
		peakListIntensityTable.setLayoutData(gridData);
		peakListIntensityTable.setHeaderVisible(true);
		peakListIntensityTable.setLinesVisible(true);
		peakListIntensityTable.addListener(SWT.MouseDoubleClick, new Listener() {

			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {

				TableItem[] selection = peakListIntensityTable.getSelection();
				for(int i = 0; i < selection.length; i++) {
					selection[i].dispose();
				}
			}
		});
		peakListIntensityTable.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {

				TableItem item = (TableItem)event.item;
				String filename = item.getText();
				Map<ISample, IPcaResult> resultMap = pcaResults.getPcaResultMap();
				for(ISample key : resultMap.keySet()) {
					if(key.getName().equals(filename)) {
						if(key.isSelected()) {
							key.setSelected(false);
							return;
						} else {
							key.setSelected(true);
							return;
						}
					}
				}
			}
		});
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		formToolkit.paintBordersFor(client);
	}

	private void reloadPeakListIntensityTable() {

		if(peakListIntensityTable != null) {
			String peakStartPoint;
			String peakEndPoint;
			/*
			 * Remove all entries.
			 */
			peakListIntensityTable.setRedraw(false);
			peakListIntensityTable.removeAll();
			while(peakListIntensityTable.getColumnCount() > 0) {
				/*
				 * Delete all columns.
				 */
				peakListIntensityTable.getColumns()[0].dispose();
			}
			peakListIntensityTable.setRedraw(true);
			/*
			 * Header
			 */
			List<String> titleList = new ArrayList<String>();
			titleList.add("Filename");
			for(int retentionTime : pcaResults.getExtractedRetentionTimes()) {
				titleList.add(numberFormat.format(retentionTime / AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
			}
			final String[] titles = titleList.toArray(new String[titleList.size()]);
			currentNumberOfPeaks = titles.length - 1;
			TableColumn filenameColumn = new TableColumn(peakListIntensityTable, SWT.NONE);
			filenameColumn.setText(titles[0]);
			/*
			 * Makes filename entry clickable to be able to display certain time range
			 */
			filenameColumn.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent event) {

					/*
					 * Make wizard to allow user to input time range for table
					 */
					TimeRangeWizard tableWizard = new TimeRangeWizard();
					WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), tableWizard);
					int returnCode = wizardDialog.open();
					String range = tableWizard.getTextOne();
					/*
					 * If timerange entered
					 */
					if(returnCode == WizardDialog.OK) {
						System.out.println("Ok pressed");
						int split = range.indexOf("-");
						double startRange = Double.parseDouble(range.substring(0, split));
						double endRange = Double.parseDouble(range.substring(split + 1));
						if(endRange < startRange) {
							return;
						}
						TableColumn[] columns = peakListIntensityTable.getColumns();
						boolean startRowSet = false;
						int startRow = 0;
						int endRow = 0;
						double currentTitle = 0.0;
						for(int i = 1; i <= columns.length; i++) {
							currentTitle = Double.parseDouble(columns[i].getText());
							if(currentTitle > startRange && !startRowSet) {
								startRowSet = true;
								startRow = i;
							} else if(currentTitle > endRange) {
								if(i != 0) {
									endRow = i - 1;
								}
								break;
							}
						}
						// Deletes columns before start range
						for(int j = 1; j < startRow; j++) {
							columns[j].dispose();
							currentNumberOfPeaks--;
						}
						// Deletes columns after end range
						for(int k = endRow; k < columns.length; k++) {
							columns[k].dispose();
							currentNumberOfPeaks--;
						}
						redrawTableHeader(dataInputEntries, currentNumberOfPeaks, columns[startRow].getText(), columns[endRow - 1].getText());
					} else {
						System.out.println("Cancel pressed");
					}
				}
			});
			for(int i = 1; i < titles.length; i++) {
				final TableColumn column = new TableColumn(peakListIntensityTable, SWT.NONE);
				column.setText(titles[i]);
				column.addSelectionListener(new SelectionAdapter() {

					public void widgetSelected(SelectionEvent event) {

						column.dispose();
						currentNumberOfPeaks--;
						TableColumn[] newColumns = peakListIntensityTable.getColumns();
						redrawTableHeader(dataInputEntries, currentNumberOfPeaks, newColumns[1].getText(), newColumns[newColumns.length - 1].getText());
					}
				});
			}
			/*
			 * Data
			 */
			for(Map.Entry<ISample, IPcaResult> entry : pcaResults.getPcaResultMap().entrySet()) {
				int index = 0;
				TableItem item = new TableItem(peakListIntensityTable, SWT.NONE);
				if(entry.getKey().isSelected()) {
					item.setChecked(true);
				}
				item.setText(index++, entry.getKey().getName());
				IPcaResult pcaResult = entry.getValue();
				double[] sampleData = pcaResult.getSampleData();
				for(double data : sampleData) {
					item.setText(index++, numberFormat.format(data));
				}
			}
			/*
			 * Pack to make the entries visible.
			 */
			for(int i = 0; i < titles.length; i++) {
				peakListIntensityTable.getColumn(i).pack();
			}
			peakStartPoint = titles[1];
			peakEndPoint = titles[titles.length - 1];
			redrawTableHeader(dataInputEntries, currentNumberOfPeaks, peakStartPoint, peakEndPoint);
		}
	}

	// --------------------------------------------------------------------------------------------------------------Peak Intensity Table Page
	private void createScorePlotPage() {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Score Plot");
		//
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		//
		Composite parent = new Composite(composite, SWT.NONE);
		parent.setLayout(new GridLayout(1, true));
		parent.setLayoutData(GridData.FILL_BOTH);
		/*
		 * Selection of the plotted PCs
		 */
		Composite spinnerComposite = new Composite(parent, SWT.NONE);
		spinnerComposite.setLayout(new GridLayout(5, false));
		spinnerComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Label label;
		GridData gridData = new GridData();
		gridData.widthHint = 50;
		gridData.heightHint = 20;
		//
		label = new Label(spinnerComposite, SWT.NONE);
		label.setText("PC X-Axis: ");
		spinnerPCx = new Spinner(spinnerComposite, SWT.NONE);
		spinnerPCx.setMinimum(1);
		spinnerPCx.setMaximum(1);
		spinnerPCx.setIncrement(1);
		spinnerPCx.setLayoutData(gridData);
		//
		label = new Label(spinnerComposite, SWT.NONE);
		label.setText(" PC Y-Axis: ");
		spinnerPCy = new Spinner(spinnerComposite, SWT.NONE);
		spinnerPCy.setMinimum(1);
		spinnerPCy.setMaximum(1);
		spinnerPCy.setIncrement(1);
		spinnerPCy.setLayoutData(gridData);
		//
		Button button = new Button(spinnerComposite, SWT.PUSH);
		button.setText("Reload Score Plot");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				reloadScorePlotChart();
			}
		});
		/*
		 * Plot the PCA chart.
		 */
		Composite chartComposite = new Composite(parent, SWT.NONE);
		chartComposite.setLayout(new GridLayout(1, true));
		chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		scorePlotChart = new InteractiveChartExtended(chartComposite, SWT.NONE);
		scorePlotChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		scorePlotChart.getTitle().setText("PCA Score Plot");
		scorePlotChart.getTitle().setForeground(COLOR_BLACK);
		//
		scorePlotChart.setBackground(COLOR_WHITE);
		scorePlotChart.getLegend().setVisible(false);
		//
		scorePlotChart.getAxisSet().getXAxis(0).getTitle().setText("PC" + spinnerPCx.getSelection());
		scorePlotChart.getAxisSet().getXAxis(0).getTitle().setForeground(COLOR_BLACK);
		scorePlotChart.getAxisSet().getXAxis(0).getTick().setForeground(COLOR_BLACK);
		//
		scorePlotChart.getAxisSet().getYAxis(0).getTitle().setText("PC" + spinnerPCy.getSelection());
		scorePlotChart.getAxisSet().getYAxis(0).getTitle().setForeground(COLOR_BLACK);
		scorePlotChart.getAxisSet().getYAxis(0).getTick().setForeground(COLOR_BLACK);
		//
		IPlotArea plotArea = (IPlotArea)scorePlotChart.getPlotArea();
		/*
		 * Plot a marker at zero.
		 */
		plotArea.addCustomPaintListener(new ICustomPaintListener() {

			@Override
			public void paintControl(PaintEvent e) {

				Range xRange = scorePlotChart.getAxisSet().getXAxes()[0].getRange();
				Range yRange = scorePlotChart.getAxisSet().getYAxes()[0].getRange();
				/*
				 * Mark the zero lines if possible.
				 * Otherwise draw the marker in half width.
				 */
				if(xRange.lower < 0 && xRange.upper > 0 && yRange.lower < 0 && yRange.upper > 0) {
					Rectangle rectangle = scorePlotChart.getPlotArea().getClientArea();
					int width = rectangle.width;
					int height = rectangle.height;
					int xWidth;
					int yHeight;
					/*
					 * Dependent where the zero values are.
					 * xDelta and yDelta can't be zero -> protect from division by zero.
					 */
					double xDelta = xRange.upper - xRange.lower;
					double yDelta = yRange.upper - yRange.lower;
					double xDiff = xRange.lower * -1; // lower is negative
					double yDiff = yRange.upper;
					double xPart = ((100 / xDelta) * xDiff) / 100; // percent -> 0.0 - 1.0
					double yPart = ((100 / yDelta) * yDiff) / 100; // percent -> 0.0 - 1.0
					xWidth = (int)(width * xPart);
					yHeight = (int)(height * yPart);
					/*
					 * Draw the line.
					 */
					e.gc.setForeground(COLOR_BLACK);
					e.gc.drawLine(xWidth, 0, xWidth, height); // Vertical line through zero
					e.gc.drawLine(0, yHeight, width, yHeight); // Horizontal line through zero
				}
			}

			@Override
			public boolean drawBehindSeries() {

				return false;
			}
		});
		/*
		 * Plot the series name above the entry.
		 */
		plotArea.addCustomPaintListener(new ICustomPaintListener() {

			@Override
			public void paintControl(PaintEvent e) {

				ISeriesSet seriesSet = scorePlotChart.getSeriesSet();
				ISeries[] series = seriesSet.getSeries();
				for(ISeries serie : series) {
					String label = serie.getId();
					Point point = serie.getPixelCoordinates(0);
					/*
					 * Draw the label
					 */
					Point labelSize = e.gc.textExtent(label);
					e.gc.setForeground(COLOR_BLACK);
					e.gc.drawText(label, (int)(point.x - labelSize.x / 2.0d), (int)(point.y - labelSize.y - SYMBOL_SIZE / 2.0d), true);
				}
			}

			@Override
			public boolean drawBehindSeries() {

				return false;
			}
		});
		//
		tabItem.setControl(composite);
	}

	private void updateSpinnerPCMaxima() {

		if(pcaResults != null) {
			spinnerPCx.setMaximum(pcaResults.getNumberOfPrincipleComponents());
			spinnerPCx.setSelection(1); // PC1
			spinnerPCy.setMaximum(pcaResults.getNumberOfPrincipleComponents());
			spinnerPCy.setSelection(2); // PC2
		}
	}

	private void createErrorResiduePage() {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Error Residues");
		//
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		//
		Composite parent = new Composite(composite, SWT.NONE);
		parent.setLayout(new GridLayout(1, true));
		parent.setLayoutData(GridData.FILL_BOTH);
		/*
		 * Selection of the plotted PCs
		 */
		Composite spinnerComposite = new Composite(parent, SWT.NONE);
		spinnerComposite.setLayout(new GridLayout(5, false));
		spinnerComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		GridData gridData = new GridData();
		gridData.widthHint = 50;
		gridData.heightHint = 20;
		/*
		 * Plot the Error residue chart chart.
		 */
		Composite chartComposite = new Composite(parent, SWT.NONE);
		chartComposite.setLayout(new GridLayout(1, true));
		chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		Button button = new Button(spinnerComposite, SWT.PUSH);
		button.setText("Reload Error Chart");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				reloadErrorResidueChart();
			}
		});
		//
		errorResidueChart = new InteractiveChartExtended(chartComposite, SWT.NONE);
		errorResidueChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		errorResidueChart.getTitle().setText("Error Residue Chart");
		errorResidueChart.getTitle().setForeground(COLOR_BLACK);
		//
		errorResidueChart.setBackground(COLOR_WHITE);
		errorResidueChart.getLegend().setVisible(false);
		//
		errorResidueChart.getAxisSet().getXAxis(0).getTitle().setText("Sample Names");
		errorResidueChart.getAxisSet().getXAxis(0).getTitle().setForeground(COLOR_BLACK);
		errorResidueChart.getAxisSet().getXAxis(0).getTick().setForeground(COLOR_BLACK);
		//
		errorResidueChart.getAxisSet().getYAxis(0).getTitle().setText("Error Values(10^-6)");
		errorResidueChart.getAxisSet().getYAxis(0).getTitle().setForeground(COLOR_BLACK);
		errorResidueChart.getAxisSet().getYAxis(0).getTick().setForeground(COLOR_BLACK);
		//
		String[] tempCategories = {" ", " ", " "};
		errorResidueChart.getAxisSet().getXAxis(0).setCategorySeries(tempCategories);
		errorResidueChart.getAxisSet().getXAxis(0).enableCategory(true);
		IPlotArea plotArea = (IPlotArea)errorResidueChart.getPlotArea();
		/*
		 * Plot a marker at zero.
		 */
		plotArea.addCustomPaintListener(new ICustomPaintListener() {

			@Override
			public void paintControl(PaintEvent e) {

				Range xRange = errorResidueChart.getAxisSet().getXAxes()[0].getRange();
				Range yRange = errorResidueChart.getAxisSet().getYAxes()[0].getRange();
				/*
				 * Mark the zero lines if possible.
				 * Otherwise draw the marker in half width.
				 */
				if(xRange.lower < 0 && xRange.upper > 0 && yRange.lower < 0 && yRange.upper > 0) {
					Rectangle rectangle = errorResidueChart.getPlotArea().getClientArea();
					int width = rectangle.width;
					int height = rectangle.height;
					int xWidth;
					int yHeight;
					/*
					 * Dependent where the zero values are.
					 * xDelta and yDelta can't be zero -> protect from division by zero.
					 */
					double xDelta = xRange.upper - xRange.lower;
					double yDelta = yRange.upper - yRange.lower;
					double xDiff = xRange.lower * -1; // lower is negative
					double yDiff = yRange.upper;
					double xPart = ((100 / xDelta) * xDiff) / 100; // percent -> 0.0 - 1.0
					double yPart = ((100 / yDelta) * yDiff) / 100; // percent -> 0.0 - 1.0
					xWidth = (int)(width * xPart);
					yHeight = (int)(height * yPart);
					/*
					 * Draw the line.
					 */
					e.gc.setForeground(COLOR_BLACK);
					e.gc.drawLine(xWidth, 0, xWidth, height); // Vertical line through zero
					e.gc.drawLine(0, yHeight, width, yHeight); // Horizontal line through zero
				}
			}

			@Override
			public boolean drawBehindSeries() {

				return false;
			}
		});
		/*
		 * Plot the series name above the entry.
		 */
		plotArea.addCustomPaintListener(new ICustomPaintListener() {

			@Override
			public void paintControl(PaintEvent e) {

				ISeriesSet seriesSet = errorResidueChart.getSeriesSet();
				ISeries[] series = seriesSet.getSeries();
				for(ISeries serie : series) {
					String label = serie.getId();
					Point point = serie.getPixelCoordinates(0);
					/*
					 * Draw the label
					 */
					Point labelSize = e.gc.textExtent(label);
					e.gc.setForeground(COLOR_BLACK);
					e.gc.drawText("", (int)(point.x - labelSize.x / 2.0d), (int)(point.y - labelSize.y - SYMBOL_SIZE / 2.0d), true);
				}
			}

			@Override
			public boolean drawBehindSeries() {

				return false;
			}
		});
		//
		tabItem.setControl(composite);
	}

	private void reloadScorePlotChart() {

		if(scorePlotChart != null) {
			/*
			 * Delete all other series.
			 */
			ISeriesSet seriesSet = scorePlotChart.getSeriesSet();
			ISeries[] series = seriesSet.getSeries();
			for(ISeries serie : series) {
				seriesSet.deleteSeries(serie.getId());
			}
			String[] fileNames = new String[pcaResults.getPcaResultMap().entrySet().size()];
			int count = 0;
			/*
			 * Data
			 */
			for(Map.Entry<ISample, IPcaResult> entry : pcaResults.getPcaResultMap().entrySet()) {
				/*
				 * Create the series.
				 */
				String name = entry.getKey().getName();
				fileNames[count] = name;
				ILineSeries scatterSeries = (ILineSeries)scorePlotChart.getSeriesSet().createSeries(SeriesType.LINE, name);
				scatterSeries.setLineStyle(LineStyle.NONE);
				scatterSeries.setSymbolSize(SYMBOL_SIZE);
				//
				IPcaResult pcaResult = entry.getValue();
				double[] eigenSpace = pcaResult.getEigenSpace();
				/*
				 * Note.
				 * The spinners are 1 based.
				 * The index is zero based.
				 */
				int pcx = spinnerPCx.getSelection();
				int pcy = spinnerPCy.getSelection();
				scorePlotChart.getAxisSet().getXAxis(0).getTitle().setText("PC" + pcx);
				scorePlotChart.getAxisSet().getYAxis(0).getTitle().setText("PC" + pcy);
				double x = eigenSpace[pcx - 1]; // e.g. 0 = PC1
				double y = eigenSpace[pcy - 1]; // e.g. 1 = PC2
				scatterSeries.setXSeries(new double[]{x});
				scatterSeries.setYSeries(new double[]{y});
				/*
				 * Set the color.
				 */
				if(x > 0 && y > 0) {
					scatterSeries.setSymbolColor(COLOR_RED);
					scatterSeries.setSymbolType(PlotSymbolType.SQUARE);
				} else if(x > 0 && y < 0) {
					scatterSeries.setSymbolColor(COLOR_BLUE);
					scatterSeries.setSymbolType(PlotSymbolType.TRIANGLE);
				} else if(x < 0 && y > 0) {
					scatterSeries.setSymbolColor(COLOR_MAGENTA);
					scatterSeries.setSymbolType(PlotSymbolType.DIAMOND);
				} else if(x < 0 && y < 0) {
					scatterSeries.setSymbolColor(COLOR_CYAN);
					scatterSeries.setSymbolType(PlotSymbolType.INVERTED_TRIANGLE);
				} else {
					scatterSeries.setSymbolColor(COLOR_GRAY);
					scatterSeries.setSymbolType(PlotSymbolType.CIRCLE);
				}
			}
			scorePlotChart.getAxisSet().adjustRange();
			scorePlotChart.redraw();
			scorePlotChart.update();
		}
	}

	private void reloadErrorResidueChart() {

		if(errorResidueChart != null) {
			/*
			 * Delete all other series.
			 */
			ISeriesSet seriesSet = errorResidueChart.getSeriesSet();
			ISeries[] series = seriesSet.getSeries();
			for(ISeries serie : series) {
				seriesSet.deleteSeries(serie.getId());
			}
			String[] fileNames = new String[pcaResults.getPcaResultMap().entrySet().size()];
			int count = 0;
			/*
			 * Data
			 */
			double[] errorResidue = new double[pcaResults.getPcaResultMap().size()];
			int counter = 0;
			errorResidueChart.getAxisSet().getXAxis(0).getTitle().setText("Sample Names");
			errorResidueChart.getAxisSet().getYAxis(0).getTitle().setText("Error Values(10^-6)");
			for(ISample key : pcaResults.getPcaResultMap().keySet()) {
				IPcaResult temp = pcaResults.getPcaResultMap().get(key);
				// Done to better display error values
				errorResidue[counter] = temp.getErrorMemberShip() * Math.pow(10, 6);
				counter++;
			}
			for(Map.Entry<ISample, IPcaResult> entry : pcaResults.getPcaResultMap().entrySet()) {
				/*
				 * Create the series.
				 */
				String name = entry.getKey().getName();
				fileNames[count] = name;
				count++;
			}
			ILineSeries scatterSeries = (ILineSeries)errorResidueChart.getSeriesSet().createSeries(SeriesType.LINE, "Samples");
			scatterSeries.setLineStyle(LineStyle.NONE);
			scatterSeries.setSymbolSize(SYMBOL_SIZE);
			double[] xSeries = new double[fileNames.length];
			for(int i = 0; i < fileNames.length; i++) {
				xSeries[i] = i + 1;
			}
			scatterSeries.setYSeries(errorResidue);
			scatterSeries.setXSeries(xSeries);
			/*
			 * Set the color.
			 */
			scatterSeries.setSymbolColor(COLOR_RED);
			scatterSeries.setSymbolType(PlotSymbolType.DIAMOND);
			errorResidueChart.getAxisSet().getXAxis(0).setCategorySeries(fileNames);
			errorResidueChart.getAxisSet().getXAxis(0).enableCategory(true);
			errorResidueChart.getAxisSet().adjustRange();
			errorResidueChart.redraw();
			errorResidueChart.update();
		}
	}
}
