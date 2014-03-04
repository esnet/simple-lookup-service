package net.es.lookup.database;


import com.mongodb.*;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.common.exception.internal.DuplicateEntryException;

import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Pattern;

public class ServiceDAOMongoDb {

    private String dburl = "127.0.0.1";
    private int dbport = 27017;
    private String dbname = "LookupService";
    private String collname = "services";

    private Mongo mongo;
    private DB db;
    private DBCollection coll;

    private static Map<String, String> operatorMapping = new HashMap();
    private static Map<String, String> listOperatorMapping = new HashMap();

    {
        operatorMapping.put(ReservedValues.RECORD_OPERATOR_ALL, "$and");
        operatorMapping.put(ReservedValues.RECORD_OPERATOR_ANY, "$or");

        listOperatorMapping.put(ReservedValues.RECORD_OPERATOR_ANY, "$in");
        listOperatorMapping.put(ReservedValues.RECORD_OPERATOR_ALL, "$all");

    }

    //retrieves the db and collection(table); creates a new one if it cannot find one
    public ServiceDAOMongoDb(String dburl, int dbport, String dbname, String collname) throws DatabaseException {

        this.dburl = dburl;
        this.dbport = dbport;
        this.dbname = dbname;
        this.collname = collname;
        init();

    }


    private void init() throws DatabaseException {

        if (DBMapping.containsKey(dbname)) {

            // An instance has been already created.
            throw new DatabaseException("Attempt to create a second instance of ServiceDAOMongoDb");

        }

        DBMapping.addDb(dbname, this);

        try {

            mongo = new Mongo(dburl, dbport);
            db = mongo.getDB(dbname);
            coll = db.getCollection(collname);
            coll.getCount();

        } catch (UnknownHostException e) {

            throw new DatabaseException(e.getMessage());

        } catch (Exception e) {

            throw new DatabaseException(e.getMessage());

        }


    }


    //should use json specific register requestUrl and response.
    public Message queryAndPublishService(Message message, Message queryRequest, Message operators) throws DatabaseException, DuplicateEntryException {

        Message response;

        //check for duplicates
        try {

            List<Message> dupEntries = this.query(message, queryRequest, operators);
            //System.out.println("Duplicate Entries: "+dupEntries.size());
            if (dupEntries.size() > 0) {

                throw new DuplicateEntryException("Record already exists");

            }

        } catch (DatabaseException e) {

            throw new DatabaseException("Error inserting record");

        }

        Map<String, Object> services = message.getMap();
        BasicDBObject doc = new BasicDBObject();
        doc.putAll(services);
        WriteResult wrt = coll.insert(doc);
        CommandResult cmdres = wrt.getLastError();

        if (!cmdres.ok()) {

            throw new DatabaseException("Error inserting record");

        }

        response = new Message(services);
        return response;

    }


    public Message deleteService(String serviceid) throws DatabaseException {

        Message response = new Message();
        BasicDBObject query = new BasicDBObject();
        //TODO: add check to see if only one elem is returned
        query.put(ReservedKeys.RECORD_URI, serviceid);
        response = getServiceByURI(serviceid);

        try {

            WriteResult wrt = coll.remove(query);

            CommandResult cmdres = wrt.getLastError();

            if (!cmdres.ok()) {

                throw new DatabaseException(cmdres.getErrorMessage());

            }

        } catch (MongoException e) {

            throw new DatabaseException(e.getMessage());

        }

        return response;

    }


    public Message updateService(String serviceid, Message updateRequest) throws DatabaseException {

        Message response = new Message();

        if (serviceid != null && !serviceid.isEmpty()) {

            BasicDBObject query = new BasicDBObject();
            query.put(ReservedKeys.RECORD_URI, serviceid);

            BasicDBObject updateObject = new BasicDBObject();
            updateObject.putAll(updateRequest.getMap());

            try {

                WriteResult wrt = coll.update(query, updateObject);
                CommandResult cmdres = wrt.getLastError();

                if (cmdres.ok()) {

                    response = (Message) getServiceByURI(serviceid);

                } else {

                    throw new DatabaseException(cmdres.getErrorMessage());

                }

            } catch (MongoException e) {

                throw new DatabaseException(e.getMessage());

            }

        } else {

            throw new DatabaseException("Record URI not specified!!!");

        }

        return response;

    }

    public List<Message> query(Message message, Message queryRequest, Message operators) throws DatabaseException {

        return this.query(message, queryRequest, operators, 0, 0);

    }

