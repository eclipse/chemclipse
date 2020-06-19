/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Target extends AbstractVariable implements ITarget {

	private String target = "";

	public static List<Target> create(Set<String> targets) {

		List<Target> targetList = new ArrayList<>();
		for(String target : targets) {
			targetList.add(new Target(target));
		}
		return targetList;
	}

	public Target(String target) {

		super();
		setTarget(target);
		setValue(target);
		setType(ITarget.TYPE);
		setSelected(true);
	}

	public Target(String target, String description) {

		this(target);
		setDescription(description);
	}

	@Override
	public int compareTo(IVariable o) {

		if(o instanceof ITarget) {
			ITarget target = (ITarget)o;
			return this.target.compareTo(target.getTarget());
		}
		return 0;
	}

	@Override
	public String getTarget() {

		return target;
	}

	@Override
	public void setTarget(String target) {

		this.target = target;
	}
}
