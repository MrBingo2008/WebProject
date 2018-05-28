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
material的dim改为comment，增加numPerBox
增加表product_material