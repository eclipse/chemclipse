/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ReportRowModel implements IReportRowModel {

	private List<List<String>> rows;

	public ReportRowModel() {
		rows = new ArrayList<List<String>>();
	}

	@Override
	public int getColumnIndex(String columnName) {

		if(rows.size() > 0) {
			List<String> header = rows.get(0);
			for(int index = 0; index < header.size(); index++) {
				if(header.get(index).equals(columnName)) {
					return index;
				}
			}
		}
		return -1;
	}

	@Override
	public boolean add(List<String> e) {

		return rows.add(e);
	}

	@Override
	public void add(int index, List<String> element) {

		rows.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends List<String>> c) {

		return rows.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends List<String>> c) {

		return rows.addAll(index, c);
	}

	@Override
	public void clear() {

		rows.clear();
	}

	@Override
	public boolean contains(Object o) {

		return rows.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {

		return rows.containsAll(c);
	}

	@Override
	public List<String> get(int index) {

		return rows.get(index);
	}

	@Override
	public int indexOf(Object o) {

		return rows.indexOf(o);
	}

	@Override
	public boolean isEmpty() {

		return rows.isEmpty();
	}

	@Override
	public Iterator<List<String>> iterator() {

		return rows.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {

		return rows.lastIndexOf(o);
	}

	@Override
	public ListIterator<List<String>> listIterator() {

		return rows.listIterator();
	}

	@Override
	public ListIterator<List<String>> listIterator(int index) {

		return rows.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {

		return rows.remove(o);
	}

	@Override
	public List<String> remove(int index) {

		return rows.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {

		return rows.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {

		return rows.retainAll(c);
	}

	@Override
	public List<String> set(int index, List<String> element) {

		return rows.set(index, element);
	}

	@Override
	public int size() {

		return rows.size();
	}

	@Override
	public List<List<String>> subList(int fromIndex, int toIndex) {

		return rows.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {

		return rows.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {

		return rows.toArray(a);
	}
}
