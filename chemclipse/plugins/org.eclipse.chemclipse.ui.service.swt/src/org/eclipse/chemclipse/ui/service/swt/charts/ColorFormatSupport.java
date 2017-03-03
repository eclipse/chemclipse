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
package org.eclipse.chemclipse.ui.service.swt.charts;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * Remove this class as soon as the charts are configurable completely via settings.
 */
public class ColorFormatSupport {

	public static Color COLOR_BLACK = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	public static Color COLOR_RED = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	//
	public static DecimalFormat decimalFormatVariable = new DecimalFormat(("0.0##"), new DecimalFormatSymbols(Locale.ENGLISH));
	public static DecimalFormat decimalFormatFixed = new DecimalFormat(("0.00"), new DecimalFormatSymbols(Locale.ENGLISH));
	public static DecimalFormat decimalFormatScientific = new DecimalFormat(("0.0#E0"), new DecimalFormatSymbols(Locale.ENGLISH));
	public static DecimalFormat decimalFormatInteger = new DecimalFormat(("0"), new DecimalFormatSymbols(Locale.ENGLISH));
}
