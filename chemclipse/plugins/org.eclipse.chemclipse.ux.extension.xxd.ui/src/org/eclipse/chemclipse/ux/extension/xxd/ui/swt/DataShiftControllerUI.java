/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.OverlayChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.DisplayModus;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swtchart.ISeries;
import org.eclipse.swtchart.ISeriesSet;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IAxisScaleConverter;
import org.eclipse.swtchart.extensions.core.IAxisSettings;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IExtendedChart;
import org.eclipse.swtchart.extensions.core.ISeriesSettings;
import org.eclipse.swtchart.extensions.core.MappedSeriesSettings;
import org.eclipse.swtchart.extensions.core.SeriesStatusAdapter;

public class DataShiftControllerUI extends Composite {

	private static final Logger logger = Logger.getLogger(DataShiftControllerUI.class);
	/*
	 * Mirror Button
	 */
	private static final String BUTTON_MIRROR_KEY = "DisplayModus";
	private static final String normalTooltip = "Set the selected series to normal modus.";
	private static final String mirrorTooltip = "Set the selected series to mirrored modus.";
	//
	private Text textX;
	private ComboViewer comboViewerX;
	private Text textY;
	private ComboViewer comboViewerY;
	//
	private ComboViewer comboViewerSelect;
	private Button buttonMirrorSeries;
	//
	private Button buttonShiftLeft;
	private Button buttonShiftRight;
	private Button buttonShiftUp;
	private Button buttonShiftDown;
	//
	private ChromatogramChart chromatogramChart = null;
	private BaseChart baseChart = null;
	//
	private final OverlayChartSupport overlayChartSupport = new OverlayChartSupport();
	private final Set<String> mirroredSeries = new HashSet<>();

