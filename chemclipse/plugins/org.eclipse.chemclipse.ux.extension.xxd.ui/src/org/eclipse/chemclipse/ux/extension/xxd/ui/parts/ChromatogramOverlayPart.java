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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePage;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.eavp.service.swtchart.core.BaseChart;
import org.eclipse.eavp.service.swtchart.core.IAxisScaleConverter;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.core.IExtendedChart;
import org.eclipse.eavp.service.swtchart.core.ISeriesData;
import org.eclipse.eavp.service.swtchart.core.SeriesData;
import org.eclipse.eavp.service.swtchart.customcharts.ChromatogramChart;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesSettings;
import org.eclipse.eavp.service.swtchart.linecharts.LineChart;
import org.eclipse.eavp.service.swtchart.linecharts.LineSeriesData;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.swtchart.ISeries;
import org.swtchart.LineStyle;

public class ChromatogramOverlayPart extends AbstractMeasurementEditorPartSupport {

	private static final Logger logger = Logger.getLogger(ChromatogramOverlayPart.class);
	@Inject
	private EPartService partService;
	//
	private static final String OVERLAY_TYPE_TIC = "TIC"; // Total Intensity Chromatogram
	private static final String OVERLAY_TYPE_BPC = "BPC"; // Base Peak Chromatogram
	private static final String OVERLAY_TYPE_XIC = "XIC"; // Extracted Ion Chromatogram
	private static final String OVERLAY_TYPE_SIC = "SIC"; // Selected Ion Chromatogram
	private static final String OVERLAY_TYPE_TSC = "TSC"; // Total Substracted Chromatogram
	// private static final String OVERLAY_TYPE_SRM = "SRM"; // Single Reaction Monitoring
	// private static final String OVERLAY_TYPE_MRM = "MRM"; // Single Reaction Monitoring
	private static final String OVERLAY_TYPE_CONCATENATOR = "+";
	private static final String ESCAPE_CONCATENATOR = "\\";
	private static final String SELECTED_IONS_CONCATENATOR = " ";
	private static final String EDITOR_TAB = "_EditorTab#";
	private static final String OVERLAY_START_MARKER = "_(";
	private static final String OVERLAY_STOP_MARKER = ")";
	//
	private static final String SELECTED_SERIES_NONE = "None";
	private static final String SELECTED_IONS_DEFAULT = "18 28 32 84 207";
	private static final String SELECTED_IONS_HYDROCARBONS = "Hydrocarbons";
	private static final String SELECTED_IONS_FATTY_ACIDS = "Fatty Acids";
	private static final String SELECTED_IONS_FAME = "FAME";
	private static final String SELECTED_IONS_SOLVENT_TAILING = "Solvent Tailing";
	private static final String SELECTED_IONS_COLUMN_BLEED = "Column Bleed";
	//
	private static final String DISPLAY_MODUS_NORMAL = "Normal";
	private static final String DISPLAY_MODUS_MIRRORED = "Mirrored";
	//
	private ChromatogramChart chromatogramChart;
	private IColorScheme colorScheme;
	private Map<String, Color> usedColors;
	private IColorScheme colorSchemeSic;
	private Map<String, String> selectedIonsMap;
	private Set<String> mirroredSeries;
	//
	private Composite compositeToolbar;
	private Composite compositeType;
	private Composite compositeIons;
	private Composite compositeShift;
	private Composite compositeStatus;
	//
	private String[] overlayTypes;
	private String[] selectedIons;
	private String[] displayModi;
	private Combo comboOverlayType;
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

