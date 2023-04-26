/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.swt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedComboViewer;
import org.eclipse.chemclipse.support.ui.swt.dialogs.WindowsFileDialog;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ISettingsHandler;
import org.eclipse.chemclipse.xxd.process.supplier.pca.io.SampleTemplateIO;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Algorithm;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.ISamplesPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.LabelOptionPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.runnable.CalculationExecutor;
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
	private ISamplesPCA<IVariable, ISample> samples = null;
	private EvaluationPCA evaluationPCA = null;
	//
	private Button buttonToolbarSearch;
	private AtomicReference<SearchSupportUI> toolbarSearch = new AtomicReference<>();
	private Spinner spinnerPCs;
	private ComboViewer comboViewerAlgorithm;
	private AtomicReference<SamplesListUI> sampleListControl = new AtomicReference<>();
	private AtomicReference<ComboViewer> labelOptionControl = new AtomicReference<>();
	private AtomicReference<PreprocessingSettingsUI> preprocessingSettingsControl = new AtomicReference<>();
	private AtomicReference<FilterSettingsUI> filterSettingsControl = new AtomicReference<>();

	public AnalysisEditorUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Override
	public boolean setFocus() {

		fireUpdate(this.getDisplay(), evaluationPCA);
		return super.setFocus();
	}

	public void setInput(ISamplesPCA<IVariable, ISample> samples) {

		this.samples = samples;
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
	}

	private void initialize() {

		enableToolbar(toolbarSearch, buttonToolbarSearch, IMAGE_SEARCH, TOOLTIP_EDIT, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(6, false));
		//
		createLabel(composite, "Number of PCs:");
		spinnerPCs = createSpinnerPrincipleComponents(composite);
		createLabel(composite, "Algorithm:");
		comboViewerAlgorithm = createComboViewerAlgorithm(composite);
		createButtonRun(composite);
		createSettingsButton(composite);
	}

	private Label createLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		return label;
	}

	private Spinner createSpinnerPrincipleComponents(Composite parent) {

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
		return spinner;
	}

	private ComboViewer createComboViewerAlgorithm(Composite parent) {

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
						}
					}
				}
			}
		});
		//
		comboViewer.setInput(Algorithm.values());
		combo.select(0);
		return comboViewer;
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

	private TabFolder createDataTab(Composite parent) {

		TabFolder tabFolder = new TabFolder(parent, SWT.TOP);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		tabFolder.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		//
		createSamplesSection(tabFolder);
		createPreprocessingUI(tabFolder);
		createFilterUI(tabFolder);
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
		composite.setLayout(new GridLayout(5, false));
		//
		buttonToolbarSearch = createButtonToggleToolbar(composite, toolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH);
		createComboViewerLabelOption(composite);
		createButtonColorScheme(composite);
		createButtonImport(composite);
		createButtonExport(composite);
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
					if(OperatingSystemUtils.isWindows()) {
						WindowsFileDialog.ClearInitialDirectoryWorkaround();
					}
					FileDialog fileDialog = new FileDialog(e.display.getActiveShell(), SWT.READ_ONLY);
					fileDialog.setText("Import");
					fileDialog.setFilterExtensions(new String[]{SampleTemplateIO.FILTER_EXTENSION});
					fileDialog.setFilterNames(new String[]{SampleTemplateIO.FILTER_NAME});
					fileDialog.setFilterPath(PreferenceSupplier.getPathImportFile());
					String path = fileDialog.open();
					if(path != null) {
						try {
							PreferenceSupplier.setPathImportFile(fileDialog.getFilterPath());
							File file = new File(path);
							SampleTemplateIO.read(file, samples.getSampleList());
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
					if(OperatingSystemUtils.isWindows()) {
						WindowsFileDialog.ClearInitialDirectoryWorkaround();
					}
					FileDialog fileDialog = new FileDialog(e.widget.getDisplay().getActiveShell(), SWT.SAVE);
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
							SampleTemplateIO.write(file, samples.getSampleList());
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

	private void createFilterUI(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Filter");
		//
		FilterSettingsUI filterSettingsUI = new FilterSettingsUI(tabFolder, SWT.NONE);
		filterSettingsUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		tabItem.setControl(filterSettingsUI);
		filterSettingsControl.set(filterSettingsUI);
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
			filterSettingsControl.get().setInput(analysisSettings.getFilterSettings());
			spinnerPCs.setSelection(analysisSettings.getNumberOfPrincipalComponents());
			comboViewerAlgorithm.setSelection(new StructuredSelection(analysisSettings.getAlgorithm()));
		} else {
			preprocessingSettingsControl.get().setInput(null);
			filterSettingsControl.get().setInput(null);
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
			List<ISample> sampleList = samples.getSampleList();
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
			sampleListUI.updateInput(samples.getSampleList());
		} else {
			sampleListUI.updateInput(null);
		}
	}
}