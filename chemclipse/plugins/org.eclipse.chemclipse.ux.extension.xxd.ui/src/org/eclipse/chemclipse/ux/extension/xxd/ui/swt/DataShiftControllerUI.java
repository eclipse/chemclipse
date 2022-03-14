/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
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

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.OverlayChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.ShiftValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.DisplayModus;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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
import org.eclipse.swtchart.Range;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IAxisScaleConverter;
import org.eclipse.swtchart.extensions.core.IAxisSettings;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IExtendedChart;
import org.eclipse.swtchart.extensions.core.ISeriesSettings;
import org.eclipse.swtchart.extensions.core.MappedSeriesSettings;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.core.RangeRestriction.ExtendType;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.core.SeriesStatusAdapter;

public class DataShiftControllerUI extends Composite implements IExtendedPartUI {

	private static final String SERIES_REDRAW = "";
	private static final String SERIES_ALL = "";
	/*
	 * Mirror Button
	 */
	private static final String KEY_MIRROR_MODUS = "MirrorModus";
	private static final String IMAGE_MIRROR = IApplicationImage.IMAGE_SHIFT_MIRROR;
	private static final String TOOLTIP_MIRROR = "the mirror modus";
	//
	private Button buttonAutoMirror;
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
	private ScrollableChart scrollableChart = null;
	private BaseChart baseChart = null;
	//
	private final OverlayChartSupport overlayChartSupport = new OverlayChartSupport();
	private final Set<String> mirroredSeries = new HashSet<>();
	private IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();

