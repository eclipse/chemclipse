/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.AbstractChromatogramEditorPartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.OverlaySupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePage;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.eavp.service.swtchart.core.BaseChart;
import org.eclipse.eavp.service.swtchart.core.IAxisScaleConverter;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.core.IExtendedChart;
import org.eclipse.eavp.service.swtchart.core.ISeriesModificationListener;
import org.eclipse.eavp.service.swtchart.core.SeriesStatusAdapter;
import org.eclipse.eavp.service.swtchart.customcharts.ChromatogramChart;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.LineChart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.swtchart.ISeries;

public class ChromatogramOverlayPart extends AbstractChromatogramEditorPartSupport {

	private static final Logger logger = Logger.getLogger(ChromatogramOverlayPart.class);
	@Inject
	private EPartService partService;
	//
	private Set<String> mirroredSeries;
	//
	private ChromatogramChart chromatogramChart;
	//
	private Composite toolbarType;
	private Composite toolbarShift;
	//
	private Combo comboOverlayType;
	private Combo comboDerivativeType;
	private Combo comboSelectedSeries;
	private Combo comboDisplayModus;
	private Combo comboSelectedIons;
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
	private OverlaySupport overlaySupport;

	public ChromatogramOverlayPart() {
		overlaySupport = new OverlaySupport();
		mirroredSeries = new HashSet<String>();
	}

	@PostConstruct
	public void createComposite(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarType = createToolbarType(parent);
		toolbarShift = createToolbarShift(parent);
		createChromatogramChart(parent);
		/*
		 * Hide both toolbars initially.
		 * Enable/disable widgets.
		 */
		PartSupport.setToolbarVisibility(toolbarType, false);
		PartSupport.setToolbarVisibility(toolbarShift, false);
		modifyWidgetStatus();
	}

