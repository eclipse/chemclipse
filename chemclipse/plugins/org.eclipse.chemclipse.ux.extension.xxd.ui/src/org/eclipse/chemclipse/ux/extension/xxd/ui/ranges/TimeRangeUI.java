/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
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
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class TimeRangeUI extends Composite {

	private TimeSpinner timeSpinnerStart;
	private TimeSpinner timeSpinnerCenter;
	private TimeSpinner timeSpinnerStop;
	//
	private TimeRange timeRange;
	//
	private IUpdateListener updateListener = null;

	public TimeRangeUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Override
	public void update() {

		super.update();
		timeSpinnerStart.update();
		timeSpinnerCenter.update();
		timeSpinnerStop.update();
	}

	public void update(TimeRange timeRange) {

		this.timeRange = timeRange;
		updateTimeRange();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

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
	}

	private TimeSpinner createSpinner(Composite parent, TimeRange.Marker marker) {

		TimeSpinner timeSpinner = new TimeSpinner(parent, SWT.NONE, marker);
		timeSpinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		timeSpinner.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				fireUpdate();
			}
		});
		return timeSpinner;
	}

	private void updateTimeRange() {

		timeSpinnerStart.update(timeRange);
		timeSpinnerCenter.update(timeRange);
		timeSpinnerStop.update(timeRange);
	}

	private void fireUpdate() {

		if(updateListener != null) {
			/*
			 * The time spinner act independently.
			 * If one range is modified, all other ranges
			 * shall be updated.
			 */
			updateTimeRange();
			/*
			 * Inform all other listeners.
			 */
			getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {

					updateListener.update();
				}
			});
		}
	}
}
