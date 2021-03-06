package eu.kyotoproject.util;

import eu.kyotoproject.kaf.KafEntity;
import eu.kyotoproject.kaf.KafSaxParser;
import eu.kyotoproject.kaf.KafTerm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by piek on 16/05/16.
 */
public class FixEntities {

    static public void main (String[] args) {
        KafSaxParser kafSaxParser = new KafSaxParser();
        String pathToFile = "";
        String extension = "";
         pathToFile = "/Users/piek/Desktop/NWR-INC/dasym/test1/test.naf";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equalsIgnoreCase("--input") && args.length>(i+1)) {
                pathToFile = args[i+1];
            }
            else if (arg.equalsIgnoreCase("--extension") && args.length>(i+1)) {
                extension = args[i+1];
            }
        }
        if (pathToFile.equalsIgnoreCase("stream")) {
            kafSaxParser.parseFile(System.in);
            fix(kafSaxParser);
            kafSaxParser.writeNafToStream(System.out);
        }
        else {
            File file = new File(pathToFile);
            if (file.isDirectory()) {
                ArrayList<File> files = FileProcessor.makeRecursiveFileArrayList(pathToFile, extension);
                for (int i = 0; i < files.size(); i++) {
                    File nextFile = files.get(i);
                    kafSaxParser.parseFile(nextFile);
                    fix(kafSaxParser);
                    try {
                        OutputStream fos = new FileOutputStream(nextFile);
                        kafSaxParser.writeNafToStream(fos);
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                kafSaxParser.parseFile(file);
                fix(kafSaxParser);
                try {
                    OutputStream fos = new FileOutputStream(file);
                    kafSaxParser.writeNafToStream(fos);
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static public void fix (KafSaxParser kafSaxParser) {
        ArrayList<KafEntity> fixedEntities = new ArrayList<KafEntity>();
        for (int i = 0; i < kafSaxParser.kafEntityArrayList.size(); i++) {
            KafEntity kafEntity = kafSaxParser.kafEntityArrayList.get(i);
            if (validEntityType(kafEntity.getType())) {
                fixedEntities.add(kafEntity);
            }
            else {
                ArrayList<String> spanIds = kafEntity.getTermIds();
                String lastSpanId = spanIds.get(spanIds.size()-1);
                KafTerm kafTerm = kafSaxParser.getTerm(lastSpanId);
                if (kafTerm!=null) {
                    if (kafTerm.getPos().equalsIgnoreCase("name")) {
                      fixedEntities.add(kafEntity);
                    }
                }
            }
        }
        kafSaxParser.kafEntityArrayList = fixedEntities;
    }

    static boolean validEntityType (String type) {
        if (type.equalsIgnoreCase("PER")) {
            return true;
        }
        else if (type.equalsIgnoreCase("PRO")) {
            return true;
        }
        else if (type.equalsIgnoreCase("MISC")) {
            return true;
        }
        else if (type.equalsIgnoreCase("ORG")) {
            return true;
        }
        else if (type.equalsIgnoreCase("LOC")) {
            return true;
        }
        return false;
    }



}
