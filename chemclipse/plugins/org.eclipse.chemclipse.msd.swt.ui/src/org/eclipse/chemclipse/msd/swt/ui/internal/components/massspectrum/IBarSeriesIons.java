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
package org.eclipse.chemclipse.msd.swt.ui.internal.components.massspectrum;

import java.util.List;

/**
 * @author Philip (eselmeister) Wenig
 * 
 */
public interface IBarSeriesIons {

	void add(IBarSeriesIon barSeriesIon);

	void clear();

	List<IBarSeriesIon> getIonsWithHighestAbundance(int amount);

	List<IBarSeriesIon> getIonsByModulo(int amount);

	IBarSeriesIon getBarSeriesIon(int index);

	int size();
}
