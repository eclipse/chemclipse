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

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.model.ranges.TimeRanges;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class TimeRangeUI extends Composite implements IExtendedPartUI {

	private static final String TOOLTIP_LOCK = "locking the time range.";
	private static final String IMAGE_LOCK = IApplicationImage.IMAGE_LOCK_UPDATE;
	//
	private AtomicReference<Button> buttonLockControl = new AtomicReference<>();
	private AtomicReference<TimeSpinner> timeSpinnerStartControl = new AtomicReference<>();
	private AtomicReference<TimeSpinner> timeSpinnerStopControl = new AtomicReference<>();
	//
	private TimeRanges timeRanges = null;
	private TimeRange timeRange = null;
	private ITimeRangeUpdateListener updateListener = null;

	public TimeRangeUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Override
	public void update() {

		if(isDisposed()) {
			super.update();
			timeSpinnerStartControl.get().update();
			timeSpinnerStopControl.get().update();
		}
	}

	public void setInput(TimeRanges timeRanges, TimeRange timeRange) {

		this.timeRanges = timeRanges;
		this.timeRange = timeRange;
		updateInput();
	}

	public void setUpdateListener(ITimeRangeUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		createButtonLock(this);
		timeSpinnerStartControl.set(createSpinner(this, TimeRange.Marker.START));
		timeSpinnerStopControl.set(createSpinner(this, TimeRange.Marker.STOP));
		//
		initialize();
	}

	private void initialize() {

		updateInput();
	}

	private void createButtonLock(Composite parent) {

		Button button = new Button(parent, SWT.TOGGLE);
		button.setText("");
		setButtonImage(button, IMAGE_LOCK, PREFIX_ENABLE, PREFIX_DISABLE, TOOLTIP_LOCK, false);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(timeRange != null) {
					updateButtonLock(TimeRangeSupport.toggleUpdateLock(timeRanges, timeRange));
					updateInput(timeRange);
				}
			}
		});
		//
		buttonLockControl.set(button);
	}

	private void updateButtonLock(boolean enabled) {

		setButtonImage(buttonLockControl.get(), IMAGE_LOCK, PREFIX_ENABLE, PREFIX_DISABLE, TOOLTIP_LOCK, enabled);
	}

	private TimeSpinner createSpinner(Composite parent, TimeRange.Marker marker) {

		TimeSpinner timeSpinner = new TimeSpinner(parent, SWT.NONE, marker);
		timeSpinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		timeSpinner.setUpdateListener(new ITimeRangeUpdateListener() {

			@Override
			public void update(TimeRange timeRange) {

				updateInput(timeRange);
			}
		});
		//
		return timeSpinner;
	}

	private void updateInput() {

		updateButtonLock(timeRange != null ? timeRange.isLocked() : false);
		timeSpinnerStartControl.get().setInput(timeRange);
		timeSpinnerStopControl.get().setInput(timeRange);
	}

	private void updateInput(TimeRange timeRange) {

		/*
		 * The time spinner acts independently.
		 * If one range is modified, all other ranges
		 * shall be updated.
		 */
		updateInput();
		fireUpdate(timeRange);
	}

	private void fireUpdate(TimeRange timeRange) {

		if(updateListener != null) {
			updateListener.update(timeRange);
		}
	}
}