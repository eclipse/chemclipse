/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
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

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.TestPathHelper;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.FilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.FilterSettings;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignal;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;

public class SavitzkyGolayProcessor_4_ITest extends TestCase {

	private ITotalScanSignals totalScanSignals;
	private SavitzkyGolayProcessor processor;
	private FilterSettings supplierFilterSettings;

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
		processor = new SavitzkyGolayProcessor();
		supplierFilterSettings = new FilterSettings();
		supplierFilterSettings.setWidth(27);
		supplierFilterSettings.setOrder(3);
		supplierFilterSettings.setDerivative(2);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(5726, sgTic.length);
	}

	public void test2() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(550.2866545969985d, sgTic[0]);
	}

	public void test3() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(515.5611805818696d, sgTic[1]);
	}

	public void test4() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(480.83570656673993d, sgTic[2]);
	}

	public void test5() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(446.11023255161064d, sgTic[3]);
	}

	public void test6() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-20.495566502463177d, sgTic[289]);
	}

	public void test7() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(237.78110395351771d, sgTic[892]);
	}

	public void test8() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-286.8311144793903d, sgTic[1293]);
	}

	public void test9() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(306.2930150309456d, sgTic[1474]);
	}

	public void test10() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(351.933013346806d, sgTic[2970]);
	}

	public void test11() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-17.08777735674346d, sgTic[3766]);
	}

	public void test12() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-1.5633699633704055d, sgTic[4180]);
	}

	public void test13() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-34.820799124248765d, sgTic[4993]);
	}

	public void test14() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-41.467699605628695d, sgTic[5722]);
	}

	public void test15() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-49.37024405989632d, sgTic[5723]);
	}

	public void test16() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-57.27278851416531d, sgTic[5724]);
	}

	public void test17() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-65.17533296843476d, sgTic[5725]);
	}
}
