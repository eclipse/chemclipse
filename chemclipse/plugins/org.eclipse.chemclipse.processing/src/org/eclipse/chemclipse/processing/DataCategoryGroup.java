/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Matthias Mailänder - add MALDI support
 * Philip Wenig - generalized data categories
 *******************************************************************************/
package org.eclipse.chemclipse.processing;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class DataCategoryGroup {

	private final String name;
	private final Set<DataCategory> dataCategories;
	private static final DataCategoryGroup[] DEFAULT_GROUPS = new DataCategoryGroup[]{ //
			new DataCategoryGroup("Chromatography", DataCategory.chromatographyCategories()), //
			new DataCategoryGroup("Spectroscopy", DataCategory.spectroscopyCategories()), //
			new DataCategoryGroup("Spectrometry", DataCategory.spectrometryCategories())};

	public DataCategoryGroup(String name, DataCategory... dataCategories) {

		this(name, Arrays.asList(dataCategories));
	}

	public DataCategoryGroup(String name, Collection<DataCategory> dataCategories) {

		this.name = name;
		this.dataCategories = Collections.unmodifiableSet(new LinkedHashSet<>(dataCategories));
	}

	public String getName() {

		return name;
	}

	public Set<DataCategory> getDataCategories() {

		return dataCategories;
	}

	public static DataCategoryGroup[] defaultGroups() {

		return DEFAULT_GROUPS.clone();
	}
}