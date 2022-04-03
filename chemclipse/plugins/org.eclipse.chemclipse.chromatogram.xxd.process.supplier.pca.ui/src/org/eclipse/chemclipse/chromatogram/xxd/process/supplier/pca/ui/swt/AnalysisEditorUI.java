/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.swt;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.ProcessorPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Algorithm;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamplesPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.runnable.CalculationExecutor;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.preferences.PreferencePage;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.preferences.PreferencePageLoadingPlot;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.preferences.PreferencePageScorePlot;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ISettingsHandler;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
	private SamplesListUI sampleListUI;
	private PreprocessingSettingsUI preprocessingSettingsUI;
	private FilterSettingsUI filterSettingsUI;
	//
	private Algorithm[] algorithms = Algorithm.getAlgorithms();

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
		updateSampleList();
		updateControls();
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createToolbarSearch(this);
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
		composite.setLayout(new GridLayout(7, false));
		//
		buttonToolbarSearch = createButtonToggleToolbar(composite, toolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH);
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
		spinner.setSelection(PreferenceSupplier.getNumberOfComponents());
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

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setInput(algorithms);
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof Algorithm) {
					return ((Algorithm)element).getName();
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
				if(object instanceof Algorithm) {
					if(samples != null) {
						IAnalysisSettings analysisSettings = samples.getAnalysisSettings();
						if(analysisSettings != null) {
							analysisSettings.setAlgorithm((Algorithm)object);
						}
					}
				}
			}
		});
		//
		combo.select(0);
		return comboViewer;
	}

	private Button createButtonRun(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Run the PCA analysis.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				runCalculation(e.display);
				fireUpdate(e.display, evaluationPCA);
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

				applySettings();
			}
		});
	}

	private void createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				sampleListUI.setSearchText(searchText, caseSensitive);
			}
		});
		//
		toolbarSearch.set(searchSupportUI);
	}

	private TabFolder createDataTab(Composite parent) {

		TabFolder tabFolder = new TabFolder(parent, SWT.TOP);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		tabFolder.setBackground(Colors.WHITE);
		//
		sampleListUI = createSampleListUI(tabFolder);
		preprocessingSettingsUI = createPreprocessingUI(tabFolder);
		filterSettingsUI = createFilterUI(tabFolder);
		//
		return tabFolder;
	}

	private SamplesListUI createSampleListUI(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Sample List");
		//
		SamplesListUI sampleListUI = new SamplesListUI(tabFolder, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		Table table = sampleListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		tabItem.setControl(table);
		return sampleListUI;
	}

	private PreprocessingSettingsUI createPreprocessingUI(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Preprocessing");
		//
		PreprocessingSettingsUI preprocessingSettingsUI = new PreprocessingSettingsUI(tabFolder, SWT.NONE);
		preprocessingSettingsUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		tabItem.setControl(preprocessingSettingsUI);
		return preprocessingSettingsUI;
	}

	private FilterSettingsUI createFilterUI(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Filter");
		//
		FilterSettingsUI filterSettingsUI = new FilterSettingsUI(tabFolder, SWT.NONE);
		filterSettingsUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		tabItem.setControl(filterSettingsUI);
		return filterSettingsUI;
	}

	private void applySettings() {

	}

	private void runCalculation(Display display) {

		if(display != null) {
			try {
				if(samples != null) {
					CalculationExecutor runnable = new CalculationExecutor(samples);
					Shell shell = display.getActiveShell();
					if(shell != null) {
						ProgressMonitorDialog monitor = new ProgressMonitorDialog(shell);
						monitor.run(true, true, runnable);
					} else {
						runnable.run(new NullProgressMonitor());
					}
					evaluationPCA = runnable.getEvaluationPCA();
				}
			} catch(Exception e) {
				logger.warn(e);
			}
		}
	}

	private void updateWidgets(IAnalysisSettings analysisSettings) {

		if(analysisSettings != null) {
			preprocessingSettingsUI.setInput(analysisSettings.getPreprocessingSettings());
			filterSettingsUI.setInput(analysisSettings.getFilterSettings());
			spinnerPCs.setSelection(analysisSettings.getNumberOfPrincipalComponents());
			comboViewerAlgorithm.getCombo().select(getSelectedAlgorithmIndex(analysisSettings));
		} else {
			preprocessingSettingsUI.setInput(null);
			filterSettingsUI.setInput(null);
		}
	}

	private int getSelectedAlgorithmIndex(IAnalysisSettings analysisSettings) {

		for(int i = 0; i < algorithms.length; i++) {
			Algorithm algorithm = algorithms[i];
			if(algorithm.equals(analysisSettings.getAlgorithm())) {
				return i;
			}
		}
		return -1;
	}

	private void fireUpdate(Display display, EvaluationPCA evaluationPCA) {

		IEventBroker eventBroker = Activator.getDefault().getEventBroker();
		if(eventBroker != null) {
			display.asyncExec(new Runnable() {

				@Override
				public void run() {

					if(evaluationPCA != null) {
						eventBroker.send(ProcessorPCA.TOPIC_PCA_EVALUATION_LOAD, evaluationPCA);
					} else {
						eventBroker.send(ProcessorPCA.TOPIC_PCA_EVALUATION_CLEAR, new Object());
					}
				}
			});
		}
	}

	private void updateControls() {

		if(samples != null) {
			updateWidgets(samples.getAnalysisSettings());
		} else {
			updateWidgets(null);
		}
	}

	private void updateSampleList() {

		if(samples != null) {
			sampleListUI.updateInput(samples.getSampleList());
		} else {
			sampleListUI.updateInput(null);
		}
	}
}
