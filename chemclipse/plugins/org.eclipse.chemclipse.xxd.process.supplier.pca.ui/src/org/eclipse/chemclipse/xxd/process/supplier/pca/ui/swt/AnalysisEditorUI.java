/*******************************************************************************
 * Copyright (c) 2020, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Lorenz Gerber - OPLS Target selection, Group assigner
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.swt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.support.DataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.xxd.process.supplier.pca.io.SampleTemplateIO;
import org.eclipse.chemclipse.xxd.process.supplier.pca.io.WorkspaceIO;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Algorithm;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IEvaluation;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IResult;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.ISamplesPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.LabelOptionPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.xxd.process.supplier.pca.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.Activator;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.runnable.CalculationExecutor;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.wizards.BatchProcessWizardDialog;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.wizards.GroupNamingWizard;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.preferences.PreferencePage;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.preferences.PreferencePageLoadingPlot;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.preferences.PreferencePageScorePlot;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.support.ColorSupport;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;

public class AnalysisEditorUI extends Composite implements IExtendedPartUI {

	private static final Logger logger = Logger.getLogger(AnalysisEditorUI.class);
	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 600;
	private static final String OPLS_GROUP_TARGET_NONE = "--";
	private AtomicReference<Button> buttonToolbarSearchControl = new AtomicReference<>();
	private AtomicReference<SearchSupportUI> toolbarSearch = new AtomicReference<>();
	private AtomicReference<Spinner> spinnerControlPC = new AtomicReference<>();
	private AtomicReference<ComboViewer> comboViewerAlgorithmControl = new AtomicReference<>();
	private AtomicReference<Button> checkboxCrossvalidationControl = new AtomicReference<>();
	private AtomicReference<ComboViewer> comboViewerOplsTarget = new AtomicReference<>();
	private AtomicReference<SamplesListUI> sampleListControl = new AtomicReference<>();
	private AtomicReference<ComboViewer> labelOptionControl = new AtomicReference<>();
	private AtomicReference<PreprocessingSettingsUI> preprocessingSettingsControl = new AtomicReference<>();
	private ISamplesPCA<IVariable, ISample> samples = null;
	private IEvaluation<IVariable, ISample, IResult> evaluation = null;
	private ArrayList<String> oplsGroupTargets = new ArrayList<>();
	private Composite control;

	public AnalysisEditorUI(Composite parent, int style) {

		super(parent, style);
		createControl();
		DataUpdateSupport dataUpdateSupport = Activator.getDefault().getDataUpdateSupport();
		dataUpdateSupport.add((String topic, List<Object> objects) -> {

			if(evaluation != null) {
				if(DataUpdateSupport.isVisible(control)) {
					if(IChemClipseEvents.TOPIC_PCA_UPDATE_HIGHLIGHT_SCORELIST_SAMPLE.equals(topic) || //
							IChemClipseEvents.TOPIC_PCA_UPDATE_HIGHLIGHT_SCOREPLOT_SAMPLE.equals(topic) || //
							IChemClipseEvents.TOPIC_PCA_UPDATE_HIGHLIGHT_SCOREBAR_SAMPLE.equals(topic) || //
							IChemClipseEvents.TOPIC_PCA_UPDATE_HIGHLIGHT_VARIABLELINE_SAMPLE.equals(topic) || //
							IChemClipseEvents.TOPIC_PCA_UPDATE_HIGHLIGHT_ERRORRESIDUALS_SAMPLE.equals(topic)) {
						if(objects.size() == 1) {
							Object object = objects.get(0);
							ArrayList<ISample> samples = new ArrayList<>();
							if(object instanceof Object[] values) {
								for(int i = 0; i < values.length; i++) {
									if(values[i] instanceof ISample sample) {
										samples.add(sample);
									}
								}
								if(!samples.isEmpty()) {
									sampleListControl.get().setSelection(new StructuredSelection(samples));
								}
							}
						}
					}
				}
			}
		});
	}

	public void setInput(ISamplesPCA<IVariable, ISample> samples) {

		this.samples = samples;
		updateOplsGroupTargets();
		applyColorScheme();
		updateControls();
	}

	public void setInput(IEvaluation<IVariable, ISample, IResult> evaluation) {

		this.evaluation = evaluation;
		this.samples = evaluation.getSamples();
		updateOplsGroupTargets();
		applyColorScheme();
		updateControls();
		fireUpdate(Display.getDefault(), evaluation);
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		createToolbarMain(this);
		createDataTab(this);
		initialize();
		control = this;
	}

	private void initialize() {

		enableToolbar(toolbarSearch, buttonToolbarSearchControl.get(), IMAGE_SEARCH, TOOLTIP_EDIT, false);
		oplsGroupTargets.add(OPLS_GROUP_TARGET_NONE);
		comboViewerOplsTarget.get().setInput(oplsGroupTargets);
		comboViewerOplsTarget.get().setSelection(new StructuredSelection(OPLS_GROUP_TARGET_NONE));
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(7, false));
		createLabel(composite, "Number of PCs:");
		createSpinnerPrincipleComponents(composite);
		createLabel(composite, "Algorithm:");
		createComboViewerAlgorithm(composite);
		createButtonRun(composite);
		createCheckboxCrossValidation(composite);
		createSettingsButton(composite);
	}

	private Label createLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		return label;
	}

	private void createSpinnerPrincipleComponents(Composite parent) {

		Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setToolTipText("Number of Principal Components");
		spinner.setMinimum(PreferenceSupplier.MIN_NUMBER_OF_COMPONENTS);
		spinner.setIncrement(1);
		spinner.setSelection(PreferenceSupplier.getNumberOfPrincipalComponents());
		spinner.setMaximum(PreferenceSupplier.MAX_NUMBER_OF_COMPONENTS);
		spinner.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(samples != null) {
					IAnalysisSettings analysisSettings = samples.getAnalysisSettings();
					if(analysisSettings != null) {
						analysisSettings.setNumberOfPrincipalComponents(spinner.getSelection());
					}
				}
			}
		});
		spinnerControlPC.set(spinner);
	}

	private void createComboViewerAlgorithm(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof Algorithm algorithm) {
					return algorithm.label();
				}
				return null;
			}
		});
		Combo combo = comboViewer.getCombo();
		combo.setToolTipText("PCA Algorithm");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof Algorithm algorithm) {
					if(samples != null) {
						IAnalysisSettings analysisSettings = samples.getAnalysisSettings();
						if(analysisSettings != null) {
							analysisSettings.setAlgorithm(algorithm);
							analysisSettings.setOplsTargetGroupName(comboViewerOplsTarget.get().getStructuredSelection().getFirstElement().toString());
							if(Algorithm.OPLS_DA.equals(analysisSettings.getAlgorithm())) {
								comboViewerOplsTarget.get().getControl().setEnabled(true);
							} else {
								comboViewerOplsTarget.get().getControl().setEnabled(false);
							}
						}
					}
				}
			}
		});
		comboViewer.setInput(Algorithm.values());
		comboViewer.setSelection(new StructuredSelection(Algorithm.NIPALS));
		comboViewerAlgorithmControl.set(comboViewer);
	}

	private Button createButtonRun(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Run the Multivariate analysis.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				runCalculation(e.display);
				updateSampleList();
			}
		});
		return button;
	}

	private void createCheckboxCrossValidation(Composite parent) {

		Button button = new Button(parent, SWT.CHECK);
		button.setToolTipText("Run cross-validation if available for selected algorithm.");
		button.setText("Cross Validate");
		button.setSelection(PreferenceSupplier.isCrossValidation());
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(samples != null) {
					IAnalysisSettings analysisSettings = samples.getAnalysisSettings();
					if(analysisSettings != null) {
						analysisSettings.setCrossValidation(button.getSelection());
					}
				}
			}
		});
		checkboxCrossvalidationControl.set(button);
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePage.class, PreferencePageScorePlot.class, PreferencePageLoadingPlot.class), this::applySettings);
	}

	private void createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener((String searchText, boolean caseSensitive) -> sampleListControl.get().setSearchText(searchText, caseSensitive));
		toolbarSearch.set(searchSupportUI);
	}

	private void createNamingWizard(Shell shell) {

		IWizard wizard = new GroupNamingWizard(samples);
		BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(shell, wizard);
		wizardDialog.setMinimumPageSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		try {
			if(wizardDialog.open() == Window.OK) {
				updateSampleList();
			}
		} finally {
			wizard.dispose();
		}
	}

	private TabFolder createDataTab(Composite parent) {

		TabFolder tabFolder = new TabFolder(parent, SWT.BOTTOM);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		createSamplesSection(tabFolder);
		createPreprocessingUI(tabFolder);
		return tabFolder;
	}

	private void createSamplesSection(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Samples");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(1, true));
		createToolbarSamples(composite);
		createToolbarSearch(composite);
		createSampleListUI(composite);
		tabItem.setControl(composite);
	}

	private void createToolbarSamples(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(8, false));
		createButtonToggleToolbar(composite);
		createButtonNamingWizard(composite);
		createComboViewerTargetOPLS(composite);
		createComboViewerLabelOption(composite);
		createButtonColorScheme(composite);
		createButtonImport(composite);
		createButtonExport(composite);
		createButtonSaveWorkspace(composite);
	}

	private void createButtonToggleToolbar(Composite parent) {

		buttonToolbarSearchControl.set(createButtonToggleToolbar(parent, toolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH));
	}

	private void createComboViewerLabelOption(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.BORDER | SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof LabelOptionPCA labelOptionPCA) {
					return labelOptionPCA.label();
				}
				return null;
			}
		});
		combo.setToolTipText("Label Option");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(samples != null) {
					Object object = comboViewer.getStructuredSelection().getFirstElement();
					if(object instanceof LabelOptionPCA labelOptionPCA) {
						IAnalysisSettings analysisSettings = samples.getAnalysisSettings();
						if(analysisSettings != null) {
							analysisSettings.setLabelOptionPCA(labelOptionPCA);
							UpdateNotifierUI.update(e.display, IChemClipseEvents.TOPIC_PCA_UPDATE_LABELS, labelOptionPCA);
						}
					}
				}
			}
		});
		comboViewer.setInput(LabelOptionPCA.values());
		comboViewer.setSelection(new StructuredSelection(LabelOptionPCA.SAMPLE_NAME));
		labelOptionControl.set(comboViewer);
	}

	private void createComboViewerTargetOPLS(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.BORDER | SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof String value) {
					return value;
				} else {
					return "";
				}
			}
		});
		combo.setToolTipText("Using Classification Column for OPLS");
		GridData gridData = new GridData();
		gridData.widthHint = 250;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(samples != null) {
					IAnalysisSettings analysisSettings = samples.getAnalysisSettings();
					if(analysisSettings != null) {
						analysisSettings.setOplsTargetGroupName(comboViewer.getStructuredSelection().getFirstElement().toString());
						if(comboViewer.getStructuredSelection().getFirstElement().toString().equals(OPLS_GROUP_TARGET_NONE)) {
							combo.setToolTipText("Using Classification Column for OPLS");
						} else {
							combo.setToolTipText("Using selected Group against the rest for OPLS");
						}
					}
				}
			}
		});
		comboViewerOplsTarget.set(comboViewer);
	}

	private Button createButtonNamingWizard(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Group Naming Wizard.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				createNamingWizard(getShell());
				// applyColorScheme(e.display);
			}
		});
		return button;
	}

	private Button createButtonColorScheme(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Apply color scheme.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAMPLE_COLORIZE, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				applyColorSchemeDialog();
			}
		});
		return button;
	}

	private Button createButtonImport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Import sample settings.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IMPORT, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(samples != null) {
					FileDialog fileDialog = new FileDialog(getShell(), SWT.READ_ONLY);
					fileDialog.setText("Import");
					fileDialog.setFilterExtensions(SampleTemplateIO.FILTER_EXTENSION);
					fileDialog.setFilterNames(SampleTemplateIO.FILTER_NAME);
					fileDialog.setFilterPath(PreferenceSupplier.getPathImportFile());
					String path = fileDialog.open();
					if(path != null) {
						try {
							PreferenceSupplier.setPathImportFile(fileDialog.getFilterPath());
							File file = new File(path);
							SampleTemplateIO.read(file, samples.getSamples());
							updateSampleList();
						} catch(IOException e1) {
							MessageDialog.openWarning(getShell(), "Import", "Failed to read the sample template.");
						}
					}
				}
			}
		});
		return button;
	}

	private Button createButtonExport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Export sample settings.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXPORT, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(samples != null) {
					FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
					fileDialog.setOverwrite(true);
					fileDialog.setText("Export");
					fileDialog.setFilterExtensions(SampleTemplateIO.FILTER_EXTENSION);
					fileDialog.setFilterNames(SampleTemplateIO.FILTER_NAME);
					fileDialog.setFileName(SampleTemplateIO.FILE_NAME);
					fileDialog.setFilterPath(PreferenceSupplier.getPathExportFile());
					String path = fileDialog.open();
					if(path != null) {
						try {
							PreferenceSupplier.setPathExportFile(fileDialog.getFilterPath());
							File file = new File(path);
							SampleTemplateIO.write(file, samples.getSamples());
							MessageDialog.openInformation(getShell(), "Export", "The sample template has been exported successfully.");
						} catch(FileNotFoundException e1) {
							MessageDialog.openWarning(getShell(), "Export", "The sample template file couldn't be found.");
						}
					}
				}
			}
		});
		return button;
	}

	private Button createButtonSaveWorkspace(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Save workspace (experimental).");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAVE, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
				fileDialog.setOverwrite(true);
				fileDialog.setText("Save Workspace");
				fileDialog.setFilterExtensions(WorkspaceIO.FILTER_EXTENSION);
				fileDialog.setFilterNames(WorkspaceIO.FILTER_NAME);
				fileDialog.setFileName(WorkspaceIO.FILE_NAME);
				fileDialog.setFilterPath(PreferenceSupplier.getPathExportFile());
				String path = fileDialog.open();
				if(path != null) {
					if(evaluation == null) {
						MessageDialog.openWarning(getShell(), "Save Workspace", "No analysis results available. Please run an analysis before saving the workspace.");
						return;
					}
					if(!path.endsWith(WorkspaceIO.FILE_EXTENSION)) {
						path += WorkspaceIO.FILE_EXTENSION;
					}
					try {
						PreferenceSupplier.setPathExportFile(fileDialog.getFilterPath());
						WorkspaceIO.write(new File(path), evaluation);
						MessageDialog.openInformation(getShell(), "Save Workspace", "The workspace has been saved successfully.");
					} catch(IOException e1) {
						MessageDialog.openWarning(getShell(), "Save Workspace", "Failed to save the workspace.");
					}
				}
			}
		});
		return button;
	}

	private void createSampleListUI(Composite composite) {

		SamplesListUI sampleListUI = new SamplesListUI(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		Table table = sampleListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int[] selectedIndices = table.getSelectionIndices();
				ArrayList<Object> selectedElements = new ArrayList<>();
				for(int index : selectedIndices) {
					selectedElements.add(sampleListUI.getElementAt(index));
				}
				handleRowSelection(selectedElements);
			}
		});
		sampleListUI.setUpdateListener(this::updateSampleList);
		sampleListControl.set(sampleListUI);
		ITableSettings tableSettings = sampleListUI.getTableSettings();
		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getCategory() {

				return "";
			}

			@Override
			public String getName() {

				return "Toggle 'Use'";
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				IStructuredSelection selection = sampleListUI.getStructuredSelection();
				@SuppressWarnings("unchecked")
				List<Sample> samples = selection.toList();
				for(Sample sample : samples) {
					sample.setSelected(!sample.isSelected());
				}
				sampleListUI.updateContent();
			}
		});
		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getCategory() {

				return "";
			}

			@Override
			public String getName() {

				return "Apply Selection to Classification Column";
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				Shell shell = extendedTableViewer.getControl().getShell();
				Display display = shell.getDisplay();
				InputDialog inputDialog = new InputDialog(shell, "Apply Selected Samples to Classification columm", "Enter Character to apply:", "", null);
				if(inputDialog.open() == Window.OK) {
					String userInput = inputDialog.getValue();
					if(userInput.length() == 1) {
						for(ISample sample : evaluation.getHighlightedSamples()) {
							sample.setClassification(userInput);
						}
						sampleListUI.updateContent();
					}
				}
				// Main event loop
				while(!shell.isDisposed()) {
					if(!display.readAndDispatch()) {
						display.sleep();
					}
				}
			}
		});
		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getCategory() {

				return "";
			}

			@Override
			public String getName() {

				return "Use All Sample";
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				@SuppressWarnings("unchecked")
				List<Sample> samples = (List<Sample>)sampleListUI.getInput();
				for(Sample sample : samples) {
					sample.setSelected(true);
				}
				sampleListUI.updateContent();
			}
		});
	}

	private void handleRowSelection(List<Object> selectedElements) {

		if(selectedElements.isEmpty()) {
			UpdateNotifierUI.update(getDisplay(), IChemClipseEvents.TOPIC_PCA_UPDATE_HIGHLIGHT_SAMPLE, selectedElements.toArray());
		} else if(Sample.class.isInstance(selectedElements.get(0))) {
			ArrayList<Sample> samples = new ArrayList<>();
			for(Object element : selectedElements) {
				if(Sample.class.isInstance(element)) {
					samples.add((Sample)element);
				}
			}
		}
		UpdateNotifierUI.update(getDisplay(), IChemClipseEvents.TOPIC_PCA_UPDATE_HIGHLIGHT_SAMPLE, selectedElements.toArray());
	}

	private void createPreprocessingUI(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Preprocessing");
		PreprocessingSettingsUI preprocessingSettingsUI = new PreprocessingSettingsUI(tabFolder, SWT.NONE);
		preprocessingSettingsUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		tabItem.setControl(preprocessingSettingsUI);
		preprocessingSettingsControl.set(preprocessingSettingsUI);
	}

	private void applySettings(Display display) {

		if(samples != null) {
			IAnalysisSettings analysisSettings = samples.getAnalysisSettings();
			if(!PreferenceSupplier.getColorScheme().equals(analysisSettings.getColorScheme())) {
				applyColorSchemeDialog();
			}
		}
		updateSampleList();
	}

	private void runCalculation(Display display) {

		if(display != null) {
			try {
				if(samples != null) {
					/*
					 * Run with or without dialog.
					 */
					CalculationExecutor runnable = new CalculationExecutor(samples, evaluation);
					Shell shell = getShell();
					if(shell != null) {
						ProgressMonitorDialog monitor = new ProgressMonitorDialog(shell);
						monitor.run(true, true, runnable);
					} else {
						runnable.run(new NullProgressMonitor());
					}
					evaluation = runnable.getEvaluation();
				}
			} catch(InterruptedException e) {
				logger.warn(e);
				Thread.currentThread().interrupt();
			} catch(InvocationTargetException e) {
				logger.warn(e.getCause());
			}
			fireUpdate(display, evaluation);
		}
	}

	private void updateWidgets(IAnalysisSettings analysisSettings) {

		if(analysisSettings != null) {
			preprocessingSettingsControl.get().setInput(analysisSettings.getPreprocessingSettings());
			spinnerControlPC.get().setSelection(analysisSettings.getNumberOfPrincipalComponents());
			comboViewerAlgorithmControl.get().setSelection(new StructuredSelection(analysisSettings.getAlgorithm()));
			checkboxCrossvalidationControl.get().setSelection(analysisSettings.getCrossValidation());
		} else {
			preprocessingSettingsControl.get().setInput(null);
		}
		/*
		 * OPLS Target
		 */
		updateComboViewerOPLS(analysisSettings);
	}

	private void updateComboViewerOPLS(IAnalysisSettings analysisSettings) {

		comboViewerOplsTarget.get().setInput(oplsGroupTargets);
		String selection = OPLS_GROUP_TARGET_NONE;
		if(analysisSettings != null) {
			String groupName = analysisSettings.getOplsTargetGroupName();
			if(oplsGroupTargets.contains(groupName)) {
				selection = groupName;
			}
		}
		comboViewerOplsTarget.get().setSelection(new StructuredSelection(selection));
	}

	private void fireUpdate(Display display, IEvaluation<IVariable, ISample, IResult> evaluation) {

		UpdateNotifierUI.update(display, IChemClipseEvents.TOPIC_PCA_UPDATE_SELECTION, evaluation);
	}

	private void updateControls() {

		if(samples != null) {
			IAnalysisSettings analysisSettings = samples.getAnalysisSettings();
			updateWidgets(analysisSettings);
			if(analysisSettings != null) {
				labelOptionControl.get().setSelection(new StructuredSelection(analysisSettings.getLabelOptionPCA()));
				if(Algorithm.OPLS.equals(analysisSettings.getAlgorithm())) {
					comboViewerOplsTarget.get().getControl().setEnabled(true);
				} else {
					comboViewerOplsTarget.get().getControl().setEnabled(false);
				}
			}
		} else {
			updateWidgets(null);
		}
	}

	private void applyColorSchemeDialog() {

		if(samples != null) {
			if(MessageDialog.openQuestion(getShell(), "Color Scheme", "Would like to apply the current color scheme on the samples?")) {
				String colorScheme = applyColorScheme();
				if(colorScheme != null) {
					UpdateNotifierUI.update(getDisplay(), IChemClipseEvents.TOPIC_PCA_UPDATE_COLORSCHEME, colorScheme);
				}
			}
		}
	}

	private String applyColorScheme() {

		/*
		 * Create a default mapping, when running the process.
		 */
		String colorScheme = null;
		if(samples != null) {
			/*
			 * Apply the color scheme
			 */
			colorScheme = PreferenceSupplier.getColorScheme();
			IAnalysisSettings analysisSettings = samples.getAnalysisSettings();
			analysisSettings.setColorScheme(colorScheme);
			LabelOptionPCA labelOptionPCA = analysisSettings.getLabelOptionPCA();
			List<ISample> sampleList = samples.getSamples();
			Map<String, Color> colorMap = ColorSupport.getColorMapSamples(sampleList, labelOptionPCA, colorScheme);
			for(ISample sample : sampleList) {
				String colorGroup = ColorSupport.getColorGroup(sample, labelOptionPCA);
				Color color = colorMap.getOrDefault(colorGroup, ColorSupport.COLOR_FALLBACK);
				sample.setRGB(Colors.getColor(color));
			}
			updateSampleList();
		}
		return colorScheme;
	}

	private void updateSampleList() {

		SamplesListUI sampleListUI = sampleListControl.get();
		if(samples != null) {
			sampleListUI.updateInput(samples.getSamples());
		} else {
			sampleListUI.updateInput(null);
		}
		updateOplsGroupTargets();
		updateWidgets(samples.getAnalysisSettings());
	}

	private void updateOplsGroupTargets() {

		oplsGroupTargets.clear();
		oplsGroupTargets.add(OPLS_GROUP_TARGET_NONE);
		if(samples != null) {
			for(ISample sample : samples.getSamples()) {
				if(sample.getGroupName() == null) {
					logger.warn("Fix GroupName (must not be null): " + sample);
					sample.setGroupName("");
				}
			}
			/*
			 * Map Group Names
			 */
			List<String> groups = samples.getSamples().stream().map(x -> x.getGroupName()).distinct().toList();
			oplsGroupTargets.addAll(groups);
			UpdateNotifierUI.update(getDisplay(), IChemClipseEvents.TOPIC_PCA_UPDATE_HIGHLIGHT_SAMPLE, groups.toArray());
		}
	}
}