/*******************************************************************************
 * Copyright (c) 2022, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.menu.IChartMenuEntry;

public class DynamicHandler extends AbstractHandler {

	private IChartMenuEntry cachedEntry;
	private ScrollableChart chart;

	public DynamicHandler(IChartMenuEntry cachedEntry, ScrollableChart chart) {

		this.cachedEntry = cachedEntry;
		this.chart = chart;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		cachedEntry.execute(chart.getShell(), chart);
		return null;
	}

	@Override
	public boolean isEnabled() {

		return cachedEntry.isEnabled(chart);
	}
}
