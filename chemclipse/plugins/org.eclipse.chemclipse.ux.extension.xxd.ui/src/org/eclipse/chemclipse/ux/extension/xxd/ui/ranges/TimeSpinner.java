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

import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class TimeSpinner extends Composite {

	private static final String LABEL_RETENTION_TIME_MINUTES = "Retention Time (Minutes)";
	private static final int STEP_RETENTION_TIME = 1000; // milliseconds
	//
	private Text text;
	private TimeRange.Marker marker;
	private TimeRange timeRange = null;
	private IUpdateListener updateListener = null;
	//
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");

	public TimeSpinner(Composite parent, int style, TimeRange.Marker marker) {
		super(parent, style);
		this.marker = marker;
		createControl();
		updateTooltip();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public void update(TimeRange timeRange) {

		this.timeRange = timeRange;
		if(timeRange != null) {
			text.setEnabled(true);
			text.setText(extractRetentionTime());
		} else {
			text.setEnabled(false);
			text.setText("");
		}
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		text = createText(this);
		createSpinner(this, true, 5);
		createSpinner(this, false, -5);
	}

	private Text createText(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.verticalSpan = 2;
		text.setLayoutData(gridData);
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

	private void createSpinner(Composite parent, boolean increase, int verticalIndent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData();
		gridData.widthHint = 14;
		gridData.heightHint = 9;
		gridData.verticalIndent = verticalIndent;
		composite.setLayoutData(gridData);
		//
		composite.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent event) {

				Rectangle bounds = composite.getBounds();
				int width = bounds.width;
				int height = bounds.height;
				int border = 1;
				//
				GC gc = event.gc;
				gc.setForeground(Colors.GRAY);
				gc.drawRoundRectangle(border, border, width - 2 * border, height - 2 * border, 2, 2);
				//
				gc.setBackground(Colors.DARK_GRAY);
				if(increase) {
					gc.fillPolygon(new int[]{width / 2, border, width - border, height - border, border, height - border});
				} else {
					gc.fillPolygon(new int[]{border, border, width - border, border, width / 2, height - border});
				}
			}
		});
		//
		composite.addMouseTrackListener(new MouseTrackAdapter() {

			@Override
			public void mouseEnter(MouseEvent e) {

				composite.setBackground(Colors.GRAY);
			}

			@Override
			public void mouseExit(MouseEvent e) {

				composite.setBackground(null);
			}
		});
		//
		composite.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				if(timeRange != null) {
					updateRetentionTime(increase);
					fireUpdate();
				}
			}
		});
		//
		composite.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {

				if(timeRange != null && isEnterPressed(e)) {
					updateRetentionTime(increase);
					fireUpdate();
				}
			}
		});
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

	private void updateTooltip() {

		assert (marker != null);
		assert (text != null);
		//
		switch(marker) {
			case START:
				text.setToolTipText("Start " + LABEL_RETENTION_TIME_MINUTES);
				break;
			case CENTER:
				text.setToolTipText("Center " + LABEL_RETENTION_TIME_MINUTES);
				break;
			case STOP:
				text.setToolTipText("Stop " + LABEL_RETENTION_TIME_MINUTES);
				break;
		}
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
}
