package com.dhtmlx.connector;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;

public class JSONTreeRenderStrategy extends TreeRenderStrategy {

	/* (non-Javadoc)
	 * @see com.dhtmlx.connector.BaseConnector#render_set(com.dhtmlx.connector.ConnectorResultSet)
	 */
	protected ArrayList<JSONObject> render_json(ConnectorResultSet result, Boolean dload)
			throws ConnectorOperationException {
		ArrayList<JSONObject> output = new ArrayList<JSONObject>();
		int index = 0;
		HashMap<String,String> values;
		while ( (values = result.get_next()) != null){
			JSONTreeCommonDataItem data = (JSONTreeCommonDataItem) conn.cfactory.createDataItem(values, conn.config, index);
			if (data.get_id()==null) data.set_id(conn.uuid());
			
			conn.event.trigger().beforeRender(data);
			
			if (data.has_kids()==-1 && dload)
				data.set_kids(1);

			JSONObject record = data.to_xml_start();

			if (data.has_kids()==-1 || ( data.has_kids()!=0 && !dload)){
				DataRequest sub_request = new DataRequest(conn.request);
				sub_request.set_relation(data.get_id());
				ArrayList<JSONObject> temp = render_json(conn.sql.select(sub_request), dload);
				if (temp.size() > 0) record.put("data", temp);
			}
			output.add(record);

			data.to_xml_end(new StringBuffer());
			index++;
		}
		return output;
	}
	
}
