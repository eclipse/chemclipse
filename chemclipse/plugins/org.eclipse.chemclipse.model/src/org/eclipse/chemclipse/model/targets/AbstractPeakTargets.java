/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.targets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractPeakTargets implements IPeakTargets {

	final private Set<IPeakTarget> targets;

	public AbstractPeakTargets() {
		targets = new HashSet<IPeakTarget>();
	}

	@Override
	public void addTarget(IPeakTarget peakTarget) {

		if(peakTarget != null) {
			targets.add(peakTarget);
		}
	}

	@Override
	public void removeTarget(IPeakTarget peakTarget) {

		targets.remove(peakTarget);
	}

	@Override
	public void removeTargets(List<IPeakTarget> targetsToDelete) {

		targets.removeAll(targetsToDelete);
	}

	@Override
	public void removeAllTargets() {

		targets.clear();
	}

	@Override
	public List<IPeakTarget> getTargets() {

		List<IPeakTarget> targetList = new ArrayList<IPeakTarget>(targets);
		return targetList;
	}
}
