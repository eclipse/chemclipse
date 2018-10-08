/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.OverlayChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.IonsValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.WavelengthValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageOverlay;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelengths;
import org.eclipse.chemclipse.wsd.model.core.support.MarkedWavelengths;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.eavp.service.swtchart.core.BaseChart;
import org.eclipse.eavp.service.swtchart.core.IAxisScaleConverter;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.core.IExtendedChart;
import org.eclipse.eavp.service.swtchart.core.ISeriesModificationListener;
import org.eclipse.eavp.service.swtchart.core.SeriesStatusAdapter;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesData;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.swtchart.ISeries;

public class ExtendedChromatogramOverlayUI {

	private static final Logger logger = Logger.getLogger(ExtendedChromatogramOverlayUI.class);
	//
	private Composite toolbarType;
	private Composite toolbarProfile;
	private Composite toolbarShift;
	private ChromatogramChart chromatogramChart;
	//
	private Combo comboOverlayTypeDefault;
	private Combo comboOverlayTypeProfile;
	private Combo comboDerivativeType;
	private Combo comboSelectedSeriesDefault;
	private Combo comboSelectedSeriesShift;
	private Combo comboDisplayModus;
	private Combo comboSelectedIons;
	private Text textSignalsFromSettings;
	private Text textShiftX;
	private Combo comboScaleX;
	private Button buttonShiftLeft;
	private Button buttonShiftRight;
	private Text textShiftY;
	private Combo comboScaleY;
	private Button buttonShiftUp;
	private Button buttonShiftDown;
	private Label labelDataStatus;
	//
	private ChromatogramChartSupport chromatogramChartSupport = new ChromatogramChartSupport();
	private OverlayChartSupport overlayChartSupport = new OverlayChartSupport();
	private ControlDecoration controlDecoration;
	private Set<String> mirroredSeries = new HashSet<String>();
	//
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	//
	@SuppressWarnings("rawtypes")
	private List<IChromatogramSelection> chromatogramSelections = new ArrayList<>();

	@Inject
	public ExtendedChromatogramOverlayUI(Composite parent) {

		initialize(parent);
	}

