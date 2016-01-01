/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.series;

public interface ISeriesSetter {

	/**
	 * Sets the view series. May be overridden by subclasses to show specific
	 * values.<br/>
	 * E.g. draw a chromatogram, a peak, a baseline, mass spectra and a lot more
	 * options.<br/>
	 * Overwrite this method by subclasses to use other types of
	 * SeriesConverter.
	 */
	void setViewSeries();
}
