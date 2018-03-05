/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.eclipse.eavp.service.swtchart.core.BaseChart;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.core.IPrimaryAxisSettings;
import org.eclipse.eavp.service.swtchart.core.RangeRestriction;
import org.eclipse.eavp.service.swtchart.events.IHandledEventProcessor;
import org.eclipse.eavp.service.swtchart.linecharts.LineChart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.swtchart.IAxis.Position;
import org.swtchart.LineStyle;

public class OverviewChartUI extends LineChart {

	private Display display = Display.getDefault();

	public OverviewChartUI(Composite parent, int style) {
		super(parent, style);
		setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		try {
			initialize();
		} catch(Exception e) {
			System.out.println(e);
		}
	}

	private void initialize() throws Exception {

		IChartSettings chartSettings = getChartSettings();
		chartSettings.setOrientation(SWT.HORIZONTAL);
		chartSettings.setHorizontalSliderVisible(true);
		chartSettings.setVerticalSliderVisible(false);
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setRestrictZoom(false);
		rangeRestriction.setZeroX(true);
		rangeRestriction.setZeroY(false);
		chartSettings.addHandledEventProcessor(new IHandledEventProcessor() {

			@Override
			public void handleEvent(BaseChart baseChart, Event event) {

				/*
				 * Reset the range.
				 */
				baseChart.adjustRange(true);
				baseChart.redraw();
			}

			@Override
			public int getStateMask() {

				return SWT.NONE;
			}

			@Override
			public int getEvent() {

				return BaseChart.EVENT_MOUSE_DOUBLE_CLICK;
			}

			@Override
			public int getButton() {

				return BaseChart.BUTTON_LEFT;
			}
		});
		//
		setPrimaryAxisSet(chartSettings);
		applySettings(chartSettings);
	}

	private void setPrimaryAxisSet(IChartSettings chartSettings) {

		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle("Retention Time (milliseconds)");
		primaryAxisSettingsX.setDecimalFormat(new DecimalFormat(("0.0##"), new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsX.setColor(display.getSystemColor(SWT.COLOR_BLACK));
		primaryAxisSettingsX.setPosition(Position.Primary);
		primaryAxisSettingsX.setVisible(false);
		primaryAxisSettingsX.setGridLineStyle(LineStyle.NONE);
		//
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle("Intensity");
		primaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.0#E0"), new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsY.setColor(display.getSystemColor(SWT.COLOR_BLACK));
		primaryAxisSettingsY.setPosition(Position.Primary);
		primaryAxisSettingsY.setVisible(false);
		primaryAxisSettingsY.setGridLineStyle(LineStyle.NONE);
	}
}
