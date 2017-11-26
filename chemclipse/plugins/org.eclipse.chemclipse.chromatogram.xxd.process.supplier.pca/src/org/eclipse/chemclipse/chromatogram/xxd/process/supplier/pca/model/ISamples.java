/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;

public interface ISamples<V extends IVariable, S extends ISample<? extends ISampleData>> {

	default void createGroups() {

		List<S> samples = getSampleList();
		Set<String> groupNames = PcaUtils.getGroupNames(getSampleList(), true);
		List<IGroup<S>> groups = new ArrayList<>();
		groupNames.forEach(groupName -> {
			if(groupName != null) {
				List<S> samplesSomeGroupName = samples.stream().filter(s -> groupName.equals(s.getGroupName()) && s.isSelected()).collect(Collectors.toList());
				Group<S> group = new Group<S>(samplesSomeGroupName);
				group.setGroupName(groupName);
				groups.add(group);
			}
		});
	}

	List<IGroup<S>> getGroupList();

	List<S> getSampleList();

	List<V> getVariables();
}
