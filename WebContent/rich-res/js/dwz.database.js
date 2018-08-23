/**
 * @author ZhangHuihua@msn.com
 */
(function($){
	var _lookup = {currentGroup:"", suffix:"", $target:null, pk:"id", onItemChange:null};
	var _util = {
		_lookupPrefix: function(key){
			var strDot = _lookup.currentGroup ? "." : "";
			return _lookup.currentGroup + strDot + key + _lookup.suffix;
		},
		lookupPk: function(key){
			return this._lookupPrefix(key);
		},
		lookupField: function(key){
			return this.lookupPk(key);
		}
	};
	
	//作为multiAddButton click事件的参数保存起来，bringback时可以用
	var $curTable = null;
	
	$.extend({
		bringBackSuggest: function(args){
			//这个地方应该可以改进，不需要查找那么多
			var $box = _lookup['$target'].parents(".unitBox:first");
			$box.find(":input, select, div").each(function(){
				var $input = $(this), inputName = $input.attr("name");
				
				for (var key in args) {
					var name = null;
					if(key.indexOf(":") == 0)
						name = key.substring(1, key.length);
					else
						name = (_lookup.pk == key) ? _util.lookupPk(key) : _util.lookupField(key);

					if (name == inputName) {
						
					    var tagName = ($input)[0].tagName;
					    
						if(tagName == "SELECT"){
							$input.prev().html($input.find("option[value='"+args[key]+"']").html());
							//$input.prev().html("test");
							//$input.attr("value", args[key]);
						}
						//2018-4-25: stone:增加change()是为了手动触发input的change事件，让plan自动生成items
						//$input.val(args[key]);
						if(tagName == "DIV"){
							$input.html(args[key]);
							$input.css({width:"auto", height:"auto"});
						}
						else
							$input.val(args[key]).change();
						break;
					}
				}
			});
			if(_lookup['onItemChange'] != null)
				eval(_lookup['onItemChange']);
		},
		bringBack: function(args){
			$.bringBackSuggest(args);
			$.pdialog.closeCurrent();
		}
	});
	
	$.fn.extend({
		lookup: function(){
			return this.each(function(){
				var $this = $(this), options = {mask:true, 
					width:$this.attr('width')||820, height:$this.attr('height')||400,
					maxable:eval($this.attr("maxable") || "true"),
					resizable:eval($this.attr("resizable") || "true")
				};
				$this.click(function(event){
					_lookup = $.extend(_lookup, {
						currentGroup: $this.attr("lookupGroup") || "",
						suffix: $this.attr("suffix") || "",
						$target: $this,
						pk: $this.attr("lookupPk") || "id",
						//added by stone
						onItemChange: $this.attr("onItemChange") || null
					});
					
					var url = unescape($this.attr("href")).replaceTmById($(event.target).parents(".unitBox:first"));
					if (!url.isFinishedTm()) {
						alertMsg.error($this.attr("warn") || DWZ.msg("alertSelectMsg"));
						return false;
					}
					
					$.pdialog.open(url, "_blank", $this.attr("title") || $this.text(), options);
					return false;
				});
			});
		},
		multLookup: function(){
			return this.each(function(){
				var $this = $(this), args={};
				$this.click(function(event){
					var $unitBox = $this.parents(".unitBox:first");
					$unitBox.find("[name='"+$this.attr("multLookup")+"']").filter(":checked").each(function(){
						var _args = DWZ.jsonEval($(this).val());
						for (var key in _args) {
							var lastKey = key;
							var keys = key.split(".");
							if(keys!=null && keys.length >= 1)
								lastKey = keys[keys.length-1];
							//modified by stone,写死了
							if(lastKey == "ids"){
								var value = args[key] ? args[key]+"," : "";
								args[key] = value + _args[key];
							}else if(lastKey =="infos"){
								var value = args[key] ? args[key]+"<br/>" : "";
								args[key] = value + _args[key];
							}
							//主要用于新增生产任务选择订单产品的时候
							else if(lastKey == "number"){
								if(_args[key] == "")
									_args[key] = "0";
								var value = args[key] ? args[key] : "0";
								args[key] = Number(value) + Number(_args[key]);
							}
							else
								args[key] = _args[key];
						}
					});

					if ($.isEmptyObject(args)) {
						alertMsg.error($this.attr("warn") || DWZ.msg("alertSelectMsg"));
						return false;
					}
					$.bringBack(args);
				});
			});
		},
		suggest: function(){
			var op = {suggest$:"#suggest", suggestShadow$: "#suggestShadow"};
			var selectedIndex = -1;
			return this.each(function(){
				var $input = $(this).attr('autocomplete', 'off').keydown(function(event){
					if (event.keyCode == DWZ.keyCode.ENTER && $(op.suggest$).is(':visible')) return false; //屏蔽回车提交
				});
				
				var suggestFields=$input.attr('suggestFields').split(",");
				
				function _show(event){
					var offset = $input.offset();
					var iTop = offset.top+this.offsetHeight;
					var $suggest = $(op.suggest$);
					if ($suggest.size() == 0) $suggest = $('<div id="suggest"></div>').appendTo($('body'));

					$suggest.css({
						left:offset.left+'px',
						top:iTop+'px'
					}).show();
					
					_lookup = $.extend(_lookup, {
						currentGroup: $input.attr("lookupGroup") || "",
						suffix: $input.attr("suffix") || "",
						$target: $input,
						pk: $input.attr("lookupPk") || "id"
					});

					var url = unescape($input.attr("suggestUrl")).replaceTmById($(event.target).parents(".unitBox:first"));
					if (!url.isFinishedTm()) {
						alertMsg.error($input.attr("warn") || DWZ.msg("alertSelectMsg"));
						return false;
					}
					
					var postData = {};
					postData[$input.attr("postField")||"inputValue"] = $input.val();

					$.ajax({
						global:false,
						type:'POST', dataType:"json", url:url, cache: false,
						data: postData,
						success: function(response){
							if (!response) return;
							var html = '';

							$.each(response, function(i){
								var liAttr = '', liLabel = '';
								
								for (var i=0; i<suggestFields.length; i++){
									var str = this[suggestFields[i]];
									if (str) {
										if (liLabel) liLabel += '-';
										liLabel += str;
									}
								}
								for (var key in this) {
									if (liAttr) liAttr += ',';
									liAttr += key+":'"+this[key]+"'";
								}
								html += '<li lookupAttrs="'+liAttr+'">' + liLabel + '</li>';
							});
							
							var $lis = $suggest.html('<ul>'+html+'</ul>').find("li");
							$lis.hoverClass("selected").click(function(){
								_select($(this));
							});
							if ($lis.size() == 1 && event.keyCode != DWZ.keyCode.BACKSPACE) {
								_select($lis.eq(0));
							} else if ($lis.size() == 0){
								var jsonStr = "";
								for (var i=0; i<suggestFields.length; i++){
									if (_util.lookupField(suggestFields[i]) == event.target.name) {
										break;
									}
									if (jsonStr) jsonStr += ',';
									jsonStr += suggestFields[i]+":''";
								}
								jsonStr = "{"+_lookup.pk+":''," + jsonStr +"}";
								$.bringBackSuggest(DWZ.jsonEval(jsonStr));
							}
						},
						error: function(){
							$suggest.html('');
						}
					});

					$(document).bind("click", _close);
					return false;
				}
				function _select($item){
					var jsonStr = "{"+ $item.attr('lookupAttrs') +"}";
					
					$.bringBackSuggest(DWZ.jsonEval(jsonStr));
				}
				function _close(){
					$(op.suggest$).html('').hide();
					selectedIndex = -1;
					$(document).unbind("click", _close);
				}
				
				$input.focus(_show).click(false).keyup(function(event){
					var $items = $(op.suggest$).find("li");
					switch(event.keyCode){
						case DWZ.keyCode.ESC:
						case DWZ.keyCode.TAB:
						case DWZ.keyCode.SHIFT:
						case DWZ.keyCode.HOME:
						case DWZ.keyCode.END:
						case DWZ.keyCode.LEFT:
						case DWZ.keyCode.RIGHT:
							break;
						case DWZ.keyCode.ENTER:
							_close();
							break;
						case DWZ.keyCode.DOWN:
							if (selectedIndex >= $items.size()-1) selectedIndex = -1;
							else selectedIndex++;
							break;
						case DWZ.keyCode.UP:
							if (selectedIndex < 0) selectedIndex = $items.size()-1;
							else selectedIndex--;
							break;
						default:
							_show(event);
					}
					$items.removeClass("selected");
					if (selectedIndex>=0) {
						var $item = $items.eq(selectedIndex).addClass("selected");
						_select($item);
					}
				});
			});
		},
		
		itemDetail: function(){
			return this.each(function(){
				var $table = $(this).css("clear","both"), $tbody = $table.find("tbody");
				var fields=[];

				$table.find("tr:first th[type]").each(function(i){
					var $th = $(this);
					var field = {
						type: $th.attr("type") || "text",
						patternDate: $th.attr("dateFmt") || "yyyy-MM-dd",
						name: $th.attr("name") || "",
						display:$th.attr("display")||"",
						defaultVal: $th.attr("defaultVal") || "",
						size: $th.attr("size") || "12",
						enumUrl: $th.attr("enumUrl") || "",
						lookupGroup: $th.attr("lookupGroup") || "",
						lookupUrl: $th.attr("lookupUrl") || "",
						lookupPk: $th.attr("lookupPk") || "id",
						suggestUrl: $th.attr("suggestUrl"),
						suggestFields: $th.attr("suggestFields"),
						postField: $th.attr("postField") || "",
						fieldClass: $th.attr("fieldClass") || "",
						fieldAttrs: $th.attr("fieldAttrs") || "",
						aFieldAttrs: $th.attr("aFieldAttrs") || "",
						lookupType: $th.attr("lookupType") || "",
						aTitle: $th.attr("aTitle") || "",
						onItemChange:$th.attr("onItemChange") || ""
					};
					fields.push(field);
				});
				
				$tbody.find("a.btnDel").click(function(){
					var $btnDel = $(this);
					
					if ($btnDel.is("[href^=javascript:]")){
						$btnDel.parents("tr:first").remove();
						initSuffix($tbody);
						return false;
					}
					
					function delDbData(){
						$.ajax({
							type:'POST', dataType:"json", url:$btnDel.attr('href'), cache: false,
							success: function(){
								$btnDel.parents("tr:first").remove();
								initSuffix($tbody);
							},
							error: DWZ.ajaxError
						});
					}
					
					if ($btnDel.attr("title")){
						alertMsg.confirm($btnDel.attr("title"), {okCall: delDbData});
					} else {
						delDbData();
					}
					
					return false;
				});

				var butDisabled = $table.attr('buttonDisabled');
				var butDisabledTxt = "";
				if(butDisabled ==null || butDisabled == "false"){
					var addButTxt = $table.attr('addButton');
					if (addButTxt) {

						//var $rowNum = $('<input type="text" name="dwz_rowNum" class="textInput" style="margin:2px;" value="1" size="2"/>').insertBefore($table);
						var $addBut = null;
						var buttonTop = $table.attr('buttonTop');
						if(buttonTop!=null && buttonTop=="true")
						    $addBut = $('<div class="button"><div class="buttonContent"><button type="button"'+butDisabledTxt+'>'+addButTxt+'</button></div></div>').insertAfter($table.parent().parent().find("span:first")).find("button");
						else
							$addBut = $('<div class="button"><div class="buttonContent"><button type="button"'+butDisabledTxt+'>'+addButTxt+'</button></div></div>').insertBefore($table).find("button");
						
						var trTm = "";
						$addBut.click(function(){
							if (! trTm) trTm = trHtml(fields);
							var rowNum = 1;
							//try{rowNum = parseInt($rowNum.val())} catch(e){}

							for (var i=0; i<rowNum; i++){
								var $tr = $(trTm);
								$tr.appendTo($tbody).initUI().find("a.btnDel").click(function(){
									$(this).parents("tr:first").remove();
									initSuffix($tbody);
									return false;
								});
							}
							initSuffix($tbody);
						});
					}
					
					var multiAddButTxt = $table.attr('multiAddButton');
					if(multiAddButTxt) {
						var $multiAddBut = $('<div class="button"><div class="buttonContent"><a href="'+$table.attr('multiAddUrl')+'" lookupGroup="">' + multiAddButTxt + '</a></div></div>').insertBefore($table).find("a");
						//var $multiAddBut = $('<a href="'+$table.attr('multiAddUrl')+'" lookupGroup="">' + multiAddButTxt + '</a>').insertBefore($table).find("a");
						//$multiAddBut.click(function(){
						//	$curTable = $table;
							//var $tableParent = $curTable.parent();
							//var $tableAddButton = $tableParent.find("button:first");
							//$tableAddButton.click();
						//});
					}
					
					var saveButTxt = $table.attr('saveButton');
					if (saveButTxt) {
						var $saveBut = $('<div class="button"><div class="buttonContent"><button type="submit" ' +butDisabledTxt+ '>'+saveButTxt+'</button></div></div>').insertBefore($table).find("button");
						var saveAction = $table.attr('saveAction');
						$saveBut.click(function(){
							//这里写死了好像不大好
							$("form").attr("action", saveAction);
							//$("#planForm").submit();
						});
					}
					
					var applyButTxt = $table.attr('applyButton');
					if (applyButTxt) {
						var $applyBut = $('<div class="button"><div class="buttonContent"><button type="submit" ' +butDisabledTxt+ '>'+applyButTxt+'</button></div></div>').insertBefore($table).find("button");
						var applyAction = $table.attr('applyAction');
						$applyBut.click(function(){
							$("form").attr("action", applyAction);	
						});
					}
					
				}

				var cancelButtonDisabled = $table.attr('cancelButtonDisabled');
				var cancelButtonDisabledTxt = "";
				if(cancelButtonDisabled ==null || cancelButtonDisabled == "false"){
					var cancelApplyButTxt = $table.attr('cancelApplyButton');
					if (cancelApplyButTxt) {
						var $cancelApplyBut = $('<div class="button"><div class="buttonContent"><button type="submit" ' +cancelButtonDisabledTxt+ '>'+cancelApplyButTxt+'</button></div></div>').insertBefore($table).find("button");
						var cancelApplyAction = $table.attr('cancelApplyAction');
						$cancelApplyBut.click(function(){
							$("form").attr("action", cancelApplyAction);	
						});
					}
				}
			});
			
			/**
			 * 删除时重新初始化下标
			 */
			function initSuffix($tbody) {
				//>选择器是匹配指定元素的一级子元素
				$tbody.find('>tr').each(function(i){
					$(':input, a.btnLook, a.btnAttach, span.label, div.lookupDiv', this).each(function(){
						var $this = $(this), name = $this.attr('name'), val = $this.val();
						var display = $this.attr('display');
						
						//这个地方可以获取到defaultVal吗，是如何做到的
						var defaultVal = $this.attr('defaultVal');
						
						if(display) 
							$this.html(display.replace('#index#', i+1));

						if (name) 
							$this.attr('name', name.replaceSuffix(i));
						
						var lookupGroup = $this.attr('lookupGroup');
						if (lookupGroup) {$this.attr('lookupGroup', lookupGroup.replaceSuffix(i));}
						
						var suffix = $this.attr("suffix");
						if (suffix) {$this.attr('suffix', suffix.replaceSuffix(i));}
						
						//modified by stone
						if (val && defaultVal && defaultVal.indexOf("#index#") >= 0) 
							$this.val(defaultVal.replace('#index#',i+1));					
						});
				});
			}
			
			function tdHtml(field){
				var html = '', suffix = '';
				
				if (field.name.endsWith("[#index#]")) suffix = "[#index#]";
				else if (field.name.endsWith("[]")) suffix = "[]";
				
				var suffixFrag = suffix ? ' suffix="' + suffix + '" ' : '';
				
				var attrFrag = '';
				if (field.fieldAttrs){
					var attrs = DWZ.jsonEval(field.fieldAttrs);
					for (var key in attrs) {
						attrFrag += key+'="'+attrs[key]+'"';
					}
				}
				var aAttrFrag = '';
				if (field.aFieldAttrs){
					var attrs = DWZ.jsonEval(field.aFieldAttrs);
					for (var key in attrs) {
						aAttrFrag += key+'="'+attrs[key]+'"';
					}
				}
				
				switch(field.type){
					case 'label':
						//这个display是为了给suffix使用
						html = '<span class="label" display="' + field.display + '">#index#</span>';
							//+'<input type="hidden" name="' + field.name + '" value="' + field.defaultVal + '">';
						break;
					case 'del':
						html = '<a href="javascript:void(0)" class="btnDel '+ field.fieldClass + '">删除</a>';
						break;
					case 'lookup':
						var suggestFrag = '';
						if (field.suggestFields) {
							suggestFrag = 'autocomplete="off" lookupGroup="'+field.lookupGroup+'"'+suffixFrag+' suggestUrl="'+field.suggestUrl+'" suggestFields="'+field.suggestFields+'"' + ' postField="'+field.postField+'"';
						}
						var temp = '<input type="text" name="'+field.name+'"'+suggestFrag+' lookupPk="'+field.lookupPk+'" size="'+field.size+'" class="'+field.fieldClass+'" '+attrFrag+'/>';
						if(field.lookupType == "div")
							temp = '<div name="'+field.name+'"'+suggestFrag+' lookupPk="'+field.lookupPk+'" class="'+field.fieldClass+'" '+attrFrag+'/>';
						html = '<input type="hidden" name="'+field.lookupGroup+'.'+field.lookupPk+suffix+'"/>'
							+ temp
							+ '<a class="btnLook" href="'+field.lookupUrl+'" lookupGroup="'+field.lookupGroup+'" '+suggestFrag+' lookupPk="'+field.lookupPk+'" onItemChange="'+ field.onItemChange +'" title="'+field.aTitle+'" '+aAttrFrag+'>'+field.aTitle+'</a>';
						break;
					case 'attach':
						html = '<input type="hidden" name="'+field.lookupGroup+'.'+field.lookupPk+suffix+'"/>'
							+ '<input type="text" name="'+field.name+'" size="'+field.size+'" readonly="readonly" class="'+field.fieldClass+'"/>'
							+ '<a class="btnAttach" href="'+field.lookupUrl+'" lookupGroup="'+field.lookupGroup+'" '+suffixFrag+' lookupPk="'+field.lookupPk+'" width="560" height="300" title="查找带回">查找带回</a>';
						break;
					case 'enum':
						//这里一定要加[0]，要再搞清楚
						html = $(field.enumUrl).attr("name",field.name)[0].outerHTML;
						break;
					case 'date':
						html = '<input type="text" name="'+field.name+'" value="'+field.defaultVal+'" class="date '+field.fieldClass+'" dateFmt="'+field.patternDate+'" size="'+field.size+'" '+attrFrag+'/>'
							+'<a class="inputDateButton" href="javascript:void(0)">选择</a>';
						break;
					default:
						html = '<input type="'+field.type+'" name="'+field.name+'" value="'+field.defaultVal+'" defaultVal="'+ field.defaultVal +'" size="'+field.size+'" class="'+field.fieldClass+'" '+attrFrag+'/>';
						break;
				}
				if(field.type=='hidden')
					return html;
				return '<td>'+html+'</td>';
			}
			function trHtml(fields){
				var html = '';
				$(fields).each(function(){
					html += tdHtml(this);
				});
				//stone: bring时，会通过这个unitBox来作为查找的范围，详见bringBack，那这样就无法自动填充客户
				//return '<tr class="unitBox">'+html+'</tr>';
				return '<tr>'+html+'</tr>';
			}
		},
		
		selectedTodo: function(){
			
			function _getIds(selectedIds, targetType){
				var ids = "";
				var $box = targetType == "dialog" ? $.pdialog.getCurrent() : navTab.getCurrentPanel();
				$box.find("input:checked").filter("[name='"+selectedIds+"']").each(function(i){
					var val = $(this).val();
					ids += i==0 ? val : ","+val;
				});
				return ids;
			}
			return this.each(function(){
				var $this = $(this);
				var selectedIds = $this.attr("rel") || "ids";
				var postType = $this.attr("postType") || "map";

				$this.click(function(){
					var targetType = $this.attr("targetType");
					var ids = _getIds(selectedIds, targetType);
					if (!ids) {
						alertMsg.error($this.attr("warn") || DWZ.msg("alertSelectMsg"));
						return false;
					}
					
					var _callback = $this.attr("callback") || (targetType == "dialog" ? dialogAjaxDone : navTabAjaxDone);
					if (! $.isFunction(_callback)) _callback = eval('(' + _callback + ')');
					
					function _doPost(){
						$.ajax({
							type:'POST', url:$this.attr('href'), dataType:'json', cache: false,
							data: function(){
								if (postType == 'map'){
									return $.map(ids.split(','), function(val, i) {
										return {name: selectedIds, value: val};
									})
								} else {
									var _data = {};
									_data[selectedIds] = ids;
									return _data;
								}
							}(),
							success: _callback,
							error: DWZ.ajaxError
						});
					}
					var title = $this.attr("title");
					if (title) {
						alertMsg.confirm(title, {okCall: _doPost});
					} else {
						_doPost();
					}
					return false;
				});
				
			});
		}
	});
})(jQuery);

