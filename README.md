
springTx
========

此项目是为了体验Spring对事务的支持，也就是说怎么证明Spring的事务支持是有效的。 

========
问题描述：
  1. 独立的两个系统A和B，都要访问MySQL数据库。
  2. A要往两个表里写数据，B从这两个表里读数据。
  3. 这两个表可简写成 PrimaryTable 和 PropertyTable， PrimaryTable是主表， PropertyTable是属性表，它有外键指向PrimaryTable。系统A往两个表里写数据时，业务逻辑上要求每PrimaryTable表里一行记录在PropertyTable表里都有且只有唯一的一条记录对应。系统B从MySQL里读数据时， 先从PrimaryTable表里读N行记录（读满足一定时间段的数据），再依据这N行记录的主键到PropertyTable里读另外属性。
  4. B从MySQL读数据时偶尔会有这样的问题：成功读到PrimaryTable表的记录，如row1和row2， 其主键值依次为p1和p2, 用p1和p2去PropertyTable表里读相关属性时， p1能找到数据，p2找不到数据。
  5. 系统A中的事务是用Spring管理，MySQL用InnoDb，事务隔离级别是REPEATABLEREAD。
  6. 怀疑的点有：Spring的事务管理没起作用。
