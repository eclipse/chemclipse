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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;

public interface ISamples {

	default void createGroups() {

		List<ISample> samples = getSampleList();
		Set<String> groupNames = PcaUtils.getGroupNames(getSampleList(), true);
		List<IGroup> groups = getGroupList();
		groups.clear();
		groupNames.forEach(groupName -> {
			if(groupName != null) {
				List<ISample> samplesSomeGroupName = samples.stream().filter(s -> groupName.equals(s.getGroupName()) && s.isSelected()).collect(Collectors.toList());
				IGroup group = new Group(samplesSomeGroupName);
				group.setGroupName(groupName);
				groups.add(group);
			}
		});
	}

	List<IRetentionTime> getExtractedRetentionTimes();

	List<IGroup> getGroupList();

	List<ISample> getSampleList();
}
