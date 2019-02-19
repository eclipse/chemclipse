/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IDataExtraction {

	Samples process(IProgressMonitor monitor);

	default void setRetentionTimeDescription(Samples samples) {

		for(int i = 0; i < samples.getVariables().size(); i++) {
			final int j = i;
			final Set<String> peakNames = new HashSet<>();
			samples.getSampleList().stream().map(s -> s.getSampleData()).map(d -> d.get(j).getPeak()).forEach(peak -> {
				if(peak.isPresent()) {
					List<IIdentificationTarget> targets = new ArrayList<>(peak.get().getTargets());
					if(targets.size() > 0) {
						peakNames.add(targets.get(0).getLibraryInformation().getName());
					}
				}
			});
			if(!peakNames.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				Iterator<String> it = peakNames.iterator();
				while(it.hasNext()) {
					sb.append(it.next());
					if(it.hasNext()) {
						sb.append(", ");
					}
				}
				samples.getVariables().get(i).setDescription(sb.toString());
			}
		}
	}
}