package com.dhtmlx.connector;

import java.util.HashMap;

import org.json.simple.JSONObject;

public class JSONTreeCommonDataItem extends TreeCommonDataItem {

	public JSONTreeCommonDataItem(HashMap<String, String> data,
			DataConfig config, int index) {
		super(data, config, index);
	}
	
	public JSONObject to_xml_start(){
		if (skip) return null;
		
		JSONObject data = new JSONObject();
		data.put("id", get_id());

		for (int i = 0; i < config.text.size(); i++) {
			String extra = config.text.get(i).name;
			data.put(extra, this.data.get(extra));
		}
		if (has_kids()>0)
			data.put("dhx_kids", "1");

		userdata_to_json(data);
		return data;
	}

	public String to_xml_end(){
		return "";
	}
	
}
