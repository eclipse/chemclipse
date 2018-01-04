/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.chromatogram;

import org.eclipse.chemclipse.converter.core.Converter;

import junit.framework.TestCase;

/**
 * Testing ChromatogramConverters isValid(String input) method. This method
 * tests if the input string contains not allowed characters like \/:*?"<>|
 * 
 * @author eselmeister
 */
public class ChromatogramConverter_1_Test extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testIsValid_1() {

		String input = "abcdefg";
		boolean actual = Converter.isValid(input);
		assertEquals("Allowed characters", true, actual);
	}

	public void testIsValid_2() {

		String input = "\\/:*?\"<>|";
		boolean actual = Converter.isValid(input);
		assertEquals("Denied characters", false, actual);
	}

	public void testIsValid_3() {

		String input = "a\\";
		boolean actual = Converter.isValid(input);
		assertEquals("Denied characters", false, actual);
	}

	public void testIsValid_4() {

		String input = "a/";
		boolean actual = Converter.isValid(input);
		assertEquals("Denied characters", false, actual);
	}

	public void testIsValid_5() {

		String input = "a:";
		boolean actual = Converter.isValid(input);
		assertEquals("Denied characters", false, actual);
	}

	public void testIsValid_6() {

		String input = "a*";
		boolean actual = Converter.isValid(input);
		assertEquals("Denied characters", false, actual);
	}

	public void testIsValid_7() {

		String input = "a?";
		boolean actual = Converter.isValid(input);
		assertEquals("Denied characters", false, actual);
	}

	public void testIsValid_8() {

		String input = "a\"";
		boolean actual = Converter.isValid(input);
		assertEquals("Denied characters", false, actual);
	}

	public void testIsValid_9() {

		String input = "a<";
		boolean actual = Converter.isValid(input);
		assertEquals("Denied characters", false, actual);
	}

	public void testIsValid_10() {

		String input = "a>";
		boolean actual = Converter.isValid(input);
		assertEquals("Denied characters", false, actual);
	}

	public void testIsValid_11() {

		String input = "a|";
		boolean actual = Converter.isValid(input);
		assertEquals("Denied characters", false, actual);
	}

	public void testIsValid_12() {

		String input = "";
		boolean actual = Converter.isValid(input);
		assertEquals("Allowed characters", true, actual);
	}

	public void testIsValid_13() {

		String input = null;
		boolean actual = Converter.isValid(input);
		assertEquals("When input == null it should be false.", false, actual);
	}
}
