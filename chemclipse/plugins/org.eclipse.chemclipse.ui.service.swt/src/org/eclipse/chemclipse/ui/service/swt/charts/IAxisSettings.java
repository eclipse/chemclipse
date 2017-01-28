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

import org.eclipse.swt.graphics.Color;

public interface IAxisSettings {

	String getTitle();

	void setTitle(String title);

	DecimalFormat getDecimalFormat();

	void setDecimalFormat(DecimalFormat decimalFormat);

	boolean isEnableLogScale();

	void setEnableLogScale(boolean enableLogScale);

	boolean isEnableCategory();

	Color getColor();

	void setColor(Color color);

	boolean isVisible();

	void setVisible(boolean visible);
}