    public List<Message> query(Message message, Message queryRequest, Message operators, int maxResults, int skip) throws DatabaseException {

        BasicDBObject query = buildQuery(queryRequest, operators);

        ArrayList<Message> result = new ArrayList<Message>();

        try {

            DBCursor cur = coll.find(query);

            while (cur.hasNext()) {

                Message tmpserv = new Message();
                DBObject tmp = cur.next();
                Set<String> keys = tmp.keySet();

                if (!keys.isEmpty()) {

                    Iterator<String> it = keys.iterator();

                    while (it.hasNext()) {

                        String tmpKey = it.next();
                        //remove key added by mongodb

                        if (!tmpKey.equals("_id")) {

                            tmpserv.add(tmpKey, tmp.get(tmpKey));

                        }

                    }

                }

                result.add(tmpserv);

            }

        } catch (MongoException e) {

            throw new DatabaseException("Error retrieving results");

        }

        return result;

    }


    public List<Message> queryAll() throws DatabaseException {

        Message msg = new Message();
        List<Message> result = query(msg, msg, msg);
        return result;

    }


    //Builds the query from the given map
    private BasicDBObject buildQuery(Message queryRequest, Message operators) {

        Map<String, Object> serv = queryRequest.getMap();

        Map<String, String> ops = operators.getMap();

        List<HashMap<String, Object>> keyValueList = new ArrayList<HashMap<String, Object>>();

        for (Map.Entry<String, Object> entry : serv.entrySet()) {

            String newKey = entry.getKey();
            HashMap<String, Object> tmpHash = new HashMap<String, Object>();
            Object obj = serv.get(newKey);

            if (obj instanceof String) {

                String val = (String) obj;
                //deal with metacharacter
                if (val.endsWith("*")) {

                    val = val.substring(0, val.length() - 1);
                    //System.out.println(val);
                    Pattern newVal = Pattern.compile("^" + val);
                    tmpHash.put(newKey, newVal);

                } else if (val.startsWith("*")) {

                    val = val.substring(1, val.length());
                    //System.out.println(val);
                    Pattern newVal = Pattern.compile(val + "$");
                    tmpHash.put(newKey, newVal);

                } else {

                    tmpHash.put(newKey, (String) obj);

                }

            } else if (obj instanceof List) {

                List<Object> values = (List<Object>) obj;
                ArrayList newValues = new ArrayList();

                if (values.size() > 1) {

                    for (int i = 0; i < values.size(); i++) {

                        String val = (String) values.get(i);

                        if (val.endsWith("*")) {

                            val = val.substring(0, val.length() - 1);
                            //System.out.println(val);
                            Pattern newVal = Pattern.compile("^" + val);
                            newValues.add(newVal);

                        } else if (val.startsWith("*")) {

                            val = val.substring(1, val.length());
                            //System.out.println(val);
                            Pattern newVal = Pattern.compile(val + "$");
                            newValues.add(newVal);

                        } else {

                            newValues.add(val);

                        }

                    }

                    HashMap<String, Object> listvalues = new HashMap<String, Object>();

                    if (ops.containsKey(newKey) && listOperatorMapping.containsKey(ops.get(newKey))) {

                        //get the operator
                        String curop = listOperatorMapping.get(ops.get(newKey));
                        listvalues.put(curop, newValues);
                        tmpHash.put(newKey, listvalues);

                    } else {

                        tmpHash.put(newKey, newValues);

                    }

                } else if (values.size() == 1) {

                    String val = (String) values.get(0);
                    if (val.endsWith("*")) {

                        val = val.substring(0, val.length() - 1);
                        Pattern newVal = Pattern.compile("^" + val);
                        tmpHash.put(newKey, newVal);

                    } else if (val.startsWith("*")) {

                        val = val.substring(1, val.length());
                        //System.out.println(val);
                        Pattern newVal = Pattern.compile(val + "$");
                        newValues.add(newVal);

                    } else {

                        tmpHash.put(newKey, values.get(0));

                    }

                }

            }

            if (!tmpHash.isEmpty()) {

                keyValueList.add(tmpHash);

            }

        }

        BasicDBObject query = new BasicDBObject();
        ArrayList queryOp = (ArrayList) operators.getOperator();
        String op = null;

        if (queryOp != null && !queryOp.isEmpty()) {

            op = (String) queryOp.get(0);        //uses only the first value from the list

        } else {

            op = ReservedValues.RECORD_OPERATOR_DEFAULT;

        }

        String mongoOp = "";

        if (operatorMapping.containsKey(op)) {

            mongoOp = operatorMapping.get(op);

        }

        if (!keyValueList.isEmpty()) {

            query.put(mongoOp, keyValueList);

        }

        return query;

    }


    public Message getServiceByURI(String URI) throws DatabaseException {

        BasicDBObject query = new BasicDBObject();
        query.put(ReservedKeys.RECORD_URI, URI);
        Message result = null;

        try {

            DBCursor cur = coll.find(query);

            if (cur.size() == 1) {

                DBObject tmp = cur.next();
                Map<String, Object> tmpMap = tmp.toMap();
                tmpMap.remove("_id");
                result = new Message(tmpMap);

            }

        } catch (MongoException e) {

            throw new DatabaseException(e.getMessage());

        }

        return result;

    }


}