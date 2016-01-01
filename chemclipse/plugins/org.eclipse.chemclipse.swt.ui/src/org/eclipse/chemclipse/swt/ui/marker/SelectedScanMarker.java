/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.marker;

import org.eclipse.swt.events.PaintEvent;

/**
 * @author Philip (eselmeister) Wenig
 * 
 */
public class SelectedScanMarker extends AbstractMarker {

	private IGCPoint point;

	/**
	 * Set the point where the selected scan is.
	 * 
	 * @param point
	 */
	public void setPoint(IGCPoint point) {

		this.point = point;
	}

	@Override
	public void paintControl(PaintEvent e) {

		e.gc.setForeground(getForegroundColor());
		e.gc.setBackground(getBackgroundColor());
		if(point != null) {
			e.gc.drawText(getMarker(), point.getX(), point.getY());
		}
	}

	@Override
	public boolean drawBehindSeries() {

		return true;
	}
}
