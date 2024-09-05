/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.ranges;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TimeRangesLabelProvider;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class TimeSpinner extends Composite {

	private static final int STEP_RETENTION_TIME = 1000; // milliseconds
	//
	private Text text;
	private Button buttonDecrease;
	private Button buttonIncrease;
	//
	private TimeRange.Marker marker;
	private TimeRange timeRange = null;
	private ITimeRangeUpdateListener updateListener = null;
	//
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");

	public TimeSpinner(Composite parent, int style, TimeRange.Marker marker) {

		super(parent, style);
		this.marker = marker;
		createControl();
	}

	@Override
	public void update() {

		super.update();
	}

	public void setUpdateListener(ITimeRangeUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public void setInput(TimeRange timeRange) {

		this.timeRange = timeRange;
		boolean enabled = timeRange != null;
		//
		text.setEnabled(enabled);
		text.setText(enabled ? extractRetentionTime() : "");
		buttonDecrease.setEnabled(enabled);
		buttonIncrease.setEnabled(enabled);
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		text = createText(this);
		buttonDecrease = createButtonModify(this, false);
		buttonIncrease = createButtonModify(this, true);
		//
		initialize();
	}

	private void initialize() {

		updateDescriptions();
	}

	private Text createText(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.setText("");
		//
		ControlDecoration controlDecoration = new ControlDecoration(text, SWT.LEFT | SWT.TOP);
		TimeRangeValidator validator = new TimeRangeValidator(marker);
		//
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				super.keyReleased(e);
				if(timeRange != null && isEnterPressed(e)) {
					validator.setTimeRange(timeRange);
					if(validate(validator, controlDecoration, text)) {
						updateRetentionTime(validator.getRetentionTime());
						fireUpdate(timeRange);
					}
				}
			}
		});
		//
		return text;
	}

	private Button createButtonModify(Composite parent, boolean increase) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText((increase ? "Increase" : "Decrease") + " the current time [min].");
		button.setImage(ApplicationImageFactory.getInstance().getImage(increase ? IApplicationImage.IMAGE_VALUE_INCREASE : IApplicationImage.IMAGE_VALUE_DECREASE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(timeRange != null) {
					updateRetentionTime(increase);
					fireUpdate(timeRange);
				}
			}
		});
		return button;
	}

	private boolean validate(IValidator<Object> validator, ControlDecoration controlDecoration, Text text) {

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

	private boolean isEnterPressed(KeyEvent e) {

		return (e.keyCode == SWT.LF || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR);
	}

	private void updateRetentionTime(boolean increase) {

		assert (marker != null);
		assert (timeRange != null);
		//
		int retentionTime = 0;
		switch(marker) {
			case START:
				retentionTime = adjustTime(timeRange.getStart(), increase);
				break;
			case MAXIMUM:
				retentionTime = adjustTime(timeRange.getMaximum(), increase);
				break;
			case STOP:
				retentionTime = adjustTime(timeRange.getStop(), increase);
				break;
		}
		//
		updateRetentionTime(retentionTime);
	}

	private void updateRetentionTime(int retentionTime) {

		assert (marker != null);
		assert (timeRange != null);
		assert (text != null);
		//
		switch(marker) {
			case START:
				timeRange.updateStart(retentionTime);
				break;
			case MAXIMUM:
				timeRange.updateMaximum(retentionTime);
				break;
			case STOP:
				timeRange.updateStop(retentionTime);
				break;
		}
		//
		text.setText(extractRetentionTime());
	}

	private void updateDescriptions() {

		assert (marker != null);
		assert (text != null);
		//
		String description = "";
		//
		switch(marker) {
			case START:
				description = TimeRangesLabelProvider.START;
				break;
			case MAXIMUM:
				description = TimeRangesLabelProvider.MAXIMUM;
				break;
			case STOP:
				description = TimeRangesLabelProvider.STOP;
				break;
		}
		//
		text.setToolTipText(description);
	}

	private String extractRetentionTime() {

		assert (marker != null);
		assert (timeRange != null);
		//
		String retentionTime = "";
		switch(marker) {
			case START:
				retentionTime = getRetentionTimeMinutes(timeRange.getStart());
				break;
			case MAXIMUM:
				retentionTime = getRetentionTimeMinutes(timeRange.getMaximum());
				break;
			case STOP:
				retentionTime = getRetentionTimeMinutes(timeRange.getStop());
				break;
		}
		return retentionTime;
	}

	private int adjustTime(int time, boolean increase) {

		if(increase) {
			return time + STEP_RETENTION_TIME;
		} else {
			int adjustedTime = time - STEP_RETENTION_TIME;
			return adjustedTime < 0 ? 0 : adjustedTime;
		}
	}

	private String getRetentionTimeMinutes(int milliseconds) {

		return decimalFormat.format(milliseconds / TimeRange.MINUTE_FACTOR);
	}

	private void fireUpdate(TimeRange timeRange) {

		if(updateListener != null) {
			updateListener.update(timeRange);
		}
	}
}