/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TimeRangesLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class TimeSpinner extends Composite {

	private static final int STEP_RETENTION_TIME = 1000; // milliseconds
	//
	private Label label;
	private Label spacer;
	private Text text;
	private Button buttonDecrease;
	private Button buttonIncrease;
	//
	private TimeRange.Marker marker;
	private TimeRange timeRange = null;
	private IUpdateListener updateListener = null;
	//
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public TimeSpinner(Composite parent, int style, TimeRange.Marker marker) {

		super(parent, style);
		this.marker = marker;
		createControl();
		/*
		 * Update the descriptions based on the selected marker.
		 */
		updateDescriptions();
	}

	@Override
	public void update() {

		super.update();
		updateLabel();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public void update(TimeRange timeRange) {

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
		label = createInfo(this);
		spacer = createSpacer(this);
		//
		text = createText(this);
		buttonDecrease = createButtonModify(this, false);
		buttonIncrease = createButtonModify(this, true);
		//
		updateLabel();
	}

	private Label createInfo(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(""); // Will be set dynamically.
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.CENTER;
		label.setLayoutData(gridData);
		return label;
	}

	private Label createSpacer(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("");
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		label.setLayoutData(gridData);
		return label;
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
						fireUpdate();
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
					fireUpdate();
				}
			}
		});
		return button;
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
			case CENTER:
				retentionTime = adjustTime(timeRange.getCenter(), increase);
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
			case CENTER:
				timeRange.updateCenter(retentionTime);
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
			case CENTER:
				description = TimeRangesLabelProvider.CENTER;
				break;
			case STOP:
				description = TimeRangesLabelProvider.STOP;
				break;
		}
		//
		label.setText(description);
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
			case CENTER:
				retentionTime = getRetentionTimeMinutes(timeRange.getCenter());
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

	private void updateLabel() {

		boolean visible = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_TIME_RANGE_SPINNER_LABEL);
		PartSupport.setControlVisibility(label, visible);
		PartSupport.setControlVisibility(spacer, visible);
	}
}
