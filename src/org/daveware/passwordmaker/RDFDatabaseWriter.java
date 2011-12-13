/*
 * PasswordMaker Java Edition - One Password To Rule Them All
 * Copyright (C) 2011 Dave Marotti
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.daveware.passwordmaker;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

/**
 * Writes a Database in an RDF format.
 * 
 * @author Dave Marotti
 */
public class RDFDatabaseWriter implements DatabaseWriter {
    Logger logger = Logger.getLogger(getClass().toString());

    /**
     * Pretty prints the XML.
     * 
     * @param xml The string containing all the XML.
     * @return A formatted string.
     */
	private String formatXml(String xml)
	    throws Exception
	{
        Transformer serializer= SAXTransformerFactory.newInstance().newTransformer();
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        Source xmlSource=new SAXSource(new InputSource(new ByteArrayInputStream(xml.getBytes())));
        StreamResult res =  new StreamResult(new ByteArrayOutputStream());            
        serializer.transform(xmlSource, res);
        return new String(((ByteArrayOutputStream)res.getOutputStream()).toByteArray());
    }
	
	@Override
	/**
	 * Writes the Database to an OutputStream in the RDF format.
	 */
	public void write(OutputStream os, Database db) 
	        throws Exception 
    {
		try {
			StringWriter sWriter = new StringWriter();
			
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = outputFactory.createXMLStreamWriter(sWriter);
			
			writer.writeStartDocument();
			writer.writeStartElement("RDF:RDF");
			writer.writeAttribute("xmlns:NS1", "http://passwordmaker.mozdev.org/rdf#");
			writer.writeAttribute("xmlns:NC", "http://home.netscape.com/NC-rdf#");
			writer.writeAttribute("xmlns:RDF", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	
			//for(Account child : db.getRootAccount().getChildren()) {
			//	writeParent(child, writer);
			//}
			writeParent(db.getRootAccount(), writer);
			writeFFGlobalSettings(db, writer);
			
			writer.writeEndElement();
			writer.writeEndDocument();
			writer.flush();
			writer.close();

			sWriter.flush();
			sWriter.close();
			
			// Now pretty-print it
			String pretty = formatXml(sWriter.toString());
			BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(os));
			bWriter.write(pretty);
			bWriter.flush();
			bWriter.close();

		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * Writes a single RDF:Description node to the XML stream.
	 * @param account The account to write from.
	 * @param writer The XML stream to write into.
	 * @throws Exception ...
	 */
	private void writeDescription(Account account, XMLStreamWriter writer) 
		throws Exception 
	{
		writer.writeStartElement("RDF:Description");
		writer.writeAttribute("RDF:about", account.getId());
		writer.writeAttribute("NS1:name", account.getName());
		writer.writeAttribute("NS1:description", account.getDesc());
		
		if(account.isFolder()==false) {
    		writer.writeAttribute("NS1:whereLeetLB", account.getLeetType().toRdfString());
    		writer.writeAttribute("NS1:leetLevelLB", Integer.toString(account.getLeetLevel().getLevel()));
    		
    		if(account.isHmac())
    		    writer.writeAttribute("NS1:hashAlgorithmLB", account.getAlgorithm().toHmacRdfString());
    		else
    		    writer.writeAttribute("NS1:hashAlgorithmLB", account.getAlgorithm().toRdfString());
    		
    		writer.writeAttribute("NS1:passwordLength", Integer.toString(account.getLength()));
    		writer.writeAttribute("NS1:usernameTB", account.getUsername());
    		writer.writeAttribute("NS1:counter", account.getModifier());
    		writer.writeAttribute("NS1:charset", account.getCharacterSet());
    		writer.writeAttribute("NS1:prefix", account.getPrefix());
    		writer.writeAttribute("NS1:suffix", account.getSuffix());
    		writer.writeAttribute("NS1:autoPopulate", "false"); // TODO: make this a setting allowed in accounts
    		
    		// The default account contains specifiers for extracting pieces of an URL
    		if(account.isDefault()) {
    		    Set<Account.UrlComponents> urlComponents = account.getUrlComponents();
                writer.writeAttribute("NS1:protocolCB",  urlComponents.contains(Account.UrlComponents.Protocol) ? "true" : "false");
                writer.writeAttribute("NS1:subdomainCB", urlComponents.contains(Account.UrlComponents.Subdomain) ? "true" : "false");
                writer.writeAttribute("NS1:domainCB",    urlComponents.contains(Account.UrlComponents.Domain) ? "true" : "false");
                writer.writeAttribute("NS1:pathCB",      urlComponents.contains(Account.UrlComponents.PortPathAnchorQuery) ? "true" : "false");
    		}
    		else {
    		    // The non-default accounts store the URL
    		    writer.writeAttribute("NS1:urlToUse", account.getUrl());
    		}
    		
    		int patternCount = 0;
    		for(AccountPatternData data : account.getPatterns()) {
    			writer.writeAttribute("NS1:pattern" + patternCount, data.getPattern());
    			if(data.getType()==AccountPatternType.WILDCARD)
    				writer.writeAttribute("NS1:patterntype" + patternCount, "wildcard");
    			else
    				writer.writeAttribute("NS1:patterntype" + patternCount, "regex");
    			writer.writeAttribute("NS1:patternenabled" + patternCount, "true");  // TODO: make this a setting allowed in pattern data
    			writer.writeAttribute("NS1:patterndesc" + patternCount, data.getDesc());
    			patternCount++;
    		}
		}

		writer.writeEndElement();
	}
	
	/**
	 * Writes a parent node to the stream, recursing into any children.
	 * 
	 * @param account The parent account to write.
	 * @param writer The XML stream to write to.
	 * @throws Exception on who the hell knows.
	 */
	private void writeParent(Account account, XMLStreamWriter writer) 
			throws Exception 
	{
		// Sequence block
		writer.writeStartElement("RDF:Seq");
		writer.writeAttribute("RDF:about", account.getId());
		for(Account child : account.getChildren()) {
			logger.fine("    Write-RDF:li: " + child.getId());
			writer.writeStartElement("RDF:li");
			writer.writeAttribute("RDF:resource", child.getId());
			writer.writeEndElement();
		}
		writer.writeEndElement();

		// Descriptions of all elements including the parent
		writeDescription(account, writer);
		for(Account child : account.getChildren()) {
			logger.fine("Write-RDF:Desc: " + child.getName());
			writeDescription(child, writer);
		}
		
		// Recurse into the children
		for(Account child : account.getChildren()) {
			if(child.getChildren().size()>0) {
				writeParent(child, writer);
			}
		}
	}
	
	/**
	 * Writes the firefox settings back.
	 * 
	 * @param db The database with the settings to write.
	 * @param writer The XML stream to write to.
	 * @throws Exception ... probably never.
	 */
	private void writeFFGlobalSettings(Database db, XMLStreamWriter writer) 
	    throws Exception 
	{
	    writer.writeStartElement("RDF:Description");
	    
	    writer.writeAttribute("RDF:about", RDFDatabaseReader.FF_GLOBAL_SETTINGS_URI);
	    
	    for(String key : db.getGlobalSettings().keySet()) {
	        writer.writeAttribute(key, db.getGlobalSettings().get(key));
	    }
	    
	    writer.writeEndElement();
	}

}
