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

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class WncIons implements IWncIons {

	private Map<Integer, IWncIon> ions;

	public WncIons() {
		ions = new TreeMap<Integer, IWncIon>();
	}

	@Override
	public void add(IWncIon wncIon) {

		if(wncIon != null) {
			ions.put(wncIon.getIon(), wncIon);
		}
	}

	@Override
	public void remove(IWncIon wncIon) {

		if(wncIon != null) {
			remove(wncIon.getIon());
		}
	}

	@Override
	public void remove(Integer ion) {

		ions.remove(ion);
	}

	@Override
	public IWncIon getWNCIon(int ion) {

		return ions.get(ion);
	}

	public Object[] toArray() {

		return ions.values().toArray();
	}

	@Override
	public void add(IWncIons wncIons) {

		Set<Integer> keys = wncIons.getKeys();
		for(Integer key : keys) {
			ions.put(key, wncIons.getWNCIon(key));
		}
	}

	@Override
	public Set<Integer> getKeys() {

		return ions.keySet();
	}
}
