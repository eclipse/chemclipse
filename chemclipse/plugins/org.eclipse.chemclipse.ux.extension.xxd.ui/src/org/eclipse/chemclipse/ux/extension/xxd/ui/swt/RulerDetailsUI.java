/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
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
import java.util.List;

import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedComboViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.RulerEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IAxisScaleConverter;
import org.eclipse.swtchart.extensions.core.IAxisSettings;
import org.eclipse.swtchart.extensions.core.IExtendedChart;
import org.eclipse.swtchart.extensions.core.ScrollableChart;

public class RulerDetailsUI extends Composite {

	private Text textStartX;
	private Text textStopX;
	private Text textDeltaX;
	private ComboViewer comboViewerX;
	private Text textStartY;
	private Text textStopY;
	private Text textDeltaY;
	private ComboViewer comboViewerY;
	//
	private BaseChart baseChart = null;
	private RulerEvent rulerEvent = null;
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");

	public RulerDetailsUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setInput(RulerEvent rulerEvent) {

		this.rulerEvent = rulerEvent;
		updateRanges();
	}

	public void setScrollableChart(ScrollableChart scrollableChart) {

		if(scrollableChart != null) {
			this.baseChart = scrollableChart.getBaseChart();
		}
		//
		setAxisValues();
		updateRanges();
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(8, false);
		setLayout(gridLayout);
		//
		textStartX = createText(this, "Start X");
		textStopX = createText(this, "Stop X");
		textDeltaX = createText(this, "Delta X");
		comboViewerX = createComboViewerX(this);
		textStartY = createText(this, "Start Y");
		textStopY = createText(this, "Stop Y");
		textDeltaY = createText(this, "Delta Y");
		comboViewerY = createComboViewerY(this);
	}

	private Text createText(Composite parent, String tooltip) {

		Text text = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
		text.setText("");
		text.setToolTipText(tooltip);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return text;
	}

	private ComboViewer createComboViewerX(Composite parent) {

		ComboViewer comboViewer = new EnhancedComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IAxisSettings axisSettings) {
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
					updateRangesX();
				}
			}
		});
		//
		return comboViewer;
	}

	private ComboViewer createComboViewerY(Composite parent) {

		ComboViewer comboViewer = new EnhancedComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IAxisSettings axisSettings) {
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
					updateRangesY();
				}
			}
		});
		//
		return comboViewer;
	}

	private void updateRanges() {

		updateRangesX();
		updateRangesY();
	}

	private void updateRangesX() {

		if(rulerEvent != null) {
			String axis = IExtendedChart.X_AXIS;
			textStartX.setText(convertToAxisUnit(axis, rulerEvent.getStartX()));
			textStopX.setText(convertToAxisUnit(axis, rulerEvent.getStopX()));
			textDeltaX.setText(convertToAxisUnit(axis, rulerEvent.getDeltaX()));
		} else {
			textStartX.setText("0");
			textStopX.setText("0");
			textDeltaX.setText("0");
		}
	}

	private void updateRangesY() {

		if(rulerEvent != null) {
			String axis = IExtendedChart.Y_AXIS;
			textStartY.setText(convertToAxisUnit(axis, rulerEvent.getStartY()));
			textStopY.setText(convertToAxisUnit(axis, rulerEvent.getStopY()));
			textDeltaY.setText(convertToAxisUnit(axis, rulerEvent.getDeltaY()));
		} else {
			textStartY.setText("0");
			textStopY.setText("0");
			textDeltaY.setText("0");
		}
	}

	private String convertToAxisUnit(String axis, double primaryValue) {

		double value = primaryValue;
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
			 * Convert
			 */
			if(selectedAxis > 0) {
				IAxisScaleConverter axisScaleConverter = baseChart.getAxisScaleConverter(axis, selectedAxis);
				value = axisScaleConverter.convertToSecondaryUnit(primaryValue);
			}
		}
		//
		return decimalFormat.format(value);
	}

	public void setAxisValues() {

		setAxisValuesX();
		setAxisValuesY();
	}

	private void setAxisValuesX() {

		if(baseChart != null) {
			List<IAxisSettings> axisSettings = baseChart.getAxisSettings(IExtendedChart.X_AXIS);
			comboViewerX.setInput(axisSettings);
			comboViewerX.getCombo().select(0);
		} else {
			comboViewerX.setInput(null);
		}
	}

	private void setAxisValuesY() {

		if(baseChart != null) {
			List<IAxisSettings> axisSettings = baseChart.getAxisSettings(IExtendedChart.Y_AXIS);
			comboViewerY.setInput(axisSettings);
			comboViewerY.getCombo().select(0);
		} else {
			comboViewerY.setInput(null);
		}
	}
}
