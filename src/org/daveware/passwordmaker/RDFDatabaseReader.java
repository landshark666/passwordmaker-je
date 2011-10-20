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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Implements the DatabaseReader interface to allow reading of RDF files.
 * 
 * This works by first reading every RDF:Description that can be found and storing
 * the results in a HashMap<String, Account> where the key is the RDF:about
 * Mozilla-RDF hash thing.  This is then used to build the Account hierarchy by
 * reading in all the RDF:Seq and nested RDF:li nodes.
 * 
 * @author Dave Marotti
 */
public class RDFDatabaseReader implements DatabaseReader {

    public static final String EXTENSION = ".rdf";
    public static final String FF_GLOBAL_SETTINGS_URI = "http://passwordmaker.mozdev.org/globalSettings";
    
    boolean ignoreBuggyJavascript = true;
    Logger logger = Logger.getLogger(getClass().getName());
    
    public class SeqData {
        public String id = "";
        public ArrayList<String> listItems = new ArrayList<String>();
    };
    
    public RDFDatabaseReader() {
        
    }
    
    /**
     * Set whether or not to ignore Accounts which use the old buggy javascript
     * version of HMAC-SHA256.  This defaults to true. If set to false it will
     * cause a fatal exception to be raised and the loading will abort.
     */
    public void setIgnoreBuggyJavascript(boolean b) {
        ignoreBuggyJavascript = b;
    }
    
    public Database read(InputStream i) throws Exception {
        Database db = new Database();
        
        HashMap<String, Account> descriptionMap = new HashMap<String, Account>();  // Map of hash -> Account
        HashMap<String, ArrayList<String> > seqMap = new HashMap<String, ArrayList<String> >(); // List of non-root nodes that have children
        
        // XML crap
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(i);
        
        doc.getDocumentElement().normalize();
       
        // Locate the RDF:RDF node
        Node rdfNode = doc.getFirstChild();
        if(rdfNode.getNodeName().compareTo("RDF:RDF")!=0)
            throw new Exception("RDF file contained no 'RDF:RDF' nodes, corrupt file?");
        if(rdfNode.getNodeType()!=Node.ELEMENT_NODE)
            throw new Exception("RDF XML node does not appear to be an element, corrupt file?");
        
        //parseFile(db, (Element)rdfNode);
        
        // Build a hashmap of all accounts keyed off the weird RDF hash.
        //
        // Now build the seq-lists. ***ORDER IS IMPORTANT*** and that is why
        // ArrayList objects are used.  Mozilla SEEMS to store this information in
        // order so that the higher-order nodes are earlier in the file. I'm banking
        // on that.
        //
        // Seqs seem to be Mozilla-RDFs method of storing hierarchical information. 
        // Any node that has a child, has a corresponding RDF:Seq node which 
        // holds a list of children as RDF:li.  Each of those RDF:li objects themselves
        // COULD be SEQs later in the file.
        readAndCreateMaps((Element)rdfNode, descriptionMap, seqMap, db);
        
        // Now build the account tree itself from this data
        createParentChildRelationships(db, descriptionMap, seqMap);
        
        // woot!
        return db;
    }

