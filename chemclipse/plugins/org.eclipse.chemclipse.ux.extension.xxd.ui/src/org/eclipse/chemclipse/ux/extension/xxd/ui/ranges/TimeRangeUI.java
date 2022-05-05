/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class TimeRangeUI extends Composite {

	private TimeSpinner timeSpinnerStart;
	private TimeSpinner timeSpinnerCenter;
	private TimeSpinner timeSpinnerStop;
	//
	private TimeRange timeRange = null;
	//
	private ITimeRangeUpdateListener updateListener = null;

	public TimeRangeUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Override
	public void update() {

		if(isDisposed()) {
			super.update();
			timeSpinnerStart.update();
			timeSpinnerCenter.update();
			timeSpinnerStop.update();
		}
	}

	public void setInput(TimeRange timeRange) {

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
		timeSpinnerStart = createSpinner(this, TimeRange.Marker.START);
		timeSpinnerCenter = createSpinner(this, TimeRange.Marker.CENTER);
		timeSpinnerStop = createSpinner(this, TimeRange.Marker.STOP);
		//
		initialize();
	}

	private void initialize() {

		updateInput();
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

		timeSpinnerStart.setInput(timeRange);
		timeSpinnerCenter.setInput(timeRange);
		timeSpinnerStop.setInput(timeRange);
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