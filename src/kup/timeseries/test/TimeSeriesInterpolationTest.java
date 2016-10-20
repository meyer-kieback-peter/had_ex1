/**
 * BaaS heating anomaly detection example
 * 
 * copyright Kieback&Peter GmbH & Co KG (2016) http://www.kieback-peter.de/
 * 
 * Sie können diese Datei unter folgenden Bedingungen weiterverwenden:
 * 
 * Die Datei wurde unter der Lizenz
 * „Creative Commons Namensnennung-Weitergabe unter gleichen Bedingungen Deutschland“
 * in Version 3.0 (abgekürzt „CC-by-sa 3.0/de“) veröffentlicht.
 * 
 * Den rechtsverbindlichen Lizenzvertrag finden Sie unter http://creativecommons.org/licenses/by-sa/3.0/de/legalcode.
 * 
 * Es folgt eine vereinfachte Zusammenfassung des Vertrags in allgemeinverständlicher Sprache ohne juristische Wirkung.
 * 
 * Es ist Ihnen gestattet,
 * - das Werk zu vervielfältigen, zu verbreiten und öffentlich zugänglich zu machen sowie
 * - Abwandlungen und Bearbeitungen des Werkes anzufertigen,
 * 
 * sofern Sie folgende Bedingungen einhalten:
 * Namensnennung: Sie müssen den Urheber bzw. den Rechteinhaber in der von ihm festgelegten Weise, die URI (z. B. die Internetadresse dieser Seite) sowie den Titel des Werkes und bei einer Abwandlung einen Hinweis darauf angeben.
 *
 * Weitergabe unter gleichen Bedingungen: Wenn Sie das lizenzierte Werk bearbeiten, abwandeln oder als Vorlage für ein neues Werk verwenden, dürfen Sie die neu entstandenen Werke nur unter dieser oder einer zu dieser kompatiblen Lizenz nutzen und weiterverbreiten.
 * 
 * Lizenzangabe: Sie müssen anderen alle Lizenzbedingungen mitteilen, die für dieses Werk gelten. Am einfachsten ist es, wenn Sie dazu einen Link auf den Lizenzvertrag (siehe oben) einbinden.
 *
 * Bitte beachten Sie, dass andere Rechte die Weiterverwendung einschränken können.
 * 
 */

package kup.timeseries.test;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import kup.timeseries.DataPoint;
import kup.timeseries.InterpolationException;
import kup.timeseries.TimeSeries;
import kup.timeseries.TimeSeriesException;

public class TimeSeriesInterpolationTest {
	TimeSeries ts;
	GregorianCalendar cal;
	
	static TimeSeries createTestTimeSeries(String name, GregorianCalendar start, int[] secVal) {
		TimeSeries ts = new TimeSeries(name);
		GregorianCalendar cal = (GregorianCalendar)start.clone();
		for (int i : secVal) {
			cal.set(GregorianCalendar.SECOND, i);
			try {
				ts.addDataPoint(new DataPoint(cal.getTime(), 10.0f * i));
			} catch (TimeSeriesException e) {
				e.printStackTrace();
			}
		}
		return ts;
	}

	@Before
	public void setUp() throws Exception {
		cal = DataPointTest.magicDate();
		int[] secVal = {1,2,3,4,5,6,7,8,9};
		ts = createTestTimeSeries("ts", cal, secVal);
	}

	@Test
	public void testInterpolateStart() throws TimeSeriesException, InterpolationException {
		cal.set(GregorianCalendar.SECOND, 1);
		Date dt = cal.getTime();
		DataPoint dpi = ts.interpolate(0, dt);
		assertEquals(dpi.date(), dt);
		assertEquals(dpi.value(), 10.0, 1.0e-10);
	}

	@Test
	public void testInterpolateEnd() throws TimeSeriesException, InterpolationException {
		cal.set(GregorianCalendar.SECOND, 9);
		Date dt = cal.getTime();
		DataPoint dpi = ts.interpolate(8, dt);
		assertEquals(dpi.date(), dt);
		assertEquals(dpi.value(), 90.0, 1.0e-10);
	}

	@Test
	public void testExtrapolateOffStart() throws TimeSeriesException, InterpolationException {
		cal.set(GregorianCalendar.SECOND, 0);
		Date dt = cal.getTime();
		DataPoint dpi = ts.interpolate(0, dt);
		assertEquals(dpi.date(), dt);
		assertEquals(dpi.value(), 0.0, 1.0e-10);
	}
	
	@Test
	public void testExtrapolateOffEnd() throws TimeSeriesException, InterpolationException {
		cal.set(GregorianCalendar.SECOND, 10);
		Date dt = cal.getTime();
		DataPoint dpi = ts.interpolate(8, dt);
		assertEquals(dpi.date(), dt);
		assertEquals(dpi.value(), 100.0, 1.0e-10);
		cal.set(GregorianCalendar.SECOND, 100);
		dt = cal.getTime();
		dpi = ts.interpolate(8, dt);
		assertEquals(dpi.date(), dt);
		assertEquals(dpi.value(), 1000.0, 1.0e-10);
	}
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test(expected = InterpolationException.class)
	public void testInterpolateOffDate() throws InterpolationException, TimeSeriesException {
		cal.set(GregorianCalendar.SECOND, 8);
		ts.interpolate(3, cal.getTime());
		thrown.expect(InterpolationException.class);
		thrown.expectMessage("off date interpolation");
	}

	@Test
	public void testInterpolate() throws TimeSeriesException, InterpolationException {
		int[] secVal = {1, 2, 5, 30};
		ts = createTestTimeSeries("ts", cal, secVal);

		cal.set(GregorianCalendar.SECOND, 0);
		Date dt = cal.getTime();
		DataPoint dpi = ts.interpolate(0, dt);
		assertEquals(dpi.date(), dt);
		assertEquals(dpi.value(), 0.0, 1.0e-10);

		cal.set(GregorianCalendar.SECOND, 1);
		dt = cal.getTime();
		dpi = ts.interpolate(0, dt);
		assertEquals(dpi.date(), dt);
		assertEquals(dpi.value(), 10.0, 1.0e-10);

		cal.set(GregorianCalendar.SECOND, 2);
		dt = cal.getTime();
		dpi = ts.interpolate(2, dt);
		assertEquals(dpi.date(), dt);
		assertEquals(dpi.value(), 20.0, 1.0e-10);

		cal.set(GregorianCalendar.SECOND, 3);
		dt = cal.getTime();
		dpi = ts.interpolate(2, dt);
		assertEquals(dpi.date(), dt);
		assertEquals(dpi.value(), 30.0, 1.0e-10);

		cal.set(GregorianCalendar.SECOND, 5);
		dt = cal.getTime();
		dpi = ts.interpolate(2, dt);
		assertEquals(dpi.date(), dt);
		assertEquals(dpi.value(), 50.0, 1.0e-10);

		cal.set(GregorianCalendar.SECOND, 10);
		dt = cal.getTime();
		dpi = ts.interpolate(3, dt);
		assertEquals(dpi.date(), dt);
		assertEquals(dpi.value(), 100.0, 1.0e-10);

		cal.set(GregorianCalendar.SECOND, 30);
		dt = cal.getTime();
		dpi = ts.interpolate(3, dt);
		assertEquals(dpi.date(), dt);
		assertEquals(dpi.value(), 300.0, 1.0e-10);

		cal.set(GregorianCalendar.SECOND, 42);
		dt = cal.getTime();
		dpi = ts.interpolate(3, dt);
		assertEquals(dpi.date(), dt);
		assertEquals(dpi.value(), 420.0, 1.0e-10);
	}

}

