/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
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

public interface ITimeRangePeakListener {

	/**
	 * The start/stop coordinates are fired as soon as the user has
	 * activated the peak detection via CTRL + mouse button selection.
	 * 
	 * @param xStart
	 * @param yStart
	 * @param xStop
	 * @param yStop
	 */
	void update(int xStart, int yStart, int xStop, int yStop);
}