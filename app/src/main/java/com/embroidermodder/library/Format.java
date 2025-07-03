package com.embroidermodder.library;

import com.embroidermodder.library.reader.EmbReaderCOL;
import com.embroidermodder.library.reader.EmbReaderDST;
import com.embroidermodder.library.reader.EmbReaderEXP;
import com.embroidermodder.library.reader.EmbReaderEmm;
import com.embroidermodder.library.reader.EmbReaderINF;
import com.embroidermodder.library.reader.EmbReaderJEF;
import com.embroidermodder.library.reader.EmbReaderPCS;
import com.embroidermodder.library.reader.EmbReaderPEC;
import com.embroidermodder.library.reader.EmbReaderPES;
import com.embroidermodder.library.reader.EmbReaderSEW;
import com.embroidermodder.library.reader.EmbReaderSHV;
import com.embroidermodder.library.reader.EmbReaderSVG;
import com.embroidermodder.library.reader.EmbReaderVP3;
import com.embroidermodder.library.reader.EmbReaderXXX;
import com.embroidermodder.library.writer.EmbWriterDST;
import com.embroidermodder.library.writer.EmbWriterEXP;
import com.embroidermodder.library.writer.EmbWriterEMM;
import com.embroidermodder.library.writer.EmbWriterJEF;
import com.embroidermodder.library.writer.EmbWriterPEC;
import com.embroidermodder.library.writer.EmbWriterPES;
import com.embroidermodder.library.writer.EmbWriterPNG;
import com.embroidermodder.library.writer.EmbWriterSVG;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Format {
    public static String getExtentionByFileName(String name) {
        if (name == null) return null;
        String[] split = name.split("\\.");
        if (split.length <= 1) {
            return null;
        }
        return split[split.length - 1].toLowerCase();
    }

    public static Format.Reader getReaderByFilename(String filename) {
        String ext = getExtentionByFileName(filename);
        switch (ext) {
            case "emm":
                return new EmbReaderEmm();
            case "col":
                return new EmbReaderCOL();
            case "inf":
                return new EmbReaderINF();
            case "exp":
                return new EmbReaderEXP();
            case "dst":
                return new EmbReaderDST();
            case "jef":
                return new EmbReaderJEF();
            case "pcs":
                return new EmbReaderPCS();
            case "pec":
                return new EmbReaderPEC();
            case "pes":
                return new EmbReaderPES();
            case "sew":
                return new EmbReaderSEW();
            case "shv":
                return new EmbReaderSHV();
            case "vp3":
                return new EmbReaderVP3();
            case "xxx":
                return new EmbReaderXXX();
            case "svg":
                return new EmbReaderSVG();
            default:
                return null;
        }
    }

    public static Format.Writer getWriterByFilename(String filename) {
        String ext = getExtentionByFileName(filename);
        switch (ext) {
            case "exp":
                return new EmbWriterEXP();
            case "dst":
                return new EmbWriterDST();
            case "pec":
                return new EmbWriterPEC();
            case "pes":
                return new EmbWriterPES();
            case "emm":
                return new EmbWriterEMM();
            case "jef":
                return new EmbWriterJEF();
            case "png":
                return new EmbWriterPNG();
            case "svg":
                return new EmbWriterSVG();
            default:
                return null;
        }
    }

    public interface Reader {
        void read(EmbPattern pattern, InputStream stream) throws IOException;
    }

    public interface Writer {
        void write(EmbPattern pattern, OutputStream stream) throws IOException;
    }
}
