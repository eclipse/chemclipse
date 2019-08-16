/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model;

import java.util.Set;

public interface IWncIons {

	/**
	 * This method may return null.
	 * 
	 * @param ion
	 * @return IWNCIon
	 */
	IWncIon getWNCIon(int ion);

	void add(IWncIon wncIon);

	void add(IWncIons wncIons);

	void remove(IWncIon wncIon);

	void remove(Integer ion);

	Object[] toArray();

	Set<Integer> getKeys();
}
