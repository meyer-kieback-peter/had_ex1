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
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.junit.Test;

import kup.timeseries.CrossSampleException;
import kup.timeseries.DataPoint;
import kup.timeseries.TimeSeries;
import kup.timeseries.TimeSeriesException;
import kup.timeseries.function.Abs;
import kup.timeseries.function.BackDiff;
import kup.timeseries.function.DailyMeanTemp;
import kup.timeseries.function.T1;

public class TimeSeriesFunctionTest {

	@Test
	public void testAbs() throws TimeSeriesException {
		Calendar cal = DataPointTest.magicDate();
		int[] secVal = {1,3,5,6,8,10};
		TimeSeries ts = new TimeSeries("ts");
		TimeSeries expected = new TimeSeries("expected");
		for (int i : secVal) {
			cal.set(GregorianCalendar.SECOND, i);
			ts.addDataPoint(new DataPoint(cal.getTime(), -10.0f * i));
			expected.addDataPoint(new DataPoint(cal.getTime(), 10.0f * i));
		}
		Abs abs = new Abs(ts);
		TimeSeries tsAbs = abs.compute("expected");
		assertEquals(tsAbs, expected);
	}

	@Test
	public void testT1() throws TimeSeriesException, CrossSampleException {
		Calendar cal = DataPointTest.magicDate();
		int[] secVal = {1,2,3,4,5};
		TimeSeries ts = new TimeSeries("ts");
		for (int i : secVal) {
			cal.set(GregorianCalendar.SECOND, i);
			ts.addDataPoint(new DataPoint(cal.getTime(), i > 1 ? 1.0f : 0.0f));
		}
		TimeSeries expected = new TimeSeries("expected");
		cal.set(GregorianCalendar.SECOND, 1);
		expected.addDataPoint(new DataPoint(cal.getTime(), 0.0f));
		cal.set(GregorianCalendar.SECOND, 2);
		expected.addDataPoint(new DataPoint(cal.getTime(), 0.6321f));
		cal.set(GregorianCalendar.SECOND, 3);
		expected.addDataPoint(new DataPoint(cal.getTime(), 0.9502f));
		cal.set(GregorianCalendar.SECOND, 4);
		expected.addDataPoint(new DataPoint(cal.getTime(), 0.9975f));
		cal.set(GregorianCalendar.SECOND, 5);
		expected.addDataPoint(new DataPoint(cal.getTime(), 0.9999f));
		
		T1 t1 = new T1(ts, 1.0f);
		TimeSeries tsT1 = t1.compute();
		//System.out.println(tsT1);
		//System.out.println(expected);
		assertTrue(tsT1.compareDataWithDelta(expected, 1.0e-4f));
	}

	@Test
	public void testDailyMeanTemp() throws TimeSeriesException, CrossSampleException {
		GregorianCalendar cal = new GregorianCalendar();
		TimeSeries ts = new TimeSeries("ts");

		for (int day = 10; day < 13; day ++ ) {
			for (int hour = 0; hour < 24; hour++) {
				cal.set(2016, 2, day, hour, 00, 0);
				ts.addDataPoint(new DataPoint(cal.getTime(), day - 10 + (hour < 12 ? hour : 24 - hour)));
			}
		}
		DailyMeanTemp dmt = new  DailyMeanTemp(ts);
		TimeSeries dailyMean = dmt.compute("daily mean temp");
		//System.out.println(dailyMean);
		assertEquals(6, dailyMean.size());
		
		Vector<TimeSeries> days = dmt.splitDays();
		assertEquals(3, days.size());
		for (int i= 0; i< days.size(); i++) {
			TimeSeries day = days.elementAt(i);
			assertEquals(24, day.size());
			//System.out.println(day);
			assertEquals(i + 6, DailyMeanTemp.mean(day), 1.0e-10);
			assertEquals(i + 6, DailyMeanTemp.dailyMean(day), 1.0e-5); // delta, because of interpolation inaccuracy
			assertEquals(i + 6, dailyMean.getDataPoint(i*2).value(), 1.0e-5); // delta, because of interpolation inaccuracy
			assertEquals(i + 6, dailyMean.getDataPoint(i*2+1).value(), 1.0e-5); // delta, because of interpolation inaccuracy
		}
		
	}

	@Test
	public void testBackDiff() throws TimeSeriesException, CrossSampleException {
		GregorianCalendar cal = new GregorianCalendar();
		TimeSeries ts = new TimeSeries("ts");
		TimeSeries expected = new TimeSeries("expected");

		for (int day = 10; day < 13; day ++ ) {
			for (int hour = 0; hour < 24; hour += 6) {
				cal.set(2016, 2, day, hour, 00, 0);
				ts.addDataPoint(new DataPoint(cal.getTime(), day));
				expected.addDataPoint(new DataPoint(cal.getTime(), day == 10 ? 0 : 1));
			}
		}
		BackDiff bd = new BackDiff(ts, BackDiff.MSECS_PER_DAY);
		TimeSeries tsBd = bd.compute();
		assertEquals(ts.size(), tsBd.size());
		assertTrue(tsBd.compareDataWithDelta(expected, 1.0e-10f));
	}
}
