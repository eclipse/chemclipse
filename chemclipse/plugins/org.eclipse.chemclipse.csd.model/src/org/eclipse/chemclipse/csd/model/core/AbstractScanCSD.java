/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.csd.model.core.identifier.scan.IScanTargetCSD;
import org.eclipse.chemclipse.model.core.AbstractScan;

public abstract class AbstractScanCSD extends AbstractScan implements IScanCSD {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -864046180650106625L;
	private Set<IScanTargetCSD> targets;

	public AbstractScanCSD() {
		targets = new HashSet<IScanTargetCSD>();
	}

	@Override
	public void addTarget(IScanTargetCSD scanTarget) {

		if(scanTarget != null) {
			targets.add(scanTarget);
		}
	}

	@Override
	public void removeTarget(IScanTargetCSD scanTarget) {

		targets.remove(scanTarget);
	}

	@Override
	public void removeTargets(List<IScanTargetCSD> targetsToRemove) {

		targets.removeAll(targetsToRemove);
	}

	@Override
	public List<IScanTargetCSD> getTargets() {

		List<IScanTargetCSD> targetList = new ArrayList<IScanTargetCSD>(targets);
		return targetList;
	}
}
