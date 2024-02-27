/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
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

import java.util.Arrays;
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
		//
		return id;
	}

	public static DataCategory[] getDataTypes(IProcessMethod processMethod) {

		Set<DataCategory> categories = processMethod.getDataCategories();
		if(categories.isEmpty()) {
			/*
			 * Backward Compatibility
			 * CSD, MSD, WSD, ISD
			 */
			categories = Set.of(DataCategory.chromatographyCategories());
		}
		/*
		 * When there are no entries, return the categories of the method
		 * even though this will be a no operation when executed.
		 */
		if(processMethod.getNumberOfEntries() == 0) {
			DataCategory[] dataCategories = categories.toArray(new DataCategory[categories.size()]);
			Arrays.sort(dataCategories, (c1, c2) -> c1.name().compareTo(c2.name()));
			return dataCategories;
		}
		/*
		 * A process method could contain process entries for various scopes (single category/multiple categories):
		 * ---
		 * CSD (only CSD)
		 * MSD (only MSD)
		 * CSD, MSD
		 * CSD, MSD, WSD
		 */
		Set<DataCategory> categorySet = new HashSet<>();
		for(IProcessEntry processEntry : processMethod) {
			Set<DataCategory> dataCategories = processEntry.getDataCategories();
			if(dataCategories.size() == 1) {
				return new DataCategory[]{dataCategories.iterator().next()};
			} else {
				for(DataCategory dataCategory : dataCategories) {
					if(categories.contains(dataCategory)) {
						categorySet.add(dataCategory);
					}
				}
			}
		}
		//
		DataCategory[] dataCategories = categorySet.toArray(new DataCategory[categorySet.size()]);
		Arrays.sort(dataCategories, (c1, c2) -> c1.name().compareTo(c2.name()));
		return dataCategories;
	}
}