package ua.pp.myshko.csvholder.services.impl;

import ua.pp.myshko.csvholder.CSVHolderException;
import ua.pp.myshko.csvholder.model.ColumnMapping;
import ua.pp.myshko.csvholder.services.EntityMapper;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collection;

/**
 * @author M. Chernenko
 */
public class EntityMapperImpl implements EntityMapper {

    @Override
    public void saveMapping(String fileName, String tableName, Collection<ColumnMapping> columns)
            throws CSVHolderException {

        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLEventWriter eventWriter = null;
        try {
            eventWriter = outputFactory.createXMLEventWriter(new FileOutputStream(fileName));
            XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            XMLEvent end = eventFactory.createDTD("\n");
            //StartDocument startDocument = eventFactory.createStartDocument();
            //eventWriter.add(startDocument);
            StartElement startElement = eventFactory.createStartElement("", "", "hibernate-mapping");
            eventWriter.add(startElement);
             eventWriter.add(end);

                StartElement startClassElement = eventFactory.createStartElement("", "", "class");
                eventWriter.add(startClassElement);
                eventWriter.add(eventFactory.createAttribute("entity-name", tableName));
                eventWriter.add(eventFactory.createAttribute("schema", "public"));

                    StartElement startIdElement = eventFactory.createStartElement("", "", "id");
                    eventWriter.add(startIdElement);

                    eventWriter.add(eventFactory.createAttribute("name", "id"));
                    eventWriter.add(eventFactory.createAttribute("column", "ID"));
                    eventWriter.add(eventFactory.createAttribute("type", "long"));

                        StartElement startGeneratorElement = eventFactory.createStartElement("", "", "generator");
                        eventWriter.add(startGeneratorElement);

                        eventWriter.add(eventFactory.createAttribute("class", "sequence"));

                        eventWriter.add(end);
                        eventWriter.add(eventFactory.createEndElement("", "", "generator"));
                        eventWriter.add(end);
                    eventWriter.add(end);
                    eventWriter.add(eventFactory.createEndElement("", "", "id"));
                    eventWriter.add(end);

                    for(ColumnMapping column: columns) {

                        StartElement startPropertyElement = eventFactory.createStartElement("", "", "property");
                        eventWriter.add(startPropertyElement);

                        eventWriter.add(eventFactory.createAttribute("name", column.getDbFieldName()));
                        eventWriter.add(eventFactory.createAttribute("column", column.getDbFieldName()));
                        eventWriter.add(eventFactory.createAttribute("type", "string"));

                        eventWriter.add(end);
                        eventWriter.add(eventFactory.createEndElement("", "", "property"));
                        eventWriter.add(end);
                    }

                eventWriter.add(end);
                eventWriter.add(eventFactory.createEndElement("", "", "class"));
                eventWriter.add(end);

            eventWriter.add(eventFactory.createEndElement("", "", "hibernate-mapping"));
            eventWriter.add(end);
            eventWriter.add(eventFactory.createEndDocument());
            eventWriter.close();
        } catch (XMLStreamException | FileNotFoundException e) {
            throw new CSVHolderException(e);
        }
    }
}