    /**
     * Internal routine which will build a HashMap<String, Account> of values
     * corresponding to every RDF:Description node it can find.
     * 
     * @param doc The XML document to parse.
     * @return a SIX DEMON BAG!
     * @throws Exception upon seeing things no one else can see, doing things
     *                   noone else can do!.
     */
    private void readAndCreateMaps(Element rdfElement, 
                           HashMap<String, Account> descriptionMap,
                           HashMap<String, ArrayList<String> > seqMap,
                           Database db)
            throws Exception
    {
        Node child = rdfElement.getFirstChild();
        
        while(child!=null) {
            if(child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element)child;
                
                String nodeName = child.getNodeName();
                String about;
                
                // NODE: Account
                if(nodeName.compareTo("RDF:Description")==0) {
                    Account account = null;
                    try {
                        // PasswordMaker stores it's settings in an RDF:Description node as well
                        // so we need to catch it and ignore it as it will result in a bad account.
                        about = childElement.getAttribute("RDF:about").trim();
                        logger.fine("RDF:Desc: " + about);
                        
                        if(about.startsWith("rdf:") || about.compareTo(Account.DEFAULT_ACCOUNT_URI)==0) {
                            account = readAccountFromDescriptionNode(childElement);
                            descriptionMap.put(account.getId(), account);
                        }
                        // The firefox global settings are also stored as a RDF:Description node. Save those
                        // for later if this RDF is ever re-imported into FF.
                        else if(about.compareTo(FF_GLOBAL_SETTINGS_URI)==0) {
                            NamedNodeMap nodeMap = childElement.getAttributes();
                            int mapLength = nodeMap.getLength();

                            for(int iAttribute=0; iAttribute < mapLength; iAttribute++) {
                                Node node = nodeMap.item(iAttribute);
                                if(node.getNodeName().startsWith("NS1:"))
                                    db.setGlobalSetting(node.getNodeName(), node.getNodeValue());
                            }
                        }
                    } catch(IncompatibleException e) {
                        // I'm not about to emulate the buggy javascript... so users can either
                        // ignore it or abort.
                        if(ignoreBuggyJavascript==true)
                            logger.warning(String.format("***Incompatibility[%1s,%2s]: %2s", ((Element)child).getAttribute("RDF:about"), ((Element)child).getAttribute("NS1:name"), e.getMessage()));
                        else
                            throw e;
                    }
                }
                // NODE: Hierarchy declaration
                else if(nodeName.compareTo("RDF:Seq")==0) {
                    String seqAbout = childElement.getAttribute("RDF:about");
                    if(seqAbout.startsWith("rdf:#") || seqAbout.compareTo(Account.ROOT_ACCOUNT_URI)==0) {
                        logger.fine("SEQ: " + seqAbout);
                        ArrayList<String> listItems = readSeqListItems(childElement);
                        if(listItems.size()>0) {
                            logger.fine("   = " + listItems.size() + " items read from " + seqAbout);
                            seqMap.put(seqAbout, listItems);
                        }
                    }
                }
            }
            
            child = child.getNextSibling();
        }
    }
    
    /**
     * Reads an RDF:Seq element's children (RDF:li) and builds up an array of the data.
     * @param node The RDF:Seq element to parse.
     * @return an ArrayList<String> of data.
     */
    private ArrayList<String> readSeqListItems(Element node) {
        ArrayList<String> items = new ArrayList<String>();
        
        Node childLi = node.getFirstChild();
        while(childLi!=null) {
            if(childLi.getNodeType()==Node.ELEMENT_NODE && childLi.getNodeName().compareTo("RDF:li")==0) {
                Element child = (Element)childLi;
                logger.fine("    LI: " + child.getAttribute("RDF:resource"));
                items.add(child.getAttribute("RDF:resource"));
            }
            childLi = childLi.getNextSibling();
        }
        return items;
    }

    
    /**
     * Reads the attributes contained with-in an RDF:Description node and
     * converts it all to an Account.
     * 
     * @param element The element (Node) to parse.
     * @return An Account.
     * @throws Exception if something goes wrong.
     */
    private Account readAccountFromDescriptionNode(Element element) 
            throws Exception 
    {
        Account account = new Account();
        
        // This is the hash
        account.setId(element.getAttribute("RDF:about").trim());
        account.setName(element.getAttribute("NS1:name").trim());
        account.setDesc(element.getAttribute("NS1:description").trim());

        // Groups only have a name, about, and description attribute.
        // If this is detected, mark it as a folder. Otherwise read
        // the full account data set.
        if(element.hasAttribute("NS1:hashAlgorithmLB")==false) {
            account.setIsFolder(true);
        }
        else {
            account.setLeetType(LeetType.fromRdfString(element.getAttribute("NS1:whereLeetLB").trim().toLowerCase()));
            account.setLeetLevel(LeetLevel.fromString(element.getAttribute("NS1:leetLevelLB").trim()));
            
            String algorithm = element.getAttribute("NS1:hashAlgorithmLB").trim().toLowerCase();
            account.setAlgorithm(AlgorithmType.fromRdfString(algorithm));
            account.setHmac(algorithm.contains("hmac-"));
            
            String passwordLength = element.getAttribute("NS1:passwordLength").trim();
            if(passwordLength.length()>0)
                account.setLength(Integer.parseInt(passwordLength));
            else
                account.setLength(Account.DEFAULT_LENGTH);
            
            account.setUsername(element.getAttribute("NS1:usernameTB").trim());
            account.setModifier(element.getAttribute("NS1:counter").trim());
            account.setCharacterSet(element.getAttribute("NS1:charset").trim());
            account.setPrefix(element.getAttribute("NS1:prefix").trim());
            account.setSuffix(element.getAttribute("NS1:suffix").trim());
            account.setAutoPop(element.getAttribute("NS1:autoPopulate").trim().compareTo("true")==0);

            // TODO: should preserve this, but I don't care much to
            // I'm fairly sure I don't need this. I think this stores the tab index
            // of the last time you edited this account.
            //String selectedTabIndex = element.getAttribute("NS1:selectedTabIndex").trim();

            account.setUrl(element.getAttribute("NS1:urlToUse").trim());
    
            // pattern info... I really hope nobody has more than 100000
            for(int iPattern = 0; iPattern < 100000; ++iPattern) {
                String pattern = element.getAttribute("NS1:pattern" + iPattern).trim();
                String patternType = element.getAttribute("NS1:patterntype" + iPattern).trim();
                String patternEnabled = element.getAttribute("NS1:patternenabled" + iPattern).trim();
                String patternDesc = element.getAttribute("NS1:patterndesc" + iPattern).trim();
                
                if(pattern.length()>0 || patternType.length()>0 || patternEnabled.length()>0 || patternDesc.length()>0) {
                    AccountPatternData data = new AccountPatternData();
                    data.setPattern(pattern);
                    data.setType(AccountPatternType.fromString(patternType));
                    data.setEnabled(patternEnabled.compareTo("true")==0);
                    data.setDesc(patternDesc);
                    account.getPatterns().add(data);
                } else {
                    iPattern = 999999;
                }
            }
        }
        
        return account;
    }
    
    /**
     * Iterates through the list of Seqs read in adding the parent node and 
     * then adding children which belong to it.
     * @param db The database to add nodes to.
     * @param descriptionMap The list of RDF:Description nodes.
     * @param seqMap The list of RDF:Seq nodes, which contain RDF:li nodes.
     * @throws Exception on unrecoverable error.
     */
    private void createParentChildRelationships(Database db,
                                  HashMap<String, Account> descriptionMap,
                                  HashMap<String, ArrayList<String> > seqMap)
       throws Exception {
        // List of ID's used to avoid recursion
        ArrayList<String> parentIdStack = new ArrayList<String>();

        // Verify the root node exists
        if(seqMap.containsKey(Account.ROOT_ACCOUNT_URI)==false)
            throw new Exception("File does not contain the root account, '" + Account.ROOT_ACCOUNT_URI + "'");
        parentIdStack.add(Account.ROOT_ACCOUNT_URI);
        
        // Until we run out of parent nodes...
        while(parentIdStack.size()>0) {
            String parentId = parentIdStack.get(0);
            Account parentAccount = descriptionMap.get(parentId);
            parentIdStack.remove(0);
            
            // Attempt to add the parent node if it's not the root. Root already exists
            // in the database by default.
            if(parentId.compareTo(Account.ROOT_ACCOUNT_URI)!=0) {
                if(parentAccount!=null) {
                    // If the parent node is not already in the db, add it
                    if(db.findAccountById(parentId)==null) {
                        Account parentParentAccount = db.findParent(parentAccount);
                        if(parentParentAccount==null) {
                            logger.warning("SeqNode[" + parentId + "] does not have a parent, will be dropped");
                            parentAccount = null;
                        }
                    }
                }
                else {
                    logger.warning("SeqNode[" + parentId + "] does not have a matching RDF:Description node, it will be dropped");
                }
            }
            else {
                parentAccount = db.getRootAccount();
            }
            
            // Now add the children
            if(parentAccount!=null) {
                for(String childId : seqMap.get(parentId)) {
                    Account childAccount = descriptionMap.get(childId);
                    if(childAccount!=null) {
                        if(parentAccount.hasChild(childAccount)==false) {
                            parentAccount.getChildren().add(childAccount);
                            
                            // If the child has children, add it to the parentIdStack for later processing, also mark
                            // it as a folder (which should have been done already based on it not having an algorithm.
                            if(seqMap.containsKey(childAccount.getId())) {
                                parentIdStack.add(childId);
                                childAccount.setIsFolder(true);
                            }
                        }
                        else {
                            logger.warning("Duplicate child '" + childId + "' found of parent '" + parentAccount.getId() + "'");
                        }
                    }
                    else {
                       logger.warning("Cannot find RDF:Description for '" + childId + "', it will be dropped");
                    }
                }
            }
        }
    }
    

    public String getExtension() {
        return RDFDatabaseReader.EXTENSION;
    }
    
}
