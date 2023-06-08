package com.weaver;

import java.util.*;

import jdk.nashorn.internal.scripts.JO;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.Node;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import net.sf.jsqlparser.util.deparser.SelectDeParser;

public class SimpleSqlParser24 {


    private static final Map<String, String> tableNameAliasMap = new HashMap<>();

    private static final Map<String, List<String>> joinTableNameMap = new HashMap<>();

    static {
        joinTableNameMap.put("customers", Arrays.asList("customers_one", "customers_two"));
    }

    public static void main(String args[]) throws JSQLParserException {
        replaceTableName("SELECT id, name, email FROM customers;");
    }

    private static void replaceTableName(String sql) throws JSQLParserException {
        Select select = (Select) CCJSqlParserUtil.parse(sql);

        /*
          SQL改写规则：
          1.目标表改写成多表join的形式，需要考虑别名存在场景，考虑子查询场景
          2.对应列的表名需要处理，考虑无表名、真实表名、别名的场景，包括查询列、条件列、排序列、Having列、函数列等
         */
        new TablesNamesFinder() {
            @Override
            protected String extractTableName(Table table) {
                String tableName = super.extractTableName(table);
                String alias = table.getAlias() == null ? null : table.getAlias().getName();
                tableNameAliasMap.put(tableName, alias);
                return tableName;
            }
        };

        StringBuilder buffer = new StringBuilder();
        ExpressionDeParser expressionDeParser = new ExpressionDeParser() {
            @Override
            public void visit(Column tableColumn) {
                if (tableColumn.getTable() != null) {
//                    tableColumn.getTable().setName(tableColumn.getTable().getName() + "_mytest");
                }
                super.visit(tableColumn);
            }
        };
        SelectDeParser deparser = new SelectDeParser(expressionDeParser, buffer) {
            @Override
            public void visit(Table table) {
//                table.setName(table.getName() + "_mytest");
                if (joinTableNameMap.containsKey(table.getName())) {
                    // 创建原始表
                    List<Join> joinList = select.getSelectBody(PlainSelect.class).getJoins();
                    if (joinList == null) {
                        joinList = new ArrayList<>();
                        select.getSelectBody(PlainSelect.class).setJoins(joinList);
                    }
                    List<String> joinTables = joinTableNameMap.get(table.getName());
                    // 判断是否有join表，如果有则遍历列表，逐个创建新的表和JOIN操作
                    if (joinTables != null && !joinTables.isEmpty()) {
                        for (String joinTableName : joinTables) {
                            // 创建新的表对象并设置表名
                            Table joinTable = new Table();
                            joinTable.setName(joinTableName);

                            // 创建新的Join对象并设置左表和右表
                            Join join = new Join();
                            join.setRightItem(joinTable);

                            // 设置JOIN类型和JOIN条件
                            join.setInner(true);
                            join.setOnExpression(new EqualsTo(new Column(table, "id"),
                                    new Column(joinTable,"id")));
                            joinList.add(join);
                        }
                    }
                }
            }
        };

        expressionDeParser.setSelectVisitor(deparser);
        expressionDeParser.setBuffer(buffer);
        select.getSelectBody().accept(deparser);

        System.out.println(select.toString());
    }
}