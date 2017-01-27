/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ui.service.swt.internal.charts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.swtchart.Chart;

public abstract class AbstractHandledChart extends Chart implements IEventHandler {

	public AbstractHandledChart(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	public void handleEvent(Event event) {

		super.handleEvent(event);
		switch(event.type) {
			case SWT.KeyDown:
				handleKeyDownEvent(event);
				break;
			case SWT.KeyUp:
				handleKeyUpEvent(event);
				break;
			case SWT.MouseMove:
				handleMouseMoveEvent(event);
				break;
			case SWT.MouseDown:
				handleMouseDownEvent(event);
				break;
			case SWT.MouseUp:
				handleMouseUpEvent(event);
				break;
			case SWT.MouseWheel:
				handleMouseWheel(event);
				break;
			case SWT.MouseDoubleClick:
				handleMouseDoubleClick(event);
				break;
			case SWT.Selection:
				handleSelectionEvent(event);
				break;
		}
	}

	@Override
	public void paintControl(PaintEvent e) {

	}

	@Override
	public void handleMouseMoveEvent(Event event) {

	}

	@Override
	public void handleMouseDownEvent(Event event) {

	}

	@Override
	public void handleMouseUpEvent(Event event) {

	}

	@Override
	public void handleMouseWheel(Event event) {

	}

	@Override
	public void handleMouseDoubleClick(Event event) {

	}

	@Override
	public void handleKeyDownEvent(Event event) {

	}

	@Override
	public void handleKeyUpEvent(Event event) {

	}

	@Override
	public void handleSelectionEvent(Event event) {

	}
}
