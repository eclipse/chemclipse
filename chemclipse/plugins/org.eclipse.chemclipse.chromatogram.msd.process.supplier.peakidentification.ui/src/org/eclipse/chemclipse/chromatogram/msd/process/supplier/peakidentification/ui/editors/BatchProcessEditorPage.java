/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.editors;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIdentificationBatchJob;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.support.IdentificationSupport;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.support.IntegrationSupport;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.editors.ResultsPage.SelectionUpdateListener;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.internal.runnables.BatchRunnable;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class BatchProcessEditorPage implements IMultiEditorPage {

	private static final Logger logger = Logger.getLogger(BatchProcessEditorPage.class);
	private FormToolkit toolkit;
	private int pageIndex;
	private IPeakIdentificationBatchJob peakIdentificationBatchJob;
	private Text reportFolderTextBox;
	private Button overrideReportsCheckBox;
	private Combo integratorComboBox;
	private Button reportIntegrationResultsCheckBox;
	private Combo identifierComboBox;
	private Button reportIdentificationResultsCheckBox;
	private IntegrationSupport integrationSupport;
	private IdentificationSupport identifiationSupport;

	public BatchProcessEditorPage(BatchProcessEditor editorPart, Composite container) {
		integrationSupport = new IntegrationSupport();
		identifiationSupport = new IdentificationSupport();
		createPage(editorPart, container);
	}

	@Override
	public void setFocus() {

	}

	@Override
	public int getPageIndex() {

		return pageIndex;
	}

	@Override
	public void dispose() {

		if(toolkit != null) {
			toolkit.dispose();
		}
	}

	@Override
	public void setPeakIdentificationBatchJob(IPeakIdentificationBatchJob peakIdentificationBatchJob) {

		if(peakIdentificationBatchJob != null) {
			reportFolderTextBox.setText(peakIdentificationBatchJob.getReportFolder());
			overrideReportsCheckBox.setSelection(peakIdentificationBatchJob.isOverrideReport());
			this.peakIdentificationBatchJob = peakIdentificationBatchJob;
			setIntegratorSelection();
			setIdentifierSelection();
		}
	}

	// ---------------------------------------private methods
	/**
	 * Creates the page.
	 * 
	 */
	private void createPage(BatchProcessEditor editorPart, Composite container) {

		/*
		 * Create the parent composite.
		 */
		Composite parent = new Composite(container, SWT.NONE);
		parent.setLayout(new FillLayout());
		parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		/*
		 * Forms API
		 */
		toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm scrolledForm = toolkit.createScrolledForm(parent);
		Composite scrolledFormComposite = scrolledForm.getBody();
		scrolledFormComposite.setLayout(new TableWrapLayout());
		scrolledForm.setText("Peak Identification Batch Editor");
		/*
		 * Create the sections.
		 */
		createPropertiesSection(scrolledFormComposite, editorPart);
		createIntegratorAndIdentifierSection(scrolledFormComposite, editorPart);
		createExecuteSection(scrolledFormComposite, editorPart);
		/*
		 * Get the page index.
		 */
		pageIndex = editorPart.addPage(parent);
	}

	/**
	 * Creates the properties section.
	 */
	private void createPropertiesSection(Composite parent, final BatchProcessEditor editorPart) {

		Section section;
		Composite client;
		GridLayout layout;
		GridData gridData;
		Label label;
		/*
		 * Section
		 */
		section = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText("Properties");
		section.setDescription("Use the properties to define where the reports shall be written to.");
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		/*
		 * Client
		 */
		client = toolkit.createComposite(section, SWT.WRAP);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		label = toolkit.createLabel(client, "Select workspace to report the results:");
		label.setLayoutData(gridData);
		/*
		 * Report and override
		 */
		createReportFolderSelection(editorPart, client);
		createOverrideReportsCheckBox(editorPart, client);
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		toolkit.paintBordersFor(client);
	}

	/**
	 * Create the report folder selection.
	 * 
	 * @param editorPart
	 * @param client
	 */
	private void createReportFolderSelection(final BatchProcessEditor editorPart, Composite client) {

		GridData gridData;
		Button button;
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		reportFolderTextBox = toolkit.createText(client, "", SWT.NONE);
		reportFolderTextBox.setLayoutData(gridData);
		/*
		 * Select report folder
		 */
		button = toolkit.createButton(client, "Select ...", SWT.PUSH);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Shell shell = Display.getCurrent().getActiveShell();
				DirectoryDialog dialog = new DirectoryDialog(shell);
				dialog.setText("Select report folder:");
				String reportFolder = dialog.open();
				if(reportFolder != null) {
					reportFolderTextBox.setText(reportFolder);
					/*
					 * Set the report folder and mark the editor dirty.
					 */
					if(peakIdentificationBatchJob != null) {
						peakIdentificationBatchJob.setReportFolder(reportFolder);
						editorPart.setDirty();
					}
				}
			}
		});
	}

	/**
	 * Create the override reports check box.
	 * 
	 * @param editorPart
	 * @param client
	 */
	private void createOverrideReportsCheckBox(final BatchProcessEditor editorPart, Composite client) {

		GridData gridData;
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.heightHint = 30;
		overrideReportsCheckBox = toolkit.createButton(client, "Override reports", SWT.CHECK);
		overrideReportsCheckBox.setLayoutData(gridData);
		overrideReportsCheckBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				/*
				 * Set the override report value and mark the editor dirty.
				 */
				if(peakIdentificationBatchJob != null) {
					boolean overrideReport = (peakIdentificationBatchJob.isOverrideReport() ? false : true);
					peakIdentificationBatchJob.setOverrideReport(overrideReport);
					editorPart.setDirty();
				}
			}
		});
	}

	/**
	 * Creates the integrator and identifier section.
	 * 
	 * @param parent
	 */
	private void createIntegratorAndIdentifierSection(Composite parent, BatchProcessEditor editorPart) {

		Section section;
		Composite client;
		GridLayout layout;
		@SuppressWarnings("unused")
		Label label;
		GridData gridData;
		GridData gridDataLabel;
		/*
		 * Section
		 */
		section = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText("Integrator/Identifier");
		section.setDescription("Select the integrator and identifier to batch process the peaks.");
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		/*
		 * Client
		 */
		client = toolkit.createComposite(section, SWT.WRAP);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		// Label Grid Data
		gridDataLabel = new GridData(GridData.FILL_HORIZONTAL);
		gridDataLabel.horizontalIndent = 20;
		// Combo Box Grid Data
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalIndent = 20;
		gridData.heightHint = 30;
		/*
		 * Integrator/Identifier
		 */
		createIntegratorSelection(client, gridData, gridDataLabel, editorPart);
		createIdentifierSelection(client, gridData, gridDataLabel, editorPart);
		/*
		 * A dirty hack to create space at the bottom.
		 */
		label = toolkit.createLabel(client, "");
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		toolkit.paintBordersFor(client);
	}

	private void createIntegratorSelection(Composite client, GridData gridData, GridData gridDataLabel, final BatchProcessEditor editorPart) {

		Label label = toolkit.createLabel(client, "Select an integrator:");
		label.setLayoutData(gridDataLabel);
		//
		integratorComboBox = new Combo(client, SWT.READ_ONLY);
		integratorComboBox.setLayoutData(gridData);
		integratorComboBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = integratorComboBox.getSelectionIndex();
				if(index >= 0) {
					String name = integratorComboBox.getItem(index);
					String processorId = integrationSupport.getIntegratorId(name);
					peakIdentificationBatchJob.getPeakIntegrationEntry().setProcessorId(processorId);
					editorPart.setDirty();
				}
			}
		});
		toolkit.createComposite(integratorComboBox);
		/*
		 * Report
		 */
		reportIntegrationResultsCheckBox = toolkit.createButton(client, "Report integration results", SWT.CHECK);
		reportIntegrationResultsCheckBox.setLayoutData(gridData);
		reportIntegrationResultsCheckBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean report = reportIntegrationResultsCheckBox.getSelection();
				peakIdentificationBatchJob.getPeakIntegrationEntry().setReport(report);
				editorPart.setDirty();
			}
		});
	}

	private void setIntegratorSelection() {

		/*
		 * Integrator
		 */
		String[] integratorIds = integrationSupport.getPluginIds();
		String[] integratorList = integrationSupport.getIntegratorNames(integratorIds);
		integratorComboBox.setItems(integratorList);
		// Set the processor id.
		String integratorId = peakIdentificationBatchJob.getPeakIntegrationEntry().getProcessorId();
		int index = getIndex(integratorIds, integratorId);
		if(index >= 0) {
			integratorComboBox.select(index);
		} else {
			if(!integratorId.equals("")) {
				integratorComboBox.add(integratorId);
			}
		}
		// Enable to select no integrator.
		integratorComboBox.add("");
		/*
		 * Report CheckBox
		 */
		reportIntegrationResultsCheckBox.setSelection(peakIdentificationBatchJob.getPeakIntegrationEntry().isReport());
	}

	private void createIdentifierSelection(Composite client, GridData gridData, GridData gridDataLabel, final BatchProcessEditor editorPart) {

		Label label = toolkit.createLabel(client, "Select an identifier:");
		label.setLayoutData(gridDataLabel);
		//
		identifierComboBox = new Combo(client, SWT.READ_ONLY);
		identifierComboBox.setLayoutData(gridData);
		identifierComboBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = identifierComboBox.getSelectionIndex();
				if(index >= 0) {
					String name = identifierComboBox.getItem(index);
					String processorId = identifiationSupport.getIdentifierId(name);
					peakIdentificationBatchJob.getPeakIdentificationEntry().setProcessorId(processorId);
					editorPart.setDirty();
				}
			}
		});
		toolkit.createComposite(identifierComboBox);
		/*
		 * Report
		 */
		reportIdentificationResultsCheckBox = toolkit.createButton(client, "Report identification results", SWT.CHECK);
		reportIdentificationResultsCheckBox.setLayoutData(gridData);
		reportIdentificationResultsCheckBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean report = reportIdentificationResultsCheckBox.getSelection();
				peakIdentificationBatchJob.getPeakIdentificationEntry().setReport(report);
				editorPart.setDirty();
			}
		});
	}

	private void setIdentifierSelection() {

		/*
		 * Identifier
		 */
		String[] identifierIds = identifiationSupport.getPluginIds();
		String[] identifierList = identifiationSupport.getIdentifierNames(identifierIds);
		identifierComboBox.setItems(identifierList);
		// Set the processor id.
		String identifierId = peakIdentificationBatchJob.getPeakIdentificationEntry().getProcessorId();
		int index = getIndex(identifierIds, identifierId);
		if(index >= 0) {
			identifierComboBox.select(index);
		} else {
			if(!identifierId.equals("")) {
				identifierComboBox.add(identifierId);
			}
		}
		// Enable to select no identifier.
		identifierComboBox.add("");
		/*
		 * Report CheckBox
		 */
		reportIdentificationResultsCheckBox.setSelection(peakIdentificationBatchJob.getPeakIdentificationEntry().isReport());
	}

	/*
	 * returns -1 if the value is not in the array.
	 */
	private int getIndex(String[] array, String value) {

		int index = -1;
		for(String entry : array) {
			index++;
			if(entry.equals(value)) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * Creates the run section.
	 * 
	 * @param parent
	 */
	private void createExecuteSection(Composite parent, BatchProcessEditor editorPart) {

		Section section;
		Composite client;
		GridLayout layout;
		GridData gridData;
		Label label;
		/*
		 * Section
		 */
		section = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText("Progress");
		section.setDescription("Run the batch job, after the entries have been edited.");
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		/*
		 * Client
		 */
		client = toolkit.createComposite(section, SWT.WRAP);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalIndent = 20;
		gridData.heightHint = 30;
		/*
		 * Label I
		 */
		label = toolkit.createLabel(client, "1. Edit the input,process and output entries.");
		label.setLayoutData(gridData);
		/*
		 * Link Pages
		 */
		createInputFilesPageHyperlink(editorPart, client, gridData);
		createOutputFilesPageHyperlink(editorPart, client, gridData);
		createBatchResultsPageHyperlink(editorPart, client, gridData);
		/*
		 * Label II
		 */
		label = toolkit.createLabel(client, "2. Run the batch process job after selecting the input files.");
		label.setLayoutData(gridData);
		/*
		 * Link Run
		 * The label is a dirty workaround to make the run button full visible.
		 * Otherwise, it is cutted at its bottom. I think, it has something to
		 * do with the client height.
		 */
		createProcessHyperlink(editorPart, client, gridData);
		label = toolkit.createLabel(client, "");
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		toolkit.paintBordersFor(client);
	}

	/**
	 * Input files page.
	 * 
	 * @param editorPart
	 * @param client
	 * @param gridData
	 */
	private void createInputFilesPageHyperlink(final BatchProcessEditor editorPart, Composite client, GridData gridData) {

		ImageHyperlink imageHyperlink;
		/*
		 * Settings
		 */
		imageHyperlink = toolkit.createImageHyperlink(client, SWT.NONE);
		imageHyperlink.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		imageHyperlink.setText("Configure input files");
		imageHyperlink.setLayoutData(gridData);
		imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			public void linkActivated(HyperlinkEvent e) {

				editorPart.setActivePage(BatchProcessEditor.PEAK_INPUT_FILES_PAGE);
			}
		});
	}

	/**
	 * Output files page.
	 * 
	 * @param editorPart
	 * @param client
	 * @param gridData
	 */
	private void createOutputFilesPageHyperlink(final BatchProcessEditor editorPart, Composite client, GridData gridData) {

		ImageHyperlink imageHyperlink;
		/*
		 * Settings
		 */
		imageHyperlink = toolkit.createImageHyperlink(client, SWT.NONE);
		imageHyperlink.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		imageHyperlink.setText("Configure output files");
		imageHyperlink.setLayoutData(gridData);
		imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			public void linkActivated(HyperlinkEvent e) {

				editorPart.setActivePage(BatchProcessEditor.PEAK_OUTPUT_FILES_PAGE);
			}
		});
	}

	/**
	 * Batch results page.
	 * 
	 * @param editorPart
	 * @param client
	 * @param gridData
	 */
	private void createBatchResultsPageHyperlink(final BatchProcessEditor editorPart, Composite client, GridData gridData) {

		ImageHyperlink imageHyperlink;
		/*
		 * Settings
		 */
		imageHyperlink = toolkit.createImageHyperlink(client, SWT.NONE);
		imageHyperlink.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		imageHyperlink.setText("Peak Integration/Identification results");
		imageHyperlink.setLayoutData(gridData);
		imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			public void linkActivated(HyperlinkEvent e) {

				editorPart.setActivePage(BatchProcessEditor.PEAK_IDENTIFICATION_RESULTS_PAGE);
			}
		});
	}

	/**
	 * Process hyperlink.
	 * 
	 * @param client
	 * @param gridData
	 */
	private void createProcessHyperlink(final BatchProcessEditor editorPart, Composite client, GridData gridData) {

		ImageHyperlink imageHyperlink;
		/*
		 * Settings
		 */
		imageHyperlink = toolkit.createImageHyperlink(client, SWT.WRAP);
		imageHyperlink.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		imageHyperlink.setText("Run the batch process");
		imageHyperlink.setLayoutData(gridData);
		imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			public void linkActivated(HyperlinkEvent e) {

				Display display = Display.getCurrent();
				/*
				 * The file must be saved.
				 * Don't show a progress monitor.
				 */
				if(editorPart.isDirty()) {
					editorPart.doSave(new NullProgressMonitor());
				}
				/*
				 * Run the batch process.
				 */
				IPath path = ((IFileEditorInput)editorPart.getEditorInput()).getFile().getLocation();
				String filePath = path.toFile().toString();
				if(filePath != null && !filePath.equals("")) {
					IRunnableWithProgress runnable = new BatchRunnable(filePath);
					ProgressMonitorDialog monitor = new ProgressMonitorDialog(display.getActiveShell());
					try {
						/*
						 * Clear the results in the results page.
						 */
						SelectionUpdateListener updateListener = new ResultsPage.SelectionUpdateListener();
						updateListener.clear();
						/*
						 * Show the results page, run and refresh the workspace
						 */
						monitor.run(true, true, runnable);
						editorPart.setActivePage(BatchProcessEditor.PEAK_IDENTIFICATION_RESULTS_PAGE);
						refreshWorkspace(path);
					} catch(InvocationTargetException ex) {
						logger.warn(ex);
					} catch(InterruptedException ex) {
						logger.warn(ex);
					}
				}
			}
		});
	}

	private void refreshWorkspace(IPath path) {

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		String workspace = root.getLocation().toFile().toString();
		String file = path.toFile().getParent();
		String containerName = file.replace(workspace, "");
		IResource resource = root.findMember(new Path("/" + containerName));
		if(resource != null && resource.exists() && (resource instanceof IContainer)) {
			IContainer container = (IContainer)resource;
			try {
				container.refreshLocal(1, new NullProgressMonitor());
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
	}
	// ---------------------------------------private methods
}