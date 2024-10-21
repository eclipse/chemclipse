/*******************************************************************************
 * Copyright (c) 2020, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedComboViewer;
import org.eclipse.chemclipse.support.updates.IUpdateListener;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.DataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IDataUpdateListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ISettingsHandler;
import org.eclipse.chemclipse.xxd.process.supplier.pca.io.SampleTemplateIO;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Algorithm;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.ISamplesPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.LabelOptionPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.ResultPCA;
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
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
	//
	private AtomicReference<Button> buttonToolbarSearchControl = new AtomicReference<>();
	private AtomicReference<SearchSupportUI> toolbarSearch = new AtomicReference<>();
	private AtomicReference<Spinner> spinnerControlPC = new AtomicReference<>();
	private AtomicReference<ComboViewer> comboViewerAlgorithmControl = new AtomicReference<>();
	private AtomicReference<ComboViewer> comboViewerOplsTarget = new AtomicReference<>();
	private AtomicReference<SamplesListUI> sampleListControl = new AtomicReference<>();
	private AtomicReference<ComboViewer> labelOptionControl = new AtomicReference<>();
	private AtomicReference<PreprocessingSettingsUI> preprocessingSettingsControl = new AtomicReference<>();
	//
	private ISamplesPCA<IVariable, ISample> samples = null;
	private EvaluationPCA evaluationPCA = null;
	//
	private Composite control;
	private ArrayList<String> oplsGroupTargets;
	//
	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 600;

	public AnalysisEditorUI(Composite parent, int style) {

		super(parent, style);
		createControl();
		//
		DataUpdateSupport dataUpdateSupport = new DataUpdateSupport(Activator.getDefault().getEventBroker());
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_PCA_UPDATE_RESULT, IChemClipseEvents.EVENT_BROKER_DATA);
		dataUpdateSupport.add(new IDataUpdateListener() {

			@Override
			public void update(String topic, List<Object> objects) {

				if(evaluationPCA != null) {
					if(DataUpdateSupport.isVisible(control)) {
						if(IChemClipseEvents.TOPIC_PCA_UPDATE_RESULT.equals(topic)) {
							if(objects.size() == 1) {
								Object object = objects.get(0);
								ArrayList<ISample> samples = new ArrayList<>();
								if(object instanceof Object[] values) {
									for(int i = 0; i < values.length; i++) {
										if(values[i] instanceof ResultPCA result) {
											samples.add(result.getSample());
										}
									}
								}
								sampleListControl.get().setSelection(new StructuredSelection(samples));
							}
						}
					}
				}
			}
		});
	}

	@Override
	public boolean setFocus() {

		fireUpdate(this.getDisplay(), evaluationPCA);
		return super.setFocus();
	}

	public void setInput(ISamplesPCA<IVariable, ISample> samples) {

		this.samples = samples;
		updateOplsGroupTargets();
		applyColorScheme();
		updateControls();
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createDataTab(this);
		//
		initialize();
		control = this;
	}

	private void initialize() {

		enableToolbar(toolbarSearch, buttonToolbarSearchControl.get(), IMAGE_SEARCH, TOOLTIP_EDIT, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(6, false));
		//
		createLabel(composite, "Number of PCs:");
		createSpinnerPrincipleComponents(composite);
		createLabel(composite, "Algorithm:");
		createComboViewerAlgorithm(composite);
		createButtonRun(composite);
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
		//
		spinnerControlPC.set(spinner);
	}

	private void createComboViewerAlgorithm(Composite parent) {

		ComboViewer comboViewer = new EnhancedComboViewer(parent, SWT.READ_ONLY);
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
		//
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
							if(algorithm.equals(Algorithm.OPLS)) {
								comboViewerOplsTarget.get().getControl().setEnabled(true);
							} else {
								comboViewerOplsTarget.get().getControl().setEnabled(false);
							}
						}
					}
				}
			}
		});
		//
		comboViewer.setInput(Algorithm.values());
		comboViewer.setSelection(new StructuredSelection(Algorithm.NIPALS));
		//
		comboViewerAlgorithmControl.set(comboViewer);
	}

	private Button createButtonRun(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Run the PCA analysis.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				runCalculation(e.display);
				updateSampleList();
			}
		});
		//
		return button;
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePage.class, PreferencePageScorePlot.class, PreferencePageLoadingPlot.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings(display);
			}
		});
	}

	private void createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				sampleListControl.get().setSearchText(searchText, caseSensitive);
			}
		});
		//
		toolbarSearch.set(searchSupportUI);
	}

	private void createNamingWizard(Shell shell) {

		IWizard wizard = new GroupNamingWizard(samples);
		BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(shell, wizard);
		wizardDialog.setMinimumPageSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		//
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
		//
		createSamplesSection(tabFolder);
		createPreprocessingUI(tabFolder);
		//
		return tabFolder;
	}

	private void createSamplesSection(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Samples");
		//
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(1, true));
		//
		createToolbarSamples(composite);
		createToolbarSearch(composite);
		createSampleListUI(composite);
		//
		tabItem.setControl(composite);
	}

	private void createToolbarSamples(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(7, false));
		//
		createButtonToggleToolbar(composite);
		createButtonNamingWizard(composite);
		createComboViewerTargetOPLS(composite);
		createComboViewerLabelOption(composite);
		createButtonColorScheme(composite);
		createButtonImport(composite);
		createButtonExport(composite);
	}

	private void createButtonToggleToolbar(Composite parent) {

		buttonToolbarSearchControl.set(createButtonToggleToolbar(parent, toolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH));
	}

	private void createComboViewerLabelOption(Composite parent) {

		ComboViewer comboViewer = new EnhancedComboViewer(parent, SWT.BORDER | SWT.READ_ONLY);
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
		//
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
		//
		comboViewer.setInput(LabelOptionPCA.values());
		comboViewer.setSelection(new StructuredSelection(LabelOptionPCA.SAMPLE_NAME));
		labelOptionControl.set(comboViewer);
	}

	private void createComboViewerTargetOPLS(Composite parent) {

		ComboViewer comboViewer = new EnhancedComboViewer(parent, SWT.BORDER | SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof String) {
					return (String)element.toString();
				} else {
					return "";
				}
			}
		});
		//
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
						if(comboViewer.getStructuredSelection().getFirstElement().toString().equals("--")) {
							combo.setToolTipText("Using Classification Column for OPLS");
						} else {
							combo.setToolTipText("Using selected Group against the rest for OPLS");
						}
					}
				}
			}
		});
		//
		if(samples == null) {
			oplsGroupTargets = new ArrayList<>();
			oplsGroupTargets.add("--");
			comboViewer.setInput(oplsGroupTargets);
		} else {
			updateOplsGroupTargets();
		}
		comboViewer.setInput(oplsGroupTargets);
		comboViewer.setSelection(new StructuredSelection(oplsGroupTargets.get(0)));
		comboViewerOplsTarget.set(comboViewer);
	}

	private Button createButtonNamingWizard(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Group Naming Wizard.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				createNamingWizard(e.display.getActiveShell());
				// applyColorScheme(e.display);
			}
		});
		//
		return button;
	}

	private Button createButtonColorScheme(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Apply color scheme.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAMPLE_COLORIZE, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				applyColorScheme(e.display);
			}
		});
		//
		return button;
	}

	private Button createButtonImport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Import sample settings.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IMPORT, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(samples != null) {
					FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.READ_ONLY);
					fileDialog.setText("Import");
					fileDialog.setFilterExtensions(new String[]{SampleTemplateIO.FILTER_EXTENSION});
					fileDialog.setFilterNames(new String[]{SampleTemplateIO.FILTER_NAME});
					fileDialog.setFilterPath(PreferenceSupplier.getPathImportFile());
					String path = fileDialog.open();
					if(path != null) {
						try {
							PreferenceSupplier.setPathImportFile(fileDialog.getFilterPath());
							File file = new File(path);
							SampleTemplateIO.read(file, samples.getSamples());
							updateSampleList();
						} catch(IOException e1) {
							MessageDialog.openWarning(e.display.getActiveShell(), "Import", "Failed to read the sample template.");
						}
					}
				}
			}
		});
		//
		return button;
	}

	private Button createButtonExport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Export sample settings.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXPORT, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(samples != null) {
					FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
					fileDialog.setOverwrite(true);
					fileDialog.setText("Export");
					fileDialog.setFilterExtensions(new String[]{SampleTemplateIO.FILTER_EXTENSION});
					fileDialog.setFilterNames(new String[]{SampleTemplateIO.FILTER_NAME});
					fileDialog.setFileName(SampleTemplateIO.FILE_NAME);
					fileDialog.setFilterPath(PreferenceSupplier.getPathExportFile());
					String path = fileDialog.open();
					if(path != null) {
						try {
							PreferenceSupplier.setPathExportFile(fileDialog.getFilterPath());
							File file = new File(path);
							SampleTemplateIO.write(file, samples.getSamples());
							MessageDialog.openInformation(e.display.getActiveShell(), "Export", "The sample template has been exported successfully.");
						} catch(FileNotFoundException e1) {
							MessageDialog.openWarning(e.display.getActiveShell(), "Export", "The sample template file couldn't be found.");
						}
					}
				}
			}
		});
		//
		return button;
	}

	private void createSampleListUI(Composite composite) {

		SamplesListUI sampleListUI = new SamplesListUI(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		Table table = sampleListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		sampleListUI.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				updateSampleList();
			}
		});
		//
		sampleListControl.set(sampleListUI);
	}

	private void createPreprocessingUI(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Preprocessing");
		//
		PreprocessingSettingsUI preprocessingSettingsUI = new PreprocessingSettingsUI(tabFolder, SWT.NONE);
		preprocessingSettingsUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		tabItem.setControl(preprocessingSettingsUI);
		preprocessingSettingsControl.set(preprocessingSettingsUI);
	}

	private void applySettings(Display display) {

		if(samples != null) {
			IAnalysisSettings analysisSettings = samples.getAnalysisSettings();
			if(!PreferenceSupplier.getColorScheme().equals(analysisSettings.getColorScheme())) {
				applyColorScheme(display);
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
					CalculationExecutor runnable = new CalculationExecutor(samples, evaluationPCA);
					Shell shell = display.getActiveShell();
					if(shell != null) {
						ProgressMonitorDialog monitor = new ProgressMonitorDialog(shell);
						monitor.run(true, true, runnable);
					} else {
						runnable.run(new NullProgressMonitor());
					}
					evaluationPCA = runnable.getEvaluationPCA();
				}
			} catch(InterruptedException e) {
				logger.warn(e);
				Thread.currentThread().interrupt();
			} catch(InvocationTargetException e) {
				logger.warn(e.getCause());
			}
			fireUpdate(display, evaluationPCA);
		}
	}

	private void updateWidgets(IAnalysisSettings analysisSettings) {

		if(analysisSettings != null) {
			preprocessingSettingsControl.get().setInput(analysisSettings.getPreprocessingSettings());
			spinnerControlPC.get().setSelection(analysisSettings.getNumberOfPrincipalComponents());
			comboViewerAlgorithmControl.get().setSelection(new StructuredSelection(analysisSettings.getAlgorithm()));
			comboViewerOplsTarget.get().setInput(oplsGroupTargets);
			int selectedIndex = oplsGroupTargets.indexOf(analysisSettings.getOplsTargetGroupName());
			if(selectedIndex != -1) {
				comboViewerOplsTarget.get().setSelection(new StructuredSelection(oplsGroupTargets.get(selectedIndex)));
			} else {
				comboViewerOplsTarget.get().setSelection(new StructuredSelection(oplsGroupTargets.get(0)));
			}
		} else {
			preprocessingSettingsControl.get().setInput(null);
		}
	}

	private void fireUpdate(Display display, EvaluationPCA evaluationPCA) {

		UpdateNotifierUI.update(display, IChemClipseEvents.TOPIC_PCA_UPDATE_SELECTION, evaluationPCA);
	}

	private void updateControls() {

		if(samples != null) {
			IAnalysisSettings analysisSettings = samples.getAnalysisSettings();
			updateWidgets(analysisSettings);
			if(analysisSettings != null) {
				labelOptionControl.get().setSelection(new StructuredSelection(analysisSettings.getLabelOptionPCA()));
				if(analysisSettings.getAlgorithm() == Algorithm.OPLS) {
					comboViewerOplsTarget.get().getControl().setEnabled(true);
				} else {
					comboViewerOplsTarget.get().getControl().setEnabled(false);
				}
			}
		} else {
			updateWidgets(null);
		}
	}

	private void applyColorScheme(Display display) {

		if(samples != null) {
			if(MessageDialog.openQuestion(display.getActiveShell(), "Color Scheme", "Would like to apply the current color scheme on the samples?")) {
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
			//
			List<ISample> sampleList = samples.getSamples();
			Map<String, Color> colorMap = ColorSupport.getColorMapSamples(sampleList, labelOptionPCA, colorScheme);
			for(ISample sample : sampleList) {
				String colorGroup = ColorSupport.getColorGroup(sample, labelOptionPCA);
				Color color = colorMap.getOrDefault(colorGroup, ColorSupport.COLOR_FALLBACK);
				sample.setRGB(Colors.getColor(color));
			}
			updateSampleList();
		}
		//
		return colorScheme;
	}

	private void updateSampleList() {

		SamplesListUI sampleListUI = sampleListControl.get();
		if(samples != null) {
			sampleListUI.updateInput(samples.getSamples());
			updateOplsGroupTargets();
			updateWidgets(samples.getAnalysisSettings());
		} else {
			sampleListUI.updateInput(null);
		}
	}

	private void updateOplsGroupTargets() {

		if(samples != null) {
			List<String> fromSamples = new ArrayList<>();
			fromSamples.add("--");
			fromSamples.addAll(samples.getSamples().stream().map(x -> x.getGroupName()).distinct().toList());
			if(!oplsGroupTargets.toString().equals(fromSamples.toString())) {
				oplsGroupTargets.clear();
				oplsGroupTargets.add("--");
				oplsGroupTargets.addAll(samples.getSamples().stream().map(x -> x.getGroupName()).distinct().toList());
			}
		}
	}
}