	public DataShiftControllerUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setScrollableChart(ScrollableChart scrollableChart) {

		this.scrollableChart = scrollableChart;
		if(scrollableChart != null) {
			this.baseChart = scrollableChart.getBaseChart();
			this.baseChart.addSeriesStatusListener(new SeriesStatusAdapter() {

				@Override
				public void handleSeriesSelectionEvent(String seriesId) {

					updateComboViewerSelect(seriesId);
				}

				@Override
				public void handleRedrawEvent() {

					updateComboViewerSelect(SERIES_REDRAW);
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

		if(scrollableChart != null) {
			BaseChart baseChart = scrollableChart.getBaseChart();
			IChartSettings chartSettings = scrollableChart.getChartSettings();
			//
			if(DisplayModus.MIRROR.equals(displayModus)) {
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
			updateWidgets();
			scrollableChart.applySettings(chartSettings);
			scrollableChart.adjustRange(true);
			scrollableChart.redraw();
		}
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(14, false);
		setLayout(gridLayout);
		//
		buttonAutoMirror = createButtonAutoMirror(this);
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
		//
		initialize();
	}

	private void initialize() {

		enableButton(buttonAutoMirror, IMAGE_MIRROR, TOOLTIP_MIRROR, false);
		enableButton(buttonMirrorSeries, IMAGE_MIRROR, TOOLTIP_MIRROR, false);
	}

	private Text createTextX(Composite parent) {

		String tooltip = "Shift data in x direction.";
		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText(tooltip);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		text.setLayoutData(gridData);
		//
		ShiftValidator shiftValidator = new ShiftValidator();
		ControlDecoration controlDecoration = new ControlDecoration(text, SWT.LEFT | SWT.TOP);
		//
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				super.keyReleased(e);
				if(validate(shiftValidator, controlDecoration, tooltip, text)) {
					if(e.keyCode == SWT.LF || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
						if(baseChart != null) {
							double deltaX = getShiftValuePrimary(IExtendedChart.X_AXIS);
							double deltaY = 0.0d;
							shiftSeries(SERIES_ALL, deltaX, deltaY);
						}
					}
				}
			}
		});
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

		String tooltip = "Shift data in y direction.";
		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText(tooltip);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		text.setLayoutData(gridData);
		//
		ShiftValidator shiftValidator = new ShiftValidator();
		ControlDecoration controlDecoration = new ControlDecoration(text, SWT.LEFT | SWT.TOP);
		//
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				super.keyReleased(e);
				if(validate(shiftValidator, controlDecoration, tooltip, text)) {
					if(e.keyCode == SWT.LF || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
						if(baseChart != null) {
							double deltaX = 0.0d;
							double deltaY = getShiftValuePrimary(IExtendedChart.Y_AXIS);
							shiftSeries(SERIES_ALL, deltaX, deltaY);
						}
					}
				}
			}
		});
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

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(baseChart != null) {
					double deltaX = getShiftValuePrimary(IExtendedChart.X_AXIS);
					double deltaY = getShiftValuePrimary(IExtendedChart.Y_AXIS);
					shiftSeries(SERIES_ALL, deltaX, deltaY);
				}
			}
		});
		//
		return button;
	}

	private Button createButtonAutoMirror(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SHIFT_AUTO_MIRROR, IApplicationImage.SIZE_16x16));
		button.setData(KEY_MIRROR_MODUS, DisplayModus.NORMAL);
		button.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("rawtypes")
			@Override
			public void widgetSelected(SelectionEvent e) {

				if(scrollableChart != null) {
					/*
					 * Settings
					 */
					Object object = button.getData(KEY_MIRROR_MODUS);
					DisplayModus displayModus = object instanceof DisplayModus ? (DisplayModus)object : DisplayModus.NORMAL;
					IChartSettings chartSettings = scrollableChart.getChartSettings();
					//
					if(DisplayModus.NORMAL.equals(displayModus)) {
						/*
						 * If status is normal, then mirror the series.
						 */
						displayModus = DisplayModus.MIRROR;
						int i = 0;
						int modulo = preferences.getInt(PreferenceConstants.P_MODULO_AUTO_MIRROR_CHROMATOGRAMS);
						for(ISeries series : baseChart.getSeriesSet().getSeries()) {
							if(i % modulo == 1) {
								String seriesId = series.getId();
								if(!mirroredSeries.contains(seriesId)) {
									baseChart.multiplySeries(seriesId, IExtendedChart.Y_AXIS, -1.0d);
									mirroredSeries.add(seriesId);
								}
							}
							i++;
						}
					} else {
						/*
						 * If status is mirrored, then set the series to normal.
						 */
						displayModus = DisplayModus.NORMAL;
						for(ISeries series : baseChart.getSeriesSet().getSeries()) {
							String seriesId = series.getId();
							if(mirroredSeries.contains(seriesId)) {
								baseChart.multiplySeries(seriesId, IExtendedChart.Y_AXIS, -1.0d);
								mirroredSeries.remove(seriesId);
							}
						}
					}
					//
					buttonAutoMirror.setData(KEY_MIRROR_MODUS, displayModus);
					enableButton(buttonAutoMirror, IMAGE_MIRROR, TOOLTIP_MIRROR, DisplayModus.MIRROR.equals(displayModus));
					scrollableChart.applySettings(chartSettings);
					scrollableChart.adjustRange(true);
					scrollableChart.redraw();
				}
			}
		});
		//
		return button;
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
						updateWidgets();
					}
				}
			}
		});
		//
		return comboViewer;
	}

	private Button createButtonMirror(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText(""); // Will be set dynamically
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SHIFT_MIRROR, IApplicationImage.SIZE_16x16));
		button.setData(KEY_MIRROR_MODUS, DisplayModus.NORMAL);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = button.getData(KEY_MIRROR_MODUS);
				DisplayModus displayModus = object instanceof DisplayModus ? (DisplayModus)object : DisplayModus.NORMAL;
				displayModus = DisplayModus.NORMAL.equals(displayModus) ? DisplayModus.MIRROR : DisplayModus.NORMAL;
				setDisplayModus(displayModus, getSelectedSeriesId());
				updateComboViewerSelect(BaseChart.SELECTED_SERIES_NONE);
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
					String seriesId = getSelectedSeriesId();
					double deltaX = getShiftValuePrimary(IExtendedChart.X_AXIS) * -1.0d;
					double deltaY = 0.0d;
					shiftSeries(seriesId, deltaX, deltaY);
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
					String seriesId = getSelectedSeriesId();
					double deltaX = getShiftValuePrimary(IExtendedChart.X_AXIS);
					double deltaY = 0.0d;
					shiftSeries(seriesId, deltaX, deltaY);
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
					String seriesId = getSelectedSeriesId();
					double deltaX = 0.0d;
					double deltaY = getShiftValuePrimary(IExtendedChart.Y_AXIS);
					shiftSeries(seriesId, deltaX, deltaY);
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
					String seriesId = getSelectedSeriesId();
					double deltaX = 0.0d;
					double deltaY = getShiftValuePrimary(IExtendedChart.Y_AXIS) * -1.0d;
					shiftSeries(seriesId, deltaX, deltaY);
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

	private void updateWidgets() {

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
		DisplayModus displayModus = mirroredSeries.contains(selectedSeries) ? DisplayModus.MIRROR : DisplayModus.NORMAL;
		buttonMirrorSeries.setEnabled(isSeriesSelected);
		buttonMirrorSeries.setData(KEY_MIRROR_MODUS, displayModus);
		enableButton(buttonMirrorSeries, IMAGE_MIRROR, TOOLTIP_MIRROR, DisplayModus.MIRROR.equals(displayModus));
		//
		boolean active = mirroredSeries.size() > 0;
		buttonAutoMirror.setData(KEY_MIRROR_MODUS, active ? DisplayModus.MIRROR : DisplayModus.NORMAL);
		enableButton(buttonAutoMirror, IMAGE_MIRROR, TOOLTIP_MIRROR, active);
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
				value = getShift(textX);
			} else {
				value = getShift(textY);
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

	private double getShift(Text text) {

		double shift = 0.0d;
		ShiftValidator shiftValidator = new ShiftValidator();
		IStatus status = shiftValidator.validate(text.getText().trim());
		if(status.isOK()) {
			shift = shiftValidator.getShift();
		}
		return shift;
	}

	private void persistOverlayShiftX() {

		overlayChartSupport.setOverlayShiftX(getShift(textX));
		overlayChartSupport.setIndexShiftX(comboViewerX.getCombo().getSelectionIndex());
	}

	private void persistOverlayShiftY() {

		overlayChartSupport.setOverlayShiftY(getShift(textY));
		overlayChartSupport.setIndexShiftY(comboViewerY.getCombo().getSelectionIndex());
	}

	private void updateComboViewerSelect(String seriesId) {

		/*
		 * If this is the redraw action, keep the selection.
		 */
		if(seriesId.equals(SERIES_REDRAW)) {
			seriesId = getSelectedSeriesId();
		}
		//
		List<MappedSeriesSettings> mappedSettings = new ArrayList<>();
		mappedSettings.add(new MappedSeriesSettings(BaseChart.SELECTED_SERIES_NONE, null));
		//
		if(baseChart != null) {
			ISeriesSet seriesSet = baseChart.getSeriesSet();
			if(seriesSet != null) {
				for(ISeries<?> series : seriesSet.getSeries()) {
					String id = series.getId();
					ISeriesSettings seriesSettings = baseChart.getSeriesSettings(id);
					if(seriesSettings != null) {
						mappedSettings.add(new MappedSeriesSettings(id, seriesSettings));
					}
				}
			}
		}
		/*
		 * Display by default the series none entry if
		 * the given series id can't be found in the
		 * mapped settings.
		 */
		int index = -1;
		comboViewerSelect.setInput(mappedSettings);
		exitloop:
		for(int i = 0; i < mappedSettings.size(); i++) {
			if(seriesId.equals(mappedSettings.get(i).getIdentifier())) {
				index = i;
				break exitloop;
			}
		}
		//
		if(index >= 0) {
			comboViewerSelect.getCombo().select(index);
		}
		//
		updateWidgets();
	}

	private String getSelectedSeriesId() {

		String id = BaseChart.SELECTED_SERIES_NONE;
		Object object = comboViewerSelect.getStructuredSelection().getFirstElement();
		if(object instanceof MappedSeriesSettings) {
			MappedSeriesSettings mappedSeriesSettings = (MappedSeriesSettings)object;
			id = mappedSeriesSettings.getIdentifier();
		}
		//
		return id;
	}

	private boolean validate(IValidator<Object> validator, ControlDecoration controlDecoration, String tooltip, Text text) {

		IStatus status = validator.validate(text.getText().trim());
		if(status.isOK()) {
			controlDecoration.hide();
			text.setToolTipText(tooltip);
			return true;
		} else {
			controlDecoration.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage());
			controlDecoration.showHoverText(status.getMessage());
			controlDecoration.show();
			text.setToolTipText(status.getMessage());
			return false;
		}
	}

	private void shiftSeries(String seriesId, double deltaX, double deltaY) {

		if(scrollableChart != null) {
			/*
			 * Selected Range
			 */
			Range selectedRangeX = baseChart.getAxisSet().getXAxis(BaseChart.ID_PRIMARY_X_AXIS).getRange();
			Range selectedRangeY = baseChart.getAxisSet().getYAxis(BaseChart.ID_PRIMARY_Y_AXIS).getRange();
			/*
			 * Shift the series.
			 */
			baseChart.suspendUpdate(true);
			double shiftX = 0.0d;
			double shiftY = 0.0d;
			for(ISeries<?> series : baseChart.getSeriesSet().getSeries()) {
				/*
				 * Do a shift on the series
				 */
				boolean shift = false;
				if(seriesId.equals(SERIES_ALL)) {
					shift = true;
				} else if(seriesId.equals(series.getId())) {
					shift = true;
				}
				//
				if(shift) {
					shiftX += deltaX;
					shiftY += deltaY;
					baseChart.shiftSeries(series.getId(), shiftX, shiftY);
				}
			}
			/*
			 * Adjust the displayed range.
			 */
			IChartSettings chartSettings = scrollableChart.getChartSettings();
			RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
			rangeRestriction.setExtendTypeX(ExtendType.ABSOLUTE);
			rangeRestriction.setExtendMaxX(rangeRestriction.getExtendMaxX() + shiftX);
			rangeRestriction.setExtendTypeY(ExtendType.ABSOLUTE);
			rangeRestriction.setExtendMaxY(rangeRestriction.getExtendMaxY() + shiftY);
			scrollableChart.applySettings(chartSettings);
			baseChart.suspendUpdate(false);
			/*
			 * Update the chart and adjust the ranges.
			 */
			scrollableChart.adjustRange(true);
			scrollableChart.setRange(IExtendedChart.X_AXIS, selectedRangeX);
			scrollableChart.setRange(IExtendedChart.Y_AXIS, selectedRangeY);
			//
			persistOverlayShiftX();
			persistOverlayShiftY();
		}
	}
}
