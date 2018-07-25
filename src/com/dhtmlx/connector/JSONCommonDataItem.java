package com.dhtmlx.connector;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONCommonDataItem extends DataItem {

	public JSONCommonDataItem(HashMap<String, String> data, DataConfig config,
			int index) {
		super(data, config, index);
	}

	@SuppressWarnings("unchecked")
	public void to_json(JSONArray output) {
		JSONObject record = new JSONObject();

		record.put("id", get_id());
		for (int i=0; i<config.data.size(); i++)
			record.put(config.data.get(i).name, get_value(config.data.get(i).name));

		userdata_to_json(record);
		output.add(record);
	}
	
}
