/*******************************************************************************
 * Copyright (c) 2025, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.swt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.chemclipse.model.notifier.UpdateNotifier;
import org.eclipse.chemclipse.model.supplier.IScanProcessSupplier;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IRegularMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IStandaloneMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.MassSpectrumType;
import org.eclipse.chemclipse.msd.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.swt.ui.support.MassSpectrumUpdateNotifierUI;
import org.eclipse.chemclipse.processing.core.IMessageProvider;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.methods.AbstractProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoPartSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.ux.extension.msd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.ui.methods.MethodSupportUI;
import org.eclipse.chemclipse.ux.extension.ui.support.AuditTrailSupport;
import org.eclipse.chemclipse.ux.extension.ui.swt.ChartGridSupport;
import org.eclipse.chemclipse.ux.extension.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.ScrollableChart;

public class ExtendedMassSpectrumUI extends Composite implements IExtendedPartUI {

	private AtomicReference<Composite> toolbarMainControl = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarSelection = new AtomicReference<>();
	private AtomicReference<MassSpectraSelectionUI> toolbarSelectionControl = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarMethod = new AtomicReference<>();
	private AtomicReference<MethodSupportUI> toolbarMethodControl = new AtomicReference<>();
	private AtomicReference<Button> buttonDeleteBaseline = new AtomicReference<>();
	private AtomicReference<Composite> chartParentControl = new AtomicReference<>();

	private IProcessSupplierContext processTypeSupport = new ProcessTypeSupport();

	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private ChartGridSupport chartGridSupport = new ChartGridSupport();

	private IMassSpectrumChart massSpectrumChart;

	private IMassSpectra massSpectra;
	private IScanMSD massSpectrum;

	public ExtendedMassSpectrumUI(Composite parent, int style) {

		super(parent, style);
	}

	public void update(IMassSpectra massSpectra) {

		this.massSpectra = massSpectra;
		createControl();

		MassSpectraSelectionUI nassSpectraSelectionUI = toolbarSelectionControl.get();
		if(nassSpectraSelectionUI != null) {
			nassSpectraSelectionUI.update(massSpectra);
		}
		MethodSupportUI methodSupportUI = toolbarMethodControl.get();
		if(methodSupportUI != null) {
			methodSupportUI.updateInput();
		}
		if(massSpectra != null && !massSpectra.isEmpty()) {
			massSpectrum = massSpectra.getMassSpectrum(1);
			massSpectrumChart.update(massSpectrum);
		}
	}

	public void update(IScanMSD massSpectrum) {

		this.massSpectrum = massSpectrum;
		if(!recreateChartOnTypeChange()) {
			massSpectrumChart.update(massSpectrum);
		}
	}

	public IScanMSD getMassSpectrum() {

		return massSpectrum;
	}

	public void refresh() {

		if(massSpectrumChart != null && !recreateChartOnTypeChange()) {
			massSpectrumChart.update();
		}
	}

	/*
	 * A conversion from profile to centroid, needs a different chart.
	 */
	private boolean recreateChartOnTypeChange() {

		if(massSpectrumChart == null || !isChartTypeOutdated()) {
			return false;
		}
		Composite composite = chartParentControl.get();
		if(composite == null || composite.isDisposed()) {
			return false;
		}
		if(massSpectrumChart instanceof Control control) {
			control.dispose();
		}
		createMassSpectrumChart(composite);
		Button button = buttonDeleteBaseline.get();
		if(button != null && !button.isDisposed()) {
			button.setEnabled(getMassSpectrumType() == MassSpectrumType.PROFILE);
		}
		composite.layout(true, true);
		return true;
	}

	private boolean isChartTypeOutdated() {

		MassSpectrumType massSpectrumType = getMassSpectrumType();
		if(massSpectrumType == MassSpectrumType.PROFILE) {
			return !(massSpectrumChart instanceof MassSpectrumChartProfile);
		} else if(massSpectrumType == MassSpectrumType.CENTROID) {
			return !(massSpectrumChart instanceof MassSpectrumChartCentroid);
		}
		return false;
	}

	private void createControl() {

		setLayout(new FillLayout());
		createPages(this);
	}

	private void initialize() {

		enableToolbar(toolbarSelectionControl, buttonToolbarSelection.get(), IApplicationImage.IMAGE_EXPAND_ALL, "the selection toolbar.", false);
		enableToolbar(toolbarMethodControl, buttonToolbarMethod.get(), IApplicationImage.IMAGE_METHOD, "the method toolbar.", false);
	}

	private void createPages(Composite parent) {

		/*
		 * Switch from profile to centroid.
		 */
		boolean cleanup = parent.getChildren().length > 0;
		for(Control control : parent.getChildren()) {
			control.dispose();
		}

		if(massSpectra != null && massSpectra.getMassSpectrum(1) != null) {
			massSpectrum = massSpectra.getMassSpectrum(1);
			createMassSpectrumPage(parent);
		} else {
			createErrorMessagePage(parent);
		}

		if(cleanup) {
			parent.layout(true, true);
		}
	}

	private void createMassSpectrumPage(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));

		createToolbarMain(composite);
		createToolbarSelection(composite);
		createToolbarMethod(composite);
		createMassSpectrumChart(composite);

		initialize();
	}

	private void createMassSpectrumChart(Composite composite) {

		chartParentControl.set(composite);
		if(getMassSpectrumType() == MassSpectrumType.PROFILE) {
			massSpectrumChart = new MassSpectrumChartProfile(composite, SWT.BORDER);
		} else if(getMassSpectrumType() == MassSpectrumType.CENTROID) {
			massSpectrumChart = new MassSpectrumChartCentroid(composite, SWT.BORDER);
		} else {
			logger.error("Unknown mass spectrum type.");
		}
		massSpectrumChart.update(massSpectrum);
	}

	private MassSpectrumType getMassSpectrumType() {

		if(massSpectrum instanceof IRegularMassSpectrum regularMassSpectrum) {
			return regularMassSpectrum.getMassSpectrumType();
		} else {
			return null;
		}
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = GridData.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(6, false));

		createButtonDeleteBaseline(composite);
		buttonToolbarSelection.set(createButtonToggleSelection(composite));
		buttonToolbarMethod.set(createButtonToggleMethod(composite));
		createButtonToggleChartGrid(composite);
		createToggleChartSeriesLegendButton(composite);
		createButtonSettings(composite);

		toolbarMainControl.set(composite);
	}

	private void createToolbarSelection(Composite parent) {

		MassSpectraSelectionUI massSpectraSelectionUI = new MassSpectraSelectionUI(parent, SWT.NONE);
		massSpectraSelectionUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		massSpectraSelectionUI.setVisible(PreferenceSupplier.isSelectionComboVisible());
		toolbarSelectionControl.set(massSpectraSelectionUI);
		massSpectraSelectionUI.update(massSpectra);
		massSpectraSelectionUI.addSelectionChangeListener(createSelectionChangedListener());
	}

	private void createToolbarMethod(Composite parent) {

		MethodSupportUI methodSupportUI = new MethodSupportUI(parent, SWT.NONE);
		methodSupportUI.setVisible(PreferenceSupplier.isMethodToolbarVisible());
		methodSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		methodSupportUI.setMethodListener((processMethod, monitor) -> executeMethod(massSpectrum, scanMSD -> {

			IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
			AbstractProcessEntryContainer.applyProcessEntries(processMethod, new ProcessExecutionContext(monitor, processingInfo, processTypeSupport), IScanProcessSupplier.createConsumer(scanMSD));
			scanMSD.setDirty(true);
			if(scanMSD instanceof IStandaloneMassSpectrum standaloneMassSpectrum) {
				AuditTrailSupport.updateAuditTrail(standaloneMassSpectrum, processingInfo, processMethod, processTypeSupport);
			}

			parent.getDisplay().syncExec(() -> {
				update(scanMSD);
				massSpectrumChart.update();
				MassSpectrumUpdateNotifierUI.update(getDisplay(), scanMSD);
				updateResult(processingInfo);
			});
		}));

		toolbarMethodControl.set(methodSupportUI);
	}

	private void executeMethod(IScanMSD massSpectrum, Consumer<IScanMSD> consumer) {

		if(massSpectrum != null) {
			consumer.accept(massSpectrum);
		}
	}

	private void updateResult(IMessageProvider processingInfo) {

		getDisplay().asyncExec(() -> ProcessingInfoPartSupport.getInstance().update(processingInfo, true));
	}

	private void createErrorMessagePage(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		Label label = new Label(composite, SWT.NONE);
		label.setText("The mass spectrum couldn't be loaded.");
	}

	private void createButtonDeleteBaseline(Composite composite) {

		Button button = new Button(composite, SWT.PUSH);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_BASELINE_DELETE, IApplicationImageProvider.SIZE_16x16));
		button.setToolTipText("Delete baseline");
		button.setEnabled(getMassSpectrumType() == MassSpectrumType.PROFILE);
		buttonDeleteBaseline.set(button);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(massSpectrum instanceof IStandaloneMassSpectrum standaloneMassSpectrum) {
					standaloneMassSpectrum.getBaseline().clear();
					update(standaloneMassSpectrum);
					massSpectrumChart.update();
					MassSpectrumUpdateNotifierUI.update(getDisplay(), standaloneMassSpectrum);
				}
			}
		});
	}

	private Button createButtonToggleSelection(Composite parent) {

		Button button = createButtonToggleToolbar(parent, toolbarSelectionControl, IApplicationImage.IMAGE_EXPAND_ALL, "the selection toolbar.");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				preferenceStore.setValue(PreferenceSupplier.P_SHOW_MASS_SPECTRUM_SELECTION_COMBO, toolbarSelectionControl.get().isVisible());
			}
		});

		return button;
	}

	private Button createButtonToggleMethod(Composite parent) {

		Button button = createButtonToggleToolbar(parent, toolbarMethodControl, IApplicationImage.IMAGE_METHOD, "the method toolbar.");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				preferenceStore.setValue(PreferenceSupplier.P_MASS_SPECTRUM_SHOW_METHODS_TOOLBAR, toolbarMethodControl.get().isVisible());
			}
		});

		return button;
	}

	private ISelectionChangedListener createSelectionChangedListener() {

		return event -> {

			if(event.getSelection() instanceof IStructuredSelection selection) {
				if(selection.getFirstElement() instanceof IScanMSD scanMSD) {
					update(scanMSD);
					UpdateNotifier.update(scanMSD);
				}
			}
		};
	}

	private Button createButtonToggleChartGrid(Composite parent) {

		Button button = new Button(parent, SWT.TOGGLE);
		setButtonImage(button, IApplicationImage.IMAGE_GRID, "Show", "Hide", "Chart Grid", false);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(massSpectrumChart instanceof ScrollableChart scrollableChart) {
					IChartSettings chartSettings = scrollableChart.getChartSettings();
					boolean isGridDisplayed = !chartGridSupport.isGridDisplayed(chartSettings);
					chartGridSupport.showGrid(scrollableChart.getChartSettings(), isGridDisplayed);
					scrollableChart.applySettings(chartSettings);
					setButtonImage(button, IApplicationImage.IMAGE_GRID, "Show", "Hide", "Chart Grid", isGridDisplayed);
				}
			}
		});
		return button;
	}

	private void createToggleChartSeriesLegendButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart series legend.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(massSpectrumChart instanceof ScrollableChart scrollableChart) {
					scrollableChart.toggleSeriesLegendVisibility();
				}
			}
		});
	}

	private void createButtonSettings(Composite parent) {

		createSettingsButton(parent, getPreferencePagesSupplier(), _ -> massSpectrumChart.update(), false);
	}

	private Supplier<List<Class<? extends IPreferencePage>>> getPreferencePagesSupplier() {

		return () -> {

			List<Class<? extends IPreferencePage>> preferencePages = new ArrayList<>();
			preferencePages.add(org.eclipse.chemclipse.msd.swt.ui.preferences.PreferencePage.class);
			return preferencePages;
		};
	}

}