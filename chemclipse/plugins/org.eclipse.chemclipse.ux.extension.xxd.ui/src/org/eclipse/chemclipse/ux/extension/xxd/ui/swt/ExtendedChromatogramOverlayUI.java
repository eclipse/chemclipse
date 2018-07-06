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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.OverlayChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.IonsValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.EditorUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageOverlay;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.xwc.ExtractedWavelengthSignalExtractor;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedWavelengthSignals;
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

	private Composite toolbarType;
	private Composite toolbarProfile;
	private Composite toolbarShift;
	private ChromatogramChart chromatogramChart;
	//
	private Combo comboOverlayType1;
	private Combo comboOverlayType2;
	private Combo comboDerivativeType;
	private Combo comboSelectedSeries1;
	private Combo comboSelectedSeries2;
	private Combo comboDisplayModus;
	private Combo comboSelectedIons;
	private Text textIonsFromSettings;
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
	private EditorUpdateSupport editorUpdateSupport = new EditorUpdateSupport();
	private ChromatogramChartSupport chromatogramChartSupport = new ChromatogramChartSupport();
	private OverlayChartSupport overlayChartSupport = new OverlayChartSupport();
	private ControlDecoration controlDecoration;
	private Set<String> mirroredSeries;
	//
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	//
	private List<IChromatogramSelection> chromatogramSelections = new ArrayList<>();

	@Inject
	public ExtendedChromatogramOverlayUI(Composite parent) {
		mirroredSeries = new HashSet<String>();
		initialize(parent);
	}

	public void update() {

		chromatogramSelections = editorUpdateSupport.getChromatogramSelections();
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
		composite.setLayout(new GridLayout(9, false));
		//
		createDataStatusLabel(composite);
		createButtonToggleToolbarType(composite);
		createButtonToggleToolbarProfile(composite);
		createButtonToggleToolbarShift(composite);
		createToggleChartLegendButton(composite);
		createResetButton(composite);
		createButtonPseudo3D(composite);
		createNewOverlayPartButton(composite);
		createSettingsButton(composite);
	}

	private Composite createToolbarType(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(4, false));
		//
		comboOverlayType1 = createOverlayTypeCombo(composite);
		createDerivativeTypeCombo(composite);
		comboSelectedSeries1 = createSelectedSeriesCombo(composite);
		createDisplayModusCombo(composite);
		//
		return composite;
	}

	private Composite createToolbarProfile(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		comboOverlayType2 = createOverlayTypeCombo(composite);
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
		composite.setLayout(new GridLayout(9, false));
		//
		comboSelectedSeries2 = createSelectedSeriesCombo(composite);
		createTextShiftX(composite);
		createComboScaleX(composite);
		createButtonLeft(composite);
		createButtonRight(composite);
		createTextShiftY(composite);
		createComboScaleY(composite);
		createButtonUp(composite);
		createButtonDown(composite);
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
				comboOverlayType1.select(index);
				comboOverlayType2.select(index);
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
				comboSelectedSeries1.select(index);
				comboSelectedSeries2.select(index);
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
				String displayModus = comboDisplayModus.getText().trim();
				String selectedSeriesId = getSelectedSeriesId();
				String derivativeType = comboDerivativeType.getText();
				IChartSettings chartSettings = chromatogramChart.getChartSettings();
				//
				if(displayModus.equals(OverlayChartSupport.DISPLAY_MODUS_MIRRORED)) {
					/*
					 * Mirror
					 */
					chartSettings.getRangeRestriction().setZeroY(false);
					//
					if(!mirroredSeries.contains(selectedSeriesId)) {
						baseChart.multiplySeries(selectedSeriesId, IExtendedChart.Y_AXIS, -1.0d);
						mirroredSeries.add(selectedSeriesId);
					}
				} else {
					/*
					 * Normal
					 */
					if(mirroredSeries.contains(selectedSeriesId)) {
						baseChart.multiplySeries(selectedSeriesId, IExtendedChart.Y_AXIS, -1.0d);
						mirroredSeries.remove(selectedSeriesId);
					}
					//
					if(mirroredSeries.size() == 0 && ChromatogramChartSupport.DERIVATIVE_NONE.equals(derivativeType)) {
						chartSettings.getRangeRestriction().setZeroY(true);
					} else {
						chartSettings.getRangeRestriction().setZeroY(false);
					}
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
				textIonsFromSettings.setEnabled(extractedIonsModusEnabled);
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
				textIonsFromSettings.setText(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_USERS_CHOICE));
				preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_SELECTION, OverlayChartSupport.SELECTED_IONS_USERS_CHOICE);
				break;
			case OverlayChartSupport.SELECTED_IONS_HYDROCARBONS:
				textIonsFromSettings.setText(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_HYDROCARBONS));
				preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_SELECTION, OverlayChartSupport.SELECTED_IONS_HYDROCARBONS);
				break;
			case OverlayChartSupport.SELECTED_IONS_FATTY_ACIDS:
				textIonsFromSettings.setText(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_FATTY_ACIDS));
				preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_SELECTION, OverlayChartSupport.SELECTED_IONS_FATTY_ACIDS);
				break;
			case OverlayChartSupport.SELECTED_IONS_FAME:
				textIonsFromSettings.setText(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_FAME));
				preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_SELECTION, OverlayChartSupport.SELECTED_IONS_FAME);
				break;
			case OverlayChartSupport.SELECTED_IONS_SOLVENT_TAILING:
				textIonsFromSettings.setText(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_SOLVENT_TAILING));
				preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_SELECTION, OverlayChartSupport.SELECTED_IONS_SOLVENT_TAILING);
				break;
			case OverlayChartSupport.SELECTED_IONS_COLUMN_BLEED:
				textIonsFromSettings.setText(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_COLUMN_BLEED));
				preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_SELECTION, OverlayChartSupport.SELECTED_IONS_COLUMN_BLEED);
				break;
			default:
				textIonsFromSettings.setText("");
				break;
		}
	}

	private void createIonsFromText(Composite parent) {

		textIonsFromSettings = new Text(parent, SWT.BORDER);
		textIonsFromSettings.setText(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_USERS_CHOICE));
		textIonsFromSettings.setToolTipText("Users choice overlay ions.");
		textIonsFromSettings.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		textIonsFromSettings.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					chromatogramChart.deleteSeries();
					refreshUpdateOverlayChart();
				} else {
					IonsValidator ionsValidator = new IonsValidator();
					if(validate(ionsValidator, controlDecoration, textIonsFromSettings)) {
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
								textIonsFromSettings.setText("");
								break;
						}
					}
				}
			}
		});
		//
		controlDecoration = new ControlDecoration(textIonsFromSettings, SWT.LEFT | SWT.TOP);
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
				persistShiftXSelection();
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
				persistShiftXSelection();
			}
		});
	}

	private void persistShiftXSelection() {

		double minutesShiftX = getShiftValuePrimary(IExtendedChart.X_AXIS) / AbstractChromatogram.MINUTE_CORRELATION_FACTOR;
		overlayChartSupport.setSettingsMinutesShiftX(minutesShiftX);
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
				persistShiftYSelection();
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
				persistShiftYSelection();
			}
		});
	}

	private void persistShiftYSelection() {

		double absoluteShiftY = getShiftValuePrimary(IExtendedChart.Y_AXIS);
		overlayChartSupport.setSettingsAbsoluteShiftY(absoluteShiftY);
	}

	private void createButtonPseudo3D(Composite parent) {

		Button buttonShiftDown = new Button(parent, SWT.PUSH);
		buttonShiftDown.setToolTipText("Pseudo 3d");
		buttonShiftDown.setText("");
		buttonShiftDown.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_3D, IApplicationImage.SIZE_16x16));
		buttonShiftDown.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * Reset the chart.
				 */
				applyOverlaySettings();
				/*
				 * Move equidistant.
				 */
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
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_PROFILE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarProfile);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_PROFILE, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_PROFILE, IApplicationImage.SIZE_16x16));
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
				if(preferenceDialog.open() == PreferenceDialog.OK) {
					try {
						applyOverlaySettings();
					} catch(Exception e1) {
						System.out.println(e1);
						MessageDialog.openError(DisplayUtils.getShell(), "Settings", "Something has gone wrong to apply the chart settings.");
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
		boolean enable = isExtractedIonsModusEnabled();
		comboSelectedIons.setEnabled(enable);
		textIonsFromSettings.setEnabled(enable);
		/*
		 * Selected Series
		 */
		String selectedSeries = getSelectedSeriesId();
		boolean isSeriesSelected = !selectedSeries.equals(BaseChart.SELECTED_SERIES_NONE);
		comboDisplayModus.setEnabled(isSeriesSelected);
		textShiftX.setEnabled(isSeriesSelected);
		comboScaleX.setEnabled(isSeriesSelected);
		buttonShiftLeft.setEnabled(isSeriesSelected);
		buttonShiftRight.setEnabled(isSeriesSelected);
		textShiftY.setEnabled(isSeriesSelected);
		comboScaleY.setEnabled(isSeriesSelected);
		buttonShiftUp.setEnabled(isSeriesSelected);
		buttonShiftDown.setEnabled(isSeriesSelected);
	}

	private boolean isExtractedIonsModusEnabled() {

		String overlayType = getOverlayType();
		return (overlayType.contains(ChromatogramChartSupport.DISPLAY_TYPE_XIC) || //
				overlayType.contains(ChromatogramChartSupport.DISPLAY_TYPE_SIC) || //
				overlayType.contains(ChromatogramChartSupport.DISPLAY_TYPE_TSC));
	}

	private void applyOverlaySettings() {

		chromatogramChartSupport.loadUserSettings();
		chromatogramChart.deleteSeries();
		textIonsFromSettings.setText(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_USERS_CHOICE));
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

				comboSelectedSeries1.setText(seriesId);
				comboSelectedSeries2.setText(seriesId);
				modifyWidgetStatus();
			}
		});
		//
		setComboAxisItems();
	}

	@SuppressWarnings("rawtypes")
	private void refreshUpdateOverlayChart() {

		if(chromatogramSelections.size() > 0) {
			Set<String> availableSeriesIds = new HashSet<String>();
			BaseChart baseChart = chromatogramChart.getBaseChart();
			List<Integer> ions = getSelectedIons();
			comboDisplayModus.setText(OverlayChartSupport.DISPLAY_MODUS_NORMAL);
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
			//
			for(int i = 0; i < chromatogramSelections.size(); i++) {
				IChromatogramSelection chromatogramSelection = chromatogramSelections.get(i);
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				String chromatogramName = chromatogram.getName() + OverlayChartSupport.EDITOR_TAB + (i + 1);
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
						if(chromatogram instanceof IChromatogramMSD) {
							for(int ion : ions) {
								//
								String seriesId = chromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + overlayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivativeType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + ion + OverlayChartSupport.OVERLAY_STOP_MARKER;
								Color color = chromatogramChartSupport.getSeriesColor(seriesId, overlayType);
								availableSeriesIds.add(seriesId);
								if(!baseChart.isSeriesContained(seriesId)) {
									List<Integer> sic = new ArrayList<Integer>();
									sic.add(ion);
									lineSeriesDataList.add(chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, overlayType, derivativeType, color, sic, false));
								}
							}
						}
					} else if(overlayType.equals(ChromatogramChartSupport.DISPLAY_TYPE_SWC)) {
						/*
						 * SWC
						 */
						if(chromatogram instanceof IChromatogramWSD) {
							//
							IChromatogramWSD chromatogramWSD = (IChromatogramWSD)chromatogram;
							Set<Integer> wavelengths = getContainedWavelengths(chromatogramWSD);
							ExtractedWavelengthSignalExtractor signalExtractor = new ExtractedWavelengthSignalExtractor(chromatogramWSD);
							IExtractedWavelengthSignals extractedWavelengthSignals = signalExtractor.getExtractedWavelengthSignals();
							for(int wavelength : wavelengths) {
								//
								String seriesId = chromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + overlayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivativeType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + wavelength + OverlayChartSupport.OVERLAY_STOP_MARKER;
								availableSeriesIds.add(seriesId);
								//
								if(!baseChart.isSeriesContained(seriesId)) {
									lineSeriesDataList.add(chromatogramChartSupport.getLineSeriesData(extractedWavelengthSignals, wavelength, seriesId, overlayType));
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
							if(chromatogram instanceof IChromatogramMSD) {
								availableSeriesIds.add(seriesId);
								if(!baseChart.isSeriesContained(seriesId)) {
									lineSeriesDataList.add(chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, overlayType, derivativeType, color, ions, false));
								}
							}
						} else {
							/*
							 * TIC
							 */
							availableSeriesIds.add(seriesId);
							if(!baseChart.isSeriesContained(seriesId)) {
								lineSeriesDataList.add(chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, overlayType, derivativeType, color, ions, false));
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

	private Set<Integer> getContainedWavelengths(IChromatogramWSD chromatogramWSD) {

		Set<Integer> wavelengths = new HashSet<>();
		for(IScan scan : chromatogramWSD.getScans()) {
			if(scan instanceof IScanWSD) {
				IScanWSD scanWSD = (IScanWSD)scan;
				for(IScanSignalWSD scanSignal : scanWSD.getScanSignals()) {
					wavelengths.add((int)scanSignal.getWavelength());
				}
			}
		}
		//
		return wavelengths;
	}

	private List<Integer> getSelectedIons() {

		List<Integer> selectedIons = new ArrayList<Integer>();
		String ionsText = textIonsFromSettings.getText().trim();
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
			double minutesShiftX = overlayChartSupport.getSettingsMinutesShiftX();
			int selectedIndex = 1; // "Minutes"
			//
			if(selectedIndex >= 0 && selectedIndex < axisLabelsX.length) {
				DecimalFormat decimalFormat = baseChart.getDecimalFormat(IExtendedChart.X_AXIS, selectedIndex);
				comboScaleX.select(selectedIndex);
				textShiftX.setText(decimalFormat.format(minutesShiftX));
			} else {
				int millisecondsShiftX = (int)(minutesShiftX * AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
				comboScaleX.select(0); // Milliseconds
				textShiftX.setText(Integer.toString(millisecondsShiftX));
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
			double absoluteShiftY = overlayChartSupport.getSettingsAbsoluteShiftY();
			int selectedIndex = 0; // Absolute Intensity
			DecimalFormat decimalFormat = baseChart.getDecimalFormat(IExtendedChart.X_AXIS, selectedIndex);
			//
			if(selectedIndex >= 0 && selectedIndex < axisLabelsY.length) {
				comboScaleY.select(0); // Intensity
				textShiftY.setText(decimalFormat.format(absoluteShiftY));
			}
		}
	}

	private double getShiftValuePrimary(String axis) {

		double shiftValue = 0.0d;
		try {
			/*
			 * Try to calculate the primary unit.
			 */
			BaseChart baseChart = chromatogramChart.getBaseChart();
			DecimalFormat decimalFormat;
			/*
			 * Get the selected axis.
			 */
			int selectedAxis;
			if(axis.equals(IExtendedChart.X_AXIS)) {
				selectedAxis = comboScaleX.getSelectionIndex();
				decimalFormat = baseChart.getDecimalFormat(IExtendedChart.X_AXIS, selectedAxis);
			} else {
				selectedAxis = comboScaleY.getSelectionIndex();
				decimalFormat = baseChart.getDecimalFormat(IExtendedChart.Y_AXIS, selectedAxis);
			}
			/*
			 * Get the value.
			 */
			double value;
			if(axis.equals(IExtendedChart.X_AXIS)) {
				value = decimalFormat.parse(textShiftX.getText().trim()).doubleValue();
			} else {
				value = decimalFormat.parse(textShiftY.getText().trim()).doubleValue();
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
		} catch(ParseException e) {
			System.out.println(e);
		}
		//
		return shiftValue;
	}

	private String getSelectedSeriesId() {

		return comboSelectedSeries1.getText().trim();
	}

	private void setSelectedSeries(String[] items, String text) {

		comboSelectedSeries1.setItems(items);
		comboSelectedSeries1.setText(text);
		//
		comboSelectedSeries2.setItems(items);
		comboSelectedSeries2.setText(text);
	}

	private String getOverlayType() {

		return comboOverlayType1.getText().trim();
	}
}