	@Focus
	public void setFocus() {

		refreshUpdateOverlayChart();
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataStatus = new GridData(GridData.FILL_HORIZONTAL);
		gridDataStatus.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridDataStatus);
		composite.setLayout(new GridLayout(6, false));
		//
		createDataStatusLabel(composite);
		createButtonToggleToolbarType(composite);
		createButtonToggleToolbarShift(composite);
		createToggleChartLegendButton(composite);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private Composite createToolbarType(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(5, false));
		//
		createOverlayTypeCombo(composite);
		createDerivativeTypeCombo(composite);
		createSelectedSeriesCombo(composite);
		createDisplayModusCombo(composite);
		createSelectedIonsCombo(composite);
		//
		return composite;
	}

	private Composite createToolbarShift(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(8, false));
		//
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

	private void createOverlayTypeCombo(Composite parent) {

		comboOverlayType = new Combo(parent, SWT.READ_ONLY);
		comboOverlayType.setToolTipText("Select the overlay type");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 150;
		gridData.grabExcessHorizontalSpace = true;
		comboOverlayType.setLayoutData(gridData);
		comboOverlayType.setItems(overlaySupport.getOverlayTypes());
		comboOverlayType.select(0);
		comboOverlayType.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				modifyWidgetStatus();
				refreshUpdateOverlayChart();
			}
		});
	}

	private void createDerivativeTypeCombo(Composite parent) {

		comboDerivativeType = new Combo(parent, SWT.READ_ONLY);
		comboDerivativeType.setToolTipText("Select the derivative type");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 60;
		gridData.grabExcessHorizontalSpace = true;
		comboDerivativeType.setLayoutData(gridData);
		comboDerivativeType.setItems(overlaySupport.getDerivativeTypes());
		comboDerivativeType.select(0);
		comboDerivativeType.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IChartSettings chartSettings = chromatogramChart.getChartSettings();
				String derivativeType = comboDerivativeType.getText();
				if(OverlaySupport.DERIVATIVE_NONE.equals(derivativeType)) {
					if(comboDisplayModus.getText().equals(OverlaySupport.DISPLAY_MODUS_NORMAL)) {
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

	private void createSelectedSeriesCombo(Composite parent) {

		comboSelectedSeries = new Combo(parent, SWT.READ_ONLY);
		comboSelectedSeries.setToolTipText("Highlight the selected series");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 250;
		gridData.grabExcessHorizontalSpace = true;
		comboSelectedSeries.setLayoutData(gridData);
		comboSelectedSeries.setItems(new String[]{BaseChart.SELECTED_SERIES_NONE});
		comboSelectedSeries.select(0);
		comboSelectedSeries.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String selectedSeriesId = comboSelectedSeries.getText().trim();
				BaseChart baseChart = chromatogramChart.getBaseChart();
				baseChart.resetSeriesSettings();
				baseChart.selectSeries(selectedSeriesId);
				baseChart.redraw();
				modifyWidgetStatus();
			}
		});
	}

	private void createDisplayModusCombo(Composite parent) {

		comboDisplayModus = new Combo(parent, SWT.READ_ONLY);
		comboDisplayModus.setToolTipText("Select the display modus.");
		comboDisplayModus.setItems(overlaySupport.getDisplayModi());
		comboDisplayModus.setText(OverlaySupport.DISPLAY_MODUS_NORMAL);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 150;
		gridData.grabExcessHorizontalSpace = true;
		comboDisplayModus.setLayoutData(gridData);
		comboDisplayModus.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				BaseChart baseChart = chromatogramChart.getBaseChart();
				String displayModus = comboDisplayModus.getText().trim();
				String selectedSeriesId = comboSelectedSeries.getText().trim();
				String derivativeType = comboDerivativeType.getText();
				IChartSettings chartSettings = chromatogramChart.getChartSettings();
				//
				if(displayModus.equals(OverlaySupport.DISPLAY_MODUS_MIRRORED)) {
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
					if(mirroredSeries.size() == 0 && OverlaySupport.DERIVATIVE_NONE.equals(derivativeType)) {
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

		comboSelectedIons = new Combo(parent, SWT.NONE);
		comboSelectedIons.setToolTipText("Select the overlay ions.");
		comboSelectedIons.setItems(overlaySupport.getSelectedIons());
		comboSelectedIons.setText(OverlaySupport.SELECTED_IONS_DEFAULT);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 150;
		gridData.grabExcessHorizontalSpace = true;
		comboSelectedIons.setLayoutData(gridData);
		comboSelectedIons.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chromatogramChart.deleteSeries();
				refreshUpdateOverlayChart();
			}
		});
		comboSelectedIons.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					chromatogramChart.deleteSeries();
					refreshUpdateOverlayChart();
				}
			}
		});
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
				double shiftX = getShift(IExtendedChart.X_AXIS) * -1.0d;
				String selectedSeriesId = comboSelectedSeries.getText().trim();
				baseChart.shiftSeries(selectedSeriesId, shiftX, 0.0d);
				baseChart.redraw();
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
				double shiftX = getShift(IExtendedChart.X_AXIS);
				String selectedSeriesId = comboSelectedSeries.getText().trim();
				baseChart.shiftSeries(selectedSeriesId, shiftX, 0.0d);
				baseChart.redraw();
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
				double shiftY = getShift(IExtendedChart.Y_AXIS);
				String selectedSeriesId = comboSelectedSeries.getText().trim();
				baseChart.shiftSeries(selectedSeriesId, 0.0d, shiftY);
				baseChart.redraw();
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
				double shiftY = getShift(IExtendedChart.Y_AXIS) * -1.0d;
				String selectedSeriesId = comboSelectedSeries.getText().trim();
				baseChart.shiftSeries(selectedSeriesId, 0.0d, shiftY);
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
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PartSupport.toggleToolbarVisibility(toolbarType);
			}
		});
	}

	private void createButtonToggleToolbarShift(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle shift toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PartSupport.toggleToolbarVisibility(toolbarShift);
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

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePage = new PreferencePage();
				preferencePage.setTitle("Overlay Settings");
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePage));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(Display.getDefault().getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == PreferenceDialog.OK) {
					try {
						overlaySupport.loadUserSettings();
						applyOverlaySettings();
					} catch(Exception e1) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Settings", "Something has gone wrong to apply the chart settings.");
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
		String overlayType = comboOverlayType.getText().trim();
		boolean enableOverlayType = (overlayType.contains(OverlaySupport.OVERLAY_TYPE_XIC) || overlayType.contains(OverlaySupport.OVERLAY_TYPE_SIC) || overlayType.contains(OverlaySupport.OVERLAY_TYPE_TSC));
		comboSelectedIons.setEnabled(enableOverlayType);
		/*
		 * Selected Series
		 */
		String selectedSeries = comboSelectedSeries.getText().trim();
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

	private void applyOverlaySettings() {

		overlaySupport.resetColorMaps();
		chromatogramChart.deleteSeries();
		refreshUpdateOverlayChart();
		modifyWidgetStatus();
		modifyDataStatusLabel();
	}

	private void createChromatogramChart(Composite parent) {

		chromatogramChart = new ChromatogramChart(parent, SWT.NONE);
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

				comboSelectedSeries.setText(seriesId);
				modifyWidgetStatus();
			}
		});
		//
		setComboAxisItems();
	}

	private void refreshUpdateOverlayChart() {

		List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections(partService);
		Set<String> availableSeriesIds = new HashSet<String>();
		BaseChart baseChart = chromatogramChart.getBaseChart();
		List<Integer> ions = getSelectedIons();
		//
		comboDisplayModus.setText(OverlaySupport.DISPLAY_MODUS_NORMAL);
		//
		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		for(int i = 0; i < chromatogramSelections.size(); i++) {
			IChromatogramSelection chromatogramSelection = chromatogramSelections.get(i);
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			String chromatogramName = chromatogram.getName() + OverlaySupport.EDITOR_TAB + (i + 1);
			/*
			 * Select which series shall be displayed.
			 */
			String[] overlayTypes = comboOverlayType.getText().trim().split(OverlaySupport.ESCAPE_CONCATENATOR + OverlaySupport.OVERLAY_TYPE_CONCATENATOR);
			String derivativeType = comboDerivativeType.getText().trim();
			//
			for(String overlayType : overlayTypes) {
				if(overlayType.equals(OverlaySupport.OVERLAY_TYPE_SIC)) {
					/*
					 * SIC
					 */
					if(chromatogram instanceof IChromatogramMSD) {
						for(int ion : ions) {
							//
							String seriesId = chromatogramName + OverlaySupport.OVERLAY_START_MARKER + overlayType + OverlaySupport.DELIMITER_ION_DERIVATIVE + derivativeType + OverlaySupport.DELIMITER_ION_DERIVATIVE + ion + OverlaySupport.OVERLAY_STOP_MARKER;
							Color color = overlaySupport.getSeriesColor(seriesId, overlayType);
							//
							availableSeriesIds.add(seriesId);
							if(!baseChart.isSeriesContained(seriesId)) {
								List<Integer> sic = new ArrayList<Integer>();
								sic.add(ion);
								lineSeriesDataList.add(overlaySupport.getLineSeriesData(chromatogram, seriesId, overlayType, derivativeType, color, sic));
							}
						}
					}
				} else {
					//
					String seriesId = chromatogramName + OverlaySupport.OVERLAY_START_MARKER + overlayType + OverlaySupport.DELIMITER_ION_DERIVATIVE + derivativeType + OverlaySupport.OVERLAY_STOP_MARKER;
					Color color = overlaySupport.getSeriesColor(chromatogramName, overlayType);
					//
					if(overlayType.equals(OverlaySupport.OVERLAY_TYPE_BPC) || overlayType.equals(OverlaySupport.OVERLAY_TYPE_XIC) || overlayType.equals(OverlaySupport.OVERLAY_TYPE_TSC)) {
						/*
						 * BPC, XIC, TSC
						 */
						if(chromatogram instanceof IChromatogramMSD) {
							availableSeriesIds.add(seriesId);
							if(!baseChart.isSeriesContained(seriesId)) {
								lineSeriesDataList.add(overlaySupport.getLineSeriesData(chromatogram, seriesId, overlayType, derivativeType, color, ions));
							}
						}
					} else {
						/*
						 * TIC
						 */
						availableSeriesIds.add(seriesId);
						if(!baseChart.isSeriesContained(seriesId)) {
							lineSeriesDataList.add(overlaySupport.getLineSeriesData(chromatogram, seriesId, overlayType, derivativeType, color, ions));
						}
					}
				}
			}
		}
		/*
		 * Add the selected series
		 */
		chromatogramChart.addSeriesData(lineSeriesDataList, LineChart.MEDIUM_COMPRESSION);
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
		//
		comboSelectedSeries.setItems(items);
		comboSelectedSeries.setText(BaseChart.SELECTED_SERIES_NONE);
		//
		modifyDataStatusLabel();
		chromatogramChart.adjustRange(true);
	}

	private List<Integer> getSelectedIons() {

		DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();
		List<Integer> selectedIons = new ArrayList<Integer>();
		//
		Map<String, String> selectedIonsMap = overlaySupport.getSelectedIonsMap();
		String comboText = comboSelectedIons.getText().trim();
		String ionsText = "";
		if(selectedIonsMap.containsKey(comboSelectedIons.getText().trim())) {
			ionsText = selectedIonsMap.get(comboText);
		} else {
			ionsText = comboText;
		}
		//
		String[] ions = ionsText.split(OverlaySupport.SELECTED_IONS_CONCATENATOR);
		for(String ion : ions) {
			try {
				selectedIons.add(AbstractIon.getIon(decimalFormat.parse(ion).doubleValue()));
			} catch(ParseException e) {
				logger.warn(e);
			}
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
			int selectedIndex = 1; // "Minutes"
			if(selectedIndex >= 0 && selectedIndex < axisLabelsX.length) {
				comboScaleX.select(selectedIndex);
				textShiftX.setText("0.5");
			} else {
				comboScaleX.select(0); // Milliseconds
				textShiftX.setText("10000");
			}
		}
		/*
		 * Y Axes
		 */
		String[] axisLabelsY = baseChart.getAxisLabels(IExtendedChart.Y_AXIS);
		comboScaleY.setItems(axisLabelsY);
		if(axisLabelsY.length > 0) {
			int selectedIndex = 1; // "Relative Intensity [%]"
			if(selectedIndex >= 0 && selectedIndex < axisLabelsY.length) {
				comboScaleY.select(selectedIndex);
				textShiftY.setText("1.2");
			} else {
				comboScaleY.select(0); // Intensity
				textShiftY.setText("100000");
			}
		}
	}

	private double getShift(String axis) {

		double shiftValue = 0.0d;
		try {
			/*
			 * Try to calculate the primary unit.
			 */
			BaseChart baseChart = chromatogramChart.getBaseChart();
			DecimalFormat decimalFormat;
			int selectedAxis;
			//
			if(axis.equals(IExtendedChart.X_AXIS)) {
				selectedAxis = comboScaleX.getSelectionIndex();
				decimalFormat = baseChart.getDecimalFormat(IExtendedChart.X_AXIS, selectedAxis);
			} else {
				selectedAxis = comboScaleY.getSelectionIndex();
				decimalFormat = baseChart.getDecimalFormat(IExtendedChart.Y_AXIS, selectedAxis);
			}
			//
			double secondaryValue;
			if(axis.equals(IExtendedChart.X_AXIS)) {
				secondaryValue = decimalFormat.parse(textShiftX.getText().trim()).doubleValue();
			} else {
				secondaryValue = decimalFormat.parse(textShiftY.getText().trim()).doubleValue();
			}
			/*
			 * Convert the range on demand.
			 */
			if(selectedAxis == 0) {
				shiftValue = secondaryValue;
			} else {
				IAxisScaleConverter axisScaleConverter = baseChart.getAxisScaleConverter(axis, selectedAxis);
				shiftValue = axisScaleConverter.convertToPrimaryUnit(secondaryValue);
			}
		} catch(ParseException e) {
			System.out.println(e);
		}
		//
		return shiftValue;
	}
}
