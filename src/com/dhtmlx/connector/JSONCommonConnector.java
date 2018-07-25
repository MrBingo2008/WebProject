package com.dhtmlx.connector;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.json.simple.JSONObject;

public class JSONCommonConnector extends CommonConnector {
	
	/** The extra info , which need to be mixed in output */
	protected StringBuffer extra_output = new StringBuffer();
	
	/** The collections of options */
	protected HashMap<String,BaseConnector> options = new HashMap<String,BaseConnector>();

	/**
	 * Instantiates a new scheduler connector.
	 * 
	 * @param db the db connection
	 */
	public JSONCommonConnector(Connection db){
		this(db,DBType.Custom);
	}

	/**
	 * Instantiates a new scheduler connector.
	 * 
	 * @param db the db connection
	 * @param db_type the db type
	 */
	public JSONCommonConnector(Connection db, DBType db_type){
		this(db,db_type, new JSONCommonFactory());
	}
	
	/**
	 * Instantiates a new scheduler connector.
	 * 
	 * @param db the db connection
	 * @param db_type the db type
	 * @param a_factory the class factory, which will be used by object
	 */
	public JSONCommonConnector(Connection db, DBType db_type, BaseFactory a_factory){
		this(db,db_type,a_factory,a_factory.createRenderStrategy());
	}


	/**
	 * Instantiates a new scheduler connector.
	 * 
	 * @param db the db connection
	 * @param db_type the db type
	 * @param a_factory the class factory, which will be used by object
	 */
	public JSONCommonConnector(Connection db, DBType db_type, BaseFactory a_factory, RenderStrategy render_type){
		super(db,db_type,a_factory,render_type);
	}


	protected String output_as_xml(ConnectorResultSet result) throws ConnectorOperationException{
		String start = "";
		String end = xml_start() + render_set(result) + xml_end();
		Boolean is_sections = (sections!=null && sections.size() > 0) && is_first_call();
		if (dynloading || is_sections) {
			start = start + end;
			end="";

			if (is_sections){
				//extra sections
				for (Object key : sections.keySet()) {
				    end += ", \"" + key.toString() + "\":" + sections.get(key).toString();
				}
			}

			if (dynloading){
				//info for dyn. loadin
				String pos=request.get_start();
				if (pos != null && !pos.equals("0"))
					add_top_attribute("pos", pos);
				else {
					add_top_attribute("pos", "0");
					add_top_attribute("total_count", sql.get_size(request));
				}
			}
			end += top_attributes_json();
		}
		end += " }";

		ConnectorOutputWriter out = new ConnectorOutputWriter(start, end);
		output_as_xml(out.toString());
		return out.toString();
	}

	protected String xml_start(){ return "{ \"data\":\n"; }
	protected String xml_end() throws ConnectorOperationException{
		fill_collections();
		return extra_output.toString();
	}

	public void output_as_xml(String data){
 		http_response.reset();
		http_response.addHeader("Content-type", "text/javascript;charset=" + encoding);
		
		try {
			java.io.Writer out = http_response.getWriter();
			out.write(data);
			out.close();
			http_response.flushBuffer();
		} catch (IOException e){
			LogManager.getInstance().log("Error during data outputing");
			LogManager.getInstance().log(e.getMessage());
		}
		end_run();
	}
	
	
	/**
	 * Fill collections of options with data from DB
	 */
	protected void fill_collections(){
		ArrayList<String> result = new ArrayList<String>();
		Iterator<String> key = options.keySet().iterator();
		while (key.hasNext()){
			String name = key.next();
			BaseConnector option_connector = options.get(name);
			String data = option_connector.render();
			result.add("\"" + name + "\":" + data);
		}
		String collections = ConnectorUtils.join(result.toArray(), ", ");
	    if (collections.length() > 0)
	    	collections = ",\"collections\": {" + collections + "}";

	    extra_output.append(collections);
	}


	/**
	 * Define connector for options retrieving 
	 * 
	 * @param name the name of column
	 * @param connector the connector
	 */
	public void set_options(String name, BaseConnector connector){
		options.put(name,connector);
	}
	
	/**
	 * Define fixed list of options
	 * 
	 * @param name the name of column
	 * @param object the iterable object ( array ) 
	 */
	@SuppressWarnings("unchecked")
	public void set_options(String name, Iterable object){
		Iterator it = object.iterator();
		StringBuffer data = new StringBuffer();
		
		while (it.hasNext()){
			String value = it.next().toString();
			data.append("{\"value\":\""+value+"\", \"value\":\""+value+"\"}");
		}
		set_options(name,new DummyStringConnector(data.toString()));
	}
	
	/**
	 * Define fixed list of options
	 * 
	 * @param name the name of column
	 * @param object the hash object
	 */
	@SuppressWarnings("unchecked")
	public void set_options(String name, HashMap object){
		Iterator it = object.keySet().iterator();
		StringBuffer data = new StringBuffer();
		
		while (it.hasNext()){
			Object value = it.next();
			Object label = object.get(value).toString();
			data.append("{\"value\":\""+value.toString()+"\", \"value\":\""+label.toString()+"\"}");
		}
		set_options(name,new DummyStringConnector(data.toString()));
	}
	
	public void add_section(String name, String value) {
		super.add_section(name, "\"" + value + "\"");
	}
	
	public void add_section(String name, JSONObject value) {
		super.add_section(name, value.toString());
	}
}