	public DataShiftControllerUI(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	public void setChromatogramChart(ChromatogramChart chromatogramChart) {

		this.chromatogramChart = chromatogramChart;
		if(chromatogramChart != null) {
			this.baseChart = chromatogramChart.getBaseChart();
			this.baseChart.addSeriesStatusListener(new SeriesStatusAdapter() {

				@Override
				public void handleSeriesSelectionEvent(String seriesId) {

					updateComboViewerSelect(seriesId);
				}
			});
		}
	}

	@Override
	public void update() {

		super.update();
		setAxisValues();
		updateComboViewerSelect(BaseChart.SELECTED_SERIES_NONE);
	}

	public void reset() {

		if(baseChart != null) {
			baseChart.resetSeriesSettings();
			mirroredSeries.clear();
		}
		updateComboViewerSelect(BaseChart.SELECTED_SERIES_NONE);
	}

	protected void setDisplayModus(DisplayModus displayModus, String seriesId) {

		if(chromatogramChart != null) {
			BaseChart baseChart = chromatogramChart.getBaseChart();
			IChartSettings chartSettings = chromatogramChart.getChartSettings();
			if(DisplayModus.MIRRORED.equals(displayModus)) {
				if(!mirroredSeries.contains(seriesId)) {
					baseChart.multiplySeries(seriesId, IExtendedChart.Y_AXIS, -1.0d);
					mirroredSeries.add(seriesId);
				}
			} else {
				if(mirroredSeries.contains(seriesId)) {
					baseChart.multiplySeries(seriesId, IExtendedChart.Y_AXIS, -1.0d);
					mirroredSeries.remove(seriesId);
				}
			}
			//
			modifyWidgetStatus();
			chromatogramChart.applySettings(chartSettings);
			chromatogramChart.adjustRange(true);
			chromatogramChart.redraw();
		}
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(14, false);
		setLayout(gridLayout);
		//
		createButtonAutoMirror(this);
		createVerticalSeparator(this);
		textX = createTextX(this);
		comboViewerX = createComboViewerX(this);
		textY = createTextY(this);
		comboViewerY = createComboViewerY(this);
		createButtonShift(this);
		createVerticalSeparator(this);
		comboViewerSelect = createComboViewerSelect(this);
		buttonMirrorSeries = createButtonMirror(this);
		buttonShiftLeft = createButtonLeft(this);
		buttonShiftRight = createButtonRight(this);
		buttonShiftUp = createButtonUp(this);
		buttonShiftDown = createButtonDown(this);
	}

	private Text createTextX(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("Shift data in x direction.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		text.setLayoutData(gridData);
		//
		return text;
	}

	private ComboViewer createComboViewerX(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IAxisSettings) {
					IAxisSettings axisSettings = (IAxisSettings)element;
					return axisSettings.getLabel();
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Select the x axis.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof IAxisSettings) {
					persistOverlayShiftX();
				}
			}
		});
		//
		return comboViewer;
	}

	private Text createTextY(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("Shift data in y direction.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		text.setLayoutData(gridData);
		//
		return text;
	}

	private ComboViewer createComboViewerY(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IAxisSettings) {
					IAxisSettings axisSettings = (IAxisSettings)element;
					return axisSettings.getLabel();
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Select the y axis.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof IAxisSettings) {
					persistOverlayShiftY();
				}
			}
		});
		//
		return comboViewer;
	}

	private void createVerticalSeparator(Composite parent) {

		Label label = new Label(parent, SWT.SEPARATOR | SWT.VERTICAL);
		GridData gridData = new GridData();
		gridData.heightHint = 35;
		label.setLayoutData(gridData);
	}

	private Button createButtonShift(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Shift the data in XY direction.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("rawtypes")
			@Override
			public void widgetSelected(SelectionEvent e) {

				if(baseChart != null) {
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
			}
		});
		//
		return button;
	}

	private void createButtonAutoMirror(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Auto Mirror Chromatograms");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SHIFT_AUTO_MIRROR, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("rawtypes")
			@Override
			public void widgetSelected(SelectionEvent e) {

				if(chromatogramChart != null) {
					//
					IChartSettings chartSettings = chromatogramChart.getChartSettings();
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
			}
		});
	}

	private ComboViewer createComboViewerSelect(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof MappedSeriesSettings) {
					MappedSeriesSettings mappedSeriesSettings = (MappedSeriesSettings)element;
					ISeriesSettings seriesSettings = mappedSeriesSettings.getSeriesSettings();
					if(seriesSettings != null) {
						return seriesSettings.getDescription();
					} else {
						return mappedSeriesSettings.getIdentifier();
					}
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Highlight the selected series.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof MappedSeriesSettings) {
					MappedSeriesSettings mappedSeriesSettings = (MappedSeriesSettings)object;
					if(baseChart != null) {
						String identifier = mappedSeriesSettings.getIdentifier();
						baseChart.resetSeriesSettings();
						baseChart.selectSeries(identifier);
						baseChart.redraw();
						modifyWidgetStatus();
					}
				}
			}
		});
		//
		return comboViewer;
	}

	private Button createButtonMirror(Composite parent) {

		//
		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText(""); // Will be set dynamically
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SHIFT_MIRROR, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = button.getData(BUTTON_MIRROR_KEY);
				DisplayModus displayModus = object instanceof DisplayModus ? (DisplayModus)object : DisplayModus.NORMAL;
				setDisplayModus(displayModus, getSelectedSeriesId());
			}
		});
		//
		return button;
	}

	private Button createButtonLeft(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Move Left");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_BACKWARD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(baseChart != null) {
					double shiftX = getShiftValuePrimary(IExtendedChart.X_AXIS) * -1.0d;
					String selectedSeriesId = getSelectedSeriesId();
					baseChart.shiftSeries(selectedSeriesId, shiftX, 0.0d);
					baseChart.redraw();
					persistOverlayShiftX();
				}
			}
		});
		//
		return button;
	}

	private Button createButtonRight(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Move Right");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_FORWARD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(baseChart != null) {
					double shiftX = getShiftValuePrimary(IExtendedChart.X_AXIS);
					String selectedSeriesId = getSelectedSeriesId();
					baseChart.shiftSeries(selectedSeriesId, shiftX, 0.0d);
					baseChart.redraw();
					persistOverlayShiftX();
				}
			}
		});
		//
		return button;
	}

	private Button createButtonUp(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Move Up");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_UP_2, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(baseChart != null) {
					double shiftY = getShiftValuePrimary(IExtendedChart.Y_AXIS);
					String selectedSeriesId = getSelectedSeriesId();
					baseChart.shiftSeries(selectedSeriesId, 0.0d, shiftY);
					baseChart.redraw();
					persistOverlayShiftY();
				}
			}
		});
		//
		return button;
	}

	private Button createButtonDown(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Move Down");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_DOWN_2, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(baseChart != null) {
					double shiftY = getShiftValuePrimary(IExtendedChart.Y_AXIS) * -1.0d;
					String selectedSeriesId = getSelectedSeriesId();
					baseChart.shiftSeries(selectedSeriesId, 0.0d, shiftY);
					baseChart.redraw();
					persistOverlayShiftY();
				}
			}
		});
		//
		return button;
	}

	public void setAxisValues() {

		setAxisValuesX();
		setAxisValuesY();
	}

	private void setAxisValuesX() {

		if(baseChart != null) {
			List<IAxisSettings> axisSettings = baseChart.getAxisSettings(IExtendedChart.X_AXIS);
			comboViewerX.setInput(axisSettings);
			int size = axisSettings.size();
			//
			if(size > 0) {
				/*
				 * Get the shift value from the settings.
				 */
				double overlayShiftX = overlayChartSupport.getOverlayShiftX();
				int indexShiftX = overlayChartSupport.getIndexShiftX();
				Combo combo = comboViewerX.getCombo();
				//
				if(indexShiftX >= 0 && indexShiftX < size) {
					DecimalFormat decimalFormat = baseChart.getDecimalFormat(IExtendedChart.X_AXIS, indexShiftX);
					combo.select(indexShiftX);
					textX.setText(decimalFormat.format(overlayShiftX));
				} else {
					/*
					 * Default: Milliseconds
					 */
					combo.select(0);
					textX.setText(Integer.toString(0));
				}
			}
		}
	}

	private void setAxisValuesY() {

		if(baseChart != null) {
			/*
			 * Y Axes
			 */
			List<IAxisSettings> axisSettings = baseChart.getAxisSettings(IExtendedChart.Y_AXIS);
			comboViewerY.setInput(axisSettings);
			int size = axisSettings.size();
			//
			if(size > 0) {
				/*
				 * Get the shift value from the settings.
				 */
				double absoluteShiftY = overlayChartSupport.getOverlayShiftY();
				int indexShiftY = overlayChartSupport.getIndexShiftY();
				Combo combo = comboViewerY.getCombo();
				//
				if(indexShiftY >= 0 && indexShiftY < size) {
					DecimalFormat decimalFormat = baseChart.getDecimalFormat(IExtendedChart.Y_AXIS, indexShiftY);
					combo.select(indexShiftY);
					textY.setText(decimalFormat.format(absoluteShiftY));
				} else {
					/*
					 * Default: Absolute Intensity
					 */
					combo.select(0);
					textY.setText(Double.toString(0.0d));
				}
			}
		}
	}

	private void modifyWidgetStatus() {

		/*
		 * Selected Series
		 */
		String selectedSeries = getSelectedSeriesId();
		boolean isSeriesSelected = !selectedSeries.equals(BaseChart.SELECTED_SERIES_NONE);
		//
		buttonShiftLeft.setEnabled(isSeriesSelected);
		buttonShiftRight.setEnabled(isSeriesSelected);
		buttonShiftUp.setEnabled(isSeriesSelected);
		buttonShiftDown.setEnabled(isSeriesSelected);
		//
		buttonMirrorSeries.setEnabled(isSeriesSelected);
		if(mirroredSeries.contains(selectedSeries)) {
			buttonMirrorSeries.setData(BUTTON_MIRROR_KEY, DisplayModus.NORMAL);
			buttonMirrorSeries.setToolTipText(normalTooltip);
		} else {
			buttonMirrorSeries.setData(BUTTON_MIRROR_KEY, DisplayModus.MIRRORED);
			buttonMirrorSeries.setToolTipText(mirrorTooltip);
		}
	}

	private double getShiftValuePrimary(String axis) {

		double shiftValue = 0.0d;
		if(baseChart != null) {
			/*
			 * Get the selected axis.
			 */
			int selectedAxis;
			if(axis.equals(IExtendedChart.X_AXIS)) {
				selectedAxis = comboViewerX.getCombo().getSelectionIndex();
			} else {
				selectedAxis = comboViewerY.getCombo().getSelectionIndex();
			}
			/*
			 * Get the value.
			 */
			double value;
			if(axis.equals(IExtendedChart.X_AXIS)) {
				value = getTextValue(textX);
			} else {
				value = getTextValue(textY);
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
		}
		//
		return shiftValue;
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

		overlayChartSupport.setOverlayShiftX(getTextValue(textX));
		overlayChartSupport.setIndexShiftX(comboViewerX.getCombo().getSelectionIndex());
	}

	private void persistOverlayShiftY() {

		overlayChartSupport.setOverlayShiftY(getTextValue(textY));
		overlayChartSupport.setIndexShiftY(comboViewerY.getCombo().getSelectionIndex());
	}

	private void updateComboViewerSelect(String seriesId) {

		List<MappedSeriesSettings> mappedSettings = new ArrayList<>();
		mappedSettings.add(new MappedSeriesSettings(BaseChart.SELECTED_SERIES_NONE, null));
		String label = BaseChart.SELECTED_SERIES_NONE;
		//
		if(baseChart != null) {
			ISeriesSet seriesSet = baseChart.getSeriesSet();
			if(seriesSet != null) {
				for(ISeries<?> series : seriesSet.getSeries()) {
					String id = series.getId();
					ISeriesSettings seriesSettings = baseChart.getSeriesSettings(id);
					if(seriesSettings != null) {
						/*
						 * Add the mapping
						 */
						mappedSettings.add(new MappedSeriesSettings(id, seriesSettings));
						/*
						 * Is the current series selected?
						 */
						if(id.equals(seriesId)) {
							String description = seriesSettings.getDescription();
							if(!"".equals(description)) {
								label = description;
							} else {
								label = id;
							}
						}
					}
				}
			}
		}
		/*
		 * Display by default the series none entry.
		 */
		comboViewerSelect.setInput(mappedSettings);
		comboViewerSelect.getCombo().setText(label);
		modifyWidgetStatus();
	}

	private String getSelectedSeriesId() {

		String id = BaseChart.SELECTED_SERIES_NONE;
		Combo combo = comboViewerSelect.getCombo();
		int index = combo.getSelectionIndex();
		Object object = comboViewerSelect.getElementAt(index);
		if(object instanceof MappedSeriesSettings) {
			MappedSeriesSettings mappedSeriesSettings = (MappedSeriesSettings)object;
			id = mappedSeriesSettings.getIdentifier();
		}
		//
		return id;
	}
}
