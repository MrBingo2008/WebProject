2018-4-17：
增加order表的deadline_time
增加step的is_surface，char(1)
增加material表的surface_id
增加order表的sell_order_id
增加record表的surface_id
增加flow表的surface_id和record_id
增加process_step表的step_id, update process_step, step set process_step.step_id = step.id where process_step.step_name = step.name 
增加plan_step表的step_id, update plan_step, step set plan_step.step_id = step.id where plan_step.step_name = step.name

2018-4-25：
权限：
原来lib detail没有加权限控制，现在加了v_material
sell order detail等操作增加v_step_list.do
material detail增加v_step_list.do

role:sellOut、purchaseIn、plan.manage等角色加了v_record

2018-4-28：
更新版本

2018-5-8：
增加raw_batch_flow表的plan_step_id

2015-5-11：
获取光明数据库，修改数据

2018-5-13：
更新版本

2018-5-25：
增加material_image表

2018-5-29：
material的dim改为comment，增加num_per_box
增加表product_material

2018-5-31：
更新版本，修复中文查询编辑问题，更新2018-5-29修改的表

2018-6-25：
更新版本：
增加表ord_record_record，从ord的sell_record_id里找出记录，增加到ord_record_record里，删除ord的sell_record_id
增加record的deadline：update ord_record, ord set ord_record.deadline_time = ord.deadline_time where ord_record.ord_id = ord.id
增加表plan_record，从plan的rec_id里找出记录，增加到plan_record里，删除plan的rec_id

2018-7-2：
增加material_attach表格

2018-7-23：
更新版本，不过material_attach的size double好像没有设定长度

2018-8-25：
material表，新建process_id，指向process id
process表的category_id改为允许空
删除已有的process

2018-10-15：
新增表格plan_step_number
表格plan_step新增notArriveNumber
表格plan_step的number和notArriveNumber设为非空
