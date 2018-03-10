/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.wsd.model.core.identifier.chromatogram.IChromatogramTargetWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;

public abstract class AbstractChromatogramWSD extends AbstractChromatogram implements IChromatogramWSD {

	private static final long serialVersionUID = -7048942996283330150L;
	//
	private Set<IChromatogramTargetWSD> targets;

	public AbstractChromatogramWSD() {
		targets = new HashSet<IChromatogramTargetWSD>();
	}

	@Override
	public IScanWSD getSupplierScan(int scan) {

		int position = scan;
		if(position > 0 && position <= getScans().size()) {
			IScan storedScan = getScans().get(--position);
			if(storedScan instanceof IScanWSD) {
				return (IScanWSD)storedScan;
			}
		}
		return null;
	}

	@Override
	public void fireUpdate(IChromatogramSelection chromatogramSelection) {

		/*
		 * Fire an update to inform all listeners.
		 */
		if(chromatogramSelection instanceof ChromatogramSelectionWSD) {
			((ChromatogramSelectionWSD)chromatogramSelection).update(true);
		}
	}

	@Override
	public void addTarget(IChromatogramTargetWSD chromatogramTarget) {

		if(chromatogramTarget != null) {
			targets.add(chromatogramTarget);
		}
	}

	@Override
	public void removeTarget(IChromatogramTargetWSD chromatogramTarget) {

		targets.remove(chromatogramTarget);
	}

	@Override
	public void removeTargets(List<IChromatogramTargetWSD> targetsToRemove) {

		targets.removeAll(targetsToRemove);
	}

	@Override
	public void removeAllTargets() {

		targets.clear();
	}

	@Override
	public List<IChromatogramTargetWSD> getTargets() {

		List<IChromatogramTargetWSD> targetList = new ArrayList<IChromatogramTargetWSD>(targets);
		return targetList;
	}
}
