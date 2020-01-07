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
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignal;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;

public class SavitzkyGolayProcessor_5_ITest extends TestCase {

	private ITotalScanSignals totalScanSignals;
	private SavitzkyGolayProcessor processor;
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
		processor = new SavitzkyGolayProcessor();
		supplierFilterSettings = new ChromatogramFilterSettings();
		supplierFilterSettings.setWidth(31);
		supplierFilterSettings.setOrder(4);
		supplierFilterSettings.setDerivative(0);
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
		assertEquals(67758.20896584442d, sgTic[0]);
	}

	public void test3() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(61266.42230895289d, sgTic[1]);
	}

	public void test4() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(55875.55772159202d, sgTic[2]);
	}

	public void test5() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(51437.635988888396d, sgTic[3]);
	}

	public void test6() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(44964.866012741455d, sgTic[289]);
	}

	public void test7() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(49439.79381130549d, sgTic[892]);
	}

	public void test8() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(72844.98978663163d, sgTic[1293]);
	}

	public void test9() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(38271.11345939934d, sgTic[1474]);
	}

	public void test10() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(76556.411265042d, sgTic[2970]);
	}

	public void test11() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(236207.10789766416d, sgTic[3766]);
	}

	public void test12() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(230968.30559207205d, sgTic[4180]);
	}

	public void test13() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(333919.43573667714d, sgTic[4993]);
	}

	public void test14() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(156212.88376568144d, sgTic[5722]);
	}

	public void test15() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(155765.43254685838d, sgTic[5723]);
	}

	public void test16() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(155078.02171381755d, sgTic[5724]);
	}

	public void test17() {

		double[] sgTic = processor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(154096.83778247377d, sgTic[5725]);
	}
}
