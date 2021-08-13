/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
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
import org.eclipse.chemclipse.model.ranges.TimeRanges;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IMouseSupport;
import org.eclipse.swtchart.extensions.events.AbstractHandledEventProcessor;
import org.eclipse.swtchart.extensions.events.IHandledEventProcessor;

public class TimeRangeSelectionHandler extends AbstractHandledEventProcessor implements IHandledEventProcessor {

	private TimeRangesUI timeRangesUI;
	private TimeRanges timeRanges;

	public void update(TimeRangesUI timeRangesUI, TimeRanges timeRanges) {

		this.timeRangesUI = timeRangesUI;
		this.timeRanges = timeRanges;
	}

	@Override
	public int getEvent() {

		return IMouseSupport.EVENT_MOUSE_DOUBLE_CLICK;
	}

	@Override
	public int getButton() {

		return IMouseSupport.MOUSE_BUTTON_LEFT;
	}

	@Override
	public int getStateMask() {

		return SWT.MOD3;
	}

	@Override
	public void handleEvent(BaseChart baseChart, Event event) {

		if(timeRangesUI != null && timeRanges != null) {
			TimeRange timeRange = TimeRangeSelector.selectRange(baseChart, event, timeRanges);
			if(timeRange != null) {
				TimeRangeSelector.updateTimeRangeUI(timeRangesUI, timeRange, baseChart);
			}
		}
	}
}