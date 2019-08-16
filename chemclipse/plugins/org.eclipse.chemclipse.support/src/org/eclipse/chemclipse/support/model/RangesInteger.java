/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class RangesInteger implements Iterable<Integer> {

	private List<int[]> ranges;
	private int step;

	public RangesInteger() {
		ranges = new LinkedList<>();
		step = 1;
	}

	public void addNumber(int n) {

		ranges.add(new int[]{n, n});
	}

	public void addRange(int r1, int r2) {

		int start = Math.min(r1, r2);
		int end = Math.max(r1, r2);
		ranges.add(new int[]{start, end});
	}

	@Override
	public Iterator<Integer> iterator() {

		return getSet().iterator();
	}

	public int[] getValues() {

		SortedSet<Integer> set = getSet();
		int[] array = new int[set.size()];
		int i = 0;
		Iterator<Integer> it = set.iterator();
		while(it.hasNext()) {
			array[i] = it.next();
			i++;
		}
		return array;
	}

	private SortedSet<Integer> getSet() {

		SortedSet<Integer> s = new TreeSet<>();
		for(int[] r : ranges) {
			for(int i = r[0]; i <= r[1]; i += step) {
				s.add(i);
			}
		}
		return s;
	}
}