	public ChromatogramOverlayPart() {
		colorScheme = Colors.getColorScheme(Colors.COLOR_SCHEME_PUBLICATION);
		usedColors = new HashMap<String, Color>();
		//
		colorSchemeSic = Colors.getColorScheme(Colors.COLOR_SCHEME_HIGH_CONTRAST);
		//
		overlayTypes = new String[]{//
				OVERLAY_TYPE_TIC, //
				OVERLAY_TYPE_BPC, //
				OVERLAY_TYPE_XIC, //
				OVERLAY_TYPE_SIC, //
				OVERLAY_TYPE_TSC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_BPC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_XIC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_SIC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_TSC};
		//
		selectedIons = new String[]{//
				SELECTED_IONS_HYDROCARBONS, //
				SELECTED_IONS_FATTY_ACIDS, //
				SELECTED_IONS_FAME, //
				SELECTED_IONS_SOLVENT_TAILING, //
				SELECTED_IONS_COLUMN_BLEED //
		};
		//
		displayModi = new String[]{//
				DISPLAY_MODUS_NORMAL, //
				DISPLAY_MODUS_MIRRORED //
		};
		//
		selectedIonsMap = new HashMap<String, String>();
		selectedIonsMap.put(SELECTED_IONS_HYDROCARBONS, "57 71 85");
		selectedIonsMap.put(SELECTED_IONS_FATTY_ACIDS, "74 84");
		selectedIonsMap.put(SELECTED_IONS_FAME, "79 81");
		selectedIonsMap.put(SELECTED_IONS_SOLVENT_TAILING, "84");
		selectedIonsMap.put(SELECTED_IONS_COLUMN_BLEED, "207");
		//
		mirroredSeries = new HashSet<String>();
	}

	@PostConstruct
	public void createComposite(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		createButtonsToolbar(parent);
		createChromatogramChart(parent);
		//
		modifySelectedIonsModus();
		modifyDisplayAndShiftModus();
		modifyDataStatusLabel();
	}

	private void createButtonsToolbar(Composite parent) {

		compositeToolbar = new Composite(parent, SWT.NONE);
		compositeToolbar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		compositeToolbar.setLayout(new GridLayout(4, false));
		//
		compositeType = new Composite(compositeToolbar, SWT.NONE);
		GridData gridDataType = new GridData(GridData.FILL_HORIZONTAL);
		gridDataType.grabExcessHorizontalSpace = true;
		compositeType.setLayoutData(gridDataType);
		compositeType.setLayout(new GridLayout(2, false));
		//
		compositeIons = new Composite(compositeToolbar, SWT.NONE);
		compositeIons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		compositeIons.setLayout(new GridLayout(1, false));
		//
		compositeShift = new Composite(compositeToolbar, SWT.NONE);
		compositeShift.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		compositeShift.setLayout(new GridLayout(9, false));
		//
		compositeStatus = new Composite(compositeToolbar, SWT.NONE);
		GridData gridDataStatus = new GridData(GridData.FILL_HORIZONTAL);
		gridDataStatus.horizontalAlignment = SWT.END;
		compositeStatus.setLayoutData(gridDataStatus);
		compositeStatus.setLayout(new GridLayout(3, false));
		/*
		 * Parts
		 */
		createOverlayTypeCombo(compositeType);
		createSelectedSeriesCombo(compositeType);
		//
		createSelectedIonsCombo(compositeIons);
		//
		createDisplayModusCombo(compositeShift);
		createTextShiftX(compositeShift);
		createComboScaleX(compositeShift);
		createButtonLeft(compositeShift);
		createButtonRight(compositeShift);
		createTextShiftY(compositeShift);
		createComboScaleY(compositeShift);
		createButtonUp(compositeShift);
		createButtonDown(compositeShift);
		//
		createDataStatusLabel(compositeStatus);
		createResetButton(compositeStatus);
		createSettingsButton(compositeStatus);
	}

