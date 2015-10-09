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
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPeakInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PeakInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.runnable.PcaRunnable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.BatchProcessWizardDialog;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.PeakInputFilesWizard;
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
	private static final String TAB = "\t";
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
	private Text retentionTimeWindowText;
	private Spinner principleComponentSpinner;
	private Table peakListIntensityTable;
	private InteractiveChartExtended scorePlotChart;
	private Spinner spinnerPCx;
	private Spinner spinnerPCy;
	/*
	 * Indices of the pages.
	 */
	private int inputFilesPageIndex;
	private int scorePlotPageIndex;
	//
	private List<IPeakInputEntry> inputEntries;
	private PcaResults pcaResults;
	private File exportFile;
	/*
	 * Number format
	 */
	private NumberFormat numberFormat;
	private static final int FRACTION_DIGITS = 3;

	public PcaEditor() {

		inputEntries = new ArrayList<IPeakInputEntry>();
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
				exportResultsToFile(exportFile);
				dirtyable.setDirty(false);
			} catch(FileNotFoundException e) {
				logger.warn(e);
			}
		}
	}

	private void exportResultsToFile(File file) throws FileNotFoundException {

		PrintWriter printWriter = new PrintWriter(file);
		if(pcaResults != null) {
			Set<Map.Entry<String, PcaResult>> entrySet = pcaResults.getPcaResultMap().entrySet();
			/*
			 * Header
			 */
			printWriter.println("-------------------------------------");
			printWriter.println("Settings");
			printWriter.println("-------------------------------------");
			printWriter.print("Number of principle components:");
			printWriter.print(TAB);
			printWriter.println(pcaResults.getNumberOfPrincipleComponents());
			printWriter.print("Retention time window:");
			printWriter.print(TAB);
			printWriter.println(pcaResults.getRetentionTimeWindow());
			printWriter.println("");
			printWriter.println("-------------------------------------");
			printWriter.println("Input Files");
			printWriter.println("-------------------------------------");
			for(IPeakInputEntry entry : inputEntries) {
				printWriter.print(entry.getName());
				printWriter.print(TAB);
				printWriter.println(entry.getInputFile());
			}
			printWriter.println("");
			printWriter.println("-------------------------------------");
			printWriter.println("Extracted Retention Times (Minutes)");
			printWriter.println("-------------------------------------");
			for(int retentionTime : pcaResults.getExtractedRetentionTimes()) {
				printWriter.println(numberFormat.format(retentionTime / AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
			}
			printWriter.println("");
			printWriter.println("-------------------------------------");
			printWriter.println("Peak Intensity Table");
			printWriter.println("-------------------------------------");
			printWriter.print("Filename");
			printWriter.print(TAB);
			for(int retentionTime : pcaResults.getExtractedRetentionTimes()) {
				printWriter.print(numberFormat.format(retentionTime / AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
				printWriter.print(TAB);
			}
			printWriter.println("");
			/*
			 * Data
			 */
			for(Map.Entry<String, PcaResult> entry : entrySet) {
				printWriter.print(entry.getKey());
				printWriter.print(TAB);
				PcaResult pcaResult = entry.getValue();
				double[] sampleData = pcaResult.getSampleData();
				for(double data : sampleData) {
					printWriter.print(numberFormat.format(data));
					printWriter.print(TAB);
				}
				printWriter.println("");
			}
			printWriter.println("");
			printWriter.println("-------------------------------------");
			printWriter.println("Principle Components");
			printWriter.println("-------------------------------------");
			//
			printWriter.print("File");
			printWriter.print(TAB);
			for(int i = 1; i <= pcaResults.getNumberOfPrincipleComponents(); i++) {
				printWriter.print("PC" + i);
				printWriter.print(TAB);
			}
			printWriter.println("");
			//
			for(Map.Entry<String, PcaResult> entry : entrySet) {
				/*
				 * Print the PCs
				 */
				String name = entry.getKey();
				PcaResult pcaResult = entry.getValue();
				double[] eigenSpace = pcaResult.getEigenSpace();
				printWriter.print(name);
				printWriter.print(TAB);
				for(double value : eigenSpace) {
					printWriter.print(numberFormat.format(value));
					printWriter.print(TAB);
				}
				printWriter.println("");
			}
		} else {
			/*
			 * No results available.
			 */
			printWriter.println("There are no results available yet.");
		}
		//
		printWriter.flush();
		printWriter.close();
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
		 * Label I
		 */
		label = formToolkit.createLabel(client, "1. Select the input entries.");
		label.setLayoutData(gridData);
		/*
		 * Link Pages
		 */
		createInputFilesPageHyperlink(client, gridData);
		/*
		 * Label II
		 */
		label = formToolkit.createLabel(client, "2. Run the process job after selecting the input files.");
		label.setLayoutData(gridData);
		/*
		 * Link Run
		 * The label is a dirty workaround to make the run button full visible.
		 * Otherwise, it is cutted at its bottom. I think, it has something to
		 * do with the client height.
		 */
		createProcessHyperlink(client, gridData);
		label = formToolkit.createLabel(client, "");
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		formToolkit.paintBordersFor(client);
	}

	private void createProcessHyperlink(Composite client, GridData gridData) {

		ImageHyperlink imageHyperlink;
		/*
		 * Settings
		 */
		imageHyperlink = formToolkit.createImageHyperlink(client, SWT.WRAP);
		imageHyperlink.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		imageHyperlink.setText("Run PCA");
		imageHyperlink.setLayoutData(gridData);
		imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			public void linkActivated(HyperlinkEvent e) {

				dirtyable.setDirty(true);
				/*
				 * Get the settings.
				 */
				int retentionTimeWindow = DEFAULT_RETENTION_TIME_WINDOW;
				try {
					retentionTimeWindow = Integer.parseInt(retentionTimeWindowText.getText().trim());
				} catch(NumberFormatException e1) {
					logger.warn(e1);
				}
				int numberOfPrincipleComponents = principleComponentSpinner.getSelection();
				/*
				 * Run the process.
				 */
				PcaRunnable runnable = new PcaRunnable(inputEntries, retentionTimeWindow, numberOfPrincipleComponents);
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
					/*
					 * Activate the score plot chart.
					 */
					tabFolder.setSelection(scorePlotPageIndex);
				} catch(InvocationTargetException ex) {
					logger.warn(ex);
				} catch(InterruptedException ex) {
					logger.warn(ex);
				}
			}
		});
	}

	private void createInputFilesPageHyperlink(Composite client, GridData gridData) {

		ImageHyperlink imageHyperlink;
		/*
		 * Settings
		 */
		imageHyperlink = formToolkit.createImageHyperlink(client, SWT.NONE);
		imageHyperlink.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		imageHyperlink.setText("Peak Input Files");
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
		tabItem.setText("Peak Input Files");
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
		section.setDescription("Select the files to process. Use the add and remove buttons.");
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
		gridData.heightHint = 300;
		gridData.widthHint = 100;
		gridData.verticalSpan = 2;
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

		createAddButton(client);
		createRemoveButton(client);
	}

	/**
	 * Creates the add button.
	 * 
	 * @param client
	 * @param editorPart
	 */
	private void createAddButton(Composite client) {

		Button add;
		add = formToolkit.createButton(client, "Add", SWT.PUSH);
		add.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
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
	private void createRemoveButton(Composite client) {

		Button remove;
		remove = formToolkit.createButton(client, "Remove", SWT.PUSH);
		remove.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		remove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				removeEntries(inputFilesTable.getSelectionIndices());
			}
		});
	}

	/**
	 * Creates the file count labels.
	 * 
	 * @param client
	 */
	private void createLabels(Composite client) {

		countFiles = formToolkit.createLabel(client, FILES + "0", SWT.NONE);
		countFiles.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
	}

	/**
	 * Add the selected peak files to the input files list.
	 * 
	 * @param selectedChromatograms
	 */
	private void addEntries(List<String> selectedFiles) {

		IPeakInputEntry inputEntry;
		for(String inputFile : selectedFiles) {
			inputEntry = new PeakInputEntry(inputFile);
			inputEntries.add(inputEntry);
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
			inputEntries.remove(index);
			counter++;
		}
		redrawCountFiles(inputEntries);
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
			for(IPeakInputEntry entry : inputEntries) {
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
			redrawCountFiles(inputEntries);
		}
	}

	private void redrawCountFiles(List<IPeakInputEntry> inputEntries) {

		countFiles.setText(FILES + Integer.toString(inputEntries.size()));
	}

	// --------------------------------------------------------------------------------------------------------------Peak Intensity Table Page
	private void createPeakListIntensityTablePage() {

		/*
		 * Create the peak intensity table.
		 */
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Peak Intensity Table");
		//
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		peakListIntensityTable = new Table(composite, SWT.MULTI | SWT.VIRTUAL);
		peakListIntensityTable.setHeaderVisible(true);
		peakListIntensityTable.setLinesVisible(true);
		//
		tabItem.setControl(composite);
	}

	private void reloadPeakListIntensityTable() {

		if(peakListIntensityTable != null) {
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
			String[] titles = titleList.toArray(new String[titleList.size()]);
			for(int i = 0; i < titles.length; i++) {
				TableColumn column = new TableColumn(peakListIntensityTable, SWT.NONE);
				column.setText(titles[i]);
			}
			/*
			 * Data
			 */
			for(Map.Entry<String, PcaResult> entry : pcaResults.getPcaResultMap().entrySet()) {
				int index = 0;
				TableItem item = new TableItem(peakListIntensityTable, SWT.NONE);
				item.setText(index++, entry.getKey());
				PcaResult pcaResult = entry.getValue();
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
			/*
			 * Data
			 */
			for(Map.Entry<String, PcaResult> entry : pcaResults.getPcaResultMap().entrySet()) {
				/*
				 * Create the series.
				 */
				String name = entry.getKey();
				ILineSeries scatterSeries = (ILineSeries)scorePlotChart.getSeriesSet().createSeries(SeriesType.LINE, name);
				scatterSeries.setLineStyle(LineStyle.NONE);
				scatterSeries.setSymbolSize(SYMBOL_SIZE);
				//
				PcaResult pcaResult = entry.getValue();
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
}
