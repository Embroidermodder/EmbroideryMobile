package com.embroidermodder.embroideryviewer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class EmbReaderSVG extends EmbReader {

    public static final String MIME = "image/svg+xml";
    public static final String EXT = "svg";

    private static final String CHARACTERS = "value";
    private static final String ELEMENT = "element";
    public static final String NAME_SVG = "svg";
    public static final String NAME_PATH = "path";
    public static final String NAME_POLYLINE = "polyline";
    public static final String ATTR_DATA = "d";
    public static final String ATTR_STROKE = "stroke";
    public static final String ATTR_STYLE = "style";
    public static final String ATTR_FILL = "fill";
    public static final String ATTR_WIDTH = "width";
    public static final String ATTR_HEIGHT = "height";
    public static final String ATTR_POINTS = "points";
    public static final String ATTR_VIEWBOX = "viewBox";
    public static final String VALUE_NONE = "none";
    public static final String ATTR_VERSION = "version";
    public static final String VALUE_SVG_VERSION = "1.1";
    public static final String ATTR_XMLNS = "xmlns";
    public static final String VALUE_XMLNS = "http://www.w3.org/2000/svg";
    public static final String ATTR_XMLNS_LINK = "xmlns:xlink";
    public static final String VALUE_XLINK = "http://www.w3.org/1999/xlink";
    public static final String ATTR_XMLNS_EV = "xmlns:ev";
    public static final String VALUE_XMLNS_EV = "http://www.w3.org/2001/xml-events";

    private static final String SVG_PATH_COMMANDS = "csqtamlzhv";


    @Override
    protected void read() throws IOException {
        final PathParser parser = new PathParser(SVG_PATH_COMMANDS);
        final PathParser.ParseCommand command = new PathParser.ParseCommand() {
            @Override
            public boolean matched(String s, PathParser.Values values) {
                Float a;
                Float b;
                switch (s) {
                    case "m":
                        a = values.getFloat();
                        b = values.getFloat();
                        if (b != null) {
                            trim();
                            move(a,b);
                        }
                        a = values.getFloat();
                        b = values.getFloat();
                        while (b != null) {
                            stitch(a,b);
                            a = values.getFloat();
                            b = values.getFloat();
                        }
                        break;
                    case "l":
                        a = values.getFloat();
                        b = values.getFloat();
                        while (b != null) {
                            stitch(a,b);
                            a = values.getFloat();
                            b = values.getFloat();
                        }
                        break;
                    case "M":
                        a = values.getFloat();
                        b = values.getFloat();
                        if (b != null) {
                            trim();
                            moveAbs(a,b);
                        }
                        a = values.getFloat();
                        b = values.getFloat();
                        while (b != null) {
                            stitchAbs(a,b);
                            a = values.getFloat();
                            b = values.getFloat();
                        }
                        break;
                    case "L":
                        a = values.getFloat();
                        b = values.getFloat();
                        while (b != null) {
                            stitchAbs(a,b);
                            a = values.getFloat();
                            b = values.getFloat();
                        }
                        break;
                    case "v":
                        a = values.getFloat();
                        while (a != null) {
                            stitch(0,a);
                            a = values.getFloat();
                        }
                        break;
                    case "h":
                        a = values.getFloat();
                        while (a != null) {
                            stitch(a,0);
                            a = values.getFloat();
                        }
                        break;
                    case "V":
                        a = values.getFloat();
                        while (a != null) {
                            stitchAbs(lastx,a);
                            a = values.getFloat();
                        }
                        break;
                    case "H":
                        a = values.getFloat();
                        while (a != null) {
                            stitchAbs(a,lasty);
                            a = values.getFloat();
                        }
                        break;
                }
                return false;
            }
        };
        elementParser(stream, new Receiver() {
            int lastColor = 0;
            @Override
            public void path(String path, int strokeColor) {
                if (strokeColor != lastColor) {
                    pattern.addThread(new EmbThread(strokeColor, "SVG Color " + EmbThread.getHexColor(strokeColor), null));
                    if (lastColor != 0) pattern.addStitchRel(0,0,EmbPattern.STOP, true); //TODO: Should be Colorchange.
                }
                lastColor = strokeColor;
                parser.parse(path, command);

            }

            @Override
            public void start() {
                lastColor = 0;
            }

            @Override
            public void finished() {
            }

            @Override
            public void error() {
            }
        });
    }

    public void elementParser(InputStream file, Receiver receiver) {
        SVGHandler svgHandler = new SVGHandler(receiver);
        try {
            receiver.start();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(file, svgHandler);
            receiver.finished();
        } catch (SAXException | IOException | ParserConfigurationException e) {
            receiver.error();
        }
    }

    private class SVGHandler extends DefaultHandler {
        Receiver receiver;
        private Stack<HashMap<String, String>> tags;

        public SVGHandler(Receiver receiver) {
            tags = new Stack<>();
            tags.push(new HashMap<String, String>());
            this.receiver = receiver;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attribs) throws SAXException {
            if (Thread.interrupted()) throw new SAXException();
            HashMap<String, String> context = new HashMap<>(tags.peek());
            tags.push(context);

            int attributelen = attribs.getLength();

            for (int i = 0; i < attributelen; i++) {
                String attributeQName = attribs.getQName(i);
                String attributeValue;
                switch (attributeQName) {
                    case ATTR_STYLE:
                        attributeValue = attribs.getValue(i);
                        String[] styles = attributeValue.split(";");

                        for (String v : styles) {
                            String[] vs = v.split(":");
                            if (vs.length == 2) {
                                context.put(vs[0], vs[1]);
                            }
                        }
                        break;
                    case ATTR_DATA:
                    case ATTR_FILL:
                    case ATTR_HEIGHT:
                    case ATTR_POINTS:
                    case ATTR_STROKE:
                    case ATTR_VERSION:
                    case ATTR_VIEWBOX:
                    case ATTR_WIDTH:
                        attributeValue = attribs.getValue(i);
                        context.put(attributeQName, attributeValue);
                        break;
                }
            }
            context.put(ELEMENT, qName);

            switch (qName.toLowerCase()) {
                case NAME_PATH:
                    String path = context.get(ATTR_DATA);
                    if (path != null) {
                        receiver.path(path, EmbThread.parseColor(context.get(ATTR_STROKE)));
                    }
                    break;
                case NAME_POLYLINE:
                    String points = context.get(ATTR_POINTS);
                    if (points == null) {
                        break;
                    }
                    receiver.path("M" + points, EmbThread.parseColor(context.get(ATTR_STROKE)));
                    break;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            String s = new String(ch, start, length);
            HashMap<String, String> context = tags.peek();
            context.put(CHARACTERS, s);
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            tags.pop();
        }

    }

    public interface Receiver {
        void path(String path, int strokeColor);

        void start();

        void finished();

        void error();
    }
}
