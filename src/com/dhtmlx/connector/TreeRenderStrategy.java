package com.dhtmlx.connector;

import java.util.HashMap;

public class TreeRenderStrategy extends RenderStrategy {

	public void init(BaseConnector c) {
		conn = c;
		conn.event.attach(new TreeGridBehavior(conn.config));
	}

	/* (non-Javadoc)
	 * @see com.dhtmlx.connector.BaseConnector#render_set(com.dhtmlx.connector.ConnectorResultSet)
	 */
	@Override
	protected String render_set(ConnectorResultSet result, BaseFactory cfactory, Boolean dload, String sep, DataConfig config)
			throws ConnectorOperationException {
		
		StringBuffer output = new StringBuffer();
		int index = 0;
		HashMap<String,String> values;
		while ( (values = result.get_next()) != null){
			DataItem data = cfactory.createDataItem(values, conn.config, index);
			if (data.get_id()==null)
				data.set_id(conn.uuid());
			
			conn.event.trigger().beforeRender(data);
			
			if (data.has_kids()==-1 && dload)
				data.set_kids(1);
			
			data.to_xml_start(output);
			
			if (data.has_kids()==-1 || ( data.has_kids()!=0 && !dload)){
				DataRequest sub_request = new DataRequest(conn.request);
				sub_request.set_relation(data.get_id());
				
				//commented by stone 
				output.append(render_set(conn.sql.select(sub_request), cfactory, dload, sep, config));
			}
		
			data.to_xml_end(output);
			index++;
		}
		return output.toString();
	}

}
