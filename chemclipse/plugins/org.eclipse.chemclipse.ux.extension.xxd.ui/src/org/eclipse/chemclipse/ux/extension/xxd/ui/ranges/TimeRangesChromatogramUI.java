/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.ranges.TimeRanges;
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class TimeRangesChromatogramUI extends Composite {

	private TimeRangesUI timeRangesUI;
	private TimeRangesPeakChart timeRangesPeakChart;
	//
	private TimeRanges timeRanges = null;

	public TimeRangesChromatogramUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setTimeRanges(TimeRanges timeRanges) {

		this.timeRanges = timeRanges;
		this.timeRangesUI.setInput(timeRanges);
		timeRangesPeakChart.update(this.timeRangesUI, this.timeRanges);
	}

	public TimeRangesPeakChart getChromatogramChart() {

		return timeRangesPeakChart;
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		timeRangesUI = createTimeRangesUI(this);
		timeRangesPeakChart = createChromatogram(this);
	}

	private TimeRangesUI createTimeRangesUI(Composite parent) {

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
		return timeRangesUI;
	}

	private TimeRangesPeakChart createChromatogram(Composite parent) {

		TimeRangesPeakChart timeRangesPeakChart = new TimeRangesPeakChart(parent, SWT.NONE);
		timeRangesPeakChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		return timeRangesPeakChart;
	}
}