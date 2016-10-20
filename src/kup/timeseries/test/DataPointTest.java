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

import static org.junit.Assert.*;

import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import kup.timeseries.DataPoint;
import kup.timeseries.InterpolationException;

public class DataPointTest {
	
	public static GregorianCalendar magicDate() {
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(2016, 2, 10, 14, 8, 0);
		return cal;
	}

	@Test
	public void testInterpolate() throws InterpolationException {
		GregorianCalendar cal = magicDate();
		DataPoint dpl = new DataPoint(cal.getTime(), 40.0f);
		cal.set(GregorianCalendar.SECOND, 10);
		DataPoint dpr = new DataPoint(cal.getTime(), 50.0f);
		cal.set(GregorianCalendar.SECOND, 2);
		Date dti = cal.getTime();
		DataPoint dpi = DataPoint.interpolate(dpl, dpr, dti);
		assertEquals(dpi.date(), dti);
		assertEquals(dpi.value(), 42.0, 1.0e-10);
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test(expected = InterpolationException.class)
	public void testInterpolateOutOfOrder() throws InterpolationException {
		GregorianCalendar cal = magicDate();
		cal.set(GregorianCalendar.SECOND, 10);
		DataPoint dpl = new DataPoint(cal.getTime(), 40.0f);
		cal.set(GregorianCalendar.SECOND, 0);
		DataPoint dpr = new DataPoint(cal.getTime(), 50.0f);
		cal.set(GregorianCalendar.SECOND, 2);
		Date dti = cal.getTime();
		DataPoint.interpolate(dpl, dpr, dti);
		thrown.expect(InterpolationException.class);
		thrown.expectMessage("interpolation dates out of order");
	}

	@Test(expected = InterpolationException.class)
	public void testInterpolateOffDate() throws InterpolationException {
		GregorianCalendar cal = magicDate();
		cal.set(GregorianCalendar.SECOND, 10);
		DataPoint dpl = new DataPoint(cal.getTime(), 40.0f);
		cal.set(GregorianCalendar.SECOND, 5);
		DataPoint dpr = new DataPoint(cal.getTime(), 50.0f);
		cal.set(GregorianCalendar.SECOND, 2);
		Date dti = cal.getTime();
		DataPoint.interpolate(dpl, dpr, dti);
		thrown.expect(InterpolationException.class);
		thrown.expectMessage("off date interpolation");
	}

}
