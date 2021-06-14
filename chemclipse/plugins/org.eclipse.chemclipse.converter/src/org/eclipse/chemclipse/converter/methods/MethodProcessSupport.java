/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - add profile support
 *******************************************************************************/
package org.eclipse.chemclipse.converter.methods;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;

public class MethodProcessSupport {

	public static String getID(IProcessMethod method, String qualifier) {

		String id = "ProcessMethod." + method.getUUID();
		if(qualifier != null) {
			return id + ":" + qualifier;
		}
		return id;
	}

	public static DataCategory[] getDataTypes(IProcessMethod method) {

		Set<DataCategory> categories = method.getDataCategories();
		if(categories.isEmpty()) {
			/*
			 * backward compatibility
			 */
			categories = EnumSet.of(DataCategory.CSD, DataCategory.MSD, DataCategory.WSD);
		}
		if(method.getNumberOfEntries() == 0) {
			/*
			 * When there are no entries, return the categories of the method even though this will be a noop when executed.
			 */
			return categories.toArray(new DataCategory[0]);
		}
		/*
		 * now we search if maybe only entries of a given type are in this method, e.g. it is possible to create a processmethod
		 * with MSD+WSD type, but only add processors that are valid for WSD, then we want to return only WSD...
		 */
		Set<DataCategory> entryCategories = new HashSet<>();
		for(IProcessEntry entry : method) {
			for(DataCategory entryDataCategory : entry.getDataCategories()) {
				if(categories.contains(entryDataCategory)) {
					entryCategories.add(entryDataCategory);
				}
			}
		}
		//
		return categories.toArray(new DataCategory[0]);
	}
}