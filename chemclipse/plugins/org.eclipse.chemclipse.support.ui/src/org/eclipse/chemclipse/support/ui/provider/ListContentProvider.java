/*******************************************************************************
 * Copyright (c) 2015, 2022 Lablicate GmbH.
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

public class ListContentProvider implements IStructuredContentProvider {

	public static ListContentProvider getInstance() {

		return new ListContentProvider();
	}

	@Override
	public Object[] getElements(Object inputElement) {

		if(inputElement instanceof List<?> list) {
			return list.toArray();
		} else if(inputElement instanceof Set<?> set) {
			return set.toArray();
		} else if(inputElement instanceof Collection<?> collection) {
			return collection.toArray();
		} else if(inputElement instanceof Map<?, ?> map) {
			return map.entrySet().toArray();
		} else if(inputElement instanceof Iterable<?> iterable) {
			List<Object> list = new ArrayList<>();
			iterable.forEach(list::add);
			return list.toArray();
		}
		return new Object[0];
	}
}