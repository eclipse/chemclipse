/*******************************************************************************
 * Copyright (c) 2015, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.TestPathHelper;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignal;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class SavitzkyGolayProcessor_3_ITest extends TestCase {

	private ITotalScanSignals totalScanSignals;
	private ChromatogramFilterSettings supplierFilterSettings;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		/*
		 * Signals
		 */
		totalScanSignals = new TotalScanSignals(5726);
		BufferedReader reader = new BufferedReader(new FileReader(new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_1))));
		String line;
		while((line = reader.readLine()) != null) {
			String[] values = line.split(", ");
			ITotalScanSignal totalScanSignal = new TotalScanSignal(Integer.valueOf(values[0]), Float.valueOf(values[1]), Float.valueOf(values[2]));
			totalScanSignals.add(totalScanSignal);
		}
		reader.close();
		/*
		 * Processor and settings
		 */
		supplierFilterSettings = new ChromatogramFilterSettings();
		supplierFilterSettings.setWidth(15);
		supplierFilterSettings.setOrder(3);
		supplierFilterSettings.setDerivative(1);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(5726, sgTic.length);
	}

	public void test2() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-6559.777427637722d, sgTic[0]);
	}

	public void test3() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-5592.744487538601d, sgTic[1]);
	}

	public void test4() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-4722.498500682323d, sgTic[2]);
	}

	public void test5() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-3949.039467068877d, sgTic[3]);
	}

	public void test6() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(339.0260270774984d, sgTic[289]);
	}

	public void test7() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-3506.1580627977655d, sgTic[892]);
	}

	public void test8() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-1417.4404522492719d, sgTic[1293]);
	}

	public void test9() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(2262.8720522396984d, sgTic[1474]);
	}

	public void test10() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(4326.592625511741d, sgTic[2970]);
	}

	public void test11() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-696.1055118628701d, sgTic[3766]);
	}

	public void test12() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(3857.9334763820043d, sgTic[4180]);
	}

	public void test13() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-1020.9791352438424d, sgTic[4993]);
	}

	public void test14() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-349.78832387656075d, sgTic[5722]);
	}

	public void test15() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-380.22618748354216d, sgTic[5723]);
	}

	public void test16() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-401.81425219660014d, sgTic[5724]);
	}

	public void test17() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-414.55251801574195d, sgTic[5725]);
	}
}