	@SuppressWarnings("rawtypes")
	public void update(List<IChromatogramSelection> chromatogramSelections) {

		this.chromatogramSelections.clear();
		this.chromatogramSelections.addAll(chromatogramSelections);
		refreshUpdateOverlayChart();
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarType = createToolbarType(parent);
		toolbarProfile = createToolbarProfile(parent);
		toolbarShift = createToolbarShift(parent);
		createOverlayChart(parent);
		/*
		 * Hide both toolbars initially.
		 * Enable/disable widgets.
		 */
		PartSupport.setCompositeVisibility(toolbarType, false);
		PartSupport.setCompositeVisibility(toolbarProfile, false);
		PartSupport.setCompositeVisibility(toolbarShift, false);
		modifyWidgetStatus();
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(8, false));
		//
		createDataStatusLabel(composite);
		createButtonToggleToolbarType(composite);
		createButtonToggleToolbarProfile(composite);
		createButtonToggleToolbarShift(composite);
		createToggleChartLegendButton(composite);
		createResetButton(composite);
		createNewOverlayPartButton(composite);
		createSettingsButton(composite);
	}

	private Composite createToolbarType(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(7, false));
		//
		comboOverlayTypeDefault = createOverlayTypeCombo(composite);
		createDerivativeTypeCombo(composite);
		createVerticalSeparator(composite);
		comboSelectedSeriesDefault = createSelectedSeriesCombo(composite);
		createDisplayModusCombo(composite);
		createVerticalSeparator(composite);
		createButtonAutoMirror(composite);
		//
		return composite;
	}

	private Composite createToolbarProfile(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		comboOverlayTypeProfile = createOverlayTypeCombo(composite);
		createSelectedIonsCombo(composite);
		createIonsFromText(composite);
		//
		String ionSelection = comboSelectedIons.getText().trim();
		setSelectedIonsText(ionSelection);
		//
		return composite;
	}

	private Composite createToolbarShift(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(13, false));
		//
		createTextShiftX(composite);
		createComboScaleX(composite);
		createTextShiftY(composite);
		createComboScaleY(composite);
		createVerticalSeparator(composite);
		comboSelectedSeriesShift = createSelectedSeriesCombo(composite);
		createButtonLeft(composite);
		createButtonRight(composite);
		createButtonUp(composite);
		createButtonDown(composite);
		createVerticalSeparator(composite);
		createButtonShiftY(composite);
		createButtonShiftXY(composite);
		//
		return composite;
	}

	private Combo createOverlayTypeCombo(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setToolTipText("Select the overlay type");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 150;
		gridData.grabExcessHorizontalSpace = true;
		combo.setLayoutData(gridData);
		combo.setItems(overlayChartSupport.getOverlayTypes());
		combo.select(0);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = combo.getSelectionIndex();
				comboOverlayTypeDefault.select(index);
				comboOverlayTypeProfile.select(index);
				//
				modifyWidgetStatus();
				refreshUpdateOverlayChart();
			}
		});
		//
		return combo;
	}

	private void createDerivativeTypeCombo(Composite parent) {

		comboDerivativeType = new Combo(parent, SWT.READ_ONLY);
		comboDerivativeType.setToolTipText("Select the derivative type");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 60;
		gridData.grabExcessHorizontalSpace = true;
		comboDerivativeType.setLayoutData(gridData);
		comboDerivativeType.setItems(overlayChartSupport.getDerivativeTypes());
		comboDerivativeType.select(0);
		comboDerivativeType.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IChartSettings chartSettings = chromatogramChart.getChartSettings();
				String derivativeType = comboDerivativeType.getText();
				if(ChromatogramChartSupport.DERIVATIVE_NONE.equals(derivativeType)) {
					if(comboDisplayModus.getText().equals(OverlayChartSupport.DISPLAY_MODUS_NORMAL)) {
						chartSettings.getRangeRestriction().setZeroY(false);
					}
				} else {
					chartSettings.getRangeRestriction().setZeroY(false);
				}
				chromatogramChart.applySettings(chartSettings);
				refreshUpdateOverlayChart();
			}
		});
	}

	private Combo createSelectedSeriesCombo(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setToolTipText("Highlight the selected series");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 150;
		gridData.grabExcessHorizontalSpace = true;
		combo.setLayoutData(gridData);
		combo.setItems(new String[]{BaseChart.SELECTED_SERIES_NONE});
		combo.select(0);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = combo.getSelectionIndex();
				comboSelectedSeriesDefault.select(index);
				comboSelectedSeriesShift.select(index);
				//
				String selectedSeriesId = combo.getText().trim();
				BaseChart baseChart = chromatogramChart.getBaseChart();
				baseChart.resetSeriesSettings();
				baseChart.selectSeries(selectedSeriesId);
				baseChart.redraw();
				modifyWidgetStatus();
			}
		});
		//
		return combo;
	}

	private void createDisplayModusCombo(Composite parent) {

		comboDisplayModus = new Combo(parent, SWT.READ_ONLY);
		comboDisplayModus.setToolTipText("Select the display modus.");
		comboDisplayModus.setItems(overlayChartSupport.getDisplayModi());
		comboDisplayModus.setText(OverlayChartSupport.DISPLAY_MODUS_NORMAL);
		comboDisplayModus.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboDisplayModus.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				BaseChart baseChart = chromatogramChart.getBaseChart();
				IChartSettings chartSettings = chromatogramChart.getChartSettings();
				String displayModus = comboDisplayModus.getText().trim();
				String seriesId = getSelectedSeriesId();
				//
				if(displayModus.equals(OverlayChartSupport.DISPLAY_MODUS_MIRRORED)) {
					if(!mirroredSeries.contains(seriesId)) {
						baseChart.multiplySeries(seriesId, IExtendedChart.Y_AXIS, -1.0d);
						mirroredSeries.add(seriesId);
						chartSettings.getRangeRestriction().setZeroY(false);
					}
				} else {
					if(mirroredSeries.contains(seriesId)) {
						baseChart.multiplySeries(seriesId, IExtendedChart.Y_AXIS, -1.0d);
						mirroredSeries.remove(seriesId);
					}
				}
				//
				chromatogramChart.applySettings(chartSettings);
				chromatogramChart.adjustRange(true);
				chromatogramChart.redraw();
			}
		});
	}

	private void createButtonAutoMirror(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Auto Mirror Chromatograms");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SHIFT_AUTO_MIRROR, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				BaseChart baseChart = chromatogramChart.getBaseChart();
				IChartSettings chartSettings = chromatogramChart.getChartSettings();
				chartSettings.getRangeRestriction().setZeroY(false);
				//
				int i = 0;
				for(ISeries series : baseChart.getSeriesSet().getSeries()) {
					if(i % 2 == 1) {
						String seriesId = series.getId();
						if(!mirroredSeries.contains(seriesId)) {
							baseChart.multiplySeries(seriesId, IExtendedChart.Y_AXIS, -1.0d);
							mirroredSeries.add(seriesId);
						}
					}
					i++;
				}
				//
				chromatogramChart.applySettings(chartSettings);
				chromatogramChart.adjustRange(true);
				chromatogramChart.redraw();
			}
		});
	}

	private void createSelectedIonsCombo(Composite parent) {

		/*
		 * Get the settings.
		 */
		String[] items = overlayChartSupport.getSelectedIons();
		String overlaySelection = preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_SELECTION);
		int index = 0;
		for(int i = 0; i < items.length; i++) {
			String item = items[i];
			if(overlaySelection.equals(item)) {
				index = i;
			}
		}
		//
		comboSelectedIons = new Combo(parent, SWT.READ_ONLY);
		comboSelectedIons.setToolTipText("Select the overlay ions.");
		comboSelectedIons.setItems(overlayChartSupport.getSelectedIons());
		comboSelectedIons.select(index);
		comboSelectedIons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboSelectedIons.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean extractedIonsModusEnabled = isExtractedIonsModusEnabled();
				textSignalsFromSettings.setEnabled(extractedIonsModusEnabled);
				String ionSelection = comboSelectedIons.getText().trim();
				setSelectedIonsText(ionSelection);
				chromatogramChart.deleteSeries();
				refreshUpdateOverlayChart();
			}
		});
	}

	private void setSelectedIonsText(String ionSelection) {

		switch(ionSelection) {
			case OverlayChartSupport.SELECTED_IONS_USERS_CHOICE:
				textSignalsFromSettings.setText(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_USERS_CHOICE));
				preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_SELECTION, OverlayChartSupport.SELECTED_IONS_USERS_CHOICE);
				break;
			case OverlayChartSupport.SELECTED_IONS_HYDROCARBONS:
				textSignalsFromSettings.setText(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_HYDROCARBONS));
				preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_SELECTION, OverlayChartSupport.SELECTED_IONS_HYDROCARBONS);
				break;
			case OverlayChartSupport.SELECTED_IONS_FATTY_ACIDS:
				textSignalsFromSettings.setText(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_FATTY_ACIDS));
				preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_SELECTION, OverlayChartSupport.SELECTED_IONS_FATTY_ACIDS);
				break;
			case OverlayChartSupport.SELECTED_IONS_FAME:
				textSignalsFromSettings.setText(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_FAME));
				preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_SELECTION, OverlayChartSupport.SELECTED_IONS_FAME);
				break;
			case OverlayChartSupport.SELECTED_IONS_SOLVENT_TAILING:
				textSignalsFromSettings.setText(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_SOLVENT_TAILING));
				preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_SELECTION, OverlayChartSupport.SELECTED_IONS_SOLVENT_TAILING);
				break;
			case OverlayChartSupport.SELECTED_IONS_COLUMN_BLEED:
				textSignalsFromSettings.setText(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_COLUMN_BLEED));
				preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_SELECTION, OverlayChartSupport.SELECTED_IONS_COLUMN_BLEED);
				break;
			default:
				textSignalsFromSettings.setText("");
				break;
		}
	}

	private void createIonsFromText(Composite parent) {

		textSignalsFromSettings = new Text(parent, SWT.BORDER);
		textSignalsFromSettings.setText(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_USERS_CHOICE));
		textSignalsFromSettings.setToolTipText("Users choice overlay signals (ions or wavelengths).");
		textSignalsFromSettings.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		textSignalsFromSettings.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					chromatogramChart.deleteSeries();
					refreshUpdateOverlayChart();
				} else {
					IonsValidator ionsValidator = new IonsValidator();
					if(validate(ionsValidator, controlDecoration, textSignalsFromSettings)) {
						String ionsAsText = ionsValidator.getIonsAsString();
						String ionSelection = comboSelectedIons.getText().trim();
						switch(ionSelection) {
							case OverlayChartSupport.SELECTED_IONS_USERS_CHOICE:
								preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_USERS_CHOICE, ionsAsText);
								break;
							case OverlayChartSupport.SELECTED_IONS_HYDROCARBONS:
								preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_HYDROCARBONS, ionsAsText);
								break;
							case OverlayChartSupport.SELECTED_IONS_FATTY_ACIDS:
								preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_FATTY_ACIDS, ionsAsText);
								break;
							case OverlayChartSupport.SELECTED_IONS_FAME:
								preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_FAME, ionsAsText);
								break;
							case OverlayChartSupport.SELECTED_IONS_SOLVENT_TAILING:
								preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_SOLVENT_TAILING, ionsAsText);
								break;
							case OverlayChartSupport.SELECTED_IONS_COLUMN_BLEED:
								preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_COLUMN_BLEED, ionsAsText);
								break;
							default:
								textSignalsFromSettings.setText("");
								break;
						}
					}
				}
			}
		});
		//
		controlDecoration = new ControlDecoration(textSignalsFromSettings, SWT.LEFT | SWT.TOP);
	}

	private boolean validate(IValidator validator, ControlDecoration controlDecoration, Text text) {

		IStatus status = validator.validate(text.getText());
		if(status.isOK()) {
			controlDecoration.hide();
			return true;
		} else {
			controlDecoration.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage());
			controlDecoration.showHoverText(status.getMessage());
			controlDecoration.show();
			return false;
		}
	}

	private void createTextShiftX(Composite parent) {

		textShiftX = new Text(parent, SWT.BORDER);
		textShiftX.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		textShiftX.setLayoutData(gridData);
	}

	private void createComboScaleX(Composite parent) {

		comboScaleX = new Combo(parent, SWT.READ_ONLY);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		comboScaleX.setLayoutData(gridData);
	}

	private void createButtonLeft(Composite parent) {

		buttonShiftLeft = new Button(parent, SWT.PUSH);
		buttonShiftLeft.setToolTipText("Move Left");
		buttonShiftLeft.setText("");
		buttonShiftLeft.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_BACKWARD, IApplicationImage.SIZE_16x16));
		buttonShiftLeft.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				BaseChart baseChart = chromatogramChart.getBaseChart();
				double shiftX = getShiftValuePrimary(IExtendedChart.X_AXIS) * -1.0d;
				String selectedSeriesId = getSelectedSeriesId();
				baseChart.shiftSeries(selectedSeriesId, shiftX, 0.0d);
				baseChart.redraw();
				persistOverlayShiftX();
			}
		});
	}

	private void createButtonRight(Composite parent) {

		buttonShiftRight = new Button(parent, SWT.PUSH);
		buttonShiftRight.setToolTipText("Move Right");
		buttonShiftRight.setText("");
		buttonShiftRight.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_FORWARD, IApplicationImage.SIZE_16x16));
		buttonShiftRight.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				BaseChart baseChart = chromatogramChart.getBaseChart();
				double shiftX = getShiftValuePrimary(IExtendedChart.X_AXIS);
				String selectedSeriesId = getSelectedSeriesId();
				baseChart.shiftSeries(selectedSeriesId, shiftX, 0.0d);
				baseChart.redraw();
				persistOverlayShiftX();
			}
		});
	}

	private void createTextShiftY(Composite parent) {

		textShiftY = new Text(parent, SWT.BORDER);
		textShiftY.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		textShiftY.setLayoutData(gridData);
	}

	private void createComboScaleY(Composite parent) {

		comboScaleY = new Combo(parent, SWT.READ_ONLY);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		comboScaleY.setLayoutData(gridData);
	}

	private void createButtonUp(Composite parent) {

		buttonShiftUp = new Button(parent, SWT.PUSH);
		buttonShiftUp.setToolTipText("Move Up");
		buttonShiftUp.setText("");
		buttonShiftUp.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_UP_2, IApplicationImage.SIZE_16x16));
		buttonShiftUp.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				BaseChart baseChart = chromatogramChart.getBaseChart();
				double shiftY = getShiftValuePrimary(IExtendedChart.Y_AXIS);
				String selectedSeriesId = getSelectedSeriesId();
				baseChart.shiftSeries(selectedSeriesId, 0.0d, shiftY);
				baseChart.redraw();
				persistOverlayShiftY();
			}
		});
	}

	private void createButtonDown(Composite parent) {

		buttonShiftDown = new Button(parent, SWT.PUSH);
		buttonShiftDown.setToolTipText("Move Down");
		buttonShiftDown.setText("");
		buttonShiftDown.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_DOWN_2, IApplicationImage.SIZE_16x16));
		buttonShiftDown.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				BaseChart baseChart = chromatogramChart.getBaseChart();
				double shiftY = getShiftValuePrimary(IExtendedChart.Y_AXIS) * -1.0d;
				String selectedSeriesId = getSelectedSeriesId();
				baseChart.shiftSeries(selectedSeriesId, 0.0d, shiftY);
				baseChart.redraw();
				persistOverlayShiftY();
			}
		});
	}

	private void createButtonShiftY(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Shift Y");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SHIFT_Y, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				applyOverlaySettings();
				//
				BaseChart baseChart = chromatogramChart.getBaseChart();
				baseChart.suspendUpdate(true);
				double shiftY = 0.0d;
				double deltaY = getShiftValuePrimary(IExtendedChart.Y_AXIS);
				for(ISeries series : baseChart.getSeriesSet().getSeries()) {
					shiftY += deltaY;
					String seriesId = series.getId();
					baseChart.shiftSeries(seriesId, 0.0d, shiftY);
				}
				baseChart.suspendUpdate(false);
				baseChart.redraw();
				persistOverlayShiftY();
			}
		});
	}

	private void createButtonShiftXY(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Shift XY");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SHIFT_XY, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				applyOverlaySettings();
				//
				BaseChart baseChart = chromatogramChart.getBaseChart();
				baseChart.suspendUpdate(true);
				double shiftX = 0.0d;
				double shiftY = 0.0d;
				double deltaX = getShiftValuePrimary(IExtendedChart.X_AXIS);
				double deltaY = getShiftValuePrimary(IExtendedChart.Y_AXIS);
				for(ISeries series : baseChart.getSeriesSet().getSeries()) {
					shiftX += deltaX;
					shiftY += deltaY;
					String seriesId = series.getId();
					baseChart.shiftSeries(seriesId, shiftX, shiftY);
				}
				baseChart.suspendUpdate(false);
				baseChart.redraw();
				persistOverlayShiftX();
				persistOverlayShiftY();
			}
		});
	}

	private void createDataStatusLabel(Composite parent) {

		labelDataStatus = new Label(parent, SWT.NONE);
		labelDataStatus.setToolTipText("Indicates whether the data has been modified or not.");
		labelDataStatus.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 120;
		labelDataStatus.setLayoutData(gridData);
	}

	private void createButtonToggleToolbarType(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle type toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarType);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
				}
			}
		});
	}

	private void createButtonToggleToolbarProfile(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle profile toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM_PROFILE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarProfile);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM_PROFILE, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM_PROFILE, IApplicationImage.SIZE_16x16));
				}
			}
		});
	}

	private void createButtonToggleToolbarShift(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle shift toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_SHIFT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarShift);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_SHIFT, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_SHIFT, IApplicationImage.SIZE_16x16));
				}
			}
		});
	}

	private void createToggleChartLegendButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart legend");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chromatogramChart.toggleSeriesLegendVisibility();
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

				applyOverlaySettings();
			}
		});
	}

	private void createNewOverlayPartButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open a new Overlay");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PLUS, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String bundle = Activator.getDefault().getBundle().getSymbolicName();
				String classPath = PartSupport.PART_OVERLAY_CHROMATOGRAM;
				String name = "Chromatogram Overlay";
				createNewPart(bundle, classPath, name);
			}
		});
	}

	private void createNewPart(String bundle, String classPath, String name) {

		String partStackId = preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_OVERLAY_CHROMATOGRAM_EXTRA);
		if(!partStackId.equals(PartSupport.PARTSTACK_NONE)) {
			MPart part = MBasicFactory.INSTANCE.createPart();
			part.setLabel(name);
			part.setCloseable(true);
			part.setContributionURI("bundleclass://" + bundle + "/" + classPath);
			//
			MPartStack partStack = PartSupport.getPartStack(partStackId);
			partStack.getChildren().add(part);
			PartSupport.showPart(part);
		}
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePageOverlay = new PreferencePageOverlay();
				preferencePageOverlay.setTitle("Overlay Settings");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePageOverlay));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(DisplayUtils.getShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				//
				if(preferenceDialog.open() == Window.OK) {
					try {
						applyOverlaySettings();
					} catch(Exception e1) {
						System.out.println(e1);
						MessageDialog.openError(e.widget.getDisplay().getActiveShell(), "Settings", "Something has gone wrong to apply the chart settings.");
					}
				}
			}
		});
	}

	private void modifyDataStatusLabel() {

		if(chromatogramChart.getBaseChart().isDataShifted()) {
			labelDataStatus.setText("Shifted Data");
			labelDataStatus.setBackground(Colors.YELLOW);
		} else {
			labelDataStatus.setText("");
			labelDataStatus.setBackground(null);
		}
	}

	private void modifyWidgetStatus() {

		/*
		 * Overlay Type
		 */
		if(isExtractedIonsModusEnabled()) {
			comboSelectedIons.setEnabled(true);
			textSignalsFromSettings.setEnabled(true);
			String ionSelection = comboSelectedIons.getText().trim();
			setSelectedIonsText(ionSelection);
		} else if(isExtractedWavelengthsModusEnabled()) {
			comboSelectedIons.setEnabled(false);
			textSignalsFromSettings.setEnabled(true);
			textSignalsFromSettings.setText("");
		} else {
			comboSelectedIons.setEnabled(false);
			textSignalsFromSettings.setEnabled(false);
			textSignalsFromSettings.setText("");
		}
		/*
		 * Selected Series
		 */
		String selectedSeries = getSelectedSeriesId();
		boolean isSeriesSelected = !selectedSeries.equals(BaseChart.SELECTED_SERIES_NONE);
		comboDisplayModus.setEnabled(isSeriesSelected);
		buttonShiftLeft.setEnabled(isSeriesSelected);
		buttonShiftRight.setEnabled(isSeriesSelected);
		buttonShiftUp.setEnabled(isSeriesSelected);
		buttonShiftDown.setEnabled(isSeriesSelected);
	}

	private boolean isExtractedIonsModusEnabled() {

		String overlayType = getOverlayType();
		return (overlayType.contains(ChromatogramChartSupport.DISPLAY_TYPE_XIC) || //
				overlayType.contains(ChromatogramChartSupport.DISPLAY_TYPE_SIC) || //
				overlayType.contains(ChromatogramChartSupport.DISPLAY_TYPE_TSC));
	}

	private boolean isExtractedWavelengthsModusEnabled() {

		String overlayType = getOverlayType();
		return overlayType.contains(ChromatogramChartSupport.DISPLAY_TYPE_SWC);
	}

	private void applyOverlaySettings() {

		chromatogramChartSupport.loadUserSettings();
		chromatogramChart.deleteSeries();
		textSignalsFromSettings.setText(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_USERS_CHOICE));
		mirroredSeries.clear();
		refreshUpdateOverlayChart();
		modifyWidgetStatus();
		modifyDataStatusLabel();
	}

	private void createOverlayChart(Composite parent) {

		chromatogramChart = new ChromatogramChart(parent, SWT.BORDER);
		chromatogramChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setEnableRangeSelector(true);
		chartSettings.setShowRangeSelectorInitially(false);
		chartSettings.setSupportDataShift(true);
		chromatogramChart.applySettings(chartSettings);
		//
		BaseChart baseChart = chromatogramChart.getBaseChart();
		baseChart.addSeriesModificationListener(new ISeriesModificationListener() {

			@Override
			public void handleSeriesModificationEvent() {

				modifyDataStatusLabel();
			}
		});
		//
		baseChart.addSeriesStatusListener(new SeriesStatusAdapter() {

			@Override
			public void handleSeriesSelectionEvent(String seriesId) {

				comboSelectedSeriesDefault.setText(seriesId);
				comboSelectedSeriesShift.setText(seriesId);
				modifyWidgetStatus();
			}
		});
		//
		setComboAxisItems();
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void refreshUpdateOverlayChart() {

		if(chromatogramSelections.size() > 0) {
			Set<String> availableSeriesIds = new HashSet<String>();
			BaseChart baseChart = chromatogramChart.getBaseChart();
			comboDisplayModus.setText(OverlayChartSupport.DISPLAY_MODUS_NORMAL);
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
			//
			for(int i = 0; i < chromatogramSelections.size(); i++) {
				IChromatogramSelection chromatogramSelection = chromatogramSelections.get(i);
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				List<IChromatogram> referencedChromatograms = chromatogram.getReferencedChromatograms();
				String chromatogramName = chromatogram.getName() + ChromatogramChartSupport.EDITOR_TAB + (i + 1);
				/*
				 * Select which series shall be displayed.
				 */
				String[] overlayTypes = getOverlayType().split(OverlayChartSupport.ESCAPE_CONCATENATOR + OverlayChartSupport.OVERLAY_TYPE_CONCATENATOR);
				String derivativeType = comboDerivativeType.getText().trim();
				//
				for(String overlayType : overlayTypes) {
					if(overlayType.equals(ChromatogramChartSupport.DISPLAY_TYPE_SIC)) {
						/*
						 * SIC
						 */
						List<Integer> ions = getSelectedIons();
						if(chromatogram instanceof IChromatogramMSD) {
							for(int ion : ions) {
								String seriesId = chromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + overlayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivativeType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + ion + OverlayChartSupport.OVERLAY_STOP_MARKER;
								Color color = chromatogramChartSupport.getSeriesColor(seriesId, overlayType);
								availableSeriesIds.add(seriesId);
								if(!baseChart.isSeriesContained(seriesId)) {
									IMarkedIons markedIons = new MarkedIons();
									markedIons.add(ion);
									lineSeriesDataList.add(chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, overlayType, derivativeType, color, markedIons, false));
								}
							}
						}
						/*
						 * References
						 */
						if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_REFERENCED_CHROMATOGRAMS)) {
							int j = 1;
							for(IChromatogram referencedChromatogram : referencedChromatograms) {
								if(referencedChromatogram instanceof IChromatogramMSD) {
									for(int ion : ions) {
										String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j;
										String seriesId = referenceChromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + overlayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivativeType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + ion + OverlayChartSupport.OVERLAY_STOP_MARKER;
										Color color = chromatogramChartSupport.getSeriesColor(seriesId, overlayType);
										availableSeriesIds.add(seriesId);
										if(!baseChart.isSeriesContained(seriesId)) {
											IMarkedIons markedIons = new MarkedIons();
											markedIons.add(ion);
											lineSeriesDataList.add(chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, overlayType, derivativeType, color, markedIons, false));
										}
									}
								}
							}
						}
					} else if(overlayType.equals(ChromatogramChartSupport.DISPLAY_TYPE_SWC)) {
						/*
						 * SWC
						 */
						List<Double> wavelengths = getSelectedWavelengths();
						if(chromatogram instanceof IChromatogramWSD) {
							//
							for(double wavelength : wavelengths) {
								//
								String seriesId = chromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + overlayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivativeType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + wavelength + OverlayChartSupport.OVERLAY_STOP_MARKER;
								availableSeriesIds.add(seriesId);
								Color color = chromatogramChartSupport.getSeriesColor(seriesId, overlayType);
								IMarkedWavelengths markedWavelengths = new MarkedWavelengths();
								markedWavelengths.add(wavelength);
								//
								if(!baseChart.isSeriesContained(seriesId)) {
									lineSeriesDataList.add(chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, overlayType, derivativeType, color, markedWavelengths, false));
								}
							}
							/*
							 * References
							 */
							if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_REFERENCED_CHROMATOGRAMS)) {
								int j = 1;
								for(IChromatogram referencedChromatogram : referencedChromatograms) {
									if(referencedChromatogram instanceof IChromatogramWSD) {
										String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j;
										for(double wavelength : wavelengths) {
											//
											String seriesId = referenceChromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + overlayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivativeType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + wavelength + OverlayChartSupport.OVERLAY_STOP_MARKER;
											availableSeriesIds.add(seriesId);
											Color color = chromatogramChartSupport.getSeriesColor(seriesId, overlayType);
											IMarkedWavelengths markedWavelengths = new MarkedWavelengths();
											markedWavelengths.add(wavelength);
											//
											if(!baseChart.isSeriesContained(seriesId)) {
												lineSeriesDataList.add(chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, overlayType, derivativeType, color, markedWavelengths, false));
											}
										}
									}
								}
							}
						}
					} else {
						//
						String seriesId = chromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + overlayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivativeType + OverlayChartSupport.OVERLAY_STOP_MARKER;
						Color color = chromatogramChartSupport.getSeriesColor(chromatogramName, overlayType);
						//
						if(overlayType.equals(ChromatogramChartSupport.DISPLAY_TYPE_BPC) || overlayType.equals(ChromatogramChartSupport.DISPLAY_TYPE_XIC) || overlayType.equals(ChromatogramChartSupport.DISPLAY_TYPE_TSC)) {
							/*
							 * BPC, XIC, TSC
							 */
							IMarkedIons markedIons = new MarkedIons();
							List<Integer> ions = getSelectedIons();
							markedIons.add(ions);
							//
							if(chromatogram instanceof IChromatogramMSD) {
								availableSeriesIds.add(seriesId);
								if(!baseChart.isSeriesContained(seriesId)) {
									lineSeriesDataList.add(chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, overlayType, derivativeType, color, markedIons, false));
								}
							}
							/*
							 * References
							 */
							if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_REFERENCED_CHROMATOGRAMS)) {
								int j = 1;
								for(IChromatogram referencedChromatogram : referencedChromatograms) {
									if(referencedChromatogram instanceof IChromatogramMSD) {
										String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j;
										String referenceSeriesId = referenceChromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + overlayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivativeType + OverlayChartSupport.OVERLAY_STOP_MARKER;
										availableSeriesIds.add(referenceSeriesId);
										if(!baseChart.isSeriesContained(referenceSeriesId)) {
											color = chromatogramChartSupport.getSeriesColor(referenceChromatogramName, overlayType);
											lineSeriesDataList.add(chromatogramChartSupport.getLineSeriesData(referencedChromatogram, referenceSeriesId, overlayType, derivativeType, color, markedIons, false));
										}
									}
								}
							}
						} else {
							/*
							 * TIC
							 */
							availableSeriesIds.add(seriesId);
							if(!baseChart.isSeriesContained(seriesId)) {
								lineSeriesDataList.add(chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, overlayType, derivativeType, color, null, false));
							}
							/*
							 * References
							 */
							if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_REFERENCED_CHROMATOGRAMS)) {
								int j = 1;
								for(IChromatogram referencedChromatogram : referencedChromatograms) {
									String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j;
									String referenceSeriesId = referenceChromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + overlayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivativeType + OverlayChartSupport.OVERLAY_STOP_MARKER;
									availableSeriesIds.add(referenceSeriesId);
									if(!baseChart.isSeriesContained(referenceSeriesId)) {
										color = chromatogramChartSupport.getSeriesColor(referenceChromatogramName, overlayType);
										lineSeriesDataList.add(chromatogramChartSupport.getLineSeriesData(referencedChromatogram, referenceSeriesId, overlayType, derivativeType, color, null, false));
									}
								}
							}
						}
					}
				}
			}
			/*
			 * Add the selected series
			 */
			String compressionType = preferenceStore.getString(PreferenceConstants.P_OVERLAY_CHART_COMPRESSION_TYPE);
			int compressionToLength = chromatogramChartSupport.getCompressionLength(compressionType, lineSeriesDataList.size());
			chromatogramChart.addSeriesData(lineSeriesDataList, compressionToLength);
			/*
			 * Delete non-available series.
			 */
			for(ISeries series : baseChart.getSeriesSet().getSeries()) {
				String seriesId = series.getId();
				if(!availableSeriesIds.contains(seriesId)) {
					baseChart.deleteSeries(seriesId);
				}
			}
			/*
			 * Reset the selected series selection.
			 */
			baseChart.resetSeriesSettings();
			String[] items = new String[availableSeriesIds.size() + 1];
			items[0] = BaseChart.SELECTED_SERIES_NONE;
			int index = 1;
			for(String seriesId : availableSeriesIds) {
				items[index++] = seriesId;
			}
			setSelectedSeries(items, BaseChart.SELECTED_SERIES_NONE);
			//
			modifyDataStatusLabel();
			chromatogramChart.adjustRange(true);
		}
	}

	private List<Double> getSelectedWavelengths() {

		List<Double> selectedIons = new ArrayList<>();
		String ionsText = textSignalsFromSettings.getText().trim();
		//
		WavelengthValidator wavelengthValidator = new WavelengthValidator();
		IStatus status = wavelengthValidator.validate(ionsText);
		if(status.isOK()) {
			selectedIons = wavelengthValidator.getWavelengths();
		}
		//
		return selectedIons;
	}

	private List<Integer> getSelectedIons() {

		List<Integer> selectedIons = new ArrayList<Integer>();
		String ionsText = textSignalsFromSettings.getText().trim();
		//
		IonsValidator ionsValidator = new IonsValidator();
		IStatus status = ionsValidator.validate(ionsText);
		if(status.isOK()) {
			selectedIons = ionsValidator.getIons();
		}
		//
		return selectedIons;
	}

	public void setComboAxisItems() {

		/*
		 * X Axes
		 */
		BaseChart baseChart = chromatogramChart.getBaseChart();
		String[] axisLabelsX = baseChart.getAxisLabels(IExtendedChart.X_AXIS);
		comboScaleX.setItems(axisLabelsX);
		if(axisLabelsX.length > 0) {
			/*
			 * Get the shift value from the settings.
			 */
			double overlayShiftX = overlayChartSupport.getOverlayShiftX();
			int indexShiftX = overlayChartSupport.getIndexShiftX();
			//
			if(indexShiftX >= 0 && indexShiftX < axisLabelsX.length) {
				DecimalFormat decimalFormat = baseChart.getDecimalFormat(IExtendedChart.X_AXIS, indexShiftX);
				comboScaleX.select(indexShiftX);
				textShiftX.setText(decimalFormat.format(overlayShiftX));
			} else {
				/*
				 * Default: Milliseconds
				 */
				comboScaleX.select(0);
				textShiftX.setText(Integer.toString(0));
			}
		}
		/*
		 * Y Axes
		 */
		String[] axisLabelsY = baseChart.getAxisLabels(IExtendedChart.Y_AXIS);
		comboScaleY.setItems(axisLabelsY);
		if(axisLabelsY.length > 0) {
			/*
			 * Get the shift value from the settings.
			 */
			double absoluteShiftY = overlayChartSupport.getOverlayShiftY();
			int indexShiftY = overlayChartSupport.getIndexShiftY();
			//
			if(indexShiftY >= 0 && indexShiftY < axisLabelsY.length) {
				DecimalFormat decimalFormat = baseChart.getDecimalFormat(IExtendedChart.Y_AXIS, indexShiftY);
				comboScaleY.select(indexShiftY);
				textShiftY.setText(decimalFormat.format(absoluteShiftY));
			} else {
				/*
				 * Default: Absolute Intensity
				 */
				comboScaleY.select(0);
				textShiftY.setText(Double.toString(0.0d));
			}
		}
	}

	private double getShiftValuePrimary(String axis) {

		double shiftValue = 0.0d;
		BaseChart baseChart = chromatogramChart.getBaseChart();
		/*
		 * Get the selected axis.
		 */
		int selectedAxis;
		if(axis.equals(IExtendedChart.X_AXIS)) {
			selectedAxis = comboScaleX.getSelectionIndex();
		} else {
			selectedAxis = comboScaleY.getSelectionIndex();
		}
		/*
		 * Get the value.
		 */
		double value;
		if(axis.equals(IExtendedChart.X_AXIS)) {
			value = getTextValue(textShiftX);
		} else {
			value = getTextValue(textShiftY);
		}
		/*
		 * Convert the range on demand.
		 */
		if(selectedAxis == 0) {
			shiftValue = value;
		} else {
			IAxisScaleConverter axisScaleConverter = baseChart.getAxisScaleConverter(axis, selectedAxis);
			shiftValue = axisScaleConverter.convertToPrimaryUnit(value);
		}
		//
		return shiftValue;
	}

	private String getSelectedSeriesId() {

		return comboSelectedSeriesDefault.getText().trim();
	}

	private void setSelectedSeries(String[] items, String text) {

		comboSelectedSeriesDefault.setItems(items);
		comboSelectedSeriesDefault.setText(text);
		//
		comboSelectedSeriesShift.setItems(items);
		comboSelectedSeriesShift.setText(text);
	}

	private String getOverlayType() {

		return comboOverlayTypeDefault.getText().trim();
	}

	private void createVerticalSeparator(Composite parent) {

		Label label = new Label(parent, SWT.SEPARATOR | SWT.VERTICAL);
		GridData gridData = new GridData();
		gridData.heightHint = 35;
		label.setLayoutData(gridData);
	}

	private double getTextValue(Text text) {

		double value = 0.0d;
		try {
			value = Double.parseDouble(text.getText().trim());
		} catch(NumberFormatException e) {
			logger.warn(e);
		}
		return value;
	}

	private void persistOverlayShiftX() {

		overlayChartSupport.setOverlayShiftX(getTextValue(textShiftX));
		overlayChartSupport.setIndexShiftX(comboScaleX.getSelectionIndex());
	}

	private void persistOverlayShiftY() {

		overlayChartSupport.setOverlayShiftY(getTextValue(textShiftY));
		overlayChartSupport.setIndexShiftY(comboScaleY.getSelectionIndex());
	}
}
