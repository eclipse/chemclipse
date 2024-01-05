/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.literature;

import junit.framework.TestCase;

/*
 * Test the line endings.
 */
public class LiteratureSupport_4_Test extends TestCase {

	private static final String URL = "https://doi.org/10.1002/rcm.9294";
	private static final String TITLE = "A general procedure for rounding m/z values in low‐resolution mass spectra";
	private String contentA;
	private String contentB;
	private String contentC;

	@Override
	protected void setUp() throws Exception {

		contentA = getRIS_v1();
		contentB = getRIS_v2();
		contentC = getRIS_v3();
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1a() {

		assertEquals(URL, LiteratureSupport.getContainedLink(contentA));
	}

	public void test1b() {

		assertEquals(URL, LiteratureSupport.getContainedLink(contentB));
	}

	public void test1c() {

		assertEquals(URL, LiteratureSupport.getContainedLink(contentC));
	}

	public void test2a() {

		assertEquals(TITLE, LiteratureSupport.getTitle(contentA));
	}

	public void test2b() {

		assertEquals(TITLE, LiteratureSupport.getTitle(contentB));
	}

	public void test2c() {

		assertEquals(TITLE, LiteratureSupport.getTitle(contentC));
	}

	private String getRIS_v1() {

		return "TY  - JOUR\n" //
				+ "TI  - A general procedure for rounding m/z values in low‐resolution mass spectra\n" //
				+ "AU  - Khrisanfov, Mikhail\n" //
				+ "AU  - Samokhin, Andrey\n" //
				+ "T2  - Rapid Communications in Mass Spectrometry\n" //
				+ "DA  - 2022/06/15/\n" //
				+ "PY  - 2022\n" //
				+ "DO  - 10.1002/rcm.9294\n" //
				+ "VL  - 36\n" //
				+ "IS  - 11\n" //
				+ "J2  - Rapid Comm Mass Spectrometry\n" //
				+ "LA  - en\n" //
				+ "SN  - 0951-4198, 1097-0231\n" //
				+ "UR  - https://doi.org/10.1002/rcm.9294\n" //
				+ "Y2  - 2022/08/18/15:22:47\n" //
				+ "ER  - "; //
	}

	private String getRIS_v2() {

		return "TY  - JOUR\r\n"//
				+ "TI  - A general procedure for rounding m/z values in low‐resolution mass spectra\r\n"//
				+ "AU  - Khrisanfov, Mikhail\r\n"//
				+ "AU  - Samokhin, Andrey\r\n"//
				+ "T2  - Rapid Communications in Mass Spectrometry\r\n"//
				+ "DA  - 2022/06/15/\r\n"//
				+ "PY  - 2022\r\n"//
				+ "DO  - 10.1002/rcm.9294\r\n"//
				+ "VL  - 36\r\n"//
				+ "IS  - 11\r\n"//
				+ "J2  - Rapid Comm Mass Spectrometry\r\n"//
				+ "LA  - en\r\n"//
				+ "SN  - 0951-4198, 1097-0231\r\n"//
				+ "UR  - https://doi.org/10.1002/rcm.9294\r\n"//
				+ "Y2  - 2022/08/18/15:22:47\r\n"//
				+ "ER  - ";//
	}

	private String getRIS_v3() {

		return "TY  - JOUR\r" //
				+ "TI  - A general procedure for rounding m/z values in low‐resolution mass spectra\r" //
				+ "AU  - Khrisanfov, Mikhail\r" //
				+ "AU  - Samokhin, Andrey\r" //
				+ "T2  - Rapid Communications in Mass Spectrometry\r" //
				+ "DA  - 2022/06/15/\r" //
				+ "PY  - 2022\r" //
				+ "DO  - 10.1002/rcm.9294\r" //
				+ "VL  - 36\r" //
				+ "IS  - 11\r" //
				+ "J2  - Rapid Comm Mass Spectrometry\r" //
				+ "LA  - en\r" //
				+ "SN  - 0951-4198, 1097-0231\r" //
				+ "UR  - https://doi.org/10.1002/rcm.9294\r" //
				+ "Y2  - 2022/08/18/15:22:47\r" //
				+ "ER  - "; //
	}
}