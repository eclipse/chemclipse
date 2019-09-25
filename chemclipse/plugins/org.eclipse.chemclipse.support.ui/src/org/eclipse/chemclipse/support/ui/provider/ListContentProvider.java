/*******************************************************************************
 * Copyright (c) 2015, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - iterable support
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ListContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {

		if(inputElement instanceof List) {
			return ((List<?>)inputElement).toArray();
		} else if(inputElement instanceof Set) {
			return ((Set<?>)inputElement).toArray();
		} else if(inputElement instanceof Collection) {
			return ((Collection<?>)inputElement).toArray();
		} else if(inputElement instanceof Map) {
			return ((Map<?, ?>)inputElement).entrySet().toArray();
		} else if(inputElement instanceof Iterable<?>) {
			Iterable<?> iterable = (Iterable<?>)inputElement;
			List<Object> list = new ArrayList<>();
			iterable.forEach(list::add);
			return list.toArray();
		}
		return new Object[0];
	}

	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}
}
