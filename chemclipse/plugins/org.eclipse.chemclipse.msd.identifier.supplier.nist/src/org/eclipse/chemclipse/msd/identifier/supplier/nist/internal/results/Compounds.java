/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results;

import java.util.ArrayList;
import java.util.List;

public class Compounds implements ICompounds {

	private List<ICompound> compounds;

	public Compounds() {
		compounds = new ArrayList<ICompound>();
	}

	@Override
	public void add(ICompound compound) {

		compounds.add(compound);
	}

	@Override
	public void remove(ICompound compound) {

		compounds.remove(compound);
	}

	@Override
	public List<ICompound> getCompounds() {

		return compounds;
	}

	@Override
	public ICompound getCompound(int index) {

		/*
		 * The user index starts with 1.
		 */
		index--;
		if(index >= 0 && index < compounds.size()) {
			return compounds.get(index);
		} else {
			return null;
		}
	}

	@Override
	public int size() {

		return compounds.size();
	}
}
