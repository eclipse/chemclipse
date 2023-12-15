/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.classification.ui.provider;

import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.chemclipse.xxd.classification.model.ClassificationRule;
import org.eclipse.jface.viewers.Viewer;

public class ClassificationRuleTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof ClassificationRule rule1 && e2 instanceof ClassificationRule rule2) {
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = rule2.getSearchExpression().compareTo(rule1.getSearchExpression());
					break;
				case 1:
					sortOrder = rule2.getClassification().compareTo(rule1.getClassification());
					break;
				case 2:
					sortOrder = rule2.getReference().compareTo(rule1.getReference());
					break;
			}
			//
			if(getDirection() == ASCENDING) {
				sortOrder = -sortOrder;
			}
		}
		//
		return sortOrder;
	}
}
