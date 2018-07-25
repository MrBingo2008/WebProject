package com.dhtmlx.connector;

import java.util.HashMap;

public class RenderStrategy {

	protected BaseConnector conn = null;
	protected Integer level = 0;
	protected Integer max_level = -1;
	protected String id_postfix = "__{group_param}";
	
	public void init(BaseConnector c) {
		conn = c;
	}
	
	/**
	 * Render DB result set as XML string
	 * @param result - the DB result
	 * @param cfactory - the connector factory object
	 * @param dload - dynloading flag
	 * @param sep - items separator
	 * @return the xml string
	 * @throws ConnectorOperationException the connector operation exception 
	 */
	protected String render_set(ConnectorResultSet result, BaseFactory cfactory, Boolean dload, String sep, DataConfig config)
	throws ConnectorOperationException{
		StringBuffer output = new StringBuffer();
		int index = 0;
		HashMap<String,String> values;
		while ( (values = result.get_next()) != null){
			DataItem data = cfactory.createDataItem(values, conn.config, index);
			if (data.get_id()==null)
				data.set_id(conn.uuid());

			conn.event.trigger().beforeRender(data);
			data.to_xml(output);
			output.append(sep);
			index++;
		}
		return output.toString();
	}

	protected String parse_id(String id) {
		return parse_id(id, true);
	}

	protected String parse_id(String id, Boolean set_level) {
		String[] parts = id.split("%23|#");
		Integer level;
		if (parts.length == 2) {
			level = Integer.parseInt(parts[0]) + 1;
			id = parts[1];
		} else {
			level = 0;
			id = "";
		}
		if (set_level) this.level = level;
		return id;
	}

	/*! set maximum level of tree
	 * @param max_level
	 * 		maximum level
	 */
	public void set_max_level(int max) {
		max_level = max;
	}
	
	protected Integer get_level() {
		if (level > 0) return level;
		String parent = conn.http_request.getParameter(conn.parent_name);
		if (parent==null) {
			String ids = conn.http_request.getParameter("ids");
			if (ids!=null) {
				String[] aids = ids.split(",");
				if (aids.length > 0) {
					parse_id(aids[0]);
					level--;
				}
			}
			conn.request.set_relation("");
		} else {
			String id = parse_id(parent);
		}
		return level;
	}
	
	protected String level_id(String id) {
		return level_id(id, level);
	}

	protected String level_id(String id, Integer level) {
		return level.toString() + "%23" + id;
	}

	public String get_postfix() {
		return id_postfix;
	}
}
