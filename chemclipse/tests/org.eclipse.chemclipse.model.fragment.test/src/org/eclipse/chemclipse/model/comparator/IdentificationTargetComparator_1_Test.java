/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.comparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.support.comparator.SortOrder;

import junit.framework.TestCase;

public class IdentificationTargetComparator_1_Test extends TestCase {

	private List<IIdentificationTarget> identificationTargets;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		//
		identificationTargets = new ArrayList<>();
		identificationTargets.add(createIdentificationTarget("A", 1316, 99.878f, 99.878f, 99.88f, 99.88f, 70.93f));
		identificationTargets.add(createIdentificationTarget("B", 0, 98.489f, 98.489f, 98.49f, 98.49f, 2.84f));
		identificationTargets.add(createIdentificationTarget("C", 965, 95.96f, 95.979f, 95.96f, 96.047f, 2.51f));
		identificationTargets.add(createIdentificationTarget("D", 1316, 99.561f, 99.561f, 99.561f, 99.564f, 4.94f));
		identificationTargets.add(createIdentificationTarget("E", 1315, 99.881f, 99.881f, 99.882f, 99.882f, 70.93f));
		identificationTargets.add(createIdentificationTarget("F", 1315, 99.53f, 99.53f, 99.531f, 99.53f, 4.94f));
		identificationTargets.add(createIdentificationTarget("G", 1249, 96.25f, 96.25f, 96.764f, 96.252f, 4.94f));
		identificationTargets.add(createIdentificationTarget("H", 1425, 95.831f, 95.831f, 97.462f, 95.832f, 0.54f));
		identificationTargets.add(createIdentificationTarget("I", 996, 95.448f, 95.573f, 95.45f, 95.58f, 0.36f));
		identificationTargets.add(createIdentificationTarget("J", 1020, 95.162f, 95.162f, 95.163f, 95.166f, 0.36f));
		identificationTargets.add(createIdentificationTarget("K", 1193, 96.95f, 96.95f, 98.32f, 96.951f, 2.51f));
		identificationTargets.add(createIdentificationTarget("L", 1247, 96.386f, 96.386f, 98.301f, 96.388f, 2.84f));
		identificationTargets.add(createIdentificationTarget("M", 0, 99.366f, 99.366f, 99.366f, 99.369f, 2.84f));
		identificationTargets.add(createIdentificationTarget("N", 1323, 99.245f, 99.246f, 99.245f, 99.28f, 2.51f));
		identificationTargets.add(createIdentificationTarget("O", 1337, 99.83f, 99.83f, 99.83f, 99.831f, 13.52f));
	}

	@Override
	protected void tearDown() throws Exception {

		identificationTargets.clear();
		super.tearDown();
	}

	public void test1() {

		Collections.sort(identificationTargets, new IdentificationTargetComparator(SortOrder.DESC, 1315));
		assertEquals("E", getName(identificationTargets.get(0)));
		assertEquals("F", getName(identificationTargets.get(1)));
		assertEquals("H", getName(identificationTargets.get(8)));
		assertEquals("M", getName(identificationTargets.get(13)));
		assertEquals("B", getName(identificationTargets.get(14)));
	}

	public void test2() {

		Collections.sort(identificationTargets, new IdentificationTargetComparator(SortOrder.ASC, 1315));
		assertEquals("B", getName(identificationTargets.get(0)));
		assertEquals("M", getName(identificationTargets.get(1)));
		assertEquals("G", getName(identificationTargets.get(8)));
		assertEquals("F", getName(identificationTargets.get(13)));
		assertEquals("E", getName(identificationTargets.get(14)));
	}

	public void test3() {

		Collections.sort(identificationTargets, new IdentificationTargetComparator(SortOrder.DESC, 0));
		assertEquals("E", getName(identificationTargets.get(0)));
		assertEquals("A", getName(identificationTargets.get(1)));
		assertEquals("K", getName(identificationTargets.get(8)));
		assertEquals("I", getName(identificationTargets.get(13)));
		assertEquals("J", getName(identificationTargets.get(14)));
	}

	public void test4() {

		Collections.sort(identificationTargets, new IdentificationTargetComparator(SortOrder.ASC, 0));
		assertEquals("J", getName(identificationTargets.get(0)));
		assertEquals("I", getName(identificationTargets.get(1)));
		assertEquals("N", getName(identificationTargets.get(8)));
		assertEquals("A", getName(identificationTargets.get(13)));
		assertEquals("E", getName(identificationTargets.get(14)));
	}

	public void test5() {

		Collections.sort(identificationTargets, new IdentificationTargetComparator(SortOrder.DESC));
		assertEquals("E", getName(identificationTargets.get(0)));
		assertEquals("A", getName(identificationTargets.get(1)));
		assertEquals("K", getName(identificationTargets.get(8)));
		assertEquals("I", getName(identificationTargets.get(13)));
		assertEquals("J", getName(identificationTargets.get(14)));
	}

	public void test6() {

		Collections.sort(identificationTargets, new IdentificationTargetComparator(SortOrder.ASC));
		assertEquals("J", getName(identificationTargets.get(0)));
		assertEquals("I", getName(identificationTargets.get(1)));
		assertEquals("N", getName(identificationTargets.get(8)));
		assertEquals("A", getName(identificationTargets.get(13)));
		assertEquals("E", getName(identificationTargets.get(14)));
	}

	public void test7() {

		Collections.sort(identificationTargets, new IdentificationTargetComparator(1315));
		assertEquals("E", getName(identificationTargets.get(0)));
		assertEquals("F", getName(identificationTargets.get(1)));
		assertEquals("H", getName(identificationTargets.get(8)));
		assertEquals("M", getName(identificationTargets.get(13)));
		assertEquals("B", getName(identificationTargets.get(14)));
	}

	public void test8() {

		Collections.sort(identificationTargets, new IdentificationTargetComparator());
		assertEquals("E", getName(identificationTargets.get(0)));
		assertEquals("A", getName(identificationTargets.get(1)));
		assertEquals("K", getName(identificationTargets.get(8)));
		assertEquals("I", getName(identificationTargets.get(13)));
		assertEquals("J", getName(identificationTargets.get(14)));
	}

	public void test9() {

		IIdentificationTarget identificationTarget = getIdentificationTarget(identificationTargets, "J");
		identificationTarget.setManuallyVerified(true);
		//
		Collections.sort(identificationTargets, new IdentificationTargetComparator(SortOrder.DESC, 1315));
		assertEquals("J", getName(identificationTargets.get(0)));
		assertEquals("E", getName(identificationTargets.get(1)));
		assertEquals("L", getName(identificationTargets.get(8)));
		assertEquals("M", getName(identificationTargets.get(13)));
		assertEquals("B", getName(identificationTargets.get(14)));
	}

	public void test10() {

		IIdentificationTarget identificationTarget;
		identificationTarget = getIdentificationTarget(identificationTargets, "J");
		identificationTarget.setManuallyVerified(true);
		identificationTarget = getIdentificationTarget(identificationTargets, "K");
		identificationTarget.setManuallyVerified(true);
		//
		Collections.sort(identificationTargets, new IdentificationTargetComparator(SortOrder.DESC, 1315));
		assertEquals("K", getName(identificationTargets.get(0)));
		assertEquals("J", getName(identificationTargets.get(1)));
		assertEquals("G", getName(identificationTargets.get(8)));
		assertEquals("M", getName(identificationTargets.get(13)));
		assertEquals("B", getName(identificationTargets.get(14)));
	}

	private IIdentificationTarget createIdentificationTarget(String name, float retentionIndex, float matchFactor, float reverseMatchFactor, float matchFactorDirect, float reverseMatchFactorDirect, float probability) {

		ILibraryInformation libraryInformation = new LibraryInformation();
		libraryInformation.setName(name);
		libraryInformation.setRetentionIndex(retentionIndex);
		IComparisonResult comparisonResult = new ComparisonResult(matchFactor, reverseMatchFactor, matchFactorDirect, reverseMatchFactorDirect, probability);
		return new IdentificationTarget(libraryInformation, comparisonResult);
	}

	private String getName(IIdentificationTarget identificationTarget) {

		ILibraryInformation libraryInformation = identificationTarget.getLibraryInformation();
		return libraryInformation.getName();
	}

	private IIdentificationTarget getIdentificationTarget(List<IIdentificationTarget> identificationTargets, String name) {

		for(IIdentificationTarget identificationTarget : identificationTargets) {
			if(getName(identificationTarget).equals(name)) {
				return identificationTarget;
			}
		}
		return null;
	}
}
