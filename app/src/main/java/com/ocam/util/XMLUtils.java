package com.ocam.util;

import android.util.Xml;

import com.ocam.model.types.GPSPoint;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de utilidad para parsear contenido XML extraída de la documentación oficial de Google.
 * Adaptada para interpretar ficheros con contenido GPX
 * Versiones admitidas: 1.0 y 1.1
 */
public class XMLUtils {

    private static final String ns = null;

    public static List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag(); //definicion del XML
            return readGPX(parser);
        } finally {
            in.close();
        }
    }

    private static List readGPX(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<GPSPoint> entries = new ArrayList();
        parser.require(XmlPullParser.START_TAG, ns, "gpx");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("trk")) {
                entries.addAll(readTrk(parser));
            } else if (name.equals("wpt")) {
                entries.add(readPoint(parser));
                skip(parser);
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private static List<GPSPoint> readTrk(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<GPSPoint> list = new ArrayList();
        parser.require(XmlPullParser.START_TAG, ns, "trk");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("trkseg")) {
                list.addAll(readTrkSeg(parser));
            } else {
                skip(parser);
            }
        }
        return list;
    }

    private static List<GPSPoint> readTrkSeg(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<GPSPoint> gps = new ArrayList();
        parser.require(XmlPullParser.START_TAG, ns, "trkseg");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("trkpt")) {
                gps.add(readPoint(parser));
                skip(parser);
            }
        }
        return gps;
    }

    private static GPSPoint readPoint(XmlPullParser parser) throws IOException, XmlPullParserException {
        String lat = parser.getAttributeValue(null, "lat");
        String lon = parser.getAttributeValue(null, "lon");
        return new GPSPoint(Float.parseFloat(lat), Float.parseFloat(lon));
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
