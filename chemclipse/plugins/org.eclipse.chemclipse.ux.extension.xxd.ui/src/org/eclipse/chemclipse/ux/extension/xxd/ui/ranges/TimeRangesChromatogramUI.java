/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
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

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.ranges.TimeRanges;
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class TimeRangesChromatogramUI extends Composite implements IExtendedPartUI {

	private AtomicReference<TimeRangesUI> timeRangesControl = new AtomicReference<>();
	private TimeRangesPeakChart timeRangesPeakChart;
	//
	private TimeRanges timeRanges = null;
	private IUpdateListener updateListener;

	public TimeRangesChromatogramUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public void setTimeRanges(TimeRanges timeRanges) {

		this.timeRanges = timeRanges;
		TimeRangesUI timeRangesUI = timeRangesControl.get();
		timeRangesUI.setInput(timeRanges);
		timeRangesPeakChart.update(timeRangesUI, this.timeRanges);
	}

	public AtomicReference<TimeRangesUI> getTimeRangesControl() {

		return timeRangesControl;
	}

	public TimeRangesPeakChart getChromatogramChart() {

		return timeRangesPeakChart;
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createTimeRangesUI(this);
		timeRangesPeakChart = createChromatogram(this);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(timeRangesControl, true);
	}

	private void createTimeRangesUI(Composite parent) {

		TimeRangesUI timeRangesUI = new TimeRangesUI(parent, SWT.NONE);
		timeRangesUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		timeRangesUI.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				if(timeRanges != null) {
					timeRangesPeakChart.updateTimeRangeMarker();
				}
			}
		});
		//
		timeRangesControl.set(timeRangesUI);
	}

	private TimeRangesPeakChart createChromatogram(Composite parent) {

		TimeRangesPeakChart timeRangesPeakChart = new TimeRangesPeakChart(parent, SWT.NONE);
		timeRangesPeakChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		timeRangesPeakChart.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				fireTimeRangeUpdate();
			}
		});
		//
		return timeRangesPeakChart;
	}

	private void fireTimeRangeUpdate() {

		if(updateListener != null) {
			updateListener.update();
		}
	}
}