/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.support;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AbstractMarkedWavelengths implements IMarkedWavelengths {

	private Set<IMarkedWavelength> markedWavelengths;

	public AbstractMarkedWavelengths() {
		markedWavelengths = new HashSet<IMarkedWavelength>();
	}

	@Override
	public boolean add(IMarkedWavelength e) {

		return markedWavelengths.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends IMarkedWavelength> c) {

		return markedWavelengths.addAll(c);
	}

	@Override
	public void clear() {

		markedWavelengths.clear();
	}

	@Override
	public boolean contains(Object o) {

		return markedWavelengths.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {

		return markedWavelengths.containsAll(c);
	}

	@Override
	public boolean isEmpty() {

		return markedWavelengths.isEmpty();
	}

	@Override
	public Iterator<IMarkedWavelength> iterator() {

		return markedWavelengths.iterator();
	}

	@Override
	public boolean remove(Object o) {

		return markedWavelengths.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {

		return markedWavelengths.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {

		return markedWavelengths.retainAll(c);
	}

	@Override
	public int size() {

		return markedWavelengths.size();
	}

	@Override
	public Object[] toArray() {

		return markedWavelengths.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {

		return markedWavelengths.toArray(a);
	}

	@Override
	public Set<Double> getWavelengths() {

		Set<Double> wavelengths = new HashSet<Double>();
		for(IMarkedWavelength markedWavelength : markedWavelengths) {
			wavelengths.add(markedWavelength.getWavelength());
		}
		return wavelengths;
	}

	@Deprecated
	@Override
	public void add(int ionStart, int ionStop) {

		if(ionStart > ionStop) {
			int tmp = ionStart;
			ionStart = ionStop;
			ionStop = tmp;
		}
		for(int i = ionStart; i <= ionStop; i++) {
			markedWavelengths.add(new MarkedWavelength(i));
		}
	}

	@Override
	public void add(double... wavelenghts) {

		for(double wavelenght : wavelenghts) {
			markedWavelengths.add(new MarkedWavelength(wavelenght));
		}
	}

	@Override
	public void add(Collection<Double> wavelenghts) {

		for(double wavelenght : wavelenghts) {
			markedWavelengths.add(new MarkedWavelength(wavelenght));
		}
	}
}
