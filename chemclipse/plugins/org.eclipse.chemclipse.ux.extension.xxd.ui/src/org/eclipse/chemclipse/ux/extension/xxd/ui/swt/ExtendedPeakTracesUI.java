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
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePagePeakTraces;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakDataSupport;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swtchart.extensions.core.IChartSettings;

public class ExtendedPeakTracesUI {

	private Composite toolbarInfo;
	private Label labelPeak;
	private ComboViewer comboViewerTraces;
	private Button buttonDeleteTrace;
	private Button checkboxActive;
	private PeakTracesUI peakTracesUI;
	//
	private IPeak peak = null;
	//
	private final PeakDataSupport peakDataSupport = new PeakDataSupport();

	@Inject
	public ExtendedPeakTracesUI(Composite parent) {
		initialize(parent);
	}

	@Focus
	public void setFocus() {

		update(peak);
	}

	public void update(IPeak peak) {

		this.peak = peak;
		labelPeak.setText(peakDataSupport.getPeakLabel(peak));
		checkboxActive.setSelection((peak != null) ? peak.isActiveForAnalysis() : false);
		updatePeak();
	}

	private void updatePeak() {

		peakTracesUI.setInput(peak);
		updateComboTraces();
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarInfo = createToolbarInfo(parent);
		createPeakChart(parent);
		//
		PartSupport.setCompositeVisibility(toolbarInfo, true);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(7, false));
		//
		createButtonToggleToolbarInfo(composite);
		comboViewerTraces = createComboViewerTraces(composite);
		buttonDeleteTrace = createButtonDeleteTrace(composite);
		checkboxActive = createActiveAnalysisButton(composite);
		createToggleChartSeriesLegendButton(composite);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private Composite createToolbarInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelPeak = new Label(composite, SWT.NONE);
		labelPeak.setText("");
		labelPeak.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Button createButtonToggleToolbarInfo(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle info toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarInfo);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private ComboViewer createComboViewerTraces(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof Integer) {
					return "Trace " + Integer.toString((int)element);
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Select a trace.");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof Integer) {
					int selectedTrace = (int)object;
					buttonDeleteTrace.setEnabled(true);
					peakTracesUI.setSelectedTrace(selectedTrace);
					peakTracesUI.updateChart();
				}
			}
		});
		//
		return comboViewer;
	}

	private Button createButtonDeleteTrace(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Delete the selected trace.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewerTraces.getStructuredSelection().getFirstElement();
				if(object instanceof Integer) {
					int selectedTrace = (int)object;
					if(peak instanceof IPeakMSD) {
						IPeakMSD peakMSD = (IPeakMSD)peak;
						IScanMSD scanMSD = peakMSD.getPeakModel().getPeakMassSpectrum();
						scanMSD.removeIon(selectedTrace);
						peakTracesUI.updateChart();
						updateComboTraces();
					}
				}
			}
		});
		//
		return button;
	}

	private Button createActiveAnalysisButton(Composite parent) {

		Button button = new Button(parent, SWT.CHECK);
		button.setToolTipText("Toogle the active for analysis choice.");
		button.setText("");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(peak != null) {
					boolean activeForAnalysis = button.getSelection();
					peak.setActiveForAnalysis(activeForAnalysis);
				}
			}
		});
		//
		return button;
	}

	private void createToggleChartSeriesLegendButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart series legend.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				peakTracesUI.toggleSeriesLegendVisibility();
			}
		});
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the Overlay");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				reset();
			}
		});
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
				preferenceManager.addToRoot(new PreferenceNode("1", new PreferencePagePeakTraces()));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(e.display.getActiveShell(), "Settings", "Something has gone wrong to apply the chart settings.");
					}
				}
			}
		});
	}

	private void createPeakChart(Composite parent) {

		peakTracesUI = new PeakTracesUI(parent, SWT.BORDER);
		peakTracesUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = peakTracesUI.getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setShowLegendMarker(false);
		peakTracesUI.applySettings(chartSettings);
	}

	private void reset() {

		updatePeak();
	}

	private void applySettings() {

		updatePeak();
	}

	private void updateComboTraces() {

		comboViewerTraces.setInput(peakTracesUI.getTraces());
		buttonDeleteTrace.setEnabled(false);
	}
}