	private void createOverlayTypeCombo(Composite parent) {

		comboOverlayType = new Combo(parent, SWT.READ_ONLY);
		comboOverlayType.setToolTipText("Select the overlay type");
		comboOverlayType.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 150;
		gridData.grabExcessHorizontalSpace = true;
		comboOverlayType.setLayoutData(gridData);
		comboOverlayType.setItems(overlayTypes);
		comboOverlayType.select(0);
		comboOverlayType.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				modifySelectedIonsModus();
				refreshUpdateOverlayChart();
			}
		});
	}

	private void createSelectedSeriesCombo(Composite parent) {

		comboSelectedSeries = new Combo(parent, SWT.READ_ONLY);
		comboSelectedSeries.setToolTipText("Highlight the selected series");
		comboSelectedSeries.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 250;
		gridData.grabExcessHorizontalSpace = true;
		comboSelectedSeries.setLayoutData(gridData);
		comboSelectedSeries.setItems(new String[]{SELECTED_SERIES_NONE});
		comboSelectedSeries.select(0);
		comboSelectedSeries.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				BaseChart baseChart = chromatogramChart.getBaseChart();
				baseChart.resetSeriesSettings();
				baseChart.selectSeries(comboSelectedSeries.getText().trim());
				baseChart.redraw();
				//
				modifyDisplayAndShiftModus();
			}
		});
	}

	private void createDisplayModusCombo(Composite parent) {

		comboDisplayModus = new Combo(parent, SWT.READ_ONLY);
		comboDisplayModus.setToolTipText("Select the display modus.");
		comboDisplayModus.setItems(displayModi);
		comboDisplayModus.setText(DISPLAY_MODUS_NORMAL);
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
				//
				if(displayModus.equals(DISPLAY_MODUS_MIRRORED)) {
					/*
					 * Mirror
					 */
					IChartSettings chartSettings = chromatogramChart.getChartSettings();
					chartSettings.getRangeRestriction().setZeroY(false);
					chromatogramChart.applySettings(chartSettings);
					//
					if(!mirroredSeries.contains(selectedSeriesId)) {
						baseChart.multiplySeriesY(selectedSeriesId, -1.0d);
						mirroredSeries.add(selectedSeriesId);
					}
				} else {
					/*
					 * Normal
					 */
					if(mirroredSeries.contains(selectedSeriesId)) {
						baseChart.multiplySeriesY(selectedSeriesId, -1.0d);
						mirroredSeries.remove(selectedSeriesId);
					}
					//
					IChartSettings chartSettings = chromatogramChart.getChartSettings();
					chartSettings.getRangeRestriction().setZeroY((mirroredSeries.size() == 0) ? true : false);
					chromatogramChart.applySettings(chartSettings);
				}
				//
				chromatogramChart.adjustRange(true);
				chromatogramChart.redraw();
			}
		});
	}

	private void createSelectedIonsCombo(Composite parent) {

		comboSelectedIons = new Combo(parent, SWT.NONE);
		comboSelectedIons.setToolTipText("Select the overlay ions.");
		comboSelectedIons.setItems(selectedIons);
		comboSelectedIons.setText(SELECTED_IONS_DEFAULT);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 250;
		gridData.grabExcessHorizontalSpace = true;
		comboSelectedIons.setLayoutData(gridData);
		comboSelectedIons.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chromatogramChart.deleteSeries();
				refreshUpdateOverlayChart();
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
				modifyDataStatusLabel();
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
				modifyDataStatusLabel();
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
				modifyDataStatusLabel();
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
				modifyDataStatusLabel();
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

				colorScheme.reset();
				usedColors.clear();
				chromatogramChart.deleteSeries();
				refreshUpdateOverlayChart();
				modifySelectedIonsModus();
				modifyDisplayAndShiftModus();
				modifyDataStatusLabel();
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
						applyOverlaySettings();
					} catch(Exception e1) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Settings", "Something has gone wrong to apply the chart settings.");
					}
				}
			}
		});
	}

	private void modifySelectedIonsModus() {

		String overlayType = comboOverlayType.getText().trim();
		boolean enabled = (overlayType.contains(OVERLAY_TYPE_XIC) || overlayType.contains(OVERLAY_TYPE_SIC) || overlayType.contains(OVERLAY_TYPE_TSC));
		//
		comboSelectedIons.setEnabled(enabled);
	}

	private void modifyDisplayAndShiftModus() {

		String selectedSeries = comboSelectedSeries.getText().trim();
		boolean visible = !selectedSeries.equals(SELECTED_SERIES_NONE);
		//
		comboDisplayModus.setVisible(visible);
		textShiftX.setVisible(visible);
		comboScaleX.setVisible(visible);
		buttonShiftLeft.setVisible(visible);
		buttonShiftRight.setVisible(visible);
		textShiftY.setVisible(visible);
		comboScaleY.setVisible(visible);
		buttonShiftUp.setVisible(visible);
		buttonShiftDown.setVisible(visible);
		excludeComposite(compositeShift, visible);
	}

	private void excludeComposite(Composite composite, boolean visible) {

		/*
		 * Modify the composite.
		 */
		GridData gridDataComposite = (GridData)composite.getLayoutData();
		gridDataComposite.exclude = !visible;
		//
		GridData gridDataType = (GridData)compositeType.getLayoutData();
		gridDataType.horizontalSpan += (visible) ? -1 : 1;
		//
		Composite parent = compositeToolbar;
		parent.layout(false);
		parent.redraw();
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

	private void applyOverlaySettings() {

		comboOverlayType.select(0);
		comboSelectedSeries.select(0);
		//
		modifySelectedIonsModus();
		modifyDisplayAndShiftModus();
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
		setComboAxisItems();
	}

	@Focus
	public void setFocus() {

		refreshUpdateOverlayChart();
	}

	private void refreshUpdateOverlayChart() {

		List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections(partService);
		Set<String> availableSeriesIds = new HashSet<String>();
		BaseChart baseChart = chromatogramChart.getBaseChart();
		List<Integer> ions = getSelectedIons();
		//
		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		for(int i = 0; i < chromatogramSelections.size(); i++) {
			IChromatogramSelection chromatogramSelection = chromatogramSelections.get(i);
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			String chromatogramName = chromatogram.getName() + EDITOR_TAB + (i + 1);
			/*
			 * Select which series shall be displayed.
			 */
			String[] overlayTypes = comboOverlayType.getText().trim().split(ESCAPE_CONCATENATOR + OVERLAY_TYPE_CONCATENATOR);
			for(String overlayType : overlayTypes) {
				if(overlayType.equals(OVERLAY_TYPE_SIC)) {
					/*
					 * SIC
					 */
					if(chromatogram instanceof IChromatogramMSD) {
						for(int ion : ions) {
							String seriesId = chromatogramName + OVERLAY_START_MARKER + overlayType + "-" + ion + OVERLAY_STOP_MARKER;
							availableSeriesIds.add(seriesId);
							if(!baseChart.isSeriesContained(seriesId)) {
								List<Integer> sic = new ArrayList<Integer>();
								sic.add(ion);
								Color color = colorSchemeSic.getColor();
								colorSchemeSic.incrementColor();
								lineSeriesDataList.add(getLineSeriesData(chromatogram, seriesId, overlayType, color, sic));
							}
						}
					}
				} else if(overlayType.equals(OVERLAY_TYPE_BPC) || overlayType.equals(OVERLAY_TYPE_XIC) || overlayType.equals(OVERLAY_TYPE_TSC)) {
					/*
					 * BPC, XIC, TSC
					 */
					if(chromatogram instanceof IChromatogramMSD) {
						Color color = getSeriesColor(chromatogramName);
						String seriesId = chromatogramName + OVERLAY_START_MARKER + overlayType + OVERLAY_STOP_MARKER;
						availableSeriesIds.add(seriesId);
						if(!baseChart.isSeriesContained(seriesId)) {
							lineSeriesDataList.add(getLineSeriesData(chromatogram, seriesId, overlayType, color, ions));
						}
					}
				} else {
					/*
					 * TIC
					 */
					Color color = getSeriesColor(chromatogramName);
					String seriesId = chromatogramName + OVERLAY_START_MARKER + overlayType + OVERLAY_STOP_MARKER;
					availableSeriesIds.add(seriesId);
					if(!baseChart.isSeriesContained(seriesId)) {
						lineSeriesDataList.add(getLineSeriesData(chromatogram, seriesId, overlayType, color, ions));
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
		items[0] = SELECTED_SERIES_NONE;
		int index = 1;
		for(String seriesId : availableSeriesIds) {
			items[index++] = seriesId;
		}
		//
		comboSelectedSeries.setItems(items);
		comboSelectedSeries.setText(SELECTED_SERIES_NONE);
		//
		modifyDataStatusLabel();
	}

	private List<Integer> getSelectedIons() {

		DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();
		List<Integer> selectedIons = new ArrayList<Integer>();
		//
		String comboText = comboSelectedIons.getText().trim();
		String ionsText = "";
		if(selectedIonsMap.containsKey(comboSelectedIons.getText().trim())) {
			ionsText = selectedIonsMap.get(comboText);
		} else {
			ionsText = comboText;
		}
		//
		String[] ions = ionsText.split(SELECTED_IONS_CONCATENATOR);
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

	private Color getSeriesColor(String chromatogramName) {

		Color color = usedColors.get(chromatogramName);
		if(color == null) {
			color = colorScheme.getColor();
			colorScheme.incrementColor();
			usedColors.put(chromatogramName, color);
		}
		return color;
	}

	private ILineSeriesData getLineSeriesData(IChromatogram chromatogram, String seriesId, String overlayType, Color color, List<Integer> ions) {

		double[] xSeries = new double[chromatogram.getNumberOfScans()];
		double[] ySeries = new double[chromatogram.getNumberOfScans()];
		LineStyle lineStyle = getLineStyle(overlayType);
		/*
		 * Get the data.
		 */
		int index = 0;
		for(IScan scan : chromatogram.getScans()) {
			/*
			 * Get the retention time and intensity.
			 */
			xSeries[index] = scan.getRetentionTime();
			ySeries[index] = getIntensity(scan, overlayType, ions);
			index++;
		}
		/*
		 * Add the series.
		 */
		ISeriesData seriesData = new SeriesData(xSeries, ySeries, seriesId);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSerieSettings = lineSeriesData.getLineSeriesSettings();
		lineSerieSettings.setLineColor(color);
		lineSerieSettings.setLineStyle(lineStyle);
		lineSerieSettings.setEnableArea(false);
		ILineSeriesSettings lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSerieSettings.getSeriesSettingsHighlight();
		lineSeriesSettingsHighlight.setLineWidth(2);
		//
		return lineSeriesData;
	}

	private double getIntensity(IScan scan, String overlayType, List<Integer> ions) {

		double intensity = 0.0d;
		if(overlayType.equals(OVERLAY_TYPE_TIC)) {
			/*
			 * TIC
			 */
			intensity = scan.getTotalSignal();
		} else if(overlayType.equals(OVERLAY_TYPE_BPC)) {
			/*
			 * BPC
			 */
			if(scan instanceof IScanMSD) {
				IScanMSD scanMSD = (IScanMSD)scan;
				IIon ion = scanMSD.getHighestAbundance();
				if(ion != null) {
					intensity = ion.getAbundance();
				}
			}
		} else if(overlayType.equals(OVERLAY_TYPE_XIC) || overlayType.equals(OVERLAY_TYPE_SIC)) {
			/*
			 * XIC, SIC
			 */
			if(scan instanceof IScanMSD) {
				IScanMSD scanMSD = (IScanMSD)scan;
				IExtractedIonSignal extractedIonSignal = scanMSD.getExtractedIonSignal();
				for(int ion : ions) {
					intensity += extractedIonSignal.getAbundance(ion);
				}
			}
		} else if(overlayType.equals(OVERLAY_TYPE_TSC)) {
			/*
			 * TSC
			 */
			if(scan instanceof IScanMSD) {
				IScanMSD scanMSD = (IScanMSD)scan;
				IExtractedIonSignal extractedIonSignal = scanMSD.getExtractedIonSignal();
				intensity = scanMSD.getTotalSignal();
				for(int ion : ions) {
					intensity -= extractedIonSignal.getAbundance(ion);
				}
			}
		}
		//
		return intensity;
	}

	private LineStyle getLineStyle(String overlayType) {

		LineStyle lineStyle;
		if(overlayType.equals(OVERLAY_TYPE_TIC)) {
			lineStyle = LineStyle.SOLID;
		} else if(overlayType.equals(OVERLAY_TYPE_BPC)) {
			lineStyle = LineStyle.DASH;
		} else if(overlayType.equals(OVERLAY_TYPE_XIC)) {
			lineStyle = LineStyle.DASH;
		} else if(overlayType.equals(OVERLAY_TYPE_SIC)) {
			lineStyle = LineStyle.DASH;
		} else if(overlayType.equals(OVERLAY_TYPE_TSC)) {
			lineStyle = LineStyle.DASH;
		} else {
			lineStyle = LineStyle.DOT;
		}
		return lineStyle;
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
