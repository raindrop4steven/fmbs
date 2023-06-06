package com.weaver;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import net.sf.jsqlparser.util.deparser.SelectDeParser;

public class SimpleSqlParser24 {

    public static void main(String args[]) throws JSQLParserException {
//        replaceTableName("select id, test from test where name = \"test\"");
//        replaceTableName("select * from t2 join t1 on t1.aa = t2.bb where t1.a = \"someCondition\" limit 10");
        replaceTableName("select sum(t1.score), t1.name, t2.age from uf_lw_user t1 inner join uf_lw_user_0 t2 on t1.id = t2.id where" +
                " t1.name like '%steven%' and t2.age > 32 group by t2.age order by t1.name asc");
//        replaceTableName("select cast(form_table_data.id as char ) as id,cast(form_table_data.id as char ) as rowKey,cast(form_table_data.form_data_id as char ) as form_data_id,cast(form_table_data.id as char ) as form_table_id,form_table_data.data_status,form_table_data.data_status as data_status_value,form_table_data.is_flow,form_table_data.flow_status,form_table_data.create_time,form_table_data.dxwb_4e7y as dxwb_4e7y,form_table_data.ryxz_14ul as ryxz_14ul,form_table_data.tp_53w0 as tp_53w0,form_table_data.fj_ht80 as fj_ht80 from uf_cjj_pdf_wuli form_table_data where form_table_data.IS_DELETE = 0 and form_table_data.tenant_key='t2k74i3rjo' and ( (form_table_data.id in (SELECT DISTINCT id FROM uf_cjj_pdf_wuli form_table_data WHERE form_table_data.is_flow = '0' AND form_table_data.data_status = '1' AND form_table_data.tenant_key = 't2k74i3rjo' AND form_table_data.is_delete = 0 AND 1 = 1 AND ( form_table_data.id in ( select wpws.source_id from ebdf_data_ph_ebdf43291 wpws where wpws.tenant_key = 't2k74i3rjo' and ( wpws.delete_type = 0 OR wpws.delete_type IS NULL ) and wpws.source_type = 1 and permission_config_id in (select id from ( select 10000 as id union all select t1.id from ebdf_mt_permission t1 where t1.belong_id = 819595449323970561 union all select t2.id from ebdf_permission t2 where t2.belong_id = 819595449323970561 ) t) and wpws.permission_type = 1 AND ( (wpws.target_id in (6442997916844032,6442998487269377,6558987369459713,775810392359714816) and wpws.target_type = 1 ) OR ( wpws.target_type = 11 and wpws.tenant_key = 't2k74i3rjo' and wpws.target_id = 0 and wpws.target_min_sec_level <= 90 and wpws.target_max_sec_level >= 90 ) OR (wpws.target_id in (775810392359714816,6442997916844032,6442998487269377,6558987369459713) and wpws.target_type = 17 ) )) OR EXISTS ( select wpws.target_id from ebdf_data_ph_ebdf43291 wpws where wpws.tenant_key = 't2k74i3rjo' and ( wpws.delete_type = 0 OR wpws.delete_type IS NULL ) and wpws.source_type = -21 and permission_config_id in (select id from ( select 10000 as id union all select t1.id from ebdf_mt_permission t1 where t1.belong_id = 819595449323970561 union all select t2.id from ebdf_permission t2 where t2.belong_id = 819595449323970561 ) t) and wpws.permission_type = 1 AND ( (wpws.target_id in (6442997916844032,6442998487269377,6558987369459713,775810392359714816) and wpws.target_type = 1 ) OR ( wpws.target_type = 11 and wpws.tenant_key = 't2k74i3rjo' and wpws.target_id = 0 and wpws.target_min_sec_level <= 90 and wpws.target_max_sec_level >= 90 ) OR (wpws.target_id in (775810392359714816,6442997916844032,6442998487269377,6558987369459713) and wpws.target_type = 17 ) ) ) ) )) or (((form_table_data.data_status ='0' and form_table_data.is_flow='0') or (form_table_data.flow_status ='0' and form_table_data.is_flow='1')) and form_table_data.creator in (775810392359714816,6442997916844032,6442998487269377,6558987369459713)) ) order by form_table_data.create_time desc,form_table_data.id desc LIMIT 10");
    }

    private static void replaceTableName(String sql) throws JSQLParserException {
        Select select = (Select) CCJSqlParserUtil.parse(sql);

        StringBuilder buffer = new StringBuilder();
        ExpressionDeParser expressionDeParser = new ExpressionDeParser() {
            @Override
            public void visit(Column tableColumn) {
                if (tableColumn.getTable() != null) {
//                    tableColumn.getTable().setName(tableColumn.getTable().getName() + "_mytest");
                    System.out.println("TableColumn:" + tableColumn.getTable().getName());
                }
                super.visit(tableColumn);
            }
        };
        SelectDeParser deparser = new SelectDeParser(expressionDeParser, buffer) {
            @Override
            public void visit(Table tableName) {
//                tableName.setName(tableName.getName() + "_mytest");
                System.out.println("TableName: " + tableName.getName() + ", alias:" + tableName.getAlias().getName());
                super.visit(tableName);
            }
        };

        expressionDeParser.setSelectVisitor(deparser);
        expressionDeParser.setBuffer(buffer);
        select.getSelectBody().accept(deparser);

//        System.out.println(buffer.toString());
    }
}