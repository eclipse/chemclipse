/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Algorithm;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamplesPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.runnable.CalculationExecutor;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.preferences.PreferenceLoadingPlot2DPage;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.preferences.PreferencePage;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.preferences.PreferenceScorePlot2DPage;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.window.Window;
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

public class AnalysisEditorUI extends Composite {

	private static final Logger logger = Logger.getLogger(AnalysisEditorUI.class);
	//
	private ISamplesPCA<? extends IVariable, ? extends ISample> samples = null;
	//
	private Composite toolbarSearch;
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

	public void setInput(ISamplesPCA<IVariable, ISample> samples) {

		this.samples = samples;
		if(samples != null) {
			sampleListUI.setInput(samples.getSampleList());
			updateWidgets(samples.getAnalysisSettings());
			preprocessingSettingsUI.setInput(samples.getPreprocessingSettings());
			filterSettingsUI.setInput(samples.getFilterSettings());
		} else {
			sampleListUI.setInput(null);
			updateWidgets(null);
			preprocessingSettingsUI.setInput(null);
			filterSettingsUI.setInput(null);
		}
		//
		// runCalculation(getShell());
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		toolbarSearch = createToolbarSearch(this);
		createDataTab(this);
		//
		PartSupport.setCompositeVisibility(toolbarSearch, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(7, false));
		//
		createButtonToggleToolbarSearch(composite);
		createLabel(composite, "Number of PCs:");
		spinnerPCs = createSpinnerPrincipleComponents(composite);
		createLabel(composite, "Algorithm:");
		comboViewerAlgorithm = createComboViewerAlgorithm(composite);
		createButtonRun(composite);
		createSettingsButton(composite);
	}

	private Button createButtonToggleToolbarSearch(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle search toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarSearch);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
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

				runCalculation(e.display.getActiveShell());
			}
		});
		//
		return button;
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", new PreferencePage()));
				preferenceManager.addToRoot(new PreferenceNode("2", new PreferenceScorePlot2DPage()));
				preferenceManager.addToRoot(new PreferenceNode("3", new PreferenceLoadingPlot2DPage()));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(e.display.getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private Composite createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				sampleListUI.setSearchText(searchText, caseSensitive);
			}
		});
		//
		return searchSupportUI;
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

		// runCalculation();
	}

	private void runCalculation(Shell shell) {

		synchronized(this) {
			if(shell != null) {
				try {
					/*
					 * Check
					 */
					final EvaluationPCA evaluationPCA;
					if(samples != null) {
						CalculationExecutor runnable = new CalculationExecutor(samples);
						ProgressMonitorDialog monitor = new ProgressMonitorDialog(shell);
						monitor.run(true, true, runnable);
						evaluationPCA = runnable.getEvaluationPCA();
					} else {
						evaluationPCA = null;
					}
					/*
					 * Send the update event.
					 */
					IEventBroker eventBroker = Activator.getDefault().getEventBroker();
					if(eventBroker != null) {
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {

								if(evaluationPCA != null) {
									eventBroker.send(Activator.TOPIC_PCA_EVALUATION_LOAD, evaluationPCA);
								} else {
									eventBroker.send(Activator.TOPIC_PCA_EVALUATION_CLEAR, new Object());
								}
							}
						});
					}
				} catch(InvocationTargetException | InterruptedException e) {
					logger.warn(e);
				}
			}
		}
	}

	private void updateWidgets(IAnalysisSettings analysisSettings) {

		if(analysisSettings != null) {
			spinnerPCs.setSelection(analysisSettings.getNumberOfPrincipalComponents());
			comboViewerAlgorithm.getCombo().select(getSelectedAlgorithmIndex(analysisSettings));
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
}
