2018-4-17��
����order���deadline_time
����step��is_surface��char(1)
����material���surface_id
����order���sell_order_id
����record���surface_id
����flow���surface_id��record_id
����process_step���step_id, update process_step, step set process_step.step_id = step.id where process_step.step_name = step.name 
����plan_step���step_id, update plan_step, step set plan_step.step_id = step.id where plan_step.step_name = step.name

2018-4-25��
Ȩ�ޣ�
ԭ��lib detailû�м�Ȩ�޿��ƣ����ڼ���v_material
sell order detail�Ȳ�������v_step_list.do
material detail����v_step_list.do

role:sellOut��purchaseIn��plan.manage�Ƚ�ɫ����v_record

2018-4-28��
���°汾

2018-5-8��
����raw_batch_flow���plan_step_id

2015-5-11��
��ȡ�������ݿ⣬�޸�����

2018-5-13��
���°汾

2018-5-25��
����material_image��

2018-5-29��
material��dim��Ϊcomment������num_per_box
���ӱ�product_material

2018-5-31��
���°汾���޸����Ĳ�ѯ�༭���⣬����2018-5-29�޸ĵı�

2018-6-25��
���°汾��
���ӱ�ord_record_record����ord��sell_record_id���ҳ���¼�����ӵ�ord_record_record�ɾ��ord��sell_record_id
����record��deadline��update ord_record, ord set ord_record.deadline_time = ord.deadline_time where ord_record.ord_id = ord.id
���ӱ�plan_record����plan��rec_id���ҳ���¼�����ӵ�plan_record�ɾ��plan��rec_id

2018-7-2��
����material_attach���

2018-7-23��
���°汾������material_attach��size double����û���趨����

2018-8-25��
material���½�process_id��ָ��process id
process���category_id��Ϊ�����
ɾ�����е�process

2018-10-15��
�������plan_step_number
���plan_step����notArriveNumber
���plan_step��number��notArriveNumber��Ϊ�ǿ�
