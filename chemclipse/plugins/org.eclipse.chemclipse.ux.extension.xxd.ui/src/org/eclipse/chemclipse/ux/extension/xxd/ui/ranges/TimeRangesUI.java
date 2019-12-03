/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.ranges;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.model.ranges.TimeRanges;
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class TimeRangesUI extends Composite {

	private ComboViewer comboViewer;
	private Text textStart;
	private Text textCenter;
	private Text textStop;
	/*
	 * TimeRanges is the object, that contains a map of ranges.
	 * TimeRange is the currently selected time range.
	 */
	private TimeRanges timeRanges = null;
	private TimeRange timeRange = null;
	//
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();
	//
	private IUpdateListener updateListener = null;

	public TimeRangesUI(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	public void setInput(TimeRanges timeRanges) {

		this.timeRanges = timeRanges;
		updateInput();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	/**
	 * Updates the values of the currently selected time range.
	 */
	public void update() {

		updateTimeRange(timeRange);
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(4, true);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		comboViewer = createComboViewer(this);
		textStart = createText(this, TimeRange.Marker.START);
		textCenter = createText(this, TimeRange.Marker.CENTER);
		textStop = createText(this, TimeRange.Marker.STOP);
	}

	private ComboViewer createComboViewer(Composite composite) {

		ComboViewer comboViewer = new ComboViewer(composite, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof String) {
					return element.toString();
				}
				return null;
			}
		});
		/*
		 * Select the item.
		 */
		combo.setToolTipText("Select an identifier.");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof String) {
					String identifier = (String)object;
					updateTimeRange(identifier);
				}
			}
		});
		//
		return comboViewer;
	}

	private Text createText(Composite composite, TimeRange.Marker marker) {

		Text text = new Text(composite, SWT.BORDER);
		text.setText("");
		switch(marker) {
			case START:
				text.setToolTipText("Start Retention Time");
				break;
			case CENTER:
				text.setToolTipText("Center Retention Time");
				break;
			case STOP:
				text.setToolTipText("Stop Retention Time");
				break;
		}
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		TimeRangeValidator validator = new TimeRangeValidator(marker);
		ControlDecoration controlDecoration = new ControlDecoration(text, SWT.LEFT | SWT.TOP);
		//
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				super.keyReleased(e);
				if(timeRange != null) {
					validator.setTimeRange(timeRange);
					if(validate(validator, controlDecoration, text)) {
						switch(marker) {
							case START:
								timeRange.updateStart(validator.getRetentionTime());
								break;
							case CENTER:
								timeRange.updateCenter(validator.getRetentionTime());
								break;
							case STOP:
								timeRange.updateStop(validator.getRetentionTime());
								break;
						}
						//
						fireUpdate();
					}
				}
			}
		});
		//
		return text;
	}

	private void updateInput() {

		String identifier = "";
		//
		if(timeRanges != null) {
			/*
			 * Sort
			 */
			List<String> keys = new ArrayList<>(timeRanges.keySet());
			Collections.sort(keys);
			//
			Combo combo = comboViewer.getCombo();
			int selectionIndex = combo.getSelectionIndex();
			comboViewer.setInput(keys);
			//
			if(combo.getItemCount() > 0) {
				int index = (selectionIndex >= 0 && selectionIndex < combo.getItemCount()) ? selectionIndex : 0;
				combo.select(index);
				identifier = keys.get(index);
			}
		} else {
			comboViewer.setInput(null);
		}
		//
		updateTimeRange(identifier);
	}

	private void updateTimeRange(String identifier) {

		timeRange = null;
		if(timeRanges != null) {
			timeRange = timeRanges.get(identifier);
		}
		//
		updateTimeRange(timeRange);
	}

	private void updateTimeRange(TimeRange timeRange) {

		boolean enabled = false;
		String start = "";
		String center = "";
		String stop = "";
		//
		if(timeRange != null) {
			start = getRetentionTimeMinutes(timeRange.getStart());
			center = getRetentionTimeMinutes(timeRange.getCenter());
			stop = getRetentionTimeMinutes(timeRange.getStop());
			enabled = true;
		}
		//
		textStart.setText(start);
		textCenter.setText(center);
		textStop.setText(stop);
		//
		textStart.setEnabled(enabled);
		textCenter.setEnabled(enabled);
		textStop.setEnabled(enabled);
	}

	private String getRetentionTimeMinutes(int milliseconds) {

		return decimalFormat.format(milliseconds / TimeRange.MINUTE_FACTOR);
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

	private void fireUpdate() {

		if(updateListener != null) {
			updateListener.update();
		}
	}
}
