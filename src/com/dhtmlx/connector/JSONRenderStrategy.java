package com.dhtmlx.connector;

import java.util.HashMap;

import org.json.simple.JSONArray;

public class JSONRenderStrategy extends RenderStrategy {

	@Override
	protected String render_set(ConnectorResultSet result, BaseFactory cfactory, Boolean dload, String sep, DataConfig config)
			throws ConnectorOperationException {
		JSONArray output = new JSONArray();
		int index = 0;
		HashMap<String,String> values;
		while ( (values = result.get_next()) != null){
			DataItem data = (DataItem)cfactory.createDataItem(values, conn.config, index);
			if (data.get_id()==null)
				data.set_id(conn.uuid());
			
			conn.event.trigger().beforeRender(data);
			data.to_json(output);
			index++;
		}
		return output.toString();
	}

}
