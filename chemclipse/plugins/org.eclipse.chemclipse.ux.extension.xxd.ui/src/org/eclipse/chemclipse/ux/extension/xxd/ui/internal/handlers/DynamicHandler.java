/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.handlers;

import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramChart;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.swtchart.extensions.menu.IChartMenuEntry;

public class DynamicHandler implements IHandler {

	private IChartMenuEntry cachedEntry;
	private ChromatogramChart chromatogramChart;

	public DynamicHandler(IChartMenuEntry cachedEntry, ChromatogramChart chromatogramChart) {

		this.cachedEntry = cachedEntry;
		this.chromatogramChart = chromatogramChart;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		cachedEntry.execute(chromatogramChart.getShell(), chromatogramChart);
		return null;
	}

	@Override
	public boolean isEnabled() {

		return cachedEntry.isEnabled(chromatogramChart);
	}

	@Override
	public boolean isHandled() {

		return true;
	}

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {

	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {

	}

	@Override
	public void dispose() {

	}
}
